package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;

/**
 * 清机加钞记录Entity
 *
 * @author ZXK
 * @version 2019-07-23
 */
public class ClearAddMoney extends DataEntity<ClearAddMoney> {

    private static final long serialVersionUID = 1L;
    private String doorId; //门店编号
    @ExcelField(title = "门店名称", align = 2)
    private String doorName; //门店名称
    private String equipmentId; //机具 编号
    @ExcelField(title = "机具名称", align = 2)
    private String equipmentName; //机具名称
    private String clearCenterId; //清分中心编号
    private String clearCenterName; //清分中心名称
    @ExcelField(title = "款袋编号", align = 2)
    private String BagNo; //款袋编号(旧)
    @ExcelField(title = "累计总张数", align = 2)
    private int count; // 总张数
    @ExcelField(title = "本机金额", align = 2)
    private BigDecimal amount; // 总金额
    private String bagStatus; //款袋状态
    private Date changeDate; //更换时间
    private String changeCode; //更换人编号
    @ExcelField(title = "凭条号", align = 2)
    private String businessId;// 业务流水ID
    /* 增加 类型  余额  修改人：zxk 修改时间：2019-7-30 begin */
    @ExcelField(title = "类型", align = 2)
    private String type; //类型 (0, 存款  1,清机)
    @ExcelField(title = "余额", align = 2)
    private BigDecimal surplusAmount; //余额
    /** 开始时间和结束时间（查询用） */
    private String searchDateStart;
    private String searchDateEnd;
    /** 页面对应的开发时间和结束时间（查询用） */
    private Date createTimeStart;
    private Date createTimeEnd;
    /* end */
  
    /** add:ZXK  2020-6-2 begin*/
    @ExcelField(title = "业务备注", align = 2)
    private String remarks; //业务备注
    @ExcelField(title = "操作人", align = 2)
    private String operator; //操作人
    @ExcelField(title = "操作时间", align = 2)
    private String operationTime; //操作时间
    /** end */

	public String getOperator() {
		return operator;
	}

	public String getOperationTime() {
		return operationTime;
	}

	public void setOperationTime(String operationTime) {
		this.operationTime = operationTime;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getSearchDateStart() {
        return searchDateStart;
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

    public void setSearchDateStart(String searchDateStart) {
        this.searchDateStart = searchDateStart;
    }

    public String getSearchDateEnd() {
        return searchDateEnd;
    }

    public void setSearchDateEnd(String searchDateEnd) {
        this.searchDateEnd = searchDateEnd;
    }


    public String getDoorId() {
        return doorId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getSurplusAmount() {
        return surplusAmount;
    }

    public void setSurplusAmount(BigDecimal surplusAmount) {
        this.surplusAmount = surplusAmount;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public String getChangeCode() {
        return changeCode;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public void setChangeCode(String changeCode) {
        this.changeCode = changeCode;
    }

    public void setDoorId(String doorId) {
        this.doorId = doorId;
    }

    public String getDoorName() {
        return doorName;
    }

    public void setDoorName(String doorName) {
        this.doorName = doorName;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getClearCenterId() {
        return clearCenterId;
    }

    public void setClearCenterId(String clearCenterId) {
        this.clearCenterId = clearCenterId;
    }

    public String getClearCenterName() {
        return clearCenterName;
    }

    public void setClearCenterName(String clearCenterName) {
        this.clearCenterName = clearCenterName;
    }

    public String getBagNo() {
        return BagNo;
    }

    public void setBagNo(String bagNo) {
        BagNo = bagNo;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBagStatus() {
        return bagStatus;
    }

    public void setBagStatus(String bagStatus) {
        this.bagStatus = bagStatus;
    }

}
