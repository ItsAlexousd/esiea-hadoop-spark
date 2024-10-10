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

public class CountListingsByNeighborhood implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(CountListingsByNeighborhood.class);

    public static void main(String[] args) {
        Preconditions.checkArgument(args.length > 1, "Please provide the path of input file and output dir as parameters.");
        new CountListingsByNeighborhood().run(args[0], args[1]);
    }

    public void run(String inputFilePath, String outputDir) {
        SparkConf conf = new SparkConf()
                .setAppName(CountListingsByNeighborhood.class.getName())
                .setMaster("yarn");

        try (JavaSparkContext sc = new JavaSparkContext(conf)) {
            JavaRDD<String> textFile = sc.textFile(inputFilePath);
            LOGGER.info("Loaded " + textFile.count() + " lines from " + inputFilePath);

            JavaPairRDD<String, Integer> neighborhoodCounts = textFile
                    .filter(line -> !line.startsWith("id")) // Ignorer l'en-tête
                    .mapToPair(line -> {
                        String[] parts = line.split(",");
                        // Ensure there are enough columns
                        if (parts.length == 16) {
                            String neighborhood = parts[4];
                            return new Tuple2<>(neighborhood, 1); // chaque logement est compté comme 1
                        } else {
                            LOGGER.warn("Malformed row: " + line);
                            return new Tuple2<>("", 0);
                        }
                    })
                    .reduceByKey(Integer::sum)
                    .filter(t -> !t._1.isEmpty()); // Remove empty neighborhoods

            LOGGER.info("Found " + neighborhoodCounts.count() + " neighborhoods");
            neighborhoodCounts.saveAsTextFile(outputDir);
        } catch (Exception e) {
            LOGGER.error("Error while closing Spark context", e);
        }
    }
}
