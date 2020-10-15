package com.coffer.businesses.modules.allocation.v01.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 调缴功能DAO接口
 * 
 * @author Chengshu
 * @version 2015-05-11
 */
@MyBatisDao
public interface AllAllocateInfoDao extends CrudDao<AllAllocateInfo> {

	/**
	 * @author chengshu
	 * @date 2015-05-14
	 * 
	 *       页面分页用查询
	 */
	public List<String> findPageList(AllAllocateInfo allocateInfo);

	/**
	 * 查询库间调拨信息列表
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月7日
	 * 
	 * 
	 * @param allocateInfo
	 *            查询条件
	 * @return 查询库间调拨信息列表
	 */
	public List<AllAllocateInfo> findBetweenPageList(AllAllocateInfo allocateInfo);

	/**
	 * 查询库外上缴信息列表
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月29日
	 * 
	 * 
	 * @param allocateInfo
	 *            查询条件
	 * @return 库外上缴信息列表
	 */
	public List<AllAllocateInfo> findHandinPageList(AllAllocateInfo allocateInfo);

	/**
	 * 根据流水号查询库间调拨信息列表
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月8日
	 * 
	 * 
	 * @param allId
	 *            查询条件 流水号
	 * @return 查询库间调拨信息列表
	 */
	public AllAllocateInfo findAllocateInfoByAllId(String allId);

	/**
	 * @author chengshu
	 * @date 2015-05-14
	 * 
	 *       调拨信息查询
	 */
	public List<AllAllocateInfo> findAllocationList(AllAllocateInfo allocateInfo);

	/**
	 * @author chengshu
	 * @date 2015-05-14
	 * 
	 *       库间现金调拨合计
	 */
	public List<AllAllocateInfo> countCashBetween(AllAllocateInfo allocateInfo);

	/**
	 * @author chengshu
	 * @date 2015-05-14
	 * 
	 *       更新调拨状态
	 */
	public int updateStatus(AllAllocateInfo allocateInfo);

	/**
	 * 更新接收状态及接收金额
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月10日
	 * 
	 * 
	 * @param allocateInfo
	 *            检索条件
	 * @return 更新数量
	 */
	public int updateBetweenAcceptStatus(AllAllocateInfo allocateInfo);

	/**
	 * 
	 * @author LLF
	 * @version 2015年6月19日
	 * 
	 *          根据箱袋编号查询调拨信息
	 * @param allocateInfo
	 * @return
	 */
	public List<AllAllocateInfo> findAllocationByNo(AllAllocateInfo allocateInfo);

	/**
	 * 
	 * @author suiwei
	 * @version 2015年9月11日
	 * 
	 *          更新配款信息
	 * @param allocateInfo
	 * @return
	 */
	public int updateAllocateInfoStatus(AllAllocateInfo allocateInfo);

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
	public String getCashBetweenRegistAllId(AllAllocateInfo allocateInfo);

	/**
	 * 
	 * @author chengshu
	 * @version 2015年9月14日
	 * 
	 *          查询调拨相关所有数据
	 * @param allocateInfo
	 *            查询条件
	 * @return 调拨相关数据
	 */
	public String AllAllocateInfoResult(AllAllocateInfo allocateInfo);

	/**
	 * 
	 * @author chengshu
	 * @version 2015年9月14日
	 * 
	 *          查询调拨相关所有数据
	 * @param allocateInfo
	 *            查询条件
	 * @return 库间调拨相关数据
	 */
	public AllAllocateInfo getBetween(String allId);

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年10月15日
	 * 
	 *          根据交接ID更新状态
	 * @param allocateInfo
	 *            调拨信息
	 * @return 更新数量
	 */
	public int updateStatusByHandoverId(AllAllocateInfo allocateInfo);

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
	public List<AllAllocateInfo> findBoxInfoList(AllAllocateInfo allocateInfo);

	/**
	 * 
	 * @author liuzhiheng
	 * @version 2015年10月13日
	 * 
	 *          查询PDA网点接收箱袋相关信息
	 * @param allocateInfo
	 *            查询条件
	 * @return PDA网点接收箱袋相关信息
	 */
	public List<AllAllocateInfo> serachbankAcceptedBoxDetail(AllAllocateInfo allAllocateInfo);

	/**
	 * 
	 * @author liuzhiheng
	 * @version 2015年10月14日
	 * 
	 *          库外箱袋出入库查询
	 * @param allocateInfo
	 *            查询条件
	 * @return 库外箱袋出入库相关信息
	 */
	public List<AllAllocateInfo> serachstoOutsideBoxInOrOutDetail(AllAllocateInfo allAllocateInfo);

	/**
	 * 
	 * @author liuzhiheng
	 * @version 2015年10月15日
	 * 
	 *          库外交接任务查询
	 * @param allocateInfo
	 *            查询条件
	 * @return 库外交接任务相关信息
	 */
	public List<AllAllocateInfo> serachstoOutsideHandoverDetail(AllAllocateInfo allAllocateInfo);

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年10月23日
	 * 
	 *          根据交接ID，交接状态，出入库类型，业务类型，查询调拨箱子列表
	 * @param allAllocateInfo
	 *            查询条件
	 * @return 调拨箱子列表
	 */
	public List<AllAllocateInfo> findBoxListByHandoverId(AllAllocateInfo allAllocateInfo);

	/**
	 * 
	 * @author chengshu
	 * @version 2015年10月23日
	 * 
	 *          取得个数（总个数，尾箱个数，款箱个数）
	 * @param allAllocateInfo
	 *            查询条件
	 * @return 调拨箱子列表
	 */

	public AllAllocateInfo getBoxCount(AllAllocateInfo allAllocateInfo);

	/**
	 * 
	 * @author liuyaowen
	 * @version 2017年7月13日
	 * 
	 *          根据线路ID获取到最近的一条流水
	 * @param allAllocateInfo
	 *            查询条件
	 * @return
	 */
	public AllAllocateInfo getMaxdateByrouteId(AllAllocateInfo allAllocateInfo);

	/**
	 * 
	 * @author liuyaowen
	 * @version 2017年7月13日
	 * 
	 *          根据交接ID获取到所有机构ID
	 * @param allAllocateInfo
	 *            查询条件
	 * @return
	 */
	public List<AllAllocateInfo> getAllAllocateInfoByHandoverId(
			@Param(value = "storeHandoverId") String storeHandoverId);

	/**
	 * 更新接收确认信息
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月11日
	 * 
	 * @param allAllocateInfo
	 *            主表信息
	 * @return 更新个数
	 */
	public void updateConfirmMessage(AllAllocateInfo allAllocateInfo);

	/**
	 * 清分中心更新清分信息
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月12日
	 * 
	 * @param allAllocateInfo
	 *            主表信息
	 * @return 更新个数
	 */
	public void updateClearMessage(AllAllocateInfo allAllocateInfo);

	/**
	 * 现金库更新接收信息
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月17日
	 * 
	 * @param allAllocateInfo
	 *            主表信息
	 * @return 更新个数
	 */
	public void updateInStoreReceiveMessage(AllAllocateInfo allAllocateInfo);

	/**
	 * 现金库更新接收信息
	 * 
	 * @author liuyaowen
	 * @version 2017年7月17日
	 * 
	 * @param allAllocateInfo
	 *            主表信息
	 * @return 更新个数
	 */
	public List<AllAllocateInfo> getAllAllocateInfoByBoxNo(String boxNo);

	/**
	 * 根据报表过滤条件获取报表内容
	 * 
	 * @author yanbingxu
	 * @version 2017-8-25
	 * @return
	 */
	public List<AllAllocateInfo> findReportListByDateFlag(AllAllocateInfo allocateInfo);

	/**
	 * 根据报表过滤条件获取线图数据
	 * 
	 * @author yanbingxu
	 * @version 2017-8-29
	 * @return
	 */
	public List<AllAllocateInfo> findBarGraphData(AllAllocateInfo allocateInfo);

	/**
	 * 根据报表过滤条件获取柱图数据
	 * 
	 * @author yanbingxu
	 * @version 2017-8-30
	 * @return
	 */
	public List<AllAllocateInfo> findLineGraphData(AllAllocateInfo allocateInfo);

	/**
	 * @author yanbingxu
	 * @date 2017-11-07
	 * 
	 *       根据交接ID获取流水信息
	 */
	public List<AllAllocateInfo> findAllocateByHandoverId(AllAllocateInfo allocateInfo);

	/**
	 * @author sg
	 * @date 2017-11-13
	 * 
	 *       通过allId更新对应主表信息（ATM库外清分入库确认接口用）
	 */
	public int updateAtm(AllAllocateInfo allocateInfo);
	
	/**
	 * 获取同步的加钞计划的boxList(箱带明细)
	 * 
	 * @author wxz
	 * @version 2017年11月15日
	 * @param addPlanId
	 * @return
	 */
	public List<Map<String, Object>> findByAddPlanId(AllAllocateInfo allAllocateInfo);

	/**
	 * 根据钞箱编号获取加钞计划Id
	 * 
	 * @author XL
	 * @version 2017年12月13日
	 * @param allAllocateInfo
	 * @return
	 */
	public List<AllAllocateInfo> getPlanIdByBoxNo(AllAllocateInfo allAllocateInfo);

	/**
	 * 查询钞箱出入库信息列表
	 * 
	 * @author XL
	 * @version 2018年1月4日
	 * @param allAllocateInfo
	 * @return
	 */
	public List<AllAllocateInfo> findAtmBoxInOutList(AllAllocateInfo allAllocateInfo);
}