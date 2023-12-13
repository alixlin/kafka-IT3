package com.example;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Collections;
import java.util.Properties;


public class ProductionStageConsumer {

    private static int carCount = 0;

    public static void main(String[] args) {
        // Consumer setup
        Properties consumerProps = new Properties();
        consumerProps.put("bootstrap.servers", "localhost:9092");
        consumerProps.put("group.id", "production-group");
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        // Producer setup
        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", "localhost:9092");
        producerProps.put("acks", "all");
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
             Producer<String, String> producer = new KafkaProducer<>(producerProps)) {

            consumer.subscribe(Collections.singletonList("sales"));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    String model = record.value();
                    produceCar(model,producer);

                    // After production, send a message to the production topic
                    producer.send(new ProducerRecord<>("productions", model, "Produced " + model));
                }
            }
        }
    }

    private static void produceCar(String model, Producer<String, String> producer)  {
        // Simulate production time
        int productionTime = getProductionTime(model);
        try {
            Thread.sleep(productionTime);
            System.out.println("Produced car: " + model);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Interrupted during production");
        }
        carCount++;
        if (carCount >= 10) {
            deliverCars(producer);
            carCount = 0;
        }
    }

    private static void deliverCars(Producer<String, String> producer) {
        System.out.println("Delivering cars...");
        try {
            Thread.sleep(2000); // Simulating delivery time
            System.out.println("Delivery complete");
            producer.send(new ProducerRecord<>("deliveries", "Delivery Complete"));
            producer.send(new ProducerRecord<>("sales", "Delivery Complete"));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Interrupted during delivery");
        }
    }

    private static int getProductionTime(String model) {
        switch (model) {
            case "Model M":
                return 10000;
            case "Model I":
                return 2000;
            case "Model A":
                return 3000;
            case "Model G":
                return 4000;
            case "Model E":
                return 7000;
            default:
                return 1000; // Default time for unknown models
        }
    }
}
