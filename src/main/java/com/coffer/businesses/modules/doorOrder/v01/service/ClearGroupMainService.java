package com.coffer.businesses.modules.doorOrder.v01.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.v01.dao.ClearGroupDoorInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.ClearGroupMainDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.ClearGroupUserInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearGroupDoorInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearGroupMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearGroupUserInfo;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 清机组Service
 * 
 * @author ZXK
 * @version 2019-07-24
 */
@Service
public class ClearGroupMainService extends CrudService<ClearGroupMainDao, ClearGroupMain> {

	@Autowired
	private ClearGroupUserInfoDao clearGroupUserInfoDao;
	@Autowired
	private ClearGroupDoorInfoDao clearGroupDoorInfoDao;
	@Autowired
	private ClearGroupMainDao clearGroupMainDao;

	public ClearGroupMain get(String id) {
		return super.get(id);
	}

	/**
	 * 获取清机组信息列表
	 * 
	 * @author ZXK
	 * @version 2019年7月24日
	 * @param page
	 * @param clearGroupMain
	 * @return
	 */
	public Page<ClearGroupMain> findPage(Page<ClearGroupMain> page, ClearGroupMain clearGroupMain) {
		// 查询条件： 开始时间
				if (clearGroupMain.getCreateTimeStart() != null) {
					clearGroupMain.setSearchDateStart(
							DateUtils.foramtSearchDate(DateUtils.getDateStart(clearGroupMain.getCreateTimeStart())));
				}
				// 查询条件： 结束时间
				if (clearGroupMain.getCreateTimeEnd() != null) {
					clearGroupMain.setSearchDateEnd(
							DateUtils.foramtSearchDate(DateUtils.getDateEnd(clearGroupMain.getCreateTimeEnd())));
				}
		clearGroupMain.getSqlMap().put("dsf", dataScopeFilter(clearGroupMain.getCurrentUser(), "o", null));
		return super.findPage(page, clearGroupMain);
	}

	/**
	 * 保存清机组信息(添加与修改)
	 * 
	 * @author ZXK
	 * @version 2019年7月25日
	 * @param page
	 * @param clearGroupMain
	 * @return
	 */
	@Transactional(readOnly = false)
	public void save(ClearGroupMain clearGroupMain) {
		if (!StringUtils.isNotBlank(clearGroupMain.getClearGroupId())) {
			clearGroupMain.preInsert();
			//通过登录人获取清分中心
			clearGroupMain.setClearCenterId(UserUtils.getUser().getOffice().getId());
			clearGroupMain.setClearCenterName(UserUtils.getUser().getOffice().getName());
			dao.insert(clearGroupMain);

		} else {
			clearGroupMain.preUpdate();
			dao.update(clearGroupMain);
			// 删除清机门店明细表相关记录
			clearGroupDoorInfoDao.deleteByClearGroupId(clearGroupMain.getClearGroupId());
			// 删除清机人员明细表相关记录
			clearGroupUserInfoDao.deleteByClearGroupId(clearGroupMain.getClearGroupId());
		}
		List<Office> doorList = clearGroupMain.getDoorList();
		List<StoEscortInfo> userList = clearGroupMain.getUserList();
		if (!Collections3.isEmpty(doorList)) {
			for (Office office : doorList) {
				ClearGroupDoorInfo clearGroupDoorInfo = new ClearGroupDoorInfo();
				clearGroupDoorInfo.setId(IdGen.uuid());
				clearGroupDoorInfo.setClearGroupId(clearGroupMain.getId());
				clearGroupDoorInfo.setDoorId(office.getId());
				// 门店名称
				clearGroupDoorInfo.setDoorName(StoreCommonUtils.getOfficeById(office.getId()).getName());
				clearGroupDoorInfoDao.insert(clearGroupDoorInfo);
			}
		}
		if (!Collections3.isEmpty(userList)) {
			for (StoEscortInfo user : userList) {
				ClearGroupUserInfo clearGroupUserInfo = new ClearGroupUserInfo();
				clearGroupUserInfo.setId(IdGen.uuid());
				clearGroupUserInfo.setClearGroupId(clearGroupMain.getId());
				clearGroupUserInfo.setUserId(user.getId());
				clearGroupUserInfo.setUserName(StoreCommonUtils.getEscortById(user.getId()).getEscortName());
				clearGroupUserInfoDao.insert(clearGroupUserInfo);
			}
		}

	}

	/**
	 * 删除清机组信息(及其相关字表信息)
	 * 
	 * @author ZXK
	 * @version 2019年7月26日
	 * @param page
	 * @param clearGroupMain
	 * @return
	 */
	@Transactional(readOnly = false)
	public void delete(ClearGroupMain clearGroupMain) {
		dao.delete(clearGroupMain);
		// 删除清机门店明细表相关记录
		clearGroupDoorInfoDao.deleteByClearGroupId(clearGroupMain.getClearGroupId());
		// 删除清机人员明细表相关记录
		clearGroupUserInfoDao.deleteByClearGroupId(clearGroupMain.getClearGroupId());

	}

	/**
	 * 筛选出该机构下没有被分配的门店
	 * 
	 * @author ZXK
	 * @version 2019年7月26日
	 * @param allDoors
	 * @return
	 */
	public List<Office> checkDoorList(List<Office> allDoors, ClearGroupMain clearGroupMain) {
		List<Office> list = Lists.newArrayList();
		List<Office> doorList = clearGroupMain.getDoorList();
		if (!Collections3.isEmpty(doorList)) {
			for (Office door : doorList) {
				door.setName(StoreCommonUtils.getOfficeById(door.getId()).getName());
				list.add(door);
			}
		}
		for (Office office : allDoors) {
			ClearGroupDoorInfo cl = new ClearGroupDoorInfo();
			cl.setDoorId(office.getId());
			cl.setClearGroupId(clearGroupMain.getClearGroupId());
			cl.setDelFlag(Constant.deleteFlag.Valid);
			if (Collections3.isEmpty(clearGroupDoorInfoDao.checkDoorsList(cl))) {
				list.add(office);
			}
		}
		return list;
	}

	/**
	 * 根据清机组名称查询列表
	 * 
	 * @author ZXK
	 * @version 2019年7月31日
	 * @param clearGroupMain
	 * @return
	 */
	public List<ClearGroupMain> findListByName(ClearGroupMain clearGroupMain) {
		return clearGroupMainDao.findListByName(clearGroupMain);
	}
	
	
	/**
	 * 根据清机组编号获取人员列表
	 *
	 * @author XL 
	 * @version 2019年8月12日 
	 * @param groupId
	 * @return
	 */
	public List<ClearGroupUserInfo> findUserByGroupId(String groupId){
		ClearGroupUserInfo clearGroupUserInfo=new ClearGroupUserInfo();
		clearGroupUserInfo.setClearGroupId(groupId);
		return clearGroupUserInfoDao.findList(clearGroupUserInfo);
	}
}
