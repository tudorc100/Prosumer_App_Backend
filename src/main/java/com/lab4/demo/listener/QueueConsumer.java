package com.lab4.demo.listener;

import com.lab4.demo.model.Consumption;
import com.lab4.demo.service.DeviceService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

@Component
@RequiredArgsConstructor
public class QueueConsumer {

    private final DeviceService deviceService;

    public void readMessages() throws Exception, InterruptedException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("cow.rmq2.cloudamqp.com");
        factory.setUsername("mnyspvbm");
        factory.setPassword("QPnkU9RP8ZvX8nieYDRnHHNq_2Ebh2-m");
        factory.setPort(5672);
        factory.setUri("amqps://mnyspvbm:QPnkU9RP8ZvX8nieYDRnHHNq_2Ebh2-m@sparrow.rmq.cloudamqp.com/mnyspvbm");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();


        channel.queueDeclare("Tudor", false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {

            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

            System.out.println(" [x] Received '" + message + "'");

            try {
                Thread.sleep(400);
                addEnergy(message);
                Thread.sleep(400);
            } catch (InterruptedException | JSONException e) {
                throw new RuntimeException(e);
            }
        };
        channel.basicConsume("Tudor", true, deliverCallback, consumerTag -> {
        });
    }

    private void addEnergy(String message) throws InterruptedException, JSONException {
        JSONObject jsonObject = new JSONObject(message);
        System.out.println(jsonObject.get("date"));
        System.out.println(jsonObject.get("deviceId"));
        System.out.println(jsonObject.get("energyConsumption"));
        Timestamp timestamp = Timestamp.valueOf((String) jsonObject.get("date"));
        Long device_id = Long.parseLong((String) jsonObject.get("deviceId"));
        Double energy = Double.parseDouble((String) jsonObject.get("energyConsumption"));



        Consumption consumption = new Consumption(timestamp, energy, device_id);
        deviceService.addConsumption(consumption);

    }
}