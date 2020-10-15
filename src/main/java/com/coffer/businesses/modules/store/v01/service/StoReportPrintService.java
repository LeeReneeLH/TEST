package com.coffer.businesses.modules.store.v01.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.v01.dao.StoBoxInfoDao;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * @author niguoyong
 * @version 2015-09-15
 */
@Component
@Transactional(readOnly = true)
public class StoReportPrintService extends BaseService {

	@Autowired
	private StoBoxInfoDao boxDao;

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-15
	 * 
	 * @Description 库房报表统计查询
	 * @return
	 */
	public List<StoBoxInfo> find(StoBoxInfo stoBoxInfo) {
		return boxDao.findSqlBox(stoBoxInfo);
	}

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-15
	 * 
	 * @Description 库房报表统计查询(在途箱袋查询)
	 * @return
	 */
	public List<StoBoxInfo> findInTransitBoxInfos() {

		StoBoxInfo box = new StoBoxInfo ();
		box.setBoxStatus(Constant.BoxStatus.ONPASSAGE);
		// 查询数据列表
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		box.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "o5", null));
		
		List<StoBoxInfo> list = boxDao.findList(box);

		return list;
	}

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-15
	 * 
	 * @Description 报表显示信息封装到map中
	 * @return
	 */
	public Map<String, Object> reportMessage() {
		// 分类数据存储map中
		Map<String, Object> map = Maps.newHashMap();

		// 款箱数量【在库】
		int cashBoxNum = 0;
		// 尾箱【在库】
		int trailBoxNum = 0;
		// 钞箱数量【在库】
		int atmBoxNum = 0;
		/*// 款包【在库】
		int cashBagNum = 0;
		// 稽核包【在库】
		int auditBag = 0;
		// 重空凭证【在库】
		int emptyCertificates = 0;
		// 交换包【在库】
		int changeBag = 0;*/
		

		// 款箱列表【在途】
		List<StoBoxInfo> cashBoxList = Lists.newArrayList();
		// 尾箱列表【在途】
		List<StoBoxInfo> trailBoxList = Lists.newArrayList();
		// 钞箱列表【在途】
		List<StoBoxInfo> atmBoxList = Lists.newArrayList();
		/*// 款包列表【在途】
		List<StoBoxInfo> cashBagList = Lists.newArrayList();
		// 稽核包列表【在途】
		List<StoBoxInfo> auditBagList = Lists.newArrayList();
		// 重空凭证列表【在途】
		List<StoBoxInfo> emptyCertificatesList = Lists.newArrayList();
		// 交换包列表【在途】
		List<StoBoxInfo> changeBagList = Lists.newArrayList();*/
		
		// 款箱列表【在网点】
		List<StoBoxInfo> outlets_cashBoxList = Lists.newArrayList();
		// 尾箱列表【在网点】
		List<StoBoxInfo> outlets_trailBoxList = Lists.newArrayList();
		// 钞箱列表【在网点】
		List<StoBoxInfo> outlets_atmBoxList = Lists.newArrayList();
/*		// 款包列表【在网点】
		List<StoBoxInfo> outlets_cashBagList = Lists.newArrayList();
		// 稽核包列表【在网点】
		List<StoBoxInfo> outlets_auditBagList = Lists.newArrayList();
		// 重空凭证列表【在网点】
		List<StoBoxInfo> outlets_emptyCertificatesList = Lists.newArrayList();
		// 交换包列表【在网点】
		List<StoBoxInfo> outlets_changeBagList = Lists.newArrayList();*/

		// 获取库房箱袋信息
		StoBoxInfo box =new StoBoxInfo();		
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		box.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "o5", null));
		List<StoBoxInfo> list = find(box);
		// 过滤箱袋信息
		if (!Collections3.isEmpty(list)) {
			for (StoBoxInfo stoBoxInfo : list) {
				// 在途
				if (Constant.BoxStatus.ONPASSAGE.equals(stoBoxInfo.getBoxStatus())
						|| Constant.BoxStatus.ATM_BOX_STATUS_ADD_MONEY.equals(stoBoxInfo.getBoxStatus())
						|| Constant.BoxStatus.ATM_BOX_STATUS_CLEAR_IN.equals(stoBoxInfo.getBoxStatus())) {
					// 款箱
					if (Constant.BoxType.BOX_PARAGRAPH.equals(stoBoxInfo.getBoxType())) {
						// 统计款箱数量【在途】
						cashBoxList.add(stoBoxInfo);
					}
					// 钞箱
					if (Constant.BoxType.BOX_NOTE.equals(stoBoxInfo.getBoxType())) {
						// 统计钞箱数量【在途】,钞箱允许在途一定时间
						//Date date = DateUtils.parseDate(StringUtils.substring(stoBoxInfo.getUpdateDate().toString(),
							//	0, 10));
						if (Long.parseLong(Global.getConfig("atm.add.plan.cycle")) <= DateUtils.pastDays(stoBoxInfo.getUpdateDate())) {
							atmBoxList.add(stoBoxInfo);
						}
					}
					// 尾箱
					if (Constant.BoxType.BOX_TAIL.equals(stoBoxInfo.getBoxType())) {
						// 统计尾箱数量【在途】
						trailBoxList.add(stoBoxInfo);
					}
/*					// 款包
					if (Constant.BoxType.BOX_BAG.equals(stoBoxInfo.getBoxType())) {
						// 统计款包数量【在途】
						cashBagList.add(stoBoxInfo);
					}
					// 交换包
					if (Constant.BoxType.BOX_BILL_3.equals(stoBoxInfo.getBoxType())) {
						// 统计交换包数量【在途】
						changeBagList.add(stoBoxInfo);
					}
					// 稽核包
					if (Constant.BoxType.BOX_BILL_1.equals(stoBoxInfo.getBoxType())) {
						// 统计稽核包数量【在途】
						auditBagList.add(stoBoxInfo);
					}
					// 重空凭证
					if (Constant.BoxType.BOX_BILL_2.equals(stoBoxInfo.getBoxType())) {
						// 统计重空凭证数量【在途】
						emptyCertificatesList.add(stoBoxInfo);
					}*/
				}
				// 在库
				else if (Constant.BoxStatus.EMPTY.equals(stoBoxInfo.getBoxStatus()) // 空箱
						|| Constant.BoxStatus.COFFER.equals(stoBoxInfo.getBoxStatus())// 在库房
						|| Constant.BoxStatus.CLASSFICATION.equals(stoBoxInfo.getBoxStatus())//整点室
						|| Constant.BoxStatus.ATM_BOX_STATUS_USE.equals(stoBoxInfo.getBoxStatus())//17 在用 
						|| Constant.BoxStatus.ATM_BOX_STATUS_CLEAR.equals(stoBoxInfo.getBoxStatus())//18清点 
						|| Constant.BoxStatus.ATM_BOX_STATUS_BACK.equals(stoBoxInfo.getBoxStatus())//19退库
						|| Constant.BoxStatus.ATM_BOX_STATUS_ADD_STORE.equals(stoBoxInfo.getBoxStatus())//20加钞寄库
						|| Constant.BoxStatus.ATM_BOX_STATUS_CLEAR_STORE.equals(stoBoxInfo.getBoxStatus())//21清点寄库
						|| Constant.BoxStatus.ATM_BOX_STATUS_BACK_STORE.equals(stoBoxInfo.getBoxStatus())//22退库寄库
						|| Constant.BoxStatus.ATM_BOX_STATUS_PREPARE_OUT.equals(stoBoxInfo.getBoxStatus())//23待出库
						) { 
					// 款箱
					if (Constant.BoxType.BOX_PARAGRAPH.equals(stoBoxInfo.getBoxType())) {
						// 统计款箱数量【非在途】
						cashBoxNum = cashBoxNum + 1;
						continue;
					}
					// 尾箱
					if (Constant.BoxType.BOX_TAIL.equals(stoBoxInfo.getBoxType())) {
						// 统计尾箱数量
						trailBoxNum = trailBoxNum + 1;
						continue;
					}
					/*// 款包
					if (Constant.BoxType.BOX_BAG.equals(stoBoxInfo.getBoxType())) {
						// 统计黄金箱数量
						cashBagNum = cashBagNum + 1;
						continue;
					}
					// 钞箱
					if (Constant.BoxType.BOX_NOTE.equals(stoBoxInfo.getBoxType())) {
						// 统计尾箱数量
						atmBoxNum = atmBoxNum + 1;
						continue;
					}
					// 重空凭证
					if (Constant.BoxType.BOX_BILL_2.equals(stoBoxInfo.getBoxType())) {
						// 统计重空凭证数量
						emptyCertificates = emptyCertificates + 1;
						continue;
					}
					// 稽核包
					if (Constant.BoxType.BOX_BILL_1.equals(stoBoxInfo.getBoxType())) {
						// 统计稽核包数量
						auditBag = auditBag + 1;
						continue;
					}
					// 交换包
					if (Constant.BoxType.BOX_BILL_3.equals(stoBoxInfo.getBoxType())) {
						// 统计交换包数量
						changeBag = changeBag + 1;
						continue;
					}*/
				}
				//网点
				else if(Constant.BoxStatus.BANK_OUTLETS.equals(stoBoxInfo.getBoxStatus()) ){//网点
					// 款箱
					if (Constant.BoxType.BOX_PARAGRAPH.equals(stoBoxInfo.getBoxType())) {
						// 统计款箱数量【非在途】
						outlets_cashBoxList.add(stoBoxInfo);
					}
					// 尾箱
					if (Constant.BoxType.BOX_TAIL.equals(stoBoxInfo.getBoxType())) {
						// 统计尾箱数量
						outlets_trailBoxList.add(stoBoxInfo);
					}
					// 钞箱
					if (Constant.BoxType.BOX_NOTE.equals(stoBoxInfo.getBoxType())) {
						// 统计尾箱数量
						outlets_atmBoxList.add(stoBoxInfo);
					}
					/*// 款包
					if (Constant.BoxType.BOX_BAG.equals(stoBoxInfo.getBoxType())) {
						// 统计黄金箱数量
						outlets_cashBagList.add(stoBoxInfo);
					}
					// 重空凭证
					if (Constant.BoxType.BOX_BILL_2.equals(stoBoxInfo.getBoxType())) {
						// 统计重空凭证数量
						outlets_emptyCertificatesList.add(stoBoxInfo);
					}
					// 稽核包
					if (Constant.BoxType.BOX_BILL_1.equals(stoBoxInfo.getBoxType())) {
						// 统计稽核包数量
						outlets_auditBagList.add(stoBoxInfo);
					}
					// 交换包
					if (Constant.BoxType.BOX_BILL_3.equals(stoBoxInfo.getBoxType())) {
						// 统计交换包数量
						outlets_changeBagList.add(stoBoxInfo);
					}*/
				}
			}
			// 在库数量
			map.put("cashBoxNum", cashBoxNum);
			map.put("atmBoxNum", atmBoxNum);
			map.put("trailBoxNum", trailBoxNum);
			/*map.put("cashBagNum", cashBagNum);
			map.put("changeBag", changeBag);
			map.put("auditBag", auditBag);
			map.put("emptyCertificates", emptyCertificates);*/
			// 在网点数量
			List<HashMap<String, Object>> outletsList = Lists.newArrayList();
			if (!Collections3.isEmpty(outlets_cashBoxList)) {
				HashMap<String, Object> outlets_cashBoxMap = Maps.newHashMap();
				outlets_cashBoxMap.put("name", "store.cashBox");
				outlets_cashBoxMap.put("num", String.valueOf(outlets_cashBoxList.size()));
				outlets_cashBoxMap.put("value", Collections3.extractToString(outlets_cashBoxList, "id", ","));
				outletsList.add(outlets_cashBoxMap);
			}
			if (!Collections3.isEmpty(outlets_atmBoxList)) {
				HashMap<String, Object> outlets_atmBoxMap = Maps.newHashMap();
				outlets_atmBoxMap.put("name", "store.atmBox");
				outlets_atmBoxMap.put("num", String.valueOf(outlets_atmBoxList.size()));
				outlets_atmBoxMap.put("value", Collections3.extractToString(outlets_atmBoxList, "id", ","));
				outletsList.add(outlets_atmBoxMap);
			}
			if (!Collections3.isEmpty(outlets_trailBoxList)) {
				HashMap<String, Object> outlets_trailBoxMap = Maps.newHashMap();
				outlets_trailBoxMap.put("name", "store.trailBox");
				outlets_trailBoxMap.put("num", String.valueOf(outlets_trailBoxList.size()));
				outlets_trailBoxMap.put("value", Collections3.extractToString(outlets_trailBoxList, "id", ","));
				outletsList.add(outlets_trailBoxMap);
			}
			/*if (!Collections3.isEmpty(outlets_cashBagList)) {
				HashMap<String, Object> outlets_goldBoxMap = Maps.newHashMap();
				outlets_goldBoxMap.put("name", "label.store.report.table.cashBag");
				outlets_goldBoxMap.put("num", String.valueOf(outlets_cashBagList.size()));
				outlets_goldBoxMap.put("value", Collections3.extractToString(outlets_cashBagList, "id", ","));
				outletsList.add(outlets_goldBoxMap);
			}
			if (!Collections3.isEmpty(outlets_auditBagList)) {
				HashMap<String, Object> outlets_auditBagMap = Maps.newHashMap();
				outlets_auditBagMap.put("name", "label.store.report.table.auditBag");
				outlets_auditBagMap.put("num", String.valueOf(outlets_auditBagList.size()));
				outlets_auditBagMap.put("value", Collections3.extractToString(outlets_auditBagList, "id", ","));
				outletsList.add(outlets_auditBagMap);
			}
			if (!Collections3.isEmpty(outlets_changeBagList)) {
				HashMap<String, Object> outlets_changeBagMap = Maps.newHashMap();
				outlets_changeBagMap.put("name", "label.store.report.table.changeBag");
				outlets_changeBagMap.put("num", String.valueOf(outlets_changeBagList.size()));
				outlets_changeBagMap.put("value", Collections3.extractToString(outlets_changeBagList, "id", ","));
				outletsList.add(outlets_changeBagMap);
			}
			if (!Collections3.isEmpty(outlets_emptyCertificatesList)) {
				HashMap<String, Object> outlets_emptyCertificatesMap = Maps.newHashMap();
				outlets_emptyCertificatesMap.put("name", "label.store.report.table.emptyCertificates");
				outlets_emptyCertificatesMap.put("num", String.valueOf(outlets_emptyCertificatesList.size()));
				outlets_emptyCertificatesMap.put("value", Collections3.extractToString(outlets_emptyCertificatesList, "id", ","));
				outletsList.add(outlets_emptyCertificatesMap);
			}*/
			map.put("outlets_list", outletsList);
			// 在途明细
			List<HashMap<String, Object>> outWayList = Lists.newArrayList();
			if (!Collections3.isEmpty(cashBoxList)) {
				HashMap<String, Object> cashBoxMap = Maps.newHashMap();
				cashBoxMap.put("name", "store.cashBox");
				cashBoxMap.put("num", String.valueOf(cashBoxList.size()));
				cashBoxMap.put("value", Collections3.extractToString(cashBoxList, "id", ","));
				outWayList.add(cashBoxMap);
			}
			if (!Collections3.isEmpty(atmBoxList)) {
				HashMap<String, Object> atmBoxMap = Maps.newHashMap();
				atmBoxMap.put("name", "store.atmBox");
				atmBoxMap.put("num", String.valueOf(atmBoxList.size()));
				atmBoxMap.put("value", Collections3.extractToString(atmBoxList, "id", ","));
				outWayList.add(atmBoxMap);
			}
			if (!Collections3.isEmpty(trailBoxList)) {
				HashMap<String, Object> trailBoxMap = Maps.newHashMap();
				trailBoxMap.put("name", "store.trailBox");
				trailBoxMap.put("num", String.valueOf(trailBoxList.size()));
				trailBoxMap.put("value", Collections3.extractToString(trailBoxList, "id", ","));
				outWayList.add(trailBoxMap);
			}
			/*if (!Collections3.isEmpty(cashBagList)) {
				HashMap<String, Object> goldBoxMap = Maps.newHashMap();
				goldBoxMap.put("name", "label.store.report.table.cashBag");
				goldBoxMap.put("num", String.valueOf(cashBagList.size()));
				goldBoxMap.put("value", Collections3.extractToString(cashBagList, "id", ","));
				outWayList.add(goldBoxMap);
			}
			if (!Collections3.isEmpty(auditBagList)) {
				HashMap<String, Object> auditBagMap = Maps.newHashMap();
				auditBagMap.put("name", "label.store.report.table.auditBag");
				auditBagMap.put("num", String.valueOf(auditBagList.size()));
				auditBagMap.put("value", Collections3.extractToString(auditBagList, "id", ","));
				outWayList.add(auditBagMap);
			}
			if (!Collections3.isEmpty(changeBagList)) {
				HashMap<String, Object> changeBagMap = Maps.newHashMap();
				changeBagMap.put("name", "label.store.report.table.changeBag");
				changeBagMap.put("num", String.valueOf(changeBagList.size()));
				changeBagMap.put("value", Collections3.extractToString(changeBagList, "id", ","));
				outWayList.add(changeBagMap);
			}
			if (!Collections3.isEmpty(emptyCertificatesList)) {
				HashMap<String, Object> emptyCertificatesMap = Maps.newHashMap();
				emptyCertificatesMap.put("name", "label.store.report.table.emptyCertificates");
				emptyCertificatesMap.put("num", String.valueOf(emptyCertificatesList.size()));
				emptyCertificatesMap.put("value", Collections3.extractToString(emptyCertificatesList, "id", ","));
				outWayList.add(emptyCertificatesMap);
			}*/
			map.put("list", outWayList);
		}
		return map;
	}

}
