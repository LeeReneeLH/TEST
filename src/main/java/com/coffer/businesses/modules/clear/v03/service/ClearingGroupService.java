package com.coffer.businesses.modules.clear.v03.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.dao.ClearingGroupDao;
import com.coffer.businesses.modules.clear.v03.dao.ClearingGroupDetailDao;
import com.coffer.businesses.modules.clear.v03.entity.ClearingGroup;
import com.coffer.businesses.modules.clear.v03.entity.ClearingGroupDetail;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 清分组管理Service
 * 
 * @author XL
 * @version 2017-08-14
 */
@Service
@Transactional(readOnly = true)
public class ClearingGroupService extends CrudService<ClearingGroupDao, ClearingGroup> {

	@Autowired
	private ClearingGroupDetailDao clearingGroupDetailDao;
	@Autowired
	private ClearingGroupDao clearingGroupDao;

	/**
	 * 获取分组信息
	 * 
	 * @author XL
	 * @version 2017年8月14日
	 * @param id
	 * @return
	 */
	public ClearingGroup get(String id) {
		ClearingGroup clearingGroup = super.get(id);
		if (clearingGroup != null) {
			clearingGroup.setClearingGroupDetailList(
					clearingGroupDetailDao.findList(new ClearingGroupDetail(clearingGroup)));
		}
		return clearingGroup;
	}

	/**
	 * 设置可选用户
	 * 
	 * @author XL
	 * @version 2017年8月14日
	 * @param clearingGroup
	 * @return
	 */
	public ClearingGroup getSelectedUsers(ClearingGroup clearingGroup) {
		// 查询用户（现钞清分员，清分管理员类型）
		String[] userTypeArray = Global.getConfig(Constant.CLEAR_MANAGE_TYPE).split(";");
		List<User> userList = Lists.newArrayList();
		User userInfo = UserUtils.getUser();
		for (int i = 0; i < userTypeArray.length; i++) {
			userList = Collections3.union(userList,
					UserUtils.getUsersByTypeAndOffice(userTypeArray[i], userInfo.getOffice().getId()));
		}
		// 清分人员可以在多清分组中，LLF 20171114 begin
		/* 增加无id时候查询所有 wzj 2017-11-22 begin */
		if (clearingGroup.getId() != null) {
			ClearingGroupDetail clearingGroupDetails = new ClearingGroupDetail();
			clearingGroupDetails.setClearingGroupId(clearingGroup);
			// 查询明细表所有信息
			List<ClearingGroupDetail> detailList = clearingGroupDetailDao.findList(clearingGroupDetails);
			// 可选用户
			List<User> userListSelect = Lists.newArrayList();
			// 所有用户和已存在用户的差集
			for (User user : userList) {
				boolean boo = true;
				for (ClearingGroupDetail clearingGroupDetail : detailList) {
					// 用户在所有明细中是否存在
					if (user.getId().equals(clearingGroupDetail.getUser().getId())) {
						// 存在
						boo = false;
						break;
					}
				}
				// 不存在
				if (boo) {
					// 添加进可选用户集合
					userListSelect.add(user);
				}
			}
			// 可选用户
			clearingGroup.setUserListSelect(userListSelect);
		} else {
			// 没有id时候查询所有
			clearingGroup.setUserListSelect(userList);

		}
		/* end */
		// end
		// clearingGroup.setUserListSelect(userList);
		return clearingGroup;
	}

	/**
	 * 获取清分组列表
	 * 
	 * @author XL
	 * @version 2017年8月14日
	 * @param clearingGroup
	 * @return
	 */
	public List<ClearingGroup> findList(ClearingGroup clearingGroup) {
		clearingGroup.getSqlMap().put("dsf", dataScopeFilter(clearingGroup.getCurrentUser(), "o", null));
		return super.findList(clearingGroup);
	}

	/**
	 * 获取所有分组
	 * 
	 * @author XL
	 * @version 2017年8月14日
	 * @return
	 */
	public List<ClearingGroup> findAllList() {
		return clearingGroupDao.findAllList(new ClearingGroup());
	}

	/**
	 * 获取清分组列表
	 * 
	 * @author XL
	 * @version 2017年8月14日
	 * @param page
	 * @param clearingGroup
	 * @return
	 */
	public Page<ClearingGroup> findPage(Page<ClearingGroup> page, ClearingGroup clearingGroup) {
		// 查询条件： 开始时间
		if (clearingGroup.getCreateTimeStart() != null) {
			clearingGroup.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(clearingGroup.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (clearingGroup.getCreateTimeEnd() != null) {
			clearingGroup.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(clearingGroup.getCreateTimeEnd())));
		}
		/* 增加数据穿透 wzj 2017-11-24 begin */
		/* end */
		clearingGroup.getSqlMap().put("dsf", dataScopeFilter(clearingGroup.getCurrentUser(), "o", null));
		return super.findPage(page, clearingGroup);
	}

	/**
	 * 设置清分组人数
	 * 
	 * @author XL
	 * @version 2017年8月14日
	 * @param clearingGrouplist
	 * @return
	 */
	public List<ClearingGroup> setGroupNumber(List<ClearingGroup> clearingGrouplist) {
		// 设置每组人数
		for (ClearingGroup clearingGroup : clearingGrouplist) {
			clearingGroup.setNumber((long) get(clearingGroup.getId()).getClearingGroupDetailList().size());
		}
		return clearingGrouplist;
	}

	/**
	 * 保存清分组
	 * 
	 * @author XL
	 * @version 2017年8月14日
	 * @param clearingGroup
	 * @return
	 */
	@Transactional(readOnly = false)
	public void save(ClearingGroup clearingGroup) {
		// 判断更新或插入
		if (StringUtils.isNotBlank(clearingGroup.getId())) {
			// 更新者姓名
			clearingGroup.setUpdateName(UserUtils.getUser().getName());
		} else {
			// 插入，设置启用状态
			clearingGroup.setGroupStatus(ClearConstant.ClGroupStatus.START);
		}
		// 获取当前登录人
		// 登陆用户
		User userInfo = UserUtils.getUser();
		clearingGroup.setOffice(userInfo.getOffice());
		super.save(clearingGroup);
		// 物理删除原明细
		clearingGroupDetailDao.deletePhysical(new ClearingGroupDetail(clearingGroup));
		// 获取新提交的用户
		List<User> userList = clearingGroup.getUserListForm();
		// 批量增加
		for (User user : userList) {
			ClearingGroupDetail clearingGroupDetail = new ClearingGroupDetail();
			// 设置清分组ID
			clearingGroupDetail.setClearingGroupId(clearingGroup);
			// 设置用户
			clearingGroupDetail.setUser(user);
			// 设置用户名
			clearingGroupDetail.setUserName(UserUtils.get(user.getId()).getName());
			// 插入信息
			clearingGroupDetail.preInsert();
			clearingGroupDetailDao.insert(clearingGroupDetail);
		}
	}

	/**
	 * 删除
	 * 
	 * @author XL
	 * @version 2017年8月14日
	 * @param clearingGroup
	 * @return
	 */
	@Transactional(readOnly = false)
	public void delete(ClearingGroup clearingGroup) {
		// 逻辑删除主表信息
		super.delete(clearingGroup);
	}

	/**
	 * 更新，启用停用
	 * 
	 * @author XL
	 * @version 2017年8月14日
	 * @param clearingGroup
	 * @return
	 */
	@Transactional(readOnly = false)
	public void update(ClearingGroup clearingGroup) {
		clearingGroup.preUpdate();
		// 设置更新人姓名
		clearingGroup.setUpdateName(UserUtils.getUser().getName());
		// 获取当前登录人
		// 登陆用户
		User userInfo = UserUtils.getUser();
		clearingGroup.setOffice(userInfo.getOffice());
		clearingGroupDao.update(clearingGroup);
	}

	/**
	 * 检查组名,编号一致性
	 * 
	 * @author XL
	 * @version 2017年8月14日
	 * @param name
	 * @return
	 */
	public ClearingGroup findByNoAndName(ClearingGroup clearingGroup) {
		return clearingGroupDao.findByNoAndName(clearingGroup);
	}
	
	/**
	 * 通过清分组号获取清分组详细信息
	 * 
	 * @author qph	
	 * @version 2017年8月24日
	 * @param name
	 * @return
	 */
	public List<ClearingGroupDetail> getByGroupId(String groupId) {
		return clearingGroupDetailDao.getByGroupId(groupId);
	}
	/**
	 * 通过id获取单条清分组详细信息
	 * 
	 * @author qph	
	 * @version 2017年8月25日
	 * @param id
	 * @return
	 */
	public ClearingGroupDetail getClGroupDetailById(String id){
		return clearingGroupDetailDao.get(id);
	}

	/**
	 * 根据清分人员id查询明细
	 * 
	 * @author XL
	 * @version 2017年9月19日
	 * @param id
	 * @param groupType
	 * @param delFlag
	 * @return
	 */
	public ClearingGroupDetail getClGroupDetailByUser(String clearGroupId, String id, String groupType,
			String delFlag) {
		return clearingGroupDetailDao.getClGroupDetailByUser(clearGroupId, id, groupType, delFlag);
	}


}