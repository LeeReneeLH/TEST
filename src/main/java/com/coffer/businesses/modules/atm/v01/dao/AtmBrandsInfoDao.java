package com.coffer.businesses.modules.atm.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.atm.v01.entity.AtmBrandsInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * ATM品牌参数管理DAO接口
 * 
 * @author Murphy
 * @version 2015-05-11
 */
@MyBatisDao
public interface AtmBrandsInfoDao extends CrudDao<AtmBrandsInfo> {

	/**
	 * 获取不重复的ATM品牌信息列表
	 * 
	 * @author Murphy
	 * @version 2015年5月13日
	 * 
	 * 
	 * @return
	 */
	public List<AtmBrandsInfo> findDistinctAtmBrandList(AtmBrandsInfo atmBrandsInfo);

	/**
	 * 获取不重复的ATM型号信息列表
	 * 
	 * @author Murphy
	 * @version 2015年5月13日
	 * 
	 * 
	 * @return
	 */
	public List<AtmBrandsInfo> findDistinctAtmTypeList(AtmBrandsInfo atmBrandsInfo);
	
	/**
	 * 根据选择的品牌编号，获取联动型号名称和联动型号编号
	 * @author wxz
	 * @version 2017年11月3日
	 * 
	 */
	public List<AtmBrandsInfo> findAtmTypeNameNo(AtmBrandsInfo atmBrandsInfo);

	/**
	 * 根据ATM机编号获取品牌
	 * 
	 * @author XL
	 * @version 2018年01月05日
	 * @param atmId
	 * @return
	 */
	public List<AtmBrandsInfo> findByAtmNo(String atmId);
}