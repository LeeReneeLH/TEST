/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.coffer.core.modules.sys.dao;

import java.util.List;

import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.core.modules.sys.entity.Menu;

/**
 * 菜单DAO接口
 * @author ThinkGem
 * @version 2014-05-16
 */
@MyBatisDao
public interface MenuDao extends CrudDao<Menu> {

	public List<Menu> findByParentIdsLike(Menu menu);

	public List<Menu> findByUserId(Menu menu);
	
	public int updateParentIds(Menu menu);
	
	public int updateSort(Menu menu);
	
	public List<Menu> findByUserIdAndCategory(Menu menu);
	
}
