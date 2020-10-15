package com.coffer.businesses.modules.store.v01.entity;

import java.util.Date;
import java.util.List;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;
import com.google.common.collect.Lists;

/**
 * RFID绑定面值Entity
 * 
 * @author chengshu
 * @version 2016-05-30
 */
public class StoRfidDenomination extends DataEntity<StoRfidDenomination>  {
    private static final long serialVersionUID = 1L;

    private String rfid;

    private String boxStatus;

    private String officeId;
    
    private Office office;

    private String denomination;

    private String createName;

    private String updateName;
    
    private String goodsId;
    
    private String useFlag;
    
    private String value;
    /**当前RFID所在机构ID**/
    private String atOfficeId;
    /**当前RFID所在机构名称**/
    private String atOfficeName;
    /** 替换后RFID **/
    private String destRfid;
    
	/** 表外部字段 **/
    private String reBindingFlag;
    
    private String userId;
    
    private String userName;
    
    private String inoutType;
    
    /** 所属金库 **/
    private Office storeOffice;

    /** 库区名称 **/
    private String areaName;

    private List<String> rfidList = Lists.newArrayList();
   
    /** 错误标识 **/
    private String errorFlag;
    /** 业务类型**/
	private String businessType;
	
	/** 物品名称 **/
	private String goodsName;
	
	/** 流水单号 **/
	private String allId;
	
	/** 机构名称 **/
	private String officeName;
	
	
	/**
	 * 库区类型 
	 * 01:流通券库  02：原封券库 03：残损未复点券库 04：残损已复点券库
	 */
	private String storeAreaType;
	
	/** 创建时间：开始时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;
	
	/** 页面对应的开发时间和结束时间（查询用） **/
    private String searchDateStart;
    private String searchDateEnd;
	
	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	
	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	
    /**
	 * @return businessType
	 */
	public String getBusinessType() {
		return businessType;
	}

	/**
	 * @param businessType 要设置的 businessType
	 */
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid == null ? null : rfid.trim();
    }

    public String getBoxStatus() {
        return boxStatus;
    }

    public void setBoxStatus(String boxStatus) {
        this.boxStatus = boxStatus == null ? null : boxStatus.trim();
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId == null ? null : officeId.trim();
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination == null ? null : denomination.trim();
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName == null ? null : createName.trim();
    }

    public String getUpdateName() {
        return updateName;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName == null ? null : updateName.trim();
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag == null ? null : delFlag.trim();
    }
    
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getReBindingFlag() {
        return reBindingFlag;
    }

    public void setReBindingFlag(String reBindingFlag) {
        this.reBindingFlag = reBindingFlag;
    }

    public List<String> getRfidList() {
        return rfidList;
    }

    public void setRfidList(List<String> rfidList) {
        this.rfidList = rfidList;
    }

    public String getInoutType() {
        return inoutType;
    }

    public void setInoutType(String inoutType) {
        this.inoutType = inoutType;
    }

    public Office getStoreOffice() {
        return storeOffice;
    }

    public void setStoreOffice(Office storeOffice) {
        this.storeOffice = storeOffice;
    }

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getErrorFlag() {
        return errorFlag;
    }

    public void setErrorFlag(String errorFlag) {
        this.errorFlag = errorFlag;
    }

    public String getUseFlag() {
        return useFlag;
    }

    public void setUseFlag(String useFlag) {
        this.useFlag = useFlag;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

	/**
	 * @return the storeAreaType
	 */
	public String getStoreAreaType() {
		return storeAreaType;
	}

	/**
	 * @param storeAreaType the storeAreaType to set
	 */
	public void setStoreAreaType(String storeAreaType) {
		this.storeAreaType = storeAreaType;
	}
	
	/**
	 * @return allId
	 */
	public String getAllId() {
		return allId;
	}

	/**
	 * @param allId 要设置的 allId
	 */
	public void setAllId(String allId) {
		this.allId = allId;
	}
	
	 
    public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the atOfficeId
	 */
	public String getAtOfficeId() {
		return atOfficeId;
	}

	/**
	 * @param atOfficeId the atOfficeId to set
	 */
	public void setAtOfficeId(String atOfficeId) {
		this.atOfficeId = atOfficeId;
	}

	/**
	 * @return the atOfficeName
	 */
	public String getAtOfficeName() {
		return atOfficeName;
	}

	/**
	 * @param atOfficeName the atOfficeName to set
	 */
	public void setAtOfficeName(String atOfficeName) {
		this.atOfficeName = atOfficeName;
	}

	/**
	 * @return the destRfid
	 */
	public String getDestRfid() {
		return destRfid;
	}

	/**
	 * @param destRfid the destRfid to set
	 */
	public void setDestRfid(String destRfid) {
		this.destRfid = destRfid;
	}

	/**
	 * @return createTimeStart
	 */
	public Date getCreateTimeStart() {
		return createTimeStart;
	}

	/**
	 * @param createTimeStart 要设置的 createTimeStart
	 */
	public void setCreateTimeStart(Date createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	/**
	 * @return createTimeEnd
	 */
	public Date getCreateTimeEnd() {
		return createTimeEnd;
	}

	/**
	 * @param createTimeEnd 要设置的 createTimeEnd
	 */
	public void setCreateTimeEnd(Date createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	/**
	 * @return searchDateStart
	 */
	public String getSearchDateStart() {
		return searchDateStart;
	}

	/**
	 * @param searchDateStart 要设置的 searchDateStart
	 */
	public void setSearchDateStart(String searchDateStart) {
		this.searchDateStart = searchDateStart;
	}

	/**
	 * @return searchDateEnd
	 */
	public String getSearchDateEnd() {
		return searchDateEnd;
	}

	/**
	 * @param searchDateEnd 要设置的 searchDateEnd
	 */
	public void setSearchDateEnd(String searchDateEnd) {
		this.searchDateEnd = searchDateEnd;
	}
}