package com.coffer.businesses.modules.collection.v03.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.collection.v03.dao.EmpWorkDetailDao;
import com.coffer.businesses.modules.collection.v03.entity.EmpWorkDetail;
import com.coffer.businesses.modules.store.v01.dao.StoEscortInfoDao;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;



/**
 * 员工工作量明细统计Service
 * 
 * @author wanglin
 * @version 2017-09-04
 */
@Service
@Transactional(readOnly = true)
public class EmpWorkDetailService extends CrudService<EmpWorkDetailDao, EmpWorkDetail> {

	@Autowired
	private StoEscortInfoDao stoEscortInfoDao;

	@Autowired
	private EmpWorkDetailDao empWorkDao;
	
	
	public EmpWorkDetail get(String detailId) {
		return super.get(detailId);
	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 当前页数据取得
	 * 
	 * @param EmpWorkDetail
	 */
	public Page<EmpWorkDetail> findPage(Page<EmpWorkDetail> page, EmpWorkDetail empWork) {
		
		// 查询条件： 开始时间
		if (empWork.getCreateTimeStart() != null) {
			empWork.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(empWork.getCreateTimeStart())));
		}

		// 查询条件： 结束时间
		if (empWork.getCreateTimeEnd() != null) {
			empWork.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(empWork.getCreateTimeEnd())));
		}
		//清分人
		empWork.setSearchClearManId("");
		if (StringUtils.isNotBlank(empWork.getSearchClearManNo())){
			StoEscortInfo stoescortinfo = stoEscortInfoDao.get(empWork.getSearchClearManNo());
			if (stoescortinfo != null){
				empWork.setSearchClearManId(stoescortinfo.getUser().getId());
			}
		}
		
		
		return super.findPage(page, empWork);
	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 明细行查看
	 * 
	 * @param EmpWorkDetail
	 */
	public List<EmpWorkDetail> findRowList(EmpWorkDetail empWork) {
		empWork.setType(Constant.DenominationType.RMB_PDEN); // 人民币：纸币
		return empWorkDao.findRowList(empWork);
	}

	
	
	/**
	 * 
	 * @author wanglin
	 * @version 所有数据取得（Excel下载用）
	 * 
	 * @param EmpWorkDetail
	 */
	public List<EmpWorkDetail> findExcelList(EmpWorkDetail empWork) {
		
		// 查询条件： 开始时间
		if (empWork.getCreateTimeStart() != null) {
			empWork.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(empWork.getCreateTimeStart())));
		}

		// 查询条件： 结束时间
		if (empWork.getCreateTimeEnd() != null) {
			empWork.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(empWork.getCreateTimeEnd())));
		}
		//清分人
		empWork.setSearchClearManId("");
		if (StringUtils.isNotBlank(empWork.getSearchClearManNo())){
			StoEscortInfo stoescortinfo = stoEscortInfoDao.get(empWork.getSearchClearManNo());
			if (stoescortinfo != null){
				empWork.setSearchClearManId(stoescortinfo.getUser().getId());
			}
		}
		return empWorkDao.findExcelList(empWork);
	}

}