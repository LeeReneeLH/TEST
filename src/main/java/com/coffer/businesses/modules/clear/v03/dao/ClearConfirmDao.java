package com.coffer.businesses.modules.clear.v03.dao;
import com.coffer.businesses.modules.clear.v03.entity.ClearConfirm;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 清分接收DAO接口
 * @author wl
 * @date 2017-02-13
 */
@MyBatisDao
public interface ClearConfirmDao extends CrudDao<ClearConfirm> {
	

	/**
	 * 状态的更新
	 * @author WL
	 * @date 2017年12月01日
	 * @param 
	 * @return 
	 */
	public int updateStatus(ClearConfirm taskConfirm);
}

