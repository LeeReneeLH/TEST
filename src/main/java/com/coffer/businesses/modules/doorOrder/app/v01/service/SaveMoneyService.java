package com.coffer.businesses.modules.doorOrder.app.v01.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.collection.v03.entity.TaskConfirm;
import com.coffer.businesses.modules.collection.v03.entity.TaskDown;
import com.coffer.businesses.modules.collection.v03.service.CheckCashService;
import com.coffer.businesses.modules.collection.v03.service.TaskConfirmService;
import com.coffer.businesses.modules.collection.v03.service.TaskDownService;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.DoorCommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.store.v01.dao.StoDictDao;
import com.coffer.businesses.modules.weChat.WeChatConstant;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderAmountDao;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderDetailDao;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderInfoDao;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderAmount;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderDetail;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderInfo;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Maps;

@Service
public class SaveMoneyService extends CrudService<DoorOrderInfoDao, DoorOrderInfo> {

	// 门店预约DAO接口
	@Autowired
	private DoorOrderDetailDao doorOrderDetailDao;

	@Autowired
	private DoorOrderAmountDao doorOrderAmountDao;

	@Autowired
	private StoDictDao stoDictDao;

	@Autowired
	private TaskConfirmService taskConfirmService;

	@Autowired
	private TaskDownService taskDownService;

	@Autowired
	private CheckCashService checkCashService;

	@Autowired
	private OfficeDao officeDao;

	/**
	 * 门店存款登记
	 *
	 * @author GJ
	 * @version 2020年1月6日
	 * @param doorOrderInfo
	 * @return
	 */
	@Transactional(readOnly = false)
	public synchronized void save(DoorOrderInfo doorOrderInfo) {
		// 总笔数
		int intTotalCount = 0;
		// 设置状态（登记）
		doorOrderInfo.setStatus(DoorOrderConstant.Status.REGISTER);
		// 登录用户
		User currentUser = UserUtils.get(doorOrderInfo.getUserId());
		// 设置预约单号
		if (StringUtils.isBlank(doorOrderInfo.getId())) {
			String orderId = BusinessUtils.getNewBusinessNo(DoorOrderConstant.BusinessType.TRADITIONAL_SAVE,
					currentUser.getOffice());
			doorOrderInfo.setOrderId(orderId);

		}
		// 判断申请方式
		// if
		// (!DoorOrderConstant.MethodType.METHOD_WECHAT.equals(doorOrderInfo.getMethod())
		// &&
		// !DoorOrderConstant.MethodType.METHOD_PDA.equals(doorOrderInfo.getMethod()))
		// {
		// // 设置申请方式为PC端
		// doorOrderInfo.setMethod(DoorOrderConstant.MethodType.METHOD_PC);
		// // PC端直接设置为已确认
		// doorOrderInfo.setStatus(DoorOrderConstant.status.CONFIRM);
		// }
		// 设置申请方式为PC端
		doorOrderInfo.setMethod(DoorOrderConstant.MethodType.METHOD_WECHAT);
		// PC端直接设置为已确认
		doorOrderInfo.setStatus(DoorOrderConstant.Status.CONFIRM);
		// 金额格式化
		doorOrderInfo.setAmount(doorOrderInfo.getAmount().replaceAll(",", ""));
		// 当前时间
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		// 设置申请日期
		if (StringUtils.isBlank(doorOrderInfo.getOrderDate())) {
			doorOrderInfo.setOrderDate(dateString);
			doorOrderInfo.setUpdateDate(currentTime);
		}
		doorOrderInfo.preInsert();
		// 预约明细删除
		DoorOrderDetail doorOrderDetailDel = new DoorOrderDetail();
		doorOrderDetailDel.setOrderId(doorOrderInfo.getOrderId());
		doorOrderDetailDao.deleteByOrderId(doorOrderDetailDel);
		// 金额列表
		String[] amountList = doorOrderInfo.getAmountList().split(",", -1);
		// 业务类型列表
		String[] busTypeList = doorOrderInfo.getBusTypeList().split(",", -1);
		// 凭条
		String tickertape = BusinessUtils.getNewBusinessNo(WeChatConstant.BusinessType.DOOR_ORDER,
				currentUser.getOffice());

		// 存款备注列表 gzd 2019-12-16
		String[] remarksList = doorOrderInfo.getRemarksList().split(",", -1);
		// 金额明细列表
		// String[] detailList = doorOrderInfo.getDetailList().split(",", -1);
		// 凭条对应明细编号
		Map<String, String> detailMap = Maps.newHashMap();
		// 重新录入明细
		DoorOrderDetail doorOrderDetail;
		// 包号
		String rfid = doorOrderInfo.getRfid();
		// 行数
		int rowCnt = 0;
		String detailId = IdGen.uuid();
		String detailAmont = "";
		// for (int i = 0; i < amountList.length; i++) {
		// 明细金额
		String amount = amountList[1];
		// 明细业务类型
		String busType = busTypeList[1];
		// 明细凭条
		// String tickertape = tickertapeList[i];
		// 明细存款备注 gzd 2019-12-16
		String remarks = remarksList[1];
		detailAmont = amount;
		if (StringUtils.isNotBlank(amount)) {
			doorOrderDetail = new DoorOrderDetail();
			// 设置金额
			doorOrderDetail.setAmount(amount);
			// 设置包号
			doorOrderDetail.setRfid(rfid);
			// 设置凭条
			doorOrderDetail.setTickertape(tickertape);
			// 设置 业务类型
			doorOrderDetail.setBusinessType(busType);
			// 设置 存款备注 gzd 2019-12-16
			doorOrderDetail.setRemarks(remarks);
			// 设置预约单号
			doorOrderDetail.setOrderId(doorOrderInfo.getOrderId());
			// 行数编号
			rowCnt++;
			doorOrderDetail.setDetailId(String.valueOf(rowCnt));
			doorOrderDetail.setCreateBy(doorOrderInfo.getCreateBy());
			doorOrderDetail.setUpdateBy(doorOrderInfo.getUpdateBy());
			doorOrderDetail.setOrderDate(dateString);
			doorOrderDetail.setId(detailId);
			doorOrderDetail.setCreateDate(doorOrderInfo.getCreateDate());
			doorOrderDetail.setUpdateDate(doorOrderInfo.getUpdateDate());
			logger.debug("金额：" + amount + "--------" + "包号：" + rfid + "--------" + "凭条号：" + tickertape + "--------"
					+ "业务类型：" + busType + "--------" + "存款备注：" + remarks + "--------" + "预约单号："
					+ doorOrderInfo.getOrderId() + "--------" + "创建人：" + doorOrderInfo.getCreateBy() + "--------"
					+ "更新人：" + doorOrderInfo.getUpdateBy() + "--------" + "日期：" + dateString + "--------" + "明细id："
					+ detailId + "--------" + "创建时间：" + doorOrderInfo.getCreateDate() + "--------" + "更新时间："
					+ doorOrderInfo.getUpdateDate());
			doorOrderDetailDao.insert(doorOrderDetail);
			// 明细凭条对应编号
			detailMap.put(doorOrderDetail.getTickertape(), doorOrderDetail.getId());
			// 增加笔数
			intTotalCount = intTotalCount + 1;
			// 将流水关联到账务(按照明细循环插入)
			DoorCenterAccountsMain doorCenterAccountsMain = new DoorCenterAccountsMain();
			// 设置关联账务流水Id
			doorCenterAccountsMain.setBusinessId(doorOrderInfo.getOrderId());
			// 设置客户Id
			doorCenterAccountsMain.setClientId(doorOrderInfo.getDoorId());
			// 设置业务类型
			doorCenterAccountsMain.setBusinessType(DoorOrderConstant.BusinessType.TRADITIONAL_SAVE);
			// 设置入库金额
			doorCenterAccountsMain.setInAmount(new BigDecimal(doorOrderDetail.getAmount()));
			// 设置出入库类型
			doorCenterAccountsMain.setInoutType(DoorOrderConstant.AccountsInoutType.ACCOUNTS_IN);
			// 设置账务发生机构
			User userInfo = currentUser;
			doorCenterAccountsMain.setRofficeId(getClearCenter(userInfo.getOffice().getId()).getId());
			// 设置账务所在机构
			doorCenterAccountsMain.setAofficeId(doorOrderInfo.getDoorId());
			// 设置业务状态
			doorCenterAccountsMain.setBusinessStatus(DoorOrderConstant.StatusType.CREATE);
			// 设置账务代付状态(未代付)
			doorCenterAccountsMain.setStatus(DoorOrderConstant.AccountPaidStatus.NOTHASPAID);
			// 存款备注 gzd 2019-12-16
			doorCenterAccountsMain.setSevenCode(remarks);
			// 创建人信息设置
			doorCenterAccountsMain.setCreateBy(userInfo);
			doorCenterAccountsMain.setCreateName(userInfo.getName());
			doorCenterAccountsMain.setUpdateBy(userInfo);
			doorCenterAccountsMain.setUpdateName(userInfo.getName());
			// 设置凭条
			doorCenterAccountsMain.setTickertape(doorOrderDetail.getTickertape());
			DoorCommonUtils.insertAccounts(doorCenterAccountsMain);
		}
		// 分配状态（2：已确认）
		doorOrderInfo.setAllotStatus(DoorOrderConstant.AllotStatus.CONFIRMED);
		// 总笔数
		doorOrderInfo.setTotalCount(String.valueOf(intTotalCount));
		dao.insert(doorOrderInfo);
		// 金额明细
		// for (int i = 0; i < detailList.length; i++) {
		// 金额明细内容
		// String[] detail = detailList[i].split("_", -1);
		// 物品字典
		// StoDict stoDict = stoDictDao.get(detail[2]);
		DoorOrderAmount doorOrderAmount = new DoorOrderAmount();
		// 主键
		doorOrderAmount.setId(IdGen.uuid());
		// 存款类型
		doorOrderAmount.setTypeId(DoorOrderConstant.SaveMethod.WECHAT_SAVE);
		// 明细主键
		// doorOrderAmount.setDetailId(detailMap.get(detail[0]));
		doorOrderAmount.setDetailId(detailId);
		// 面值
		// doorOrderAmount.setDenomination(StringUtils.isBlank(detail[2]) ? null
		// : detail[2]);
		doorOrderAmount.setDenomination(null);
		// 张数
		// doorOrderAmount.setCountZhang(StringUtils.isBlank(detail[3]) ? null :
		// detail[3]);
		doorOrderAmount.setCountZhang(null);
		// 金额
		// if (StringUtils.isNotBlank(detail[4])) {
		// doorOrderAmount.setDetailAmount(detail[4]);
		doorOrderAmount.setDetailAmount(detailAmont);
		// } else {
		// doorOrderAmount
		// .setDetailAmount(String.valueOf(stoDict.getUnitVal().multiply(new
		// BigDecimal(detail[3]))));
		// }
		// 序号
		// doorOrderAmount.setRowNo(i + 1);
		doorOrderAmount.setRowNo(1);
		// 币种
		doorOrderAmount.setCurrency("1");
		doorOrderAmountDao.insert(doorOrderAmount);
		// }
		// 页面存款时将拆箱主表做成
		// 根据款袋编号获取任务
		TaskDown taskDown = new TaskDown();
		// 清分机构
		taskDown.setOffice(getClearCenter(currentUser.getOffice().getId()));
		// 款袋编号
		taskDown.setRfid(rfid);
		// 设置状态（登记、确认）
		taskDown.setStatusList(
				Arrays.asList(new String[] { DoorOrderConstant.Status.REGISTER, DoorOrderConstant.Status.CONFIRM }));
		// 延长日
		taskDown.setExtendeDay(WeChatConstant.EXTENDE_DAY);
		// 任务列表
		List<TaskDown> taskDownList = taskDownService.findList(taskDown);
		taskDown = taskDownList.get(0);
		TaskConfirm taskConfirm = taskConfirmService.get(taskDown.getId());
		checkCashService.createCheckCashForPage(taskConfirm, currentUser, new Date(),
				getClearCenter(currentUser.getOffice().getId()));
	}

	/**
	 * 获取清分机构
	 */
	public Office getClearCenter(String officeId) {
		// 根据id获取所有父机构
		Office office = officeDao.get(officeId);
		String parentIds = office.getParentIds();
		String[] parentIdArray = parentIds.split(",", -1);
		List<String> parentIdList = new ArrayList<String>();
		for (String parentId : parentIdArray) {
			if (parentId != null && !parentId.equals("")) {
				parentIdList.add(parentId);
			}
		}
		for (String parentId : parentIdList) {
			if (officeDao.get(parentId) != null
					&& officeDao.get(parentId).getType().equals(Constant.OfficeType.CENTRAL_BANK)) {
				return officeDao.getClearCenterByParentId(parentId);
			}
		}
		return officeDao.get(officeId);
	}

}
