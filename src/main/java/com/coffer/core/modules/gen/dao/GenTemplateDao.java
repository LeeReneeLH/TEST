/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.coffer.core.modules.gen.dao;

import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.core.modules.gen.entity.GenTemplate;

/**
 * 代码模板DAO接口
 * @author ThinkGem
 * @version 2013-10-15
 */
@MyBatisDao
public interface GenTemplateDao extends CrudDao<GenTemplate> {
	
}
