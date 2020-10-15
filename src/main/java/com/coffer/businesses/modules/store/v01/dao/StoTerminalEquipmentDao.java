package com.coffer.businesses.modules.store.v01.dao;

import com.coffer.businesses.modules.store.v01.entity.StoTerminalEquipment;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 终端设备管理DAO接口
 * @author yuxixuan
 * @version 2015-12-11
 */
@MyBatisDao
public interface StoTerminalEquipmentDao extends CrudDao<StoTerminalEquipment> {
	StoTerminalEquipment getTe(StoTerminalEquipment stoTerminalEquipment);
}