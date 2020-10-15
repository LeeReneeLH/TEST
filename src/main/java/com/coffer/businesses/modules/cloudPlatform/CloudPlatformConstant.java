/**
 * @author Administrator
 * @date 2014-10-16
 * 
 * @Description 
 */
package com.coffer.businesses.modules.cloudPlatform;

import com.coffer.businesses.common.Constant;

/**
 * @author Lemon
 *
 */
public class CloudPlatformConstant extends Constant {

	/**
	 * 人员类型（V：访客；E：员工）
	 * 
	 * @author wangqingjie
	 *
	 */
	public static class escortType {
		public static final String VISITOR = "V"; // 访客
		public static final String ESCORT = "E"; // 员工
	}

	/**
	 * 删除标记（（0：正常；1：删除））
	 * 
	 * @author wangqingjie
	 *
	 */
	public static class delFlag {
		public static final String NORMAL = "0"; // 正常
		public static final String DELETE = "1"; // 删除
	}
}
