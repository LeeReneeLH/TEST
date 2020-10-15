package com.coffer.external.hessian;

import java.util.Arrays;
import java.util.List;

/**
 * 接口常量类
 * 
 * @author chengshu
 * @date 2015年12月23日
 */
public class HardwareConstant {

	public static final String SERVICE_BEAN_NAME_PREFIX = "Service";

	/**
	 * 接口服务代码
	 * 
	 * @author chengshu
	 *
	 */
	public static class ServiceNo {
		public static final String service_no_S01 = "01";
		public static final String service_no_S02 = "02";
		public static final String service_no_S03 = "03";
		public static final String service_no_S04 = "04";
		public static final String service_no_S05 = "05";
		public static final String service_no_S06 = "06";
		public static final String service_no_S07 = "07";
		public static final String service_no_S08 = "08";
		public static final String service_no_S09 = "09";
		public static final String service_no_S10 = "10";
		public static final String service_no_S11 = "11";
		public static final String service_no_S12 = "12";
		public static final String service_no_S13 = "13";
		public static final String service_no_S14 = "14";
		public static final String service_no_S15 = "15";
		public static final String service_no_S16 = "16";
		public static final String service_no_S17 = "17";
		public static final String service_no_S18 = "18";
		public static final String service_no_S19 = "19";
		public static final String service_no_S20 = "20";
		public static final String service_no_S21 = "21";
		public static final String service_no_S22 = "22";
		public static final String service_no_S23 = "23";
		public static final String service_no_S24 = "24";
		public static final String service_no_S25 = "25";
		public static final String service_no_S26 = "26";
		public static final String service_no_S27 = "27";
		public static final String service_no_S28 = "28";
		public static final String service_no_S29 = "29";
		public static final String service_no_S30 = "30";
		public static final String service_no_S31 = "31";
		public static final String service_no_S32 = "32";
		public static final String service_no_S33 = "33";
		public static final String service_no_S34 = "34";
		public static final String service_no_S35 = "35";
		public static final String service_no_S36 = "36";
		public static final String service_no_S37 = "37";
		public static final String service_no_S38 = "38";
		public static final String service_no_S39 = "39";
		public static final String service_no_S40 = "40";
		/** 修改登录密码 */
		public static final String service_no_S41 = "41";
		/** 人行接口编码 **/
		public static final String service_no_S42 = "42";
		public static final String service_no_S43 = "43";
		public static final String service_no_S44 = "44";
		public static final String service_no_S45 = "45";
		public static final String service_no_S46 = "46";
		public static final String service_no_S47 = "47";
		public static final String service_no_S48 = "48";
		public static final String service_no_S49 = "49";
		public static final String service_no_S50 = "50";
		public static final String service_no_S51 = "51";
		public static final String service_no_S52 = "52";
		public static final String service_no_S53 = "53";
		public static final String service_no_S54 = "54";
		public static final String service_no_S55 = "55";
		public static final String service_no_S56 = "56";
		public static final String service_no_S57 = "57";
		public static final String service_no_S58 = "58";
		public static final String service_no_S59 = "59";
		public static final String service_no_S60 = "60";
		public static final String service_no_S61 = "61";
		public static final String service_no_S62 = "62";
		public static final String service_no_S63 = "63";
		public static final String service_no_S64 = "64";
		public static final String service_no_S65 = "65";
		public static final String service_no_S66 = "66";
		public static final String service_no_S67 = "67";
		public static final String service_no_S68 = "68";
		public static final String service_no_S69 = "69";
		public static final String service_no_S70 = "70";
		// 追加PDA装箱登记的接口服务代码 修改人：xp 修改时间：2017-7-7 begin
		public static final String service_no_S0180 = "0180";
		// end
		// 追加PDA更换机构的接口服务代码 修改人：xp 修改时间：2017-7-7 begin
		public static final String service_no_S0181 = "0181";
		// end
		public static final String service_no_S0182 = "0182";
		public static final String service_no_S0183 = "0183";
		public static final String service_no_S0185 = "0185";
	}

	/** 所有PDA操作接口的服务代码 */
	public static List<String> pdaServices = Arrays.asList(ServiceNo.service_no_S08, ServiceNo.service_no_S09,
			ServiceNo.service_no_S10, ServiceNo.service_no_S13, ServiceNo.service_no_S14, ServiceNo.service_no_S15,
			ServiceNo.service_no_S18, ServiceNo.service_no_S19, ServiceNo.service_no_S20, ServiceNo.service_no_S21,
			ServiceNo.service_no_S35, ServiceNo.service_no_S36, ServiceNo.service_no_S37, ServiceNo.service_no_S38,
			ServiceNo.service_no_S39,
			// 追加PDA装箱登记接口服务代码 修改人：xp 修改时间：2017-7-7 begin
			ServiceNo.service_no_S0180,
			// end
			// 追加PDA更换机构接口服务代码 修改人：xp 修改时间：2017-7-7 begin
			ServiceNo.service_no_S0181
	// end
	);

	/** 是否确认绑定标识 */
	public static class ReBindingFlag {
		public static final String binding = "0";
		public static final String yes = "1";
	}

	/** 人行出入库箱袋错误种别 */
	public static class ErrorRfidType {
		public static final String not_exist = "0";
		public static final String first_out = "1";
	}

	/** 卡片长度 **/
	public static class CardLength {
		public static final int box = 8;
		public static final int rfid = 16;
	}
}
