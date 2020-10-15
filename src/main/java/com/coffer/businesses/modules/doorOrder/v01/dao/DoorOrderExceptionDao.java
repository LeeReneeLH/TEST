package com.coffer.businesses.modules.doorOrder.v01.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorOrderException;
import com.coffer.businesses.modules.doorOrder.v01.entity.OfficeAmount;

/**
 * 存款异常信息DAO接口
 * @author zxk
 * @version 2019-11-11
 */
@MyBatisDao
public interface DoorOrderExceptionDao extends CrudDao<DoorOrderException> {
	
	/**
	 * 门店人员列表
	 * @author gzd
	 * @version 2019年11月15日
	 * @param door_id
	 * @return
	 */
	public List<User> getPerson(@Param("doorId")String doorId);
	
	/**
	 * 存款备注列表（七位码）
	 * @author gzd
	 * @version 2019年12月12日
	 * @param door_id
	 * @return
	 */
	public List<Office> getRemarks(@Param("doorId")String doorId);

	/**
	 * 
	 * Title: getDoorExceptionCount
	 * <p>
	 * Description: 查询存款异常/已处理 总数
	 * </p>
	 * @author: ZXK
	 * @param officeAmount
	 * @return OfficeAmount 返回类型
	 */
	OfficeAmount getDoorExceptionCount(OfficeAmount officeAmount);
	
	}