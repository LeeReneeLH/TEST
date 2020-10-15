package com.coffer.businesses.modules.doorOrder.v01.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.dao.DepositPanoramaDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.SaveTypeDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.SaveType;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderDetailDao;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderDetail;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderInfo;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.dao.UserDao;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 缴存全景Service
 * 
 * @author lihe
 * @version 2020年5月26日
 */
@Service
@Transactional(readOnly = true)
public class DepositPanoramaService extends CrudService<DepositPanoramaDao, DoorOrderInfo> {

	@Autowired
	private DepositPanoramaDao depositPanoramaDao;

	@Autowired
	private DoorOrderDetailDao doorOrderDetailDao;

	@Autowired
	private SaveTypeDao saveTypeDao;
	
	@Autowired
	private UserDao userDao;
	
	/**
	  * 
	  * Title: get
	  * <p>
	  * Description: 款袋编号明细查询
	  * </p >
	  * 
	  * @author:HaoShijie
	  * @param depositPanorama
	  * @return DoorOrderInfo 返回类型
	  */
	public DoorOrderInfo get(String id) {
		DoorOrderInfo doorOrderInfo = super.get(id);
		if (doorOrderInfo != null && doorOrderInfo.getId() != null) {
			// 查找明细
			DoorOrderDetail doorOrderDetail = new DoorOrderDetail();
			doorOrderDetail.setOrderId(doorOrderInfo.getOrderId());
			//查询凭条明细信息
			List<DoorOrderDetail> doorOrderDetailList = doorOrderDetailDao.findList(doorOrderDetail);
			StringBuilder amountList = new StringBuilder();
			StringBuilder rfidList = new StringBuilder();
			StringBuilder tickertapeList = new StringBuilder();
			StringBuilder busTypeList = new StringBuilder();
			// 存款备注 gzd 2019-12-16
			StringBuilder remarksList = new StringBuilder();
			StringBuilder detailIdList = new StringBuilder();
			StringBuilder createByList = new StringBuilder();
			StringBuilder createDateList = new StringBuilder();
			for (DoorOrderDetail amountDoorOrderDetail : doorOrderDetailList) {
				amountList.append(Constant.Punctuation.COMMA);
				amountList.append(amountDoorOrderDetail.getAmount());
				String strRfid = amountDoorOrderDetail.getRfid();
				if (strRfid == null) {
					strRfid = "";
				}
				rfidList.append(Constant.Punctuation.COMMA);
				rfidList.append(strRfid);
				String strTickertape = amountDoorOrderDetail.getTickertape();
				if (strTickertape == null) {
					strTickertape = "";
				}
				tickertapeList.append(Constant.Punctuation.COMMA);
				tickertapeList.append(strTickertape);
				// 存款备注 gzd 2019-12-16 begin
				String strRemarks = amountDoorOrderDetail.getRemarks();
				if (strRemarks == null) {
					strRemarks = "";
				}
				remarksList.append(Constant.Punctuation.COMMA);
				remarksList.append(strRemarks);
				// end
				String strId = amountDoorOrderDetail.getId();
				if (strId == null) {
					strId = "";
				}
				detailIdList.append(Constant.Punctuation.COMMA) ;
				detailIdList.append(strId);
				String strBusType = amountDoorOrderDetail.getBusinessType();
				SaveType saveType = saveTypeDao.get(strBusType);
				strBusType = saveType == null ? "" : saveType.getTypeName();
				busTypeList.append(Constant.Punctuation.COMMA);
				busTypeList.append(strBusType);
				// 存款用户列表构造
				String createById = amountDoorOrderDetail.getCreateBy().getId();
				//User createBy = UserUtils.get(createById);
				User createBy = userDao.getUserById(createById);
				createByList.append(Constant.Punctuation.COMMA);
				createByList.append(createBy.getName());
				// 存款日期列表构造
				Date createDate = amountDoorOrderDetail.getCreateDate();
				String strCreateDate = DateUtils.formatDateTime(createDate);
				createDateList.append(Constant.Punctuation.COMMA);
				createDateList.append(strCreateDate);
			}
			doorOrderInfo.setAmountList(amountList.toString());
			doorOrderInfo.setRfidList(rfidList.toString());
			doorOrderInfo.setTickertapeList(tickertapeList.toString());
			doorOrderInfo.setDetailIdList(detailIdList.toString());
			doorOrderInfo.setBusTypeList(busTypeList.toString());
			doorOrderInfo.setCreateByList(createByList.toString());
			doorOrderInfo.setCreateDateList(createDateList.toString());
			// 存款备注 gzd 2019-12-16
			doorOrderInfo.setRemarksList(remarksList.toString());
		}
		return doorOrderInfo;
	}

	/**
	 * 
	 * Title: findList
	 * <p>
	 * Description: 设备缴存列表查询
	 * </p>
	 * 
	 * @author: lihe
	 * @param depositPanorama
	 * @return List<DoorOrderInfo> 返回类型
	 */
	public List<DoorOrderInfo> findList(DoorOrderInfo depositPanorama) {
		depositPanorama.setMethod(DoorOrderConstant.MethodType.METHOD_EQUIPMENT);
		return depositPanoramaDao.findList(depositPanorama);
	}

	/**
	 * 
	 * Title: packageList
	 * <p>
	 * Description: 设备缴存列表查询
	 * </p>
	 * 
	 * @author: hzy
	 * @param depositPanorama
	 * @return List<DoorOrderInfo> 返回类型
	 */
	public List<DoorOrderInfo> packageList(DoorOrderInfo depositPanorama) {
		
		// 当前登陆人机构类型
		if (UserUtils.getUser().getOffice().getType().equals(Constant.OfficeType.CLEAR_CENTER)) {
			// 按清分中心过滤
			depositPanorama.getSqlMap().put("dsf", dataScopeFilter(depositPanorama.getCurrentUser(), "o", null));
		} else {
			// 按门店过滤
			depositPanorama.getSqlMap().put("dsf", dataScopeFilter(depositPanorama.getCurrentUser(), "o1", null));
		}
		// 查询条件： 开始时间
		if (depositPanorama.getCreateTimeStart() != null) {
			depositPanorama.setSearchDateStart(
				DateUtils.foramtSearchDate(DateUtils.getDateStart(depositPanorama.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (depositPanorama.getCreateTimeEnd() != null) {
			depositPanorama.setSearchDateEnd(
				DateUtils.foramtSearchDate(DateUtils.getDateEnd(depositPanorama.getCreateTimeEnd())));
		}
		List<String> methodList = Lists.newArrayList();
		methodList.add(DoorOrderConstant.MethodType.METHOD_PC);
		methodList.add(DoorOrderConstant.MethodType.METHOD_WECHAT);
		depositPanorama.setMethodList(methodList);
		List<String> statusList = Lists.newArrayList();
		if (StringUtils.isNotEmpty(depositPanorama.getStatus())) {
			statusList.add(depositPanorama.getStatus());
			depositPanorama.setStatusList(statusList);
		}
		return depositPanoramaDao.packageList(depositPanorama);
	}

	/**
	 * 
	 * Title: findPage
	 * <p>
	 * Description: 设备缴存列表分页查询
	 * </p>
	 * 
	 * @author: lihe
	 * @param page,depositPanorama
	 * @return Page<DoorOrderInfo> 返回类型
	 */
	public Page<DoorOrderInfo> findPage(Page<DoorOrderInfo> page, DoorOrderInfo depositPanorama) {
		// 当前登陆人机构类型
		if (UserUtils.getUser().getOffice().getType().equals(Constant.OfficeType.CLEAR_CENTER)) {
			// 按清分中心过滤
			depositPanorama.getSqlMap().put("dsf", dataScopeFilter(depositPanorama.getCurrentUser(), "o", null));
		} else {
			// 按门店过滤
			depositPanorama.getSqlMap().put("dsf", dataScopeFilter(depositPanorama.getCurrentUser(), "o1", null));
		}
		// 查询条件： 开始时间
		if (depositPanorama.getCreateTimeStart() != null) {
			depositPanorama.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(depositPanorama.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (depositPanorama.getCreateTimeEnd() != null) {
			depositPanorama.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(depositPanorama.getCreateTimeEnd())));
		}
		depositPanorama.setPage(page);
		page.setList(findList(depositPanorama));
		return page;
	}

	/**
	 * 
	 * Title: packagePage
	 * <p>
	 * Description: 封包缴存列表分页查询
	 * </p>
	 * 
	 * @author: hzy
	 * @param page,depositPanorama
	 * @return Page<DoorOrderInfo> 返回类型
	 */
	public Page<DoorOrderInfo> packagePage(Page<DoorOrderInfo> page, DoorOrderInfo depositPanorama) {
		depositPanorama.setPage(page);
		page.setList(packageList(depositPanorama));
		return page;
	}

}
