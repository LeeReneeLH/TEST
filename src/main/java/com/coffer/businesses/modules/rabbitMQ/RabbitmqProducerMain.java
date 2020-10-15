package com.coffer.businesses.modules.rabbitMQ;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


public class RabbitmqProducerMain {

	/** 日期格式 */
	private static final String FORMATE_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

	public static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
			.enableComplexMapKeySerialization().serializeNulls().setDateFormat(FORMATE_YYYY_MM_DD_HH_MM_SS)
			.setPrettyPrinting().create();

    public static void main(String[] args) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("serviceNo", "104");
		map.put("palleteNo", "11111");
		map.put("routeNo", "");

		List<Map<String, Object>> bundleDetailSetList = Lists.newArrayList();
		// 总捆数
		Map<String, Object> resultCountMap = new HashMap<String, Object>();
		// 获得物品类型
		resultCountMap.put("type", "01");
		// 获得面值
		resultCountMap.put("faceValue", "01");
		// 获得数量
		resultCountMap.put("num", new BigDecimal(20));

			// 获得物品名称
		resultCountMap.put("goodsName", "100元");

			// 获得总价值
		resultCountMap.put("amount", new BigDecimal(100));

		bundleDetailSetList.add(resultCountMap);

		map.put("itemList", bundleDetailSetList);

        ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("172.16.3.99");
		factory.setPort(5672);
		factory.setUsername("Administrator");
		factory.setPassword("root");
		factory.setVirtualHost("/");

        Connection connection =  factory.newConnection();

        Channel channel = connection.createChannel();
		String queueName = "queue1";
		String exchangeName = "exchangerOne";
		String routingKey = "abc";
		channel.exchangeDeclare(exchangeName, "direct");
		channel.queueDeclare(queueName, true, false, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);

		for (long i = 1; i <= 1; i++) {
			// String msg = "这是第" + i + "条msg :" + Math.random() * 100;
			String jsonData = gson.toJson(map);
			channel.basicPublish(exchangeName, routingKey, null, jsonData.getBytes()); // 发送消息
			System.out.println(jsonData);
            TimeUnit.MILLISECONDS.sleep((long) (Math.random()*500));
		
		}
		channel.close();
		connection.close();

	}
}