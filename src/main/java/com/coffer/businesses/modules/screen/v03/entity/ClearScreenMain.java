package com.coffer.businesses.modules.screen.v03.entity;

import java.util.Date;
import java.util.List;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 清分监控中心Entity
 * @author wanglin
 * @version 2018-01-04
 */
public class ClearScreenMain extends DataEntity<ClearScreenMain> {
	
	private static final long serialVersionUID = 1L;

	/** 全国网点地址 */
	private List<ClearAddress> chinaAddressList;

	/** 辽宁省网点地址 */
	private List<ClearAddress> provinceAddressList;
	
	/** 鞍山市网点地址 */
	private List<ClearAddress> cityAddressList;
	
	private ClearAddress centerAddress;		//聚龙清分中心地址
	
	
	private String toDayBankUpAmount; // 今日 商行上缴额
	private String toDayBankBackAmount;		//今日 商行取款额
	private String toDayClearAmount;		//今日 清分中心清点额
	private String toDayClearErrorAmount;	//今日 清分差错额
	
	private String totalBankUpAmount;		//商行 累计上缴额
	private String totalBankBackAmount;		//商行 累计取款额
	private String totalClearAmount;		//清分中心 累计清点额
	private String totalClearErrorAmount;	//清分中心 累计差错额
	
	
	private String toDayBankUpRate;				//今日 商行上缴百分比率
	private String toDayBankBackRate;			//今日 商行取款额百分比率
	private String yesterDayBankUpRate;			//昨日 商行上缴百分比率
	private String yesterDayBankBackRate;		//昨日 商行取款额百分比率
	private String totalClearCountAmount;		//累计 清分中心清点额
	private String totalClearReCountAmount;		//累计 清分中心复点额
	private String totalClearCountRate;			//累计 清分中心清点率
	private String totalClearReCountRate;		//累计 清分中心复点率
	
	private String inAmount;					//入库总金额（借）
	private String outAmount;					//出库总金额（贷）
	private String errorMoney;					//差错额
	
	private String countAmount;					//清分中心 清分额
	private String reCountAmount;				//清分中心 复点额

	private String clearCount;					//服务清分业务
	private String goldBankCount;				//服务金库业务
	private String doorCustCount;				//上门收款门店
	private String doorBusinessCount;			//上门收款商户
	private String doorGoldBankCount;			//服务上门收款
	private String atmCount;					//加钞自助设备(ATM)
	private String atmCustCount;				//服务自助设备客户(ATM)
	

	private List<String> officeList;			//机构ID
	private Date registerDate;					//登记日期
	private String businessType;				//业务类型
	private String excludeTaskType;				//任务类型
	
	// add by qph 2018-03-02
	private String officeId; // 机构编号
	private String officeName; // 机构名称

	private String provinceCode; // 省级编号
	private String cityCode; // 城市编号
	private String countyCode; // 县区编号

	public ClearScreenMain() {
		super();
	}
	
	public ClearScreenMain(String id){
		super(id);
	}

	public String getToDayBankUpAmount() {
		return toDayBankUpAmount;
	}

	public void setToDayBankUpAmount(String toDayBankUpAmount) {
		this.toDayBankUpAmount = toDayBankUpAmount;
	}

	public String getToDayBankBackAmount() {
		return toDayBankBackAmount;
	}

	public void setToDayBankBackAmount(String toDayBankBackAmount) {
		this.toDayBankBackAmount = toDayBankBackAmount;
	}

	public String getToDayClearAmount() {
		return toDayClearAmount;
	}

	public void setToDayClearAmount(String toDayClearAmount) {
		this.toDayClearAmount = toDayClearAmount;
	}

	public String getToDayClearErrorAmount() {
		return toDayClearErrorAmount;
	}

	public void setToDayClearErrorAmount(String toDayClearErrorAmount) {
		this.toDayClearErrorAmount = toDayClearErrorAmount;
	}

	public String getTotalBankUpAmount() {
		return totalBankUpAmount;
	}

	public void setTotalBankUpAmount(String totalBankUpAmount) {
		this.totalBankUpAmount = totalBankUpAmount;
	}

	public String getTotalBankBackAmount() {
		return totalBankBackAmount;
	}

	public void setTotalBankBackAmount(String totalBankBackAmount) {
		this.totalBankBackAmount = totalBankBackAmount;
	}

	public String getTotalClearAmount() {
		return totalClearAmount;
	}

	public void setTotalClearAmount(String totalClearAmount) {
		this.totalClearAmount = totalClearAmount;
	}

	public String getTotalClearErrorAmount() {
		return totalClearErrorAmount;
	}

	public void setTotalClearErrorAmount(String totalClearErrorAmount) {
		this.totalClearErrorAmount = totalClearErrorAmount;
	}

	public String getToDayBankUpRate() {
		return toDayBankUpRate;
	}

	public void setToDayBankUpRate(String toDayBankUpRate) {
		this.toDayBankUpRate = toDayBankUpRate;
	}

	public String getToDayBankBackRate() {
		return toDayBankBackRate;
	}

	public void setToDayBankBackRate(String toDayBankBackRate) {
		this.toDayBankBackRate = toDayBankBackRate;
	}

	public String getYesterDayBankUpRate() {
		return yesterDayBankUpRate;
	}

	public void setYesterDayBankUpRate(String yesterDayBankUpRate) {
		this.yesterDayBankUpRate = yesterDayBankUpRate;
	}

	public String getYesterDayBankBackRate() {
		return yesterDayBankBackRate;
	}

	public void setYesterDayBankBackRate(String yesterDayBankBackRate) {
		this.yesterDayBankBackRate = yesterDayBankBackRate;
	}

	public String getTotalClearCountAmount() {
		return totalClearCountAmount;
	}

	public void setTotalClearCountAmount(String totalClearCountAmount) {
		this.totalClearCountAmount = totalClearCountAmount;
	}

	public String getTotalClearReCountAmount() {
		return totalClearReCountAmount;
	}

	public void setTotalClearReCountAmount(String totalClearReCountAmount) {
		this.totalClearReCountAmount = totalClearReCountAmount;
	}

	public String getTotalClearCountRate() {
		return totalClearCountRate;
	}

	public void setTotalClearCountRate(String totalClearCountRate) {
		this.totalClearCountRate = totalClearCountRate;
	}

	public String getTotalClearReCountRate() {
		return totalClearReCountRate;
	}

	public void setTotalClearReCountRate(String totalClearReCountRate) {
		this.totalClearReCountRate = totalClearReCountRate;
	}

	public String getInAmount() {
		return inAmount;
	}

	public void setInAmount(String inAmount) {
		this.inAmount = inAmount;
	}

	public String getOutAmount() {
		return outAmount;
	}

	public void setOutAmount(String outAmount) {
		this.outAmount = outAmount;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getCountAmount() {
		return countAmount;
	}

	public void setCountAmount(String countAmount) {
		this.countAmount = countAmount;
	}

	public String getReCountAmount() {
		return reCountAmount;
	}

	public void setReCountAmount(String reCountAmount) {
		this.reCountAmount = reCountAmount;
	}

	public String getExcludeTaskType() {
		return excludeTaskType;
	}

	public void setExcludeTaskType(String excludeTaskType) {
		this.excludeTaskType = excludeTaskType;
	}

	public List<ClearAddress> getChinaAddressList() {
		return chinaAddressList;
	}

	public void setChinaAddressList(List<ClearAddress> chinaAddressList) {
		this.chinaAddressList = chinaAddressList;
	}

	public List<ClearAddress> getProvinceAddressList() {
		return provinceAddressList;
	}

	public void setProvinceAddressList(List<ClearAddress> provinceAddressList) {
		this.provinceAddressList = provinceAddressList;
	}

	public List<ClearAddress> getCityAddressList() {
		return cityAddressList;
	}

	public void setCityAddressList(List<ClearAddress> cityAddressList) {
		this.cityAddressList = cityAddressList;
	}

	public ClearAddress getCenterAddress() {
		return centerAddress;
	}

	public void setCenterAddress(ClearAddress centerAddress) {
		this.centerAddress = centerAddress;
	}

	public String getErrorMoney() {
		return errorMoney;
	}

	public void setErrorMoney(String errorMoney) {
		this.errorMoney = errorMoney;
	}

	public List<String> getOfficeList() {
		return officeList;
	}

	public void setOfficeList(List<String> officeList) {
		this.officeList = officeList;
	}

	public String getClearCount() {
		return clearCount;
	}

	public void setClearCount(String clearCount) {
		this.clearCount = clearCount;
	}

	public String getGoldBankCount() {
		return goldBankCount;
	}

	public void setGoldBankCount(String goldBankCount) {
		this.goldBankCount = goldBankCount;
	}

	public String getDoorCustCount() {
		return doorCustCount;
	}

	public void setDoorCustCount(String doorCustCount) {
		this.doorCustCount = doorCustCount;
	}

	public String getDoorGoldBankCount() {
		return doorGoldBankCount;
	}

	public void setDoorGoldBankCount(String doorGoldBankCount) {
		this.doorGoldBankCount = doorGoldBankCount;
	}

	public String getAtmCount() {
		return atmCount;
	}

	public void setAtmCount(String atmCount) {
		this.atmCount = atmCount;
	}

	public String getAtmCustCount() {
		return atmCustCount;
	}

	public void setAtmCustCount(String atmCustCount) {
		this.atmCustCount = atmCustCount;
	}

	public String getDoorBusinessCount() {
		return doorBusinessCount;
	}

	public void setDoorBusinessCount(String doorBusinessCount) {
		this.doorBusinessCount = doorBusinessCount;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
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



}