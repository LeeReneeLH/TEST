package com.coffer.businesses.modules.doorOrder.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 缴存全景Dao
 * 
 * @author lihe
 * @version 2020年5月26日
 */
@MyBatisDao
public interface DepositPanoramaDao extends CrudDao<DoorOrderInfo> {

	/**
	 * 获取缴存全景列表
	 */
	List<DoorOrderInfo> findList(DoorOrderInfo depositPanorama);
	/**
	 * 获取封包列表
	 */
	List<DoorOrderInfo> packageList(DoorOrderInfo depositPanorama);
}
