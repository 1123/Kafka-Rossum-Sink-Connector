# Rossum AI Sink Connector for Apache Kafka

This is a Kafka Connect Sink Connector that allows exporting files from a Kafka topic to Rossum AI.

Prerequisites
* An account with Rossum.ai (username, password)
* the id of a queue you want to publish to. You can find the queue id in the rossum web interface
* A running Kafka Cluster (either locally or e.g. Confluent Cloud)
* gradle and a recent JDK to build the distribution

How to run this connector: 

* Create a Kafka Topic
* Write some binary files (images, pdfs, scans, etc) to the the topic, via a KafkaProducer
* build the distribution
* bring the distribution on the plugin path of Kafka Connect and unzip it
* Start Kafka Connect
* Make sure that the Rossum Sink Connector has been registered as a Connect Plugin
* Create an instance of the connector via the http Connect api
* Make sure you have filled in your rossum connection credentials (username, password, company, queue id)
* Check the logs of the connect worker to see if any error messages have occurred
* Check the status of the connector and the tasks
* Check that the data has been uploaded to Rossum.ai via the web interface
