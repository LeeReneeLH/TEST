package com.coffer.businesses.modules.doorOrder.v01.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.DoorCommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.dao.DepositErrorDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.DepositError;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 存款差错管理Service
 * 
 * @author ZXK
 * @version 2019年7月17日
 */
@Service
@Transactional(readOnly = true)
public class DepositErrorService extends CrudService<DepositErrorDao, DepositError> {

	@Autowired
	private DepositErrorDao depositErrorDao;

	public DepositError get(String id) {
		return super.get(id);
	}

	public List<DepositError> findList(DepositError depositError) {
		return super.findList(depositError);
	}

	/**
	 * 获取存款差错信息列表
	 * 
	 * @author ZXK
	 * @version 2019年7月17日
	 * @param page
	 * @param depositError
	 * @return
	 */
	public Page<DepositError> findPage(Page<DepositError> page, DepositError depositError) {
		depositError.getSqlMap().put("dsf", dataScopeFilter(depositError.getCurrentUser(), "o1", null));
		return super.findPage(page, depositError);
	}

	/**
	 * 新增存款差错信息
	 * 
	 * @author ZXK
	 * @version 2019年7月17日
	 * @param depositError
	 * @return
	 */
	@Transactional(readOnly = false)
	public void save(DepositError depositError) {
		// 业务流水，主键
//		String orderId = BusinessUtils.getNewBusinessNo(
//				DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE, UserUtils
//						.getUser().getOffice());
//		depositError.setOrderId(orderId);
		String orderId = depositError.getOrderId();

		User use = UserUtils.getUser();
		// 登记人ID
		depositError.setRegisterId(use.getId());
		// 登记人姓名
		depositError.setRegisterName(use.getName());

		Office office = new Office();
		office.setId(use.getOffice().getId());
		office.setName(use.getOffice().getName());
		// 差错登记机构
		depositError.setOffice(office);
		// 门店名称
		depositError.setDoorName(StoreCommonUtils.getOfficeById(depositError.getDoorId()).getName());
		// 状态类型(1,登记 2,冲正)
		depositError.setStatus(DoorOrderConstant.StatusType.CREATE);
		depositError.preInsert();
		int i = depositErrorDao.insert(depositError);
		if (i > 0) {
			// 将存款差错登记关联到账务(按照明细循环冲正)
			DoorCenterAccountsMain doorCenterAccountsMain = new DoorCenterAccountsMain();
			// 设置关联账务流水Id
			doorCenterAccountsMain.setBusinessId(orderId);
			// 设置客户Id
			doorCenterAccountsMain.setClientId(depositError.getDoorId());
			// 设置业务类型
			doorCenterAccountsMain.setBusinessType(DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE);
			// 设置出库或入库金额(长款[2]==入库 短款[3]==出库)
			if (depositError.getErrorType().equals(ClearConstant.ErrorType.SHORT_CURRENCY)) {
				// 短款
				doorCenterAccountsMain.setOutAmount(new BigDecimal(depositError.getAmount()));
				doorCenterAccountsMain.setInoutType(DoorOrderConstant.AccountsInoutType.ACCOUNTS_OUT);
			} else if (depositError.getErrorType().equals(ClearConstant.ErrorType.LONG_CURRENCY)) {
				// 长款
				doorCenterAccountsMain.setInAmount(new BigDecimal(depositError.getAmount()));
				doorCenterAccountsMain.setInoutType(DoorOrderConstant.AccountsInoutType.ACCOUNTS_IN);
			}
			// 设置账务发生机构
			User userInfo = UserUtils.getUser();
			doorCenterAccountsMain.setRofficeId(userInfo.getOffice().getId());
			// 设置账务所在机构
			doorCenterAccountsMain.setAofficeId(depositError.getDoorId());
			// 设置业务状态
			doorCenterAccountsMain.setBusinessStatus(DoorOrderConstant.StatusType.CREATE);
			// 设置该笔差错是否已经处理
			doorCenterAccountsMain.setIsTakeUp(DoorOrderConstant.ErrorIsTakeUp.NOTTAKEUP);
			DoorCommonUtils.insertAccounts(doorCenterAccountsMain);

		}
	}

	/**
	 * 存款差错 将状态改为冲正(并冲正账目表)
	 * 
	 * @author ZXK
	 * @version 2019年7月17日
	 * @param depositError
	 * @return
	 */
	@Transactional(readOnly = false)
	public void reversal(DepositError depositError) {
		// 防止重复冲正
		if (depositError.getStatus().equals(DoorOrderConstant.StatusType.DELETE)) {
			throw new BusinessException("message.I75002", "", new String[] { depositError.getOrderId() });
		}
		// 冲正 修改差错表状态
		depositError.setStatus(DoorOrderConstant.StatusType.DELETE);
		depositError.preUpdate();
		// 门店名称
		depositError.setDoorName(StoreCommonUtils.getOfficeById(depositError.getDoorId()).getName());
		int i = depositErrorDao.update(depositError);
		if (i > 0) {
			// 冲正账目表数据
			// 将冲正关联到账务(按照明细循环冲正)
			DoorCenterAccountsMain doorCenterAccountsMain = new DoorCenterAccountsMain();
			// 设置关联账务流水Id
			doorCenterAccountsMain.setBusinessId(depositError.getOrderId());
			// 设置客户Id
			doorCenterAccountsMain.setClientId(depositError.getDoorId());
			// 设置业务类型
			doorCenterAccountsMain.setBusinessType(DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE);
			// 冲正金额(长款[2]==入库 短款[3]==出库)
			if (depositError.getErrorType().equals(ClearConstant.ErrorType.SHORT_CURRENCY)) {
				// 如果是短款,则冲正为入库
				doorCenterAccountsMain.setInAmount(new BigDecimal(depositError.getAmount()));
				doorCenterAccountsMain.setInoutType(DoorOrderConstant.AccountsInoutType.ACCOUNTS_IN);
			} else if (depositError.getErrorType().equals(ClearConstant.ErrorType.LONG_CURRENCY)) {
				// 如果是长款,则冲正为出库
				doorCenterAccountsMain.setOutAmount(new BigDecimal(depositError.getAmount()));
				doorCenterAccountsMain.setInoutType(DoorOrderConstant.AccountsInoutType.ACCOUNTS_OUT);
			}
			// 设置账务发生机构
			User userInfo = UserUtils.getUser();
			doorCenterAccountsMain.setRofficeId(userInfo.getOffice().getId());
			// 设置账务所在机构
			doorCenterAccountsMain.setAofficeId(depositError.getDoorId());
			// 设置业务状态
			doorCenterAccountsMain.setBusinessStatus(DoorOrderConstant.StatusType.DELETE);
			// 设置该笔差错是否已经处理
			doorCenterAccountsMain.setIsTakeUp(DoorOrderConstant.ErrorIsTakeUp.NOTTAKEUP);
			DoorCommonUtils.insertAccounts(doorCenterAccountsMain);
		}
	}

	/**
	 * 判断未冲正的带差错存款单号是否存在
	 * 
	 * @author GJ
	 * @version 2019年10月24日
	 * @param orderId
	 * @return
	 */
	public boolean isOrderExists(String orderId) {
		if (depositErrorDao.isOrderExists(orderId) == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 根据单号去查找对应单号的门店，保证二者一致性
	 * 
	 * @author GJ
	 * @version 2019年10月24日
	 * @param orderId
	 * @return
	 */
	public String getDoorIdByOrderId(String orderId) {
		return depositErrorDao.getDoorIdByOrderId(orderId);
	}

	/**
	 * 在单号未冲正的情况下保证该单号下仅有一条差错记录处于登记状态
	 * 
	 * @author GJ
	 * @version 2019年10月24日
	 * @param orderId
	 * @return
	 */
	public Integer getLoginCount(String orderId) {
		return depositErrorDao.getLoginCount(orderId);
	}

	/**
	 * 登记差错时，短款金额不能多于存款金额
	 * 
	 * @author GJ
	 * @version 2019年10月24日
	 * @param orderId
	 * @return
	 */
	public BigDecimal isMoreThanSave(String orderId) {
		return new BigDecimal(depositErrorDao.isMoreThanSave(orderId));
	}
}
