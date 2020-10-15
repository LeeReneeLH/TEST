package com.coffer.businesses.modules.common.entity;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 业务流水号Entity
 * @author Clark
 * @version 2015-04-28
 */
public class SerialNumber extends DataEntity<SerialNumber> {
	
	private static final long serialVersionUID = 1L;
	private String sequenceDate;	// 日期
	private String businessType;	// 类型
	private Integer sequence;		// 流水号
	
	public SerialNumber() {
		super();
	}

	public SerialNumber(String id){
		super(id);
	}

	public String getSequenceDate() {
		return sequenceDate;
	}

	public void setSequenceDate(String sequenceDate) {
		this.sequenceDate = sequenceDate;
	}
	
	@Length(min=0, max=2, message="类型长度必须介于 0 和 2 之间")
	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	
	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	
}