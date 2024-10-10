#!/bin/bash

HADOOP_FOLDER="/usr/local/hadoop"
LOCAL_OUTPUT_DIR="/home/root/spark_jobs_output"

# Create HDFS directory
create_hdfs_directory() {
  echo "Creating HDFS directory..."
  hdfs dfs -mkdir -p /user/root/output
  hdfs dfs -chmod -R 755 /user/root/output
}

# Create local directory for output
create_local_directory() {
  echo "Cleaning local output directory..."
  rm -rf $LOCAL_OUTPUT_DIR/*
  echo "Creating local directory for Spark job outputs..."
  mkdir -p $LOCAL_OUTPUT_DIR
}

# Copy output from HDFS to local
copy_output_to_local() {
  local job_name=$1
  echo "Copying output of $job_name from HDFS to local..."
  hdfs dfs -get /user/root/output/$job_name $LOCAL_OUTPUT_DIR/$job_name
}

# Run Spark jobs
run_spark_job() {
  local class_name=$1
  echo "Running Spark job: $class_name"

  spark-submit \
  --class fr.esiea.spark.$class_name \
  --master yarn \
  --deploy-mode cluster \
  $HADOOP_FOLDER/data/spark-1.0-SNAPSHOT.jar \
  dataset.csv \
  output/$class_name

  # After job completion, copy the output to the local filesystem
  copy_output_to_local $class_name
}

run_spark_jobs() {
  run_spark_job "AveragePriceByNeighborhood"
  run_spark_job "AveragePriceByRoomType"
  run_spark_job "CountListingsByNeighborhood"
  run_spark_job "CountListingsByPriceRange"
  run_spark_job "TopHostsByTotalListings"
}

echo "Running Spark jobs..."

create_hdfs_directory
create_local_directory
run_spark_jobs

echo "All Spark jobs completed and outputs copied to local."
