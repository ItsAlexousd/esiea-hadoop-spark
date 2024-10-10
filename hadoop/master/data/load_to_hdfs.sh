#!/bin/bash

# Path to the CSV file on the mounted volume
LOCAL_CSV_FILE="/data/dataset.csv"

# Path where the file will be loaded into HDFS
HDFS_PATH="/user/root/dataset.csv"

# Wait for the namenode to be ready
until hdfs dfs -ls / > /dev/null 2>&1
do
    echo "Waiting for the namenode to start..."
    sleep 5
done

# Check if the file exists
if [ ! -f "$LOCAL_CSV_FILE" ]; then
    echo "Error: $LOCAL_CSV_FILE does not exist"
    exit 1
fi

# Load the CSV file into HDFS
hdfs dfs -mkdir -p /user/root # Create the directory if necessary
hdfs dfs -put $LOCAL_CSV_FILE $HDFS_PATH # Load the CSV file into HDFS

echo "File loaded into HDFS at $HDFS_PATH"

bash /usr/local/bin/run_spark_jobs.sh
