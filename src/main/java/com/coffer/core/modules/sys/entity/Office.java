package com.coffer.core.modules.sys.entity;

import java.beans.Transient;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.TreeEntity;
import com.coffer.core.common.utils.StringUtils;

/**
 * 机构Entity
 * 
 * @author Clark
 * @version 2015-05-13
 */
public class Office extends TreeEntity<Office> {

	private static final long serialVersionUID = 1L;
	private String code; // 机构编码
	private String type; // 机构类型
	private String master;
	private String address; // 联系地址
	private String zipCode; // 邮政编码
	private String phone; // 电话
	private String fax; // 传真
	private String email; // 邮箱
	private String provinceCode;// 机构所在省编码
	private String cityCode;// 机构所在市编码
	private String countyCode;// 机构所在区县编码
	private String longitude;// 机构经度
	private String latitude;// 机构纬度
	private List<String> childDeptList;// 快速添加子部门
	// 增加归属机构 修改人：xp 修改时间：2017-7-3 begin
	private Office ascriptionOfficeId;

	// end
	private String tradeFlag;// 同业标识

	// 增加备付金余额验证开关、交接人员验证开关 修改人：qph 修改时间：2017-12-28 begin
	private String provisionsSwitch; // 备付金余额验证开关

	private String joinManSwitch; // 交接人员验证开关
	// end

	/** 银行卡号 修改人：XL 日期：2019-06-26 */
	private String bankCard;

	// BS端机构管理，树状图，追加当前机构下子机构的数量
	private int subagencyCount;

	/** 新增字段 修改人：ZXK 日期：2019-12-12 */
	private String payerAccountId; // 付款方客户账号
	private String payerAccountName; // 付款方账户名称
	private String payeeAccountId; // 收款方客户账号
	private String payeeAccountName; // 收款方账户名称
	private String payeeBankName; // 收款方开户行名称
	/** end */
	/** 父级id列表 修改人：lihe 日期：2020-06-15 */
	private List<String> parentIdList;

	public Office() {
		super();
	}

	public String getPayerAccountId() {
		return payerAccountId;
	}

	public void setPayerAccountId(String payerAccountId) {
		this.payerAccountId = payerAccountId;
	}

	public String getPayerAccountName() {
		return payerAccountName;
	}

	public void setPayerAccountName(String payerAccountName) {
		this.payerAccountName = payerAccountName;
	}

	public String getPayeeAccountId() {
		return payeeAccountId;
	}

	public void setPayeeAccountId(String payeeAccountId) {
		this.payeeAccountId = payeeAccountId;
	}

	public String getPayeeAccountName() {
		return payeeAccountName;
	}

	public void setPayeeAccountName(String payeeAccountName) {
		this.payeeAccountName = payeeAccountName;
	}

	public String getPayeeBankName() {
		return payeeBankName;
	}

	public void setPayeeBankName(String payeeBankName) {
		this.payeeBankName = payeeBankName;
	}

	public Office(String id) {
		super(id);
		this.id = id;
	}

	public List<String> getChildDeptList() {
		return childDeptList;
	}

	public void setChildDeptList(List<String> childDeptList) {
		this.childDeptList = childDeptList;
	}

	// @JsonBackReference
	// @NotNull
	public Office getParent() {
		return parent;
	}

	public void setParent(Office parent) {
		this.parent = parent;
	}

	@Length(min = 1, max = 2)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Length(min = 0, max = 255)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Length(min = 0, max = 100)
	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	@Length(min = 0, max = 200)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Length(min = 0, max = 200)
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Length(min = 0, max = 200)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getCountyCode() {
		return countyCode;
	}

	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	@Length(min = 0, max = 15)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	// 增加归属机构 修改人：xp 修改时间：2017-7-3 begin
	public void setAscriptionOfficeId(Office ascriptionOfficeId) {
		this.ascriptionOfficeId = ascriptionOfficeId;
	}

	public Office getAscriptionOfficeId() {
		return ascriptionOfficeId;
	}

	// end
	/**
	 * @author Clark
	 * @version 2015年5月13日 机构树结构排序
	 * 
	 * @param list
	 * @param sourcelist
	 * @param parentId
	 */
	@Transient
	public static void sortList(List<Office> list, List<Office> sourcelist, String parentId) {
		if (sourcelist == null || sourcelist.size() == 0) {
			return;
		}
		if (StringUtils.isBlank(parentId)) {
			parentId = sourcelist.get(0).getParentId();
		}

		for (int i = 0; i < sourcelist.size(); i++) {
			Office e = sourcelist.get(i);

			if (e.getParentId().equals(parentId)) {
				list.add(e);
				// 判断是否还有子节点, 有则继续获取子节点
				for (int j = 1; j < sourcelist.size(); j++) {
					Office child = sourcelist.get(j);
					if (child.getParentId().equals(e.getId())) {
						sortList(list, sourcelist, e.getId());
						break;
					}
				}
			}
		}
	}

	/**
	 * @author Clark
	 * @version 2015年6月9日
	 * 
	 *          是否为顶级机构
	 * @return
	 */
	public boolean isRoot() {
		String topId = Global.getConfig("top.office.id");

		if (StringUtils.isNotBlank(topId) && topId.equals(this.id)) {
			return true;
		}
		return false;
	}

	public String getTradeFlag() {
		return tradeFlag;
	}

	public void setTradeFlag(String tradeFlag) {
		this.tradeFlag = tradeFlag;
	}

	public String getProvisionsSwitch() {
		return provisionsSwitch;
	}

	public void setProvisionsSwitch(String provisionsSwitch) {
		this.provisionsSwitch = provisionsSwitch;
	}

	public String getJoinManSwitch() {
		return joinManSwitch;
	}

	public void setJoinManSwitch(String joinManSwitch) {
		this.joinManSwitch = joinManSwitch;
	}

	public String getBankCard() {
		return bankCard;
	}

	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}

	public int getSubagencyCount() {
		return subagencyCount;
	}

	public void setSubagencyCount(int subagencyCount) {
		this.subagencyCount = subagencyCount;
	}

	public List<String> getParentIdList() {
		return parentIdList;
	}

	public void setParentIdList(List<String> parentIdList) {
		this.parentIdList = parentIdList;
	}

}