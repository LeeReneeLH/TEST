package com.coffer.businesses.modules.clear.v03.entity;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;

/**
 * 账务管理明细Entity
 * @author QPH
 * @version 2017-09-04
 */
public class CenterAccountsDetail extends DataEntity<CenterAccountsDetail> {
	
	private static final long serialVersionUID = 1L;
	private String detailId;		// 明细ID
	private String accountsId;		// 主表ID
	/* 添加导出excel的注解 wzj begin */
	@ExcelField(title = "币种", align = 2)
	/* end */
	private String currency;		// 币种
	/* 添加导出excel的注解 wzj begin */
	@ExcelField(title = "面值", align = 2)
	/* end */
	private String denomination;		// 面值
	private String unit;		// 单位
	private BigDecimal totalCount; // 总数量
	/* 添加导出excel的注解 wzj begin */
	@ExcelField(title = "总金额", align = 2)
	/* end */
	private BigDecimal totalAmount; // 总金额
	/* 添加导出excel的注解及编号属性 wzj begin */
	@ExcelField(title = "编号", align = 2)
	private String no;// 编号导出excel
	/* end */
	/* 添加导出excel的注解及总计属性 wzj begin */
	@ExcelField(title = "总计", align = 2)
	private BigDecimal total;// 总计导出excel
	/* end */
	public CenterAccountsDetail() {
		super();
	}

	public CenterAccountsDetail(String id){
		super(id);
	}

	@Length(min=1, max=64, message="明细ID长度必须介于 1 和 64 之间")
	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}
	
	@Length(min=0, max=64, message="主表ID长度必须介于 0 和 64 之间")
	public String getAccountsId() {
		return accountsId;
	}

	public void setAccountsId(String accountsId) {
		this.accountsId = accountsId;
	}
	
	@Length(min=0, max=3, message="币种长度必须介于 0 和 3 之间")
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	@Length(min=0, max=2, message="面值长度必须介于 0 和 2 之间")
	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}
	
	@Length(min=0, max=2, message="单位长度必须介于 0 和 2 之间")
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	public BigDecimal getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(BigDecimal totalCount) {
		this.totalCount = totalCount;
	}
	
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

}