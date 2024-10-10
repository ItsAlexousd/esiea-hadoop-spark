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

public class AveragePriceByNeighborhood implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AveragePriceByNeighborhood.class);

    public static void main(String[] args) {
        Preconditions.checkArgument(args.length > 1, "Please provide the path of input file and output dir as parameters.");
        new AveragePriceByNeighborhood().run(args[0], args[1]);
    }

    public void run(String inputFilePath, String outputDir) {
        SparkConf conf = new SparkConf()
                .setAppName(AveragePriceByNeighborhood.class.getName())
                .setMaster("yarn");

        try (JavaSparkContext sc = new JavaSparkContext(conf)) {
            JavaRDD<String> textFile = sc.textFile(inputFilePath);

            JavaPairRDD<String, Tuple2<Double, Integer>> priceByNeighborhood = textFile
                    .filter(line -> !line.startsWith("id"))
                    .mapToPair(line -> {
                        String[] parts = line.split(",");
                        if (parts.length == 16) {
                            String neighborhood = parts[4];
                            double price = Double.parseDouble(parts[9]);
                            return new Tuple2<>(neighborhood, new Tuple2<>(price, 1));
                        } else {
                            return new Tuple2<>("", new Tuple2<>(0.0, 0));
                        }
                    })
                    .reduceByKey((a, b) -> new Tuple2<>(a._1 + b._1, a._2 + b._2))
                    .filter(t -> !t._1.isEmpty());

            JavaPairRDD<String, Double> avgPriceByNeighborhood = priceByNeighborhood
                    .mapValues(sumCount -> sumCount._1 / sumCount._2);

            LOGGER.info("Calculated average price for " + avgPriceByNeighborhood.count() + " neighborhoods");
            avgPriceByNeighborhood.saveAsTextFile(outputDir);
        } catch (Exception e) {
            LOGGER.error("Error while closing Spark context", e);
        }
    }
}
