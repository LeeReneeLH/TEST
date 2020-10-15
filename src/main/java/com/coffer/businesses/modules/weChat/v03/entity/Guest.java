package com.coffer.businesses.modules.weChat.v03.entity;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;
import com.fasterxml.jackson.annotation.JsonFormat;


/**
 * 客户授权Entity
 *
 * @author qipeihong
 * @version 2017-04-18
 */
public class Guest extends DataEntity<Guest> {

    private static final long serialVersionUID = 1L;
    private String gname;        // 用户名
    private String gphone;        // 手机号
    private String gidcardNo;        // 身份证号
    private String openId;        // 微信号
    private String unionId;     // 开放平台统一ID
    private Date loginDate;        // 申请授权日期
    private String gofficeId;        // 门店id
    private String gofficeName;        // 门店名
    private String grantstatus;        // 授权状态   1：已授权   2：未授权
    private String method;            // 申请方式   1：PC端      2：微信端
    private Date grantDate;        // 授权期限
    private String guestType;    // 用户类型   1：普通用户   2：管理员用户

    //追加授权业务类型  修改人：XL 修改时间：2018-05-17 begin
    private String busType;
    //end
    
    private String userType;// 用户类型
    
    private List<String> equipmentIdList;//机具ID

    public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}


	public List<String> getEquipmentIdList() {
		return equipmentIdList;
	}

	public void setEquipmentIdList(List<String> equipmentIdList) {
		this.equipmentIdList = equipmentIdList;
	}

	public String getGuestType() {
        return guestType;
    }

    public void setGuestType(String guestType) {
        this.guestType = guestType;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "授权日期不能为空")
    public Date getGrantDate() {
        return grantDate;
    }

    public void setGrantDate(Date grantDate) {
        this.grantDate = grantDate;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getGrantstatus() {
        return grantstatus;
    }

    public void setGrantstatus(String grantstatus) {
        this.grantstatus = grantstatus;
    }

    public String getGofficeName() {
        return gofficeName;
    }

    public void setGofficeName(String gofficeName) {
        this.gofficeName = gofficeName;
    }

    public Guest() {
        super();
    }

    public Guest(String id) {
        super(id);
    }

    @Length(min = 0, max = 64, message = "gname长度必须介于 0 和 64 之间")
    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }

    @Length(min = 0, max = 64, message = "gphone长度必须介于 0 和 64 之间")
    public String getGphone() {
        return gphone;
    }

    public void setGphone(String gphone) {
        this.gphone = gphone;
    }

    @Length(min = 0, max = 64, message = "gidcard_no长度必须介于 0 和 64 之间")
    public String getGidcardNo() {
        return gidcardNo;
    }

    public void setGidcardNo(String gidcardNo) {
        this.gidcardNo = gidcardNo;
    }

    @Length(min = 0, max = 100, message = "open_id长度必须介于 0 和 100 之间")
    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "login_date不能为空")
    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    @Length(min = 0, max = 64, message = "goffice_id长度必须介于 0 和 64 之间")
    public String getGofficeId() {
        return gofficeId;
    }

    public void setGofficeId(String gofficeId) {
        this.gofficeId = gofficeId;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }
}