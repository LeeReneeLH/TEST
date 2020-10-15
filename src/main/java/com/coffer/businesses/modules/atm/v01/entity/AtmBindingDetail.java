package com.coffer.businesses.modules.atm.v01.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 绑定信息明细Entity
 * @author Lmeon
 * @version 2015-06-03
 */
public class AtmBindingDetail extends DataEntity<AtmBindingDetail> {
	
	private static final long serialVersionUID = 1L;
	private String detailId;		// 主键
	private String bindingId;		// 主表主键
	private Long idObj;		// 冠字号关联表主键ID
	private String boxNo;		// 钞箱编号
	
	private String atmNo;// ATM机编号
	private String modName;// 钞箱类型名
	private BigDecimal addAmount;// 加钞金额
	private BigDecimal clearAmount;// 清点金额
	private Date addDate;// 加钞时间
	private Date clearDate;// 清点时间

	public AtmBindingDetail() {
		super();
	}

	public AtmBindingDetail(String id){
		super(id);
	}

	@Length(min=1, max=64, message="主键长度必须介于 1 和 64 之间")
	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}
	
	@Length(min=0, max=64, message="主表主键长度必须介于 0 和 64 之间")
	public String getBindingId() {
		return bindingId;
	}

	public void setBindingId(String bindingId) {
		this.bindingId = bindingId;
	}
	
	@NotNull(message="冠字号关联表主键ID不能为空")
	public Long getIdObj() {
		return idObj;
	}

	public void setIdObj(Long idObj) {
		this.idObj = idObj;
	}
	
	@Length(min=1, max=10, message="钞箱编号长度必须介于 1 和 10 之间")
	public String getBoxNo() {
		return boxNo;
	}

	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}
	
	@Length(min=1, max=50, message="创建人名称长度必须介于 1 和 50 之间")
	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}
	
	@Length(min=1, max=50, message="更新人名称长度必须介于 1 和 50 之间")
	public String getUpdateName() {
		return updateName;
	}

	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}

	public String getAtmNo() {
		return atmNo;
	}

	public void setAtmNo(String atmNo) {
		this.atmNo = atmNo;
	}

	public String getModName() {
		return modName;
	}

	public void setModName(String modName) {
		this.modName = modName;
	}

	public BigDecimal getAddAmount() {
		return addAmount;
	}

	public void setAddAmount(BigDecimal addAmount) {
		this.addAmount = addAmount;
	}

	public BigDecimal getClearAmount() {
		return clearAmount;
	}

	public void setClearAmount(BigDecimal clearAmount) {
		this.clearAmount = clearAmount;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public Date getClearDate() {
		return clearDate;
	}

	public void setClearDate(Date clearDate) {
		this.clearDate = clearDate;
	}
	
}