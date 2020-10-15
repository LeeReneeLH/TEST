package com.coffer.businesses.modules.atm.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.atm.v01.entity.AtmInfoMaintain;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * ATM机信息维护DAO接口
 * 
 * @author wxz
 * @version 2017-11-3
 */
@MyBatisDao
public interface AtmInfoMaintainDao extends CrudDao<AtmInfoMaintain> {

	/**
	 * 获取不重复的ATM品牌信息列表
	 * 
	 * @author wxz
	 * @version 2017年11月3日
	 * 
	 * 
	 * @return
	 */
	public List<AtmInfoMaintain> findDistinctAtmBrandList(AtmInfoMaintain atmInfoMaintain);

	/**
	 * 获取不重复的ATM型号信息列表
	 * 
	 * @author wxz
	 * @version 2017年11月3日
	 * 
	 * 
	 * @return
	 */
	public List<AtmInfoMaintain> findDistinctAtmTypeList(AtmInfoMaintain atmInfoMaintain);
	
	/**
	 * 根据选择的品牌，获取品牌名称(单条数据)
	 * @author wxz
	 * @version 2017年11月10日
	 * 
	 */
	public AtmInfoMaintain getAtmName(AtmInfoMaintain atmInfoMaintain);
	
	/**
	 * 根据atmId(ATM机编号)，获取ATM型号信息列表
	 * @author wxz
	 * @version 2017年11月21日
	 * 
	 */
	public AtmInfoMaintain findByAtmId(AtmInfoMaintain atmInfoMaintain);
	
	/**
	 * 根据ATM机编号和柜员编号，获取ATM型号信息列表
	 * 
	 * @author XL
	 * @version 2017年12月19日
	 * @param atmInfoMaintain
	 * @return
	 */
	public List<AtmInfoMaintain> findByAtmNoAndTellerId(AtmInfoMaintain atmInfoMaintain);
	
	/**
	 * 品牌型号删除判断(查询要删除的品牌型号是否已创建ATM机)
	 * @author wxz
	 * @version 2018年1月4日
	 * @param atmInfoMaintain
	 * @return
	 */
	public List<AtmInfoMaintain> findByNo(AtmInfoMaintain atmInfoMaintain);

}