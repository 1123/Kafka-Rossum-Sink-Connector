# Rossum AI Sink Connector for Apache Kafka

This is a Kafka Connect Sink Connector that allows exporting files from a Kafka topic to Rossum AI.

## Prerequisites
* An account with Rossum.ai (username, password)
* the id of a queue you want to publish to. You can find the queue id in the rossum web interface
* A running Kafka Cluster (either locally or e.g. Confluent Cloud)
* Apache Maven and a recent JDK to build the distribution

## How to run this connector:
* Create a Kafka Topic with name `t1`
* Build the project: `mvn clean package -DskipTests`
* Write some binary files (images, pdfs, scans, etc) to the topic, via the main Method in the class `SampleDataProducer`.
  * There is a shell script to help you: `./produce-sample-message.sh`
* bring the distribution on the plugin path of Kafka Connect and unzip it
  * You may want to adapt the script `./build-and-install.sh` for this purpose
* Start Kafka Connect (e.g. via the Confluent CLI) `confluent local services connect start`
* Make sure that the Rossum Sink Connector has been registered as a Connect Plugin:
    * `curl -X GET http://localhost:8083/connector-plugins | jq`. 
    * You should see the Rossum Sink Connector in the list.
* Fill in your Rossum.ai credentials and queue id into the file `curl/sample.env`
* Use the file `sample-connector.json.template` and the `substitute.sh` script to generate a
  connector configuration including your credentials and queue id: 
  `cd curl; source sample.env; ./substitute.sh`
* Create an instance of the connector via the http Connect api: 
  `./create-connector.sh`
* Check the logs of the connect worker to see if any error messages have occurred
  e.g. with `confluent local services connect log -f`.  
  You should see that the connector has uploaded a message to Rossum. 
* Check the status of the connector and the tasks
  e.g. `curl localhost:8083/connectors/rossum-test-connector/status | jq`. 
  The connector should be in state `RUNNING` and the task should also be in state `RUNNING`.
* Check that the data has been uploaded to Rossum.ai via the Rossum web interface.

## Configuration Properties

The following configuration properties are supported:

* rossum.username: The username for the Rossum.ai account
* rossum.password: The password for the Rossum.ai account
* rossum.company: The company name for the Rossum.ai account
* rossum.queue.id: The id of the queue to which the documents should be uploaded
* topics: The Kafka topics from which the documents should be read

The following JSON file is a sample configuration: 

```
{
  "name": "rossum-test-connector",
  "config": {
    "connector.class": "io.confluent.connectors.rossum.sink.RossumSinkConnector",
    "tasks.max": "1",
    "topics": "t1",
    "rossum.username": "me",
    "rossum.password": "s3cret",
    "rossum.company": "foo-bar-inc",
    "rossum.queue.id": "987654",
    "key.converter": "org.apache.kafka.connect.converters.ByteArrayConverter",
    "value.converter": "org.apache.kafka.connect.converters.ByteArrayConverter"
  }
}
```

## Contributing
Contributions are very welcome. Please open an issue or a pull request.

## Bug Reports and Feature Requests
Please open an issue on this repository. 
