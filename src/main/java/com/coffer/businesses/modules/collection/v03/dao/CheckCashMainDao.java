package com.coffer.businesses.modules.collection.v03.dao;

import java.util.List;

import com.coffer.businesses.modules.collection.v03.entity.CheckCashAmount;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearPlanInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 款箱拆箱主表DAO接口
 * 
 * @author wl
 * @version 2017-02-13
 */
@MyBatisDao
public interface CheckCashMainDao extends CrudDao<CheckCashMain> {

	/**
	 * 逻辑删除
	 * 
	 * @author wanglin
	 * @date 2017-09-02
	 * 
	 * @Description
	 * @param checkCashAmount
	 * @return
	 */
	public int logicDelete(CheckCashAmount checkCashAmount);

	/**
	 * 
	 * @author wanglin
	 * @date 2017年9月4日
	 * 
	 *       款箱拆箱当日数据取得(PDA)
	 * @param checkCashMain
	 * 
	 * @return
	 */
	public List<CheckCashMain> pdaFindList(CheckCashMain checkCashMain);

	/**
	 * 
	 * @author guojian
	 * @date 2020年3月5日
	 * 
	 *       获取上次清机时间
	 * @param getLastClearDate
	 * 
	 * @return
	 */
	public ClearPlanInfo getLastClearDate(ClearPlanInfo clearPlanInfo);

	/**
	 * 
	 * Title: getCheckCashList
	 * 
	 * @date 2020年3月11日
	 *       <p>
	 * 		Description: 查询款箱拆箱列表
	 *       </p>
	 * @author: lihe
	 * @param checkCashMain
	 * @return List<CheckCashMain> 返回类型
	 */
	List<CheckCashMain> getCheckCashList(CheckCashMain checkCashMain);

	/**
	 *
	 * Title: getByCondition
	 *
	 * @date 2020年6月5日
	 *       <p>
	 * 		Description: 条件查询
	 *       </p>
	 * @author: yinkai
	 * @param checkCashMain
	 * @return List<CheckCashMain> 返回类型
	 */
	List<CheckCashMain> getByCondition(CheckCashMain checkCashMain);
}
