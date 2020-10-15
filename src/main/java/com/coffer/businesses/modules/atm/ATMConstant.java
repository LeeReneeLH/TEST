/**
 * @author Administrator
 * @date 2014-10-16
 * 
 * @Description 
 */
package com.coffer.businesses.modules.atm;

import com.coffer.businesses.common.Constant;

/**
 * @author Lemon
 *
 */
public class ATMConstant extends Constant {

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
	
	/**
	 * 接口查询类型参数
	 * @author wxz
	 */
	public static class SearchType {
		/** 清分查询	*/
		public static final String CLEAR_SEARCH = "1";
		/**	押运查询	*/
		public static final String ESCORT_SEARCH = "0";
	}

	/**
	 * 扫描表示
	 * 
	 * @author qph
	 */
	public static class ScanFlag {
		/** 未扫描 */
		public static final String NoScan = "0";
		/** 已扫描 */
		public static final String Scan = "1";
		/** 补录 */
		public static final String Additional = "2";
	}

	/**
	 * 授权方式
	 * 
	 * @author XL
	 */
	public static class ManagerType {
		/** 系统登录 */
		public static final String SYSTEM_LOGIN = "0";
		/** 人脸识别 */
		public static final String FACE = "1";
		/** 身份证 */
		public static final String IDENTITY_CARD = "2";
	}
}
