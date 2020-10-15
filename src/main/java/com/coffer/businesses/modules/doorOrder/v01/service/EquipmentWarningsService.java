package com.coffer.businesses.modules.doorOrder.v01.service;
import java.util.List;

import org.springframework.stereotype.Service;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.doorOrder.v01.dao.EquipmentWarningsDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentWarnings;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 机具报警Service
 * 
 * @author zhaohaoran	
 * @version 2019-07-24
 */
@Service
public class EquipmentWarningsService extends CrudService<EquipmentWarningsDao, EquipmentWarnings>{		
	
	/**
	 * 查询分页数据
	 * @param page 分页对象
	 * @param entity
	 * @return
	 */
	public Page<EquipmentWarnings> findPage(Page<EquipmentWarnings> page, EquipmentWarnings equipmentWarnings) {
				// 查询条件：开始时间
				if (equipmentWarnings.getCreateTimeStart() != null) {
					equipmentWarnings.setSearchTimeStart(
							DateUtils.formatDate(
									DateUtils.getDateStart(equipmentWarnings.getCreateTimeStart()), 
									AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
				}
				// 查询条件：结束时间
				if (equipmentWarnings.getCreateTimeEnd() != null) {
					equipmentWarnings.setSearchTimeEnd(DateUtils.formatDate(
									DateUtils.getDateEnd(equipmentWarnings.getCreateTimeEnd()), 
									AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
				}
			
			//	equipmentWarnings.getSqlMap().put("dsf", dataScopeFilter(equipmentWarnings.getCurrentUser(), "o1", null));
			/*  equipmentWarnings.getSqlMap().put("dsf",
						"AND o1.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getParentId() + "%'");*/
				return super.findPage(page, equipmentWarnings);
	}
	
	public List<EquipmentWarnings> findWarningList(EquipmentWarnings equipmentWarnings) {
		Office office = new Office();
		if(UserUtils.getUser().getId() != null) {
			office = UserUtils.getUser().getOffice();
		} else {
			office = equipmentWarnings.getOffice();
		}
		// 当前登陆人机构类型
		if (office.getType().equals(Constant.OfficeType.CLEAR_CENTER)) {
			// 按清分中心过滤
			equipmentWarnings.getSqlMap().put( "dsf", "AND o.parent_ids LIKE '%" + office.getParentId() + "%'");
		} else if(office.getType().equals(Constant.OfficeType.STORE)) {
			// 按门店过滤
			equipmentWarnings.getSqlMap().put( "dsf", "AND o.id = '" + office.getId() + "'");
		} else {
			// 登录机构是非清分中心(包括商户)
			equipmentWarnings.getSqlMap().put("dsf", "AND (o.parent_ids LIKE '%" + office.getId() + "%' OR o.ID = "
					+ office.getId() + ")");
		}
		return dao.findWarningList(equipmentWarnings);
	}
	
	/**
	 * 
	 * Title: findDoorList
	 * <p>
	 * Description: 查询清分中心上级人行下所有门店
	 * </p>
	 * 
	 * @version 2019年7月4日
	 * @author: zhr
	 * @return List<Office> 返回类型
	 */
	public List<Office> findDoorList(Office office,String otype) {
		return dao.findDoorList(office,otype);
	}	
}
