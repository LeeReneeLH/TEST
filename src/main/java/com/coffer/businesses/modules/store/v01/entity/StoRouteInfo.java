package com.coffer.businesses.modules.store.v01.entity;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;
import com.google.common.collect.Lists;

/**
 * 线路管理Entity
 * 
 * @author niguoyong
 * @version 2015-09-02
 */
public class StoRouteInfo extends DataEntity<StoRouteInfo> {

	private static final long serialVersionUID = 1L;
	private String routeId; // 编号
	private String routeName; // 名称
	private Integer detailNum; // 网点数量
	private String routeType; // 线路类型
	private String rfid; // rfid
	private StoEscortInfo escortInfo1; // 押运人员1信息
	private String escortId1;
	private StoEscortInfo escortInfo2; // 押运人员2信息
	private String escortId2;
	private Date createDate;// 路线创建时间
	private List<StoRouteDetail> stoRouteDetailList = Lists.newArrayList();

	private String carNo;

	// 查询使用
	private Office office; // 网点
	private String escortId;// 人员查询条件
	// 线路的所属机构
	private Office curOffice;
	private String routeLnglat;
	// 线路规划ID
	private String routePlanId;
	// 车辆实时速度
	private String carSpeed;
	// 车辆实时任务标识
	private String taskFlag;
	// 规划线路颜色
	private String routePlanColor;
	// 车辆经过轨迹颜色
	private String carTrackColor;

	/**
	 * 删除标记（0：正常；1：删除；2：审核；）
	 */
	public static final String DEL_FLAG_NORMAL = "0";
	public static final String DEL_FLAG_DELETE = "1";

	public StoRouteInfo() {
		super();
	}

	public StoRouteInfo(String routeId) {
		super(routeId);
		this.routeId = routeId;
	}

	/**
	 * @return routeId
	 */
	@Override
	public String getId() {
		return routeId;
	}

	/**
	 * @param routeId
	 *            要设置的 routeId
	 */
	@Override
	public void setId(String id) {
		this.routeId = id;
	}

	/**
	 * @return routeName
	 */
	@NotNull(message = "线路名称不能为空")
	@Length(min = 1, max = 25, message = "线路名称长度必须介于 1 和 25 之间")
	public String getRouteName() {
		return routeName;
	}

	/**
	 * @param routeName
	 *            要设置的 routeName
	 */
	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	/**
	 * @return detailNum
	 */
	public Integer getDetailNum() {
		return detailNum;
	}

	/**
	 * @param detailNum
	 *            要设置的 detailNum
	 */
	public void setDetailNum(Integer detailNum) {
		this.detailNum = detailNum;
	}

	/**
	 * @return routeType
	 */
	public String getRouteType() {
		return routeType;
	}

	/**
	 * @param routeType
	 *            要设置的 routeType
	 */
	public void setRouteType(String routeType) {
		this.routeType = routeType;
	}

	/**
	 * @return rfid
	 */
	public String getRfid() {
		return rfid;
	}

	/**
	 * @param rfid
	 *            要设置的 rfid
	 */
	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	/**
	 * @return escortInfo1
	 */
	public StoEscortInfo getEscortInfo1() {
		return escortInfo1;
	}

	/**
	 * @param escortInfo1
	 *            要设置的 escortInfo1
	 */
	public void setEscortInfo1(StoEscortInfo escortInfo1) {
		this.escortInfo1 = escortInfo1;
	}

	/**
	 * @return escortId1
	 */
	public String getEscortId1() {
		return escortId1;
	}

	/**
	 * @param escortId1
	 *            要设置的 escortId1
	 */
	public void setEscortId1(String escortId1) {
		this.escortId1 = escortId1;
	}

	/**
	 * @return escortInfo2
	 */
	public StoEscortInfo getEscortInfo2() {
		return escortInfo2;
	}

	/**
	 * @param escortInfo2
	 *            要设置的 escortInfo2
	 */
	public void setEscortInfo2(StoEscortInfo escortInfo2) {
		this.escortInfo2 = escortInfo2;
	}

	/**
	 * @return escortId2
	 */
	public String getEscortId2() {
		return escortId2;
	}

	/**
	 * @param escortId2
	 *            要设置的 escortId2
	 */
	public void setEscortId2(String escortId2) {
		this.escortId2 = escortId2;
	}

	/**
	 * @return stoRouteDetailList
	 */
	public List<StoRouteDetail> getStoRouteDetailList() {
		return stoRouteDetailList;
	}

	/**
	 * @param stoRouteDetailList
	 *            要设置的 stoRouteDetailList
	 */
	public void setStoRouteDetailList(List<StoRouteDetail> stoRouteDetailList) {
		this.stoRouteDetailList = stoRouteDetailList;
	}

	/**
	 * @return office
	 */
	public Office getOffice() {
		return office;
	}

	/**
	 * @param office
	 *            要设置的 office
	 */
	public void setOffice(Office office) {
		this.office = office;
	}

	/**
	 * @return escortId
	 */
	public String getEscortId() {
		return escortId;
	}

	/**
	 * @param escortId
	 *            要设置的 escortId
	 */
	public void setEscortId(String escortId) {
		this.escortId = escortId;
	}

	/**
	 * @return officeIds
	 */
	public String getOfficeIds() {
		List<String> nameIdList = Lists.newArrayList();
		for (StoRouteDetail stoRouteDetail : stoRouteDetailList) {
			Office office = stoRouteDetail.getOffice();
			nameIdList.add(office.getId());
		}
		return StringUtils.join(nameIdList, ",");
	}

	/**
	 * @param officeIds
	 *            要设置的 officeIds
	 */
	public void setOfficeIds(String officeIds) {
		stoRouteDetailList = Lists.newArrayList();
		if (officeIds != null) {
			String[] ids = StringUtils.split(officeIds, ",");
			for (String officeId : ids) {
				Office office = new Office();
				office.setId(officeId);
				StoRouteDetail stoRouteDetail = new StoRouteDetail();
				stoRouteDetail.setOffice(office);
				stoRouteDetailList.add(stoRouteDetail);
			}
		}
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCarNo() {
		return carNo;
	}

	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}

	// 线路的所属机构
	public Office getCurOffice() {
		return curOffice;
	}

	public void setCurOffice(Office curOffice) {
		this.curOffice = curOffice;
	}

	/**
	 * @return the routeLnglat
	 */
	public String getRouteLnglat() {
		return routeLnglat;
	}

	/**
	 * @param routeLnglat the routeLnglat to set
	 */
	public void setRouteLnglat(String routeLnglat) {
		this.routeLnglat = routeLnglat;
	}

	/**
	 * @return the routePlanId
	 */
	public String getRoutePlanId() {
		return routePlanId;
	}

	/**
	 * @param routePlanId the routePlanId to set
	 */
	public void setRoutePlanId(String routePlanId) {
		this.routePlanId = routePlanId;
	}

	/**
	 * @return the carSpeed
	 */
	public String getCarSpeed() {
		return carSpeed;
	}

	/**
	 * @param carSpeed the carSpeed to set
	 */
	public void setCarSpeed(String carSpeed) {
		this.carSpeed = carSpeed;
	}

	/**
	 * @return the taskFlag
	 */
	public String getTaskFlag() {
		return taskFlag;
	}

	/**
	 * @param taskFlag the taskFlag to set
	 */
	public void setTaskFlag(String taskFlag) {
		this.taskFlag = taskFlag;
	}

	/**
	 * @return the routePlanColor
	 */
	public String getRoutePlanColor() {
		return routePlanColor;
	}

	/**
	 * @param routePlanColor the routePlanColor to set
	 */
	public void setRoutePlanColor(String routePlanColor) {
		this.routePlanColor = routePlanColor;
	}

	/**
	 * @return the carTrackColor
	 */
	public String getCarTrackColor() {
		return carTrackColor;
	}

	/**
	 * @param carTrackColor the carTrackColor to set
	 */
	public void setCarTrackColor(String carTrackColor) {
		this.carTrackColor = carTrackColor;
	}

}
