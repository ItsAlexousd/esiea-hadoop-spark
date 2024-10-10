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
import java.util.List;

public class TopHostsByTotalListings implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TopHostsByTotalListings.class);

    public static void main(String[] args) {
        Preconditions.checkArgument(args.length > 1, "Please provide the path of input file and output dir as parameters.");
        new TopHostsByTotalListings().run(args[0], args[1]);
    }

    public void run(String inputFilePath, String outputDir) {
        SparkConf conf = new SparkConf()
                .setAppName(TopHostsByTotalListings.class.getName())
                .setMaster("yarn");

        try (JavaSparkContext sc = new JavaSparkContext(conf)) {
            JavaRDD<String> textFile = sc.textFile(inputFilePath);
            LOGGER.info("Loaded " + textFile.count() + " lines from " + inputFilePath);

            JavaPairRDD<String, Integer> hostListings = textFile
                    .filter(line -> !line.startsWith("id")) // Ignore header
                    .mapToPair(line -> {
                        String[] parts = line.split(",");
                        if (parts.length == 16) {
                            String hostName = parts[3];
                            return new Tuple2<>(hostName, 1); // Each listing counts as 1 for the host
                        } else {
                            LOGGER.warn("Malformed row: " + line);
                            return new Tuple2<>("", 0);
                        }
                    })
                    .reduceByKey(Integer::sum)
                    .filter(t -> !t._1.isEmpty()); // Remove empty host names

            // Sort by total listings in descending order and take the top 10 hosts
            List<Tuple2<String, Integer>> topHosts = hostListings
                    .mapToPair(Tuple2::swap) // Swap (host_name, count) to (count, host_name) for sorting
                    .sortByKey(false) // Sort by count in descending order
                    .mapToPair(Tuple2::swap) // Swap back to (host_name, count) after sorting
                    .take(10); // Take the top 10

            // Output the results
            JavaRDD<Tuple2<String, Integer>> topHostsRDD = sc.parallelize(topHosts);
            topHostsRDD.saveAsTextFile(outputDir);
            LOGGER.info("Top 10 hosts saved to " + outputDir);
        } catch (Exception e) {
            LOGGER.error("Error while closing Spark context", e);
        }
    }
}
