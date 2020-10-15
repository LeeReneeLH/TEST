package com.coffer.businesses.modules.allocation.v01.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.dao.AllAllocateInfoDao;
import com.coffer.businesses.modules.allocation.v01.dao.AllAllocateItemDao;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateItem;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.ChangeStoreEntity;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.entity.StoStoresInfo;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 现金调拨功能Service
 * 
 * @author SongYuanYang
 * @version 2017-07-06
 */
@Service
@Transactional(readOnly = true)
public class CashBetweenService extends CrudService<AllAllocateInfoDao, AllAllocateInfo> {

	@Autowired
	private AllAllocateItemDao allAllocateItemDao;

	@Override
	public AllAllocateInfo get(String allId) {
		return super.get(allId);
	}

	/**
	 * 计算物品总价值
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月7日
	 * 
	 * @param allAllocateInfo
	 *            主表信息
	 */
	public void computeGoodsAmount(AllAllocateInfo allAllocateInfo) {
		// 初始化
		BigDecimal registeAmount = new BigDecimal(0.0d);
		BigDecimal confirmAmount = new BigDecimal(0.0d);
		
		// 计算登记物品及确认物品总价值
		for (AllAllocateItem item : allAllocateInfo.getAllAllocateItemList()) {
			if (AllocationConstant.BetweenConfirmFlag.UNCONFIRMED.equals(item.getConfirmFlag())) {
				registeAmount = registeAmount.add(item.getMoneyAmount());
			} else if (AllocationConstant.BetweenConfirmFlag.CONFIRMED.equals(item.getConfirmFlag())) {
				confirmAmount = confirmAmount.add(item.getMoneyAmount());
			}
		}
		
		// 设置登记金额
		allAllocateInfo.setRegisterAmount(registeAmount);
		// 设置确认金额
		allAllocateInfo.setConfirmAmount(confirmAmount);
	}

	/**
	 * 现金库保存登记信息
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月7日
	 * 
	 * @param allAllocateInfo
	 *            主表信息
	 */
	@Transactional(readOnly = false)
	public void saveRegisterInfo(AllAllocateInfo allAllocateInfo) {
		boolean isNewData = false;
		if (get(allAllocateInfo) == null) {
			allAllocateInfo.setAllId(null);
			isNewData = true;
		}
		
		ChangeStoreEntity changeStoreItem;
		if (isNewData) {
			Office rOffice = SysCommonUtils.findOfficeById(allAllocateInfo.getrOffice().getId());
			// 生成流水单号
			allAllocateInfo.setAllId(BusinessUtils.getNewBusinessNo(allAllocateInfo.getBusinessType(),
					rOffice));
		} else {
			// 检查数据一致性
			checkVersion(allAllocateInfo);
			// 取得当前数据库数据
			AllAllocateInfo currentData = super.get(allAllocateInfo.getAllId());
			List<ChangeStoreEntity> changeCuList = Lists.newArrayList();
			for (AllAllocateItem item : currentData.getAllAllocateItemList()) {
				changeStoreItem = new ChangeStoreEntity(); 
				// 设置回滚物品id和数量
				changeStoreItem.setGoodsId(item.getGoodsId());
				changeStoreItem.setNum(item.getMoneyNumber());
				changeCuList.add(changeStoreItem);
			}
			User user = new User();
			user.setId(allAllocateInfo.getCurrentUser().getId());
			user.setName(allAllocateInfo.getCurrentUser().getName());
			user.setOffice(SysCommonUtils.findOfficeById(currentData.getrOffice().getId())); 
			// 回滚预剩余库存及实际库存
			StoreCommonUtils.changeStoreAndSurplusStores(changeCuList, currentData.getrOffice().getId(),
					currentData.getAllId(), user);
			// 修改时删除明细表相关明细
			AllAllocateItem allAllocateItem = new AllAllocateItem();
			allAllocateItem.setAllocationInfo(allAllocateInfo);
			allAllocateItemDao.delete(allAllocateItem);
		}
		
		// 物品改变list
		List<ChangeStoreEntity> changeList = Lists.newArrayList();
		for (AllAllocateItem item : allAllocateInfo.getAllAllocateItemList()) {
			changeStoreItem = new ChangeStoreEntity();
			// 根据物品id取得现金库物品库存信息
			StoStoresInfo storeInfo = StoreCommonUtils.getStoStoresInfoByGoodsId(item.getGoodsId(),
					allAllocateInfo.getrOffice().getId());
			// 物品信息不存在弹出提醒消息
			if (storeInfo == null) {
				String strGoodsName = StoreCommonUtils.getGoodsNameById(item.getGoodsId());
				throw new BusinessException("message.E2051", null, new String[] { strGoodsName });
			}
			// 预剩余库存数量为空或为零时弹出提醒消息
			if (storeInfo.getSurplusStoNum() == null || storeInfo.getSurplusStoNum() == 0L) {
				throw new BusinessException("message.E2052", null, new String[] { storeInfo.getGoodsName() });
			}
			// 登记物品数量大于库存内物品数量弹出提醒消息
			long lGoodsNum = storeInfo.getSurplusStoNum();
			if (item.getMoneyNumber() > lGoodsNum) {
				throw new BusinessException("message.E2051", null,
						new String[] { StoreCommonUtils.getGoodsNameById(item.getGoodsId()) });
			}
			// 设置物品改变信息
			changeStoreItem.setGoodsId(item.getGoodsId());

			/*
			 * // 判断是否是修改数据 if (allAllocateInfo.getMoneyBeforeMap() != null) {
			 * // 修改情况下与之前物品做对比进行减库存 Map<String, Long> itemMap =
			 * allAllocateInfo.getMoneyBeforeMap(); for (String key :
			 * itemMap.keySet()) { // 存在物品与之前物品做对比减库存 if
			 * (key.equals(item.getGoodsId())) {
			 * changeStoreItem.setNum(itemMap.get(key) - item.getMoneyNumber());
			 * break; } // 不存在物品直接减库存
			 * changeStoreItem.setNum(-item.getMoneyNumber()); } } else { //
			 * 非修改时直接减库存 changeStoreItem.setNum(-item.getMoneyNumber()); }
			 */

			changeStoreItem.setNum(-item.getMoneyNumber());

			changeList.add(changeStoreItem);
			// 设置明细表信息
			item.setAllItemsId(IdGen.uuid());
			// 设置流水号与主表流水号一致
			item.setAllocationInfo(allAllocateInfo);
			// 插入明细表数据
			allAllocateItemDao.insert(item);
		}
		
		// 判断是否为新登记业务
		if (isNewData) {
			
			allAllocateInfo.setStatus(AllocationConstant.BetweenStatus.TO_ACCEPT_STATUS);
			// 插入登记机构信息
			allAllocateInfo.setrOffice(SysCommonUtils.findOfficeById(allAllocateInfo.getrOffice().getId()));
			// 插入接收机构信息
			allAllocateInfo.setaOffice(SysCommonUtils.findOfficeById(allAllocateInfo.getaOffice().getId()));
			allAllocateInfo.preInsert();
			dao.insert(allAllocateInfo);
		} else {
			
			// 更新主表数据
			allAllocateInfo.preUpdate();
			dao.update(allAllocateInfo);
		}

		
		User user = new User();
		user.setId(allAllocateInfo.getCurrentUser().getId());
		user.setName(allAllocateInfo.getCurrentUser().getName());
		user.setOffice(allAllocateInfo.getrOffice());
		// 修改预剩余库存及实际库存
		StoreCommonUtils.changeStoreAndSurplusStores(changeList, allAllocateInfo.getrOffice().getId(),
				allAllocateInfo.getAllId(), user);
	}

	/**
	 * 清分中心保存确认信息
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月11日
	 * 
	 * @param allAllocateInfo
	 *            主表信息
	 */
	@Transactional(readOnly = false)
	public void saveConfirmInfo(AllAllocateInfo allAllocateInfo) {
		// 检查数据一致性
		checkVersion(allAllocateInfo);
		// 保存确认人信息
		allAllocateInfo.setConfirmName(allAllocateInfo.getCurrentUser().getName());
		// 保存确认时间
		allAllocateInfo.setConfirmDate(new Date());
		if (AllocationConstant.BusinessType.Between_Clear.equals(allAllocateInfo.getBusinessType())) {
			// 业务为库间清分时状态更改为清分中
			allAllocateInfo.setStatus(AllocationConstant.BetweenStatus.TO_IN_SORTING);
		} else if (AllocationConstant.BusinessType.Between_ATM_Add.equals(allAllocateInfo.getBusinessType())) {
			// 业务为库间配钞时状态更改为完成
			allAllocateInfo.setStatus(AllocationConstant.BetweenStatus.FINISH_STATUS);
		}
		allAllocateInfo.preUpdate();
		// 更新接收确认信息
		dao.updateConfirmMessage(allAllocateInfo);
	}

	/**
	 * 清分中心保存清分信息
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月12日
	 * 
	 * @param allAllocateInfo
	 *            主表信息
	 */
	@Transactional(readOnly = false)
	public void saveClearInfo(AllAllocateInfo allAllocateInfo) {
		// 状态更改为待入库
		allAllocateInfo.setStatus(AllocationConstant.BetweenStatus.TO_IN_STORE_STATUS);
		// 设置清分信息
		allAllocateInfo.setClearInRegisterBy(allAllocateInfo.getCurrentUser());
		allAllocateInfo.setClearInRegisterName(allAllocateInfo.getCurrentUser().getName());
		allAllocateInfo.setClearInRegisterDate(new Date());
		// 检查数据一致性
		checkVersion(allAllocateInfo);
		
		allAllocateInfo.preUpdate();
		// 更新清分信息
		dao.updateClearMessage(allAllocateInfo);
		// 删除登记过的明细信息
		AllAllocateItem allAllocateItem = new AllAllocateItem();
		allAllocateItem.setAllocationInfo(allAllocateInfo);
		allAllocateItemDao.deleteByConfirmed(allAllocateItem);
		
		// 取得明细信息
		List<AllAllocateItem> itemList = allAllocateInfo.getAllAllocateItemList();
		for (AllAllocateItem item : itemList) {
			// 保存清分中心登记的物品明细
			if (AllocationConstant.BetweenConfirmFlag.CONFIRMED.equals(item.getConfirmFlag())) {
				// 设置流水单号
				item.setAllocationInfo(allAllocateInfo);
				item.setAllItemsId(IdGen.uuid());
				allAllocateItemDao.insert(item);
			}
		}
	}

	/**
	 * 清分中心保存清分信息
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月24日
	 * 
	 * @param allAllocateInfo
	 *            主表信息
	 */
	@Transactional(readOnly = false)
	public void saveClearRegisterInfo(AllAllocateInfo allAllocateInfo) {
		if (StringUtils.isBlank(allAllocateInfo.getAllId())) {
			
			Office rOffice = SysCommonUtils.findOfficeById(allAllocateInfo.getrOffice().getId());
			// 生成流水单号
			allAllocateInfo.setAllId(BusinessUtils.getOfficeBusinessNo(allAllocateInfo.getBusinessType(), rOffice));
			// 状态更改为待入库
			allAllocateInfo.setStatus(AllocationConstant.BetweenStatus.TO_IN_STORE_STATUS);
			// 设置清分信息
			allAllocateInfo.setClearInRegisterBy(allAllocateInfo.getCurrentUser());
			allAllocateInfo.setClearInRegisterName(allAllocateInfo.getCurrentUser().getName());
			allAllocateInfo.setClearInRegisterDate(new Date());
			// 插入登记机构信息
			allAllocateInfo.setrOffice(SysCommonUtils.findOfficeById(allAllocateInfo.getrOffice().getId()));
			// 插入接收机构信息
			allAllocateInfo.setaOffice(SysCommonUtils.findOfficeById(allAllocateInfo.getaOffice().getId()));
			
			// 插入登记人登记时间更新人更新时间
			if (allAllocateInfo.getCurrentUser() != null) {
				allAllocateInfo.setCreateBy(allAllocateInfo.getCurrentUser());
				allAllocateInfo.setUpdateBy(allAllocateInfo.getCurrentUser());
				allAllocateInfo.setCreateName(allAllocateInfo.getCurrentUser().getName());
				allAllocateInfo.setUpdateName(allAllocateInfo.getCurrentUser().getName());
			}
			allAllocateInfo.setUpdateDate(new Date());
			allAllocateInfo.setCreateDate(allAllocateInfo.getUpdateDate());
			// 插入清分信息
			dao.insert(allAllocateInfo);
		} else {
			// 检查数据一致性
			checkVersion(allAllocateInfo);
			//allAllocateInfo.preUpdate();
			// 插入登记人登记时间,更新人更新时间
			if (allAllocateInfo.getCurrentUser() != null) {
				allAllocateInfo.setCreateBy(allAllocateInfo.getCurrentUser());
				allAllocateInfo.setUpdateBy(allAllocateInfo.getCurrentUser());
				allAllocateInfo.setCreateName(allAllocateInfo.getCurrentUser().getName());
				allAllocateInfo.setUpdateName(allAllocateInfo.getCurrentUser().getName());
			}
			allAllocateInfo.setUpdateDate(new Date());
			allAllocateInfo.setCreateDate(allAllocateInfo.getUpdateDate());
			// 更新清分信息
			dao.update(allAllocateInfo);
			// 修改时还原箱子状态
			if (AllocationConstant.BusinessType.Between_ATM_Clear.equals(allAllocateInfo.getBusinessType())) {
				// 库间清机业务修改时，还原箱子状态
				AllAllocateInfo tempAllocat = get(allAllocateInfo.getAllId());
				/** 获取编辑后的箱袋信息List	by:wxz  2018-01-16 begin*/
				List<String> editList = Lists.newArrayList();
				for(AllAllocateItem editItem : allAllocateInfo.getAllAllocateItemList()){
					editList.add(editItem.getBoxNo());
				}
				for (AllAllocateItem item : tempAllocat.getAllAllocateItemList()) {
					//箱号不为空时把箱袋状态变为空箱
					if (StringUtils.isNotBlank(item.getBoxNo())) {
						StoBoxInfo stoBoxInfo = new StoBoxInfo();
						stoBoxInfo.setId(item.getBoxNo());
						// 根据rfid查询对应的实体类对象
						stoBoxInfo = StoreCommonUtils.getBoxInfoByRfidAndBoxNo(stoBoxInfo);
						// 对象不为空时进行操作,并且判断前后的箱袋号不相同时进行操作
						if (stoBoxInfo != null && !editList.contains(item.getBoxNo())) {
							// 更改箱袋状态为清分
							stoBoxInfo.setBoxStatus(Constant.BoxStatus.ATM_BOX_STATUS_CLEAR);
							// 设置箱袋条件：更新人，更新时间和箱袋状态
							if (allAllocateInfo.getCurrentUser() != null) {
								stoBoxInfo.setUpdateBy(allAllocateInfo.getCurrentUser());
								stoBoxInfo.setUpdateName(allAllocateInfo.getCurrentUser().getName());
							}
							stoBoxInfo.setUpdateDate(new Date());
							// 更新操作
							StoreCommonUtils.updateBoxStatus(stoBoxInfo);
						}
					}
				}
			}
			// 删除登记过的明细信息
			AllAllocateItem allAllocateItem = new AllAllocateItem();
			allAllocateItem.setAllocationInfo(allAllocateInfo);
			allAllocateItemDao.delete(allAllocateItem);
		}
		// 取得明细信息
		List<AllAllocateItem> itemList = allAllocateInfo.getAllAllocateItemList();
		
		for (AllAllocateItem item : itemList) {
			// 保存清分中心登记的物品明细
			if (AllocationConstant.BetweenConfirmFlag.UNCONFIRMED.equals(item.getConfirmFlag())) {
				// 设置流水单号
				item.setAllocationInfo(allAllocateInfo);
				item.setAllItemsId(IdGen.uuid());
				allAllocateItemDao.insert(item);
				if (AllocationConstant.BusinessType.Between_ATM_Clear.equals(allAllocateInfo.getBusinessType())) {
					//箱号不为空时把箱袋状态变为空箱
					if (StringUtils.isNotBlank(item.getBoxNo())) {
						StoBoxInfo stoBoxInfo = new StoBoxInfo();
						stoBoxInfo.setId(item.getBoxNo());
						// 根据rfid查询对应的实体类对象
						stoBoxInfo = StoreCommonUtils.getBoxInfoByRfidAndBoxNo(stoBoxInfo);
						// 对象不为空时进行操作
						if (stoBoxInfo != null) {
							// 更改箱袋状态为清分
							stoBoxInfo.setBoxStatus(Constant.BoxStatus.EMPTY);
							// 设置箱袋条件：更新人，更新时间和箱袋状态
							if (allAllocateInfo.getCurrentUser() != null) {
								stoBoxInfo.setUpdateBy(allAllocateInfo.getCurrentUser());
								stoBoxInfo.setUpdateName(allAllocateInfo.getCurrentUser().getName());
							}
							stoBoxInfo.setUpdateDate(new Date());
							// 更新操作
							StoreCommonUtils.updateBoxStatus(stoBoxInfo);
						}
					}
				}
			}
		}
		// 发送消息
		List<String> paramsList = Lists.newArrayList();
		paramsList.add(allAllocateInfo.getaOffice().getName());
		paramsList.add(allAllocateInfo.getAllId());
		SysCommonUtils.allocateMessageQueueAdd(allAllocateInfo.getBusinessType(), allAllocateInfo.getStatus(),
				paramsList, allAllocateInfo.getaOffice().getId(), UserUtils.getUser());

	}

	/**
	 * 库间清分保存入库接收信息
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月13日
	 * 
	 * @param allAllocateInfo
	 *            主表信息
	 */
	@Transactional(readOnly = false)
	public void saveCashReceiveClearInfo(AllAllocateInfo allAllocateInfo) {
		// 设置状态为已完成
		allAllocateInfo.setStatus(AllocationConstant.BetweenStatus.FINISH_STATUS);
		// 设置接收人
		allAllocateInfo.setClearInReceiveBy(allAllocateInfo.getCurrentUser());
		// 设置接收人姓名
		allAllocateInfo.setClearInReceiveName(allAllocateInfo.getCurrentUser().getName());
		// 设置接收日期
		allAllocateInfo.setClearInReceiveDate(new Date());
		// 检查数据一致性
		checkVersion(allAllocateInfo);
		allAllocateInfo.preUpdate();
		// 更新接收信息
		dao.updateInStoreReceiveMessage(allAllocateInfo);
		// 物品明细表信息
		List<AllAllocateItem> itemList = allAllocateInfo.getAllAllocateItemList();
		// 物品改变list
		List<ChangeStoreEntity> changeList = Lists.newArrayList();
		ChangeStoreEntity changeStoreItem;
		for (AllAllocateItem item : itemList) {
			// 已确认物品进行入库操作
			if (AllocationConstant.BetweenConfirmFlag.CONFIRMED.equals(item.getConfirmFlag())) {
				changeStoreItem = new ChangeStoreEntity();
				// 设置物品改变信息
				changeStoreItem.setGoodsId(item.getGoodsId());
				// 入库时增库存
				changeStoreItem.setNum(item.getMoneyNumber());
				changeList.add(changeStoreItem);
			}
		}
		User user = new User();
		user.setId(allAllocateInfo.getCurrentUser().getId());
		user.setName(allAllocateInfo.getCurrentUser().getName());
		user.setOffice(allAllocateInfo.getrOffice());
		// 修改预剩余库存及实际库存
		StoreCommonUtils.changeStoreAndSurplusStores(changeList, allAllocateInfo.getrOffice().getId(),
				allAllocateInfo.getAllId(), user);
	}
	
	/**
	 * 上门收款/库间清机入库接收
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月13日
	 * 
	 * @param allAllocateInfo
	 *            主表信息
	 */
	@Transactional(readOnly = false)
	public void saveCashReceiveInStoreInfo(AllAllocateInfo allAllocateInfo) {
				// 设置状态为已完成
				allAllocateInfo.setStatus(AllocationConstant.BetweenStatus.FINISH_STATUS);
				// 设置接收人
				allAllocateInfo.setClearInReceiveBy(allAllocateInfo.getCurrentUser());
				// 设置接收人姓名
				allAllocateInfo.setClearInReceiveName(allAllocateInfo.getCurrentUser().getName());
				// 设置接收日期
				allAllocateInfo.setClearInReceiveDate(new Date());
				allAllocateInfo.setConfirmName(allAllocateInfo.getCurrentUser().getName());
				allAllocateInfo.setConfirmDate(allAllocateInfo.getClearInReceiveDate());
				allAllocateInfo.setConfirmAmount(allAllocateInfo.getRegisterAmount());
				// 检查数据一致性
				checkVersion(allAllocateInfo);
				allAllocateInfo.preUpdate();
				// 更新确认信息
				dao.updateConfirmMessage(allAllocateInfo);
				// 更新接收信息
				dao.updateInStoreReceiveMessage(allAllocateInfo);
				// 物品明细表信息
				List<AllAllocateItem> itemList = allAllocateInfo.getAllAllocateItemList();
				
				for (AllAllocateItem item : itemList) {
					// 保存清分中心登记的物品明细
					if (AllocationConstant.BetweenConfirmFlag.UNCONFIRMED.equals(item.getConfirmFlag())) {
						// 设置流水单号
						item.setAllocationInfo(allAllocateInfo);
						item.setAllItemsId(IdGen.uuid());
						item.setConfirmFlag(AllocationConstant.BetweenConfirmFlag.CONFIRMED);
						allAllocateItemDao.insert(item);
					}
				}
				// 发送消息
				List<String> paramsList = Lists.newArrayList();
				paramsList.add(allAllocateInfo.getaOffice().getName());
				paramsList.add(allAllocateInfo.getAllId());
				SysCommonUtils.allocateMessageQueueAdd(allAllocateInfo.getBusinessType(), allAllocateInfo.getStatus(),
						paramsList, allAllocateInfo.getaOffice().getId(), UserUtils.getUser());
				// 物品改变list
				List<ChangeStoreEntity> changeList = Lists.newArrayList();
				ChangeStoreEntity changeStoreItem;
				for (AllAllocateItem item : itemList) {
					// 已确认物品进行入库操作
					if (AllocationConstant.BetweenConfirmFlag.CONFIRMED.equals(item.getConfirmFlag())) {
						changeStoreItem = new ChangeStoreEntity();
						// 设置物品改变信息
						changeStoreItem.setGoodsId(item.getGoodsId());
						// 入库时增库存
						changeStoreItem.setNum(item.getMoneyNumber());
						changeList.add(changeStoreItem);
					}
				}
				User user = new User();
				user.setId(allAllocateInfo.getCurrentUser().getId());
				user.setName(allAllocateInfo.getCurrentUser().getName());
				user.setOffice(allAllocateInfo.getaOffice());
				// 修改预剩余库存及实际库存
				StoreCommonUtils.changeStoreAndSurplusStores(changeList, allAllocateInfo.getaOffice().getId(),
						allAllocateInfo.getAllId(), user);
	}

	/**
	 * 现金库逻辑删除主表信息
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月13日
	 * 
	 * @param AllAllocateInfo
	 *            主表信息
	 */
	@Override
	@Transactional(readOnly = false)
	public void delete(AllAllocateInfo allAllocateInfo) {
		
		if (AllocationConstant.BusinessType.Between_Clear.equals(allAllocateInfo.getBusinessType())
				|| AllocationConstant.BusinessType.Between_ATM_Add.equals(allAllocateInfo.getBusinessType())) {
			
			// 取得物品明细表list
			List<AllAllocateItem> itemList = allAllocateInfo.getAllAllocateItemList();
			// 设置变更物品信息
			List<ChangeStoreEntity> changeList = Lists.newArrayList();
			ChangeStoreEntity changeItem;
			for (AllAllocateItem item : itemList) {
				changeItem = new ChangeStoreEntity();
				// 设置变更物品id和数量
				changeItem.setGoodsId(item.getGoodsId());
				changeItem.setNum(item.getMoneyNumber());
				changeList.add(changeItem);
			}
			User user = new User();
			user.setId(allAllocateInfo.getCurrentUser().getId());
			user.setName(allAllocateInfo.getCurrentUser().getName());
			user.setOffice(allAllocateInfo.getrOffice());
			// 回滚预剩余库存及实际库存
			StoreCommonUtils.changeStoreAndSurplusStores(changeList, allAllocateInfo.getrOffice().getId(),
					allAllocateInfo.getAllId(), user);
		}
		
		// 库间清机业务删除时，还原箱子状态
		if (AllocationConstant.BusinessType.Between_ATM_Clear.equals(allAllocateInfo.getBusinessType())) {
			StoBoxInfo stoBoxInfo = null;
			for (AllAllocateItem item : allAllocateInfo.getAllAllocateItemList()) {
				//箱号不为空时把箱袋状态变为空箱
				if (StringUtils.isNotBlank(item.getBoxNo())) {
					stoBoxInfo = new StoBoxInfo();
					stoBoxInfo.setId(item.getBoxNo());
					// 根据rfid查询对应的实体类对象
					stoBoxInfo = StoreCommonUtils.getBoxInfoByRfidAndBoxNo(stoBoxInfo);
					// 对象不为空时进行操作
					if (stoBoxInfo != null) {
						// 更改箱袋状态为清分
						stoBoxInfo.setBoxStatus(Constant.BoxStatus.ATM_BOX_STATUS_CLEAR);
						// 设置箱袋条件：更新人，更新时间和箱袋状态
						if (allAllocateInfo.getCurrentUser() != null) {
							stoBoxInfo.setUpdateBy(allAllocateInfo.getCurrentUser());
							stoBoxInfo.setUpdateName(allAllocateInfo.getCurrentUser().getName());
						}
						stoBoxInfo.setUpdateDate(new Date());
						// 更新操作
						StoreCommonUtils.updateBoxStatus(stoBoxInfo);
					}
				}
			}
		}
		
		allAllocateInfo.preUpdate();
		// 逻辑删除主表信息
		super.delete(allAllocateInfo);
	}

	/**
	 * 调拨主表的数据一致性验证
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月18日
	 * 
	 * @param strUpdateDate
	 *            页面上的更新时间
	 * @param allAllocateInfo
	 */
	public void checkVersion(AllAllocateInfo allAllocateInfo) {
		// 数据一致性验证
		AllAllocateInfo oldData = super.get(allAllocateInfo.getAllId());
		if (oldData != null) {
			String oldUpdateDate = DateUtils.formatDate(oldData.getUpdateDate(), Constant.Dates.FORMATE_YYYYMMDDHHMMSSSSSSSS);
			if (!oldUpdateDate.equals(allAllocateInfo.getStrUpdateDate())) {
				throw new BusinessException("message.E0007", "", new String[] { allAllocateInfo.getAllId() });
			}
		}
	}

}
