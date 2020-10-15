package com.coffer.businesses.modules.rabbitMQ;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.core.common.config.Global;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;


/**
 * RabbitMq 共同处理类
 * 
 * @author qph
 * @version 2018-07-10
 */
public class RabbitMQCommonUtils extends BusinessUtils {

	static {
		ConnectionFactory factory = new ConnectionFactory();
		// 获取主机IP
		String hostIp = Global.getConfig("rabbitMQ.host.ip");
		factory.setHost(hostIp);
		// 获取端口号
		String port = Global.getConfig("rabbitMQ.host.port");
		factory.setPort(Integer.parseInt(port));
		// 获取RabbitMq用户名
		String username = Global.getConfig("rabbitMQ.username");
		factory.setUsername(username);
		// 获取RabbitMq 密码
		String password = Global.getConfig("rabbitMQ.password");
		factory.setPassword(password);
		factory.setVirtualHost("/");
		Connection connection;
		// 获取RabbitMq 路由
		String exchangeName = Global.getConfig("rabbitMQ.exchanger");
		// 获取队列名
		List<String> queueNameList = Global.getList("rabbitMQ.queue.name");
		// 获取routingKey
		List<String> routingKeyList = Global.getList("rabbitMQ.queue.routingKey");

		try {
			connection = factory.newConnection();
			Channel channel = connection.createChannel();
			for (int i = 0; i < queueNameList.size(); i++) {
				String queueName = queueNameList.get(i);
				String routingKey = routingKeyList.get(i);
				channel.exchangeDeclare(exchangeName, "direct", true);
				channel.queueDeclare(queueName, true, false, false, null);
				channel.queueBind(queueName, exchangeName, routingKey);
			}

		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

	}
	/**
	 * rabbitMQ生产者生成
	 * 
	 * @author qph
	 * @version 2018年7月10日
	 * @param
	 * @return
	 * @throws TimeoutException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void rabbitMqProducer(String routingKey, String msg)
			throws IOException, TimeoutException, InterruptedException {

		ConnectionFactory factory = new ConnectionFactory();
		// 获取主机IP
		String hostIp = Global.getConfig("rabbitMQ.host.ip");
		factory.setHost(hostIp);
		// 获取端口号
		String port = Global.getConfig("rabbitMQ.host.port");
		factory.setPort(Integer.parseInt(port));
		// 获取RabbitMq用户名
		String username = Global.getConfig("rabbitMQ.username");
		factory.setUsername(username);
		// 获取RabbitMq 密码
		String password = Global.getConfig("rabbitMQ.password");
		factory.setPassword(password);
		factory.setVirtualHost("/");

		Connection connection = factory.newConnection();

		Channel channel = connection.createChannel();
		// 获取RabbitMq 路由
		String exchangeName = Global.getConfig("rabbitMQ.exchanger");
		channel.exchangeDeclare(exchangeName, "direct", true);
		// channel.queueDeclare(queueName, false, false, false, null);
		// channel.queueBind(queueName, exchangeName, routingKey);
		channel.basicPublish(exchangeName, routingKey, null, msg.getBytes()); // 发送消息
		System.out.println("produce msg :" + msg);
		// TimeUnit.MILLISECONDS.sleep((long) (Math.random() * 500));

		channel.close();
		connection.close();
	}


	/**
	 * rabbitMQ消费者生成
	 * 
	 * @author qph
	 * @version 2018年7月10日
	 * @param
	 * @return
	 * @throws TimeoutException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void rabbitMqConsumer(String queueName)
			throws IOException, TimeoutException, InterruptedException {
		

		ConnectionFactory factory = new ConnectionFactory();
		// 获取主机IP
		String hostIp = Global.getConfig("rabbitMQ.host.ip");
		factory.setHost(hostIp);
		// 获取端口号
		String port = Global.getConfig("rabbitMQ.host.port");
		factory.setPort(Integer.parseInt(port));
		// 获取RabbitMq用户名
		String username = Global.getConfig("rabbitMQ.username");
		factory.setUsername(username);
		// 获取RabbitMq 密码
		String password = Global.getConfig("rabbitMQ.password");
		factory.setPassword(password);
		factory.setVirtualHost("/");

		Connection connection = factory.newConnection();

		Channel channel = connection.createChannel();
		channel.queueDeclare(queueName, false, false, false, null);

		channel.basicQos(5); // 每次取5条消息

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				// 消费消费
				String msg = new String(body, "utf-8");
				System.out.println("consume1 msg: " + msg);
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
