package com.coffer.core.modules.sys.entity;

import java.beans.Transient;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.common.utils.excel.fieldtype.RoleListType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

/**
 * 用户Entity
 * 
 * @author Clark
 * @version 2015-05-05
 */
public class User extends DataEntity<User> {

	private static final long serialVersionUID = 1L;
	private Office office; // 归属机构
	private String loginName;// 登录名
	private String password;// 密码
	private String no; // 工号
	private String name; // 姓名
	private String email; // 邮箱
	private String phone; // 电话
	private String mobile; // 手机
	private String userType;// 用户类型
	private String loginIp; // 最后登陆IP
	private Date loginDate; // 最后登陆日期

	private String oldLoginName;// 原登录名
	private String newPassword; // 新密码

	private String oldLoginIp; // 上次登陆IP
	private Date oldLoginDate; // 上次登陆日期

	private Role role; // 根据角色查询用户条件
	private String csPermission; // 硬件设备权限

	private List<Role> roleList = Lists.newArrayList(); // 拥有角色列表
	private List<String> csPermlist = Lists.newArrayList(); // 拥有硬件设备权限列表

	private String idcardNo; // 身份证号

	/** 用户脸部识别ID **/
	private Long userFaceId;
	/** 设定用户脸部识别ID 标识 **/
	private String initFaceIdFlag;

	/** 微信绑定标识 add by lihe 2019-09-20 **/
	private String bindFlag;

	/** 押运人员ID */
	private String escortId;

	public User() {
		super();
	}

	public User(String id) {
		super(id);
	}

	public User(String id, String loginName) {
		super(id);
		this.loginName = loginName;
	}

	public User(Role role) {
		super();
		this.role = role;
	}

	// @ExcelField(title = "ID", type = 1, align = 2, sort = 1)
	public String getId() {
		return id;
	}

	@JsonIgnore
	@NotNull(message = "归属机构不能为空")
	@ExcelField(title = "归属机构", align = 2, sort = 10)
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	@Pattern(regexp = "^[a-zA-Z0-9]{2,19}$", message = "登录名必须为字母或数字")
	@Length(min = 2, max = 20, message = "登录名长度必须介于 2 和 20之间")
	@ExcelField(title = "登录名", align = 2, sort = 20)
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@NotNull(message = "身份证不能为空")
	@ExcelField(title = "身份证号码", type = 0, align = 1, sort = 30)
	public String getIdcardNo() {
		return idcardNo;
	}

	public void setIdcardNo(String idcardNo) {
		this.idcardNo = idcardNo;
	}

	@JsonIgnore
	// @Length(min = 1, max = 100, message = "密码长度必须介于 1 和 15 之间")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Length(min = 1, max = 10, message = "姓名长度必须介于 1 和 10 之间")
	@ExcelField(title = "姓名", align = 2, sort = 40)
	public String getName() {
		return name;
	}

	@ExcelField(title = "工号", align = 2, sort = 50)
	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Email(message = "邮箱格式不正确")
	@Length(min = 0, max = 50, message = "邮箱长度必须介于 1 和 50 之间")
	@ExcelField(title = "邮箱", align = 1, sort = 60)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Length(min = 0, max = 15, message = "电话长度必须介于 1 和 15 之间")
	@ExcelField(title = "电话", align = 2, sort = 70)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Length(min = 0, max = 11, message = "手机长度必须介于 1 和 11 之间")
	@ExcelField(title = "手机", align = 2, sort = 80)
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Length(min = 0, max = 100, message = "备注长度必须介于 0 和 11 之间")
	@ExcelField(title = "备注", align = 1, sort = 90)
	public String getRemarks() {
		return remarks;
	}

	@ExcelField(title = "创建时间", type = 1, align = 1, sort = 100)
	public Date getCreateDate() {
		return createDate;
	}

	@ExcelField(title = "最后登录IP", type = 1, align = 1, sort = 110)
	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title = "最后登录日期", type = 1, align = 1, sort = 120)
	public Date getLoginDate() {
		return loginDate;
	}

	@JsonIgnore
	@ExcelField(title = "拥有角色", align = 1, sort = 130, fieldType = RoleListType.class)
	public List<Role> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}

	@Length(min = 1, max = 10, message = "岗位长度必须介于 1 和 10 之间")
	@ExcelField(title = "岗位", align = 2, sort = 140, dictType = "sys_user_type")
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	public String getOldLoginName() {
		return oldLoginName;
	}

	public void setOldLoginName(String oldLoginName) {
		this.oldLoginName = oldLoginName;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getOldLoginIp() {
		if (oldLoginIp == null) {
			return loginIp;
		}
		return oldLoginIp;
	}

	public void setOldLoginIp(String oldLoginIp) {
		this.oldLoginIp = oldLoginIp;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getOldLoginDate() {
		if (oldLoginDate == null) {
			return loginDate;
		}
		return oldLoginDate;
	}

	public void setOldLoginDate(Date oldLoginDate) {
		this.oldLoginDate = oldLoginDate;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@JsonIgnore
	public List<String> getRoleIdList() {
		List<String> roleIdList = Lists.newArrayList();
		for (Role role : roleList) {
			roleIdList.add(role.getId());
		}
		return roleIdList;
	}

	public void setRoleIdList(List<String> roleIdList) {
		roleList = Lists.newArrayList();
		for (String roleId : roleIdList) {
			Role role = new Role();
			role.setId(roleId);
			roleList.add(role);
		}
	}

	public String getCsPermission() {
		return csPermission;
	}

	public void setCsPermission(String csPermission) {
		this.csPermission = csPermission;
	}

	@Transient
	public List<String> getCsPermlist() {
		if (Collections3.isEmpty(this.csPermlist)) {
			if (StringUtils.isNotBlank(this.csPermission)) {
				String[] perms = StringUtils.split(this.csPermission, Global.getConfig("common.separatorChar"));
				csPermlist = Arrays.asList(perms);
			}
		}
		return csPermlist;
	}

	@Transient
	public void setCsPermlist(List<String> csPermlist) {
		this.csPermission = Collections3.convertToString(csPermlist, Global.getConfig("common.separatorChar"));
		this.csPermlist = csPermlist;
	}

	/**
	 * 用户拥有的角色名称字符串, 多个角色名称用','分隔.
	 */
	public String getRoleNames() {
		return Collections3.extractToString(roleList, "name", ",");
	}

	public boolean isAdmin() {
		return isAdmin(this.id);
	}

	/**
	 * @author Clark
	 * @version 2015-05-19 是否为超级用户
	 * 
	 * @param user
	 * @return
	 */
	public static boolean isAdmin(String id) {
		return id != null && "1".equals(id);
	}

	/**
	 * @return the userFaceId
	 */
	public Long getUserFaceId() {
		return userFaceId;
	}

	/**
	 * @param userFaceId
	 *            the userFaceId to set
	 */
	public void setUserFaceId(Long userFaceId) {
		this.userFaceId = userFaceId;
	}

	/**
	 * @return initFaceIdFlag
	 */
	public String getInitFaceIdFlag() {
		return initFaceIdFlag;
	}

	/**
	 * @param initFaceIdFlag
	 *            要设置的 initFaceIdFlag
	 */
	public void setInitFaceIdFlag(String initFaceIdFlag) {
		this.initFaceIdFlag = initFaceIdFlag;
	}

	public String getBindFlag() {
		return bindFlag;
	}

	public void setBindFlag(String bindFlag) {
		this.bindFlag = bindFlag;
	}

	public String getEscortId() {
		return escortId;
	}

	public void setEscortId(String escortId) {
		this.escortId = escortId;
	}
}