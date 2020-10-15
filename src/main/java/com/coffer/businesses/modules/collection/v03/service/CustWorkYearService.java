package com.coffer.businesses.modules.collection.v03.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.collection.v03.dao.CustWorkYearDao;
import com.coffer.businesses.modules.collection.v03.entity.CustWorkYear;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;



/**
 * 客户清点量(年)统计Service
 * 
 * @author wanglin
 * @version 2017-09-04
 */
@Service
@Transactional(readOnly = true)
public class CustWorkYearService extends CrudService<CustWorkYearDao, CustWorkYear> {


	@Autowired
	private CustWorkYearDao custWorkYearDao;
	
	
	public CustWorkYear get(String detailId) {
		return super.get(detailId);
	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 当前页数据取得
	 * 
	 * @param custWorkYear
	 */
	public Page<CustWorkYear> findPage(Page<CustWorkYear> page, CustWorkYear custWorkYear) {
		return super.findPage(page, custWorkYear);
	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 明细行查看（面值）
	 * 
	 * @param custWorkYear
	 */
	public List<CustWorkYear> findDetailParList(CustWorkYear custWorkYear) {
		custWorkYear.setType(Constant.DenominationType.RMB_PDEN); // 人民币：纸币
		return custWorkYearDao.findDetailParList(custWorkYear);
	}

	/**
	 * 
	 * @author wanglin
	 * @version 明细行查看（人员）
	 * 
	 * @param custWorkYear
	 */
	public List<CustWorkYear> findDetailManList(CustWorkYear custWorkYear) {
		return custWorkYearDao.findDetailManList(custWorkYear);
	}
	
	
	/**
	 * 
	 * @author wanglin
	 * @version 所有数据取得（Excel下载用）
	 * 
	 * @param custWorkYear
	 */
	public List<CustWorkYear> findExcelList(CustWorkYear custWorkYear) {
		return custWorkYearDao.findExcelList(custWorkYear);
	}

	/**
	 * 查找款箱拆箱年度列表
	 * @return
	 */
	public List<String> findYearList(CustWorkYear custWorkYear){
		return custWorkYearDao.findYearList(custWorkYear);
	}
	
	
}