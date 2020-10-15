package com.coffer.businesses.modules.clear.v03.dao;

import java.util.List;

import com.coffer.businesses.modules.clear.v03.entity.EmpWorkStatistics;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 员工工作量统计DAO接口
 * 
 * @author XL
 * @version 2017-09-04
 */
@MyBatisDao
public interface EmpWorkStatisticsDao extends CrudDao<EmpWorkStatistics> {

	/**
	 * 查询工作量页面显示集合
	 * 
	 * @author XL
	 * @version 2017-09-04
	 * @param empWorkStatistics
	 * @return
	 */
	public List<EmpWorkStatistics> findListView(EmpWorkStatistics empWorkStatistics);

	/**
	 * 查询工作量数据集合
	 * 
	 * @author XL
	 * @version 2017-09-04
	 * @param empWorkStatistics
	 * @return
	 */
	public List<EmpWorkStatistics> findListData(EmpWorkStatistics empWorkStatistics);

	/**
	 * 根据工位类型统计数量
	 * 
	 * @author wxz
	 * @version 2017-10-17
	 */
	public List<EmpWorkStatistics> findWorkingType(EmpWorkStatistics empWorkStatistics);
	
	/**
	 * 根据人员统计数量
	 * 
	 * @author wxz
	 * @version 2017-10-17
	 */
	public List<EmpWorkStatistics> findPeople(EmpWorkStatistics empWorkStatistics);

	/**
	 * 按员工名称进行机械清分手工清分复点的面值数量显示
	 * 
	 * @author wzj
	 * @version 2017-11-22
	 */
	public List<EmpWorkStatistics> findListAllEmp(EmpWorkStatistics empWorkStatistics);

	/**
	 * 查询人员统计数量
	 * 
	 * @author wzj
	 * @version 2017-12-4
	 */
	public List<EmpWorkStatistics> findPeopleList(EmpWorkStatistics empWorkStatistics);

	/**
	 * 查询机械清分和手工清分统计数量
	 * 
	 * @author wzj
	 * @version 2017-12-4
	 */
	public List<EmpWorkStatistics> findWorkClearingList(EmpWorkStatistics empWorkStatistics);

	/**
	 * 查询复点统计数量
	 * 
	 * @author wzj
	 * @version 2017-12-4
	 */
	public List<EmpWorkStatistics> findWorkPointList(EmpWorkStatistics empWorkStatistics);
}