package com.coffer.businesses.modules.weChat.v03.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 预约明细Entity
 * 
 * @author Iceman
 * @version 2017-02-13
 */
public class DoorOrderDetail extends DataEntity<DoorOrderDetail> {

	private static final long serialVersionUID = 1L;
	private String detailId; // 明细ID
	private String orderId; // 预约ID
	private String amount; // 每笔金额
	private String rfid; // 箱袋编号
	private String orderDate; // 预约日期
    private String businessType; // 业务类型
	private BigDecimal paperAmount;
	private int paperCount;
	private BigDecimal coinAmount;
	private int coinCount;
	private BigDecimal otherAmount;
	private BigDecimal forceAmount;

	/** 凭条 修改人：XL 日期：2019-06-26 */
	private String tickertape;
	/** 凭条照片 修改人：XL 日期：2019-08-15 */
	private byte[] photo;

	/**开始时间、结束时间、耗时 字段添加 gzd 2020-01-06 交易报表功能移植 2020-05-27 start*/
	private Date startTime;		//开始时间
	private Date endTime;		//结束时间
	private String costTime;	//耗时
	
	/** 凭条信息明细  修改人：ZXK 日期：2020-06-03 */
	private List<DoorOrderAmount> amountList;
	
	
	public List<DoorOrderAmount> getAmountList() {
		return amountList;
	}

	public void setAmountList(List<DoorOrderAmount> amountList) {
		this.amountList = amountList;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getCostTime() {
		return costTime;
	}

	public void setCostTime(String costTime) {
		this.costTime = costTime;
	}
	/**开始时间、结束时间、耗时 字段添加 gzd 2020-01-06 交易报表功能移植 2020-05-27 end*/

	
	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	public DoorOrderDetail() {
		super();
	}

	public DoorOrderDetail(String id) {
		super(id);
	}

	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getTickertape() {
		return tickertape;
	}

	public void setTickertape(String tickertape) {
		this.tickertape = tickertape;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

	public BigDecimal getPaperAmount() {
		return paperAmount;
	}

	public void setPaperAmount(BigDecimal paperAmount) {
		this.paperAmount = paperAmount;
	}

	public int getPaperCount() {
		return paperCount;
	}

	public void setPaperCount(int paperCount) {
		this.paperCount = paperCount;
	}

	public BigDecimal getCoinAmount() {
		return coinAmount;
	}

	public void setCoinAmount(BigDecimal coinAmount) {
		this.coinAmount = coinAmount;
	}

	public int getCoinCount() {
		return coinCount;
	}

	public void setCoinCount(int coinCount) {
		this.coinCount = coinCount;
	}

	public BigDecimal getOtherAmount() {
		return otherAmount;
	}

	public void setOtherAmount(BigDecimal otherAmount) {
		this.otherAmount = otherAmount;
	}

	public BigDecimal getForceAmount() {
		return forceAmount;
	}

	public void setForceAmount(BigDecimal forceAmount) {
		this.forceAmount = forceAmount;
	}
}