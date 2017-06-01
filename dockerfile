# Version 1.0
# ODA stack

# Alpine-micro
FROM openjdk:alpine

# Locale
ENV LC_ALL="en_US.utf8"

# Copy jar-files
COPY build/libs/oda-phr.jar /home/oda-phr.jar

# Launch jar-files
CMD ["java","-jar","/home/oda-phr.jar"]