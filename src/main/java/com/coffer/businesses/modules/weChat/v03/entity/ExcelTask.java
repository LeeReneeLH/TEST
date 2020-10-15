package com.coffer.businesses.modules.weChat.v03.entity;

import java.util.Date;
import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;

/**
 * 任务Entity(excel出力用)	暂时做成于此  王林   
 * 
 * @author Clark
 * @version 2015-05-05
 */
public class ExcelTask extends DataEntity<ExcelTask> {

	private static final long serialVersionUID = 1L;

	
	private String numcode; 		// 序号
	private String doorname; 		// 门店名称
	private String doorid; 			// 门店编号
	private String amount; 			// 收款金额
	private String registername; 	// 登记人
	private String registerdate; 	// 登记时间
	private String orderid; 		// 预约单号
	private String packnum; 		// 包号
	
//	private Date loginDate; // 最后登陆日期

	
	public ExcelTask() {
		super();
	}

	public ExcelTask(String id) {
		super(id);
	}

	public String getId() {
		return id;
	}


	@ExcelField(title = "门店名称", align = 1, sort = 20)
	public String getDoorname() {
		return doorname;
	}

	public void setDoorname(String doorname) {
		this.doorname = doorname;
	}

	public Date getCreateDate() {
		return createDate;
	}
	
	@ExcelField(title = "序号", align = 2, sort = 10)
	public String getNumcode() {
		return numcode;
	}

	public void setNumcode(String numcode) {
		this.numcode = numcode;
	}
	
	@ExcelField(title = "门店编号", align = 1, sort = 30)
	public String getDoorid() {
		return doorid;
	}

	public void setDoorid(String doorid) {
		this.doorid = doorid;
	}

	@ExcelField(title = "收款金额", align = 3, sort = 40)
	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	@ExcelField(title = "登记人", align = 1, sort = 50)
	public String getRegistername() {
		return registername;
	}

	public void setRegistername(String registername) {
		this.registername = registername;
	}
	
	@ExcelField(title = "登记时间", align = 1, sort = 60)
	public String getRegisterdate() {
		return registerdate;
	}

	public void setRegisterdate(String registerdate) {
		this.registerdate = registerdate;
	}

	@ExcelField(title = "预约单号", align = 1, sort = 70)
	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	
	@ExcelField(title = "包号", align = 1, sort = 35)
	public String getPacknum() {
		return packnum;
	}

	public void setPacknum(String packnum) {
		this.packnum = packnum;
	}


}