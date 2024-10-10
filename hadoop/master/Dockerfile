FROM madjidtaoualit/hadoop-cluster:latest

# Install OpenSSH server
RUN apt-get update && apt-get install -y openssh-server

# Create SSH directory
RUN mkdir -p /var/run/sshd

# Copy the load_to_hdfs.sh script into the container
COPY data/load_to_hdfs.sh /usr/local/bin/load_to_hdfs.sh

# Copy the run_spark_jobs.sh script into the container
COPY data/run_spark_jobs.sh /usr/local/bin/run_spark_jobs.sh

# Copy the Spark jar file into the container
COPY data/spark-1.0-SNAPSHOT.jar /usr/local/hadoop/data/spark-1.0-SNAPSHOT.jar

# Make the scripts executable
RUN chmod +x /usr/local/bin/load_to_hdfs.sh
RUN chmod +x /usr/local/bin/run_spark_jobs.sh

# Expose ports needed by Hadoop Master
EXPOSE 9870 8088 7077 16010

# Start SSH, Hadoop, and run the load_to_hdfs.sh script
CMD ["/bin/bash", "-c", "service ssh start && ./start-hadoop.sh && /usr/local/bin/load_to_hdfs.sh && tail -f /dev/null"]
