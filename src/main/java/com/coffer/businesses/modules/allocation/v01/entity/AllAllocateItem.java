/**
 * @author Administrator
 * @version 2015年9月2日
 * 
 * 
 */
package com.coffer.businesses.modules.allocation.v01.entity;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;

/**
 * 调拨物品表 Entity
 * @author wangbaozhong
 * @version 2015-09-02
 *
 */
public class AllAllocateItem  extends DataEntity<AllAllocateItem> {
	
	private static final long serialVersionUID = 1L;
	
	/** 主键 */
	private String allItemsId;
	/** 缴调流水号 */
	private String allId;
	/** 箱号 */
	private String boxNo;
	/** 加钞计划批次号*/
	private String batchNo;
	/** 登记种别 */
	private String registType;
	/** 币种 */
	private String currency;
	/** 类别 */
	private String classification;
	/** 套别 */
	private String sets;
	/** 材质 */
	private String cash;
	/** 面值 */
	private String denomination;
	/** 单位 */
	private String unit;
	/** 备用 */
	private String bak;
	
	/** 数量 */
	private Long moneyNumber;
	/** 金额 */
	private BigDecimal moneyAmount;
	/** 配款数量 */
	private Long confirmNumber;
	/** 配款金额 */
	private BigDecimal confirmAmount;
	/** 提交页面类型 */
	private String pageType;
	/** 确认标识（0：预约、1：确认） **/
	private String confirmFlag;
	/** 物品ID **/
	private String goodsId;
	/** 主表信息 */
	private AllAllocateInfo allocationInfo;
	
	/** 日期 **/
	private String time = "";
	
	/** 物品名称 **/
	private String goodsName = "";
	
	/** 用户名 **/
	private String userName = "";
	
	public AllAllocateItem() {
		super();
	}

	public AllAllocateItem(String id){
		super(id);
	}
	
	@ExcelField(title="业务流水号")
	public String getAllId() {
		return allId;
	}

	public void setAllId(String allId) {
		this.allId = allId;
	}

	public String getBoxNo() {
		return boxNo;
	}

	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}

	public String getRegistType() {
		return registType;
	}

	public void setRegistType(String registType) {
		this.registType = registType;
	}

	public Long getMoneyNumber() {
		return moneyNumber;
	}

	@ExcelField(title="数量")
	public void setMoneyNumber(Long moneyNumber) {
		this.moneyNumber = moneyNumber;
	}

	public BigDecimal getMoneyAmount() {
		return moneyAmount;
	}

	public void setMoneyAmount(BigDecimal moneyAmount) {
		this.moneyAmount = moneyAmount;
	}

	public String getAllItemsId() {
		return allItemsId;
	}

	public void setAllItemsId(String allItemsId) {
		this.allItemsId = allItemsId;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	public AllAllocateInfo getAllocationInfo() {
		return allocationInfo;
	}

	public void setAllocationInfo(AllAllocateInfo allocationInfo) {
		this.allocationInfo = allocationInfo;
	}

	public Long getConfirmNumber() {
		return confirmNumber;
	}

	public void setConfirmNumber(Long confirmNumber) {
		this.confirmNumber = confirmNumber;
	}
	
	@Length(min=0, max=3, message="币种长度必须介于 0 和 3 之间")
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	@Length(min=0, max=2, message="类别长度必须介于 0 和 2 之间")
	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}
	
	@Length(min=0, max=1, message="套别长度必须介于 0 和 1 之间")
	public String getSets() {
		return sets;
	}

	public void setSets(String sets) {
		this.sets = sets;
	}
	
	@Length(min=0, max=1, message="材质长度必须介于 0 和 1 之间")
	public String getCash() {
		return cash;
	}

	public void setCash(String cash) {
		this.cash = cash;
	}
	
	@Length(min=0, max=2, message="面值长度必须介于 0 和 2 之间")
	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}
	
	@Length(min=0, max=3, message="单位长度必须介于 0 和 3 之间")
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	@Length(min=0, max=3, message="备用长度必须介于 0 和 3 之间")
	public String getBak() {
		return bak;
	}

	public void setBak(String bak) {
		this.bak = bak;
	}

	public BigDecimal getConfirmAmount() {
		return confirmAmount;
	}

	public void setConfirmAmount(BigDecimal confirmAmount) {
		this.confirmAmount = confirmAmount;
	}
	
	@ExcelField(title="日期")
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@ExcelField(title="物品名称")
	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	@ExcelField(title="用户名")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getConfirmFlag() {
		return confirmFlag;
	}

	public void setConfirmFlag(String confirmFlag) {
		this.confirmFlag = confirmFlag;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	
	public String getscurrency(String goodsId){
		
		return goodsId.substring(0,3);
	}
	public String getsclassification(String goodsId){
		
		return goodsId.substring(3,5);
	}
	public String getssets(String goodsId){
		
		return goodsId.substring(5,6);
	}
	public String getscash(String goodsId){
		
		return goodsId.substring(6,7);
	}
	public String getsdenomination(String goodsId){
		
		return goodsId.substring(7,9);
	}
	public String getsunit(String goodsId){
		
		return goodsId.substring(9,12);
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

}
