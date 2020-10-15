/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.coffer.businesses.modules.store.v01.entity;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.Length;

import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 重空管理Entity
 * @author LLF
 * @version 2015-12-11
 */
public class StoEmptyDocument extends DataEntity<StoEmptyDocument> {
	
	private static final long serialVersionUID = 1L;
	private String documentId;		// document_id
	private String goodsId;		// goods_id
	private String goodsName;		// goods_name
	private String documentType;		// document_type
	private BigDecimal startNumber;		// start_number
	private BigDecimal endNumber;		// end_number
	private BigDecimal createNumber;		// create_number
	private BigDecimal balanceNumber;		// balance_number
	private String time;		// time
	
	private Office office;
	private StoGoods stoGoods;
	/** 重空属性 */
	private StoBlankBillSelect stoBlankBillSelect;
	
	public StoEmptyDocument() {
		super();
	}

	public StoEmptyDocument(String id){
		super(id);
		this.documentId = id;
	}
	
	@Override
	public void preInsert() {
		super.preInsert();
		setId(BusinessUtils.getNewBusinessNo(Global.getConfig("businessType.store.blank.bill"), UserUtils.getUser()
				.getOffice()));
	}

	@Override
	public String getId() {
		return documentId;
	}

	@Override
	public void setId(String documentId) {
		this.documentId = documentId;
	}
	
	@Length(min=0, max=64, message="goods_id长度必须介于 0 和 64 之间")
	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	
	@Length(min=0, max=3, message="document_type长度必须介于 0 和 3之间")
	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	
	public BigDecimal getStartNumber() {
		return startNumber;
	}

	public void setStartNumber(BigDecimal startNumber) {
		this.startNumber = startNumber;
	}
	
	public BigDecimal getEndNumber() {
		return endNumber;
	}

	public void setEndNumber(BigDecimal endNumber) {
		this.endNumber = endNumber;
	}
	
	public BigDecimal getCreateNumber() {
		return createNumber;
	}

	public void setCreateNumber(BigDecimal createNumber) {
		this.createNumber = createNumber;
	}
	
	@ExcelField(title="数量")
	public BigDecimal getBalanceNumber() {
		return balanceNumber;
	}

	public void setBalanceNumber(BigDecimal balanceNumber) {
		this.balanceNumber = balanceNumber;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}
	
	@ExcelField(title="重空名称")
	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	
	@ExcelField(title="日期")
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public StoBlankBillSelect getStoBlankBillSelect() {
		return stoBlankBillSelect;
	}

	public void setStoBlankBillSelect(StoBlankBillSelect stoBlankBillSelect) {
		this.stoBlankBillSelect = stoBlankBillSelect;
	}

	public StoGoods getStoGoods() {
		return stoGoods;
	}

	public void setStoGoods(StoGoods stoGoods) {
		this.stoGoods = stoGoods;
	}

	@Override
	public String toString() {
		return "StoEmptyDocument [documentId=" + documentId + ", goodsId=" + goodsId + ", documentType=" + documentType
				+ ", startNumber=" + startNumber + ", endNumber=" + endNumber + ", createNumber=" + createNumber
				+ ", balanceNumber=" + balanceNumber + "]";
	}
	
}