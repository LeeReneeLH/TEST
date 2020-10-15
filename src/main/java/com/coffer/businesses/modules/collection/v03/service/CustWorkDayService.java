package com.coffer.businesses.modules.collection.v03.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.collection.v03.dao.CustWorkDayDao;
import com.coffer.businesses.modules.collection.v03.entity.CustWorkDay;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;



/**
 * 员工工作量明细统计Service
 * 
 * @author wanglin
 * @version 2017-09-04
 */
@Service
@Transactional(readOnly = true)
public class CustWorkDayService extends CrudService<CustWorkDayDao, CustWorkDay> {


	@Autowired
	private CustWorkDayDao empWorkDao;
	
	
	public CustWorkDay get(String detailId) {
		return super.get(detailId);
	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 当前页数据取得
	 * 
	 * @param CustWorkDay
	 */
	public Page<CustWorkDay> findPage(Page<CustWorkDay> page, CustWorkDay empWork) {
		
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
		return super.findPage(page, empWork);
	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 明细行查看（面值）
	 * 
	 * @param CustWorkDay
	 */
	public List<CustWorkDay> findDetailParList(CustWorkDay empWork) {
		empWork.setType(Constant.DenominationType.RMB_PDEN); // 人民币：纸币
		return empWorkDao.findDetailParList(empWork);
	}

	/**
	 * 
	 * @author wanglin
	 * @version 明细行查看（人员）
	 * 
	 * @param CustWorkDay
	 */
	public List<CustWorkDay> findDetailManList(CustWorkDay empWork) {
		return empWorkDao.findDetailManList(empWork);
	}
	
	
	/**
	 * 
	 * @author wanglin
	 * @version 所有数据取得（Excel下载用）
	 * 
	 * @param CustWorkDay
	 */
	public List<CustWorkDay> findExcelList(CustWorkDay empWork) {
		
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

		return empWorkDao.findExcelList(empWork);
	}

}