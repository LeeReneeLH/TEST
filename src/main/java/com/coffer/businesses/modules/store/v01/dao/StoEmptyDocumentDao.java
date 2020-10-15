/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.coffer.businesses.modules.store.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.store.v01.entity.StoEmptyDocument;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 重空管理DAO接口
 * @author LLF
 * @version 2015-12-11
 */
@MyBatisDao
public interface StoEmptyDocumentDao extends CrudDao<StoEmptyDocument> {

	/**
	 * 
	 * @author LLF
	 * @version 2015年12月15日
	 * 
	 *          验证登记重空编号是否存在
	 * @param stoEmptyDocument
	 * @return
	 */
	public List<StoEmptyDocument> checkEmptyDocument(StoEmptyDocument stoEmptyDocument);

	/**
	 * 
	 * @author LLF
	 * @version 2015年12月17日
	 * 
	 *  查询当前重空不为零的区间记录
	 * @param stoEmptyDocument
	 * @return
	 */
	public List<StoEmptyDocument> findEmptyDocument(StoEmptyDocument stoEmptyDocument);
	
}