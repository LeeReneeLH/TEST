package com.coffer.businesses.modules.store.v02.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 原封新券管理Entity
 * @author LLF
 * @version 2016-05-30
 */
public class StoOriginalBanknote extends DataEntity<StoOriginalBanknote> {
	
	private static final long serialVersionUID = 1L;
	private String boxNo;		// 原封箱号
	private String outId;		// 出库单号
	private String inId;		// 入库单号
	private String sets;		// 套别
	private String originalTranslate;		// 原封券翻译
	private String denomination;		// 面值
	private Long amount;		// 金额
	private Office coffice;		// 出库机构
	private User outBy;		// 出库操作人
	private String outName;		// 出库操作人名字
	private Date outDate;		// 出库操作时间
	private Office hoffice;		// 回收机构
	private User recoverBy;		// 回收操作人
	private String recoverName;		// 回收操作人名字
	private Date recoverDate;		// 回收时间
	private Office roffice;		// 入库机构
	private String recoverStatus;		// 回收状态0：未回收；1：已回收（出库后此状态有效）
	/** 物品ID **/
	private String goodsId;
	/** 物品名称 **/
	private String goodsName;
	// 出库列表查询使用
	private String cofficeName; // 出库机构名称
	private BigDecimal totalAmount; // 出库合计金额
	
	public StoOriginalBanknote() {
		super();
	}

	public StoOriginalBanknote(String id){
		super(id);
	}

	public String getId() {
		return boxNo;
	}

	public void setId(String boxNo) {
		this.boxNo = boxNo;
	}
	
	@Length(min=0, max=64, message="出库单号长度必须介于 0 和 64 之间")
	public String getOutId() {
		return outId;
	}

	public void setOutId(String outId) {
		this.outId = outId;
	}
	
	@Length(min=0, max=2, message="套别长度必须介于 0 和 2 之间")
	public String getSets() {
		return sets;
	}

	public void setSets(String sets) {
		this.sets = sets;
	}
	
	@Length(min=0, max=300, message="原封券翻译长度必须介于 0 和 300 之间")
	public String getOriginalTranslate() {
		return originalTranslate;
	}

	public void setOriginalTranslate(String originalTranslate) {
		this.originalTranslate = originalTranslate;
	}
	
	@Length(min=0, max=3, message="面值长度必须介于 0 和 3之间")
	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}
	
	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}
	
	public Office getCoffice() {
		return coffice;
	}

	public void setCoffice(Office coffice) {
		this.coffice = coffice;
	}
	
	public User getOutBy() {
		return outBy;
	}

	public void setOutBy(User outBy) {
		this.outBy = outBy;
	}
	
	@Length(min=0, max=200, message="出库操作人名字长度必须介于 0 和 200 之间")
	public String getOutName() {
		return outName;
	}

	public void setOutName(String outName) {
		this.outName = outName;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getOutDate() {
		return outDate;
	}

	public void setOutDate(Date outDate) {
		this.outDate = outDate;
	}
	
	public Office getHoffice() {
		return hoffice;
	}

	public void setHoffice(Office hoffice) {
		this.hoffice = hoffice;
	}
	
	public User getRecoverBy() {
		return recoverBy;
	}

	public void setRecoverBy(User recoverBy) {
		this.recoverBy = recoverBy;
	}
	
	@Length(min=0, max=200, message="回收操作人名字长度必须介于 0 和 200 之间")
	public String getRecoverName() {
		return recoverName;
	}

	public void setRecoverName(String recoverName) {
		this.recoverName = recoverName;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getRecoverDate() {
		return recoverDate;
	}

	public void setRecoverDate(Date recoverDate) {
		this.recoverDate = recoverDate;
	}
	
	public Office getRoffice() {
		return roffice;
	}

	public void setRoffice(Office roffice) {
		this.roffice = roffice;
	}
	
	@Length(min=0, max=1, message="回收状态0：未回收；1：已回收（出库后此状态有效）长度必须介于 0 和 1 之间")
	public String getRecoverStatus() {
		return recoverStatus;
	}

	public void setRecoverStatus(String recoverStatus) {
		this.recoverStatus = recoverStatus;
	}

	public String getCofficeName() {
		return cofficeName;
	}

	public void setCofficeName(String cofficeName) {
		this.cofficeName = cofficeName;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Override
	public String toString() {
		return "StoOriginalBanknote [boxNo=" + boxNo + ", outId=" + outId + ", sets=" + sets + ", originalTranslate="
				+ originalTranslate + ", denomination=" + denomination + ", amount=" + amount + ", coffice=" + coffice
				+ ", outBy=" + outBy + ", outName=" + outName + ", outDate=" + outDate + ", hoffice=" + hoffice
				+ ", recoverBy=" + recoverBy + ", recoverName=" + recoverName + ", recoverDate=" + recoverDate
				+ ", roffice=" + roffice + ", recoverStatus=" + recoverStatus + "]";
	}

	/**
	 * @return goodsId
	 */
	public String getGoodsId() {
		return goodsId;
	}

	/**
	 * @param goodsId 要设置的 goodsId
	 */
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	/**
	 * @return inId
	 */
	public String getInId() {
		return inId;
	}

	/**
	 * @param inId 要设置的 inId
	 */
	public void setInId(String inId) {
		this.inId = inId;
	}

	/**
	 * @return goodsName
	 */
	public String getGoodsName() {
		return goodsName;
	}

	/**
	 * @param goodsName 要设置的 goodsName
	 */
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	
}