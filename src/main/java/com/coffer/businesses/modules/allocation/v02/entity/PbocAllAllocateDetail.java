package com.coffer.businesses.modules.allocation.v02.entity;

import org.hibernate.validator.constraints.Length;

import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.businesses.modules.store.v02.entity.StoOriginalBanknote;
import com.coffer.core.common.persistence.DataEntity;

/**
 * 人行调拨箱袋管理Entity
 * @author LLF
 * @version 2016-05-25
 */
public class PbocAllAllocateDetail extends DataEntity<PbocAllAllocateDetail> {
	
	private static final long serialVersionUID = 1L;
	private String allDetailId;		// 主键
	private String allId;		// 流水单号
	private String boxNo;		// 箱袋编号
	private String scanFlag;		// 扫描状态0：未扫描，1：已扫描，2：补录
	private String place;		// 箱袋状态10:空箱； 11:网点； 12:在途； 13:库房； 14:整点室； 15:票据室
	private String boxType;		// 箱袋类型11款箱；12尾箱；13款包；14钞箱等
	private Double amount;		// 登记金额
	private String routeId;		// 线路ID
	private String rfid;		// rfid
	private String status;		// 0：正常；1：强制
	/** 出入库类型0：出库；1：入库 **/
	private String inoutType;
	
	private StoGoodsLocationInfo goodsLocationInfo; // 库区物品摆放信息
	
	private StoOriginalBanknote stoOriginalBanknote; //原封箱信息
	
	public PbocAllAllocateDetail() {
		super();
	}

	public PbocAllAllocateDetail(String id){
		super(id);
	}

	public String getId() {
		return allDetailId;
	}

	public void setId(String allDetailId) {
		this.allDetailId = allDetailId;
	}
	
	@Length(min=1, max=25, message="流水单号长度必须介于 1 和 25 之间")
	public String getAllId() {
		return allId;
	}

	public void setAllId(String allId) {
		this.allId = allId;
	}
	
	@Length(min=0, max=10, message="箱袋编号长度必须介于 0 和 10 之间")
	public String getBoxNo() {
		return boxNo;
	}

	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}
	
	@Length(min=0, max=1, message="扫描状态0：未扫描，1：已扫描，2：补录长度必须介于 0 和 1 之间")
	public String getScanFlag() {
		return scanFlag;
	}

	public void setScanFlag(String scanFlag) {
		this.scanFlag = scanFlag;
	}
	
	@Length(min=0, max=2, message="箱袋状态10:空箱； 11:网点； 12:在途； 13:库房； 14:整点室； 15:票据室长度必须介于 0 和 2 之间")
	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}
	
	@Length(min=0, max=2, message="箱袋类型11款箱；12尾箱；13款包；14钞箱等长度必须介于 0 和 2 之间")
	public String getBoxType() {
		return boxType;
	}

	public void setBoxType(String boxType) {
		this.boxType = boxType;
	}
	
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	@Length(min=0, max=64, message="线路ID长度必须介于 0 和 64 之间")
	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
	
	@Length(min=0, max=20, message="rfid长度必须介于 0 和 20 之间")
	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}
	
	@Length(min=0, max=1, message="0：正常；1：强制长度必须介于 0 和 1 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	

    public StoGoodsLocationInfo getGoodsLocationInfo() {
        return goodsLocationInfo;
    }

    public void setGoodsLocationInfo(StoGoodsLocationInfo goodsLocationInfo) {
        this.goodsLocationInfo = goodsLocationInfo;
    }

	/**
	 * @return inoutType
	 */
	public String getInoutType() {
		return inoutType;
	}

	/**
	 * @param inoutType 要设置的 inoutType
	 */
	public void setInoutType(String inoutType) {
		this.inoutType = inoutType;
	}

	/**
	 * @return stoOriginalBanknote
	 */
	public StoOriginalBanknote getStoOriginalBanknote() {
		return stoOriginalBanknote;
	}

	/**
	 * @param stoOriginalBanknote 要设置的 stoOriginalBanknote
	 */
	public void setStoOriginalBanknote(StoOriginalBanknote stoOriginalBanknote) {
		this.stoOriginalBanknote = stoOriginalBanknote;
	}
	
}