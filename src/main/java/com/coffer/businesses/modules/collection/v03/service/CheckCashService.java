package com.coffer.businesses.modules.collection.v03.service;

import java.math.BigDecimal;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.entity.DenominationInfo;
import com.coffer.businesses.modules.collection.CollectionConstant;
import com.coffer.businesses.modules.collection.v03.dao.CheckCashAmountDao;
import com.coffer.businesses.modules.collection.v03.dao.CheckCashDetailDao;
import com.coffer.businesses.modules.collection.v03.dao.CheckCashMainDao;
import com.coffer.businesses.modules.collection.v03.dao.TaskConfirmDao;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashAmount;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashConfirm;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashDetail;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashMain;
import com.coffer.businesses.modules.collection.v03.entity.TaskConfirm;
import com.coffer.businesses.modules.collection.v03.entity.TaskDown;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.DoorCommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorErrorInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearPlanInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.DepositError;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorErrorInfo;
import com.coffer.businesses.modules.doorOrder.v01.service.DepositErrorService;
import com.coffer.businesses.modules.store.v01.dao.StoDictDao;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.businesses.modules.weChat.WeChatConstant;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderDetailDao;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderDetail;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.dao.UserDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 款箱拆箱Service
 * 
 * @author wl
 * @version 2017-02-13
 */
@Service
@Transactional(readOnly = true)
public class CheckCashService extends CrudService<CheckCashMainDao, CheckCashMain> {
	@Autowired
	private CheckCashAmountDao checkCashAmountDao;

	@Autowired
	private CheckCashDetailDao checkCashDetailDao;
	@Autowired
	private StoDictDao stoDictDao;
	@Autowired
	private CheckCashMainDao checkCashMainDao;

	@Autowired
	private OfficeDao officeDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private DoorErrorInfoDao doorErrorInfoDao;
	@Autowired
	private TaskConfirmDao taskConfirmDao;
	@Autowired
	private DoorOrderDetailDao doorOrderDetailDao;
	@Autowired
	private TaskDownService taskDownService;
	@Autowired
	private TaskConfirmService taskConfirmService;
	@Autowired
	private DepositErrorService depositErrorService;

	public CheckCashMain get(String id) {
		CheckCashMain checkCashMain = super.get(id);
		return checkCashMain;
	}

	/**
	 * 
	 * @author wanglin
	 * @version 一览页面数据
	 * @param CheckCashMain
	 * @return
	 */
	public List<CheckCashMain> findList(CheckCashMain checkCashMain) {
		return super.findList(checkCashMain);
	}

	public Page<CheckCashMain> findPage(Page<CheckCashMain> page, CheckCashMain checkCashMain) {
		// 数据范围过滤
		checkCashMain.getSqlMap().put("dsf", dataScopeFilter(checkCashMain.getCurrentUser(), "o", null));
		Page<CheckCashMain> pageData = super.findPage(page, checkCashMain);
		// 未拆箱数的设定
		List<CheckCashMain> mainList = page.getList();
		// GJ 添加款袋使用时间 start
		String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		// GJ 添加款袋使用时间 end
		// 存款差错，实际存款金额设定
		for (CheckCashMain check : mainList) {
			if (StringUtils.isNotBlank(check.getOutNo())) {
				check.setNoBoxCount(String.valueOf(checkCashAmountDao.findNoBoxCount(check.getOutNo())));
			}
			ClearPlanInfo clearPlanInfo = new ClearPlanInfo();
			if (StringUtils.isNotBlank(check.getEquipmentId()) && check.getCurrentClearDate() != null) {
				clearPlanInfo.setEquipmentId(check.getEquipmentId());
				clearPlanInfo.setUpdateDate(check.getCurrentClearDate());
				ClearPlanInfo clearPlanInfoRes = checkCashMainDao.getLastClearDate(clearPlanInfo);
				String distanceTime = null;
				if (clearPlanInfoRes != null) {
					distanceTime = DateUtils.getDistanceTime(sdf.format(check.getCurrentClearDate()),
							sdf.format(clearPlanInfoRes.getLastClearDate()));
					check.setPackNumUseTime(distanceTime);
				}
			}
			check = this.getDepositErrorByOutNo(check);
		}
		return pageData;
	}

	/**
	 * 
	 * Title: findPage1
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: lihe
	 * @param page
	 * @param checkCashMain
	 * @return Page<CheckCashMain> 返回类型
	 */
	public Page<CheckCashMain> findPage1(Page<CheckCashMain> page, CheckCashMain checkCashMain) {
		// 数据范围过滤
		checkCashMain.getSqlMap().put("dsf", dataScopeFilter(checkCashMain.getCurrentUser(), "o", null));
		Page<CheckCashMain> pageData = super.findPage(page, checkCashMain);
		// 未拆箱数的设定
		List<CheckCashMain> mainList = page.getList();
		// GJ 添加款袋使用时间 start
		String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		// GJ 添加款袋使用时间 end
		// 存款差错，实际存款金额设定
		for (CheckCashMain check : mainList) {
			if (StringUtils.isNotBlank(check.getOutNo())) {
				check.setNoBoxCount(String.valueOf(checkCashAmountDao.findNoBoxCount(check.getOutNo())));
			}
			ClearPlanInfo clearPlanInfo = new ClearPlanInfo();
			if (StringUtils.isNotBlank(check.getEquipmentId()) && check.getCurrentClearDate() != null) {
				clearPlanInfo.setEquipmentId(check.getEquipmentId());
				clearPlanInfo.setUpdateDate(check.getCurrentClearDate());
				ClearPlanInfo clearPlanInfoRes = checkCashMainDao.getLastClearDate(clearPlanInfo);
				String distanceTime = null;
				if (clearPlanInfoRes != null) {
					distanceTime = DateUtils.getDistanceTime(sdf.format(check.getCurrentClearDate()),
							sdf.format(clearPlanInfoRes.getLastClearDate()));
					check.setPackNumUseTime(distanceTime);
				}
			}
			check = this.getDepositErrorByOutNo(check);
		}
		return pageData;
	}

	/**
	 * 
	 * @author wanglin
	 * @version 每笔金额列表的取得
	 * 
	 * @param checkCashMain
	 */
	public List<CheckCashAmount> findAmountList(CheckCashMain checkCashMain) {
		CheckCashAmount checkCashAmount = new CheckCashAmount();
		checkCashAmount.setOutNo(checkCashMain.getOutNo());
		return checkCashAmountDao.findList(checkCashAmount);
	}

	/**
	 * 
	 * @author wanglin
	 * @version 每笔金额面值列表的取得
	 * 
	 * @param checkCashMain
	 */
	public List<CheckCashDetail> findAmountDetailList(CheckCashMain checkCashMain) {
		CheckCashDetail checkCashDetail = new CheckCashDetail();
		checkCashDetail.setOutNo(checkCashMain.getOutNo());
		return checkCashDetailDao.findList(checkCashDetail);
	}

	/**
	 * 
	 * @author wanglin
	 * @version 整单删除
	 * 
	 * @param checkCashMain
	 */
	@Transactional(readOnly = false)
	public void deleteMain(CheckCashMain checkCashMain) {
		CheckCashAmount checkCashAmount = new CheckCashAmount();
		try {
			checkCashAmount.setOutNo(checkCashMain.getOutNo());
			checkCashAmount.preUpdate();
			checkCashMainDao.logicDelete(checkCashAmount); // 款箱拆箱表
			checkCashAmountDao.logicDelete(checkCashAmount); // 每笔金额表
			checkCashDetailDao.logicDelete(checkCashAmount); // 每笔金额面值表
			// ----------差错管理------------
			DoorErrorInfo doorErrorInfo = new DoorErrorInfo();
			// 流水号
			doorErrorInfo.setBusinessId(checkCashMain.getOutNo());
			// 查询差错列表
			List<DoorErrorInfo> doorErrorInfoList = doorErrorInfoDao.findList(doorErrorInfo);
			if (!Collections3.isEmpty(doorErrorInfoList)) {
				// 删除相关的差错信息
				for (DoorErrorInfo doorE : doorErrorInfoList) {
					doorErrorInfoDao.delete(doorE);
				}

			}
			// ----------差错管理------------
		} catch (Exception e) {
			String strErrMsg = "拆箱单号：" + checkCashAmount.getOutNo();
			logger.error(strErrMsg + ",删除失败！", e.getMessage());
			throw new BusinessException("message.I7003", strErrMsg);
		}

	}

	/**
	 * 
	 * @author wanglin
	 * @version 每笔金额的删除
	 * 
	 * @param amountId
	 *            款箱拆箱每笔明细表ID
	 * @param chkUpdateCnt
	 *            主表更新回数
	 */
	@Transactional(readOnly = false)
	public Map<String, Object> deleteDetail(String amountId, String chkUpdateCnt) {
		Map<String, Object> map = new HashMap<String, Object>();
		CheckCashAmount checkCashAmount = new CheckCashAmount();
		try {
			map.put("mainDelFlag", "0"); // 主表删除标记
			map.put("chkUpdateCnt", chkUpdateCnt); // 主表更新回数
			if (StringUtils.isBlank(amountId)) {
				return map;
			}

			checkCashAmount = checkCashAmountDao.get(amountId);
			if (checkCashAmount == null) {
				return map;
			}

			checkCashAmount.preUpdate();
			checkCashAmountDao.logicDelete(checkCashAmount); // 每笔金额表
			checkCashDetailDao.logicDelete(checkCashAmount); // 每笔金额面值表

			// 明细表记录的判断，没有的场合删主表，有的场合更新主表
			// 处理前检查（防止多用同时操作）
			CheckCashMain checkMain = new CheckCashMain();
			checkMain = checkCashMainDao.get(checkCashAmount.getOutNo());
			if (checkMain != null && StringUtils.isNotBlank(chkUpdateCnt)
					&& !chkUpdateCnt.equals(checkMain.getUpdateCnt())) {
				String strErrMsg = "拆箱单号：" + checkCashAmount.getOutNo();
				throw new BusinessException("message.I7224", strErrMsg);
			}
			// 更新处理
			CheckCashAmount cashAmountTemp = new CheckCashAmount();
			cashAmountTemp.setOutNo(checkCashAmount.getOutNo());
			List<CheckCashAmount> cashAmountList = checkCashAmountDao.findList(cashAmountTemp);
			if (cashAmountList == null || cashAmountList.size() < 1) {
				map.put("mainDelFlag", "1");
				map.put("chkUpdateCnt", ""); // 主表更新回数
				checkCashMainDao.logicDelete(checkCashAmount); // 款箱拆箱主表
			} else {
				int intBoxCount = 0;
				// 每笔明细表的取得总金额的计算
				String mainInputAmount = "0";
				String mainCheckAmount = "0";
				String mianDiffAmount = "0";
				for (CheckCashAmount CashItem : cashAmountList) {
					intBoxCount = intBoxCount + 1;
					mainInputAmount = String
							.valueOf(Double.valueOf(mainInputAmount) + Double.valueOf(CashItem.getInputAmount()));

					mainCheckAmount = String
							.valueOf(Double.valueOf(mainCheckAmount) + Double.valueOf(CashItem.getCheckAmount()));

				}

				// 差额
				mianDiffAmount = String.valueOf(Double.valueOf(mainCheckAmount) - Double.valueOf(mainInputAmount));
				// 保存
				CheckCashMain checkCashMain = new CheckCashMain();
				checkCashMain = checkCashMainDao.get(checkCashAmount.getOutNo());
				checkCashMain.setInputAmount(mainInputAmount); // 拆箱总金额
				checkCashMain.setCheckAmount(mainCheckAmount); // 清点总金额
				checkCashMain.setDiffAmount(mianDiffAmount); // 差额
				checkCashMain.setBoxCount(String.valueOf(intBoxCount)); // 总笔数
				checkCashMain.preUpdate();
				checkCashMainDao.update(checkCashMain);

				// 更新回数的取得（冲突处理）
				CheckCashMain checkMainTemp = new CheckCashMain();
				checkMainTemp = checkCashMainDao.get(checkCashAmount.getOutNo());
				map.put("chkUpdateCnt", checkMainTemp.getUpdateCnt()); // 主表更新回数
			}
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			String strErrMsg = "拆箱单号：" + checkCashAmount.getOutNo() + ",每笔行号：" + checkCashAmount.getOutRowNo()
					+ ",每笔ID：" + amountId;
			logger.error(strErrMsg + ",删除失败！", e.getMessage());
			throw new BusinessException("message.I7003", strErrMsg);
		}
		return map;

	}

	/**
	 * 
	 * @author wanglin
	 * @version 每笔金额的保存
	 * 
	 * @param checkCashConfirm
	 */
	@Transactional(readOnly = false)
	public void confirmDetail(CheckCashConfirm checkCashConfirm) {
		try {

			String outNo = checkCashConfirm.getOutNo(); // 单号
			String outNewNo = ""; // 单号(新单号)
			String outRowNo = ""; // 行号
			String sumCheckAmount = "0";
			String[] arrPayValue = checkCashConfirm.getPayValueJoin().split(","); // 每笔面值List（券别-张数）
			// 单号的生成
			if (StringUtils.isBlank(outNo)) {
				outNewNo = BusinessUtils.getNewBusinessNo(WeChatConstant.BusinessType.CASH_BOX,
						UserUtils.getUser().getOffice());
			} else {
				// 修正的场合
				outNewNo = outNo;

				// 处理前检查（防止多用同时操作）
				CheckCashMain checkMain = new CheckCashMain();
				checkMain = checkCashMainDao.get(outNewNo);
				String chkUpdateCnt = checkCashConfirm.getChkUpdateCnt();
				if (checkMain != null && StringUtils.isNotBlank(chkUpdateCnt)
						&& !chkUpdateCnt.equals(checkMain.getUpdateCnt())) {
					String strErrMsg = "拆箱单号：" + outNewNo;
					throw new BusinessException("message.I7224", strErrMsg);
				}
			}

			// 行号的取得
			if (StringUtils.isBlank(checkCashConfirm.getAmountId())) {
				outRowNo = String.valueOf(checkCashAmountDao.getMaxOutRowNo(outNewNo) + 1);
			} else {
				CheckCashAmount checkCashAmount = checkCashAmountDao.get(checkCashConfirm.getAmountId());
				outRowNo = checkCashAmount.getOutRowNo();
			}

			// 券别面值list
			StoDict stoDict = new StoDict();
			stoDict.setType(Constant.DenominationType.RMB_PDEN); // 人民币：纸币
			List<StoDict> denDictList = stoDictDao.findList(stoDict);

			Map<String, String> denDictMap = new HashMap<String, String>();
			for (StoDict info : denDictList) {
				denDictMap.put(info.getValue(), String.valueOf(info.getUnitVal()));
			}

			// -----------------------------------------------------
			// -------- 每笔面值明细表的做成 (CHECK_CASH_DETAIL) ---------
			// -----------------------------------------------------
			// 删除已有数据
			CheckCashDetail checkCashDetail = new CheckCashDetail();
			checkCashDetail.setOutNo(outNewNo);
			checkCashDetail.setOutRowNo(outRowNo);
			checkCashDetailDao.delete(checkCashDetail);
			// 新数据做成
			for (int i = 0; i < arrPayValue.length; i++) {
				String[] arrSplit = arrPayValue[i].split("-");
				if (arrSplit != null && arrSplit.length > 1) {
					if (StringUtils.isNotBlank(arrSplit[1])) {
						checkCashDetail = new CheckCashDetail();
						checkCashDetail.setOutNo(outNewNo); // 单号
						checkCashDetail.setOutRowNo(outRowNo); // 行号
						checkCashDetail.setCurrency(Constant.Currency.RMB); // 币种
						checkCashDetail.setDenomination(arrSplit[0]); // 券别
						checkCashDetail.setCountZhang(arrSplit[1]); // 张数
						checkCashDetail.setUnitId(Constant.Unit.piece); // 单位
						String unitVal = denDictMap.get(arrSplit[0]);
						String detailAmount = String.valueOf(Double.valueOf(unitVal) * Double.valueOf(arrSplit[1]));
						checkCashDetail.setDetailAmount(detailAmount); // 金额
						sumCheckAmount = String.valueOf(Double.valueOf(sumCheckAmount) + Double.valueOf(detailAmount));
						checkCashDetail.setParValue(unitVal); // 面值
						checkCashDetail.preInsert();
						checkCashDetailDao.insert(checkCashDetail);
					}
				}
			}

			// -----------------------------------------------------
			// ---------- 每笔明细表的做成 (CHECK_CASH_AMOUNT) ----------
			// -----------------------------------------------------
			CheckCashAmount checkAmount = new CheckCashAmount();
			User sysUser = UserUtils.getUser();
			User authUser = new User();
			String authorizeUserId = ""; // 授权人ID
			String authorizeUserNm = ""; // 授权人名
			if (StringUtils.isNotBlank(checkCashConfirm.getAuthUserId())) {
				authorizeUserId = checkCashConfirm.getAuthUserId(); // 授权人ID
				authUser = userDao.get(authorizeUserId);
				authorizeUserNm = authUser.getName(); // 授权人名
			}

			if (StringUtils.isBlank(checkCashConfirm.getAmountId())) {
				// 登记的场合
				checkAmount.setOutNo(outNewNo); // 单号
				checkAmount.setOutRowNo(outRowNo); // 明细序号
				checkAmount.setInputAmount(checkCashConfirm.getInputAmount()); // 录入金额
				checkAmount.setCheckAmount(sumCheckAmount); // 清点金额
				String diffAmount = String
						.valueOf(Double.valueOf(sumCheckAmount) - Double.valueOf(checkCashConfirm.getInputAmount()));
				checkAmount.setDiffAmount(diffAmount); // 差额
				checkAmount.setDataFlag(CollectionConstant.dataFlagType.INPUT); // 数据区分（0：录入
																				// 1：分配）
				checkAmount.setEnabledFlag(CollectionConstant.enabledFlagType.OK); // 启用标识（1：启用
																					// 0
																					// ：未启用）
				checkAmount.setConfirmUserId(sysUser.getId()); // 确认人ID
				checkAmount.setConfirmUserNm(sysUser.getName()); // 确认人名
				checkAmount.setAuthorizeUserId(authorizeUserId); // 授权人ID
				checkAmount.setAuthorizeUserNm(authorizeUserNm); // 授权人名
				checkAmount.preInsert();
				checkCashAmountDao.insert(checkAmount);
			} else {
				// 修改的场合
				checkAmount = checkCashAmountDao.get(checkCashConfirm.getAmountId());
				checkAmount.setInputAmount(checkCashConfirm.getInputAmount()); // 录入金额
				checkAmount.setCheckAmount(sumCheckAmount); // 清点金额
				String diffAmount = String
						.valueOf(Double.valueOf(sumCheckAmount) - Double.valueOf(checkCashConfirm.getInputAmount()));
				checkAmount.setDiffAmount(diffAmount); // 差额
				checkAmount.setEnabledFlag(CollectionConstant.enabledFlagType.OK); // 启用标识（1：启用
																					// 0
																					// ：未启用）
				checkAmount.setConfirmUserId(sysUser.getId()); // 确认人ID
				checkAmount.setConfirmUserNm(sysUser.getName()); // 确认人名
				checkAmount.setAuthorizeUserId(authorizeUserId); // 授权人ID
				checkAmount.setAuthorizeUserNm(authorizeUserNm); // 授权人名
				checkAmount.preUpdate();
				checkCashAmountDao.update(checkAmount);
			}

			// -----------------------------------------------------
			// ---------- 款箱拆箱主表的做成 (CHECK_CASH_MAIN) ----------
			// -----------------------------------------------------
			int intBoxCount = 0;
			CheckCashMain checkCashMain = new CheckCashMain();

			// 门店检索
			Office office = officeDao.get(checkCashConfirm.getCustNo());

			// 每笔明细表的取得总金额的计算
			String mainInputAmount = "0";
			String mainCheckAmount = "0";
			String mianDiffAmount = "0";
			CheckCashAmount cashAmountTemp = new CheckCashAmount();
			cashAmountTemp.setOutNo(outNewNo);
			List<CheckCashAmount> cashAmountList = checkCashAmountDao.findList(cashAmountTemp);
			for (CheckCashAmount CashItem : cashAmountList) {
				intBoxCount = intBoxCount + 1;
				mainInputAmount = String
						.valueOf(Double.valueOf(mainInputAmount) + Double.valueOf(CashItem.getInputAmount()));

				mainCheckAmount = String
						.valueOf(Double.valueOf(mainCheckAmount) + Double.valueOf(CashItem.getCheckAmount()));

			}

			// 差额
			mianDiffAmount = String.valueOf(Double.valueOf(mainCheckAmount) - Double.valueOf(mainInputAmount));
			// 保存
			if (StringUtils.isBlank(outNo)) {
				// 登记的场合
				checkCashMain.setOutNo(outNewNo); // 单号
				checkCashMain.setCustNo(checkCashConfirm.getCustNo()); // 门店ID
				checkCashMain.setCustName(office.getName()); // 门店名称
				checkCashMain.setInputAmount(mainInputAmount); // 拆箱总金额
				checkCashMain.setCheckAmount(mainCheckAmount); // 清点总金额
				checkCashMain.setDiffAmount(mianDiffAmount); // 差额
				checkCashMain.setBoxCount(String.valueOf(intBoxCount)); // 总笔数
				checkCashMain.setRegDate(new Date()); // 登记日期
				checkCashMain.setRemarks(checkCashConfirm.getRemarks()); // 备注
				checkCashMain.setDataFlag(CollectionConstant.dataFlagType.INPUT); // 数据区分（0：录入
																					// 1：分配）
				checkCashMain.preInsert();
				checkCashMainDao.insert(checkCashMain);
			} else {
				// 修改的场合
				checkCashMain = checkCashMainDao.get(outNewNo);
				checkCashMain.setCustNo(checkCashConfirm.getCustNo()); // 门店ID
				checkCashMain.setCustName(office.getName()); // 门店名称
				checkCashMain.setInputAmount(mainInputAmount); // 拆箱总金额
				checkCashMain.setCheckAmount(mainCheckAmount); // 清点总金额
				checkCashMain.setDiffAmount(mianDiffAmount); // 差额
				checkCashMain.setBoxCount(String.valueOf(intBoxCount)); // 总笔数
				checkCashMain.setRemarks(checkCashConfirm.getRemarks()); // 备注
				checkCashMain.preUpdate();
				checkCashMainDao.update(checkCashMain);
			}
			checkCashConfirm.setNewOutNo(outNewNo); // 拆箱单号(新)
			checkCashConfirm.setNewAmountId(checkAmount.getId()); // 每笔ID(新)
			// 更新回数的取得（冲突处理）
			CheckCashMain checkMainTemp = new CheckCashMain();
			checkMainTemp = checkCashMainDao.get(outNewNo);
			checkCashConfirm.setChkUpdateCnt(checkMainTemp.getUpdateCnt()); // 更新回数

			// ----------差错管理------------
			DoorErrorInfo doorErrorInfo = new DoorErrorInfo();
			// 流水号
			doorErrorInfo.setBusinessId(outNewNo);
			// 明细序号
			doorErrorInfo.setOutRowNo(checkAmount.getOutRowNo());
			// 查询差错列表
			List<DoorErrorInfo> doorErrorInfoList = doorErrorInfoDao.findList(doorErrorInfo);
			// 是否产生差额
			if (StringUtils.isNotBlank(authorizeUserId)) {
				// 该流水是否产生过差错信息
				if (!Collections3.isEmpty(doorErrorInfoList)) {
					doorErrorInfoDao.delete(doorErrorInfoList.get(0));
				}
				// 凭条
				doorErrorInfo.setTickertape(checkAmount.getPackNum());
				// 差错类型
				if (new BigDecimal(checkAmount.getInputAmount())
						.compareTo(new BigDecimal(checkAmount.getCheckAmount())) == 1) {
					// 短款
					doorErrorInfo.setErrorType(DoorOrderConstant.ErrorType.SHORT_CURRENCY);
				} else {
					// 长款
					doorErrorInfo.setErrorType(DoorOrderConstant.ErrorType.LONG_CURRENCY);
				}
				// 录入金额
				doorErrorInfo.setInputAmount(new BigDecimal(checkAmount.getInputAmount()));
				// 清点金额
				doorErrorInfo.setCheckAmount(new BigDecimal(checkAmount.getCheckAmount()));
				// 差额
				doorErrorInfo.setDiffAmount(new BigDecimal(checkAmount.getDiffAmount()));
				// 门店
				doorErrorInfo.setCustNo(office.getId());
				// 门店名称
				doorErrorInfo.setCustName(office.getName());
				// 清分人
				doorErrorInfo.setClearManNo(sysUser.getId());
				// 清分人名称
				doorErrorInfo.setClearManName(sysUser.getName());
				// 确认人
				doorErrorInfo.setMakesureManNo(authorizeUserId);
				// 确认人名称
				doorErrorInfo.setMakesureManName(authorizeUserNm);
				// 清分中心
				doorErrorInfo.setOffice(sysUser.getOffice());
				// 主键
				doorErrorInfo.setErrorNo(IdGen.uuid());
				doorErrorInfo.preInsert();
				doorErrorInfoDao.insert(doorErrorInfo);
			} else if (!Collections3.isEmpty(doorErrorInfoList)) {
				doorErrorInfo = doorErrorInfoList.get(0);
				doorErrorInfoDao.delete(doorErrorInfo);
			}
			// ----------差错管理------------
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			String strErrMsg = "拆箱单号：" + checkCashConfirm.getOutNo() + ",每笔ID：" + checkCashConfirm.getAmountId()
					+ ",录入金额：" + checkCashConfirm.getInputAmount();
			logger.error(strErrMsg + ",保存失败！", e.getMessage());
			throw new BusinessException("message.I7003", strErrMsg);
		}

	}

	/**
	 * 
	 * @author wanglin
	 * @version 款箱拆箱当日主表数据取得(PDA)
	 * 
	 * @param checkCashMain
	 */
	public List<CheckCashMain> PdaMainFindList(CheckCashMain checkCashMain) {
		return checkCashMainDao.pdaFindList(checkCashMain);
	}

	/**
	 * 
	 * @author wanglin
	 * @version 款箱拆箱明细数据取得(PDA)
	 * 
	 * @param CheckCashAmount
	 */
	public List<CheckCashAmount> PdaAmountFindList(CheckCashAmount checkCashAmount) {
		return checkCashAmountDao.pdaFindList(checkCashAmount);
	}

	/**
	 * 保存拆箱信息
	 *
	 * @author XL
	 * @version 2019年7月11日
	 * @param checkCashMain
	 */
	@Transactional(readOnly = false)
	public void save(CheckCashMain checkCashMain) {
		try {
			// 将金额转换成数字
			checkCashMain.setCheckAmount(checkCashMain.getCheckAmount().replace(",", ""));
			checkCashMain.setInputAmount(checkCashMain.getInputAmount().replace(",", ""));
			checkCashMain.setDiffAmount(checkCashMain.getDiffAmount().replace(",", ""));
			checkCashMain.setTrueSumMoney(checkCashMain.getTrueSumMoney().replace(",", ""));
			// 登陆人
			User sysUser = UserUtils.getUser();
			// 授权人
			User authUser = new User();
			if (StringUtils.isNotBlank(checkCashMain.getAuthUserId())) {
				authUser = userDao.get(checkCashMain.getAuthUserId());
			}
			// 门店
			Office office = officeDao.get(checkCashMain.getCustNo());
			// 拆箱单号
			String outNo = checkCashMain.getOutNo();
			// 单号生成
			if (StringUtils.isBlank(outNo)) {
				outNo = BusinessUtils.getNewBusinessNo(WeChatConstant.BusinessType.CASH_BOX,
						UserUtils.getUser().getOffice());
			}
			// 券别面值list
			StoDict stoDict = new StoDict();
			// 人民币：纸币
			stoDict.setType(Constant.DenominationType.RMB_PDEN);
			List<StoDict> denDictList = stoDictDao.findList(stoDict);
			// 人民币：硬币
			stoDict.setType(Constant.DenominationType.RMB_HDEN);
			// 合并面值列表
			denDictList.addAll(stoDictDao.findList(stoDict));
			Map<String, String> denDictMap = new HashMap<String, String>();
			for (StoDict info : denDictList) {
				denDictMap.put(info.getValue(), String.valueOf(info.getUnitVal()));
			}
			// -----------------------------------------------------
			// -------- 每笔面值明细表的做成 (CHECK_CASH_DETAIL) ---------
			// -----------------------------------------------------
			// 删除已有数据
			CheckCashDetail checkCashDetail = new CheckCashDetail();
			// 拆箱单号
			checkCashDetail.setOutNo(outNo);
			checkCashDetailDao.delete(checkCashDetail);
			// 新数据做成(纸币)
			for (DenominationInfo denominationInfo : checkCashMain.getDenominationList()) {
				// 是否输入张数
				if (StringUtils.isNotBlank(denominationInfo.getColumnValue1())
						&& Integer.parseInt(denominationInfo.getColumnValue1()) != 0) {
					checkCashDetail = new CheckCashDetail();
					// 拆箱单号
					checkCashDetail.setOutNo(outNo);
					// 币种
					checkCashDetail.setCurrency(Constant.Currency.RMB);
					// 券别
					checkCashDetail.setDenomination(denominationInfo.getMoneyKey());
					// 张数
					checkCashDetail.setCountZhang(denominationInfo.getColumnValue1());
					// 单位
					checkCashDetail.setUnitId(Constant.Unit.piece);
					// 面值
					String unitVal = denDictMap.get(denominationInfo.getMoneyKey());
					checkCashDetail.setParValue(unitVal);
					// 金额
					String detailAmount = String
							.valueOf(Double.valueOf(unitVal) * Double.valueOf(denominationInfo.getColumnValue1()));
					checkCashDetail.setDetailAmount(detailAmount);
					checkCashDetail.preInsert();
					checkCashDetailDao.insert(checkCashDetail);
				}
			}
			// 新数据做成(硬币)
			for (DenominationInfo denominationInfo : checkCashMain.getCnyhdenList()) {
				// 是否输入张数
				if (StringUtils.isNotBlank(denominationInfo.getColumnValue1())
						&& Integer.parseInt(denominationInfo.getColumnValue1()) != 0) {
					checkCashDetail = new CheckCashDetail();
					// 拆箱单号
					checkCashDetail.setOutNo(outNo);
					// 币种
					checkCashDetail.setCurrency(Constant.Currency.RMB);
					// 券别
					checkCashDetail.setDenomination(denominationInfo.getMoneyKey());
					// 张数
					checkCashDetail.setCountZhang(denominationInfo.getColumnValue1());
					// 单位
					checkCashDetail.setUnitId(Constant.Unit.coin);
					// 面值
					String unitVal = denDictMap.get(denominationInfo.getMoneyKey());
					checkCashDetail.setParValue(unitVal);
					// 金额
					String detailAmount = String
							.valueOf(Double.valueOf(unitVal) * Double.valueOf(denominationInfo.getColumnValue1()));
					checkCashDetail.setDetailAmount(detailAmount);
					checkCashDetail.preInsert();
					checkCashDetailDao.insert(checkCashDetail);
				}
			}
			// -----------------------------------------------------
			// ---------- 每笔明细表的做成 (CHECK_CASH_AMOUNT) ----------
			// -----------------------------------------------------
			// 删除原录入明细
			CheckCashAmount checkAmountTemp = new CheckCashAmount();
			checkAmountTemp.setDataFlag(CollectionConstant.dataFlagType.INPUT);
			checkAmountTemp.setOutNo(outNo);
			checkAmountTemp.setUpdateBy(sysUser);
			checkAmountTemp.setUpdateDate(new Date());
			checkCashAmountDao.inputLogicDelete(checkAmountTemp);
			// 金额列表
			List<String> amountList = Arrays.asList(checkCashMain.getAmountListStr().split(","));
			CheckCashAmount checkAmount = new CheckCashAmount();
			for (int i = 0; i < amountList.size(); i++) {
				String[] arrSplit = amountList.get(i).split("_");
				// 行号
				String outRowNo = StringUtils.toString(i + 1);
				// 明细编号
				String amountId = arrSplit[0];
				// 金额
				String amount = arrSplit[1];
				// 查询明细
				checkAmount = checkCashAmountDao.get(amountId);
				// 明细不存在或录入明细（已被删除）
				if (checkAmount == null || checkAmount.getDataFlag().equals(CollectionConstant.dataFlagType.INPUT)) {
					// 登记的场合
					checkAmount = new CheckCashAmount();
					// 拆箱编号
					checkAmount.setOutNo(outNo);
					// 明细序号
					checkAmount.setOutRowNo(outRowNo);
					// 录入金额
					checkAmount.setInputAmount(amount);
					// 数据区分（0：录入；1：分配）
					checkAmount.setDataFlag(CollectionConstant.dataFlagType.INPUT);
					// 启用标识（1：启用；0：未启用）
					checkAmount.setEnabledFlag(CollectionConstant.enabledFlagType.OK);
					// 确认人ID
					checkAmount.setConfirmUserId(sysUser.getId());
					// 确认人名
					checkAmount.setConfirmUserNm(sysUser.getName());
					// 授权人ID
					checkAmount.setAuthorizeUserId(authUser.getId());
					// 授权人名
					checkAmount.setAuthorizeUserNm(authUser.getName());
					checkAmount.preInsert();
					checkCashAmountDao.insert(checkAmount);
				} else {
					// 修改的场合
					checkAmount = checkCashAmountDao.get(amountId);
					// 启用标识（1：启用；0：未启用）
					checkAmount.setEnabledFlag(CollectionConstant.enabledFlagType.OK);
					// 确认人ID
					checkAmount.setConfirmUserId(sysUser.getId());
					// 确认人名
					checkAmount.setConfirmUserNm(sysUser.getName());
					// 授权人ID
					checkAmount.setAuthorizeUserId(authUser.getId());
					// 授权人名
					checkAmount.setAuthorizeUserNm(authUser.getName());
					checkAmount.preUpdate();
					checkCashAmountDao.update(checkAmount);
				}
			}
			// -----------------------------------------------------
			// ---------- 款箱拆箱主表的做成 (CHECK_CASH_MAIN) ----------
			// -----------------------------------------------------
			if (StringUtils.isBlank(checkCashMain.getOutNo())) {
				// 登记的场合
				checkCashMain.setOutNo(outNo);
				// 登记日期
				checkCashMain.setRegDate(new Date());
				// 数据区分（0：录入；1：分配）
				checkCashMain.setDataFlag(CollectionConstant.dataFlagType.INPUT);
				checkCashMain.preInsert();
				checkCashMainDao.insert(checkCashMain);
			} else {
				// 修改的场合
				checkCashMain.preUpdate();
				checkCashMainDao.update(checkCashMain);
			}
			// -----------------------------------------------------
			// ---------- 差错管理主表的做成 (DOOR_ERROR_INFO) ----------
			// -----------------------------------------------------
			if (authUser != null && StringUtils.isNotBlank(authUser.getId())) {
				DoorErrorInfo doorErrorInfo = new DoorErrorInfo();
				// 流水号
				doorErrorInfo.setBusinessId(outNo);
				// 差错类型
				if (new BigDecimal(checkCashMain.getTrueSumMoney())
						.compareTo(new BigDecimal(checkCashMain.getCheckAmount())) == 1) {
					// 短款
					doorErrorInfo.setErrorType(DoorOrderConstant.ErrorType.SHORT_CURRENCY);
				} else {
					// 长款
					doorErrorInfo.setErrorType(DoorOrderConstant.ErrorType.LONG_CURRENCY);
				}
				// 状态
				doorErrorInfo.setStatus(DoorOrderConstant.Status.REGISTER);
				// 存款金额
				doorErrorInfo.setInputAmount(new BigDecimal(checkCashMain.getInputAmount()));
				// 清点金额
				doorErrorInfo.setCheckAmount(new BigDecimal(checkCashMain.getCheckAmount()));
				// 差额
				doorErrorInfo.setDiffAmount(new BigDecimal(checkCashMain.getDiffAmount()).abs());
				// 门店
				doorErrorInfo.setCustNo(office.getId());
				// 门店名称
				doorErrorInfo.setCustName(office.getName());
				// 清分人
				doorErrorInfo.setClearManNo(sysUser.getId());
				// 清分人名称
				doorErrorInfo.setClearManName(sysUser.getName());
				// 确认人
				doorErrorInfo.setMakesureManNo(authUser.getId());
				// 确认人名称
				doorErrorInfo.setMakesureManName(authUser.getName());
				// 清分中心
				doorErrorInfo.setOffice(sysUser.getOffice());
				// 主键
				doorErrorInfo.setErrorNo(IdGen.uuid());
				doorErrorInfo.preInsert();
				doorErrorInfoDao.insert(doorErrorInfo);
				// -----------------------------------------------------
				// ---------- 关联到账务 ----------
				// -----------------------------------------------------
				DoorCenterAccountsMain doorCenterAccountsMain = new DoorCenterAccountsMain();
				// 设置关联账务流水Id
				doorCenterAccountsMain.setBusinessId(doorErrorInfo.getErrorNo());
				// 设置客户Id
				doorCenterAccountsMain.setClientId(office.getId());
				// 设置业务类型
				doorCenterAccountsMain.setBusinessType(DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE);
				// 设置出库或入库金额(长款==入库 短款==出库)
				if (doorErrorInfo.getErrorType().equals(ClearConstant.ErrorType.SHORT_CURRENCY)) {
					// 短款
					doorCenterAccountsMain.setOutAmount(doorErrorInfo.getDiffAmount());
					doorCenterAccountsMain.setInoutType(DoorOrderConstant.AccountsInoutType.ACCOUNTS_OUT);
				} else if (doorErrorInfo.getErrorType().equals(ClearConstant.ErrorType.LONG_CURRENCY)) {
					// 长款
					doorCenterAccountsMain.setInAmount(doorErrorInfo.getDiffAmount());
					doorCenterAccountsMain.setInoutType(DoorOrderConstant.AccountsInoutType.ACCOUNTS_IN);
				}
				// 设置账务发生机构
				User userInfo = UserUtils.getUser();
				doorCenterAccountsMain.setRofficeId(userInfo.getOffice().getId());
				// 设置账务所在机构
				doorCenterAccountsMain.setAofficeId(office.getId());
				// 设置业务状态
				doorCenterAccountsMain.setBusinessStatus(DoorOrderConstant.StatusType.CREATE);
				// 设置该笔差错是否已经处理
				doorCenterAccountsMain.setIsTakeUp(DoorOrderConstant.ErrorIsTakeUp.NOTTAKEUP);
				DoorCommonUtils.insertAccounts(doorCenterAccountsMain);
			}

			// -----------------------------------------------------
			// ---------- 更改对应的存款记录状态 ----------
			// -----------------------------------------------------
			// 根据款袋编号获取待分配任务
			TaskDown taskDown = new TaskDown();
			// 清分机构
			taskDown.setOffice(UserUtils.getUser().getOffice());
			// 款袋编号
			taskDown.setRfid(checkCashMain.getRfid());
			//拆箱单号
			taskDown.setOrderId(checkCashMain.getOutNo());
			// 设置状态（确认,清分）
			taskDown.setStatusList(
					Arrays.asList(new String[] { DoorOrderConstant.Status.CONFIRM, DoorOrderConstant.Status.CLEAR }));
			// 延长日
			taskDown.setExtendeDay(WeChatConstant.EXTENDE_DAY);
			// 任务列表
			List<TaskDown> taskDownList = taskDownService.findList(taskDown);
			taskDown = taskDownList.get(0);
			// 任务确认
			TaskConfirm taskConfirm = taskConfirmService.get(taskDown.getId());
			taskConfirm.setAllotStatus(CollectionConstant.allotStatusType.CONFIRM_OK);
			taskConfirm.setUpdateBy(UserUtils.getUser());
			taskConfirm.setUpdateName(UserUtils.getUser().getName());
			taskConfirm.setUpdateDate(new Date());
			taskConfirmDao.updateAllotStatus(taskConfirm);
			// 状态：清分
			taskConfirm.setStatus(CollectionConstant.statusType.CLEAR);
			taskConfirmDao.updateStatus(taskConfirm);
		} catch (BusinessException e) {
			throw e;
		}
	}

	/**
	 * 款箱拆箱冲正操作
	 *
	 * @author XL
	 * @version 2019年7月26日
	 * @param checkCashMain
	 */
	@Transactional(readOnly = false)
	public void reverse(CheckCashMain checkCashMain) {
		CheckCashAmount checkCashAmount = new CheckCashAmount();
		// 清点金额
		checkCashMain.setCheckAmount("0");
		// 差额
		checkCashMain.setDiffAmount("0");
		// 备注
		checkCashMain.setRemarks(null);
		checkCashMain.preUpdate();
		// 更新人置为空,别人可拆箱
		checkCashMain.setUpdateBy(null);
		checkCashMainDao.update(checkCashMain);
		// 拆箱单号
		checkCashAmount.setOutNo(checkCashMain.getOutNo());
		List<CheckCashAmount> checkCashAmountList = checkCashAmountDao.findList(checkCashAmount);
		for (CheckCashAmount checkAmount : checkCashAmountList) {
			// 未启用
			checkAmount.setEnabledFlag(CollectionConstant.enabledFlagType.NO);
			checkCashAmountDao.update(checkAmount);
		}
		// 删除款箱拆箱每笔面值明细表
		CheckCashDetail checkCashDetail = new CheckCashDetail();
		checkCashDetail.setOutNo(checkCashMain.getOutNo());
		checkCashDetailDao.delete(checkCashDetail);
		// 查询差错信息
		DoorErrorInfo doorErrorInfo = new DoorErrorInfo();
		doorErrorInfo.setBusinessId(checkCashMain.getOutNo());
		doorErrorInfo.setStatus(DoorOrderConstant.Status.REGISTER);
		List<DoorErrorInfo> doorErrorInfoList = doorErrorInfoDao.findList(doorErrorInfo);
		// 是否存在差错
		if (!Collections3.isEmpty(doorErrorInfoList)) {
			// 差错信息
			doorErrorInfo = doorErrorInfoList.get(0);
			// 设置状态（冲正）
			doorErrorInfo.setStatus(DoorOrderConstant.Status.REVERSE);
			// 更新
			doorErrorInfo.preUpdate();
			doorErrorInfoDao.update(doorErrorInfo);
			// -----------------------------------------------------
			// ---------- 关联到账务 ----------
			// -----------------------------------------------------
			DoorCenterAccountsMain doorCenterAccountsMain = new DoorCenterAccountsMain();
			// 设置关联账务流水Id
			doorCenterAccountsMain.setBusinessId(doorErrorInfo.getErrorNo());
			// 设置客户Id
			doorCenterAccountsMain.setClientId(doorErrorInfo.getCustNo());
			// 设置业务类型
			doorCenterAccountsMain.setBusinessType(DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE);
			// 设置出库或入库金额(长款==入库 短款==出库)
			if (doorErrorInfo.getErrorType().equals(ClearConstant.ErrorType.SHORT_CURRENCY)) {
				// 短款
				doorCenterAccountsMain.setInAmount(doorErrorInfo.getDiffAmount());
				doorCenterAccountsMain.setInoutType(DoorOrderConstant.AccountsInoutType.ACCOUNTS_IN);
			} else if (doorErrorInfo.getErrorType().equals(ClearConstant.ErrorType.LONG_CURRENCY)) {
				// 长款
				doorCenterAccountsMain.setOutAmount(doorErrorInfo.getDiffAmount());
				doorCenterAccountsMain.setInoutType(DoorOrderConstant.AccountsInoutType.ACCOUNTS_OUT);
			}
			// 设置账务发生机构
			User userInfo = UserUtils.getUser();
			doorCenterAccountsMain.setRofficeId(userInfo.getOffice().getId());
			// 设置账务所在机构
			doorCenterAccountsMain.setAofficeId(doorErrorInfo.getCustNo());
			// 设置业务状态
			doorCenterAccountsMain.setBusinessStatus(DoorOrderConstant.StatusType.DELETE);
			// 设置该笔差错是否已经处理
			doorCenterAccountsMain.setIsTakeUp(DoorOrderConstant.ErrorIsTakeUp.NOTTAKEUP);
			DoorCommonUtils.insertAccounts(doorCenterAccountsMain);
		}
	}

	/**
	 * 查询面值明细列表
	 *
	 * @author XL
	 * @version 2019年8月7日
	 * @param checkCashDetail
	 * @return
	 */
	public List<CheckCashDetail> findList(CheckCashDetail checkCashDetail) {
		return checkCashDetailDao.findList(checkCashDetail);
	}

	/**
	 * 拆箱数据生成（页面存款时生成）
	 *
	 * @author WQJ
	 * @version 2019年10月16日
	 * @param taskConfirm
	 * @param user
	 * @param date
	 * @param office
	 */
	@Transactional(readOnly = false)
	public void createCheckCashForPage(TaskConfirm taskConfirm, User user, Date date, Office office) {
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
		// checkCashMain.setUpdateBy(user);
		// checkCashMain.setUpdateName(user.getName());
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
			checkCashAmount.setDetailId(itemData.getId());
			checkCashAmount.setRemarks(itemData.getRemarks());
			checkCashAmountDao.insert(checkCashAmount);
		}
	}

	/**
	 * 按单号获取存款差错金额，并设置实际存款金额
	 *
	 * @author WQJ
	 * @version 2019年10月24日
	 * @param taskConfirm
	 * @param user
	 * @param date
	 * @param office
	 */

	public CheckCashMain getDepositErrorByOutNo(CheckCashMain checkCashMain) {
		// 按拆箱单号（存款单号）去找每一笔存款
		DoorOrderDetail doorOrderDetail = new DoorOrderDetail();
		doorOrderDetail.setOrderId(checkCashMain.getOutNo());
		List<DoorOrderDetail> doorOrderDetails = doorOrderDetailDao.findList(doorOrderDetail);
		// 总差额
		BigDecimal saveErrorMoney = BigDecimal.ZERO;
		// 每一笔存款找到对应的存款差错
		for (DoorOrderDetail detail : doorOrderDetails) {
			// 按凭条查找对应存款差错
			DepositError depositError = new DepositError();
			depositError.setStatus(DoorOrderConstant.StatusType.CREATE);
			depositError.setOrderId(detail.getTickertape());
			List<DepositError> result = depositErrorService.findList(depositError);
			if (!Collections3.isEmpty(result)) {
				// 长款
				if (result.get(0).getErrorType().equals(DoorOrderConstant.ErrorType.LONG_CURRENCY)) {
					saveErrorMoney = saveErrorMoney.add(new BigDecimal(result.get(0).getAmount()));
				}
				// 短款
				else {
					saveErrorMoney = saveErrorMoney.subtract((new BigDecimal(result.get(0).getAmount())));
				}
			}
			// 设置存款差错总额
			checkCashMain.setSaveErrorMoney(saveErrorMoney.toString());
			// 设置实际存款金额
			checkCashMain
					.setTrueSumMoney(new BigDecimal(checkCashMain.getInputAmount()).add(saveErrorMoney).toString());
		}
		return checkCashMain;
	}

	/**
	 * 
	 * Title: getCheckCashList
	 * <p>
	 * Description: 查询款箱拆箱列表
	 * </p>
	 * 
	 * @author: lihe
	 * @param checkCashMain
	 * @return List<CheckCashMain> 返回类型
	 */
	public List<CheckCashMain> getCheckCashList(CheckCashMain checkCashMain) {
		// 数据范围过滤
		checkCashMain.getSqlMap().put("dsf", dataScopeFilter(checkCashMain.getCurrentUser(), "o", null));
		// 列表查询
		List<CheckCashMain> result = checkCashMainDao.getCheckCashList(checkCashMain);
		if (Collections3.isEmpty(result)) {
			result = Lists.newArrayList();
		}
		// 格式化钞袋使用时间(钞袋使用时间SQL获取 暂注)
		/*
		 * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 * for (CheckCashMain check : result) { if (null != check.getLastTime()
		 * && null != check.getThisTime()) { check.setPackNumUseTime(
		 * DateUtils.getDistanceTime(sdf.format(check.getLastTime()),
		 * sdf.format(check.getThisTime()))); } }
		 */
		for(CheckCashMain c : result){
			if(c != null){
			DoorOrderDetail d = doorOrderDetailDao.getRemarks(c.getOutNo());
				if(d != null){
					c.setRemarks(d.getRemarks());
				}		
			}
		}
		return result;
	}

	/**
	 * 
	 * Title: getCheckCashPage
	 * <p>
	 * Description: 分页查询款箱拆箱列表（未拆）
	 * </p>
	 * 
	 * @author: lihe
	 * @param page
	 * @param checkCashMain
	 * @return Page<CheckCashMain> 返回类型
	 */
	public Page<CheckCashMain> getCheckCashPage(Page<CheckCashMain> page, CheckCashMain checkCashMain) {
		checkCashMain.setChecked(CollectionConstant.CheckStatus.CHECKED);
		checkCashMain.setPage(page);
		page.setList(getCheckCashList(checkCashMain));
		return page;
	}

	/**
	 * 
	 * Title: getCheckCashPage
	 * <p>
	 * Description: 历史分页查询款箱拆箱列表（已拆）
	 * </p>
	 * 
	 * @author: lihe
	 * @param page
	 * @param checkCashMain
	 * @return Page<CheckCashMain> 返回类型
	 */
	public Page<CheckCashMain> getHistoryCheckCashPage(Page<CheckCashMain> page, CheckCashMain checkCashMain) {
		checkCashMain.setChecked(CollectionConstant.CheckStatus.UNCHECKED);
		checkCashMain.setPage(page);
		page.setList(getCheckCashList(checkCashMain));
		return page;
	}

}