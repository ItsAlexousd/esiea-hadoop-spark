FROM madjidtaoualit/hadoop-cluster:latest

# Ensure the SSH service starts on container startup
RUN apt-get update && apt-get install -y openssh-server

# Create SSH directory
RUN mkdir -p /var/run/sshd

# Expose ports needed by Hadoop Worker
EXPOSE 8042

# Start SSH and keep the container running
CMD ["/bin/bash", "-c", "service ssh start && tail -f /dev/null"]
