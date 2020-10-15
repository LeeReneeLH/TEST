package com.coffer.businesses.modules.doorOrder.v01.dao;

import com.coffer.businesses.modules.doorOrder.v01.entity.DoorOrderExceptionDetail;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 存款异常明细DAO接口
 * @author zxk
 * @version 2019-11-12
 */
@MyBatisDao
public interface DoorOrderExceptionDetailDao extends CrudDao<DoorOrderExceptionDetail>{

	/**
	 * 
	 * Title: deleteById
	 * <p>
	 * Description: 根据id删除存款异常明细信息
	 * </p>
	 * 
	 * @author: zxk
	 * @param id
	 * @return int 返回类型
	 */
	int deleteById(DoorOrderExceptionDetail doorOrderExceptionDetail);
}
