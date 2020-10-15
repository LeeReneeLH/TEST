package com.coffer.businesses.modules.clear.v03.dao;

import java.util.List;

import com.coffer.businesses.modules.clear.v03.entity.ClInMain;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 商行交款DAO接口
 * 
 * @author wanglin
 * @version 2015-05-11
 */
@MyBatisDao
public interface ClInMainDao extends CrudDao<ClInMain> {

	/**
	 * 状态 更新(1：登记，2：冲正)
	 * 
	 * @author wanglin
	 * @version 2017年7月11日
	 * @param 商行交款(bankPayInfo)
	 * @return
	 */
	public void updateStatus(ClInMain bankPayInfo);

	/**
	 * 
	 * 
	 * @author
	 * @version 2017年7月11日
	 * @param
	 * @return
	 */
	public List<ClInMain> getDetailList(ClInMain bankPayInfo);

}