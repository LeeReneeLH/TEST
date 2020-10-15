package com.coffer.businesses.modules.store.v01.entity;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 库存管理Entity
 * @author LLF
 * @version 2015-09-09
 */
public class StoStoresInfo extends DataEntity<StoStoresInfo> {
	
	private static final long serialVersionUID = 1L;
	private String stoId;		// 主键ID
	private String goodsId;		// 物品ID
	private String goodsName;		// 物品名称
	private Long stoNum;		// 库存数量
	private Long surplusStoNum;		// 预剩余库存数量
	private BigDecimal amount;		// 总价值
	private Office office;		// 所属金库
	private String goodsType;		// 库存类型（0：货币；1：贵金属；2：箱袋等）
	private String currency;		// 币种
	private String time;		// 日期
	private String officeN;
	public StoStoresInfo() {
		super();
	}

	public StoStoresInfo(String id){
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
	
	@ExcelField(title = "数量")
	public Long getStoNum() {
		return stoNum;
	}

	public void setStoNum(Long stoNum) {
		this.stoNum = stoNum;
	}
	
	public Long getSurplusStoNum() {
		return surplusStoNum;
	}

	public void setSurplusStoNum(Long surplusStoNum) {
		this.surplusStoNum = surplusStoNum;
	}

	@ExcelField(title = "总金额")
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}
	
	@Length(min=0, max=1, message="库存类型（0：货币；1：贵金属；2：箱袋等）长度必须介于 0 和 1 之间")
	public String getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@ExcelField(title="日期")
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@ExcelField(title = "机构")
	public String getOfficeN() {
		return officeN;
	}

	public void setOfficeN(String officeN) {
		this.officeN = officeN;
	}

}