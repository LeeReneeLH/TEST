/**
 * @author WangBaozhong
 * @version 2017年5月31日
 * 
 * 
 */
package com.coffer.core.modules.sys.entity;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 获取登陆用户ID Entity
 * @author WangBaozhong
 *
 */
public class LoginIdSerialNumber extends DataEntity<LoginIdSerialNumber> {

	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * loginId序列
	 */
	private Integer sequence;
	
	/**
	 * 所属机构ID
	 */
	private String officeId;

	/**
	 * @return sequence
	 */
	public Integer getSequence() {
		return sequence;
	}

	/**
	 * @param sequence 要设置的 sequence
	 */
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return officeId
	 */
	public String getOfficeId() {
		return officeId;
	}

	/**
	 * @param officeId 要设置的 officeId
	 */
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
}
