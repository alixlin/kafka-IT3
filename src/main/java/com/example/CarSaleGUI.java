package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;


public class CarSaleGUI {

    private static JComboBox<String> carModelComboBox;

    public static void main(String[] args) {
        // Create the frame
        JFrame frame = new JFrame("Car Sale Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new FlowLayout());

        // Create the dropdown (combo box) for car models
        String[] carModels = {"Model M", "Model I", "Model A", "Model G", "Model E"};
        carModelComboBox = new JComboBox<>(carModels);

        // Create the button
        JButton buyButton = new JButton("Buy a Car");
        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sellCar();
            }
        });

        // Add the combo box and button to the frame
        frame.getContentPane().add(carModelComboBox);
        frame.getContentPane().add(buyButton);

        // Display the frame
        frame.setVisible(true);
    }

    private static void sellCar() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            String selectedCarModel = (String) carModelComboBox.getSelectedItem();
            producer.send(new ProducerRecord<>("sales", selectedCarModel));
            System.out.println("Car sold: " + selectedCarModel);
        } catch (Exception e) {
            System.out.println("Error while sending message to Kafka: " + e.getMessage());
        }
    }
}