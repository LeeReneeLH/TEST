package com.coffer.businesses.modules.allocation.v01.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.allocation.AllocationCommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.AllocationConstant.BusinessType;
import com.coffer.businesses.modules.allocation.AllocationConstant.InOutCoffer;
import com.coffer.businesses.modules.allocation.v01.dao.AllAllocateDetailDao;
import com.coffer.businesses.modules.allocation.v01.dao.AllAllocateInfoDao;
import com.coffer.businesses.modules.allocation.v01.dao.AllAllocateItemDao;
import com.coffer.businesses.modules.allocation.v01.dao.AllHandoverDetailDao;
import com.coffer.businesses.modules.allocation.v01.dao.AllHandoverInfoDao;
import com.coffer.businesses.modules.allocation.v01.dao.TempAllAllocateInfoDao;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateItem;
import com.coffer.businesses.modules.allocation.v01.entity.AllHandoverDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllHandoverInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AtmClearBoxInfo;
import com.coffer.businesses.modules.report.ReportGraphConstant.DataGraphList;
import com.coffer.businesses.modules.report.ReportGraphConstant.GraphType;
import com.coffer.businesses.modules.report.ReportGraphConstant.SeriesClass;
import com.coffer.businesses.modules.report.ReportGraphConstant.SeriesProperties;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.dao.StoRouteInfoDao;
import com.coffer.businesses.modules.store.v01.entity.ChangeStoreEntity;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.entity.StoRouteInfo;
import com.coffer.businesses.modules.store.v01.entity.StoStoresInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.businesses.modules.store.v01.service.StoGoodsService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 调缴功能Service
 * 
 * @author Chengshu
 * @version 2015-05-07
 */
@Service
@Transactional(readOnly = true)
public class AllocationService extends BaseService {

	@Autowired
	private AllAllocateInfoDao allocateInfoDao;
	@Autowired
	private AllAllocateDetailDao allocateDetailDao;
	@Autowired
	private AllHandoverInfoDao handoverInfoDao;
	@Autowired
	private AllAllocateItemDao allAllocateItemDao;
	@Autowired
	private StoRouteInfoDao stoRouteInfoDao;
	@Autowired
	private AllHandoverDetailDao allHandoverDetailDao;
	@Autowired
	private TempAllAllocateInfoDao tempAllocateInfoDao;
	@Autowired
	private StoBoxInfoService stoBoxInfoService;
	@Autowired
	private StoGoodsService stoGoodsService;

	/**
	 * @author qph
	 * @version 2017/08/10
	 * 
	 * @Description 根据流水号取得调拨信息(临时线路表)
	 * @param allId
	 *            流水号
	 * @return 调拨相关信息
	 */
	public AllAllocateInfo getAllocate(String allId) {
		AllAllocateInfo allAllocateInfo = allocateInfoDao.get(allId);
		if (allAllocateInfo == null) {
			AllAllocateInfo allAllocate = tempAllocateInfoDao.get(allId);
			if (allAllocate != null) {
				allAllocate.setTaskType(AllocationConstant.TaskType.TEMPORARY_TASK);
				return allAllocate;
			} else {
				return null;
			}
		} else {
			allAllocateInfo.setTaskType(AllocationConstant.TaskType.ROUTINET_TASK);
			return allAllocateInfo;
		}

	}

	/**
	 * @author chengshu
	 * @version 2015/05/07
	 * 
	 * @Description 根据流水号取得库间调拨信息
	 * @param allId
	 *            流水号
	 * @return 调拨信息
	 */
	public AllAllocateInfo getAllocateBetween(String allId) {
		return allocateInfoDao.getBetween(allId);
	}

	/**
	 * @author suiwei
	 * @version 2015年9月7日
	 * 
	 *          根据流水号取得预约/配款信息
	 * @param allId
	 *            流水号
	 * @return 预约/配款信息
	 */
	public AllAllocateItem getItem(String allId) {
		return allAllocateItemDao.get(allId);
	}

	/**
	 * @author chengshu
	 * @version 2015/05/07
	 * 
	 *          根据查询条件取得分页信息
	 * @param page
	 *            页面信息
	 * @param allocateInfo
	 *            查询条件
	 * @return 分页列表
	 */
	private List<String> findPage(Page<AllAllocateInfo> page, AllAllocateInfo allocateInfo) {
		List<String> allIdList = Lists.newArrayList();

		// 查询条件：开始时间
		if (allocateInfo.getCreateTimeStart() != null) {
			allocateInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(allocateInfo.getCreateTimeStart())));
		}
		// 查询条件：结束时间
		if (allocateInfo.getCreateTimeEnd() != null) {
			allocateInfo.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(allocateInfo.getCreateTimeEnd())));
		}

		// 设置分页参数
		allocateInfo.setPage(page);

		// 根据查询条件，取得调拨流水号(分页用)
		allIdList = allocateInfoDao.findPageList(allocateInfo);
		return allIdList;
	}

	/**
	 * @author chengshu
	 * @version 2015/05/07
	 * 
	 *          根据流水号，取得调缴信息
	 * @param page
	 *            页面信息
	 * @param allocateInfo
	 *            查询条件
	 * @param pageFlag
	 *            页面查询标识(true:页面查询/false:不是页面查询)
	 * @return 查询结果
	 */
	public Page<AllAllocateInfo> findAllocation(Page<AllAllocateInfo> page, AllAllocateInfo allocateInfo,
			boolean pageFlag) {

		if (pageFlag) {
			// 根据查询条件，取得调拨流水号(分页用)
			List<String> allIdList = findPage(page, allocateInfo);
			// 如果查询不到结果的场合，返回空
			if (allIdList == null || allIdList.size() == 0) {
				return page;
			}

			// 把page内容清空
			allocateInfo.setPage(null);
			allocateInfo.setAllIds(allIdList);
			allocateInfo.setOrderBy(page.getOrderBy());
		}

		// 根据查询条件，取得调拨全部信息
		List<AllAllocateInfo> allocationList = findAllocation(allocateInfo);
		// 计算箱子个数
		countBoxNumber(allocateInfo);
		page.setList(allocationList);
		return page;
	}

	/**
	 * @author chengshu
	 * @version 2015/05/07
	 * 
	 *          取得调缴信息
	 * @param page
	 *            页面信息
	 * @param allocateInfo
	 *            查询条件
	 * @param pageFlag
	 *            页面查询标识(true:页面查询/false:不是页面查询)
	 * @return 查询结果
	 */
	public List<AllAllocateInfo> findBetweenPageList(AllAllocateInfo allocateInfo) {
		return allocateInfoDao.findBetweenPageList(allocateInfo);
	}

	/**
	 * @author 王宝忠
	 * @version 2016/01/08
	 * 
	 *          取得上缴信息
	 * @param page
	 *            页面信息
	 * @param allocateInfo
	 *            查询条件
	 * @param pageFlag
	 *            页面查询标识(true:页面查询/false:不是页面查询)
	 * @return 查询结果
	 */
	public List<AllAllocateInfo> findHandinPageList(AllAllocateInfo allocateInfo) {
		return allocateInfoDao.findHandinPageList(allocateInfo);
	}

	/**
	 * @author wangbaozhong
	 * @version 2015/05/07
	 * 
	 *          取得上缴接收信息列表
	 * @param page
	 *            页面信息
	 * @param allocateInfo
	 *            查询条件
	 * @return 查询结果
	 */
	public Page<AllAllocateInfo> findHandinCash(Page<AllAllocateInfo> page, AllAllocateInfo allocateInfo) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		allocateInfo.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "RO", null));
		// 查询条件：开始时间
		if (allocateInfo.getCreateTimeStart() != null) {
			allocateInfo
					.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(allocateInfo.getCreateTimeStart()),
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 查询条件：结束时间
		if (allocateInfo.getCreateTimeEnd() != null) {
			allocateInfo.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(allocateInfo.getCreateTimeEnd()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}

		allocateInfo.setPage(page);
		// 根据查询条件，取得调拨全部信息
		List<AllAllocateInfo> allocationList = allocateInfoDao.findHandinPageList(allocateInfo);
		page.setList(allocationList);
		return page;
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年10月28日
	 * 
	 *          取得网点上缴钞箱记录
	 * @param conditionInfo
	 *            查询条件
	 * @return 调拨记录
	 */
	public List<AllAllocateInfo> findAllocationList(AllAllocateInfo conditionInfo) {
		return allocateInfoDao.findAllocationList(conditionInfo);
	}

	/**
	 * @author wangbaozhong
	 * @version 2015/05/07
	 * 
	 *          取得预约配款信息
	 * @param page
	 *            页面信息
	 * @param allocateInfo
	 *            查询条件
	 * @return 查询结果
	 */
	public Page<AllAllocateInfo> findOrderCash(Page<AllAllocateInfo> page, AllAllocateInfo allocateInfo) {
		// 查询条件：开始时间
		if (allocateInfo.getCreateTimeStart() != null) {
			allocateInfo
					.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(allocateInfo.getCreateTimeStart()),
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 查询条件：结束时间
		if (allocateInfo.getCreateTimeEnd() != null) {
			allocateInfo.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(allocateInfo.getCreateTimeEnd()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}

		allocateInfo.setPage(page);
		AllAllocateInfo conditionInfo = null;

		Office rOffice = null;
		// 根据查询条件，取得调拨全部信息
		List<AllAllocateInfo> allocationList = allocateInfoDao.findAllocationList(allocateInfo);
		// 取得箱号
		for (AllAllocateInfo info : allocationList) {
			rOffice = StoreCommonUtils.getOfficeById(info.getrOffice().getId());
			info.setrOffice(rOffice);
			conditionInfo = new AllAllocateInfo();
			conditionInfo.setrOffice(rOffice); // 登记机构
			// 业务类型=库外箱袋调拨
			conditionInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_Reservation);
			// 查询时间为当日，每日上缴仅1次
			List<AllAllocateInfo> allocationBoxInfoList = allocateInfoDao.findAllocationList(conditionInfo);
			if (allocationBoxInfoList.size() > 0) {
				// 钞箱信息
				info.setAllDetailList(allocationBoxInfoList.get(0).getAllDetailList());
			}
		}
		page.setList(allocationList);
		return page;

	}

	/**
	 * @author chengshu
	 * @version 2015/05/27
	 * 
	 *          取得调缴信息
	 * @param allocateInfo
	 *            查询条件
	 * @return 查询结果
	 */
	public List<AllAllocateInfo> findAllocation(AllAllocateInfo allocateInfo) {

		// 查询条件：开始时间
		if (allocateInfo.getCreateTimeStart() != null) {
			allocateInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(allocateInfo.getCreateTimeStart())));
		}
		// 查询条件：结束时间
		if (allocateInfo.getCreateTimeEnd() != null) {
			allocateInfo.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(allocateInfo.getCreateTimeEnd())));
		}

		// 根据查询条件，取得调拨全部信息
		List<AllAllocateInfo> allocationList = allocateInfoDao.findAllocationList(allocateInfo);

		return allocationList;
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月14日
	 * 
	 *          取得库间调拨登记状态的流水号
	 * @param allocateInfo
	 *            查询条件
	 * @return 流水号
	 */
	public String getCashBetweenRegistAllId(AllAllocateInfo allocateInfo) {
		return allocateInfoDao.getCashBetweenRegistAllId(allocateInfo);
	}

	/**
	 * @author ChengShu
	 * @date 2014/1/14
	 * 
	 *       取得现金出入库的合计
	 * @param 查询条件
	 */

	/**
	 * @author wangbaozhong
	 * @version 2015年9月10日
	 * 
	 *          根据页面条件生成Map的Key值
	 * @param item
	 *            调拨物品
	 * @return Map的Key值
	 */
	public String getAllAllocateItemMapKey(AllAllocateItem item) {
		StringBuilder strKeyBud = new StringBuilder();
		// 币种
		strKeyBud.append(StringUtils.isEmpty(item.getCurrency()) ? "" : item.getCurrency());
		// 类别
		strKeyBud.append(StringUtils.isEmpty(item.getClassification()) ? "" : item.getClassification());
		// 套别
		strKeyBud.append(StringUtils.isEmpty(item.getSets()) ? "" : item.getSets());
		// 现金材质
		strKeyBud.append(StringUtils.isEmpty(item.getCash()) ? "" : item.getCash());
		// 券别
		strKeyBud.append(StringUtils.isEmpty(item.getDenomination()) ? "" : item.getDenomination());
		// 单位
		strKeyBud.append(StringUtils.isEmpty(item.getUnit()) ? "" : item.getUnit());

		return strKeyBud.toString();
	}

	/**
	 * @author qph
	 * @version 2017年7月5日
	 * 
	 *          根据页面条件生成Map的Key值
	 * @param item
	 *            调拨物品
	 * @return Map的Key值
	 */
	public String getAllAllocateItemMapKey1(AllAllocateItem item) {
		StringBuilder strKeyBud = new StringBuilder();
		// 币种
		strKeyBud.append(item.getscurrency(item.getGoodsId()));
		// 类别
		strKeyBud.append(item.getsclassification(item.getGoodsId()));
		// 套别
		strKeyBud.append(item.getssets(item.getGoodsId()));
		// 现金材质
		strKeyBud.append(item.getscash(item.getGoodsId()));
		// 券别
		strKeyBud.append(item.getsdenomination(item.getGoodsId()));
		// 单位
		strKeyBud.append(item.getsunit(item.getGoodsId()));

		return strKeyBud.toString();
	}

	/**
	 * @author wangbaozhong
	 * @version 2015年9月07日
	 * 
	 *          插入库间现金调拨信息
	 * @param allAllocateItem
	 *            调拨物品明细
	 * @param allocationCash
	 *            登记用现金信息
	 */
	public void setCash(AllAllocateInfo allAllocateInfo, AllAllocateInfo allocationCash) {

		AllAllocateItem item = allAllocateInfo.getAllAllocateItem();

		// 现金库出库登记时，由于币种类别一致，更新数据时，重新修改Map的Key值
		if (AllocationConstant.PageType.CashOutStore.equals(allAllocateInfo.getPageType())) {
			// 新作成出库登记时
			if (StringUtils.isBlank(allocationCash.getCurrency())
					|| StringUtils.isBlank(allocationCash.getClassification())) {
				allocationCash.setClassification(item.getClassification());
				allocationCash.setCurrency(item.getCurrency());
			}
			// 出库币种和类别变更，清除Map
			if (!allocationCash.getClassification().equals(item.getClassification())
					|| !allocationCash.getCurrency().equals(item.getCurrency())) {
				allocationCash.setClassification(item.getClassification());
				allocationCash.setCurrency(item.getCurrency());
				allocationCash.getAllAllocateItemMap().clear();
			} else {
				// 更新出库币种和类别，并做成新主键
				Map<String, AllAllocateItem> cashOutStoreUpdateMap = Maps.newTreeMap();
				Iterator<String> innerKeyIterator = allocationCash.getAllAllocateItemMap().keySet().iterator();
				while (innerKeyIterator.hasNext()) {
					AllAllocateItem tempItem = allocationCash.getAllAllocateItemMap().get(innerKeyIterator.next());
					// 现金库出库登记一一种类别
					tempItem.setClassification(item.getClassification());
					tempItem.setCurrency(item.getCurrency());
					if (!StringUtils.isBlank(allAllocateInfo.getAllId())) {
						tempItem.setAllItemsId(IdGen.uuid());// 更新时，作成新主键
					}
					String strOutStoreKey = getAllAllocateItemMapKey(tempItem);
					cashOutStoreUpdateMap.put(strOutStoreKey, tempItem);
				}
				allocationCash.setAllAllocateItemMap(cashOutStoreUpdateMap);
			}
		}

		String strMapKey = getAllAllocateItemMapKey(item);
		// 如果存在相同 套别-券别-单位 的明细，将
		if (allocationCash.getAllAllocateItemMap().containsKey(strMapKey)) {
			AllAllocateItem tempItem = allocationCash.getAllAllocateItemMap().get(strMapKey);
			// 整点室出库登记以外时 数量增加，金额累加
			tempItem.setMoneyNumber(tempItem.getMoneyNumber() + item.getMoneyNumber());
			tempItem.setMoneyAmount(tempItem.getMoneyAmount().add(item.getMoneyAmount()));

		} else {
			// 否则追加整条明细
			item.setAllItemsId(IdGen.uuid());
			allocationCash.getAllAllocateItemMap().put(strMapKey, item);
		}

		// 计算总金额
		Iterator<String> keyIterator = allocationCash.getAllAllocateItemMap().keySet().iterator();
		BigDecimal bDRegisterAmount = new BigDecimal(0);

		while (keyIterator.hasNext()) {
			AllAllocateItem tempItem = allocationCash.getAllAllocateItemMap().get(keyIterator.next());
			if (AllocationConstant.PageType.CashOutClassficationRoom.equals(allAllocateInfo.getPageType())) {
				// 整点室出库时
				bDRegisterAmount = bDRegisterAmount.add(tempItem.getMoneyAmount());
			} else {
				// 总金额
				bDRegisterAmount = bDRegisterAmount.add(tempItem.getMoneyAmount());
			}
		}

		// 整点室出库,库房出库时为登记金额
		if (AllocationConstant.PageType.CashOutClassficationRoom.equals(allAllocateInfo.getPageType())
				|| AllocationConstant.PageType.CashOutStore.equals(allAllocateInfo.getPageType())) {
			allocationCash.setRegisterAmount(bDRegisterAmount);
		} else {
			// 库房入库时为确认金额
			allocationCash.setConfirmAmount(bDRegisterAmount);
		}
	}

	/**
	 * @author wangbaozhong
	 * @version 2015年9月07日
	 * 
	 *          删除库间现金调拨信息
	 * @param allocateInfo
	 *            删除条件
	 * @return 插入结果
	 */
	@Transactional(readOnly = false)
	public int deleteCash(AllAllocateInfo allocateInfo) {

		// 删除件数
		int allResult = 0;
		int detailResult = 0;
		int itemResult = 0;
		int allTempResult = 0;
		// 删除调拨主表
		allocateInfo.preUpdate();
		allResult = allocateInfoDao.delete(allocateInfo);
		allTempResult = tempAllocateInfoDao.delete(allocateInfo);
		if (allResult + allTempResult == 0) {
			String strMessageContent = "流水单号：" + allocateInfo.getAllId() + "删除失败！";
			throw new BusinessException("message.E2015", strMessageContent, new String[] { "调拨主表" });
		}

		// 删除调拨详细表
		if (allocateInfo.getAllDetailList().size() > 0) {
			AllAllocateDetail detail = allocateInfo.getAllDetailList().get(0);
			detail.setAllocationInfo(allocateInfo);
			detailResult = allocateDetailDao.delete(detail);
			if (detailResult == 0) {
				String strMessageContent = "流水单号：" + detail.getId() + "删除失败！";
				throw new BusinessException("message.E2015", strMessageContent, new String[] { "调拨详细表" });
			}
		}

		// 删除调拨物品表
		AllAllocateItem item = new AllAllocateItem();
		item.setAllocationInfo(allocateInfo);
		item.preUpdate();
		itemResult = allAllocateItemDao.delete(item);

		return allResult + detailResult + itemResult;
	}

	/**
	 * 
	 * @author LLF
	 * @version 2015年6月19日
	 * 
	 *          根据箱袋编号获取出库调拨信息
	 * @param allocateInfo
	 * @return
	 */
	public List<AllAllocateInfo> findAllocationByNo(AllAllocateInfo allocateInfo) {
		return allocateInfoDao.findAllocationByNo(allocateInfo);
	}

	/**
	 * @author wangbaozhong
	 * @version 2015年9月07日
	 * 
	 *          插入调拨信息
	 * @param allocateInfo
	 *            调拨信息内容
	 * @param insertFalg
	 *            创建标识
	 * @return 插入结果
	 */
	@Transactional(readOnly = false)
	public int saveAllocation(AllAllocateInfo allocateInfo) {

		// 插入件数
		int iAllResultCnt = 0;
		int iDetailResultCnt = 0;
		int iItemResultCnt = 0;

		// 新规的场合，插入主表
		if (StringUtils.isBlank(allocateInfo.getAllId())) {
			// 登记机构
			allocateInfo.setrOffice(allocateInfo.getLoginUser().getOffice());
			// 主键空的场合，设置主键
			allocateInfo.setAllId(BusinessUtils.getNewBusinessNo(
					Global.getConfig("businessType.allocation.between.cash"), allocateInfo.getLoginUser().getOffice()));

			// 执行插入处理
			allocateInfo.preInsert();
			iAllResultCnt = allocateInfoDao.insert(allocateInfo);

			// 插入调拨详细表
			allocateInfo.getAllAllocateDetail().setAllocationInfo(allocateInfo);
			iDetailResultCnt = allocateDetailDao.insert(allocateInfo.getAllAllocateDetail());

			// 插入调拨物品表
			Iterator<String> keyIterator = allocateInfo.getAllAllocateItemMap().keySet().iterator();
			while (keyIterator.hasNext()) {
				AllAllocateItem tempItem = allocateInfo.getAllAllocateItemMap().get(keyIterator.next());
				tempItem.setAllocationInfo(allocateInfo);
				tempItem.preInsert();
				iItemResultCnt += allAllocateItemDao.insert(tempItem);
			}

		} else {
			// 更新的场合，更新金额和更新者，删除已经存在的详细信息
			// 执行更新调拨主表
			allocateInfo.preUpdate();
			iAllResultCnt = allocateInfoDao.update(allocateInfo);

			// 删除已经存在的调拨详细表信息
			AllAllocateDetail delDetail = new AllAllocateDetail();
			delDetail.setAllId(allocateInfo.getAllId());
			allocateDetailDao.deleteDetailByAllId(delDetail);
			// 插入调拨详细表
			allocateInfo.getAllAllocateDetail().setAllocationInfo(allocateInfo);
			iDetailResultCnt = allocateDetailDao.insert(allocateInfo.getAllAllocateDetail());
			// 更新调拨物品表
			AllAllocateItem conditionItem = new AllAllocateItem();
			conditionItem.setAllocationInfo(allocateInfo);
			List<AllAllocateItem> itemsList = allAllocateItemDao.findItemsListByAllId(conditionItem);

			for (AllAllocateItem item : itemsList) {
				String strMapKey = getAllAllocateItemMapKey(item);
				if (allocateInfo.getAllAllocateItemMap().containsKey(strMapKey)) {
					// 如果存在相同物品，执行更新
					item.setMoneyNumber(allocateInfo.getAllAllocateItemMap().get(strMapKey).getMoneyNumber());
					item.setMoneyAmount(allocateInfo.getAllAllocateItemMap().get(strMapKey).getMoneyAmount());
					item.preUpdate();
					item.setAllocationInfo(allocateInfo);
					allAllocateItemDao.update(item);
					allocateInfo.getAllAllocateItemMap().remove(strMapKey);
				} else {
					// 如果不存在相同物品执行删除
					item.preUpdate();
					allAllocateItemDao.deleteDetail(item);
				}
			}
			// 插入调拨物品表
			Iterator<String> keyIterator = allocateInfo.getAllAllocateItemMap().keySet().iterator();
			while (keyIterator.hasNext()) {
				AllAllocateItem tempItem = allocateInfo.getAllAllocateItemMap().get(keyIterator.next());
				tempItem.setAllocationInfo(allocateInfo);
				tempItem.preInsert();
				iItemResultCnt += allAllocateItemDao.insert(tempItem);
			}

		}

		return iAllResultCnt + iDetailResultCnt + iItemResultCnt;
	}

	/**
	 * 更新接收状态及金额
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月10日
	 * 
	 * 
	 * @param allocateInfo
	 *            更新条件
	 * @return 更新数量
	 */
	@Transactional(readOnly = false)
	public int updateBetweenAcceptStatus(AllAllocateInfo allocateInfo) {
		allocateInfo.preUpdate();
		return allocateInfoDao.updateBetweenAcceptStatus(allocateInfo);
	}

	/**
	 * 1：插入调拨主表信息 2：插入调拨详细表信息
	 * 
	 * @author Lemon
	 * 
	 * @param allocateInfo
	 */
	@Transactional(readOnly = false)
	public void insertAllocation(AllAllocateInfo allocateInfo) {
		// 插入调拨主表信息
		int insertInfoResult = allocateInfoDao.insert(allocateInfo);
		if (insertInfoResult == 0) {
			String strMessageContent = "调拨主表：" + allocateInfo.getAllId() + "更新失败！";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		}
		// 插入调拨详细表
		for (AllAllocateDetail box : allocateInfo.getAllDetailList()) {
			box.setAllocationInfo(allocateInfo);
			int insertDetailResult = allocateDetailDao.insert(box);
			if (insertDetailResult == 0) {
				String strMessageContent = "调拨详细：" + allocateInfo.getAllId() + "更新失败！";
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
			}
		}
		// 插入调拨物品表
		for (AllAllocateItem item : allocateInfo.getAllAllocateItemList()) {
			item.setAllocationInfo(allocateInfo);
			int insertItemResult = allAllocateItemDao.insert(item);
			if (insertItemResult == 0) {
				String strMessageContent = "调拨详细：" + allocateInfo.getAllId() + "更新失败！";
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
			}
		}

	}

	/**
	 * 1：更新调拨主表信息 2：插入调拨详细表信息
	 * 
	 * @author Lemon
	 * 
	 * @param allocateInfo
	 */
	@Transactional(readOnly = false)
	public void updateAllocation(AllAllocateInfo allocateInfo) {
		// 插入调拨主表信息
		allocateInfoDao.update(allocateInfo);

		AllAllocateDetail allallocatedetail = allocateInfo.getAllAllocateDetail();
		allallocatedetail.setAllocationInfo(allocateInfo);
		allocateDetailDao.deleteDetailByAllId(allallocatedetail);

		// 插入调拨详细表
		for (AllAllocateDetail box : allocateInfo.getAllDetailList()) {
			box.setAllocationInfo(allocateInfo);
			box.setPdaScanDate(new Date());
			int insertresult = allocateDetailDao.insert(box);
			if (insertresult == 0) {
				String strMessageContent = "流水单号：" + box.getAllDetailId() + ")(调拨详细表)更新失败！";
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
			}
		}
	}

	/**
	 * @author chengshu
	 * @version 2015/09/23
	 * 
	 *          插入交接信息
	 * @param handoverInfo
	 *            交接内容
	 * @return 插入结果
	 */
	@Transactional(readOnly = false)
	public int saveHandover(AllHandoverInfo handoverInfo) {

		// 插入件数
		int allResult = 0;

		// 插入主表
		allResult = handoverInfoDao.insert(handoverInfo);

		return allResult;
	}

	/**
	 * 获取交接任务列表
	 * 
	 * @author Lemon
	 * 
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> getHandoverTaskList(Map<String, Object> param) {
		return handoverInfoDao.getHandoverTaskList(param);
	}

	/**
	 * 获取交接任务详细信息
	 * 
	 * @author Lemon
	 * 
	 * @param param
	 * @return
	 */
	public AllHandoverInfo getHandoverTask(Map<String, Object> param) {
		return handoverInfoDao.get(param);
	}

	/**
	 * 
	 * @author suiwei
	 * @version 2015年9月8日
	 * 
	 *          添加、修改预约明细
	 * @param allAllocateInfo
	 *            主表数据
	 * @param allocationCash
	 *            物品表数据
	 */
	public String setOrderCash(AllAllocateInfo allAllocateInfo, AllAllocateInfo allocationCash) {

		AllAllocateItem item = this.getItemCopy(allAllocateInfo.getAllAllocateItem());
		String strMapKey = getAllAllocateItemMapKey(item);
		// 网点端
		if (allocationCash.getAllAllocateItemMap().containsKey(strMapKey)) {
			// 添加时：如果存在相同 套别-券别-单位 的明细，将数量增加
			return Constant.FAILED;
		} else {
			// 添加时：追加整条明细
			// 设置物品表主键
			item.setAllItemsId(IdGen.uuid());
			// 配款数量
			item.setConfirmNumber(0L);
			// 设置goodsId
			item.setGoodsId(strMapKey);

			allocationCash.getAllAllocateItemMap().put(strMapKey, item);
		}

		// 重新计算金额
		Map<String, AllAllocateItem> countMap = countCashByCurrencyFromMap(allocationCash.getAllAllocateItemMap());
		allocationCash.setCountItemMap(countMap);
		return Constant.SUCCESS;

	}

	/**
	 * 
	 * @author suiwei
	 * @version 2015年9月16日
	 * 
	 *          设置输入项内容
	 * @param item
	 * @return 输入项内容
	 */
	private AllAllocateItem getItemCopy(AllAllocateItem item) {
		AllAllocateItem rtnItem = new AllAllocateItem();
		rtnItem.setAllItemsId(item.getAllItemsId());
		rtnItem.setClassification(item.getClassification());
		rtnItem.setCash(item.getCash());
		rtnItem.setConfirmNumber(item.getConfirmNumber());
		rtnItem.setConfirmAmount(item.getConfirmAmount());
		rtnItem.setCurrency(item.getCurrency());
		rtnItem.setDenomination(item.getDenomination());
		rtnItem.setMoneyNumber(item.getMoneyNumber());
		rtnItem.setMoneyAmount(item.getMoneyAmount());
		rtnItem.setUnit(item.getUnit());
		rtnItem.setSets(item.getSets());
		return rtnItem;
	}

	/**
	 * 
	 * @author suiwei
	 * @version 2015年9月8日
	 * 
	 *          保存预约信息
	 * @param allocateInfo
	 *            需要保存的数据
	 * @return 插入的数据条数
	 */
	@Transactional(readOnly = false)
	public int saveOrderAllocation(AllAllocateInfo allocateInfo) {

		// 插入件数
		int iAllResultCnt = 0;
		int iItemResultCnt = 0;
		if (null != allocateInfo.getCountItemMap()) {
			// 获取预约金额
			Iterator<String> insertKeyIterator1 = allocateInfo.getCountItemMap().keySet().iterator();
			AllAllocateItem registAmountItem = allocateInfo.getCountItemMap().get(insertKeyIterator1.next());
			allocateInfo.setRegisterAmount(registAmountItem.getMoneyAmount());
		}
		// 新规的场合
		if (StringUtils.isBlank(allocateInfo.getAllId())) {

			// 插入主表
			// 主键空的场合，设置主键
			allocateInfo.setAllId(BusinessUtils.getNewBusinessNo(Global.getConfig("businessType.allocation.cashOrder"),
					allocateInfo.getLoginUser().getOffice()));

			// 业务类型
			allocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_Reservation);
			// 交接状态（现金预约未配款）
			allocateInfo.setStatus(AllocationConstant.Status.Register);
			// 登记机构
			allocateInfo.setrOffice(allocateInfo.getLoginUser().getOffice());
			// 设置接收机构
			// 取得当前机构金库编号
			Office aOffice = BusinessUtils.getCashCenterByOffice(allocateInfo.getLoginUser().getOffice());
			allocateInfo.setaOffice(aOffice);

			if (AllocationConstant.PageType.PointAdd.equals(allocateInfo.getPageType())) {
				// 根据登记机构设置线路
				List<StoRouteInfo> list = stoRouteInfoDao
						.searchStoRouteInfoByOffice(allocateInfo.getLoginUser().getOffice().getId());
				if (Collections3.isEmpty(list)) {
					return iAllResultCnt;
				}
				allocateInfo.setRouteId(list.get(0).getId());
				allocateInfo.setRouteName(list.get(0).getRouteName());
			}
			if (AllocationConstant.PageType.PointTempAdd.equals(allocateInfo.getPageType())) {
				// 根据登记机构设置线路
				allocateInfo.setRouteId(IdGen.uuid());

				AllAllocateInfo allocate = new AllAllocateInfo();
				// 查询条件：开始时间
				allocate.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(new Date()),
						AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
				// 查询条件：结束时间
				allocate.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(new Date()),
						AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
				allocate.setaOffice(aOffice);
				List<AllAllocateInfo> list = tempAllocateInfoDao.findList(allocate);
				Integer num = list.size() + 1;
				allocateInfo.setRouteName("临时线路" + num.toString());
			}

			// 有效标识
			allocateInfo.setDelFlag(AllocationConstant.deleteFlag.Valid);

			// 执行插入处理
			allocateInfo.preInsert();

			// 插入物品表
			Iterator<String> insertKeyIterator = allocateInfo.getAllAllocateItemMap().keySet().iterator();

			while (insertKeyIterator.hasNext()) {
				String goodsId = insertKeyIterator.next();
				AllAllocateItem tempItem = allocateInfo.getAllAllocateItemMap().get(goodsId);

				// 设置物品表主键
				tempItem.setAllItemsId(IdGen.uuid());
				tempItem.setAllId(allocateInfo.getAllId());
				tempItem.setAllocationInfo(allocateInfo);
				tempItem.preInsert();
				tempItem.setConfirmFlag(AllocationConstant.confirmFlag.Appointment);
				tempItem.setGoodsId(goodsId);
				iItemResultCnt += allAllocateItemDao.insert(tempItem);
			}
			// 设置登记金额
			// allocateInfo.setRegisterAmount(tempItem.getMoneyAmount());
			if (AllocationConstant.PageType.PointAdd.equals(allocateInfo.getPageType())) {
				iAllResultCnt = allocateInfoDao.insert(allocateInfo);
			}
			if (AllocationConstant.PageType.PointTempAdd.equals(allocateInfo.getPageType())) {
				iAllResultCnt = tempAllocateInfoDao.insert(allocateInfo);
			}

		} else {
			// 一致性校验
			checkVersion(allocateInfo);

			// 更新的场合，更新金额和更新者，删除已经存在的详细信息
			AllAllocateItem delItem = new AllAllocateItem();
			// 流水号
			delItem.setAllId(allocateInfo.getAllId());
			// 登记种别
			// delItem.setRegistType(AllocationConstant.RegistType.RegistPoint);
			// 设置更新状态
			delItem.preUpdate();
			// 物品表中删除旧数据
			iItemResultCnt += allAllocateItemDao.delOrderDetail(delItem);

			// 物品表中插入修改后的新数据
			Iterator<String> keyIterator = allocateInfo.getAllAllocateItemMap().keySet().iterator();

			while (keyIterator.hasNext()) {
				AllAllocateItem tempItem1 = allocateInfo.getAllAllocateItemMap().get(keyIterator.next());

				// 设置物品表主键
				tempItem1.setAllItemsId(IdGen.uuid());
				// 设置流水号
				tempItem1.setAllocationInfo(allocateInfo);
				// 设置确认标识
				tempItem1.setConfirmFlag(AllocationConstant.confirmFlag.Appointment);

				tempItem1.preInsert();
				iItemResultCnt += allAllocateItemDao.insert(tempItem1);
			}

			// 执行插入处理
			allocateInfo.preUpdate();
			if (AllocationConstant.PageType.PointEdit.equals(allocateInfo.getPageType())) {
				iAllResultCnt = allocateInfoDao.updateAllocateInfoStatus(allocateInfo);
			}
			if (AllocationConstant.PageType.PointTempEdit.equals(allocateInfo.getPageType())) {
				iAllResultCnt = tempAllocateInfoDao.updateAllocateInfoStatus(allocateInfo);
			}

		}
		return iAllResultCnt + iItemResultCnt;
	}

	/**
	 * 配款修改时，回滚修改前的库存
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月30日
	 * 
	 * 
	 * @param allocateInfo
	 *            查询配款条件
	 * @return 变更成功
	 */
	public String surplusStoreRollback(AllAllocateInfo allocateInfo) {

		Map<String, AllAllocateItem> delItemMap = getOrderedItem(allocateInfo);
		// 变更预剩库存
		return updateSurplusStore(allocateInfo, delItemMap, allocateInfo.getLoginUser().getOffice().getId());
	}

	/**
	 * 取得已配款项目
	 * 
	 * @author wangbaozhong
	 * @version 2015年10月15日
	 * 
	 * 
	 * @param allocateInfo
	 *            查询配款条件
	 * @return 已配款项目
	 */
	public Map<String, AllAllocateItem> getOrderedItem(AllAllocateInfo allocateInfo) {
		AllAllocateItem orderedItem = new AllAllocateItem();
		// 流水号
		orderedItem.setAllId(allocateInfo.getAllId());
		// 回滚修改前的库存
		List<AllAllocateItem> delItemList = allAllocateItemDao.findItemsList(orderedItem);
		Map<String, AllAllocateItem> orderedItemMap = Maps.newHashMap();
		String strMapKey = null;
		for (AllAllocateItem item : delItemList) {
			strMapKey = getAllAllocateItemMapKey(item);
			item.setMoneyNumber(item.getMoneyNumber());
			orderedItemMap.put(strMapKey, item);
		}

		return orderedItemMap;
	}

	/**
	 * 
	 * @author suiwei
	 * @version 2015年9月8日
	 * 
	 *          保存配款信息
	 * @param allocateInfo
	 *            需要保存的数据
	 * @return 插入的数据条数
	 */
	@Transactional(readOnly = false)
	public int saveQuotaAllocation(AllAllocateInfo allocateInfo) {

		// 插入件数
		int iAllResultCnt = 0;
		int iItemResultCnt = 0;

		// 更新的场合，更新金额和更新者，删除已经存在的详细信息
		AllAllocateItem delItem = new AllAllocateItem();
		// 流水号
		delItem.setAllId(allocateInfo.getAllId());
		// 设置更新状态
		delItem.preUpdate();
		Iterator<String> keyIterator = allocateInfo.getAllAllocateItemMap().keySet().iterator();
		AllAllocateItem tempItem = null;
		while (keyIterator.hasNext()) {
			tempItem = allocateInfo.getAllAllocateItemMap().get(keyIterator.next());

			// 设置配款金额
			tempItem.setMoneyNumber(tempItem.getConfirmNumber());
			tempItem.setMoneyAmount(tempItem.getConfirmAmount());

			// 设置物品表主键
			tempItem.setAllItemsId(IdGen.uuid());
			// 设置流水号
			tempItem.setAllocationInfo(allocateInfo);
			// 设置确认标识
			tempItem.setConfirmFlag(AllocationConstant.confirmFlag.Confirm);

			tempItem.preInsert();
			iItemResultCnt += allAllocateItemDao.insert(tempItem);
		}

		// 更新主表
		// 交接状态（已确认）
		allocateInfo.setStatus(AllocationConstant.Status.BetweenConfirm);
		// 接收机构
		allocateInfo.setaOffice(allocateInfo.getLoginUser().getOffice());
		// 确认人
		allocateInfo.setConfirmName(allocateInfo.getLoginUser().getName());
		// 确认日期
		allocateInfo.setConfirmDate(new Date());
		// 确认金额
		if (!allocateInfo.getCountQuotaItemMap().isEmpty()) {
			Iterator<String> insertKeyIterator1 = allocateInfo.getCountQuotaItemMap().keySet().iterator();
			AllAllocateItem registAmountItem = allocateInfo.getCountQuotaItemMap().get(insertKeyIterator1.next());
			allocateInfo.setConfirmAmount(registAmountItem.getConfirmAmount());
		} else {
			Iterator<String> insertKeyIterator1 = allocateInfo.getCountItemMap().keySet().iterator();
			AllAllocateItem registAmountItem = allocateInfo.getCountItemMap().get(insertKeyIterator1.next());
			allocateInfo.setConfirmAmount(registAmountItem.getMoneyAmount());
		}
		// 执行插入处理
		allocateInfo.preUpdate();
		iAllResultCnt = allocateInfoDao.updateAllocateInfoStatus(allocateInfo);

		// 变更预剩库存
		keyIterator = allocateInfo.getAllAllocateItemMap().keySet().iterator();
		while (keyIterator.hasNext()) {
			AllAllocateItem tempItem1 = allocateInfo.getAllAllocateItemMap().get(keyIterator.next());
			tempItem1.setMoneyNumber(-tempItem1.getMoneyNumber());
		}
		updateSurplusStore(allocateInfo, allocateInfo.getAllAllocateItemMap(),
				allocateInfo.getLoginUser().getOffice().getId());

		return iAllResultCnt + iItemResultCnt;
	}

	/**
	 * 
	 * @author suiwei
	 * @version 2015年9月14日
	 * 
	 *          合并预约、配款信息
	 * @param allocateInfo
	 *            预约、配款信息
	 * @return 合并后的预约配款信息
	 */
	public AllAllocateInfo unionOrderInfo(AllAllocateInfo allocateInfo) {
		// 组合后的预约、配款信息
		Map<String, AllAllocateItem> resultMap = Maps.newTreeMap();
		// 预约信息
		Map<String, AllAllocateItem> registMap = Maps.newTreeMap();
		// 拆分预约、配款信息
		Iterator<String> keyIterator = allocateInfo.getAllAllocateItemMap().keySet().iterator();
		while (keyIterator.hasNext()) {
			String strMapKey = keyIterator.next();
			AllAllocateItem tempItem = allocateInfo.getAllAllocateItemMap().get(strMapKey);
			registMap.put(strMapKey, tempItem);
		}
		Iterator<String> resultkeyIterator = resultMap.keySet().iterator();
		while (resultkeyIterator.hasNext()) {
			registMap.remove(resultkeyIterator.next());
		}

		// 设置未配款项的配款数量设定为预约数量，将未配款金额设定为预约金额
		Iterator<String> noQuotakeyIterator = registMap.keySet().iterator();
		while (noQuotakeyIterator.hasNext()) {
			String registKey = noQuotakeyIterator.next();
			AllAllocateItem noTempItem = registMap.get(registKey);
			noTempItem.setConfirmNumber(noTempItem.getMoneyNumber());
			noTempItem.setConfirmAmount(noTempItem.getMoneyAmount());
			noTempItem.setCurrency(noTempItem.getscurrency(noTempItem.getGoodsId()));
			noTempItem.setClassification(noTempItem.getsclassification(noTempItem.getGoodsId()));
			noTempItem.setSets(noTempItem.getssets(noTempItem.getGoodsId()));
			noTempItem.setCash(noTempItem.getscash(noTempItem.getGoodsId()));
			noTempItem.setDenomination(noTempItem.getsdenomination(noTempItem.getGoodsId()));
			noTempItem.setUnit(noTempItem.getsunit(noTempItem.getGoodsId()));
			resultMap.put(registKey, noTempItem);
		}

		allocateInfo.setAllAllocateItemMap(resultMap);
		return allocateInfo;
	}

	/**
	 * 
	 * @author suiwei
	 * @version 2015年9月14日
	 * 
	 *          查询是否有预约信息
	 * @param allocateInfo
	 *            查询条件
	 * @return 预约信息
	 */
	public AllAllocateInfo orderIsExit(AllAllocateInfo allocateInfo) {
		AllAllocateInfo resultAllAllocateInfo = null;
		// 查询条件：开始时间
		if (allocateInfo.getCreateTimeStart() != null) {
			allocateInfo
					.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(allocateInfo.getCreateTimeStart()),
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 查询条件：结束时间
		if (allocateInfo.getCreateTimeEnd() != null) {
			allocateInfo.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(allocateInfo.getCreateTimeEnd()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}

		List<String> statusList = Lists.newArrayList();
		statusList.add(AllocationConstant.Status.Register);
		statusList.add(AllocationConstant.Status.BetweenConfirm);
		statusList.add(AllocationConstant.Status.CashOrderQuotaYes);
		allocateInfo.setStatuses(statusList);
		// 根据查询条件，取得预约全部信息
		List<AllAllocateInfo> allocationList = allocateInfoDao.findAllocationList(allocateInfo);

		if (allocationList.size() == 1) {
			AllAllocateInfo tempAllAllocateInfo = allocationList.get(0);
			resultAllAllocateInfo = this.getAllocate(tempAllAllocateInfo.getAllId());
		}

		return resultAllAllocateInfo;
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月27日
	 * 
	 *          合并上缴接收信息
	 * @param allocateInfo
	 *            上缴接收信息
	 * @return 合并后的上缴接收信息
	 */
	public AllAllocateInfo unionHandinInfo(AllAllocateInfo allocateInfo) {
		// 组合后的上缴、接收信息
		Map<String, AllAllocateItem> resultMap = Maps.newTreeMap();
		// 上缴信息
		Map<String, AllAllocateItem> registMap = Maps.newTreeMap();
		// 接收信息
		Map<String, AllAllocateItem> confirmMap = Maps.newTreeMap();

		// 拆分上缴、接收信息
		Iterator<String> keyIterator = allocateInfo.getAllAllocateItemMap().keySet().iterator();
		while (keyIterator.hasNext()) {
			String strMapKey = keyIterator.next();
			AllAllocateItem tempItem = allocateInfo.getAllAllocateItemMap().get(strMapKey);
			// map key 去除登记种别
			strMapKey = strMapKey.substring(2);
			if (AllocationConstant.RegistType.RegistStore.equals(tempItem.getRegistType())) {
				confirmMap.put(strMapKey, tempItem);
			} else {
				registMap.put(strMapKey, tempItem);
			}
		}

		// 设置上缴与接收相匹配的金额，并删除相同键的内容
		Iterator<String> registkeyIterator = registMap.keySet().iterator();

		while (registkeyIterator.hasNext()) {
			String registKey = registkeyIterator.next();

			if (confirmMap.containsKey(registKey)) {
				// 接收额合并到上缴数据中
				registMap.get(registKey).setConfirmNumber(confirmMap.get(registKey).getMoneyNumber());
				registMap.get(registKey).setConfirmAmount(confirmMap.get(registKey).getMoneyAmount());
				resultMap.put(registKey, registMap.get(registKey));
				confirmMap.remove(registKey);
			}
		}
		Iterator<String> resultkeyIterator = resultMap.keySet().iterator();
		while (resultkeyIterator.hasNext()) {
			registMap.remove(resultkeyIterator.next());
		}

		// 设置未接收项的接收数量设定为0，将未接收金额设定为0
		Iterator<String> noQuotakeyIterator = registMap.keySet().iterator();
		while (noQuotakeyIterator.hasNext()) {
			String registKey = noQuotakeyIterator.next();
			AllAllocateItem noTempItem = registMap.get(registKey);
			noTempItem.setConfirmNumber(0L);
			noTempItem.setConfirmAmount(new BigDecimal(0.00));
			resultMap.put(registKey, noTempItem);
		}
		// 设置新增接收项的上缴金额为0，上缴数量设定为0
		Iterator<String> newQuotakeyIterator = confirmMap.keySet().iterator();
		while (newQuotakeyIterator.hasNext()) {
			String confirmKey = newQuotakeyIterator.next();
			AllAllocateItem newTempItem = confirmMap.get(confirmKey);
			newTempItem.setConfirmNumber(newTempItem.getMoneyNumber());
			newTempItem.setConfirmAmount(newTempItem.getMoneyAmount());
			newTempItem.setMoneyNumber(0L);
			newTempItem.setMoneyAmount(new BigDecimal(0.00));

			resultMap.put(confirmKey, newTempItem);
		}

		allocateInfo.setAllAllocateItemMap(resultMap);
		return allocateInfo;
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月23日
	 * 
	 *          保存上缴信息
	 * @param allocateInfo
	 *            需要保存的数据
	 * @return 插入的数据条数
	 */
	@Transactional(readOnly = false)
	public int saveHandinAllocation(AllAllocateInfo allocateInfo) {

		// 插入件数
		int iAllResultCnt = 0;
		int iItemResultCnt = 0;
		// 新规的场合
		if (StringUtils.isBlank(allocateInfo.getAllId())) {

			// 插入主表
			// 主键空的场合，设置主键
			allocateInfo.setAllId(BusinessUtils.getNewBusinessNo(Global.getConfig("businessType.allocation.cashHandin"),
					allocateInfo.getLoginUser().getOffice()));

			// 业务类型
			allocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_HandIn);
			// 交接状态（网点上缴金额未确认）
			allocateInfo.setStatus(AllocationConstant.Status.BankOutletsHandInConfirmNo);
			// 登记机构
			allocateInfo.setrOffice(allocateInfo.getLoginUser().getOffice());
			// 设置接收机构
			// 取得当前机构金库编号
			Office aOffice = BusinessUtils.getCashCenterByOffice(allocateInfo.getLoginUser().getOffice());
			allocateInfo.setaOffice(aOffice);

			// 有效标识
			allocateInfo.setDelFlag(AllocationConstant.deleteFlag.Valid);
			// 执行插入处理
			allocateInfo.preInsert();

			iAllResultCnt = allocateInfoDao.insert(allocateInfo);

			// 插入物品表
			Iterator<String> insertKeyIterator = allocateInfo.getAllAllocateItemMap().keySet().iterator();
			while (insertKeyIterator.hasNext()) {
				AllAllocateItem tempItem = allocateInfo.getAllAllocateItemMap().get(insertKeyIterator.next());
				tempItem.setAllocationInfo(allocateInfo);
				tempItem.preInsert();
				iItemResultCnt += allAllocateItemDao.insert(tempItem);
			}

		} else {
			// 更新的场合，更新金额和更新者，删除已经存在的详细信息
			AllAllocateItem delItem = new AllAllocateItem();
			// 流水号
			delItem.setAllId(allocateInfo.getAllId());
			// 登记种别
			delItem.setRegistType(AllocationConstant.RegistType.RegistPoint);
			// 设置更新状态
			delItem.preUpdate();
			// 物品表中删除旧数据
			iItemResultCnt += allAllocateItemDao.delOrderDetail(delItem);

			// 物品表中插入修改后的新数据
			Iterator<String> keyIterator = allocateInfo.getAllAllocateItemMap().keySet().iterator();
			while (keyIterator.hasNext()) {
				AllAllocateItem tempItem = allocateInfo.getAllAllocateItemMap().get(keyIterator.next());
				// 设置物品表主键
				tempItem.setAllItemsId(IdGen.uuid());
				// 设置流水号
				tempItem.setAllocationInfo(allocateInfo);

				tempItem.preInsert();
				iItemResultCnt += allAllocateItemDao.insert(tempItem);
			}

			// 更新主表
			// 执行插入处理
			allocateInfo.preUpdate();

			iAllResultCnt = allocateInfoDao.updateAllocateInfoStatus(allocateInfo);
		}
		return iAllResultCnt + iItemResultCnt;
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月23日
	 * 
	 *          保存网点上缴接收信息
	 * @param allocateInfo
	 *            需要保存的数据
	 * @return 插入的数据条数
	 */
	@Transactional(readOnly = false)
	public int saveAcceptAllocation(AllAllocateInfo allocateInfo) {

		// 插入件数
		int iAllResultCnt = 0;
		int iItemResultCnt = 0;

		// 更新的场合，更新金额和更新者，删除已经存在的详细信息
		AllAllocateItem delItem = new AllAllocateItem();
		// 流水号
		delItem.setAllId(allocateInfo.getAllId());
		// 登记种别
		delItem.setRegistType(AllocationConstant.RegistType.RegistStore);
		// 设置更新状态
		delItem.preUpdate();
		// 物品表中删除旧数据
		iItemResultCnt = allAllocateItemDao.delOrderDetail(delItem);

		// 物品表中插入修改后的新数据
		Iterator<String> keyIterator = allocateInfo.getAllAllocateItemMap().keySet().iterator();
		while (keyIterator.hasNext()) {
			AllAllocateItem tempItem = allocateInfo.getAllAllocateItemMap().get(keyIterator.next());

			// 设置物品表主键
			tempItem.setAllItemsId(IdGen.uuid());
			// 设置流水号
			tempItem.setAllocationInfo(allocateInfo);

			tempItem.preInsert();
			iItemResultCnt += allAllocateItemDao.insert(tempItem);
		}

		// 更新主表
		// 交接状态（网点上缴金额已确认 ）
		allocateInfo.setStatus(AllocationConstant.Status.BankOutletsHandInConfirmYes);
		// 接收机构
		allocateInfo.setaOffice(allocateInfo.getLoginUser().getOffice());

		// 执行插入处理
		allocateInfo.preUpdate();

		// // 设置接收人
		// allocateInfo.setAcceptBy(allocateInfo.getLoginUser());
		// allocateInfo.setAcceptName(allocateInfo.getLoginUser().getName());
		// // 设置接收日期
		// allocateInfo.setAcceptDate(new Date());

		iAllResultCnt = allocateInfoDao.updateAllocateInfoStatus(allocateInfo);

		// 更新库存信息
		this.updateStore(allocateInfo);

		return iAllResultCnt + iItemResultCnt;
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年10月28日
	 * 
	 *          保存同业预约配款信息
	 * @param allocateInfo
	 *            需要保存的数据
	 * @return 插入的数据条数
	 */
	@Transactional(readOnly = false)
	public int saveSameTradeOrderAllocation(AllAllocateInfo allocateInfo) {
		// 插入件数
		int iAllResultCnt = 0;
		int iItemResultCnt = 0;
		// 新规的场合
		if (StringUtils.isBlank(allocateInfo.getAllId())) {

			// 插入主表
			// 主键空的场合，设置主键
			allocateInfo.setAllId(BusinessUtils.getNewBusinessNo(Global.getConfig("businessType.allocation.cashOrder"),
					allocateInfo.getLoginUser().getOffice()));

			// 业务类型
			allocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_Reservation);
			// 交接状态（现金预约未配款）
			allocateInfo.setStatus(AllocationConstant.Status.CashOrderQuotaYes);
			// 登记机构
			allocateInfo.setrOffice(allocateInfo.getrOffice());
			// 接收机构
			allocateInfo.setaOffice(allocateInfo.getLoginUser().getOffice());
			// 执行插入处理
			allocateInfo.preUpdate();

			// // 设置配款人
			// allocateInfo.setAcceptBy(allocateInfo.getLoginUser());
			// allocateInfo.setAcceptName(allocateInfo.getLoginUser().getName());
			// // 设置配款日期
			// allocateInfo.setAcceptDate(new Date());
			// 有效标识
			allocateInfo.setDelFlag(AllocationConstant.deleteFlag.Valid);

			// 执行插入处理
			allocateInfo.preInsert();

			iAllResultCnt = allocateInfoDao.insert(allocateInfo);

			// 插入物品表
			Iterator<String> insertKeyIterator = allocateInfo.getAllAllocateItemMap().keySet().iterator();
			while (insertKeyIterator.hasNext()) {
				AllAllocateItem tempItem = allocateInfo.getAllAllocateItemMap().get(insertKeyIterator.next());
				// 设置登记种别
				tempItem.setRegistType(AllocationConstant.RegistType.RegistStore);
				tempItem.setAllocationInfo(allocateInfo);
				tempItem.preInsert();
				iItemResultCnt += allAllocateItemDao.insert(tempItem);
			}

		} else {

			// 更新的场合，更新金额和更新者，删除已经存在的详细信息
			AllAllocateItem delItem = new AllAllocateItem();
			// 流水号
			delItem.setAllId(allocateInfo.getAllId());
			// 登记种别
			delItem.setRegistType(AllocationConstant.RegistType.RegistStore);
			// 设置更新状态
			delItem.preUpdate();
			// 物品表中删除旧数据
			iItemResultCnt += allAllocateItemDao.delOrderDetail(delItem);

			// 物品表中插入修改后的新数据
			Iterator<String> keyIterator = allocateInfo.getAllAllocateItemMap().keySet().iterator();
			while (keyIterator.hasNext()) {
				AllAllocateItem tempItem = allocateInfo.getAllAllocateItemMap().get(keyIterator.next());
				// 设置物品表主键
				tempItem.setAllItemsId(IdGen.uuid());
				// 设置登记种别
				tempItem.setRegistType(AllocationConstant.RegistType.RegistStore);
				// 设置流水号
				tempItem.setAllocationInfo(allocateInfo);

				tempItem.preInsert();
				iItemResultCnt += allAllocateItemDao.insert(tempItem);
			}

			// 接收机构
			allocateInfo.setaOffice(allocateInfo.getLoginUser().getOffice());
			// 设置配款人
			// allocateInfo.setAcceptBy(allocateInfo.getLoginUser());
			// allocateInfo.setAcceptName(allocateInfo.getLoginUser().getName());
			// // 设置配款日期
			// allocateInfo.setAcceptDate(new Date());
			// 执行更新处理
			allocateInfo.preUpdate();
			// 更新主表
			iAllResultCnt = allocateInfoDao.updateAllocateInfoStatus(allocateInfo);
		}

		// 变更预剩库存
		Iterator<String> keyIterator = allocateInfo.getAllAllocateItemMap().keySet().iterator();
		while (keyIterator.hasNext()) {
			AllAllocateItem tempItem = allocateInfo.getAllAllocateItemMap().get(keyIterator.next());
			tempItem.setMoneyNumber(-tempItem.getMoneyNumber());
		}
		updateSurplusStore(allocateInfo, allocateInfo.getAllAllocateItemMap(),
				allocateInfo.getLoginUser().getOffice().getId());

		return iAllResultCnt + iItemResultCnt;
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年10月27日
	 * 
	 *          保存同业上缴接收信息
	 * @param allocateInfo
	 *            需要保存的数据
	 * @return 插入的数据条数
	 */
	@Transactional(readOnly = false)
	public int saveSameTradeHandinAllocation(AllAllocateInfo allocateInfo) {

		// 插入件数
		int iAllResultCnt = 0;
		int iItemResultCnt = 0;

		// // 设置接收人
		// allocateInfo.setAcceptBy(allocateInfo.getLoginUser());
		// allocateInfo.setAcceptName(allocateInfo.getLoginUser().getName());
		// // 设置接收日期
		// allocateInfo.setAcceptDate(new Date());

		// 新规的场合
		if (StringUtils.isBlank(allocateInfo.getAllId())) {

			// 插入主表
			// 主键空的场合，设置主键
			allocateInfo.setAllId(BusinessUtils.getNewBusinessNo(Global.getConfig("businessType.allocation.cashHandin"),
					allocateInfo.getLoginUser().getOffice()));

			// 业务类型
			allocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_HandIn);
			// 交接状态（同业变为已确认）
			allocateInfo.setStatus(AllocationConstant.Status.BankOutletsHandInConfirmYes);
			// 登记机构
			allocateInfo.setaOffice(allocateInfo.getLoginUser().getOffice());
			// 有效标识
			allocateInfo.setDelFlag(AllocationConstant.deleteFlag.Valid);

			// 执行插入处理
			allocateInfo.preInsert();

			iAllResultCnt = allocateInfoDao.insert(allocateInfo);

			// 插入物品表
			Iterator<String> insertKeyIterator = allocateInfo.getAllAllocateItemMap().keySet().iterator();
			while (insertKeyIterator.hasNext()) {
				AllAllocateItem tempItem = allocateInfo.getAllAllocateItemMap().get(insertKeyIterator.next());
				tempItem.setAllocationInfo(allocateInfo);
				tempItem.preInsert();
				iItemResultCnt += allAllocateItemDao.insert(tempItem);
			}

		} else {
			// 查询已经登记物品
			AllAllocateInfo oldAcceptAllocateInfo = allocateInfoDao.get(allocateInfo.getAllId());
			String strMapKey = "";
			for (AllAllocateItem item : oldAcceptAllocateInfo.getAllAllocateItemList()) {
				strMapKey = getAllAllocateItemMapKey(item);
				item.setMoneyNumber(-item.getMoneyNumber());
				oldAcceptAllocateInfo.getAllAllocateItemMap().put(strMapKey, item);
			}
			oldAcceptAllocateInfo.setLoginUser(allocateInfo.getLoginUser());
			// 还原库存信息
			this.updateStore(oldAcceptAllocateInfo);

			// 更新的场合，更新金额和更新者，删除已经存在的详细信息
			AllAllocateItem delItem = new AllAllocateItem();
			// 流水号
			delItem.setAllId(allocateInfo.getAllId());
			// 登记种别
			delItem.setRegistType(AllocationConstant.RegistType.RegistStore);
			// 设置更新状态
			delItem.preUpdate();
			// 物品表中删除旧数据
			iItemResultCnt += allAllocateItemDao.delOrderDetail(delItem);

			// 物品表中插入修改后的新数据
			Iterator<String> keyIterator = allocateInfo.getAllAllocateItemMap().keySet().iterator();
			while (keyIterator.hasNext()) {
				AllAllocateItem tempItem = allocateInfo.getAllAllocateItemMap().get(keyIterator.next());
				// 设置物品表主键
				tempItem.setAllItemsId(IdGen.uuid());
				// 设置流水号
				tempItem.setAllocationInfo(allocateInfo);

				tempItem.preInsert();
				iItemResultCnt += allAllocateItemDao.insert(tempItem);
			}

			// 更新主表
			// 执行插入处理
			allocateInfo.preUpdate();

			iAllResultCnt = allocateInfoDao.updateAllocateInfoStatus(allocateInfo);
		}
		// 更新库存信息
		this.updateStore(allocateInfo);

		return iAllResultCnt + iItemResultCnt;
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月25日
	 * 
	 *          更新库存量
	 * @param allocationCash
	 *            接收现金信息
	 * @return 成功或失败
	 */
	public String updateStore(AllAllocateInfo allocationCash) {
		List<ChangeStoreEntity> entiryList = Lists.newArrayList();

		Iterator<String> keyIterator = allocationCash.getAllAllocateItemMap().keySet().iterator();
		String strKey = "";
		ChangeStoreEntity storeEntity = null;
		// 将修改的信息放入输入项
		while (keyIterator.hasNext()) {
			strKey = keyIterator.next();
			AllAllocateItem tempItem = allocationCash.getAllAllocateItemMap().get(strKey);
			storeEntity = new ChangeStoreEntity();
			storeEntity.setGoodsId(AllocationCommonUtils.getGoodsKey(tempItem));
			// 以库房接收数量为准
			storeEntity.setNum(tempItem.getMoneyNumber());
			entiryList.add(storeEntity);

		}
		String strOfficeId = allocationCash.getLoginUser().getOffice().getId();
		String strAllId = allocationCash.getAllId();
		return StoreCommonUtils.changeStoreAndSurplusStores(entiryList, strOfficeId, strAllId,
				allocationCash.getLoginUser());
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月25日
	 * 
	 *          修改物品库存预剩余库存数量
	 * @param allocationCash
	 *            配款现金信息
	 * @return 成功或失败
	 */
	public String updateSurplusStore(AllAllocateInfo allAllocateInfo, Map<String, AllAllocateItem> itemMap,
			String strOfficeId) {

		List<ChangeStoreEntity> entiryList = Lists.newArrayList();

		Iterator<String> keyIterator = itemMap.keySet().iterator();
		String strKey = "";
		ChangeStoreEntity storeEntity = null;
		// 将修改的信息放入输入项
		while (keyIterator.hasNext()) {
			strKey = keyIterator.next();
			AllAllocateItem tempItem = itemMap.get(strKey);
			storeEntity = new ChangeStoreEntity();
			storeEntity.setGoodsId(AllocationCommonUtils.getGoodsKeybygoodsId(tempItem));
			// 以库房接收数量为准
			storeEntity.setNum(tempItem.getMoneyNumber());
			entiryList.add(storeEntity);

		}
		String strAllId = allAllocateInfo.getAllId();
		return StoreCommonUtils.changeStoreAndSurplusStores(entiryList, strOfficeId, strAllId,
				allAllocateInfo.getLoginUser());
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年7月30日
	 * 
	 *          修改物品库存预剩余库存数量
	 * @param allAllocateInfo、itemList
	 *            配款现金信息
	 * @return 成功或失败
	 */
	@Transactional(readOnly = false)
	public String updateSurplusStorebyitemList(AllAllocateInfo allAllocateInfo, List<AllAllocateItem> itemList,
			String strOfficeId) {

		List<ChangeStoreEntity> entiryList = Lists.newArrayList();

		// Iterator<String> keyIterator = itemMap.keySet().iterator();
		// String strKey = "";
		ChangeStoreEntity storeEntity = null;
		// 将修改的信息放入输入项
		for (AllAllocateItem tempItem : itemList) {
			if (AllocationConstant.confirmFlag.Confirm.equals(tempItem.getConfirmFlag())) {
				storeEntity = new ChangeStoreEntity();
				storeEntity.setGoodsId(tempItem.getGoodsId());
				storeEntity.setNum(tempItem.getMoneyNumber());
				entiryList.add(storeEntity);
			}
		}
		/*
		 * while (keyIterator.hasNext()) { strKey = keyIterator.next();
		 * AllAllocateItem tempItem = itemMap.get(strKey); storeEntity = new
		 * ChangeStoreEntity();
		 * storeEntity.setGoodsId(AllocationCommonUtils.getGoodsKeybygoodsId(
		 * tempItem)); // 以库房接收数量为准
		 * storeEntity.setNum(tempItem.getMoneyNumber());
		 * entiryList.add(storeEntity);
		 * 
		 * }
		 */

		User user = new User();
		user.setName(allAllocateInfo.getLoginUser().getName());
		user.setId(allAllocateInfo.getLoginUser().getId());
		Office office = new Office();
		office.setId(strOfficeId);
		user.setOffice(office);

		String strAllId = allAllocateInfo.getAllId();
		return StoreCommonUtils.changeStoreAndSurplusStores(entiryList, strOfficeId, strAllId, user);
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月28日
	 * 
	 *          添加上缴接收明细
	 * @param allAllocateInfo
	 *            主表数据
	 * @param allocationCash
	 *            物品表数据
	 */
	public void setHandInCash(AllAllocateInfo allAllocateInfo, AllAllocateInfo allocationCash) {

		AllAllocateItem item = allAllocateInfo.getAllAllocateItem();

		String strMapKey = getAllAllocateItemMapKey(item);

		if (allocationCash.getAllAllocateItemMap().containsKey(strMapKey)) {
			// 添加时：如果存在相同 物品 的明细，将数量增加
			AllAllocateItem tempItem = allocationCash.getAllAllocateItemMap().get(strMapKey);
			tempItem.setMoneyNumber(tempItem.getMoneyNumber() + item.getMoneyNumber());
			tempItem.setMoneyAmount(tempItem.getMoneyAmount().add(item.getMoneyAmount()));

			allocationCash.getAllAllocateItemMap().put(strMapKey, tempItem);

		} else {
			// 添加时：追加整条明细
			// 设置物品表主键
			item.setAllItemsId(IdGen.uuid());

			// 状态标识
			item.setDelFlag(AllocationConstant.deleteFlag.Valid);
			allocationCash.getAllAllocateItemMap().put(strMapKey, item);
		}
		// 重新计算金额
		Map<String, AllAllocateItem> countMap = countCashByCurrencyFromMap(allocationCash.getAllAllocateItemMap());
		allocationCash.setCountItemMap(countMap);
	}

	/**
	 * 页面录入明细和数据库明细，按照币种比对总金额是否一致
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月28日
	 * 
	 * 
	 * @param infoPage
	 *            页面录入明细
	 * @param infoDb
	 *            数据库明细
	 * @return true：一致，false：不一致
	 */
	public boolean compareCash(AllAllocateInfo infoPage, AllAllocateInfo infoDb) {
		// 按币种计算页面录入明细总价值
		Map<String, AllAllocateItem> countCashFromPage = countCashByCurrencyFromMap(infoPage.getAllAllocateItemMap());
		Map<String, AllAllocateItem> countCashFromDb = countCashByCurrencyFromList(infoDb.getAllAllocateItemList());
		// 币种种类总数不一致，返回false
		if (countCashFromPage.size() != countCashFromDb.size()) {
			return false;
		}
		// 币种种类不一致，或者每个币种总金额不一致，返回false
		Iterator<String> keyIterator = countCashFromPage.keySet().iterator();
		AllAllocateItem pageCurrencyItem = null;
		AllAllocateItem dbCurrencyItem = null;
		while (keyIterator.hasNext()) {
			String strMapKey = keyIterator.next();
			if (!countCashFromDb.containsKey(strMapKey)) {
				return false;
			} else {
				pageCurrencyItem = countCashFromPage.get(strMapKey);
				dbCurrencyItem = countCashFromDb.get(strMapKey);
				if (pageCurrencyItem.getMoneyAmount().compareTo(dbCurrencyItem.getMoneyAmount()) != 0) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * 按币种统计物品总价值（页面录入）
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月28日
	 * 
	 * 
	 * @param allAllocateInfo
	 *            物品信息
	 * @return 按币种统计物品总价值（页面录入）
	 */
	public Map<String, AllAllocateItem> countQuotaCashByCurrencyFromMap(
			Map<String, AllAllocateItem> allAllocateItemMap) {
		if (allAllocateItemMap == null || allAllocateItemMap.size() == 0) {
			return Maps.newTreeMap();
		}

		Map<String, AllAllocateItem> currencyCountMap = Maps.newTreeMap();
		Iterator<String> keyIterator = allAllocateItemMap.keySet().iterator();
		AllAllocateItem valueItem = null;
		AllAllocateItem oldItem = null;
		while (keyIterator.hasNext()) {
			AllAllocateItem tempItem = allAllocateItemMap.get(keyIterator.next());
			valueItem = new AllAllocateItem();
			valueItem.setCurrency(tempItem.getCurrency()); // 币种
			valueItem.setConfirmNumber(tempItem.getConfirmNumber()); // 数量
			valueItem.setConfirmAmount(tempItem.getConfirmAmount()); // 金额
			if (!currencyCountMap.containsKey(tempItem.getCurrency())) {
				// 不存在时，保存
				currencyCountMap.put(tempItem.getCurrency(), valueItem);
			} else {
				// 存在时累加
				oldItem = currencyCountMap.get(tempItem.getCurrency());
				oldItem.setConfirmNumber(valueItem.getConfirmNumber() + oldItem.getConfirmNumber());
				oldItem.setConfirmAmount(oldItem.getConfirmAmount().add(valueItem.getConfirmAmount()));
			}
		}

		return currencyCountMap;
	}

	/**
	 * 按币种统计物品总价值（页面录入）
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月28日
	 * 
	 * 
	 * @param allAllocateInfo
	 *            物品信息
	 * @return 按币种统计物品总价值（页面录入）
	 */
	public Map<String, AllAllocateItem> countCashByCurrencyFromMap(Map<String, AllAllocateItem> allAllocateItemMap) {
		if (allAllocateItemMap == null || allAllocateItemMap.size() == 0) {
			return Maps.newTreeMap();
		}

		Map<String, AllAllocateItem> currencyCountMap = Maps.newTreeMap();
		Iterator<String> keyIterator = allAllocateItemMap.keySet().iterator();
		AllAllocateItem valueItem = null;
		AllAllocateItem oldItem = null;
		while (keyIterator.hasNext()) {
			AllAllocateItem tempItem = allAllocateItemMap.get(keyIterator.next());
			valueItem = new AllAllocateItem();
			valueItem.setCurrency(tempItem.getCurrency()); // 币种
			valueItem.setMoneyNumber(tempItem.getMoneyNumber()); // 数量
			valueItem.setMoneyAmount(tempItem.getMoneyAmount()); // 金额
			if (!currencyCountMap.containsKey(tempItem.getCurrency())) {
				// 不存在时，保存
				currencyCountMap.put(tempItem.getCurrency(), valueItem);
			} else {
				// 存在时累加
				oldItem = currencyCountMap.get(tempItem.getCurrency());
				oldItem.setMoneyNumber(valueItem.getMoneyNumber() + oldItem.getMoneyNumber());
				oldItem.setMoneyAmount(oldItem.getMoneyAmount().add(valueItem.getMoneyAmount()));
			}
		}

		return currencyCountMap;
	}

	/**
	 * 按币种统计物品总价值（数据库中取得）
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月28日
	 * 
	 * 
	 * @param allAllocateInfo
	 *            物品信息
	 * @return 按币种统计物品总价值（数据库中取得）
	 */
	public Map<String, AllAllocateItem> countCashByCurrencyFromList(List<AllAllocateItem> allAllocateItemList) {
		if (allAllocateItemList == null) {
			return Maps.newTreeMap();
		}

		Map<String, AllAllocateItem> currencyCountMap = Maps.newTreeMap();
		AllAllocateItem valueItem = null;
		AllAllocateItem oldItem = null;
		for (AllAllocateItem tempItem : allAllocateItemList) {
			valueItem = new AllAllocateItem();
			valueItem.setCurrency(tempItem.getCurrency()); // 币种
			valueItem.setMoneyNumber(tempItem.getMoneyNumber()); // 数量
			valueItem.setMoneyAmount(tempItem.getMoneyAmount()); // 金额
			if (!currencyCountMap.containsKey(tempItem.getCurrency())) {
				// 不存在时，保存
				currencyCountMap.put(tempItem.getCurrency(), valueItem);
			} else {
				// 存在时累加
				oldItem = currencyCountMap.get(tempItem.getCurrency());
				oldItem.setMoneyNumber(valueItem.getMoneyNumber() + oldItem.getMoneyNumber());
				oldItem.setMoneyAmount(oldItem.getMoneyAmount().add(valueItem.getMoneyAmount()));
			}
		}

		return currencyCountMap;
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年10月22日
	 * 
	 *          根据流水号更新状态
	 * @param allocateInfo
	 *            更新条件
	 * @return 更新数量
	 */
	@Transactional(readOnly = false)
	public int updateAllocateInfoStatus(AllAllocateInfo allocateInfo) {

		return allocateInfoDao.updateStatus(allocateInfo);
	}

	/**
	 * 库间交接任务交接接口
	 * 
	 * @author wangbaozhong
	 * @version 2015年10月13日
	 * 
	 *          交接按完指纹后，根据流水号，更新任务状态为已完成，保存交接时的金库交接员，整点室人员，授权人员信息。
	 * 
	 * @param allocateInfo
	 *            交接信息
	 * @return true : 更新成功
	 */
	@Transactional(readOnly = false)
	public boolean updateHandoverInfo(AllAllocateInfo allocateInfo) {

		// 更新调拨主表
		allocateInfo.setUpdateDate(new Date());
		allocateInfo.setUpdateBy(allocateInfo.getLoginUser());
		// 设定交接状态为完成
		allocateInfo.setStatus(AllocationConstant.Status.Finish);

		int iUpdateCnt = allocateInfoDao.updateStatus(allocateInfo);
		if (iUpdateCnt == 0) {
			String strMessageContent = "流水单号：" + allocateInfo.getAllId() + "对应业务状态(调拨主表)更新失败！";
			throw new BusinessException("message.E2015", strMessageContent, new String[] { "调拨主表" });
		}

		List<String> boxList = Lists.newArrayList();
		// 更新调拨详细表
		for (AllAllocateDetail allocateDetail : allocateInfo.getAllDetailList()) {
			allocateDetail.setAllocationInfo(allocateInfo);
			allocateDetail.setScanFlag(AllocationConstant.ScanFlag.Scan);
			iUpdateCnt = allocateDetailDao.updateAllocateDetailStatus(allocateDetail);
			boxList.add(allocateDetail.getBoxNo());
			if (iUpdateCnt == 0) {
				String strMessageContent = "流水单号：" + allocateInfo.getAllId() + "对应小车状态(调拨详细表)更新失败！";
				throw new BusinessException("message.E2015", strMessageContent, new String[] { "调拨详细表" });
			}
		}

		// 更新箱袋信息表
		// iUpdateCnt = StoreCommonUtils.updateBoxStatusBatch(boxList,
		// strPlace);

		if (iUpdateCnt == 0) {
			String strMessageContent = "流水单号：" + allocateInfo.getAllId() + "对应小车位置(箱袋信息表)更新失败！";
			throw new BusinessException("message.E2015", strMessageContent, new String[] { "箱袋信息表" });
		}

		// 插入交接主表
		AllHandoverInfo handoverInfo = allocateInfo.getAllHandoverInfo();
		// 设置主键
		handoverInfo.setHandoverId(IdGen.uuid());
		// handoverInfo.setAllocationInfo(allocateInfo);
		handoverInfo.setCreateDate(new Date());
		handoverInfo.setAcceptDate(new Date());
		// handoverInfo.setInoutType(allocateInfo.getInoutType());
		handoverInfo.setDelFlag(AllocationConstant.deleteFlag.Valid);
		int iInsertCnt = handoverInfoDao.insert(handoverInfo);
		if (iInsertCnt == 0) {
			String strMessageContent = "流水单号：" + allocateInfo.getAllId() + "对应交接信息(交接表)作成失败！";
			throw new BusinessException("message.E2016", strMessageContent, new String[] { "交接表" });
		}
		// 更新库存
		ChangeStoreEntity storeEntity = null;
		List<ChangeStoreEntity> entiryList = Lists.newArrayList();
		for (AllAllocateItem item : allocateInfo.getAllAllocateItemList()) {

			/*
			 * if (AllocationConstant.InoutType.Out.equals(allocateInfo.
			 * getInoutType())) { item.setMoneyNumber(-item.getMoneyNumber()); }
			 */
			if (AllocationConstant.RegistType.RegistStore.equals(item.getRegistType())) {

				storeEntity = new ChangeStoreEntity();
				storeEntity.setGoodsId(AllocationCommonUtils.getGoodsKey(item));
				// 以库房接收数量为准
				storeEntity.setNum(item.getMoneyNumber());
				entiryList.add(storeEntity);
			}
		}
		String strOfficeId = allocateInfo.getrOffice().getId();
		allocateInfo.getLoginUser().setOffice(new Office(strOfficeId));
		String strAllId = allocateInfo.getAllId();
		StoreCommonUtils.changeStoreAndSurplusStores(entiryList, strOfficeId, strAllId, allocateInfo.getLoginUser());

		return true;
	}

	/**
	 * 库外交接任务交接接口
	 * 
	 * @author liuyaowen
	 * @version 2017年7月14日
	 * 
	 * @param allocateInfo
	 *            交接信息
	 * @return true : 更新成功
	 */
	@Transactional(readOnly = false)
	public boolean updateOutHandoverInfo(AllAllocateInfo allocateInfo) {
		// 更新调拨主表
		int iUpdateCnt = 0;
		// 设置调拨主表更新时间
		allocateInfo.setUpdateDate(new Date());
		// 更新人
		allocateInfo.setUpdateBy(allocateInfo.getLoginUser());
		iUpdateCnt = allocateInfoDao.updateStatusByHandoverId(allocateInfo);
		if (iUpdateCnt == 0) {
			String strMessageContent = "修改人信息(调拨主表)更新失败！";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		}
		// 更新交接表交接时间
		int updateHandoverInfo = handoverInfoDao.update(allocateInfo.getAllHandoverInfo());
		if (updateHandoverInfo == 0) {
			String strMessageContent = "交接时间(交接主表)更新失败！";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		}
		// 更新交接明细表
		boolean flag = insertHandoverDetail(allocateInfo);
		if (!flag) {
			String strMessageContent = "对应接收日期(交接表)更新失败！";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		}
		List<AllAllocateInfo> allocateList = allocateInfoDao.findAllocateByHandoverId(allocateInfo);
		for (AllAllocateInfo allocate : allocateList) {
			// 发送通知
			List<String> paramsList = Lists.newArrayList();
			paramsList.add(allocate.getrOffice().getName());
			paramsList.add(allocate.getAllId());
			SysCommonUtils.allocateMessageQueueAdd(allocate.getBusinessType(), allocate.getStatus(), paramsList,
					allocate.getrOffice().getId(), allocateInfo.getLoginUser());
		}
		return true;
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年10月16日
	 * 
	 *          根据路线查询箱钞信息
	 * @param allocateInfo
	 *            查询条件
	 * @return 钞箱信息
	 */
	public List<AllAllocateInfo> findBoxInfoList(AllAllocateInfo allocateInfo) {
		return allocateInfoDao.findBoxInfoList(allocateInfo);
	}

	/**
	 * 
	 * @author liuyaowen
	 * @version 2017年7月14日
	 * 
	 *          库外箱袋入库确认接口
	 * @param allocateInfo
	 *            更新信息及条件
	 * @return true : 更新成功
	 */
	@Transactional(readOnly = false)
	public boolean boxInStoreConfirm(Map<String, Object> headInfo, AllAllocateInfo allocateInfo) {
		Map<String, Integer> updateAllocateIdMap = Maps.newHashMap();
		Map<String, BigDecimal> updateAllocateIdAmountMap = Maps.newHashMap();
		int iUpdateCnt = 0;
		List<String> boxList = Lists.newArrayList();
		for (AllAllocateDetail boxDetail : allocateInfo.getAllDetailList()) {

			// 记录每个流水号对应接收钞箱数量
			if (!updateAllocateIdMap.containsKey(boxDetail.getAllId())) {
				updateAllocateIdMap.put(boxDetail.getAllId(), 1);
			} else {
				int iAcceptBoxNum = updateAllocateIdMap.get(boxDetail.getAllId()) + 1;
				updateAllocateIdMap.put(boxDetail.getAllId(), iAcceptBoxNum);
			}
			// 记录每个流水号对应接收金额
			if (!updateAllocateIdAmountMap.containsKey(boxDetail.getAllId())) {
				if (boxDetail.getAmount() == null) {
					updateAllocateIdAmountMap.put(boxDetail.getAllId(), new BigDecimal(0));
				} else {
					updateAllocateIdAmountMap.put(boxDetail.getAllId(), boxDetail.getAmount());
				}
			} else {
				if (boxDetail.getAmount() != null) {
					BigDecimal amount = updateAllocateIdAmountMap.get(boxDetail.getAllId()).add(boxDetail.getAmount());
					updateAllocateIdAmountMap.put(boxDetail.getAllId(), amount);
				}
			}
			// 更新调拨详细表
			iUpdateCnt = allocateDetailDao.updateDetailStatusByBoxNo(boxDetail);
			if (iUpdateCnt == 0) {
				String strMessageContent = "流水单号：" + boxDetail.getAllId() + "对应调拨详细表更新失败！";
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
			}
			boxList.add(boxDetail.getBoxNo());
		}
		// 设置更新者信息
		User loginUser = UserUtils.get(headInfo.get(Parameter.USER_ID_KEY).toString());
		// 更新箱袋信息表
		iUpdateCnt = StoreCommonUtils.updateBoxStatusBatch(boxList, AllocationConstant.Place.StoreRoom, loginUser);
		if (iUpdateCnt == 0) {
			String strMessageContent = "箱袋位置(箱袋信息表)更新失败！";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		}
		// 插入交接主表
		AllHandoverInfo handoverInfo = allocateInfo.getAllHandoverInfo();
		int iInsertCnt = handoverInfoDao.insert(handoverInfo);
		if (iInsertCnt == 0) {
			String strMessageContent = "交接信息(交接表)作成失败！";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		}
		if (!handoverInfo.getDetailList().isEmpty()) {
			for (AllHandoverDetail allhandoverdetail : handoverInfo.getDetailList()) {
				int insertResult = allHandoverDetailDao.insert(allhandoverdetail);
				if (insertResult == 0) {
					String strMessageContent = "交接明细：" + allhandoverdetail.getDetailId() + "更新失败！";
					throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
				}
			}
		}
		Date date = new Date();
		// 更新调拨主表
		// 设置库房交接ID
		allocateInfo.setStoreHandoverId(handoverInfo.getHandoverId());
		// 更新条件:更新状态
		allocateInfo.setStatus(AllocationConstant.Status.HandoverTodo);
		// 设置更新时间
		allocateInfo.setUpdateDate(date);
		// 设置更新者信息
		allocateInfo.setUpdateBy(allocateInfo.getLoginUser());
		Iterator<String> keyIterator = updateAllocateIdMap.keySet().iterator();
		while (keyIterator.hasNext()) {
			String strAllId = keyIterator.next();
			// 设置更新流水号
			allocateInfo.setAllId(strAllId);
			// 设置接收个数
			allocateInfo.setAcceptNumber(updateAllocateIdMap.get(strAllId));
			// 设置接收金额
			allocateInfo.setConfirmAmount(updateAllocateIdAmountMap.get(strAllId).setScale(2));
			// 设置扫描时间
			allocateInfo.setScanDate(date);
			// 更新调拨主表
			iUpdateCnt = allocateInfoDao.updateAllocateInfoStatus(allocateInfo);
			if (iUpdateCnt == 0) {
				String strMessageContent = "流水单号：" + strAllId + "对应调拨主表更新失败！";
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
			}
			// 发送通知
			List<String> paramsList = Lists.newArrayList();
			paramsList.add(getAllocate(strAllId).getrOffice().getName());
			paramsList.add(allocateInfo.getAllId());
			SysCommonUtils.allocateMessageQueueAdd(allocateInfo.getBusinessType(), allocateInfo.getStatus(), paramsList,
					getAllocate(strAllId).getaOffice().getId(), loginUser);
		}

		AllAllocateInfo allocate = new AllAllocateInfo();
		String routeId = headInfo.get(Parameter.ROUTE_ID_KEY).toString();
		// 查询条件：线路ID
		allocate.setRouteId(routeId);
		// 查询条件：业务类型= 现金预约
		allocate.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_HandIn);
		// 设置状态 = 已装箱
		allocate.setStatus(AllocationConstant.Status.Onload);

		List<AllAllocateInfo> allocateList = allocateInfoDao.findAllocationList(allocate);
		for (AllAllocateInfo info : allocateList) {
			info.setScanDate(date);
			// 设置库房交接ID
			info.setStoreHandoverId(handoverInfo.getHandoverId());
			// 更新条件:更新状态
			info.setStatus(AllocationConstant.Status.HandoverTodo);
			allocateInfoDao.updateAllocateInfoStatus(info);
		}
		return true;
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年10月19日
	 * 
	 *          库外箱袋出库确认接口
	 * @param allocateInfo
	 *            更新信息及条件
	 * @return true : 更新成功
	 */
	@Transactional(readOnly = false)
	public boolean boxOutStoreConfirm(Map<String, Object> headInfo, AllAllocateInfo allocateInfo) {
		Map<String, Integer> updateAllocateIdMap = Maps.newHashMap();
		int iUpdateCnt = 0;
		List<String> boxList = Lists.newArrayList();
		for (AllAllocateDetail boxDetail : allocateInfo.getAllDetailList()) {

			// 记录每个流水号对应接收钞箱数量
			if (!updateAllocateIdMap.containsKey(boxDetail.getAllId())) {
				updateAllocateIdMap.put(boxDetail.getAllId(), 1);
			} else {
				int iAcceptBoxNum = updateAllocateIdMap.get(boxDetail.getAllId()) + 1;
				updateAllocateIdMap.put(boxDetail.getAllId(), iAcceptBoxNum);
			}
			// 更新调拨详细表
			iUpdateCnt = allocateDetailDao.updateDetailStatusByBoxNo(boxDetail);
			if (iUpdateCnt == 0) {
				String strMessageContent = "流水单号：" + boxDetail.getAllId() + "对应箱袋(" + boxDetail.getBoxNo()
						+ ")扫描状态(调拨详细表)更新失败！";
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
			}
			boxList.add(boxDetail.getBoxNo());
		}
		// 设置更新者信息
		User loginUser = UserUtils.get(headInfo.get(Parameter.USER_ID_KEY).toString());
		if (!Collections3.isEmpty(boxList)) {
			// 更新箱袋信息表
			iUpdateCnt = StoreCommonUtils.updateBoxStatusBatch(boxList, AllocationConstant.Place.onPassage, loginUser);

			if (iUpdateCnt == 0) {
				String strMessageContent = "箱袋位置(箱袋信息表)更新失败！";
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
			}
		}

		// 插入交接主表
		AllHandoverInfo handoverInfo = allocateInfo.getAllHandoverInfo();

		int iInsertCnt = handoverInfoDao.insert(handoverInfo);
		if (iInsertCnt == 0) {
			String strMessageContent = "交接信息(交接表)作成失败！";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		}
		if (!handoverInfo.getDetailList().isEmpty()) {
			for (AllHandoverDetail allhandoverdetail : handoverInfo.getDetailList()) {
				int insertResult = allHandoverDetailDao.insert(allhandoverdetail);
				if (insertResult == 0) {
					String strMessageContent = "交接明细：" + allhandoverdetail.getDetailId() + "更新失败！";
					throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
				}
			}
		}
		Date date = new Date();
		// 更新调拨主表
		// 设置库房交接ID
		allocateInfo.setStoreHandoverId(handoverInfo.getHandoverId());
		// 更新条件:更新状态
		allocateInfo.setStatus(AllocationConstant.Status.HandoverTodo);
		// 设置更新时间
		allocateInfo.setUpdateDate(new Date());
		// 设置更新者信息
		allocateInfo.setUpdateBy(allocateInfo.getLoginUser());
		if (updateAllocateIdMap != null) {
			Iterator<String> keyIterator = updateAllocateIdMap.keySet().iterator();
			while (keyIterator.hasNext()) {
				String strAllId = keyIterator.next();
				// 设置更新流水号
				allocateInfo.setAllId(strAllId);
				// 设置接收个数
				// allocateInfo.setAcceptNumber(updateAllocateIdMap.get(strAllId));
				// 设置扫描时间
				allocateInfo.setScanDate(date);
				iUpdateCnt = allocateInfoDao.updateAllocateInfoStatus(allocateInfo);

				if (iUpdateCnt == 0) {
					String strMessageContent = "流水单号：" + strAllId + "对接业务状态(调拨主表)更新失败！";
					throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
				}
				// 发送通知
				List<String> paramsList = Lists.newArrayList();
				paramsList.add(getAllocate(strAllId).getrOffice().getName());
				paramsList.add(allocateInfo.getAllId());
				SysCommonUtils.allocateMessageQueueAdd(allocateInfo.getBusinessType(), allocateInfo.getStatus(),
						paramsList, getAllocate(strAllId).getaOffice().getId(), loginUser);
			}
		}
		AllAllocateInfo allocate = new AllAllocateInfo();
		String routeId = headInfo.get(Parameter.ROUTE_ID_KEY).toString();
		// 查询条件：线路ID
		allocate.setRouteId(routeId);
		// 查询条件：业务类型= 现金预约
		allocate.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_Reservation);
		// 设置状态 = 已装箱
		allocate.setStatus(AllocationConstant.Status.CashOrderQuotaYes);

		List<AllAllocateInfo> allocateList = allocateInfoDao.findList(allocate);
		for (AllAllocateInfo info : allocateList) {
			info.setScanDate(date);
			// 设置库房交接ID
			info.setStoreHandoverId(handoverInfo.getHandoverId());
			// 更新条件:更新状态
			info.setStatus(AllocationConstant.Status.HandoverTodo);
			allocateInfoDao.updateAllocateInfoStatus(info);
		}
		return true;
	}

	/**
	 * 按照机构ID查询下拨已装箱信息接口
	 * 
	 * @author qipeihong
	 * @version 2017年7月11日
	 * 
	 * 
	 * @param aofficeId
	 *            银行机构Id
	 * @return
	 */
	public List<AllAllocateInfo> bankAcceptedBoxDetailList(String aofficeId) {

		// 查询结果List
		List<AllAllocateInfo> allallocateInfoList = Lists.newArrayList();
		AllAllocateInfo allallocateInfo = new AllAllocateInfo();

		// 业务类型为现金预约
		String businessType = AllocationConstant.BusinessType.OutBank_Cash_Reservation;
		// 设置交接状态 (已装箱)
		allallocateInfo.setStatus(AllocationConstant.Status.CashOrderQuotaYes);

		// 设置接收机构
		Office aOffice = new Office();
		aOffice.setId(aofficeId);
		allallocateInfo.setaOffice(aOffice);
		// 设置业务类型
		allallocateInfo.setBusinessType(businessType);
		allallocateInfo.getSqlMap().put("dsf", "OR o.parent_ids LIKE '%" + aOffice.getId() + "%'");
		allallocateInfoList = allocateInfoDao.findAllocationList(allallocateInfo);
		return allallocateInfoList;
	}

	/**
	 * 按照条件查询库外箱袋出入库信息接口
	 * 
	 * @author liuzhiheng
	 * @version 2015年10月14日
	 * 
	 * 
	 * @param aofficeId
	 *            银行机构Id
	 * @return 按币种统计物品总价值（数据库中取得）
	 */
	public List<AllAllocateInfo> stoOutsideBoxInOrOutList(String inoutType, String officeId) {

		// 查询结果List
		List<AllAllocateInfo> aaInfoList = Lists.newArrayList();
		// 传参实体类
		AllAllocateInfo aaInfo = new AllAllocateInfo();
		Office aOffice = new Office();
		// 如果为出库业务
		if (ExternalConstant.Allocation.INOUT_TYPE_OUT.equals(inoutType)) {
			aOffice.setId(officeId);
			// 状态为已装箱
			aaInfo.setStatus(AllocationConstant.Status.CashOrderQuotaYes);
			// 业务类型为现金下拨
			aaInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_Reservation);
		}
		// 如果为入库业务
		if (ExternalConstant.Allocation.INOUT_TYPE_IN.equals(inoutType)) {
			// 金库为接收单位
			aOffice.setId(officeId);
			// 状态为在途
			aaInfo.setStatus(AllocationConstant.Status.Onload);
			// 业务类型为现金上缴
			aaInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_HandIn);

		}
		aaInfo.setaOffice(aOffice);
		aaInfo.setScanFlag(AllocationConstant.ScanFlag.NoScan);
		aaInfoList = allocateInfoDao.serachstoOutsideBoxInOrOutDetail(aaInfo);
		return aaInfoList;
	}

	/**
	 * 按照条件查询库外交接任务信息接口
	 * 
	 * @author qipeihong
	 * @version 2017年7月17日
	 * 
	 * 
	 * @param aofficeId
	 *            银行机构Id
	 * @return 按币种统计物品总价值（数据库中取得）
	 */
	public List<AllAllocateInfo> stoOutsideHandoverList(String officeId, String inoutType, String searchDateBegin,
			String searchDateEnd) {
		// 查询结果List
		List<AllAllocateInfo> aaInfoList = Lists.newArrayList();
		// 传参实体类
		AllAllocateInfo aaInfo = new AllAllocateInfo();
		Office office = new Office();
		office.setId(officeId);
		// 设置接收机构
		aaInfo.setaOffice(office);
		aaInfo.getSqlMap().put("dsf", "OR o.parent_ids LIKE '%" + office.getId() + "%'");
		// 状态为已扫描
		aaInfo.setStatus(AllocationConstant.Status.HandoverTodo);
		// 设置开始日期
		// SimpleDateFormat date = new SimpleDateFormat("YYYY-MM-DD");
		aaInfo.setSearchDateStart(searchDateBegin);
		// 设置结束日期
		aaInfo.setSearchDateEnd(searchDateEnd);
		// 如果为出库业务
		if (ExternalConstant.Allocation.INOUT_TYPE_OUT.equals(inoutType)) {
			aaInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_Reservation);
		}
		// 如果为入库业务
		if (ExternalConstant.Allocation.INOUT_TYPE_IN.equals(inoutType)) {
			aaInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_HandIn);
		}
		List<String> scanFlagList = Lists.newArrayList();
		// 设置扫描标识为已扫描、补录、强制补录
		scanFlagList.add(AllocationConstant.ScanFlag.Scan);
		scanFlagList.add(AllocationConstant.ScanFlag.Additional);
		scanFlagList.add(AllocationConstant.ScanFlag.UnknownAdditional);
		aaInfo.setScanFlagList(scanFlagList);
		aaInfoList = allocateInfoDao.serachstoOutsideHandoverDetail(aaInfo);
		return aaInfoList;
	}

	/**
	 * 
	 * @author chenxin
	 * @version 2015年10月14日 更新库间调拨状态
	 * 
	 * @return
	 */
	public int updateAllocateDetailStatus(AllAllocateDetail allocateDetail) {
		return allocateDetailDao.updateAllocateDetailStatus(allocateDetail);
	}

	/**
	 * 
	 * @author chenxin
	 * @version 2015年10月14日
	 * 
	 *          生成交接任务
	 * @param allHandoverInfo
	 * @return
	 */
	public int insertHandoverInfo(AllHandoverInfo allHandoverInfo) {
		return handoverInfoDao.insert(allHandoverInfo);
	}

	/**
	 * 
	 * @author chenxin
	 * @version 2015年10月15日
	 * 
	 *          插入库间调拨详细信息
	 * @param allocateDetail
	 * @return
	 */
	public int insertAllocateDetail(AllAllocateDetail allocateDetail) {
		return allocateDetailDao.insert(allocateDetail);
	}

	/**
	 * 
	 * @author chenxin
	 * @version 2015年10月15日
	 * 
	 *          根据allId查询详细信息
	 * @param allId
	 * @return
	 */
	public AllAllocateDetail getDetailByAllId(String allId) {
		return allocateDetailDao.getByAllId(allId);
	}

	public AllHandoverInfo getHandoverByAllId(String allId) {
		return handoverInfoDao.getByAllId(allId);
	}

	/**
	 * 
	 * @author chenxin
	 * @version 2015年10月26日
	 * 
	 *          删除箱袋信息
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = false)
	public Boolean deleteByAllId(AllAllocateInfo allAllocateInfo, List<ChangeStoreEntity> entiryList) {

		// 更新物品
		if (!entiryList.isEmpty()) {
			String officeId = allAllocateInfo.getrOffice().getId();
			User user = UserUtils.getUser();
			StoreCommonUtils.changeStore(entiryList, officeId, allAllocateInfo.getAllId(), user);
		}

		// 删除调拨信息
		if (allAllocateInfo.getAllDetailList() != null && allAllocateInfo.getAllDetailList().size() > 0) {
			allocateDetailDao.deleteDetailByAllId(allAllocateInfo.getAllDetailList().get(0));
		}
		allAllocateInfo.preUpdate();
		allocateInfoDao.delete(allAllocateInfo);
		return true;
	}

	/**
	 * 
	 * @author chengs
	 * @version 2015年10月26日
	 * 
	 *          根据机构ID，取得当天该机构的配款信息
	 * @param allAllocateInfo
	 *            调拨信息
	 * @return
	 */
	public List<AllAllocateInfo> getQuotaInfoByrOffice(AllAllocateInfo allAllocateInfo) {

		String rOfficeId = allAllocateInfo.getaOffice().getId();

		AllAllocateInfo allocateInfo = new AllAllocateInfo();

		// 查询条件:开始时间
		allocateInfo.setCreateTimeStart(allAllocateInfo.getCreateDate());
		// 查询条件:结束时间
		allocateInfo.setCreateTimeEnd(allAllocateInfo.getCreateDate());

		// 业务种别:现金预约
		allocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_Reservation);
		// 登记机构
		Office office = new Office();
		office.setId(rOfficeId);
		allocateInfo.setrOffice(office);

		// 执行查询处理
		return findAllocation(allocateInfo);
	}

	/**
	 * 计算箱子个数
	 * 
	 * @param allAllocateInfo
	 *            页面调拨信息
	 * @param allocationList
	 *            调拨查询结果
	 */
	private void countBoxNumber(AllAllocateInfo allocateInfo) {

		// 查询条件：开始时间
		if (allocateInfo.getCreateTimeStart() != null) {
			allocateInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(allocateInfo.getCreateTimeStart())));
		}
		// 查询条件：结束时间
		if (allocateInfo.getCreateTimeEnd() != null) {
			allocateInfo.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(allocateInfo.getCreateTimeEnd())));
		}

		// 根据查询条件，取得调拨全部信息
		AllAllocateInfo boxCountInfo = allocateInfoDao.getBoxCount(allocateInfo);
		AllAllocateInfo tempBoxCountInfo = tempAllocateInfoDao.getBoxCount(allocateInfo);

		// 箱子总数
		allocateInfo.setBoxCount(boxCountInfo.getBoxCount() + tempBoxCountInfo.getBoxCount());
		// 尾箱个数
		allocateInfo.setTailBoxCount(boxCountInfo.getTailBoxCount() + tempBoxCountInfo.getTailBoxCount());
		// 款箱个数
		allocateInfo
				.setParagraphBoxCount(boxCountInfo.getParagraphBoxCount() + tempBoxCountInfo.getParagraphBoxCount());
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年12月28日
	 * 
	 *          保存预约信息(接口用)
	 * @param allocateInfo
	 *            需要保存的数据
	 * @return 插入的数据条数
	 */
	@Transactional(readOnly = false)
	public int saveOrderAllocationForInterFace(AllAllocateInfo allocateInfo) {

		// 插入件数
		int iAllResultCnt = 0;
		int iItemResultCnt = 0;
		// 新规的场合
		if (StringUtils.isBlank(allocateInfo.getAllId())) {

			// 插入主表
			// 主键空的场合，设置主键
			allocateInfo.setAllId(BusinessUtils.getNewBusinessNo(Global.getConfig("businessType.allocation.cashOrder"),
					allocateInfo.getLoginUser().getOffice()));

			// 业务类型
			allocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_Reservation);
			// 交接状态（现金预约未配款）
			allocateInfo.setStatus(AllocationConstant.Status.CashOrderQuotaNo);
			// 登记机构
			allocateInfo.setrOffice(allocateInfo.getLoginUser().getOffice());
			// 设置接收机构
			// 取得当前机构金库编号
			Office aOffice = BusinessUtils.getCashCenterByOffice(allocateInfo.getLoginUser().getOffice());
			allocateInfo.setaOffice(aOffice);

			// 有效标识
			allocateInfo.setDelFlag(AllocationConstant.deleteFlag.Valid);

			// 设置更新者，创建者及更新创建时间
			if (StringUtils.isNotBlank(allocateInfo.getLoginUser().getId())) {
				allocateInfo.setCreateBy(allocateInfo.getLoginUser());
				allocateInfo.setUpdateBy(allocateInfo.getLoginUser());
				allocateInfo.setCreateName(allocateInfo.getLoginUser().getName());
				allocateInfo.setUpdateName(allocateInfo.getLoginUser().getName());
			}
			allocateInfo.setUpdateDate(new Date());
			allocateInfo.setCreateDate(allocateInfo.getUpdateDate());

			iAllResultCnt = allocateInfoDao.insert(allocateInfo);

			// 插入物品表
			Iterator<String> insertKeyIterator = allocateInfo.getAllAllocateItemMap().keySet().iterator();
			while (insertKeyIterator.hasNext()) {
				AllAllocateItem tempItem = allocateInfo.getAllAllocateItemMap().get(insertKeyIterator.next());
				tempItem.setAllocationInfo(allocateInfo);
				// 设置更新者，创建者及更新创建时间
				if (StringUtils.isNotBlank(allocateInfo.getLoginUser().getId())) {
					tempItem.setCreateBy(allocateInfo.getLoginUser());
					tempItem.setUpdateBy(allocateInfo.getLoginUser());
					tempItem.setCreateName(allocateInfo.getLoginUser().getName());
					tempItem.setUpdateName(allocateInfo.getLoginUser().getName());
				}
				tempItem.setUpdateDate(new Date());
				tempItem.setCreateDate(tempItem.getUpdateDate());

				iItemResultCnt += allAllocateItemDao.insert(tempItem);
			}

		} else {
			// 更新的场合，更新金额和更新者，删除已经存在的详细信息
			AllAllocateItem delItem = new AllAllocateItem();
			// 流水号
			delItem.setAllId(allocateInfo.getAllId());
			// 登记种别
			delItem.setRegistType(AllocationConstant.RegistType.RegistPoint);
			// 设置更新者及更新时间
			if (StringUtils.isNotBlank(allocateInfo.getLoginUser().getId())) {
				delItem.setUpdateBy(allocateInfo.getLoginUser());
			}
			delItem.setUpdateDate(new Date());

			// 物品表中删除旧数据
			iItemResultCnt += allAllocateItemDao.delOrderDetail(delItem);

			// 物品表中插入修改后的新数据
			Iterator<String> keyIterator = allocateInfo.getAllAllocateItemMap().keySet().iterator();
			while (keyIterator.hasNext()) {
				AllAllocateItem tempItem = allocateInfo.getAllAllocateItemMap().get(keyIterator.next());
				// 设置物品表主键
				tempItem.setAllItemsId(IdGen.uuid());
				// 设置流水号
				tempItem.setAllocationInfo(allocateInfo);

				// 设置更新者，创建者及更新创建时间
				if (StringUtils.isNotBlank(allocateInfo.getLoginUser().getId())) {
					tempItem.setCreateBy(allocateInfo.getLoginUser());
					tempItem.setUpdateBy(allocateInfo.getLoginUser());
					tempItem.setCreateName(allocateInfo.getLoginUser().getName());
					tempItem.setUpdateName(allocateInfo.getLoginUser().getName());
				}
				tempItem.setUpdateDate(new Date());
				tempItem.setCreateDate(tempItem.getUpdateDate());

				iItemResultCnt += allAllocateItemDao.insert(tempItem);
			}

			// 更新主表

			// 设置更新者及更新时间
			if (StringUtils.isNotBlank(allocateInfo.getLoginUser().getId())) {
				allocateInfo.setUpdateBy(allocateInfo.getLoginUser());
			}
			allocateInfo.setUpdateDate(new Date());

			iAllResultCnt = allocateInfoDao.updateAllocateInfoStatus(allocateInfo);
		}
		return iAllResultCnt + iItemResultCnt;
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年12月28日
	 * 
	 *          保存上缴信息(接口用)
	 * @param allocateInfo
	 *            需要保存的数据
	 * @return 插入的数据条数
	 */
	@Transactional(readOnly = false)
	public int saveHandinAllocationForInterFace(AllAllocateInfo allocateInfo) {

		// 插入件数
		int iAllResultCnt = 0;
		int iItemResultCnt = 0;
		// 新规的场合
		if (StringUtils.isBlank(allocateInfo.getAllId())) {

			// 插入主表
			// 主键空的场合，设置主键
			allocateInfo.setAllId(BusinessUtils.getNewBusinessNo(Global.getConfig("businessType.allocation.cashHandin"),
					allocateInfo.getLoginUser().getOffice()));

			// 业务类型
			allocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_HandIn);
			// 交接状态（网点上缴金额未确认）
			allocateInfo.setStatus(AllocationConstant.Status.BankOutletsHandInConfirmNo);
			// 登记机构
			allocateInfo.setrOffice(allocateInfo.getLoginUser().getOffice());
			// 设置接收机构
			// 取得当前机构金库编号
			Office aOffice = BusinessUtils.getCashCenterByOffice(allocateInfo.getLoginUser().getOffice());
			allocateInfo.setaOffice(aOffice);

			// 有效标识
			allocateInfo.setDelFlag(AllocationConstant.deleteFlag.Valid);
			// 调缴出入库类型
			// allocateInfo.setInoutType(AllocationConstant.InoutType.In);
			// 设置更新者，创建者及更新创建时间
			if (StringUtils.isNotBlank(allocateInfo.getLoginUser().getId())) {
				allocateInfo.setCreateBy(allocateInfo.getLoginUser());
				allocateInfo.setUpdateBy(allocateInfo.getLoginUser());
				allocateInfo.setCreateName(allocateInfo.getLoginUser().getName());
				allocateInfo.setUpdateName(allocateInfo.getLoginUser().getName());
			}
			allocateInfo.setUpdateDate(new Date());
			allocateInfo.setCreateDate(allocateInfo.getUpdateDate());

			iAllResultCnt = allocateInfoDao.insert(allocateInfo);

			// 插入物品表
			Iterator<String> insertKeyIterator = allocateInfo.getAllAllocateItemMap().keySet().iterator();
			while (insertKeyIterator.hasNext()) {
				AllAllocateItem tempItem = allocateInfo.getAllAllocateItemMap().get(insertKeyIterator.next());
				tempItem.setAllocationInfo(allocateInfo);
				// 设置更新者，创建者及更新创建时间
				if (StringUtils.isNotBlank(allocateInfo.getLoginUser().getId())) {
					tempItem.setCreateBy(allocateInfo.getLoginUser());
					tempItem.setUpdateBy(allocateInfo.getLoginUser());
					tempItem.setCreateName(allocateInfo.getLoginUser().getName());
					tempItem.setUpdateName(allocateInfo.getLoginUser().getName());
				}
				tempItem.setUpdateDate(new Date());
				tempItem.setCreateDate(tempItem.getUpdateDate());
				iItemResultCnt += allAllocateItemDao.insert(tempItem);
			}

		} else {
			// 更新的场合，更新金额和更新者，删除已经存在的详细信息
			AllAllocateItem delItem = new AllAllocateItem();
			// 流水号
			delItem.setAllId(allocateInfo.getAllId());
			// 登记种别
			delItem.setRegistType(AllocationConstant.RegistType.RegistPoint);
			// 设置更新者及更新时间
			if (StringUtils.isNotBlank(allocateInfo.getLoginUser().getId())) {
				delItem.setUpdateBy(allocateInfo.getLoginUser());
			}
			delItem.setUpdateDate(new Date());
			// 物品表中删除旧数据
			iItemResultCnt += allAllocateItemDao.delOrderDetail(delItem);

			// 物品表中插入修改后的新数据
			Iterator<String> keyIterator = allocateInfo.getAllAllocateItemMap().keySet().iterator();
			while (keyIterator.hasNext()) {
				AllAllocateItem tempItem = allocateInfo.getAllAllocateItemMap().get(keyIterator.next());
				// 设置物品表主键
				tempItem.setAllItemsId(IdGen.uuid());
				// 设置流水号
				tempItem.setAllocationInfo(allocateInfo);

				// 设置更新者，创建者及更新创建时间
				if (StringUtils.isNotBlank(allocateInfo.getLoginUser().getId())) {
					tempItem.setCreateBy(allocateInfo.getLoginUser());
					tempItem.setUpdateBy(allocateInfo.getLoginUser());
					tempItem.setCreateName(allocateInfo.getLoginUser().getName());
					tempItem.setUpdateName(allocateInfo.getLoginUser().getName());
				}
				tempItem.setUpdateDate(new Date());
				tempItem.setCreateDate(tempItem.getUpdateDate());
				iItemResultCnt += allAllocateItemDao.insert(tempItem);
			}

			// 更新主表
			// 设置更新者及更新时间
			if (StringUtils.isNotBlank(allocateInfo.getLoginUser().getId())) {
				allocateInfo.setUpdateBy(allocateInfo.getLoginUser());
			}
			allocateInfo.setUpdateDate(new Date());

			iAllResultCnt = allocateInfoDao.updateAllocateInfoStatus(allocateInfo);
		}
		return iAllResultCnt + iItemResultCnt;
	}

	/**
	 * @author wangbaozhong
	 * @version 2015年12月28日
	 * 
	 *          删除现金调拨信息（接口用）
	 * @param allocateInfo
	 *            删除条件
	 * @return 插入结果
	 */
	@Transactional(readOnly = false)
	public int deleteCashForInterFace(AllAllocateInfo allocateInfo) {

		// 删除件数
		int allResult = 0;
		int detailResult = 0;
		int itemResult = 0;
		// 删除调拨主表
		// 设置更新者及更新时间
		if (StringUtils.isNotBlank(allocateInfo.getLoginUser().getId())) {
			allocateInfo.setUpdateBy(allocateInfo.getLoginUser());
		}
		allocateInfo.setUpdateDate(new Date());
		allResult = allocateInfoDao.delete(allocateInfo);

		// 删除调拨详细表
		if (allocateInfo.getAllDetailList().size() > 0) {
			AllAllocateDetail detail = allocateInfo.getAllDetailList().get(0);
			detail.setAllocationInfo(allocateInfo);
			detailResult = allocateDetailDao.delete(detail);
		}

		// 删除调拨物品表
		AllAllocateItem item = new AllAllocateItem();
		item.setAllocationInfo(allocateInfo);
		// 设置更新者及更新时间
		if (StringUtils.isNotBlank(allocateInfo.getLoginUser().getId())) {
			item.setUpdateBy(allocateInfo.getLoginUser());
		}
		item.setUpdateDate(new Date());
		itemResult = allAllocateItemDao.delete(item);

		return allResult + detailResult + itemResult;
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年7月13日
	 * 
	 *          金库现金预约尾箱出库调拨信息增加(接口用)
	 * @param allocateInfo
	 *            需要保存的数据
	 * @return 插入的数据条数
	 */
	@Transactional(readOnly = false)
	public int savedetailboxAllocationForInterFace(AllAllocateInfo allocateInfo, Map<String, Object> headInfo) {

		// 插入件数
		int iAllResultCnt = 0;

		// 新规的场合
		if (StringUtils.isBlank(allocateInfo.getAllId())) {

			// 插入主表
			// 主键空的场合，设置主键
			allocateInfo.setAllId(BusinessUtils.getNewBusinessNo(Global.getConfig("businessType.allocation.cashOrder"),
					allocateInfo.getrOffice()));
			// 业务类型
			allocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_Reservation);
			// 交接状态（网点上缴金额未确认）
			allocateInfo.setStatus(AllocationConstant.Status.BetweenConfirm);
			// 登记机构
			allocateInfo.setrOffice(allocateInfo.getrOffice());
			// Office office =
			// StoreCommonUtils.getOfficeById(allocateInfo.getrOffice().getId());
			// office=StoreCommonUtils.getOfficeById(office.getParentId());
			// Office aOffice = new Office();
			// aOffice.setId(office.getParentId());
			// aOffice.setName(StoreCommonUtils.getOfficeById(office.getParentId()).getName());
			// allocateInfo.setaOffice(aOffice);
			List<StoRouteInfo> list = stoRouteInfoDao.searchStoRouteInfoByOffice(allocateInfo.getrOffice().getId());
			allocateInfo.setRouteId(list.get(0).getId());
			allocateInfo.setRouteName(list.get(0).getRouteName());
			// 有效标识
			allocateInfo.setDelFlag(AllocationConstant.deleteFlag.Valid);
			// 设置更新者，创建者及更新创建时间
			User user = new User();
			String userid = (String) headInfo.get(Parameter.USER_ID_KEY);
			String username = (String) headInfo.get(Parameter.USER_NAME_KEY);
			user.setId(userid);
			user.setName(username);
			allocateInfo.setCreateBy(user);
			allocateInfo.setUpdateBy(user);
			allocateInfo.setCreateName(username);
			allocateInfo.setUpdateName(username);
			allocateInfo.setUpdateDate(new Date());
			allocateInfo.setCreateDate(allocateInfo.getUpdateDate());

			iAllResultCnt = allocateInfoDao.insert(allocateInfo);
			if (iAllResultCnt == 0) {
				String strMessageContent = "流水单号：" + allocateInfo.getAllId() + "更新失败！";
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
			}
		}
		return iAllResultCnt;

	}

	/**
	 * 
	 * @author liuyaowen
	 * @version 2017年7月13日
	 * 
	 *          根据线路ID获取到最近的流水
	 * @param allocateInfo
	 *            需要保存的数据
	 * @return
	 */
	@Transactional(readOnly = false)
	public AllAllocateInfo getMaxdateByrouteId(AllAllocateInfo allocateInfo) {
		return allocateInfoDao.getMaxdateByrouteId(allocateInfo);

	}

	/**
	 * 
	 * @author liuyaowen
	 * @version 2017年7月13日
	 * 
	 *          根据流水获取到Detail
	 * @param allocateInfo
	 *            需要保存的数据
	 * @return
	 */
	public List<AllAllocateDetail> getBoxNoByAllId(AllAllocateInfo allocateInfo) {
		return allocateDetailDao.getBoxNoByAllId(allocateInfo);
	}

	/**
	 * 
	 * @author liuyaowen
	 * @version 2017年7月13日
	 * 
	 *          根据交接ID获取到所有机构ID
	 * @param allocateInfo
	 *            需要保存的数据
	 * @return
	 */
	public List<AllAllocateInfo> getAllAllocateInfoByHandoverId(String handoverId) {
		return allocateInfoDao.getAllAllocateInfoByHandoverId(handoverId);
	}

	/**
	 * 
	 * @author liuyaowen
	 * @version 2017年7月13日
	 * 
	 *          根据交接ID获取到所有机构ID
	 * @param allocateInfo
	 *            需要保存的数据
	 * @return
	 */
	@Transactional(readOnly = false)
	public int updateDetailStatusByBoxNo(AllAllocateDetail allocateDetail) {
		return allocateDetailDao.updateDetailStatusByBoxNo(allocateDetail);
	}

	/**
	 * 
	 * @author liuyaowen
	 * @version 2017年7月13日
	 * 
	 *          插入补录信息
	 * @param allocateInfo
	 *            需要保存的数据
	 * @return
	 */
	@Transactional(readOnly = false)
	public int insertAdditional(AllAllocateDetail allocateDetail) {
		return allocateDetailDao.insertAdditional(allocateDetail);
	}

	/**
	 * 
	 * @author liuyaowen
	 * @version 2017年7月14日
	 * 
	 *          插入交接明细表信息
	 * @param allocateInfo
	 * 
	 * @return true : 更新成功
	 */
	@Transactional(readOnly = false)
	public boolean insertHandoverDetail(AllAllocateInfo allocateInfo) {
		for (AllHandoverDetail allhandoverdetail : allocateInfo.getAllHandoverInfo().getDetailList()) {
			int insertResult = allHandoverDetailDao.insert(allhandoverdetail);
			if (insertResult == 0) {
				String strMessageContent = "交接明细：" + allhandoverdetail.getDetailId() + "插入失败！";
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
			}
		}
		return true;
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年7月18日
	 * 
	 *          网店下拨确认
	 * @param allocateInfo
	 * 
	 * @return
	 */

	@Transactional(readOnly = false)
	public void pointconfirm(AllAllocateInfo allocateInfo) {
		Map<String, Integer> updateAllocateIdMap = Maps.newHashMap();
		List<String> boxList = Lists.newArrayList();
		for (AllAllocateDetail boxDetail : allocateInfo.getAllDetailList()) {
			// 记录每个流水号对应接收钞箱数量
			if (!updateAllocateIdMap.containsKey(boxDetail.getAllId())) {
				updateAllocateIdMap.put(boxDetail.getAllId(), 1);
			} else {
				int iAcceptBoxNum = updateAllocateIdMap.get(boxDetail.getAllId()) + 1;
				updateAllocateIdMap.put(boxDetail.getAllId(), iAcceptBoxNum);
			}
			// 更新调拨详细表
			boxList.add(boxDetail.getBoxNo());
		}
		Iterator<String> keyIterator = updateAllocateIdMap.keySet().iterator();
		while (keyIterator.hasNext()) {
			String strAllId = keyIterator.next();
			// 设置接收个数
			allocateInfo.setAcceptNumber(updateAllocateIdMap.get(strAllId));
			// 设置状态
			allocateInfo.setStatus(AllocationConstant.Status.Finish);
			int iUpdateCnt = allocateInfoDao.updateAllocateInfoStatus(allocateInfo);
			int iUpdateTempCnt = tempAllocateInfoDao.updateAllocateInfoStatus(allocateInfo);
			if (iUpdateCnt == 0 && iUpdateTempCnt == 0) {
				String strMessageContent = "流水单号：" + strAllId + "对接业务状态(调拨主表)更新失败！";
				throw new BusinessException("message.E2015", strMessageContent, new String[] { "调拨主表" });
			}
		}
		// 发送通知
		List<String> paramsList = Lists.newArrayList();
		paramsList.add(getAllocate(allocateInfo.getAllId()).getrOffice().getName());
		paramsList.add(allocateInfo.getAllId());
		SysCommonUtils.allocateMessageQueueAdd(allocateInfo.getBusinessType(), allocateInfo.getStatus(), paramsList,
				allocateInfo.getaOffice().getId(), allocateInfo.getUpdateBy());
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(readOnly = false)
	public int update(AllAllocateInfo allocateInfo) {
		int insertResult = allocateInfoDao.update(allocateInfo);
		if (insertResult == 0) {
			String strMessageContent = "调拨主表：" + allocateInfo.getAllId() + "更新失败！";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		}
		return insertResult;
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年7月30日
	 * 
	 *          重新计算物品总价值
	 * @return
	 */
	public void computation(AllAllocateInfo allocationOrderCash) {
		BigDecimal confirmAmount = new BigDecimal(0.0d);
		BigDecimal registeAmount = new BigDecimal(0.0d);

		for (AllAllocateItem item : allocationOrderCash.getAllAllocateItemList()) {

			if (AllocationConstant.confirmFlag.Appointment.equals(item.getConfirmFlag())) {
				// 网点登记的场合，设置申请金额
				registeAmount = registeAmount.add(item.getMoneyAmount());
			} else if (AllocationConstant.confirmFlag.Confirm.equals(item.getConfirmFlag())) {
				// 库房登记的场合，设置审批金额
				confirmAmount = confirmAmount.add(item.getMoneyAmount());
			}
		}

		allocationOrderCash.setRegisterAmount(registeAmount);
		allocationOrderCash.setConfirmAmount(confirmAmount);
	}

	/**
	 * 
	 * @author liuyaowen
	 * @version 2017年7月14日
	 * 
	 *          根据流水获取到Item
	 * @return
	 */
	public List<AllAllocateItem> getAllocateItemByAllId(AllAllocateItem allAllocateItem) {
		return allAllocateItemDao.findItemsListByAllId(allAllocateItem);
	}

	/**
	 * 
	 * @author qph
	 * @version 2017年7月31日
	 * 
	 *          提交现金预约信息
	 * @param allocateInfo
	 *            现金预约明细
	 * @param allocationCash
	 *            登记用现金预约信息
	 * @param model
	 *            页面Session信息
	 * @param status
	 *            页面Session状态
	 * @param redirectAttributes
	 *            页面跳转信息
	 * @return 现金预约信息列表页面
	 */
	@Transactional(readOnly = false)
	public void saveAllcation(AllAllocateInfo allocateInfo, AllAllocateInfo allocationCash, Model model,
			SessionStatus status, RedirectAttributes redirectAttributes, HttpServletRequest request,
			HttpServletResponse response) {
		// 登陆用户
		User userInfo = UserUtils.getUser();
		allocationCash.setLoginUser(userInfo);

		// 如果提交的预约明細不存在的場合
		if (allocationCash.getAllAllocateItemList().size() < 2) {

			// [保存失败]：请添加明细数据！
			String strMessageContent = "预约明细不存在";
			throw new BusinessException("message.E2003", strMessageContent, new String[] { "预约明细" });
		}
		List<AllAllocateItem> itemList = allocationCash.getAllAllocateItemList();
		for (AllAllocateItem item : itemList) {
			if (AllocationConstant.confirmFlag.Appointment.equals(item.getConfirmFlag())) {
				// 调拨物品明细的登记类型为网点登记时，不处理
				continue;
			}
			String strGoodId = item.getGoodsId();
			StoStoresInfo storeInfo = StoreCommonUtils.getStoStoresInfoByGoodsId(strGoodId,
					allocationCash.getaOffice().getId());
			if (storeInfo == null) {
				String strGoodsName = StoreCommonUtils.getGoodsNameById(strGoodId);
				strGoodsName = StringUtils.isBlank(strGoodsName) ? "" : strGoodsName;
				// [配款失败]：物品[{0}:{1}]对应库存信息不存在，请稍后再试或与系统管理员联系！
				String strMessageContent = "对应库存信息不存在，请稍后再试或与系统管理员联系";
				throw new BusinessException("message.E2014", strMessageContent,
						new String[] { strGoodId, strGoodsName });
			}

			if (storeInfo.getSurplusStoNum() == null) {
				// [配款失败]：物品[{0}]库存不足！
				String strGoodsName = StoreCommonUtils.getGoodsNameById(strGoodId);
				String strMessageContent = "库存不足！";
				throw new BusinessException("message.E2013", strMessageContent, new String[] { strGoodsName });
			}
			item.setAllId(allocateInfo.getAllId());
			item.setAllocationInfo(allocateInfo);
			item.setAllItemsId(IdGen.uuid());
			allAllocateItemDao.insert(item);
		}
		allocationCash.setStatus(AllocationConstant.Status.BetweenConfirm);
		// 接收机构
		// allocationCash.setaOffice(allocationCash.getLoginUser().getOffice());
		// 确认人
		allocationCash.setConfirmName(allocationCash.getLoginUser().getName());
		// 确认日期
		allocationCash.setConfirmDate(new Date());
		// 执行插入处理
		allocationCash.preUpdate();
		int iAllResultCnt = 0;
		int TempiAllResultCnt = 0;
		if (AllocationConstant.PageType.TempStoreEdit.equals(allocationCash.getPageType())) {
			TempiAllResultCnt = tempAllocateInfoDao.updateAllocateInfoStatus(allocationCash);
		} else {
			iAllResultCnt = allocateInfoDao.updateAllocateInfoStatus(allocationCash);
		}
		if (iAllResultCnt + TempiAllResultCnt == 0) {
			String strMessageContent = "调拨主表：" + allocateInfo.getAllId() + "更新失败！";
			throw new BusinessException("message.E2015", strMessageContent, new String[] { "调拨主表" });
		}

		List<AllAllocateItem> allitemList = Lists.newArrayList();
		for (AllAllocateItem tempItem1 : allocationCash.getAllAllocateItemList()) {
			if (AllocationConstant.confirmFlag.Confirm.equals(tempItem1.getConfirmFlag())) {
				AllAllocateItem allitem = new AllAllocateItem();
				allitem.setGoodsId(tempItem1.getGoodsId());
				allitem.setConfirmFlag(tempItem1.getConfirmFlag());
				allitem.setMoneyNumber(-tempItem1.getMoneyNumber());
				allitemList.add(allitem);
			}
		}
		// 下拨审批后修改库存
		updateSurplusStorebyitemList(allocationCash, allitemList, allocationCash.getaOffice().getId());
		// 清空Session
		status.setComplete();
	}

	/**
	 * 调拨主表的数据一致性验证
	 * 
	 * @param allAllocateInfo
	 */
	public void checkVersion(AllAllocateInfo allAllocateInfo) {

		// 数据一致性验证
		AllAllocateInfo oldData = getAllocate(allAllocateInfo.getAllId());

		if (oldData != null) {
			String oldUpdateDate = DateUtils.formatDate(oldData.getUpdateDate(),
					Constant.Dates.FORMATE_YYYYMMDDHHMMSSSSSSSS);
			if (!oldUpdateDate.equals(allAllocateInfo.getStrUpdateDate())) {
				throw new BusinessException("message.E0007", "", new String[] { allAllocateInfo.getAllId() });
			}
		} else {
			throw new BusinessException("message.E0008", "", new String[] { allAllocateInfo.getAllId() });
		}
	}

	/**
	 * 
	 * @author qph
	 * @version 2017年8月1日
	 * 
	 *          修改现金预约信息 以及 删除对应调拨详细
	 * @param allocateInfo
	 *            现金预约明细
	 * 
	 */
	@Transactional(readOnly = false)
	public int updateAndDeleteDetail(AllAllocateInfo allocateInfo) {
		int insertResult = allocateInfoDao.update(allocateInfo);
		if (insertResult == 0) {
			String strMessageContent = "调拨主表：" + allocateInfo.getAllId() + "更新失败！";
			throw new BusinessException("message.E2015", strMessageContent, new String[] { "调拨主表" });
		}
		if (allocateInfo.getAllDetailList().size() > 0) {
			AllAllocateDetail detail = allocateInfo.getAllDetailList().get(0);
			detail.setAllocationInfo(allocateInfo);
			allocateDetailDao.deleteDetailByAllId(detail);
		}

		return insertResult;
	}

	/**
	 * 
	 * @author liuyaowen
	 * @version 2017-08-09
	 * 
	 *          查询临时线路任务
	 * @param allAllocateInfo
	 * @return
	 */
	public List<AllAllocateInfo> findTempAllocateInfoList(AllAllocateInfo allAllocateInfo) {
		return tempAllocateInfoDao.findAllocationList(allAllocateInfo);
	}

	/**
	 * 1：更新调拨主表信息 2：插入调拨详细表信息
	 * 
	 * @author Lemon
	 * 
	 * @param allocateInfo
	 */
	@Transactional(readOnly = false)
	public void updateTempAllocation(AllAllocateInfo allocateInfo) {
		// 插入调拨主表信息
		tempAllocateInfoDao.update(allocateInfo);

		AllAllocateDetail allallocatedetail = allocateInfo.getAllAllocateDetail();
		allallocatedetail.setAllocationInfo(allocateInfo);
		allocateDetailDao.deleteDetailByAllId(allallocatedetail);

		// 插入调拨详细表
		for (AllAllocateDetail box : allocateInfo.getAllDetailList()) {
			box.setAllocationInfo(allocateInfo);
			box.setPdaScanDate(new Date());
			int insertresult = allocateDetailDao.insert(box);
			if (insertresult == 0) {
				String strMessageContent = "流水单号：" + box.getAllDetailId() + ")(调拨详细表)更新失败！";
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
			}
		}
		// 发送通知
		List<String> paramsList = Lists.newArrayList();
		paramsList.add(getAllocate(allocateInfo.getAllId()).getrOffice().getName());
		paramsList.add(allocateInfo.getAllId());
		SysCommonUtils.allocateMessageQueueAdd(getAllocate(allocateInfo.getAllId()).getBusinessType(),
				allocateInfo.getStatus(), paramsList, getAllocate(allocateInfo.getAllId()).getrOffice().getId(),
				allocateInfo.getUpdateBy());
	}

	/**
	 * @author qipeihong
	 * @version 2017/08/10
	 * 
	 *          取得预约以及临时预约信息
	 * @param page
	 *            页面信息
	 * @param allocateInfo
	 *            查询条件
	 * @return 查询结果
	 */
	public Page<AllAllocateInfo> findOrderCashAndTempCash(Page<AllAllocateInfo> page, AllAllocateInfo allocateInfo) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		allocateInfo.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "RO", null));
		// 查询条件：开始时间
		if (allocateInfo.getCreateTimeStart() != null) {
			allocateInfo
					.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(allocateInfo.getCreateTimeStart()),
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 查询条件：结束时间
		if (allocateInfo.getCreateTimeEnd() != null) {
			allocateInfo.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(allocateInfo.getCreateTimeEnd()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 计算金库未审批任务以及网点未接收任务
		countTask(allocateInfo);
		allocateInfo.setPage(page);
		// 根据查询条件，取得调拨全部信息
		List<AllAllocateInfo> allocationList = tempAllocateInfoDao.findAllocationAndTempList(allocateInfo);
		for (AllAllocateInfo allocate : allocationList) {
			String today = DateUtils.formatDate(new Date(), AllocationConstant.Dates.FORMATE_YYYY_MM_DD);
			String createDay = DateUtils.formatDate(allocate.getCreateDate(),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD);
			if (AllocationConstant.TaskType.TEMPORARY_TASK.equals(allocate.getTaskType())
					&& (AllocationConstant.Status.Register.equals(allocate.getStatus())
							|| AllocationConstant.Status.BetweenConfirm.equals(allocate.getStatus()))
					&& !today.equals(createDay)) {
				allocate.setTempFlag("1");

			}
		}

		page.setList(allocationList);
		return page;
	}

	/**
	 * @author qipeihong
	 * @version 2017/08/10
	 * 
	 *          根据流水号，取得调缴信息以及临时调缴信息
	 * @param page
	 *            页面信息
	 * @param allocateInfo
	 *            查询条件
	 * @param pageFlag
	 *            页面查询标识(true:页面查询/false:不是页面查询)
	 * @return 查询结果
	 */
	public Page<AllAllocateInfo> findAllocationAndTemp(Page<AllAllocateInfo> page, AllAllocateInfo allocateInfo) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		allocateInfo.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "RO", null));
		// 查询条件：开始时间
		if (allocateInfo.getCreateTimeStart() != null) {
			allocateInfo
					.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(allocateInfo.getCreateTimeStart()),
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 查询条件：结束时间
		if (allocateInfo.getCreateTimeEnd() != null) {
			allocateInfo.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(allocateInfo.getCreateTimeEnd()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 计算金库未审批任务以及网点未接收任务
		countTask(allocateInfo);
		allocateInfo.setPage(page);
		// 根据查询条件，取得调拨全部信息
		List<AllAllocateInfo> allocationList = tempAllocateInfoDao.findAllocationAndTempList(allocateInfo);
		for (AllAllocateInfo allocate : allocationList) {
			String today = DateUtils.formatDate(new Date(), AllocationConstant.Dates.FORMATE_YYYY_MM_DD);
			String createDay = DateUtils.formatDate(allocate.getCreateDate(),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD);
			if (AllocationConstant.TaskType.TEMPORARY_TASK.equals(allocate.getTaskType())
					&& (AllocationConstant.Status.Register.equals(allocate.getStatus())
							|| AllocationConstant.Status.BetweenConfirm.equals(allocate.getStatus()))
					&& !today.equals(createDay)) {
				allocate.setTempFlag("1");

			}
		}
		page.setList(allocationList);
		return page;
	}

	/**
	 * 
	 * @author liuyaowen
	 * @version 2017年7月13日
	 * 
	 * @return
	 */
	@Transactional(readOnly = false)
	public List<AllAllocateDetail> findDetailByAllId(AllAllocateDetail allAllocateDetail) {
		return allocateDetailDao.findList(allAllocateDetail);
	}

	/**
	 * @author chengshu
	 * @version 2015/05/07
	 * 
	 *          根据流水号，取得调缴信息
	 * @param page
	 *            页面信息
	 * @param allocateInfo
	 *            查询条件
	 * @param pageFlag
	 *            页面查询标识(true:页面查询/false:不是页面查询)
	 * @return 查询结果
	 */
	public Page<AllAllocateInfo> findAllocationPage(Page<AllAllocateInfo> page, AllAllocateInfo allocateInfo,
			boolean pageFlag) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		allocateInfo.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "RO", null));
		// 查询条件：开始时间
		if (allocateInfo.getCreateTimeStart() != null) {
			allocateInfo
					.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(allocateInfo.getCreateTimeStart()),
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 查询条件：结束时间
		if (allocateInfo.getCreateTimeEnd() != null) {
			allocateInfo.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(allocateInfo.getCreateTimeEnd()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 计算箱子个数
		countBoxNumber(allocateInfo);
		allocateInfo.setPage(page);
		// 根据查询条件，取得调拨全部信息
		List<AllAllocateInfo> allocationList = allocateInfoDao.findHandinPageList(allocateInfo);
		page.setList(allocationList);
		return page;
	}

	/**
	 * 撤回
	 * 
	 * @param cancelParam
	 */
	@Transactional(readOnly = false)
	public void handInCancel(AllAllocateInfo allAllocateInfo) {
		// 取得当前登录用户和机构
		User currentUser = UserUtils.getUser();
		Office currentOffice = currentUser.getOffice();
		// 验证"撤回原因"是否为空
		if (StringUtils.isBlank(allAllocateInfo.getCancelReason())) {
			throw new BusinessException("message.E2061", "", new String[] {});
		}
		// 验证"撤回原因"长度不能小于10
		if (allAllocateInfo.getCancelReason().length() < 10) {
			throw new BusinessException("message.E2064", "", new String[] {});
		}
		// 验证"撤回原因"长度不能大于600
		if (allAllocateInfo.getCancelReason().length() > 600) {
			throw new BusinessException("message.E2065", "", new String[] {});
		}

		// 根据流水号取得款袋信息
		List<String> boxNoList = Lists.newArrayList();
		List<String> serialorderNoList = Lists.newArrayList();
		serialorderNoList.add(allAllocateInfo.getAllId());
		allAllocateInfo.setAllIds(serialorderNoList);
		List<AllAllocateDetail> detailList = allocateDetailDao.getBoxNoByAllId(allAllocateInfo);
		for (AllAllocateDetail detail : detailList) {
			boxNoList.add(detail.getBoxNo());
		}
		// 1、修改箱子状态
		StoBoxInfo stoBoxInfo = new StoBoxInfo();
		stoBoxInfo.setBoxNos(boxNoList);
		List<String> tailBoxNoList = Lists.newArrayList();
		List<String> otherBoxNoList = Lists.newArrayList();
		List<StoBoxInfo> boxInfoList = stoBoxInfoService.searchBoxList(stoBoxInfo);
		for (StoBoxInfo boxInfo : boxInfoList) {
			if (!Constant.BoxType.BOX_TAIL.equals(boxInfo.getBoxType())) {
				otherBoxNoList.add(boxInfo.getId());
			} else {
				tailBoxNoList.add(boxInfo.getId());
			}
		}
		// 更新箱子状态：尾箱改为空箱，其他改为在网点
		if (!Collections3.isEmpty(tailBoxNoList)) {
			StoreCommonUtils.updateBoxStatusBatch(tailBoxNoList, Constant.BoxStatus.EMPTY, currentUser);
		}
		if (!Collections3.isEmpty(otherBoxNoList)) {
			StoreCommonUtils.updateBoxStatusBatch(otherBoxNoList, Constant.BoxStatus.BANK_OUTLETS, currentUser);
		}
		// 2、修改预约状态
		AllAllocateInfo cancelParam = getAllocate(allAllocateInfo.getAllId());
		cancelParam.setCancelReason(allAllocateInfo.getCancelReason());
		cancelParam.setStatus(AllocationConstant.Status.CANCEL_STATUS);
		cancelParam.setUpdateBy(currentUser);
		cancelParam.setUpdateDate(new Date());
		cancelParam.setCancelOffice(currentOffice);
		cancelParam.setCancelUser(currentUser);
		cancelParam.setCancelDate(new Date());
		allocateInfoDao.update(cancelParam);
		// 发送通知
		List<String> paramsList = Lists.newArrayList();
		paramsList.add(cancelParam.getrOffice().getName());
		paramsList.add(cancelParam.getAllId());
		SysCommonUtils.allocateMessageQueueAdd(cancelParam.getBusinessType(), cancelParam.getStatus(), paramsList,
				cancelParam.getaOffice().getId(), UserUtils.getUser());
	}

	/**
	 * 现金预约撤回
	 * 
	 * @param allAllocateInfo
	 */
	@Transactional(readOnly = false)
	public void handOutCancel(AllAllocateInfo allAllocateInfo) {
		// 取得当前登录用户和机构
		User currentUser = UserUtils.getUser();
		Office currentOffice = currentUser.getOffice();
		// 验证"撤回原因"是否为空
		if (StringUtils.isBlank(allAllocateInfo.getCancelReason())) {
			throw new BusinessException("message.E2061", "", new String[] {});
		}
		// 验证"撤回原因"长度不能小于10
		if (allAllocateInfo.getCancelReason().length() < 10) {
			throw new BusinessException("message.E2064", "", new String[] {});
		}
		// 验证"撤回原因"长度不能大于600
		if (allAllocateInfo.getCancelReason().length() > 600) {
			throw new BusinessException("message.E2065", "", new String[] {});
		}
		// 根据流水号取得款袋信息
		List<String> boxNoList = Lists.newArrayList();
		List<String> serialorderNoList = Lists.newArrayList();
		serialorderNoList.add(allAllocateInfo.getAllId());
		allAllocateInfo.setAllIds(serialorderNoList);
		List<AllAllocateDetail> detailList = allocateDetailDao.getBoxNoByAllId(allAllocateInfo);
		for (AllAllocateDetail detail : detailList) {
			boxNoList.add(detail.getBoxNo());
		}
		// 1、修改箱子状态
		if (!Collections3.isEmpty(boxNoList)) {

			StoBoxInfo stoBoxInfo = new StoBoxInfo();
			stoBoxInfo.setBoxNos(boxNoList);
			List<String> tailBoxNoList = Lists.newArrayList();
			List<String> otherBoxNoList = Lists.newArrayList();
			List<StoBoxInfo> boxInfoList = stoBoxInfoService.searchBoxList(stoBoxInfo);
			for (StoBoxInfo boxInfo : boxInfoList) {
				if (!Constant.BoxType.BOX_TAIL.equals(boxInfo.getBoxType())) {
					otherBoxNoList.add(boxInfo.getId());
				} else {
					tailBoxNoList.add(boxInfo.getId());
				}
			}
			// 更新箱子状态：尾箱改为空箱，其他改为在库房
			if (!Collections3.isEmpty(tailBoxNoList)) {
				StoreCommonUtils.updateBoxStatusBatch(tailBoxNoList, Constant.BoxStatus.EMPTY, currentUser);
			}
			if (!Collections3.isEmpty(otherBoxNoList)) {
				StoreCommonUtils.updateBoxStatusBatch(otherBoxNoList, Constant.BoxStatus.COFFER, currentUser);
			}
		}
		// 2、修改预约状态
		AllAllocateInfo cancelParam = getAllocate(allAllocateInfo.getAllId());
		cancelParam.setCancelReason(allAllocateInfo.getCancelReason());
		cancelParam.setStatus(AllocationConstant.Status.CANCEL_STATUS);
		cancelParam.setUpdateBy(currentUser);
		cancelParam.setUpdateDate(new Date());
		cancelParam.setCancelOffice(currentOffice);
		cancelParam.setCancelUser(currentUser);
		cancelParam.setCancelDate(new Date());
		if (AllocationConstant.TaskType.ROUTINET_TASK.equals(allAllocateInfo.getTaskType())) {
			allocateInfoDao.update(cancelParam);
		}
		if (AllocationConstant.TaskType.TEMPORARY_TASK.equals(allAllocateInfo.getTaskType())) {
			tempAllocateInfoDao.update(cancelParam);
		}
		// 发送通知
		List<String> paramsList = Lists.newArrayList();
		paramsList.add(cancelParam.getrOffice().getName());
		paramsList.add(cancelParam.getAllId());
		SysCommonUtils.allocateMessageQueueAdd(cancelParam.getBusinessType(), cancelParam.getStatus(), paramsList,
				cancelParam.getaOffice().getId(), UserUtils.getUser());
		// 3、回滚库存
		allAllocateInfo.setLoginUser(currentUser);
		AllAllocateItem allallocateItem = new AllAllocateItem();
		allallocateItem.setAllId(allAllocateInfo.getAllId());
		List<AllAllocateItem> itemList = allAllocateItemDao.findItemsList(allallocateItem);
		updateSurplusStorebyitemList(allAllocateInfo, itemList, allAllocateInfo.getaOffice().getId());
	}

	/**
	 * 
	 * @author liuyaowen
	 * @version 2015年10月29日 根据boxNos查询主表
	 * @return
	 */
	public List<AllAllocateInfo> getAllIdByBoxNo(String boxNo) {
		return allocateInfoDao.getAllAllocateInfoByBoxNo(boxNo);
	}

	/**
	 * @author qipeihong
	 * 
	 * @version 2017-08-023 计算金库未审批任务以及网点未接收任务
	 * @param allAllocateInfo
	 *            页面调拨信息
	 * @param allocationList
	 *            调拨查询结果
	 */
	private void countTask(AllAllocateInfo allocateInfo) {

		// 查询条件：开始时间
		if (allocateInfo.getCreateTimeStart() != null) {
			allocateInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(allocateInfo.getCreateTimeStart())));
		}
		// 查询条件：结束时间
		if (allocateInfo.getCreateTimeEnd() != null) {
			allocateInfo.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(allocateInfo.getCreateTimeEnd())));
		}

		// 根据查询条件，取得调拨全部信息
		List<AllAllocateInfo> allocationList = tempAllocateInfoDao.findAllocationAndTempList(allocateInfo);
		// 金库未审批个数
		int unApproved = 0;
		// 网点未接收个数
		int unAccept = 0;
		// 失效任务个数
		int invalid = 0;
		for (AllAllocateInfo allocate : allocationList) {
			String today = DateUtils.formatDate(new Date(), AllocationConstant.Dates.FORMATE_YYYY_MM_DD);
			String createDay = DateUtils.formatDate(allocate.getCreateDate(),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD);
			if (AllocationConstant.TaskType.TEMPORARY_TASK.equals(allocate.getTaskType())
					&& (AllocationConstant.Status.Register.equals(allocate.getStatus())
							|| AllocationConstant.Status.BetweenConfirm.equals(allocate.getStatus()))
					&& !today.equals(createDay)) {
				allocate.setTempFlag("1");
				invalid++;
				continue;
			}
			if (AllocationConstant.Status.Register.equals(allocate.getStatus())) {
				unApproved++;
			}
			if (!AllocationConstant.Status.Finish.equals(allocate.getStatus())) {
				if (!AllocationConstant.Status.CANCEL_STATUS.equals(allocate.getStatus())) {
					unAccept++;
				}
			}
		}
		allocateInfo.setInvalid(invalid);
		allocateInfo.setUnApproved(unApproved);
		allocateInfo.setUnAccept(unAccept);
	}

	/**
	 * @author yanbingxu @date 2017-8-26 @Description
	 *         折线图数据填充 @param @return @throws
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> makeLineGraphData(AllAllocateInfo allocateInfo) {
		Map<String, Object> rtnMap = Maps.newHashMap();

		// 图表数据初始化
		List<String> legendDataList = Lists.newArrayList();
		List<String> xAxisDataList = Lists.newArrayList();
		List<Map<String, Object>> seriesDataList = Lists.newArrayList();

		// 查询图表数据
		List<AllAllocateInfo> resultList = allocateInfoDao.findLineGraphData(allocateInfo);

		Map<String, Map<String, Object>> seriesMap = Maps.newHashMap();
		for (AllAllocateInfo entity : resultList) {
			if (!legendDataList.contains(entity.getrOfficeName())) {
				legendDataList.add(entity.getrOfficeName());
			}
			if (!xAxisDataList.contains(entity.getStrDate())) {
				xAxisDataList.add(entity.getStrDate());
			}
		}
		// 遍历查询结果数据集，处理数据
		for (AllAllocateInfo entity : resultList) {
			if (!seriesMap.containsKey(entity.getrOfficeName())) {
				String name = entity.getrOfficeName();
				String type = GraphType.LINE;
				List<String> dataList = Lists.newArrayList();
				int num;
				for (num = 0; num < xAxisDataList.size(); num++) {
					dataList.add("0");
				}
				for (num = 0; num < xAxisDataList.size(); num++) {
					if (xAxisDataList.get(num).equals(entity.getStrDate())) {
						dataList.set(num, "1");
						break;
					}
				}
				Map<String, Object> map = Maps.newHashMap();
				map.put(SeriesProperties.SERIES_NAME_KEY, name);
				map.put(SeriesProperties.SERIES_TYPE_KEY, type);
				map.put(SeriesProperties.SERIES_DATA_KEY, dataList);
				map.put(SeriesProperties.SERIES_SMOOTH_KEY, true);
				seriesMap.put(entity.getrOfficeName(), map);
			} else {
				int status;
				Map<String, Object> map = (Map<String, Object>) seriesMap.get(entity.getrOfficeName());
				List<String> dataList = (List<String>) map.get(SeriesProperties.SERIES_DATA_KEY);
				for (status = 0; status < xAxisDataList.size(); status++) {
					if (xAxisDataList.get(status).equals(entity.getStrDate())) {
						dataList.set(status, StringUtils.toString(Integer.valueOf(dataList.get(status)) + 1));
						break;
					}
				}
			}
		}
		Iterator<String> iterator = seriesMap.keySet().iterator();

		while (iterator.hasNext()) {
			seriesDataList.add(seriesMap.get(iterator.next()));
		}

		// 设置图表标题信息
		switch (allocateInfo.getBusinessType()) {
		case "21":
			rtnMap.put("titleMessage", "库间清分");
			break;
		case "22":
			rtnMap.put("titleMessage", "库间配钞");
			break;
		case "23":
			rtnMap.put("titleMessage", "库间清机");
			break;
		default:
			rtnMap.put("titleMessage", "库间调拨");
		}
		// 将处理后的数据填充
		rtnMap.put(DataGraphList.LEGEND_DATE_LIST, legendDataList);
		rtnMap.put(DataGraphList.XAXIS_DATE_LIST, xAxisDataList);
		rtnMap.put(DataGraphList.SERIES_DATE_LIST, seriesDataList);
		rtnMap.put(SeriesProperties.SERIES_NAME_KEY, DictUtils.getDictLabel(
				allocateInfo.getDateFlag().replace(Constant.Punctuation.HYPHEN, Constant.Punctuation.HALF_UNDERLINE),
				"report_filter_condition", null));
		return rtnMap;
	}

	/**
	 * @author yanbingxu @date 2017-8-30 @Description
	 *         柱状图数据填充 @param @return @throws
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> makeBarGraphData(AllAllocateInfo allocateInfo) {
		Map<String, Object> rtnMap = Maps.newHashMap();

		// 图表数据初始化
		List<String> legendDataList = Lists.newArrayList();
		List<String> xAxisDataList = Lists.newArrayList();
		List<Map<String, Object>> seriesDataList = Lists.newArrayList();

		// 设置数据过滤条件
		if (allocateInfo.getInoutType().equals(InOutCoffer.OUT)) {
			if (allocateInfo.getBusinessType().equals(BusinessType.Between_ATM_Clear)) {
				String[] businessTypes = new String[] { "noResult" };
				allocateInfo.setBusinessTypes(Arrays.asList(businessTypes));
			} else {
				if (allocateInfo.getBusinessTypes().contains(BusinessType.Between_ATM_Clear)) {
					String[] businessTypes = new String[] { BusinessType.Between_Clear, BusinessType.Between_ATM_Add };
					allocateInfo.setBusinessTypes(Arrays.asList(businessTypes));
				}
			}
		}

		// 查询图表数据
		List<AllAllocateInfo> resultList = allocateInfoDao.findBarGraphData(allocateInfo);

		Map<String, Map<String, Object>> seriesMap = Maps.newHashMap();

		for (AllAllocateInfo entity : resultList) {
			if (StringUtils.isNotBlank(entity.getGoodsId())) {
				if (!legendDataList.contains(stoGoodsService.get(entity.getGoodsId()).getGoodsName())) {
					legendDataList.add(stoGoodsService.get(entity.getGoodsId()).getGoodsName());
				}
			}
			if (!xAxisDataList.contains(entity.getStrDate())) {
				xAxisDataList.add(entity.getStrDate());
			}
		}

		// 遍历查询结果数据集，处理数据
		for (AllAllocateInfo entity : resultList) {
			if (StringUtils.isNotBlank(entity.getGoodsId())) {
				if (!seriesMap.containsKey(stoGoodsService.get(entity.getGoodsId()).getGoodsName())) {
					String name = stoGoodsService.get(entity.getGoodsId()).getGoodsName();
					String type = GraphType.BAR;
					List<String> dataList = Lists.newArrayList();
					int num;
					for (num = 0; num < xAxisDataList.size(); num++) {
						dataList.add("0");
					}
					for (num = 0; num < xAxisDataList.size(); num++) {
						if (xAxisDataList.get(num).equals(entity.getStrDate())) {
							break;
						}
					}
					if (entity.getMoneyAmount() != 0) {
						dataList.set(num, StringUtils.toString(entity.getMoneyAmount()));
					}
					Map<String, Object> map = Maps.newHashMap();
					map.put(SeriesProperties.SERIES_NAME_KEY, name);
					map.put(SeriesProperties.SERIES_TYPE_KEY, type);
					map.put(SeriesProperties.SERIES_DATA_KEY, dataList);
					map.put(SeriesProperties.SERIES_BAR_WIDTH_KEY, "50%");
					map.put(SeriesClass.STACK, "总量");
					seriesMap.put(stoGoodsService.get(entity.getGoodsId()).getGoodsName(), map);
				} else {
					int status;
					Map<String, Object> map = (Map<String, Object>) seriesMap
							.get(stoGoodsService.get(entity.getGoodsId()).getGoodsName());
					List<String> dataList = (List<String>) map.get(SeriesProperties.SERIES_DATA_KEY);
					for (status = 0; status < xAxisDataList.size(); status++) {
						if (xAxisDataList.get(status).equals(entity.getStrDate())) {
							break;
						}
					}
					if (entity.getMoneyAmount() != 0) {
						long amount = Long.valueOf(dataList.get(status)) + entity.getMoneyAmount();
						dataList.set(status, StringUtils.toString(amount));
					}
				}
			}
		}

		Iterator<String> iterator = seriesMap.keySet().iterator();

		while (iterator.hasNext()) {
			seriesDataList.add(seriesMap.get(iterator.next()));
		}

		// 设置图表标题信息
		switch (allocateInfo.getBusinessType()) {
		case "21":
			rtnMap.put("titleMessage", "库间清分");
			break;
		case "22":
			rtnMap.put("titleMessage", "库间配钞");
			break;
		case "23":
			rtnMap.put("titleMessage", "库间清机");
			break;
		default:
			rtnMap.put("titleMessage", "库间调拨");
		}

		// 将处理后的数据填充
		rtnMap.put(DataGraphList.LEGEND_DATE_LIST, legendDataList);
		rtnMap.put(DataGraphList.XAXIS_DATE_LIST, xAxisDataList);
		rtnMap.put(DataGraphList.SERIES_DATE_LIST, seriesDataList);
		rtnMap.put(SeriesProperties.SERIES_NAME_KEY, DictUtils.getDictLabel(
				allocateInfo.getDateFlag().replace(Constant.Punctuation.HYPHEN, Constant.Punctuation.HALF_UNDERLINE),
				"report_filter_condition", null));
		return rtnMap;
	}

	/**
	 * 
	 * @author liuyaowen
	 * @version 2017年7月14日
	 * 
	 *          库外箱袋入库确认接口
	 * @param allocateInfo
	 *            更新信息及条件
	 * @return true : 更新成功
	 */
	@Transactional(readOnly = false)
	public boolean acceptConfirm(AllHandoverInfo handoverInfo) {
		// 插入交接主表
		int iInsertCnt = handoverInfoDao.insert(handoverInfo);
		if (iInsertCnt == 0) {
			String strMessageContent = "交接信息(交接表)作成失败！";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		}
		if (!handoverInfo.getDetailList().isEmpty()) {
			for (AllHandoverDetail allhandoverdetail : handoverInfo.getDetailList()) {
				int insertResult = allHandoverDetailDao.insert(allhandoverdetail);
				if (insertResult == 0) {
					String strMessageContent = "交接明细：" + allhandoverdetail.getDetailId() + "更新失败！";
					throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
				}
			}
		}
		return true;
	}

	/**
	 * 通过rfid更新调拨明细
	 * 
	 * @author xp
	 * @param allocateDetail
	 */
	@Transactional(readOnly = false)
	public void updateDetailByRfid(AllAllocateDetail allocateDetail) {
		int num = allocateDetailDao.updateDetailByRfid(allocateDetail);
		if (num == 0) {
			String strMessageContent = "调拨明细：" + allocateDetail.getAllId() + "更新失败！";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		}
	}

	/**
	 * 根据网点接收交接id查询交接detail
	 * 
	 * @author liuyaowen
	 */
	public List<AllHandoverDetail> findDetailByHandoverId(String pointHandoverId) {
		return allHandoverDetailDao.findListByHandoverId(pointHandoverId);
	}

	/**
	 * @author qipeihong
	 * @version 2017/11/09
	 * 
	 *          插入ATM调拨信息(接口用)
	 * @param allocateInfo
	 *            调拨信息内容
	 * @param insertFalg
	 *            创建标识
	 * @return 插入结果
	 */
	@Transactional(readOnly = false)
	public int saveAtmAllocation(AllAllocateInfo allocateInfo) {

		// 插入件数
		int allResult = 0;
		int detailResult = 0;

		// 新规的场合，插入主表
		if (StringUtils.isBlank(allocateInfo.getAllId())) {
			// 主键空的场合，设置主键
			allocateInfo.setAllId(BusinessUtils.getNewBusinessNo(
					Global.getConfig("businessType.allocation.out.AtmBoxOut"), allocateInfo.getrOffice()));

			// 执行插入处理
			allResult = allocateInfoDao.insert(allocateInfo);

			// 更新的场合，更新金额和更新者，删除已经存在的详细信息
		} else {
			// 执行更新处理
			allResult = allocateInfoDao.update(allocateInfo);

			// 删除已经存在的详细信息
			AllAllocateDetail detailDelete = new AllAllocateDetail();
			detailDelete.setAllocationInfo(allocateInfo);
			detailResult = allocateDetailDao.deleteDetailByAllId(detailDelete);

		}

		// 插入详细表
		for (AllAllocateDetail box : allocateInfo.getAllDetailList()) {
			box.setAllocationInfo(allocateInfo);
			detailResult += allocateDetailDao.insert(box);
		}

		return allResult + detailResult;
	}

	/**
	 * @author sg
	 * @version 2017/11/09
	 * 
	 * @Description 根据流水号取得钞箱入库信息
	 * @param allId
	 *            流水号
	 * @return 调拨钞箱入库信息
	 */
	public AllAllocateInfo get(String allId) {
		return allocateInfoDao.get(allId);
	}

	/**
	 * @author sg
	 * @version 2017/11/09
	 * 
	 *          取得钞箱出入库信息
	 * @param page
	 *            页面信息
	 * @param allocateInfo
	 *            查询条件
	 * @return 查询结果
	 */
	public Page<AllAllocateInfo> findAtmBoxHand(Page<AllAllocateInfo> page, AllAllocateInfo allocateInfo) {
		// 查询条件：开始时间
		if (allocateInfo.getCreateTimeStart() != null) {
			allocateInfo
					.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(allocateInfo.getCreateTimeStart()),
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 查询条件：结束时间
		if (allocateInfo.getCreateTimeEnd() != null) {
			allocateInfo.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(allocateInfo.getCreateTimeEnd()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		allocateInfo.setPage(page);
		/* 生成数据权限过滤条件，查询钞箱出入库列表 修改人：XL 修改时间：2018-01-04 begin */
		allocateInfo.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "o", null));
		List<AllAllocateInfo> allocationList = allocateInfoDao.findAtmBoxInOutList(allocateInfo);
		/* end */
		for (AllAllocateInfo allocationLists : allocationList) {
			// 移交人
			StringBuffer handoverUserName = new StringBuffer();
			// 接收人
			StringBuffer escortUserName = new StringBuffer();
			// 循环找出所有的移交人和接收人用逗号分隔进行拼接
			if (allocationLists.getStoreHandover() != null) {
				for (AllHandoverDetail allHandoverDetails : allocationLists.getStoreHandover().getDetailList()) {
					if (AllocationConstant.OperationType.TURN_OVER.equals(allHandoverDetails.getOperationType())) {
						handoverUserName.append(allHandoverDetails.getEscortName() + ",");
					}
					if (AllocationConstant.OperationType.ACCEPT.equals(allHandoverDetails.getOperationType())) {
						escortUserName.append(allHandoverDetails.getEscortName() + ",");
					}

				}
			}

			// 移交人和接收人不为空插入移交人和接收人
			if (handoverUserName.length() > 0) {
				allocationLists.getAllAllocateDetail().setHandoverUserName(
						handoverUserName.toString().substring(0, handoverUserName.toString().length() - 1));
			}
			if (escortUserName.length() > 0) {
				allocationLists.getAllAllocateDetail().setEscortUserName(
						escortUserName.toString().substring(0, escortUserName.toString().length() - 1));
			}
		}
		page.setList(allocationList);
		return page;
	}

	/**
	 * 2017-11-10
	 * 
	 * @author xp ATM清机业务查询箱子是否存在于未完成的流水中
	 * @param boxList
	 */
	public void checkBoxExitsInUnfinishAllocate(List<String> boxList) {
		// 未完成流水中存在的箱子
		List<String> existsInUnfinishAllocateList = Lists.newArrayList();
		for (String boxNo : boxList) {
			// 查询未完成的流水中是否存在当前箱子
			AllAllocateInfo allAllocateInfo = new AllAllocateInfo();
			// 设置业务类型为库间清机
			allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.Between_ATM_Clear);
			// 设置有效标识为有效
			allAllocateInfo.setDelFlag(Constant.deleteFlag.Valid);
			// 设置流水状态为待入库
			allAllocateInfo.setStatus(AllocationConstant.BetweenStatus.TO_IN_STORE_STATUS);
			// 查询出所有对应的流水
			List<AllAllocateInfo> allAllocateInfoList = findBetweenPageList(allAllocateInfo);
			for (AllAllocateInfo allocateInfo : allAllocateInfoList) {
				List<AllAllocateItem> itemList = allocateInfo.getAllAllocateItemList();
				for (AllAllocateItem allAllocateItem : itemList) {
					if (boxNo.equals(allAllocateItem.getBoxNo())) {
						existsInUnfinishAllocateList.add(boxNo);
					}
				}
			}
		}
		if (!Collections3.isEmpty(existsInUnfinishAllocateList)) {
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99,
					"箱号：" + existsInUnfinishAllocateList + "在其他待入库的流水中已存在");
		}
	}

	/**
	 * @author sg
	 * @version 2017/11/13
	 * 
	 *          取得钞箱出入库信息
	 * @param page
	 *            页面信息
	 * @param allocateInfo
	 *            查询条件
	 * @return 查询结果
	 */
	@Transactional(readOnly = false)
	public List<AllAllocateInfo> findAtmBoxList(AllAllocateInfo allAllocateInfo) {
		return allocateInfoDao.findList(allAllocateInfo);
	}

	/**
	 * 通过rfid或boxNo更新对应详细表信息（ATM库外清分入库确认接口用）
	 * 
	 * @author sg
	 * @param allocateDetail
	 * @return
	 */
	@Transactional(readOnly = false)
	public int updateDetailByBoxNoorRfid(AllAllocateDetail allocateDetail) {
		return allocateDetailDao.updateDetailByBoxNoorRfid(allocateDetail);
	}

	/**
	 * @author sg
	 * @date 2017-11-13
	 * 
	 *       通过allId更新对应主表信息（ATM库外清分入库确认接口用）
	 */
	@Transactional(readOnly = false)
	public int updateAtm(AllAllocateInfo allocateInfo) {
		return allocateInfoDao.updateAtm(allocateInfo);
	}

	/**
	 * @author sg
	 * @date 2017-11-15
	 * 
	 *       通过handoverId更新对应主表信息（ATM库外交接任务交接接口）
	 */
	@Transactional(readOnly = false)
	public int updateAllHandoverInfo(AllHandoverInfo allHandoverInfo) {
		return handoverInfoDao.update(allHandoverInfo);
	}

	/**
	 * @author sg
	 * @date 2017-11-16
	 * 
	 *       向AllHandoverDetail表中插入数据（ATM库外交接任务交接接口）
	 */
	@Transactional(readOnly = false)
	public int AllHandoverDetailInsert(AllHandoverDetail allHandoverDetail) {
		return allHandoverDetailDao.insert(allHandoverDetail);
	}

	/**
	 * 获取同步的加钞计划的boxList(箱带明细)
	 * 
	 * @author wxz
	 * @version 2017年11月15日
	 * @param addPlanId
	 * @return
	 */
	public List<Map<String, Object>> getPDABoxDetail(String addPlanId) {
		AllAllocateInfo allAllocateInfo = new AllAllocateInfo();
		allAllocateInfo.setRouteId(addPlanId);
		allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.ATM_BOX_HANDOUT);
		/** 修改查询条件 创建人：xl 创建时间： 2017-11-22 begin **/
		// 状态
		List<String> statuses = Lists.newArrayList();
		statuses.add(AllocationConstant.Status.HandoverTodo);
		statuses.add(AllocationConstant.Status.Onload);
		allAllocateInfo.setStatuses(statuses);
		// 扫描标识
		List<String> scanFlagList = Lists.newArrayList();
		scanFlagList.add(AllocationConstant.ScanFlag.Scan);
		scanFlagList.add(AllocationConstant.ScanFlag.Additional);
		allAllocateInfo.setScanFlagList(scanFlagList);
		/** end **/
		List<Map<String, Object>> boxListDetail = allocateInfoDao.findByAddPlanId(allAllocateInfo);
		return boxListDetail;
	}

	/**
	 * 插入调拨主表和调拨详细表信息
	 * 
	 * @author wxz
	 * @version 2017年11月15日
	 * @param allocateInfo
	 */
	@Transactional(readOnly = false)
	public void insertInfoAndDetail(AllAllocateInfo allocateInfo) {
		// 插入调拨主表信息
		int insertInfoResult = allocateInfoDao.insert(allocateInfo);
		if (insertInfoResult == 0) {
			String strMessageContent = "调拨主表：" + allocateInfo.getAllId() + "更新失败！";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		}
		// 插入调拨详细表
		for (AllAllocateDetail box : allocateInfo.getAllDetailList()) {
			box.setAllocationInfo(allocateInfo);
			int insertDetailResult = allocateDetailDao.insert(box);
			if (insertDetailResult == 0) {
				String strMessageContent = "调拨详细：" + allocateInfo.getAllId() + "更新失败！";
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
			}
		}
	}

	/**
	 * 
	 * @author wxz
	 * @version 2017年11月15日
	 * 
	 *          根据流水号或者rfid更新状态
	 * @param allocateInfo
	 *            更新条件
	 * @return 更新数量
	 */
	@Transactional(readOnly = false)
	public int updatInfoStatus(AllAllocateInfo allocateInfo) {

		return allocateInfoDao.updateAllocateInfoStatus(allocateInfo);
	}

	/**
	 * 根据加钞计划批次号查询ATM清机钞箱信息
	 * 
	 * @author xp
	 * @param planId
	 * @return list
	 */
	@Transactional(readOnly = true)
	public List<AtmClearBoxInfo> getAtmClearBoxInfoByPlanId(String planId) {
		/** 修改方法参数 修改人：xl 修改时间：2017-11-21 begin */
		AllAllocateItem allAllocateItem = new AllAllocateItem();
		allAllocateItem.setBatchNo(planId);
		return allAllocateItemDao.getAtmClearBoxInfoByPlanId(allAllocateItem);
		/** end */
	}

	/**
	 * 
	 * @author sg
	 * @version 2017年11月23日
	 * 
	 *          根据allId查询所有非未扫描的信息
	 * @param allId
	 * @return
	 */
	public List<AllAllocateDetail> getByAllIdscanFlag(String allId) {
		return allocateDetailDao.getByAllIdscanFlag(allId);
	}

	/**
	 * 根据钞箱号获取加钞计划ID
	 * 
	 * @author XL
	 * @version 2017年12月13日
	 * @param boxNo
	 * @return
	 */
	public String getPlanIdByBoxNo(String boxNo) {
		// 加钞计划ID
		String planId = "";
		List<AllAllocateInfo> allAllocateInfoList = Lists.newArrayList();
		// 调缴主表
		AllAllocateInfo allAllocateInfo = new AllAllocateInfo();
		// 调缴明细
		AllAllocateDetail allAllocateDetail = new AllAllocateDetail();
		// 设置钞箱编号
		allAllocateDetail.setBoxNo(boxNo);
		// 设置业务类型
		allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.ATM_BOX_HANDIN);
		allAllocateInfo.setAllAllocateDetail(allAllocateDetail);
		allAllocateInfoList = allocateInfoDao.getPlanIdByBoxNo(allAllocateInfo);
		if (allAllocateInfoList.size() > 0) {
			planId = allAllocateInfoList.get(0).getRouteId();
		} else {
			planId = null;
		}
		return planId;
	}

	/**
	 * 根据查询条件，取得调拨全部信息
	 * 
	 * @author XL
	 * @version 2018年9月10日
	 * @param allocateInfo
	 * @return
	 */
	public List<AllAllocateInfo> findAllocationAndTempList(AllAllocateInfo allocateInfo) {
		// 查询条件：开始时间
		if (allocateInfo.getCreateTimeStart() != null) {
			allocateInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(allocateInfo.getCreateTimeStart())));
		}
		// 查询条件：结束时间
		if (allocateInfo.getCreateTimeEnd() != null) {
			allocateInfo.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(allocateInfo.getCreateTimeEnd())));
		}
		// 根据查询条件，取得调拨全部信息
		List<AllAllocateInfo> allocationList = tempAllocateInfoDao.findAllocationAndTempList(allocateInfo);
		return allocationList;
	}
	
	/**
	 * 根据箱袋编号取得调拨详细信息
	 * 
	 * @author WQJ
	 * @version 2019年1月9日
	 * @param allocateDetail
	 * @return
	 */
	public List<AllAllocateDetail> findAllocateDetailByNo(AllAllocateDetail allocateDetail){
		List<AllAllocateDetail> allAllocateDetail=allocateDetailDao.findAllocateDetailByNo(allocateDetail);
		return allAllocateDetail;
	}
}
