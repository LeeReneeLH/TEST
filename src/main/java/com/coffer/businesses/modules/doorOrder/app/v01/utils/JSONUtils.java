package com.coffer.businesses.modules.doorOrder.app.v01.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * JSON工具类
 * 
 * @author yuxixuan
 */
public class JSONUtils {

	/** 日期格式 */
	private static final String FORMATE_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

	/**
	 * Json实例对象
	 */
	// excludeFieldsWithoutExposeAnnotation():不导出实体中没有用@Expose注解的属性
	// enableComplexMapKeySerialization():支持Map的key为复杂对象的形式
	// setDateFormat("yyyy-MM-dd HH:mm:ss"):时间转化为特定格式
	// 会把字段首字母大写,注:对于实体上使用了@SerializedName注解的不会生效.
	// setPrettyPrinting():对json结果格式化.
	// //有的字段不是一开始就有的,会随着版本的升级添加进来,那么在进行序列化和返序列化的时候就会根据版本号来选择是否要序列化.
	// @Since(版本号)能完美地实现这个功能.还的字段可能,随着版本的升级而删除,那么
	// @Until(版本号)也能实现这个功能,GsonBuilder.setVersion(double)方法需要调用.
	public static Gson gson = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation().enableComplexMapKeySerialization().serializeNulls()
			.setDateFormat(FORMATE_YYYY_MM_DD_HH_MM_SS).setPrettyPrinting().create();

}
