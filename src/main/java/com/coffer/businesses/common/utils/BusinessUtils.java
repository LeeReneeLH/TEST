package com.coffer.businesses.common.utils;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.common.service.SerialNumberService;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.SpringContextHolder;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.DictUtils;

/**
 * 业务工具类
 * 
 * @author Murphy
 * @version 2014-11-07
 */
public class BusinessUtils extends CommonUtils {

	/**
	 * 根据业务生成自增序列
	 */
	private static SerialNumberService serialNumberService = SpringContextHolder.getBean(SerialNumberService.class);

	/**
	 * 机构管理Service
	 */
	private static OfficeService officeService = SpringContextHolder.getBean(OfficeService.class);

	/**
	 * 箱袋信息管理Service
	 */
	private static StoBoxInfoService boxInfoService = SpringContextHolder.getBean(StoBoxInfoService.class);

	/**
	 * 验证箱袋编号(有效性，是否在数据库中存在，是否属于机构)
	 * 
	 * @author ChengShu
	 * @version 2014年11月26日
	 * 
	 * @param boxNo
	 *            箱号
	 * @param office
	 *            机构
	 * @param status
	 *            状态验证（true:验证/false：不验证）
	 * @return 0：验证成功/其他：箱号有效性失败/箱号不存在/箱号不属于该机构
	 */
	public static String isBoxNoValidate(String boxNo, Office office, boolean status, StoBoxInfo boxInfoInput) {

		// 验证箱袋编号有效性
		if (!BusinessUtils.validateBoxNo(boxNo)) {
			return "message.E0001";
		}
		// 填充后的箱袋编号
		boxNo = BusinessUtils.fillBoxNo(boxNo);

		// 取得箱袋信息
		StoBoxInfo boxInfo = new StoBoxInfo();
		boxInfo.setId(boxNo);
		if (boxInfoInput == null) {
			boxInfo = boxInfoService.getBindingBoxInfo(boxInfo).get(0);
		} else {
			boxInfo = boxInfoInput;
		}
		// 在数据库中不存在
		if (null == boxInfo) {
			return "message.E0002";
		}

		// 如果机构不为空，验证机构是否一致
		if (null != office) {
			// 验证机构是否一致
			if (!office.getId().equals(boxInfo.getOffice().getId())) {
				return "message.E0003";
			}
		}

		// 验证箱子的状态
		if (status) {
			// 是否为空箱
			if (!Constant.BoxStatus.EMPTY.equals(boxInfo.getBoxStatus())) {
				return "message.common.boxIsUsed";
			}
		}

		// 验证成功
		return Constant.SUCCESS;
	}

	/**
	 * 验证箱袋编号的有效性
	 * 
	 * @author Murphy
	 * @date 2014年11月12日
	 * 
	 * @Description
	 * @param boxNo
	 * @return
	 */
	public static boolean validateBoxNo(String boxNo) {
		// 箱袋编号为空
		if (StringUtils.isEmpty(boxNo)) {
			return false;
		}
		// 获取箱袋编号的最大有效长度
		int boxNoMaxLen = Integer.valueOf(Global.getConfig("boxNo.max.length"));
		// 获取箱袋编号的最小有效长度
		int boxNoMinLen = Integer.valueOf(Global.getConfig("boxNo.min.length"));
		// 箱袋编号长度
		int len = boxNo.length();
		// 箱袋编号必须大于等于3，小于等于箱袋最大有效位数
		if (len < boxNoMinLen || len > boxNoMaxLen) {
			return false;
		}
		// 修改人：LLF 修改时间 ： 2015-01-08 begin 修改内容：验证箱袋类型是否存在
		String type = boxNo.substring(len - 2);
		String label = DictUtils.getDictLabel(type, "sto_box_type", null);
		if (StringUtils.isBlank(label)) {
			return false;
		}
		// end
		// 验证序列号与验证码
		if (!BusinessUtils.validateSeqNoAndValNo(boxNo)) {
			return false;
		}

		return true;
	}

	/**
	 * 验证序列号与验证码
	 * 
	 * @author Murphy
	 * @date 2014年11月12日
	 * 
	 * @Description
	 * @param boxNo
	 * @return
	 */
	public static boolean validateSeqNoAndValNo(String boxNo) {
		// 箱袋编号为空
		if (StringUtils.isEmpty(boxNo)) {
			return false;
		}
		// 填充后的箱袋编号
		boxNo = BusinessUtils.fillBoxNo(boxNo);
		// 转换序列号为整数
		int seqNo = BusinessUtils.castSeqNo2Int(boxNo);

		// 序列号转换后的验证码
		String validateNo = BusinessUtils.castSeqNo2ValidateNo(seqNo);
		// 截取箱袋编号获取的验证码
		int seqNoLen = Integer.valueOf(Global.getConfig("boxNo.seqNo.length"));
		String bValidateNo = boxNo.substring(seqNoLen, seqNoLen + 1);
		// 序列号不合法，导致验证码为空
		if (validateNo == null) {
			return false;
		} else {
			// 判断验证码是否相等
			if (!validateNo.equalsIgnoreCase(bValidateNo)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 填充系列號，位数不足，左侧用0补足
	 * 
	 * @author Murphy
	 * @date 2014年11月18日
	 * 
	 * @Description
	 * @param seqNo
	 * @return
	 */
	public static String fillSeqNo(int seqNo) {
		String str = String.valueOf(seqNo);
		int len = str.length();
		// 序列号有效长度
		int seqNoLen = Integer.valueOf(Global.getConfig("boxNo.seqNo.length"));
		if (len < seqNoLen && len > 0) {
			for (int i = 0; i < (seqNoLen - len); i++) {
				str = "0" + str;
			}
		}
		return str;
	}

	/**
	 * 填充箱袋编号，位数不足，左侧用0补足
	 * 
	 * @author Murphy
	 * @date 2014年11月18日
	 * 
	 * @Description
	 * @param boxNo
	 * @return
	 */
	public static String fillBoxNo(String boxNo) {
		int len = boxNo.length();
		// 箱袋编号的最大有效长度
		int boxNoLen = Integer.valueOf(Global.getConfig("boxNo.max.length"));
		if (len < boxNoLen && len > 0) {
			for (int i = 0; i < (boxNoLen - len); i++) {
				boxNo = "0" + boxNo;
			}
		}
		return boxNo;
	}

	/**
	 * 将序列号转为数字
	 * 
	 * @author Murphy
	 * @date 2014年11月18日
	 * 
	 * @Description
	 * @param boxNo
	 * @return
	 */
	public static Integer castSeqNo2Int(String boxNo) {

		// 获取箱袋序列号的有效长度
		int seqNoLen = Integer.valueOf(Global.getConfig("boxNo.seqNo.length"));
		// 箱袋编号不足，则填充位数
		boxNo = BusinessUtils.fillBoxNo(boxNo);
		// 截取的序列号
		String seqNo = boxNo.substring(0, seqNoLen);
		// // 检索序列号中'0'的位置
		// int index = seqNo.lastIndexOf("0");
		// 转化为整形的序列号
		int seqNoInt = -1;
		// 标示符
		// int flagIndex = 0;
		//
		// if (index != -1) {
		// for (int i = 0; i < index; i++) {
		// char c = seqNo.charAt(i);
		// if (c != '0') {
		// flagIndex = i;
		// break;
		// }
		// }
		// // 获取有效的序列号
		// seqNo = seqNo.substring(flagIndex);
		// }
		try {
			seqNoInt = Integer.valueOf(seqNo);
		} catch (Exception e) {
			// 序列号不合法
			return seqNoInt;
		}
		return seqNoInt;
	}

	/**
	 * 将序列号转为校验码
	 * 
	 * @author Murphy
	 * @date 2014年11月18日
	 * 
	 * @Description
	 * @param seqNo
	 * @return
	 */
	public static String castSeqNo2ValidateNo(int seqNo) {
		// 序列号不合法
		if (seqNo < 0) {
			return null;
		}
		// 整形序列号转为字符串
		String str = String.valueOf(seqNo);
		int sum = 0;
		for (int i = 0; i < str.length(); i++) {
			sum += Integer.valueOf(String.valueOf(str.charAt(i)));
		}
		// 序列号各位相加和
		str = String.valueOf(sum);
		// 返回验证码
		return String.valueOf(str.charAt(str.length() - 1));
	}

	/**
	 * 填充机构编号，位数不足，右侧用0补足
	 * 
	 * @author Murphy
	 * @date 2014年11月18日
	 * 
	 * @Description
	 * @param officeId
	 * @return
	 */
	public static String fillOfficeId(String officeId) {
		// 获取机构有效位数 eg:0203 需要203 修改人：LLF 修改时间 ： 2015-01-08 begin
		int officeInt = Integer.parseInt(officeId);
		officeId = String.valueOf(officeInt);
		// end

		int len = officeId.length();
		// 获取机构编号的有效长度
		int officeLen = Integer.valueOf(Global.getConfig("office.max.length"));
		if (len < officeLen && len > 0) {
			for (int i = 0; i < (officeLen - len); i++) {
				officeId = officeId + "0";
			}
		}
		return officeId;
	}

	/**
	 * 填充钞箱类型编号，位数不足，右侧用0补足
	 * 
	 * @author Murphy
	 * @date 2015年05月20日
	 * 
	 * @Description
	 * @param boxTypeNo
	 * @return
	 */
	public static String fillBoxTypeNo(String boxTypeNo) {
		if (StringUtils.isNoneBlank(boxTypeNo)) {
			int boxTypeInt = Integer.parseInt(boxTypeNo);
			boxTypeNo = String.valueOf(boxTypeInt);
		}

		int len = boxTypeNo.length();
		// 获取机构编号的有效长度
		int boxTypeLen = Integer.valueOf(Global.getConfig("boxtypeno.max.length"));
		if (len < boxTypeLen && len > 0) {
			for (int i = 0; i < (boxTypeLen - len); i++) {
				boxTypeNo = boxTypeNo + "0";
			}
		}
		return boxTypeNo;
	}

	/**
	 * 生成RFID码
	 * 
	 * @author Murphy
	 * @date 2014年11月18日
	 * 
	 * @Description
	 * @param boxType
	 * @param seqNo
	 * @param boxTypeNo
	 * @param officeId
	 * @return
	 */
	public static String generateRFID(String boxType, int seqNo, String officeId, String boxTypeNo) {
		String RFID = "";

		// 钞箱RFID生成规则：钞箱类型编号8位+箱袋编号8位
		if (Constant.BoxType.BOX_NOTE.equals(boxType)) {
			if (StringUtils.isNoneBlank(boxTypeNo)) {
				String boxTypeNo1 = boxTypeNo + Global.getConfig("atmBox.mark");
				RFID = BusinessUtils.fillBoxTypeNo(boxTypeNo1) + BusinessUtils.generateBoxNo(boxType, seqNo);
			}
		}
		// 钞箱外RFID生成规则：机构8位+箱袋编号8位
		else {
			if (StringUtils.isNotBlank(officeId)) {
				RFID = BusinessUtils.fillOfficeId(officeId) + BusinessUtils.generateBoxNo(boxType, seqNo);
			}
		}

		return RFID;
	}

	/**
	 * 创建箱袋编号
	 * 
	 * @author Murphy
	 * @date 2014年11月17日
	 * 
	 * @Description
	 * @param boxType
	 *            箱袋类型
	 * @param seqNo
	 *            序列号
	 * @return
	 */
	public static String generateBoxNo(String boxType, int seqNo) {
		// 创建箱袋编号
		String boxNo = BusinessUtils.fillSeqNo(seqNo) + BusinessUtils.castSeqNo2ValidateNo(seqNo) + boxType;
		return boxNo;
	}

	//
	/**
	 * @author wyj
	 * @date 2014-12-8
	 * 
	 * @Description 依据机构返回所属的业务金库（分金库或者总金库）
	 * @param office
	 * @return
	 */
	public static Office getCashCenterByOffice(Office office) {
		// 为空
		if (office == null) {
			return null;
		}
		// 当前机构类型为分金库或者总金库
		// if (Constant.OfficeType.BRANCH_COFFERS.equals(office.getType())
		// || Constant.OfficeType.HEAD_COFFERS.equals(office.getType())) {
		// return office;
		// 当前机构类型为金库
		if (Constant.OfficeType.COFFER.equals(office.getType())) {
			return office;
		} else {
			// 判断父级机构是否满足条件
			return getCashCenterByOffice(officeService.get(office.getParent()));
		}
	}

	/**
	 * 取得商业银行所属人行机构
	 * 
	 * @author WangBaozhong
	 * @version 2016年5月31日
	 * 
	 * 
	 * @param office
	 *            商行机构
	 * @return 人行机构
	 */
	public static Office getPbocCenterByOffice(Office office) {
		// 为空
		if (office == null) {
			return null;
		}

		// 当前机构类型为人行金库
		if (Constant.OfficeType.CENTRAL_BANK.equals(office.getType())) {
			return office;
		} else {
			// 判断父级机构是否满足条件
			return getPbocCenterByOffice(officeService.get(office.getParent()));
		}
	}
	
	/**
	 * 
	 * @author Clark
	 * @version 2015-05-14
	 * 
	 * @Description 根据不同业务生成流水单号
	 * @param busiType
	 * @return
	 */
	public static synchronized String getNewBusinessNo(String busiType, Office office) {
		// 取得当前机构金库编号
		office = BusinessUtils.getCashCenterByOffice(office);

		String officeCode = office != null ? office.getCode() : "";
		officeCode = officeCode != null ? officeCode : "";
		// 是否使用金库编码开关
		String usedOffice = Global.getConfig("business.serialNo.usedOfficeCode");
		if (StringUtils.isBlank(usedOffice)) {
			officeCode = "";
		}
		// 自增序列长度
		int seqLength = Integer.parseInt(Global.getConfig("business.serialNo.seqLength"));
		// 当前序列号
		int seqNo = serialNumberService.getSerialNumber(busiType);
		// 生成新的业务流水
		return CommonUtils.createSerialNo(seqNo, seqLength, officeCode, busiType);
	}
	
	/**
	 * 
	 * Title: getOfficeBusinessNo
	 * <p>Description: 用当前机构生成流水单号生成</p>
	 * @author:     wangbaozhong
	 * @param busiType
	 * @param office
	 * @return 
	 * String    返回类型
	 */
	public static synchronized String getOfficeBusinessNo(String busiType, Office office) {

		String officeCode = office != null ? office.getCode() : "";
		// 是否使用金库编码开关
		String usedOffice = Global.getConfig("business.serialNo.usedOfficeCode");
		if (StringUtils.isBlank(usedOffice)) {
			officeCode = "";
		}
		// 自增序列长度
		int seqLength = Integer.parseInt(Global.getConfig("business.serialNo.seqLength"));
		// 当前序列号
		int seqNo = serialNumberService.getSerialNumber(busiType);
		// 生成新的业务流水
		return CommonUtils.createSerialNo(seqNo, seqLength, officeCode, busiType);
	}

	/**
	 * (人行业务)根据不同业务生成流水单号
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月8日
	 * 
	 * 
	 * @param busiType
	 *            业务类型
	 * @param office
	 *            登陆用户所属机构
	 * @return 流水单号
	 */
	public static synchronized String getPbocNewBusinessNo(String busiType, Office office) {
		// 取得当前机构金库编号
		office = BusinessUtils.getPbocCenterByOffice(office);

		String officeCode = office != null ? office.getCode() : "";
		officeCode = officeCode != null ? officeCode : "";
		// 是否使用金库编码开关
		String usedOffice = Global.getConfig("business.serialNo.usedOfficeCode");
		if (StringUtils.isBlank(usedOffice)) {
			officeCode = "";
		}
		// 自增序列长度
		int seqLength = Integer.parseInt(Global.getConfig("business.serialNo.seqLength"));
		// 当前序列号
		int seqNo = serialNumberService.getSerialNumber(busiType);
		// 生成新的业务流水
		return CommonUtils.createSerialNo(seqNo, seqLength, officeCode, busiType);
	}
	
	/**
	 * (现钞清分中心业务)根据不同业务生成流水单号
	 * 
	 * @author LLF
	 * @version 2017年9月21日
	 * 
	 * 
	 * @param busiType
	 *            业务类型
	 * @param office
	 *            登陆用户所属机构
	 * @return 流水单号
	 */
	public static synchronized String getClearNewBusinessNo(String busiType, Office office) {

		String officeCode = office != null ? office.getCode() : "";
		officeCode = officeCode != null ? officeCode : "";
		// 是否使用金库编码开关
		String usedOffice = Global.getConfig("business.serialNo.usedOfficeCode");
		if (StringUtils.isBlank(usedOffice)) {
			officeCode = "";
		}
		// 自增序列长度
		int seqLength = Integer.parseInt(Global.getConfig("business.serialNo.seqLength"));
		// 当前序列号
		int seqNo = serialNumberService.getSerialNumber(busiType);
		// 生成新的业务流水
		return CommonUtils.createSerialNo(seqNo, seqLength, officeCode, busiType);
	}

	/**
	 * 根据流水单号取得业务类型
	 * 
	 * @author WangBaozhong
	 * @version 2016年7月9日
	 * 
	 * 
	 * @param allId
	 *            流水单号
	 * @return 业务类型
	 */
	public static String getBusinessTypeFromAllId(String allId) {

		if (StringUtils.isBlank(allId) || allId.length() < 6) {
			return "";
		}
		// 自增序列长度
		int seqLength = Integer.parseInt(Global.getConfig("business.serialNo.seqLength"));
		String businessType = allId.substring(allId.length() - seqLength - 2, allId.length() - seqLength);
		return businessType;
	}

	/**
	 * 
	 * Title: getClearCenterByParentId
	 * <p>
	 * Description: 根据父机构ID查询清分中心
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param parentsId
	 * @return Office 返回类型
	 */
	public static Office getClearCenterByParentId(String parentId) {
		return officeService.getClearCenterByParentId(parentId);
	}
}
