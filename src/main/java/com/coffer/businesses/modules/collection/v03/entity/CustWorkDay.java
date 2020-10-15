package com.coffer.businesses.modules.collection.v03.entity;

import java.util.Date;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;

/**
 * 客户清点量(日)统计Entity
 * 
 * @author wanglin
 * @version 2017-09-04
 */
public class CustWorkDay extends DataEntity<CustWorkDay> {

	private static final long serialVersionUID = 1L;
	
	@ExcelField(title = "清分日期", align = 1)
	private String clearDate; 			// 清分日期
	private String custNo; 				// 门店ID
	@ExcelField(title = "门店名称", align = 1)
	private String custName; 			// 门店名称
	@ExcelField(title = "合计单数", align = 3)
	private long sumBillCount;			// 合计单数
	@ExcelField(title = "合计数量", align = 3)
	private long sumNumCount;			// 合计数量（张）
	@ExcelField(title = "合计金额", align = 3)
	private double sumAmount;			// 合计金额
	
	//检索部
	private Date createTimeStart;		// 开始时间
	private Date createTimeEnd;			// 结束时间
	private String searchDateStart;		// 开始时间
	private String searchDateEnd;		// 结束时间
	private String searchCustNo;		// 门店ID
	private String searchCustName;		// 门店

	
	//行明细(面值)
	private String type;				// 类型(人民币)
	private String rowPayValue;			// 面额(行)
	private String rowCount;			// 数量(行)
	private String rowAmount;			// 金额(行)
	
	//行明细(人员)
	private String rowManName;			// 清分人员(行)
	private String rowManCount;			// 笔数(行)
	private String rowManAmount;		// 金额(行)
	
	
	
	//Excel下载
	@ExcelField(title = "100元数量", align = 3)
	private long count100;			// 数量(100元)
	@ExcelField(title = "100元金额", align = 3)
	private double amount100;			// 金额(100元)
	@ExcelField(title = "50元数量", align = 3)
	private long count50;				// 数量(50元)
	@ExcelField(title = "50元金额", align = 3)
	private double amount50;			// 金额(50元)
	@ExcelField(title = "20元数量", align = 3)
	private long count20;				// 数量(20元)
	@ExcelField(title = "20元金额", align = 3)
	private double amount20;			// 金额(20元)
	@ExcelField(title = "10元数量", align = 3)
	private long count10;				// 数量(10元)
	@ExcelField(title = "10元金额", align = 3)
	private double amount10;			// 金额(10元)
	@ExcelField(title = "5元数量", align = 3)
	private long count5;				// 数量(5元)
	@ExcelField(title = "5元金额", align = 3)
	private double amount5;				// 金额(5元)
	@ExcelField(title = "2元数量", align = 3)
	private long count2;				// 数量(2元)
	@ExcelField(title = "2元金额", align = 3)
	private double amount2;				// 金额(2元)
	@ExcelField(title = "1元数量", align = 3)
	private long count1;				// 数量(1元)
	@ExcelField(title = "1元金额", align = 3)
	private double amount1;				// 金额(1元)
	@ExcelField(title = "5角数量", align = 3)
	private long count05;				// 数量(5角)
	@ExcelField(title = "5角金额", align = 3)
	private double amount05;			// 金额(5角)
	@ExcelField(title = "2角数量", align = 3)
	private long count02;				// 数量(2角)
	@ExcelField(title = "2角金额", align = 3)
	private double amount02;			// 金额(2角)
	@ExcelField(title = "1角数量", align = 3)
	private long count01;				// 数量(1角)
	@ExcelField(title = "1角金额", align = 3)
	private double amount01;			// 金额(1角)
	
	public CustWorkDay() {
		super();
	}

	public CustWorkDay(String id) {
		super(id);
	}

	public String getClearDate() {
		return clearDate;
	}

	public void setClearDate(String clearDate) {
		this.clearDate = clearDate;
	}



	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public long getSumBillCount() {
		return sumBillCount;
	}

	public void setSumBillCount(long sumBillCount) {
		this.sumBillCount = sumBillCount;
	}

	public long getSumNumCount() {
		return sumNumCount;
	}

	public void setSumNumCount(long sumNumCount) {
		this.sumNumCount = sumNumCount;
	}

	public double getSumAmount() {
		return sumAmount;
	}

	public void setSumAmount(double sumAmount) {
		this.sumAmount = sumAmount;
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

	public String getSearchDateStart() {
		return searchDateStart;
	}

	public void setSearchDateStart(String searchDateStart) {
		this.searchDateStart = searchDateStart;
	}

	public String getSearchDateEnd() {
		return searchDateEnd;
	}

	public void setSearchDateEnd(String searchDateEnd) {
		this.searchDateEnd = searchDateEnd;
	}



	public String getSearchCustNo() {
		return searchCustNo;
	}

	public void setSearchCustNo(String searchCustNo) {
		this.searchCustNo = searchCustNo;
	}

	public String getSearchCustName() {
		return searchCustName;
	}

	public void setSearchCustName(String searchCustName) {
		this.searchCustName = searchCustName;
	}

	public String getRowPayValue() {
		return rowPayValue;
	}

	public void setRowPayValue(String rowPayValue) {
		this.rowPayValue = rowPayValue;
	}

	public String getRowCount() {
		return rowCount;
	}

	public void setRowCount(String rowCount) {
		this.rowCount = rowCount;
	}

	public String getRowAmount() {
		return rowAmount;
	}

	public void setRowAmount(String rowAmount) {
		this.rowAmount = rowAmount;
	}



	public String getCustNo() {
		return custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getCount100() {
		return count100;
	}

	public void setCount100(long count100) {
		this.count100 = count100;
	}

	public double getAmount100() {
		return amount100;
	}

	public void setAmount100(double amount100) {
		this.amount100 = amount100;
	}

	public long getCount50() {
		return count50;
	}

	public void setCount50(long count50) {
		this.count50 = count50;
	}

	public double getAmount50() {
		return amount50;
	}

	public void setAmount50(double amount50) {
		this.amount50 = amount50;
	}

	public long getCount20() {
		return count20;
	}

	public void setCount20(long count20) {
		this.count20 = count20;
	}

	public double getAmount20() {
		return amount20;
	}

	public void setAmount20(double amount20) {
		this.amount20 = amount20;
	}

	public long getCount10() {
		return count10;
	}

	public void setCount10(long count10) {
		this.count10 = count10;
	}

	public double getAmount10() {
		return amount10;
	}

	public void setAmount10(double amount10) {
		this.amount10 = amount10;
	}

	public long getCount5() {
		return count5;
	}

	public void setCount5(long count5) {
		this.count5 = count5;
	}

	public double getAmount5() {
		return amount5;
	}

	public void setAmount5(double amount5) {
		this.amount5 = amount5;
	}

	public long getCount2() {
		return count2;
	}

	public void setCount2(long count2) {
		this.count2 = count2;
	}

	public double getAmount2() {
		return amount2;
	}

	public void setAmount2(double amount2) {
		this.amount2 = amount2;
	}

	public long getCount1() {
		return count1;
	}

	public void setCount1(long count1) {
		this.count1 = count1;
	}

	public double getAmount1() {
		return amount1;
	}

	public void setAmount1(double amount1) {
		this.amount1 = amount1;
	}

	public long getCount05() {
		return count05;
	}

	public void setCount05(long count05) {
		this.count05 = count05;
	}

	public double getAmount05() {
		return amount05;
	}

	public void setAmount05(double amount05) {
		this.amount05 = amount05;
	}

	public long getCount02() {
		return count02;
	}

	public void setCount02(long count02) {
		this.count02 = count02;
	}

	public double getAmount02() {
		return amount02;
	}

	public void setAmount02(double amount02) {
		this.amount02 = amount02;
	}

	public long getCount01() {
		return count01;
	}

	public void setCount01(long count01) {
		this.count01 = count01;
	}

	public double getAmount01() {
		return amount01;
	}

	public void setAmount01(double amount01) {
		this.amount01 = amount01;
	}

	public String getRowManName() {
		return rowManName;
	}

	public void setRowManName(String rowManName) {
		this.rowManName = rowManName;
	}

	public String getRowManCount() {
		return rowManCount;
	}

	public void setRowManCount(String rowManCount) {
		this.rowManCount = rowManCount;
	}

	public String getRowManAmount() {
		return rowManAmount;
	}

	public void setRowManAmount(String rowManAmount) {
		this.rowManAmount = rowManAmount;
	}





}