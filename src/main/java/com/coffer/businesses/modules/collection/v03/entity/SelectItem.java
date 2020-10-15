
package com.coffer.businesses.modules.collection.v03.entity;

import javax.xml.bind.annotation.XmlAttribute;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;
import com.google.gson.annotations.Expose;

/**
 * select下拉列表Entity
 * @author wl
 * @version 2017-05-15
 */
public class SelectItem extends DataEntity<SelectItem> {

	private static final long serialVersionUID = 1L;
	@Expose
	private String value;	// 数据值
	@Expose
	private String label;	// 标签名

	public SelectItem() {
		super();
	}
	
	public SelectItem(String id){
		super(id);
	}
	
	public SelectItem(String value, String label){
		this.value = value;
		this.label = label;
	}
	
	@XmlAttribute
	@Length(min=1, max=100)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@XmlAttribute
	@Length(min=1, max=100)
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}