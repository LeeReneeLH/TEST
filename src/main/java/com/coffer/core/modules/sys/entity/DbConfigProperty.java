package com.coffer.core.modules.sys.entity;

import java.util.List;

import com.coffer.core.common.persistence.TreeEntity;
import com.coffer.core.modules.sys.DbConfigConstant;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 系统共通项实体类
 * 
 * @author xp
 * @version 2017-12-9
 */
public class DbConfigProperty extends TreeEntity<DbConfigProperty> {
	private static final long serialVersionUID = 1L;
	// 参数的键
	private String propertyKey;
	// 参数的值
	private String propertyValue;
	// 参数的操作类型：update/insert
	private String configType;
	// 备注
	private String remark;
	// 类型
	private String type = DbConfigConstant.Type.GROUP;
	// 父级id
	private String parentId;
	// 参数最新更新时间，一致性校验
	private String strUpdateDate;

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@JsonIgnore
	public static String getRootId() {
		return "1";
	}

	public DbConfigProperty getParent() {
		return (DbConfigProperty) this.parent;
	}

	public void setParent(DbConfigProperty parent) {
		this.parent = parent;
	}

	public String getConfigType() {
		return this.configType;
	}

	public void setConfigType(String configType) {
		this.configType = configType;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getParentId() {
		return this.parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getPropertyKey() {
		return this.propertyKey;
	}

	public void setPropertyKey(String propertyKey) {
		this.propertyKey = propertyKey;
	}

	public String getPropertyValue() {
		return this.propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

	public String getStrUpdateDate() {
		return strUpdateDate;
	}

	public void setStrUpdateDate(String strUpdateDate) {
		this.strUpdateDate = strUpdateDate;
	}

	@JsonIgnore
	public static void sortList(List<DbConfigProperty> list, List<DbConfigProperty> sourcelist, String parentId,
			boolean cascade) {
		for (DbConfigProperty dbConfigProperty : sourcelist) {
			DbConfigProperty property = dbConfigProperty;
			if ((property.getParent() != null) && (property.getParent().getId() != null)
					&& (property.getParent().getId().equals(parentId))) {
				list.add(property);
				if (cascade) {
					for (DbConfigProperty dbProperty : sourcelist) {
						DbConfigProperty child = dbProperty;
						if ((child.getParent() != null) && (child.getParent().getId() != null)
								&& (child.getParent().getId().equals(property.getId()))) {
							sortList(list, sourcelist, property.getId(), true);
							break;
						}
					}
				}
			}
		}
	}



}
