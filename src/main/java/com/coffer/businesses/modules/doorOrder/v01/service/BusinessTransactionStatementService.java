package com.coffer.businesses.modules.doorOrder.v01.service;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.collection.v03.dao.CheckCashAmountDao;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashAmount;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.dao.BusinessTransactionStatementDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.ClearPlanInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.EquipmentInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.BusinessTransactionStatement;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearPlanInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentInfo;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderAmountDao;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderDetailDao;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderInfoDao;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderAmount;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderDetail;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderInfo;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 交易报表Service
 *
 * @author yinkai
 * @version 2020-01-09
 */
@Service
@Transactional(readOnly = true)
public class BusinessTransactionStatementService
		extends CrudService<BusinessTransactionStatementDao, BusinessTransactionStatement> {

	@Autowired
	private DoorOrderInfoDao doorOrderInfoDao;

	@Autowired
	private DoorOrderDetailDao doorOrderDetailDao;

	@Autowired
	private EquipmentInfoDao equipmentInfoDao;

	@Autowired
	private ClearPlanInfoDao clearPlanInfoDao;

	@Autowired
	private DoorOrderAmountDao doorOrderAmountDao;

	@Autowired
	private CheckCashAmountDao checkCashAmountDao;

	@Autowired
	private OfficeDao officeDao;

	@Override
	public BusinessTransactionStatement get(String id) {
		return super.get(id);
	}

	@Override
	public List<BusinessTransactionStatement> findList(BusinessTransactionStatement businessTransactionStatement) {
		return super.findList(businessTransactionStatement);
	}

	@Override
	public Page<BusinessTransactionStatement> findPage(Page<BusinessTransactionStatement> page,
			BusinessTransactionStatement businessTransactionStatement) {
		/* page.setOrderBy("in_date desc"); */
		return super.findPage(page, businessTransactionStatement);
	}

	@Override
	@Transactional(rollbackFor = { Throwable.class })
	public void save(BusinessTransactionStatement businessTransactionStatement) {
		// 长款金额、短款金额 初始化0
		businessTransactionStatement.setLongCurrencyMoney(BigDecimal.ZERO.toString());
		businessTransactionStatement.setShortCurrencyMoney(BigDecimal.ZERO.toString());
		// 实点金额和总金额做差，大于0计长款，小于0计短款
		BigDecimal totalAmount = new BigDecimal(businessTransactionStatement.getTotalAmount()).setScale(2,
				BigDecimal.ROUND_HALF_UP);
		BigDecimal realClearAmount = new BigDecimal(businessTransactionStatement.getRealClearAmount()).setScale(2,
				BigDecimal.ROUND_HALF_UP);
		// 差与0比较，正值长款，负值短款
		BigDecimal subtract = realClearAmount.subtract(totalAmount);
		int i = subtract.compareTo(BigDecimal.ZERO);
		if (i > 0) {
			businessTransactionStatement.setLongCurrencyMoney(subtract.toString());
		} else if (i < 0) {
			businessTransactionStatement.setShortCurrencyMoney((subtract.abs()).toString());
		}
		// 报表新数据发生长短款，客户确认状态为未确认
		if (i != 0) {
			businessTransactionStatement.setCustConfirm(BusinessTransactionStatement.CUST_CONFIRM_STATUS_UNCONFIRMED);
		} else {
			businessTransactionStatement.setCustConfirm(BusinessTransactionStatement.CUST_CONFIRM_STATUS_NOTNEED);
		}
		super.save(businessTransactionStatement);
	}

	@Override
	@Transactional(rollbackFor = { Throwable.class })
	public void delete(BusinessTransactionStatement businessTransactionStatement) {
		super.delete(businessTransactionStatement);
	}

	@Transactional(readOnly = true, rollbackFor = { Throwable.class })
	public BusinessTransactionStatement getTransactionDetail(String inBatch) {
		BusinessTransactionStatement result = new BusinessTransactionStatement();
		// 查询本批次存款信息
		List<DoorOrderDetail> detailByTickerTape = doorOrderDetailDao.getDetailByTickerTape(inBatch);
		if (CollectionUtils.isEmpty(detailByTickerTape)) {
			return null;
		}
		DoorOrderDetail doorOrderDetail = detailByTickerTape.get(0);
		// orderId查询预约主表
		DoorOrderInfo condition = new DoorOrderInfo();
		String orderId = doorOrderDetail.getOrderId();
		condition.setOrderId(orderId);
		DoorOrderInfo infoByCondition = doorOrderInfoDao.getByCondition(condition);
		// 页面表单通过输入批次号返回本批次存款其他信息：
		// 设备、门店、存款日期、开始时间、结束时间、耗时、装运单号、店员、总金额、收款（清机）日期、清分日期、自助金额、强存金额
		result.setInBatch(inBatch);
		result.setEqpid(infoByCondition.getEquipmentId());
		if (equipmentInfoDao.get(infoByCondition.getEquipmentId()) == null) {
			result.setEquipmentInfo(new EquipmentInfo());
		} else {
			result.setEquipmentInfo(equipmentInfoDao.get(infoByCondition.getEquipmentId()));
		}
		result.setDoorId(infoByCondition.getDoorId());
		result.setDoor(officeDao.get(infoByCondition.getDoorId()));
		result.setInDate(doorOrderDetail.getCreateDate());
		result.setStartTime(doorOrderDetail.getStartTime());
		result.setEndTime(doorOrderDetail.getEndTime());
		result.setCostTime(doorOrderDetail.getCostTime());
		result.setRemarks(doorOrderDetail.getRemarks());
		result.setUser(UserUtils.get(doorOrderDetail.getCreateBy().getId()));
		result.setTotalAmount(doorOrderDetail.getAmount());
		// 查找此存款清机时间
		ClearPlanInfo clearPlanInfo = new ClearPlanInfo();
		clearPlanInfo.setPlanId(doorOrderDetail.getOrderId());
		List<ClearPlanInfo> planList = clearPlanInfoDao.findList(clearPlanInfo);
		if (CollectionUtils.isNotEmpty(planList) && planList.size() == 1) {
			// 清机任务的状态是否是完成
			ClearPlanInfo clearPlan = planList.get(0);
			if (Constant.ClearPlanStatus.COMPLETE.equals(clearPlan.getStatus())) {
				result.setBackDate(clearPlan.getUpdateDate());
			}
		}
		// 清分时间
		CheckCashAmount checkStatement = new CheckCashAmount();
		checkStatement.setPackNum(inBatch);
		checkStatement.setEnabledFlag("1");
		List<CheckCashAmount> checkCashAmountList = checkCashAmountDao.findList(checkStatement);
		if (!Collections3.isEmpty(checkCashAmountList)) {
			result.setClearDate(checkCashAmountList.get(0).getUpdateDate());
		}
		// 强存金额01、自助金额02
		List<DoorOrderAmount> amountList = doorOrderAmountDao.getAmountList(doorOrderDetail.getId(), "01", "02");
		if (CollectionUtils.isNotEmpty(amountList)) {
			BigDecimal packAmount = BigDecimal.ZERO;
			BigDecimal cashAmount = BigDecimal.ZERO;
			for (DoorOrderAmount amount : amountList) {
				if (DoorOrderConstant.SaveMethod.CASH_SAVE.equals(amount.getTypeId())) {
					cashAmount = cashAmount.add(new BigDecimal(amount.getDetailAmount()));
				} else if (DoorOrderConstant.SaveMethod.BAG_SAVE.equals(amount.getTypeId())) {
					packAmount = packAmount.add(new BigDecimal(amount.getDetailAmount()));
				}
			}
			result.setPackAmount(packAmount.toString());
			result.setCashAmount(cashAmount.toString());
		}
		return result;
	}

	@Transactional(readOnly = true, rollbackFor = { Throwable.class })
	public Page<BusinessTransactionStatement> getTransactionDetailPage(Page<BusinessTransactionStatement> page,
			BusinessTransactionStatement businessTransactionStatement) {
		businessTransactionStatement.getSqlMap().put("dsf",
				dataScopeFilter(businessTransactionStatement.getCurrentUser(), "o", null));
		businessTransactionStatement.setPage(page);
		page.setList(getTransactionDetailList(businessTransactionStatement));
		return page;
	}

	@Transactional(readOnly = true, rollbackFor = { Throwable.class })
	public List<BusinessTransactionStatement> getTransactionDetailList(
			BusinessTransactionStatement businessTransactionStatement) {
		return dao.getTransactionList(businessTransactionStatement);
	}

	@Transactional(rollbackFor = { Throwable.class })
	public int confirm(BusinessTransactionStatement businessTransactionStatement) {
		return dao.confirm(businessTransactionStatement);
	}

}