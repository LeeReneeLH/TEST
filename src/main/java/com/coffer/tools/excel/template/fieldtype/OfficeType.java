package com.coffer.tools.excel.template.fieldtype;

/**
 * 调缴功能Entity
 * 
 * @author Wanghan
 * @version 2017-08-25
 */
public class OfficeType {
	private static Object obj;

	public static void setValue(Object object) {
		obj = object;
	}

	public Object getValue() {
		return obj;
	}
}
