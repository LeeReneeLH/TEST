package com.coffer.core.modules.sys.dao;

import java.util.List;

import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.core.modules.sys.entity.Message;

/**
 * 系统通知Dao
 * 
 * @author yanbingxu
 * @version 2017-9-27
 */
@MyBatisDao
public interface MessageDao extends CrudDao<Message> {

	/**
	 * 根据用户权限获取通知
	 * 
	 * @return
	 */
	public List<Message> findMessageByAuthority(Message message);
	
	/**
	 * 查询当前用户所有历史消息
	 * 
	 * @return
	 */
	public List<Message> findMessageHistory(Message message);

	/**
	 * 分页查询关联信息
	 * 
	 * @return
	 */
	public List<Message> findRelevanceList(Message message);

	/**
	 * 更新关联信息
	 * 
	 * @return
	 */
	public int updateRelevance(Message message);

	/**
	 * 记录已读用户
	 * 
	 * @return
	 */
	public int insertReadUser(Message message);

	/**
	 * 记录关联信息
	 * 
	 * @return
	 */
	public int insertRelevance(Message message);

	/**
	 * 记录用户权限关联表
	 * 
	 * @return
	 */
	public int insertUserAuthority(Message message);

	/**
	 * 查询用户权限关联表
	 * 
	 * @return
	 */
	public List<String> findUserAuthority(String messageId);

	/**
	 * 记录机构权限关联表
	 * 
	 * @return
	 */
	public int insertOfficeAuthority(Message message);

	/**
	 * 查询机构权限关联表
	 * 
	 * @return
	 */
	public List<String> findOfficeAuthority(String messageId);
}
