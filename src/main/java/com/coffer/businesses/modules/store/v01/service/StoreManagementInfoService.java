/**
 * wenjian:    StoreManagementInfoService.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2017年8月9日    wangbaozhong     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2017年8月9日 上午10:19:35
 */
package com.coffer.businesses.modules.store.v01.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.v01.dao.StoreCoOfficeAssocationDao;
import com.coffer.businesses.modules.store.v01.dao.StoreManagementInfoDao;
import com.coffer.businesses.modules.store.v01.dao.StoreManagerAssocationDao;
import com.coffer.businesses.modules.store.v01.dao.StoreTypeAssocationDao;
import com.coffer.businesses.modules.store.v01.entity.StoreManagementInfo;
import com.coffer.businesses.modules.store.v01.entity.StoreCoOfficeAssocation;
import com.coffer.businesses.modules.store.v01.entity.StoreManagerAssocation;
import com.coffer.businesses.modules.store.v01.entity.StoreTypeAssocation;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
* Title: StoreManagementInfoService 
* <p>Description: </p>
* @author wangbaozhong
* @date 2017年8月9日 上午10:19:35
*/
@Service
@Transactional(readOnly = true)
public class StoreManagementInfoService extends CrudService<StoreManagementInfoDao, StoreManagementInfo> {
	@Autowired
	private StoreCoOfficeAssocationDao storeCoOfficeAssocationDao;
	@Autowired
	private StoreManagerAssocationDao storeManagerAssocationDao;
	@Autowired
	private StoreTypeAssocationDao storeTypeAssocationDao;
	
	/**
	 * 
	 * Title: saveMangementInfo
	 * <p>Description: 保存库房设定信息</p>
	 * @author:     wangbaozhong
	 * @param storeManagementInfo 
	 * void    返回类型
	 */
	@Transactional(readOnly = false)
	public void saveMangementInfo(StoreManagementInfo storeManagementInfo) {
		
		if (StringUtils.isNotBlank(storeManagementInfo.getId())) {
			storeCoOfficeAssocationDao.deleteByStoreId(storeManagementInfo.getId());
			storeManagerAssocationDao.deleteByStoreId(storeManagementInfo.getId());
			storeTypeAssocationDao.deleteByStoreId(storeManagementInfo.getId());
		}
		
		super.save(storeManagementInfo);
		
		if (!Collections3.isEmpty(storeManagementInfo.getOfficeIdList())) {
			for (String officeId : storeManagementInfo.getOfficeIdList()) {
				StoreCoOfficeAssocation entity = new StoreCoOfficeAssocation();
				
				entity.setOffice(SysCommonUtils.findOfficeById(officeId));
				entity.setStoreId(storeManagementInfo.getId());
				entity.preInsert();
				storeCoOfficeAssocationDao.insert(entity);
			}
		}
		
		if (!Collections3.isEmpty(storeManagementInfo.getUserIdList())) {
			
			for (String userId : storeManagementInfo.getUserIdList()) {
				StoreManagerAssocation entity = new StoreManagerAssocation();
				entity.setStoreId(storeManagementInfo.getId());
				entity.setUser(UserUtils.get(userId));
				entity.preInsert();
				storeManagerAssocationDao.insert(entity);
			}
		}
		if (!Collections3.isEmpty(storeManagementInfo.getBoxTypeList())) {
			
			for (String boxType : storeManagementInfo.getBoxTypeList()) {
				StoreTypeAssocation entity = new StoreTypeAssocation();
				entity.setStoreId(storeManagementInfo.getId());
				entity.setStorageType(boxType);
				entity.preInsert();
				storeTypeAssocationDao.insert(entity);
			}
		}
		
	}
	
	@Transactional(readOnly = true)
	public boolean checkStoreName(String oldStoreName, String storeName) {
		if (storeName !=null && storeName.equals(oldStoreName)) {
			return true;
		} else if (storeName !=null && getStoreInfoByStoreName(storeName) == null) {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * Title: getStoreInfoByStoreName
	 * <p>Description: 根据库房名称查询库房</p>
	 * @author:     wangbaozhong
	 * @param storeName
	 * @return 
	 * StoreManagementInfo    返回类型
	 */
	@Transactional(readOnly = true)
	public StoreManagementInfo getStoreInfoByStoreName(String storeName) {
		return dao.getStoreInfoByStoreName(storeName);
	}
	
	/**
	 * 
	 * Title: setStoreStatus
	 * <p>Description: 更新库房状态</p>
	 * @author:     wangbaozhong
	 * @param storeManagementInfo 
	 * void    返回类型
	 */
	@Transactional(readOnly = false)
	public void setStoreStatus(StoreManagementInfo storeManagementInfo) {
		
		if (Constant.deleteFlag.Valid.equals(storeManagementInfo.getDelFlag())) {
			storeManagementInfo.setDelFlag(Constant.deleteFlag.Invalid);
		} else {
			storeManagementInfo.setDelFlag(Constant.deleteFlag.Valid);
		}
		storeManagementInfo.preUpdate();
		dao.updateStatus(storeManagementInfo);
	}
}
