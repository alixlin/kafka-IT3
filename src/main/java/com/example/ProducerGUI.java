package com.example;

import javax.swing.*;
import java.awt.*;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;


import java.util.Collections;
import java.util.Properties;


public class ProducerGUI {

    private static JTextArea notificationArea;

    public static void main(String[] args) {
        // Create the frame
        JFrame frame = new JFrame("Production Notifications");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);

        // Create the text area for notifications
        notificationArea = new JTextArea();
        notificationArea.setEditable(false);

        // Add a scroll pane
        JScrollPane scrollPane = new JScrollPane(notificationArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Display the frame
        frame.setVisible(true);

        // Start listening to Kafka topic
        listenToProductionTopic();
    }

    private static void listenToProductionTopic() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "production-notification-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList("productions"));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    String notification = "Produced car: " + record.value();
                    SwingUtilities.invokeLater(() -> displayNotification(notification));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void displayNotification(String message) {
        // Append messages to the notification area
        notificationArea.append(message + "\n");
    }
}
