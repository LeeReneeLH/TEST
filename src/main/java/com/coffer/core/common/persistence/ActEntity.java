/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.coffer.core.common.persistence;

import java.io.Serializable;

import com.coffer.core.modules.act.entity.Act;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Activiti Entity类
 * @author ThinkGem
 * @version 2013-05-28
 */
public abstract class ActEntity<T> extends DataEntity<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Act act; 		// 流程任务对象

	public ActEntity() {
		super();
	}
	
	public ActEntity(String id) {
		super(id);
	}
	
	@JsonIgnore
	public Act getAct() {
		if (act == null){
			act = new Act();
		}
		return act;
	}

	public void setAct(Act act) {
		this.act = act;
	}

	/**
	 * 获取流程实例ID
	 * @return
	 */
	public String getProcInsId() {
		return this.getAct().getProcInsId();
	}

	/**
	 * 设置流程实例ID
	 * @param procInsId
	 */
	public void setProcInsId(String procInsId) {
		this.getAct().setProcInsId(procInsId);
	}
	
	/**
	 * 
	 * Title: getProcDefId
	 * <p>Description: 获取流程定义ID</p>
	 * @author:     wangbaozhong
	 * @return 
	 * String    返回类型
	 */
	public String getProcDefId() {
		return this.getAct().getProcDefId();
	}
	/**
	 * 
	 * Title: setProcDefId
	 * <p>Description: 设置流程定义ID</p>
	 * @author:     wangbaozhong
	 * @param procDefId 
	 * void    返回类型
	 */
	public void setProcDefId(String procDefId) {
		this.getAct().setProcDefId(procDefId);
	}
}
