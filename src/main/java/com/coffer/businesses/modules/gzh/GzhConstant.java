/**
 * @author Administrator
 * @date 2014-10-16
 * 
 * @Description 
 */
package com.coffer.businesses.modules.gzh;

import com.coffer.businesses.common.Constant;

/**
 * @author Lemon
 *
 */
public class GzhConstant extends Constant {

	/** 上传atm加钞计划文件路径key */
	public final static String UploadAddPlanInfoDir = "atm.addPlan.uploadFile.dir";
	
	/**
	 * 加钞计划状态
	 * @author LLF
	 *
	 */
	public static class PlanStatus {
		/** 计划创建 */
		public static final String PLAN_CREATE = "0";
		/** 计划同步 */
		public static final String PLAN_SYN = "1";
		/** 计划出库 */
		public static final String PLAN_OUT = "2";
		/** 计划使用 */
		public static final String PLAN_USE = "3";
	}

	/**
	 * 核心金额获取方式
	 * 
	 * @author yuxixuan
	 */
	public static class CoreAmountMethod {
		/** 接口获取 */
		public static final String INTERFACE = "0";
		/** 补录 */
		public static final String INPUT = "1";
	}
	
	public static class BindingStatus {
		
		/** 绑定关系创建 */
		public static final String STATUS_CREATE = "0";
		/** 绑定关系清点 */
		public static final String STATUS_CLEAR = "1";
		/** 绑定关系记账 */
		public static final String STATUS_ACCOUNT = "2";
	}
}
