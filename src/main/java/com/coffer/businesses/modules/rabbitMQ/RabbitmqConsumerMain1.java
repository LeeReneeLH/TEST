package com.coffer.businesses.modules.rabbitMQ;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RabbitmqConsumerMain1 {

	public static void main(String[] args) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("127.0.0.1");
		factory.setPort(5672);
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setVirtualHost("/");

		Connection connection = factory.newConnection();

		Channel channel = connection.createChannel();
		String queueName = "queueOne";
		channel.queueDeclare(queueName, false, false, false, null);
		channel.queueBind(queueName, "exchangerOne", "sddd");

		channel.basicQos(5); // 每次取5条消息

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				// 消费消费
				String msg = new String(body, "utf-8");
				System.out.println("consume2 msg: " + msg);
				try {
					TimeUnit.MILLISECONDS.sleep((long) (Math.random() * 500));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// 手动消息确认
				getChannel().basicAck(envelope.getDeliveryTag(), false);
			}
		};

		// 调用消费消息
		channel.basicConsume(queueName, false, "queueOne", consumer);

	}
}