package com.coffer.businesses.modules.atm.v01.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.coffer.businesses.modules.atm.ATMConstant;
import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 加钞计划导入Entity
 * 
 * @author XL
 * @version 2017-11-07
 */
public class AtmPlanInfo extends DataEntity<AtmPlanInfo> {

	private static final long serialVersionUID = 1L;
	private String planId; // 主键ID
	private String addPlanId; // 加钞计划ID
	
	/**新增属性  wanglu 2017-11-16 */
	private String addCashGroupId; //加钞组ID
	
	/**新增属性  wanglu 2017-11-16 */
	private String addCashGroupName;	//加钞组名称
	
	private String atmNo; // ATM机编号
	private String atmAddress; // 装机地址
	private String atmAccount; // 柜员编号
	private String atmBrandsName; // 品牌名称
	private String atmTypeName; // 型号名称
	private Office office; // 机构ID
	private BigDecimal addAmount; // 加钞金额
	private Integer getBoxNum; // 加钞钞箱数量
	private Integer backBoxNum; // 回收箱数量
	private Integer depositBoxNum; // 存款箱数量
	private String status; // 计划状态

	private Integer sumBoxNum;// 总钞箱数量

	private String addPlanName; // 加钞计划文件标题头
	private String atmTypeNo; // 型号编号

	// 查询使用
	private Integer atmNum; // atm机数量
	private Integer boxNum; // 钞箱总数量
	
	/** 手动生成加钞计划使用  修改人：wxz 2017-12-7 **/
	private List<AtmPlanInfo> addPlanList = Lists.newArrayList();
	
	public List<AtmPlanInfo> getAddPlanList() {
		return addPlanList;
	}

	public void setAddPlanList(List<AtmPlanInfo> addPlanList) {
		this.addPlanList = addPlanList;
	}

	/** 调缴详细列表 **/
	private List<AtmBindingDetail> atmBindingDetailList = Lists.newArrayList();
	
	/** 绑定信息 **/
	private AtmBindingInfo atmBindingInfo = new AtmBindingInfo();

	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;
	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;
	
	/** 状态列表 **/
	private List<String> statuses;
	
	/** 更新人员 **/
	// private String updateName;
	/** 更新日期 **/
	private String addAmountTime;
	
	/** 查询标识 **/
	private String searchFlag;
	/** 序号 **/
	private int no;
	
	/** 箱号列表 **/
	private String boxList;
	/** 备注 **/
	private String remark;
	/** 核对结果 **/
	private String checkResult;
	/** 押运人员 **/
	private String escortUserName;

	/** 导入模板序号 **/
	private String noImp;
	/** 导入模板加钞金额 **/
	private String addAmountStr;
	/** 导入模板取款箱个数 **/
	private String getBoxNumStr;
	/** ATM机rfid **/
	private String atmRfid;

	public AtmPlanInfo() {
		super();
	}

	public AtmPlanInfo(String id) {
		super(id);
	}

	public void preInsert() {
		super.preInsert();
		this.planId = IdGen.uuid();
		User user = UserUtils.getUser();
		this.office = user.getOffice();
		this.status = ATMConstant.PlanStatus.PLAN_CREATE;
	}

	@Length(min = 1, max = 64, message = "主键ID长度必须介于 1 和 64 之间")
	public String getId() {
		return planId;
	}

	public void setId(String planId) {
		this.planId = planId;
	}

	@Length(min = 1, max = 64, message = "加钞计划ID长度必须介于 1 和 64 之间")
	public String getAddPlanId() {
		return addPlanId;
	}

	public void setAddPlanId(String addPlanId) {
		this.addPlanId = addPlanId;
	}

	@Length(min = 1, max = 30, message = "ATM机编号长度必须介于 1 和 30 之间")
	@ExcelField(title = "终端号", align = 2, sort = 20)
	public String getAtmNo() {
		return atmNo;
	}

	public void setAtmNo(String atmNo) {
		this.atmNo = atmNo;
	}

	@Length(min = 1, max = 100, message = "装机地址长度必须介于 1 和 100 之间")
	@ExcelField(title = "网点名称", align = 2, sort = 40)
	public String getAtmAddress() {
		return atmAddress;
	}

	public void setAtmAddress(String atmAddress) {
		this.atmAddress = atmAddress;
	}

	@Length(min = 1, max = 20, message = "柜员编号长度必须介于 1 和 20 之间")
	@ExcelField(title = "柜员号", align = 2, sort = 30)
	public String getAtmAccount() {
		return atmAccount;
	}

	public void setAtmAccount(String atmAccount) {
		this.atmAccount = atmAccount;
	}

	@Length(min = 1, max = 50, message = "品牌名称长度必须介于 1 和 50 之间")
	public String getAtmBrandsName() {
		return atmBrandsName;
	}

	public void setAtmBrandsName(String atmBrandsName) {
		this.atmBrandsName = atmBrandsName;
	}

	@Length(min = 1, max = 50, message = "型号名称长度必须介于 1 和 50 之间")
	@ExcelField(title = "设备型号", align = 2, sort = 80)
	public String getAtmTypeName() {
		return atmTypeName;
	}

	public void setAtmTypeName(String atmTypeName) {
		this.atmTypeName = atmTypeName;
	}

	//@NotNull(message = "机构ID不能为空")
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public BigDecimal getAddAmount() {
		return addAmount;
	}

	public void setAddAmount(BigDecimal addAmount) {
		this.addAmount = addAmount;
	}

	@NotNull(message = "加钞金额不能为空")
	@ExcelField(title = "加钞金额", align = 2, sort = 50)
	public String getAddAmountStr() {
		return addAmountStr;
	}

	public void setAddAmountStr(String addAmountStr) {
		this.addAmountStr = addAmountStr;
	}

	public Integer getGetBoxNum() {
		return getBoxNum;
	}

	public void setGetBoxNum(Integer getBoxNum) {
		this.getBoxNum = getBoxNum;
	}

	@NotNull(message = "加钞钞箱数量不能为空")
	@ExcelField(title = "取款箱个数", align = 2, sort = 60)
	public String getGetBoxNumStr() {
		return getBoxNumStr;
	}

	public void setGetBoxNumStr(String getBoxNumStr) {
		this.getBoxNumStr = getBoxNumStr;
	}

	@NotNull(message = "回收箱数量不能为空")
	public Integer getBackBoxNum() {
		return backBoxNum;
	}

	public void setBackBoxNum(Integer backBoxNum) {
		this.backBoxNum = backBoxNum;
	}

	@NotNull(message = "存款箱数量不能为空")
	public Integer getDepositBoxNum() {
		return depositBoxNum;
	}

	public void setDepositBoxNum(Integer depositBoxNum) {
		this.depositBoxNum = depositBoxNum;
	}

	public String getAddPlanName() {
		return addPlanName;
	}

	public void setAddPlanName(String addPlanName) {
		this.addPlanName = addPlanName;
	}

	@ExcelField(title = "设备型号编号", align = 2, sort = 70)
	public String getAtmTypeNo() {
		return atmTypeNo;
	}

	public void setAtmTypeNo(String atmTypeNo) {
		this.atmTypeNo = atmTypeNo;
	}

	public Integer getAtmNum() {
		return atmNum;
	}

	public void setAtmNum(Integer atmNum) {
		this.atmNum = atmNum;
	}

	public Integer getBoxNum() {
		return boxNum;
	}

	public void setBoxNum(Integer boxNum) {
		this.boxNum = boxNum;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreateTimeStart() {
		return createTimeStart;
	}

	public void setCreateTimeStart(Date createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	public Date getCreateTimeEnd() {
		return createTimeEnd;
	}

	public void setCreateTimeEnd(Date createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	public String getSearchDateStart() {
		return searchDateStart;
	}

	public void setSearchDateStart(String searchDateStart) {
		this.searchDateStart = searchDateStart;
	}

	public String getSearchDateEnd() {
		return searchDateEnd;
	}

	public void setSearchDateEnd(String searchDateEnd) {
		this.searchDateEnd = searchDateEnd;
	}

	public String getSearchFlag() {
		return searchFlag;
	}

	public void setSearchFlag(String searchFlag) {
		this.searchFlag = searchFlag;
	}


	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getBoxList() {
		return boxList;
	}

	public void setBoxList(String boxList) {
		this.boxList = boxList;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCheckResult() {
		return checkResult;
	}

	public void setCheckResult(String checkResult) {
		this.checkResult = checkResult;
	}

	public List<String> getStatuses() {
		return statuses;
	}

	public void setStatuses(List<String> statuses) {
		this.statuses = statuses;
	}

	public List<AtmBindingDetail> getAtmBindingDetailList() {
		return atmBindingDetailList;
	}

	public void setAtmBindingDetailList(List<AtmBindingDetail> atmBindingDetailList) {
		this.atmBindingDetailList = atmBindingDetailList;
	}

	public String getUpdateName() {
		return updateName;
	}

	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}

	public String getAddAmountTime() {
		return addAmountTime;
	}

	public void setAddAmountTime(String addAmountTime) {
		this.addAmountTime = addAmountTime;
	}

	public AtmBindingInfo getAtmBindingInfo() {
		return atmBindingInfo;
	}

	public void setAtmBindingInfo(AtmBindingInfo atmBindingInfo) {
		this.atmBindingInfo = atmBindingInfo;
	}

	public String getEscortUserName() {
		return escortUserName;
	}

	public void setEscortUserName(String escortUserName) {
		this.escortUserName = escortUserName;
	}
	
	public String getAddCashGroupId() {
		return addCashGroupId;
	}

	public void setAddCashGroupId(String addCashGroupId) {
		this.addCashGroupId = addCashGroupId;
	}

	public String getAddCashGroupName() {
		return addCashGroupName;
	}

	public void setAddCashGroupName(String addCashGroupName) {
		this.addCashGroupName = addCashGroupName;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	@ExcelField(title = "序号", align = 2, sort = 10)
	public String getNoImp() {
		return noImp;
	}

	public void setNoImp(String noImp) {
		this.noImp = noImp;
	}

	public String getAtmRfid() {
		return atmRfid;
	}

	public void setAtmRfid(String atmRfid) {
		this.atmRfid = atmRfid;
	}

	public Integer getSumBoxNum() {
		return sumBoxNum;
	}

	public void setSumBoxNum(Integer sumBoxNum) {
		this.sumBoxNum = sumBoxNum;
	}

	@Override
	public String toString() {
		return "AtmPlanInfo [planId=" + planId + ", addPlanId=" + addPlanId + ", atmNo=" + atmNo + ", status=" + status
				+ "]";
	}

}