/**
 * wenjian:    StoDocStamperMgrService.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2016年9月12日    xq     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2016年9月12日 下午2:22:59
 */
package com.coffer.businesses.modules.store.v02.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.store.v02.dao.StoDocTempInfoDao;
import com.coffer.businesses.modules.store.v02.dao.StoDocTempUserDetailDao;
import com.coffer.businesses.modules.store.v02.entity.StoDocTempInfo;
import com.coffer.businesses.modules.store.v02.entity.StoDocTempUserDetail;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.IdGen;

/**
* Title: StoDocStamperMgrService 
* <p>Description: 单据模板信息管理Service</p>
* @author wangbaozhong
* @date 2016年9月12日 下午2:22:59
*/
@Service
@Transactional(readOnly = true)
public class StoDocStamperMgrService extends CrudService<StoDocTempInfoDao, StoDocTempInfo> {
	@Autowired
	private StoDocTempInfoDao stoDocTempInfoDao;
	@Autowired
	private StoDocTempUserDetailDao stoDocTempUserDetailDao;
	
	/**
	 * 
	 * Title: save
	 * <p>Description: 保存单据模板信息</p>
	 * @author:     wangbaozhong
	 * @param stoDocTempInfo 单据模板信息
	 * void    返回类型
	 */
	@Transactional(readOnly = false)
	public void save(StoDocTempInfo stoDocTempInfo){
		// 登记单据模板信息主表
		stoDocTempInfo.setId(IdGen.uuid());
		stoDocTempInfo.preInsert();
		stoDocTempInfoDao.insert(stoDocTempInfo);
		//登记单据人员信息表
		for (StoDocTempUserDetail userDetail : stoDocTempInfo.getDocTempUserDetailList()) {
			userDetail.setDocInfoId(stoDocTempInfo.getId());
			userDetail.setId(IdGen.uuid());
			stoDocTempUserDetailDao.insert(userDetail);
		}
	}
	
	/**
	 * 
	 * Title: update
	 * <p>Description: 更新单据人员信息</p>
	 * @author:     wangbaozhong
	 * @param stoDocTempInfo 单据模板信息
	 * void    返回类型
	 */
	@Transactional(readOnly = false)
	public void update(StoDocTempInfo stoDocTempInfo){
		// 更新主表
		stoDocTempInfo.preUpdate();
		stoDocTempInfoDao.update(stoDocTempInfo);
		// 删除单据人员信息表
		stoDocTempUserDetailDao.deleteByDocInfoId(stoDocTempInfo.getId());
		// 登记单据人员信息表
		for (StoDocTempUserDetail userDetail : stoDocTempInfo.getDocTempUserDetailList()) {
			userDetail.setDocInfoId(stoDocTempInfo.getId());
			userDetail.setId(IdGen.uuid());
			stoDocTempUserDetailDao.insert(userDetail);
		}
	}
	
	/**
	 * 
	 * Title: updatePbocOfficeStamperId
	 * <p>Description: 按照单据模板信息主表主键ID，更新人行机构印章ID </p>
	 * @author:     wangbaozhong
	 * @param stoDocTempInfo 更新信息
	 * void    返回类型
	 */
	@Transactional(readOnly = false)
	public void updatePbocOfficeStamperId(StoDocTempInfo stoDocTempInfo){
		// 更新主表
		stoDocTempInfo.preUpdate();
		stoDocTempInfoDao.updatePbocOfficeStamperId(stoDocTempInfo);
		
	}
	
	/**
	 * 
	 * Title: getByBusinessAndStatus
	 * <p>Description: 按照业务类型和状态查询单据印章表</p>
	 * @author:     wangbaozhong
	 * @param businessType	业务类型
	 * @param status	状态
	 * @param officeId	所属机构ID
	 * @return 
	 * StoDocTempInfo    返回类型
	 */
	@Transactional(readOnly = true)
	public StoDocTempInfo getByBusinessAndStatus(String businessType, String status, String officeId) {
		return stoDocTempInfoDao.getByBusinessAndStatus(businessType, status, officeId);
	}
}
