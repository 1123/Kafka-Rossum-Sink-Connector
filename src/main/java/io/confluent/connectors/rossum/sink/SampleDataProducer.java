package io.confluent.connectors.rossum.sink;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.ByteArraySerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;


public class SampleDataProducer {

    public static void main(String[] args) {
        // Kafka producer properties
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", ByteArraySerializer.class.getName());
        props.put("value.serializer", ByteArraySerializer.class.getName());

        // Create Kafka producer
        KafkaProducer<byte[], byte[]> producer = new KafkaProducer<>(props);

        // Read file as byte array
        byte[] fileContent = null;
        try {
            ClassLoader classLoader = SampleDataProducer.class.getClassLoader();
            File file = new File(classLoader.getResource("invoice.jpg").getPath());
            FileInputStream fileInputStream = new FileInputStream(file);
            fileContent = fileInputStream.readAllBytes();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Send file content to Kafka
        if (fileContent != null) {
            ProducerRecord<byte[], byte[]> record = new ProducerRecord<>("t1", fileContent);
            try {
                RecordMetadata metadata = producer.send(record).get();
                System.out.printf("Sent record to topic %s partition %d offset %d%n",
                        metadata.topic(), metadata.partition(), metadata.offset());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // Close producer
        producer.close();
    }
}
