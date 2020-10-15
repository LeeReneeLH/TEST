package com.coffer.core.modules.sys.entity;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;

/**
 * otp动态口令管理Entity
 * @author qph
 * @version 2018-07-02
 */
public class SysOtp extends DataEntity<SysOtp> {
	
	private static final long serialVersionUID = 1L;
	private String tokenId;		// 令牌号
	private String authKey;		// 密钥
	private Long currsucc;		// 成功值
	private Long currdft;		// 漂移值
	private String brandNo;		// 品牌编号
	private String brandName;		// 品牌名称
	private String modelNo;		// 型号编号
	private String modelName;		// 型号名称
	private User user;		// 绑定用户编号
	private Office office; // 绑定机构
	private String command; // 动态口令
	private String nextcommand; // 再次输入动态口令（同步用）

	/** 复合人员编号 **/
	private String bindingManNo = "";
	/** 复合人员 **/
	private String bindingManName = "";

	public SysOtp() {
		super();
	}

	public SysOtp(String id){
		super(id);
	}

	@Length(min=0, max=64, message="令牌号长度必须介于 0 和 64 之间")
	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}
	
	@Length(min=0, max=64, message="密钥长度必须介于 0 和 64 之间")
	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}
	
	public Long getCurrsucc() {
		return currsucc;
	}

	public void setCurrsucc(Long currsucc) {
		this.currsucc = currsucc;
	}
	
	public Long getCurrdft() {
		return currdft;
	}

	public void setCurrdft(Long currdft) {
		this.currdft = currdft;
	}
	
	@Length(min=0, max=64, message="品牌编号长度必须介于 0 和 64 之间")
	public String getBrandNo() {
		return brandNo;
	}

	public void setBrandNo(String brandNo) {
		this.brandNo = brandNo;
	}
	
	@Length(min=0, max=64, message="品牌名称长度必须介于 0 和 64 之间")
	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	
	@Length(min=0, max=64, message="型号编号长度必须介于 0 和 64 之间")
	public String getModelNo() {
		return modelNo;
	}

	public void setModelNo(String modelNo) {
		this.modelNo = modelNo;
	}
	
	@Length(min=0, max=64, message="型号名称长度必须介于 0 和 64 之间")
	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Length(min=0, max=100, message="创建人员姓名长度必须介于 0 和 100 之间")
	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public String getBindingManNo() {
		return bindingManNo;
	}

	public void setBindingManNo(String bindingManNo) {
		this.bindingManNo = bindingManNo;
	}

	public String getBindingManName() {
		return bindingManName;
	}

	public void setBindingManName(String bindingManName) {
		this.bindingManName = bindingManName;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getNextcommand() {
		return nextcommand;
	}

	public void setNextcommand(String nextcommand) {
		this.nextcommand = nextcommand;
	}

	
}