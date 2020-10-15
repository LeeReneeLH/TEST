package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;

/**
 * 财务日结(门店)导出账户相关信息Entity
 * 
 * @author ZXK
 * @version 2019年12月16日
 */
public class DayReportAccountExport extends DataEntity<DayReportAccountExport> {

	private static final long serialVersionUID = 1L;

	@ExcelField(title = "*序号", align = 2)
	private String number;// 序号
	@ExcelField(title = "*付款方客户账号", align = 2)
	private String payerAccountId;// 付款方客户账号
	@ExcelField(title = "*付款方账户名称", align = 2)
	private String payerAccountName;// 付款方账户名称
	@ExcelField(title = "*收款方客户账号", align = 2)
	private String payeeAccountId;// 收款方客户账号
	@ExcelField(title = "*收款方账户名称", align = 2)
	private String payeeAccountName;// 收款方账户名称
	@ExcelField(title = "收款方开户行名称", align = 2)
	private String payeeBankName;// 收款方开户行名称
	@ExcelField(title = "*收款方行别代码(01-本行，02-国内他行)", align = 2)
	private String payeeBankCode;// 收款方行别代码(01-本行 , 02-国内他行)
	@ExcelField(title = "收款方联行号", align = 2)
	private String payeeBankRelevance;// 收款方联行号
	@ExcelField(title = "客户方流水号", align = 2)
	private String clientSerialNo;// 客户方流水号
	@ExcelField(title = "*金额", align = 2)
	private BigDecimal amount;// 金额
	@ExcelField(title = "*用途", align = 2)
	private String use;// 用途
	@ExcelField(title = "备注", align = 2)
	private String remark;// 备注(七位码)
	@ExcelField(title = "是否短信通知收款人(0-不通知，1-通知，默认为0-不通知)", align = 2)
	private String shortNote;// 是否短信通知收款人(0-不通知，1-通知，默认为0-不通知。)
	@ExcelField(title = "收款人手机号码", align = 2)
	private String payeePhone;// 收款人手机号码
	@ExcelField(title = "短信通知附加信息", align = 2)
	private String shortNoteInfo;// 短信通知附加信息
	/** add by ZXK 2020-07-31 */
	@ExcelField(title = "日期", align = 2)
	private String sevenDate; // 七位码日期
	
	public String getSevenDate() {
		return sevenDate;
	}

	public void setSevenDate(String sevenDate) {
		this.sevenDate = sevenDate;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPayerAccountId() {
		return payerAccountId;
	}

	public void setPayerAccountId(String payerAccountId) {
		this.payerAccountId = payerAccountId;
	}

	public String getPayerAccountName() {
		return payerAccountName;
	}

	public void setPayerAccountName(String payerAccountName) {
		this.payerAccountName = payerAccountName;
	}

	public String getPayeeAccountId() {
		return payeeAccountId;
	}

	public void setPayeeAccountId(String payeeAccountId) {
		this.payeeAccountId = payeeAccountId;
	}

	public String getPayeeAccountName() {
		return payeeAccountName;
	}

	public void setPayeeAccountName(String payeeAccountName) {
		this.payeeAccountName = payeeAccountName;
	}

	public String getPayeeBankName() {
		return payeeBankName;
	}

	public void setPayeeBankName(String payeeBankName) {
		this.payeeBankName = payeeBankName;
	}

	public String getPayeeBankCode() {
		return payeeBankCode;
	}

	public void setPayeeBankCode(String payeeBankCode) {
		this.payeeBankCode = payeeBankCode;
	}

	public String getPayeeBankRelevance() {
		return payeeBankRelevance;
	}

	public void setPayeeBankRelevance(String payeeBankRelevance) {
		this.payeeBankRelevance = payeeBankRelevance;
	}

	public String getClientSerialNo() {
		return clientSerialNo;
	}

	public void setClientSerialNo(String clientSerialNo) {
		this.clientSerialNo = clientSerialNo;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getUse() {
		return use;
	}

	public void setUse(String use) {
		this.use = use;
	}

	public String getShortNote() {
		return shortNote;
	}

	public void setShortNote(String shortNote) {
		this.shortNote = shortNote;
	}

	public String getPayeePhone() {
		return payeePhone;
	}

	public void setPayeePhone(String payeePhone) {
		this.payeePhone = payeePhone;
	}

	public String getShortNoteInfo() {
		return shortNoteInfo;
	}

	public void setShortNoteInfo(String shortNoteInfo) {
		this.shortNoteInfo = shortNoteInfo;
	}

}
