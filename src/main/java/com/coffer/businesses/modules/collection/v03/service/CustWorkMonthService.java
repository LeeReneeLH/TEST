package com.coffer.businesses.modules.collection.v03.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.collection.v03.dao.CustWorkMonthDao;
import com.coffer.businesses.modules.collection.v03.entity.CustWorkMonth;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;



/**
 * 客户清点量(月)统计Service
 * 
 * @author wanglin
 * @version 2017-09-04
 */
@Service
@Transactional(readOnly = true)
public class CustWorkMonthService extends CrudService<CustWorkMonthDao, CustWorkMonth> {


	@Autowired
	private CustWorkMonthDao custWorkMonthDao;
	
	
	public CustWorkMonth get(String detailId) {
		return super.get(detailId);
	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 当前页数据取得
	 * 
	 * @param custWorkMonth
	 */
	public Page<CustWorkMonth> findPage(Page<CustWorkMonth> page, CustWorkMonth custWorkMonth) {
		return super.findPage(page, custWorkMonth);
	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 明细行查看（面值）
	 * 
	 * @param custWorkMonth
	 */
	public List<CustWorkMonth> findDetailParList(CustWorkMonth custWorkMonth) {
		custWorkMonth.setType(Constant.DenominationType.RMB_PDEN); // 人民币：纸币
		return custWorkMonthDao.findDetailParList(custWorkMonth);
	}

	/**
	 * 
	 * @author wanglin
	 * @version 明细行查看（人员）
	 * 
	 * @param custWorkMonth
	 */
	public List<CustWorkMonth> findDetailManList(CustWorkMonth custWorkMonth) {
		return custWorkMonthDao.findDetailManList(custWorkMonth);
	}
	
	
	/**
	 * 
	 * @author wanglin
	 * @version 所有数据取得（Excel下载用）
	 * 
	 * @param custWorkMonth
	 */
	public List<CustWorkMonth> findExcelList(CustWorkMonth custWorkMonth) {
		return custWorkMonthDao.findExcelList(custWorkMonth);
	}

	/**
	 * 查找款箱拆箱月度列表
	 * @return
	 */
	public List<String> findMonthList(CustWorkMonth custWorkMonth){
		return custWorkMonthDao.findMonthList(custWorkMonth);
	}
	
	
}