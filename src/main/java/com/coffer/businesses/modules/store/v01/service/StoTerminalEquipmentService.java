package com.coffer.businesses.modules.store.v01.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.dao.StoTerminalEquipmentDao;
import com.coffer.businesses.modules.store.v01.entity.StoTerminalEquipment;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.StringUtils;

/**
 * 终端设备管理Service
 * @author yuxixuan
 * @version 2015-12-11
 */
@Service
@Transactional(readOnly = true)
public class StoTerminalEquipmentService extends CrudService<StoTerminalEquipmentDao, StoTerminalEquipment> {

	public StoTerminalEquipment getTe(StoTerminalEquipment stoTerminalEquipment) {
		return dao.getTe(stoTerminalEquipment);
	}
	
	public List<StoTerminalEquipment> findList(StoTerminalEquipment stoTerminalEquipment) {
		return super.findList(stoTerminalEquipment);
	}
	
	public Page<StoTerminalEquipment> findPage(Page<StoTerminalEquipment> page, StoTerminalEquipment stoTerminalEquipment) {
		return super.findPage(page, stoTerminalEquipment);
	}
	
	@Transactional(readOnly = false)
	public void save(StoTerminalEquipment stoTerminalEquipment) {
		super.save(stoTerminalEquipment);
	}
	
	/**
	 * 变更设备状态
	 * @author yuxixuan
	 * @version 2015年12月29日
	 * @param teId
	 */
	@Transactional(readOnly = false)
	public void changeTeStatus(String teId) {

		// 验证ID是否为空
		if (StringUtils.isBlank(teId)) {
			throw new BusinessException("message.E1003");
		}
		// 取得设备信息
		StoTerminalEquipment param = new StoTerminalEquipment();
		param.setId(teId);
		// 不显示拨号ID和拨号密码
		param.setDisplayDialFg(false);
		StoTerminalEquipment stoTerminalEquipment = getTe(param);
		if(stoTerminalEquipment == null || StringUtils.isBlank(stoTerminalEquipment.getId())){
			throw new BusinessException("message.E1004");
		}
		// 变更设备状态
		if(StoreConstant.TeStatus.Valid.equals(stoTerminalEquipment.getTeStatus())){
			stoTerminalEquipment.setTeStatus(StoreConstant.TeStatus.Invalid);
		}else if(StoreConstant.TeStatus.Invalid.equals(stoTerminalEquipment.getTeStatus())){
			stoTerminalEquipment.setTeStatus(StoreConstant.TeStatus.Valid);
		}
		stoTerminalEquipment.preUpdate();
		dao.update(stoTerminalEquipment);
	}
}