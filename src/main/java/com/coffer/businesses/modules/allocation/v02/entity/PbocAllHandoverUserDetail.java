/**
 * @author WangBaozhong
 * @version 2016年5月30日
 * 
 * 
 */
package com.coffer.businesses.modules.allocation.v02.entity;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 人行交接人员明细Entity
 * @author WangBaozhong
 *
 */
public class PbocAllHandoverUserDetail extends DataEntity<PbocAllHandoverUserDetail>{

	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 1L;
	/** 交接ID **/
	private String handoverId;

	/**人员类型（1：移交人；2：接收人；3：押运人; 4：授权人）**/
	private String type;
	/** 人员ID **/
	private String escortId;
	
	/** 人员名称 **/
	private String escortName;
	
	/** 人员签收方式（1：指纹；2：身份证; 3：RFID; 4:密码；5：签名） **/
	private String handType;
	/**人员签名图片	**/
	private byte[] escortSignPic;
	/** 人员签名标记(0：无签名，1：有签名) **/
	private String escortSignFlg;

	/**出入库类型 0：出库；1：入库**/
	private String inoutType;
	/**
	 * @return handoverId
	 */
	public String getHandoverId() {
		return handoverId;
	}
	/**
	 * @param handoverId 要设置的 handoverId
	 */
	public void setHandoverId(String handoverId) {
		this.handoverId = handoverId;
	}
	/**
	 * @return type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type 要设置的 type
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return escortId
	 */
	public String getEscortId() {
		return escortId;
	}
	/**
	 * @param escortId 要设置的 escortId
	 */
	public void setEscortId(String escortId) {
		this.escortId = escortId;
	}
	/**
	 * @return escortName
	 */
	public String getEscortName() {
		return escortName;
	}
	/**
	 * @param escortName 要设置的 escortName
	 */
	public void setEscortName(String escortName) {
		this.escortName = escortName;
	}
	/**
	 * @return handType
	 */
	public String getHandType() {
		return handType;
	}
	/**
	 * @param handType 要设置的 handType
	 */
	public void setHandType(String handType) {
		this.handType = handType;
	}
	/**
	 * @return escortSignPic
	 */
	public byte[] getEscortSignPic() {
		return escortSignPic;
	}
	/**
	 * @param escortSignPic 要设置的 escortSignPic
	 */
	public void setEscortSignPic(byte[] escortSignPic) {
		this.escortSignPic = escortSignPic;
	}

    /**
     * @return escortSignFlg
     */
    public String getEscortSignFlg() {
        return escortSignFlg;
    }

    /**
     * @param escortSignPic 要设置的 escortSignFlg
     */
    public void setEscortSignFlg(String escortSignFlg) {
        this.escortSignFlg = escortSignFlg;
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
}
