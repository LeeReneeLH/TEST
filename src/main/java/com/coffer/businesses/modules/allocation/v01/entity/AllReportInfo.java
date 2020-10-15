package com.coffer.businesses.modules.allocation.v01.entity;

import java.math.BigDecimal;
import java.util.List;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.google.common.collect.Lists;

/**
 * 调缴功能Entity
 * 
 * @author Chengshu
 * @version 2015-05-11
 */
public class AllReportInfo extends DataEntity<AllReportInfo> {

	private static final long serialVersionUID = 1L;

	/** ----------调拨相关---------- **/
	/** 流水号 **/
	private String allId = "";
	/** 业务类型（0：ATM，1：库间） **/
	private String businessType;
	/** 库间调缴出入库类型(现金：10:出库/11:入库)(ATM：20:出库/21:入库)) **/
	private String inoutType;
	
	/** 日期 **/
	private String time = "";

	/** ----------物品相关---------- **/
	/** 币种 */
	private String currency;
	/** 类别 */
	private String classification;

	/** 面值01 */
	private BigDecimal denomination1 = new BigDecimal(0.0d);
	/** 面值02 */
	private BigDecimal denomination2 = new BigDecimal(0.0d);
	/** 面值03 */
	private BigDecimal denomination3 = new BigDecimal(0.0d);
	/** 面值04 */
	private BigDecimal denomination4 = new BigDecimal(0.0d);
	/** 面值05 */
	private BigDecimal denomination5 = new BigDecimal(0.0d);
	/** 面值06 */
	private BigDecimal denomination6 = new BigDecimal(0.0d);
	/** 面值07 */
	private BigDecimal denomination7 = new BigDecimal(0.0d);
	/** 面值08 */
	private BigDecimal denomination8 = new BigDecimal(0.0d);
	/** 面值09 */
	private BigDecimal denomination9 = new BigDecimal(0.0d);
	/** 面值10 */
	private BigDecimal denomination10 = new BigDecimal(0.0d);
	/** 面值11 */
	private BigDecimal denomination11 = new BigDecimal(0.0d);
	/** 面值12 */
	private BigDecimal denomination12 = new BigDecimal(0.0d);
	/** 面值13 */
	private BigDecimal denomination13 = new BigDecimal(0.0d);
	/** 面值14 */
	private BigDecimal denomination14 = new BigDecimal(0.0d);
	/** 面值15 */
	private BigDecimal denomination15 = new BigDecimal(0.0d);
	/** 面值16 */
	private BigDecimal denomination16 = new BigDecimal(0.0d);
	/** 面值17 */
	private BigDecimal denomination17 = new BigDecimal(0.0d);
	/** 面值18 */
	private BigDecimal denomination18 = new BigDecimal(0.0d);
	/** 面值19 */
	private BigDecimal denomination19 = new BigDecimal(0.0d);
	/** 面值20 */
	private BigDecimal denomination20 = new BigDecimal(0.0d);
	/** 面值21 */
	private BigDecimal denomination21 = new BigDecimal(0.0d);
	/** 面值22 */
	private BigDecimal denomination22 = new BigDecimal(0.0d);
	/** 面值23 */
	private BigDecimal denomination23 = new BigDecimal(0.0d);
	/** 面值24 */
	private BigDecimal denomination24 = new BigDecimal(0.0d);
	/** 面值25 */
	private BigDecimal denomination25 = new BigDecimal(0.0d);
	/** 面值26 */
	private BigDecimal denomination26 = new BigDecimal(0.0d);
	/** 面值27 */
	private BigDecimal denomination27 = new BigDecimal(0.0d);
	/** 面值28 */
	private BigDecimal denomination28 = new BigDecimal(0.0d);
	/** 面值29 */
	private BigDecimal denomination29 = new BigDecimal(0.0d);
	/** 面值30 */
	private BigDecimal denomination30 = new BigDecimal(0.0d);
	
	/** 核对结果 */
	private String checkResult;
	/** 登记机构名称 */
	private String rOfficeName;
	/** 登记人名称 */
	private String registUserName;
	/** 接收人名称 */
	private String acceptUserName;
	
	/** 物品名称 */
	private String goodsName;
	/** 数量 */
	private Long moneyNumber;
	/** 网点名称 */
	private String officeName;
	/** 创建人 */
	private String createName;

	/** 调拨物品详细列表 */
	private List<AllAllocateItem> allAllocateItemList = Lists.newArrayList();

	/** 金额 */
	private Long moneyAmount;
	/** 接收机构名称 */
	private String aOfficeName;
	/** 物品编号 */
	private String goodsId;

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	@ExcelField(title = "金额")
	public Long getMoneyAmount() {
		return moneyAmount;
	}

	@ExcelField(title = "接收机构名称")
	public String getaOfficeName() {
		return aOfficeName;
	}
	
	public void setaOfficeName(String aOfficeName) {
		this.aOfficeName = aOfficeName;
	}

	public void setMoneyAmount(Long moneyAmount) {
		this.moneyAmount = moneyAmount;
	}

	@ExcelField(title = "流水号")
	public String getAllId() {
		return allId;
	}

	public void setAllId(String allId) {
		this.allId = allId;
	}

	@ExcelField(title = "业务类型")
	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	@ExcelField(title = "出入库类型")
	public String getInoutType() {
		return inoutType;
	}

	public void setInoutType(String inoutType) {
		this.inoutType = inoutType;
	}

	@ExcelField(title = "日期")
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@ExcelField(title = "币种")
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@ExcelField(title = "类别")
	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	@ExcelField(title = "核对结果")
	public String getCheckResult() {
		return checkResult;
	}

	@ExcelField(title = "面值01")
	public BigDecimal getDenomination1() {
		return denomination1;
	}

	@ExcelField(title = "面值02")
	public BigDecimal getDenomination2() {
		return denomination2;
	}

	@ExcelField(title = "面值03")
	public BigDecimal getDenomination3() {
		return denomination3;
	}

	@ExcelField(title = "面值04")
	public BigDecimal getDenomination4() {
		return denomination4;
	}

	@ExcelField(title = "面值05")
	public BigDecimal getDenomination5() {
		return denomination5;
	}

	@ExcelField(title = "面值06")
	public BigDecimal getDenomination6() {
		return denomination6;
	}

	@ExcelField(title = "面值07")
	public BigDecimal getDenomination7() {
		return denomination7;
	}

	@ExcelField(title = "面值08")
	public BigDecimal getDenomination8() {
		return denomination8;
	}

	@ExcelField(title = "面值09")
	public BigDecimal getDenomination9() {
		return denomination9;
	}

	@ExcelField(title = "面值10")
	public BigDecimal getDenomination10() {
		return denomination10;
	}

	@ExcelField(title = "面值11")
	public BigDecimal getDenomination11() {
		return denomination11;
	}

	@ExcelField(title = "面值12")
	public BigDecimal getDenomination12() {
		return denomination12;
	}

	@ExcelField(title = "面值13")
	public BigDecimal getDenomination13() {
		return denomination13;
	}

	@ExcelField(title = "面值14")
	public BigDecimal getDenomination14() {
		return denomination14;
	}

	@ExcelField(title = "面值15")
	public BigDecimal getDenomination15() {
		return denomination15;
	}

	@ExcelField(title = "面值16")
	public BigDecimal getDenomination16() {
		return denomination16;
	}

	@ExcelField(title = "面值17")
	public BigDecimal getDenomination17() {
		return denomination17;
	}

	@ExcelField(title = "面值18")
	public BigDecimal getDenomination18() {
		return denomination18;
	}

	@ExcelField(title = "面值19")
	public BigDecimal getDenomination19() {
		return denomination19;
	}

	@ExcelField(title = "面值20")
	public BigDecimal getDenomination20() {
		return denomination20;
	}

	public void setDenomination20(BigDecimal denomination20) {
		this.denomination20 = denomination20;
	}

	@ExcelField(title = "面值21")
	public BigDecimal getDenomination21() {
		return denomination21;
	}

	public void setDenomination21(BigDecimal denomination21) {
		this.denomination21 = denomination21;
	}

	@ExcelField(title = "面值22")
	public BigDecimal getDenomination22() {
		return denomination22;
	}

	public void setDenomination22(BigDecimal denomination22) {
		this.denomination22 = denomination22;
	}

	@ExcelField(title = "面值23")
	public BigDecimal getDenomination23() {
		return denomination23;
	}

	public void setDenomination23(BigDecimal denomination23) {
		this.denomination23 = denomination23;
	}

	@ExcelField(title = "面值24")
	public BigDecimal getDenomination24() {
		return denomination24;
	}

	public void setDenomination24(BigDecimal denomination24) {
		this.denomination24 = denomination24;
	}

	@ExcelField(title = "面值25")
	public BigDecimal getDenomination25() {
		return denomination25;
	}

	public void setDenomination25(BigDecimal denomination25) {
		this.denomination25 = denomination25;
	}

	@ExcelField(title = "面值26")
	public BigDecimal getDenomination26() {
		return denomination26;
	}

	public void setDenomination26(BigDecimal denomination26) {
		this.denomination26 = denomination26;
	}

	@ExcelField(title = "面值27")
	public BigDecimal getDenomination27() {
		return denomination27;
	}

	public void setDenomination27(BigDecimal denomination27) {
		this.denomination27 = denomination27;
	}

	@ExcelField(title = "面值28")
	public BigDecimal getDenomination28() {
		return denomination28;
	}

	public void setDenomination28(BigDecimal denomination28) {
		this.denomination28 = denomination28;
	}

	@ExcelField(title = "面值29")
	public BigDecimal getDenomination29() {
		return denomination29;
	}

	public void setDenomination29(BigDecimal denomination29) {
		this.denomination29 = denomination29;
	}

	@ExcelField(title = "面值30")
	public BigDecimal getDenomination30() {
		return denomination30;
	}

	public void setDenomination30(BigDecimal denomination30) {
		this.denomination30 = denomination30;
	}

	public void setDenomination1(BigDecimal denomination1) {
		this.denomination1 = denomination1;
	}

	public void setDenomination2(BigDecimal denomination2) {
		this.denomination2 = denomination2;
	}

	public void setDenomination3(BigDecimal denomination3) {
		this.denomination3 = denomination3;
	}

	public void setDenomination4(BigDecimal denomination4) {
		this.denomination4 = denomination4;
	}

	public void setDenomination5(BigDecimal denomination5) {
		this.denomination5 = denomination5;
	}

	public void setDenomination6(BigDecimal denomination6) {
		this.denomination6 = denomination6;
	}

	public void setDenomination7(BigDecimal denomination7) {
		this.denomination7 = denomination7;
	}

	public void setDenomination8(BigDecimal denomination8) {
		this.denomination8 = denomination8;
	}

	public void setDenomination9(BigDecimal denomination9) {
		this.denomination9 = denomination9;
	}

	public void setDenomination10(BigDecimal denomination10) {
		this.denomination10 = denomination10;
	}

	public void setDenomination11(BigDecimal denomination11) {
		this.denomination11 = denomination11;
	}

	public void setDenomination12(BigDecimal denomination12) {
		this.denomination12 = denomination12;
	}

	public void setDenomination13(BigDecimal denomination13) {
		this.denomination13 = denomination13;
	}

	public void setDenomination14(BigDecimal denomination14) {
		this.denomination14 = denomination14;
	}

	public void setDenomination15(BigDecimal denomination15) {
		this.denomination15 = denomination15;
	}

	public void setDenomination16(BigDecimal denomination16) {
		this.denomination16 = denomination16;
	}

	public void setDenomination17(BigDecimal denomination17) {
		this.denomination17 = denomination17;
	}

	public void setDenomination18(BigDecimal denomination18) {
		this.denomination18 = denomination18;
	}

	public void setDenomination19(BigDecimal denomination19) {
		this.denomination19 = denomination19;
	}

	public void setCheckResult(String checkResult) {
		this.checkResult = checkResult;
	}

	@ExcelField(title = "登记机构名称")
	public String getrOfficeName() {
		return rOfficeName;
	}

	public void setrOfficeName(String rOfficeName) {
		this.rOfficeName = rOfficeName;
	}

	@ExcelField(title = "登记人名称")
	public String getRegistUserName() {
		return registUserName;
	}

	public void setRegistUserName(String registUserName) {
		this.registUserName = registUserName;
	}

	@ExcelField(title = "接收人名称")
	public String getAcceptUserName() {
		return acceptUserName;
	}

	public void setAcceptUserName(String acceptUserName) {
		this.acceptUserName = acceptUserName;
	}

	@ExcelField(title = "物品名称")
	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	@ExcelField(title = "数量")
	public Long getMoneyNumber() {
		return moneyNumber;
	}

	public void setMoneyNumber(Long moneyNumber) {
		this.moneyNumber = moneyNumber;
	}

	@ExcelField(title = "机构名称")
	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public List<AllAllocateItem> getAllAllocateItemList() {
		return allAllocateItemList;
	}

	public void setAllAllocateItemList(List<AllAllocateItem> allAllocateItemList) {
		this.allAllocateItemList = allAllocateItemList;
	}

	@ExcelField(title = "创建人")
	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}
}