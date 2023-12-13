package com.example;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import java.util.Collections;

public class NotificationGUI {

    private static JTextArea notificationArea;

    public static void main(String[] args) {
        // Create the frame
        JFrame frame = new JFrame("Sales and Delivery Notifications");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Create the text area for notifications
        notificationArea = new JTextArea();
        notificationArea.setEditable(false);

        // Add a scroll pane
        JScrollPane scrollPane = new JScrollPane(notificationArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Display the frame
        frame.setVisible(true);

        // Start listening to Kafka topics
        listenToKafkaTopics();
    }

     private static void listenToKafkaTopics() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "sales-delivery-notification-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Arrays.asList("deliveries", "sales"));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    String topic = record.topic();
                    String notification = topic.equals("deliveries") ?
                            "Delivery Notification: " + record.value() :
                            "Car Sold: " + record.value();

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
