package com.coffer.core.modules.sys.entity;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.coffer.core.common.persistence.DataEntity;
import com.google.common.collect.Lists;

/**
 * 令牌机构管理Entity
 * 
 * @author XL
 * @version 2018-10-26
 */
public class SysOtpOffice extends DataEntity<SysOtpOffice> {

	private static final long serialVersionUID = 1L;
	private Office office;// 机构
	private List<Office> officeList=Lists.newArrayList();//机构列表(页面传值)

	public SysOtpOffice() {
		super();
	}

	public SysOtpOffice(String id) {
		super(id);
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}
	
	public List<Office> getOfficeList() {
		return officeList;
	}

	public void setOfficeList(List<Office> officeList) {
		this.officeList = officeList;
	}

	public List<String> getOfficeIdList() {
		List<String> officeIdList = Lists.newArrayList();
		for (Office office : officeList) {
			officeIdList.add(office.getId());
		}
		return officeIdList;
	}
	
	public void setOfficeIdList(List<String> officeIdList) {
		officeList = Lists.newArrayList();
		for (String officeId : officeIdList) {
			Office office = new Office();
			office.setId(officeId);
			officeList.add(office);
		}
	}

	public String getOfficeIds() {
		return StringUtils.join(getOfficeIdList(), ",");
	}

	public void setOfficeIds(String officeIds) {
		officeList = Lists.newArrayList();
		if (officeIds != null) {
			String[] ids = StringUtils.split(officeIds, ",");
			setOfficeIdList(Lists.newArrayList(ids));
		}
	}
}