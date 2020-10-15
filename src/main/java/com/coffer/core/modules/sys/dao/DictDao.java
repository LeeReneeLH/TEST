/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.coffer.core.modules.sys.dao;

import java.util.List;

import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.core.modules.sys.entity.Dict;

/**
 * 字典DAO接口
 * @author ThinkGem
 * @version 2014-05-16
 */
@MyBatisDao
public interface DictDao extends CrudDao<Dict> {

	public List<String> findTypeList(Dict dict);
	
	/**
	 * 
	 * Title: checkDictValue
	 * <p>Description: 查询该类型的记录</p>
	 * @author:     wanghan
	 * @param dict
	 * @return 
	 * int    返回类型
	 */
	public int  checkDictValue(Dict dict);

}
