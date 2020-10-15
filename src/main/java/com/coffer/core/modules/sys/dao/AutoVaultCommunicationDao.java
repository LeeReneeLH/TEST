package com.coffer.core.modules.sys.dao;

import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.core.modules.sys.entity.AutoVaultCommunication;

/**
 * 通信DAO
 * 
 * @author SongYuanYang
 * @version 2018年5月4日
 */
@MyBatisDao
public interface AutoVaultCommunicationDao extends CrudDao<AutoVaultCommunication> {

	/**
	 * 更新通信表状态
	 * 
	 * @author WangBaozhong
	 * @version 2018年5月23日
	 * 
	 * @param autoVaultCommunication
	 *            通信信息
	 * @return 更新数量
	 */
	public int updateStatus(AutoVaultCommunication autoVaultCommunication);
}
