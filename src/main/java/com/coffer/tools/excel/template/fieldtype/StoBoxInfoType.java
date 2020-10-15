package com.coffer.tools.excel.template.fieldtype;


/**
 * 调缴功能Entity
 * @author Chengshu
 * @version 2015-05-11
 */
public class StoBoxInfoType {
	private static Object obj;

	public static void setValue(Object object){
		obj = object;
	}
	
	public Object getValue(){
		return obj;
	}
}