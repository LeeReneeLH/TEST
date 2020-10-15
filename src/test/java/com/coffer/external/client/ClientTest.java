package com.coffer.external.client;

import java.net.MalformedURLException;

import com.caucho.hessian.client.HessianProxyFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ClientTest {
	public static IHardwareService iHardwareService;
	/**
	 * Json实例对象
	 */
	protected static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation() // 不导出实体中没有用@Expose注解的属性
			.enableComplexMapKeySerialization() // 支持Map的key为复杂对象的形式
			.serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss")// 时间转化为特定格式
			// .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)//
			// 会把字段首字母大写,注:对于实体上使用了@SerializedName注解的不会生效.
			.setPrettyPrinting() // 对json结果格式化.
			// .setVersion(1.0)
			// //有的字段不是一开始就有的,会随着版本的升级添加进来,那么在进行序列化和返序列化的时候就会根据版本号来选择是否要序列化.
			// @Since(版本号)能完美地实现这个功能.还的字段可能,随着版本的升级而删除,那么
			// @Until(版本号)也能实现这个功能,GsonBuilder.setVersion(double)方法需要调用.
			.create();

	public static void main(String[] args) {
		String url = "http://127.0.0.1:8080/frame/hardwareService";
		HessianProxyFactory factory = new HessianProxyFactory();
		try {
			iHardwareService = (IHardwareService) factory.create(IHardwareService.class, url);

			// 共通
			// invokeInterface("12");
			// invokeInterface("55");
			// invokeInterface("02");

			// 一、上缴
			// 款箱尾箱初始化
			// invokeInterface("0181_1");
			// invokeInterface("0181_2");
			// invokeInterface("0181_3");
			// invokeInterface("0181_4");
			// invokeInterface("0181_5");

			// 装箱
			// invokeInterface("0180_1");
			// invokeInterface("0180_2");
			// invokeInterface("0180_3");

			// 上缴同步
			// invokeInterface("0183");

			// 上缴登记
			// invokeInterface("19");

			// 入库查询
			// invokeInterface("22");

			// 入库确认
			// invokeInterface("24");

			// 入库交接查询
			// invokeInterface("25_handin");

			// 入库交接
			// invokeInterface("26_handin");

			// 二、下拨

			// 同步下拨已装箱信息
			// invokeInterface("0182");

			// 下拨登记
			// invokeInterface("18");

			// 测试
			invokeInterface("0602");

		} catch (MalformedURLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

	}

	/**
	 * 调用接口
	 * 
	 * @param jsonFileName
	 */
	private static void invokeInterface(String jsonFileName) {
		String param = ReadJsonUtils.readJson(jsonFileName);
		String resultStr = iHardwareService.service(param);
		System.out.println(jsonFileName + "=====+++++ start ++++++=====");
		System.out.println(resultStr);
		System.out.println(jsonFileName + "=====+++++ end ++++++=====");
	}
}
