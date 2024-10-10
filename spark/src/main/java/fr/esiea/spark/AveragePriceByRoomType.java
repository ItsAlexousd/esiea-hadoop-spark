package fr.esiea.spark;

import com.google.common.base.Preconditions;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.io.Serializable;

public class AveragePriceByRoomType implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AveragePriceByRoomType.class);

    public static void main(String[] args) {
        Preconditions.checkArgument(args.length > 1, "Please provide the path of input file and output dir as parameters.");
        new AveragePriceByRoomType().run(args[0], args[1]);
    }

    public void run(String inputFilePath, String outputDir) {
        SparkConf conf = new SparkConf()
                .setAppName(AveragePriceByRoomType.class.getName())
                .setMaster("yarn");

        try (JavaSparkContext sc = new JavaSparkContext(conf)) {
            JavaRDD<String> textFile = sc.textFile(inputFilePath);
            LOGGER.info("Loaded " + textFile.count() + " lines from " + inputFilePath);

            JavaPairRDD<String, Tuple2<Integer, Integer>> roomPriceCounts = textFile
                    .filter(line -> !line.startsWith("id")) // Ignorer l'en-tête
                    .mapToPair(line -> {
                        String[] parts = line.split(",");
                        // Ensure there are enough columns
                        if (parts.length == 16) {
                            String roomType = parts[8];
                            Integer price = Integer.parseInt(parts[9]);
                            return new Tuple2<>(roomType, new Tuple2<>(price, 1));
                        } else {
                            LOGGER.warn("Malformed row: " + line);
                            return new Tuple2<>("", new Tuple2<>(0, 0));
                        }
                    })
                    .reduceByKey((a, b) -> new Tuple2<>(a._1 + b._1, a._2 + b._2)) // Sum prices and counts
                    .filter(t -> !t._1.isEmpty()); // Remove empty room types

            // Calculate the average price by dividing total price by count
            JavaPairRDD<String, Double> avgPriceByRoomType = roomPriceCounts
                    .mapValues(value -> (double) value._1 / value._2);

            LOGGER.info("Calculated average price for " + avgPriceByRoomType.count() + " room types");
            avgPriceByRoomType.saveAsTextFile(outputDir);
        } catch (Exception e) {
            LOGGER.error("Error while closing Spark context", e);
        }
    }
}
