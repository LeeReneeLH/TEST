package com.coffer.businesses.modules.doorOrder.v01.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorErrorInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorErrorInfo;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 差错管理Service
 * 
 * @author XL
 * @version 2019-06-28
 */
@Service
@Transactional(readOnly = true)
public class DoorErrorInfoService extends CrudService<DoorErrorInfoDao, DoorErrorInfo> {

	public DoorErrorInfo get(String id) {
		return super.get(id);
	}

	public List<DoorErrorInfo> findList(DoorErrorInfo doorErrorInfo) {
		return super.findList(doorErrorInfo);
	}

	@Transactional(readOnly = false)
	public void save(DoorErrorInfo doorErrorInfo) {
		super.save(doorErrorInfo);
	}

	@Transactional(readOnly = false)
	public void delete(DoorErrorInfo doorErrorInfo) {
		super.delete(doorErrorInfo);
	}

	/**
	 * 
	 * 上门收款商户差错商户列表
	 * 
	 * @author ZXK
	 * @version 2020年3月9日
	 * @param page
	 * @param doorErrorInfo
	 * @return 
	 */
	public Page<DoorErrorInfo> findMerchantPage(Page<DoorErrorInfo> page, DoorErrorInfo doorErrorInfo) {
		List<DoorErrorInfo> result = getTotalAmount(doorErrorInfo);
		doorErrorInfo.setPage(page);
		result.addAll(findMerchantList(doorErrorInfo));
		page.setList(result);
		return page;
	}
	
	/**
	 * 
	 * 获取商户清点差错列表
	 * 
	 * @author ZXK
	 * @version 2020年3月9日
	 * @param page
	 * @param doorErrorInfo
	 * @return
	 */
	public List<DoorErrorInfo> findMerchantList(DoorErrorInfo doorErrorInfo) {
		// 获取登录机构
		Office office = UserUtils.getUser().getOffice();
		// 机构过滤
		if (Constant.OfficeType.CLEAR_CENTER.equals(office.getType())) {
			// 清分中心
			doorErrorInfo.setOffice(office);
		} else {
			doorErrorInfo.getSqlMap().put("dsf", "AND( mc.parent_ids LIKE '%" + office.getId() + "%' OR mc.ID =" + office.getId() + ")");
		}
		// 查询条件： 开始时间
		if (doorErrorInfo.getCreateTimeStart() != null) {
			doorErrorInfo.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(doorErrorInfo.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (doorErrorInfo.getCreateTimeEnd() != null) {
			doorErrorInfo.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(doorErrorInfo.getCreateTimeEnd())));
		}
		return dao.findMerchantList(doorErrorInfo);
	}

	/**
	 * 
	 * Title: getTotalAmount
	 * <p>
	 * Description: 计算合计数据
	 * </p>
	 * 
	 * @author: ZXK
	 * @version 2020年3月6日
	 * @param historyUseRecordsDetail
	 * @return List<HistoryUseRecordsDetail> 返回类型
	 */
	public List<DoorErrorInfo> getTotalAmount(DoorErrorInfo doorErrorInfo) {
		List<DoorErrorInfo> list = findMerchantList(doorErrorInfo);
		List<DoorErrorInfo> result = Lists.newArrayList();
		// 合计行
		DoorErrorInfo doorError = new DoorErrorInfo();
		// 差额初始化
		BigDecimal merAmount = new BigDecimal(0);
		if (Collections3.isEmpty(list)) {
			list = Lists.newArrayList();
		}
		for (DoorErrorInfo info : list) {
			// 总金额
			if ("2".equals(info.getErrorType())) {
				merAmount = merAmount.add(info.getMerchantAmount());
			} else {
				merAmount = merAmount.subtract(info.getMerchantAmount());
			}

		}
		// 合计计算
		doorError.setMerchantName("合计");
		if (merAmount.compareTo(new BigDecimal(0)) < 0) {
			merAmount = merAmount.abs();
			doorError.setErrorType("3");
		} else if (merAmount.compareTo(new BigDecimal(0)) > 0) {
			doorError.setErrorType("2");
		} else {
			doorError.setErrorType("");
		}
		doorError.setMerchantAmount(merAmount);
		result.add(doorError);
		return result;
	}

	/**
	 * 
	 * 门店差错列表
	 * 
	 * @author gzd
	 * @version 2020年3月5日
	 * @param page
	 * @param doorErrorInfo
	 * @return page
	 */
	public Page<DoorErrorInfo> findDoorPage(Page<DoorErrorInfo> page, DoorErrorInfo doorErrorInfo) {
		List<DoorErrorInfo> result = getDoorPool(findDoorList(doorErrorInfo));
		doorErrorInfo.setPage(page);
		result.addAll(findDoorList(doorErrorInfo));
		page.setList(result);
		return page;
	}

	/**
	 * 
	 * 门店差错列表
	 * 
	 * @author gzd
	 * @version 2020年3月5日
	 * @param doorErrorInfo
	 * @return list
	 */
	public List<DoorErrorInfo> findDoorList(DoorErrorInfo doorErrorInfo) {
		// 获取当前登录人机构
		Office office = UserUtils.getUser().getOffice();
		// 登陆人所属机构
		if (Constant.OfficeType.CLEAR_CENTER.equals(office.getType())) {
			// 清分中心
			doorErrorInfo.setOffice(office);
		} else if (Constant.OfficeType.STORE.equals(office.getType())) {
			// 门店
			doorErrorInfo.setOfficeId(office.getParentId());
			doorErrorInfo.setCustNo(office.getId());
		} else {
			// 机构过滤
			doorErrorInfo.getSqlMap().put("dsf", "AND( cust.parent_ids LIKE '%" + office.getId() + "%' OR cust.parent_id =" + office.getId() + ")");
		}
		// 查询条件： 开始时间
		if (doorErrorInfo.getCreateTimeStart() != null) {
			doorErrorInfo.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(doorErrorInfo.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (doorErrorInfo.getCreateTimeEnd() != null) {
			doorErrorInfo.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(doorErrorInfo.getCreateTimeEnd())));
		}

		return dao.findDoorList(doorErrorInfo);
	}

	/**
	 * 
	 * 门店差错明细
	 * 
	 * @author gzd
	 * @version 2020年3月6日
	 * @param page
	 * @param doorErrorInfo
	 * @return page
	 */
	public Page<DoorErrorInfo> findDoorDetailPage(Page<DoorErrorInfo> page, DoorErrorInfo doorErrorInfo) {
		// 获取当前登录人机构
		Office office = UserUtils.getUser().getOffice();
		// 登陆人所属机构
		if (Constant.OfficeType.CLEAR_CENTER.equals(office.getType())) {
			// 清分中心
			doorErrorInfo.setOffice(office);
		} else if (Constant.OfficeType.STORE.equals(office.getType())) {
			// 门店
			doorErrorInfo.setOfficeId(office.getParentId());
			doorErrorInfo.setCustNo(office.getId());
		} else {
			// 机构过滤
			doorErrorInfo.getSqlMap().put("dsf", "AND( cust.parent_ids LIKE '%" + office.getId() + "%' OR cust.parent_id =" + office.getId() + ")");
		}
		// 查询条件： 开始时间
		if (doorErrorInfo.getCreateTimeStart() != null) {
			doorErrorInfo.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(doorErrorInfo.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (doorErrorInfo.getCreateTimeEnd() != null) {
			doorErrorInfo.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(doorErrorInfo.getCreateTimeEnd())));
		}
		// List<DoorErrorInfo> result = getDoorPool(findDoorDetailList(doorErrorInfo));
		// 合计行查询 gzd 2020-06-02
		List<DoorErrorInfo> result = dao.findDoorDetailListPool(doorErrorInfo);
		if(result.isEmpty()){
			DoorErrorInfo doorErrorInfoPool = new DoorErrorInfo();
			doorErrorInfoPool.setCustName("合计");
			result.add(doorErrorInfoPool);
		}else{
			result.get(0).setCustName("合计");
		}
		doorErrorInfo.setPage(page);
		List<DoorErrorInfo> doorErrorInfoList =findDoorDetailList(doorErrorInfo);
		// 格式化钞袋使用时间(钞袋使用时间SQL获取 暂注 )
		/*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (DoorErrorInfo dInfo : doorErrorInfoList) {
			if (null != dInfo.getLastTime() && null != dInfo.getThisTime()) {
				dInfo.setBagNoUseTime(
						DateUtils.getDistanceTime(sdf.format(dInfo.getLastTime()), sdf.format(dInfo.getThisTime())));
			}
		}*/
		result.addAll(doorErrorInfoList);
		page.setList(result);
		return page;
	}

	/**
	 * 
	 * 门店差错明细
	 * 
	 * @author gzd
	 * @version 2020年3月6日
	 * @param doorErrorInfo
	 * @return list
	 */
	public List<DoorErrorInfo> findDoorDetailList(DoorErrorInfo doorErrorInfo) {
		return dao.findDoorDetailList(doorErrorInfo);
	}

	/**
	 * 
	 * 合计行计算
	 * 
	 * @author gzd
	 * @version 2020年3月6日
	 * @param list
	 * @return
	 */
	public List<DoorErrorInfo> getDoorPool(List<DoorErrorInfo> list) {
		List<DoorErrorInfo> result = new ArrayList<DoorErrorInfo>();
		// 合计行
		DoorErrorInfo doorErrorInfoPool = new DoorErrorInfo();
		// 初始化
		BigDecimal sumDiffAmount = new BigDecimal(0);
		String errorType = "";
		StringBuffer custNo = new StringBuffer("");
		// 合计计算
		for (DoorErrorInfo doorErrorInfo : list) {
			custNo.append(doorErrorInfo.getCustNo() + ",");
			if (doorErrorInfo.getStatus().equals("0")) {
				if (doorErrorInfo.getErrorType().equals("2")) {
					sumDiffAmount = sumDiffAmount.add(doorErrorInfo.getDiffAmount());
				} else {
					sumDiffAmount = sumDiffAmount.subtract(doorErrorInfo.getDiffAmount());
				}
			}
		}
		if(!"".equals(custNo.toString())){
			custNo.replace(custNo.length()-1, custNo.length(), "");
		}
		if (sumDiffAmount.compareTo(new BigDecimal(0)) < 0) {
			sumDiffAmount = sumDiffAmount.abs();
			errorType = "3";
		} else if (sumDiffAmount.compareTo(new BigDecimal(0)) > 0) {
			errorType = "2";
		}
		doorErrorInfoPool.setCustName("合计");
		/*doorErrorInfoPool.setCustNo(custNo.toString());*/
		doorErrorInfoPool.setDiffAmount(sumDiffAmount);
		doorErrorInfoPool.setErrorType(errorType);
		result.add(doorErrorInfoPool);
		return result;
	}

}