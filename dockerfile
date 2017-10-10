# Alpine-micro
FROM openjdk:alpine

# Locale
ENV LC_ALL="en_US.utf8"

# Copy jar-files
COPY build/libs/oda-phr.jar /home/oda-phr.jar

# Set workdir
WORKDIR /home

# Launch jar-files
ENTRYPOINT exec java $JAVA_OPTS -jar /home/oda-phr.jar