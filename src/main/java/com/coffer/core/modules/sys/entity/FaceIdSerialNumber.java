/**
 * @author WangBaozhong
 * @version 2017年5月31日
 * 
 * 
 */
package com.coffer.core.modules.sys.entity;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 获取脸谱ID Entity
 * @author WangBaozhong
 *
 */
public class FaceIdSerialNumber extends DataEntity<FaceIdSerialNumber> {

	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 所属人行机构ID
	 */
	private String pbocOfficeId;
	/**
	 * 用户所属机构类型
	 */
	private String officeType;
	
	/**
	 * 脸谱序列
	 */
	private Long sequence;
	

	/**
	 * @return pbocOfficeId
	 */
	public String getPbocOfficeId() {
		return pbocOfficeId;
	}

	/**
	 * @param pbocOfficeId 要设置的 pbocOfficeId
	 */
	public void setPbocOfficeId(String pbocOfficeId) {
		this.pbocOfficeId = pbocOfficeId;
	}

	/**
	 * @return officeType
	 */
	public String getOfficeType() {
		return officeType;
	}

	/**
	 * @param officeType 要设置的 officeType
	 */
	public void setOfficeType(String officeType) {
		this.officeType = officeType;
	}

	/**
	 * @return sequence
	 */
	public Long getSequence() {
		return sequence;
	}

	/**
	 * @param sequence 要设置的 sequence
	 */
	public void setSequence(Long sequence) {
		this.sequence = sequence;
	}
}
