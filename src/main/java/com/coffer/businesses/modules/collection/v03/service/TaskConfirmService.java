package com.coffer.businesses.modules.collection.v03.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.collection.CollectionConstant;
import com.coffer.businesses.modules.collection.v03.dao.CheckCashAmountDao;
import com.coffer.businesses.modules.collection.v03.dao.CheckCashMainDao;
import com.coffer.businesses.modules.collection.v03.dao.TaskConfirmDao;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashAmount;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashMain;
import com.coffer.businesses.modules.collection.v03.entity.TaskConfirm;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderDetailDao;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderDetail;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;

/**
 * 门店预约Service
 * 
 * @author wl
 * @version 2017-02-13
 */
@Service
@Transactional(readOnly = true)
public class TaskConfirmService extends CrudService<TaskConfirmDao, TaskConfirm> {

	@Autowired
	private TaskConfirmDao taskConfirmDao;
	@Autowired
	private CheckCashMainDao checkCashMainDao;
	@Autowired
	private CheckCashAmountDao checkCashAmountDao;

	@Autowired
	private DoorOrderDetailDao doorOrderDetailDao;

	public TaskConfirm get(String id) {
		TaskConfirm taskConfirm = super.get(id);
		return taskConfirm;
	}

	public List<TaskConfirm> findList(TaskConfirm taskConfirm) {
		return super.findList(taskConfirm);
	}

	public Page<TaskConfirm> findPage(Page<TaskConfirm> page, TaskConfirm taskConfirm) {
		// 数据范围过滤
		taskConfirm.getSqlMap().put("dsf", dataScopeFilter(taskConfirm.getCurrentUser(), "o", null));
		// 查询条件： 开始时间
		if (taskConfirm.getCreateTimeStart() != null) {
			taskConfirm.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(taskConfirm.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (taskConfirm.getCreateTimeEnd() != null) {
			taskConfirm.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(taskConfirm.getCreateTimeEnd())));
		}
		return super.findPage(page, taskConfirm);
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2015年10月10日 任务驳回
	 * @param TaskConfirm
	 * @return
	 */
	@Transactional(readOnly = false)
	public void reject(TaskConfirm taskConfirm) {
		// 分配状态（0：未分配；1：已分配；2：已确认；3：驳回）
		taskConfirm.setAllotStatus(CollectionConstant.allotStatusType.CONFIRM_NO);
		taskConfirm.preUpdate();
		taskConfirmDao.updateAllotStatus(taskConfirm);
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2015年10月10日 任务确认
	 * @param TaskConfirm
	 * @return
	 */
	@Transactional(readOnly = false)
	public void confirm(TaskConfirm taskConfirm) {

		// 款箱拆箱主表的作成
		TaskConfirm doorOrderInfo = new TaskConfirm();
		CheckCashMain checkCashMain = new CheckCashMain();
		doorOrderInfo = taskConfirmDao.get(taskConfirm.getId());
		checkCashMain.setOutNo(doorOrderInfo.getOrderId()); // 单号
		checkCashMain.setCustNo(doorOrderInfo.getDoorId()); // 门店ID
		checkCashMain.setCustName(doorOrderInfo.getDoorName()); // 门店名称
		checkCashMain.setInputAmount(doorOrderInfo.getAmount()); // 拆箱总金额
		checkCashMain.setCheckAmount("0"); // 清点总金额
		checkCashMain.setDiffAmount("0"); // 差额
		checkCashMain.setBoxCount(doorOrderInfo.getTotalCount()); // 总笔数
		checkCashMain.setRegDate(new Date()); // 登记日期
		checkCashMain.setDataFlag(CollectionConstant.dataFlagType.ALLOT); // 数据区分（0：录入
																			// 1：分配）
		checkCashMain.preInsert();
		checkCashMainDao.insert(checkCashMain);

		// 款箱拆箱每笔明细表的作成
		List<DoorOrderDetail> orderDetailList = null;
		DoorOrderDetail doorOrderDetail = new DoorOrderDetail();
		doorOrderDetail.setOrderId(doorOrderInfo.getOrderId());
		orderDetailList = doorOrderDetailDao.findList(doorOrderDetail);

		for (DoorOrderDetail itemData : orderDetailList) {
			CheckCashAmount checkCashAmount = new CheckCashAmount();
			checkCashAmount.setOutNo(itemData.getOrderId()); // 单号
			checkCashAmount.setOutRowNo(itemData.getDetailId()); // 明细序号
			checkCashAmount.setInputAmount(itemData.getAmount()); // 录入金额
			checkCashAmount.setCheckAmount("0"); // 清点金额
			checkCashAmount.setDiffAmount("0"); // 差额
			checkCashAmount.setPackNum(itemData.getRfid()); // 包号
			checkCashAmount.setDataFlag(CollectionConstant.dataFlagType.ALLOT); // 数据区分（0：录入
																				// 1：分配）
			checkCashAmount.setEnabledFlag(CollectionConstant.enabledFlagType.NO); // 启用标识（1：启用
																					// 0
																					// ：未启用）

			checkCashAmount.preInsert();
			checkCashAmountDao.insert(checkCashAmount);
		}

		// 分配任务状态的更新
		// 分配状态（0：未分配；1：已分配；2：已确认；3：驳回）
		taskConfirm.setAllotStatus(CollectionConstant.allotStatusType.CONFIRM_OK);
		taskConfirm.preUpdate();
		taskConfirmDao.updateAllotStatus(taskConfirm);
		taskConfirm.setStatus(CollectionConstant.statusType.CLEAR); // 状态：清分
		taskConfirmDao.updateStatus(taskConfirm);

	}

	/**
	 * 任务确认（接口用）
	 *
	 * @author XL
	 * @version 2019年7月12日
	 * @param taskConfirm
	 * @param user
	 * @param date
	 * @param office
	 */
	@Transactional(readOnly = false)
	public void confirmForInterFace(TaskConfirm taskConfirm, User user, Date date, Office office) {
		// 款箱拆箱主表的作成
		TaskConfirm doorOrderInfo = new TaskConfirm();
		CheckCashMain checkCashMain = new CheckCashMain();
		doorOrderInfo = taskConfirmDao.get(taskConfirm.getId());
		// 预约单号
		checkCashMain.setOutNo(doorOrderInfo.getOrderId());
		// 包号
		checkCashMain.setRfid(doorOrderInfo.getRfid());
		// 门店编号
		checkCashMain.setCustNo(doorOrderInfo.getDoorId());
		// 门店名称
		checkCashMain.setCustName(doorOrderInfo.getDoorName());
		// 拆箱总金额
		checkCashMain.setInputAmount(doorOrderInfo.getAmount());
		// 清点总金额
		checkCashMain.setCheckAmount("0");
		// 差额
		checkCashMain.setDiffAmount("0");
		// 总笔数
		checkCashMain.setBoxCount(doorOrderInfo.getTotalCount());
		// 登记日期
		checkCashMain.setRegDate(date);
		// 数据区分（1：分配）
		checkCashMain.setDataFlag(CollectionConstant.dataFlagType.ALLOT);
		checkCashMain.setCreateBy(user);
		checkCashMain.setCreateName(user.getName());
		checkCashMain.setCreateDate(date);
		checkCashMain.setUpdateBy(user);
		checkCashMain.setUpdateName(user.getName());
		checkCashMain.setUpdateDate(date);
		checkCashMain.setId(IdGen.uuid());
		checkCashMain.setOffice(office);
		checkCashMainDao.insert(checkCashMain);
		// 款箱拆箱每笔明细表的作成
		List<DoorOrderDetail> orderDetailList = null;
		DoorOrderDetail doorOrderDetail = new DoorOrderDetail();
		// 预约单号
		doorOrderDetail.setOrderId(doorOrderInfo.getOrderId());
		// 明细列表
		orderDetailList = doorOrderDetailDao.findList(doorOrderDetail);
		for (DoorOrderDetail itemData : orderDetailList) {
			CheckCashAmount checkCashAmount = new CheckCashAmount();
			// 预约单号
			checkCashAmount.setOutNo(itemData.getOrderId());
			// 明细序号
			checkCashAmount.setOutRowNo(itemData.getDetailId());
			// 录入金额
			checkCashAmount.setInputAmount(itemData.getAmount());
			// 清点金额
			checkCashAmount.setCheckAmount("0");
			// 差额
			checkCashAmount.setDiffAmount("0");
			// 凭条
			checkCashAmount.setPackNum(itemData.getTickertape());
			// 数据区分（1：分配）
			checkCashAmount.setDataFlag(CollectionConstant.dataFlagType.ALLOT);
			// 启用标识（0：未启用）
			checkCashAmount.setEnabledFlag(CollectionConstant.enabledFlagType.NO);
			checkCashAmount.setCreateBy(user);
			checkCashAmount.setCreateName(user.getName());
			checkCashAmount.setCreateDate(date);
			checkCashAmount.setUpdateBy(user);
			checkCashAmount.setUpdateName(user.getName());
			checkCashAmount.setUpdateDate(date);
			checkCashAmount.setId(IdGen.uuid());
			checkCashAmountDao.insert(checkCashAmount);
		}
		// 分配任务状态的更新
		// 分配状态（2：已确认）
		taskConfirm.setAllotStatus(CollectionConstant.allotStatusType.CONFIRM_OK);
		taskConfirm.setUpdateBy(user);
		taskConfirm.setUpdateName(user.getName());
		taskConfirm.setUpdateDate(date);
		taskConfirmDao.updateAllotStatus(taskConfirm);
		// 状态：清分
		taskConfirm.setStatus(CollectionConstant.statusType.CLEAR);
		taskConfirmDao.updateStatus(taskConfirm);
	}

}