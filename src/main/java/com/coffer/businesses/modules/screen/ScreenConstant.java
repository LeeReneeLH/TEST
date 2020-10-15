/**
 * @author Administrator
 * @date 2014-10-16
 * 
 * @Description 
 */
package com.coffer.businesses.modules.screen;


import com.coffer.businesses.common.Constant;

/**
 * @author Lemon
 *
 */
public class ScreenConstant extends Constant {
	
	/**
	 * 数据类型
	 * @author 王林
	 *
	 */
	public static class DataType{
		public static final String NAME = "DataType";			//数据类型名
		public static final String VALUE_ALL = "1";				//全国
		public static final String VALUE_PART = "2";		    //部分地区
	}
	
	/**
	 * 省级编号
	 * 
	 * @author qph
	 *
	 */
	public static class provinceCode {
		public static final String LNCODE = "21"; // 辽宁省
	}

	/**
	 * 省级名称
	 * 
	 * @author qph
	 *
	 */
	public static class provinceName {
		public static final String LNNAME = "辽宁省"; // 辽宁省
	}

	/**
	 * 市级名称
	 * 
	 * @author qph
	 *
	 */
	public static class cityName {
		public static final String ASNAME = "鞍山市"; // 鞍山市
	}

	/**
	 * 地图类型
	 * 
	 * @author qph
	 *
	 */
	public static class MapType {
		public static final String COUNTRYMAP = "countrymap"; // 全国地图
		public static final String PROVINCEMAP = "provincemap"; // 省地图
		public static final String CITYMAP = "countrymap"; // 市地图
	}

	/**
	 * 地图在线离线标识
	 * 
	 * @author qph
	 *
	 */
	public static class MapFlag {
		public static final String ONLINE = "0"; // 在线
		public static final String OFFLINE = "1"; // 离线
	}

	/**
	 * 地图类型code
	 * 
	 * @author qph
	 * 
	 */
	public static class MapTypeCode {
		public static final String CHINAMAPCODE = "1"; // 全国地图code
		public static final String PROVINCEMAPCODE = "2"; // 全省地图code
		public static final String CITYMAPCODE = "3"; // 全市地图code
	}

	/**
	 * 平台服务名称
	 * 
	 * @author qph
	 * 
	 */
	public static class ServiceName {
		public static final String CLEARSERVICE = "清分服务";
		public static final String ISSUANCESERVICE = "发行库";
		public static final String CASHSERVICE = "现金库";
		public static final String DOORSERVICE = "上门收款";
		public static final String ATMSERVICE = "自助设备";
	}
}
