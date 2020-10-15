package com.coffer.businesses.modules.store.v01.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;

/**
 * 库存流水管理Entity
 * @author LLF
 * @version 2015-09-10
 */
public class StoStoresHistory extends DataEntity<StoStoresHistory> {
	
	private static final long serialVersionUID = 1L;
	private String stoId;		// 主键ID
	private String goodsId;		// 物品ID
	private String goodsName;		// 物品名称
	private Long stoNum;		// 库存数量
	private BigDecimal amount;		// 总价值
	private Long changeNum;		// 库存量更量
	private Office office;		// 所属金库
	private String officeName;		// 机构名称
	private String stoStatus;		// 库存更新状态（00：出库更新；01：入库更新；02：盘点更新；03：兑换更新）
	private String stoStatusName;	// 库存更新状态（文字描述）
	private String goodsType;		// 物品类型（0：货币；1：贵金属；2：箱袋等）
	private String businessId;		// 业务流水ID
	
	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;
	
	/** 日期 **/
	private String time = "";
	/** 创建人 **/
	private String createName = "";
	
	public StoStoresHistory() {
		super();
	}

	public StoStoresHistory(String id){
		super(id);
	}

	@Length(min=1, max=64, message="主键ID长度必须介于 1 和 64 之间")
	public String getStoId() {
		return stoId;
	}

	public void setStoId(String stoId) {
		this.stoId = stoId;
	}
	
	@Length(min=0, max=20, message="物品ID长度必须介于 0 和 20 之间")
	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	
	@Length(min=0, max=50, message="物品名称长度必须介于 0 和 50 之间")
	@ExcelField(title="物品名称")
	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	
	public Long getStoNum() {
		return stoNum;
	}

	public void setStoNum(Long stoNum) {
		this.stoNum = stoNum;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	@ExcelField(title="数量")
	public Long getChangeNum() {
		return changeNum;
	}

	public void setChangeNum(Long changeNum) {
		this.changeNum = changeNum;
	}
	
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}
	
	@Length(min=0, max=2, message="库存更新状态（00：出库更新；01：入库更新；02：盘点更新；03：兑换更新）长度必须介于 0 和 2 之间")
	public String getStoStatus() {
		return stoStatus;
	}

	public void setStoStatus(String stoStatus) {
		this.stoStatus = stoStatus;
	}
	
	@Length(min=0, max=1, message="物品类型（0：货币；1：贵金属；2：箱袋等）长度必须介于 0 和 1 之间")
	public String getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	}
	
	@Length(min=0, max=64, message="业务流水ID长度必须介于 0 和 64 之间")
	@ExcelField(title="业务ID")
	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
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

	@ExcelField(title="日期")
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@ExcelField(title="机构名称")
	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	@ExcelField(title="创建人")
	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	@ExcelField(title="变更类型")
	public String getStoStatusName() {
		return stoStatusName;
	}

	public void setStoStatusName(String stoStatusName) {
		this.stoStatusName = stoStatusName;
	}
}