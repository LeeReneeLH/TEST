package com.coffer.businesses.modules.store.v01.entity;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.common.persistence.DataEntity;

/**
 * 盘点管理Entity
 * @author LLF
 * @version 2015-09-22
 */
public class StoStockCountInfo extends DataEntity<StoStockCountInfo> {
	
	private static final long serialVersionUID = 1L;
	private String stockCountId;		// 主键
	private String stockCountNo;		// 盘点编号
	private String updateFlag;		// 更新库存标识（0：未更新；1：已更新）
	private String stockCountType;		// （货币、贵金属、箱袋等）
	private String goodsId;		// 物品ID
	private String goodsName;		// 物品名称
	private Long stockCountNum;		// 盘点物品数量
	private Office office;		// 所属金库
	private String managerUserid;		// 审批人
	private String managerUsername;		// 审批人姓名
	private String createName;		// 盘点人姓名
	private String updateName;		// 库存更新人姓名
	private String goodsTypes;
	
	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;
	
	public StoStockCountInfo() {
		super();
	}

	public StoStockCountInfo(String id){
		super(id);
	}

	@Length(min=1, max=64, message="主键长度必须介于 1 和 64 之间")
	public String getStockCountId() {
		return stockCountId;
	}

	public void setStockCountId(String stockCountId) {
		this.stockCountId = stockCountId;
	}
	
	@Length(min=0, max=17, message="盘点编号长度必须介于 0 和 17 之间")
	public String getStockCountNo() {
		return stockCountNo;
	}

	public void setStockCountNo(String stockCountNo) {
		this.stockCountNo = stockCountNo;
	}
	
	@Length(min=0, max=1, message="更新库存标识（0：未更新；1：已更新）长度必须介于 0 和 1 之间")
	public String getUpdateFlag() {
		return updateFlag;
	}

	public void setUpdateFlag(String updateFlag) {
		this.updateFlag = updateFlag;
	}
	
	@Length(min=0, max=2, message="（货币、重空等）长度必须介于 0 和 2 之间")
	public String getStockCountType() {
		return stockCountType;
	}

	public void setStockCountType(String stockCountType) {
		this.stockCountType = stockCountType;
	}
	
	@Length(min=0, max=20, message="物品ID长度必须介于 0 和 20 之间")
	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	
	@Length(min=0, max=50, message="物品名称长度必须介于 0 和 50 之间")
	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	
	public Long getStockCountNum() {
		return stockCountNum;
	}

	public void setStockCountNum(Long stockCountNum) {
		this.stockCountNum = stockCountNum;
	}
	
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}
	
	@Length(min=0, max=64, message="审批人长度必须介于 0 和 64 之间")
	public String getManagerUserid() {
		return managerUserid;
	}

	public void setManagerUserid(String managerUserid) {
		this.managerUserid = managerUserid;
	}
	
	@Length(min=0, max=64, message="审批人姓名长度必须介于 0 和 64 之间")
	public String getManagerUsername() {
		return managerUsername;
	}

	public void setManagerUsername(String managerUsername) {
		this.managerUsername = managerUsername;
	}
	
	@Length(min=0, max=64, message="盘点人姓名长度必须介于 0 和 64 之间")
	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}
	
	@Length(min=0, max=64, message="库存更新人姓名长度必须介于 0 和 64 之间")
	public String getUpdateName() {
		return updateName;
	}

	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}

	public Date getCreateTimeStart() {
		return createTimeStart;
	}

	public void setCreateTimeStart(Date createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	public Date getCreateTimeEnd() {
		return createTimeEnd;
	}

	public void setCreateTimeEnd(Date createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	public String getGoodsTypes() {
		return goodsTypes;
	}

	public void setGoodsTypes(String goodsTypes) {
		this.goodsTypes = goodsTypes;
	}

	@Override
	public String toString() {
		return "StoStockCountInfo [stockCountId=" + stockCountId + ", stockCountNo=" + stockCountNo + ", updateFlag="
				+ updateFlag + ", stockCountType=" + stockCountType + ", goodsId=" + goodsId + ", goodsName="
				+ goodsName + ", stockCountNum=" + stockCountNum + ", office=" + office + ", managerUserid="
				+ managerUserid + ", managerUsername=" + managerUsername + ", createName=" + createName
				+ ", updateName=" + updateName + ", goodsTypes=" + goodsTypes + ", createTimeStart=" + createTimeStart
				+ ", createTimeEnd=" + createTimeEnd + "]";
	}
	
}