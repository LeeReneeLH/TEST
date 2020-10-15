package com.coffer.businesses.modules.collection.v03.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.collection.v03.dao.CustWorkQuarterDao;
import com.coffer.businesses.modules.collection.v03.entity.CustWorkQuarter;
import com.coffer.businesses.modules.collection.v03.entity.SelectItem;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;



/**
 * 客户清点量(季)统计Service
 * 
 * @author wanglin
 * @version 2017-09-04
 */
@Service
@Transactional(readOnly = true)
public class CustWorkQuarterService extends CrudService<CustWorkQuarterDao, CustWorkQuarter> {


	@Autowired
	private CustWorkQuarterDao custWorkQuarterDao;
	
	
	public CustWorkQuarter get(String detailId) {
		return super.get(detailId);
	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 当前页数据取得
	 * 
	 * @param custWorkQuarter
	 */
	public Page<CustWorkQuarter> findPage(Page<CustWorkQuarter> page, CustWorkQuarter custWorkQuarter) {
		return super.findPage(page, custWorkQuarter);
	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 明细行查看（面值）
	 * 
	 * @param custWorkQuarter
	 */
	public List<CustWorkQuarter> findDetailParList(CustWorkQuarter custWorkQuarter) {
		custWorkQuarter.setType(Constant.DenominationType.RMB_PDEN); // 人民币：纸币
		return custWorkQuarterDao.findDetailParList(custWorkQuarter);
	}

	/**
	 * 
	 * @author wanglin
	 * @version 明细行查看（人员）
	 * 
	 * @param custWorkQuarter
	 */
	public List<CustWorkQuarter> findDetailManList(CustWorkQuarter custWorkQuarter) {
		return custWorkQuarterDao.findDetailManList(custWorkQuarter);
	}
	
	
	/**
	 * 
	 * @author wanglin
	 * @version 所有数据取得（Excel下载用）
	 * 
	 * @param custWorkQuarter
	 */
	public List<CustWorkQuarter> findExcelList(CustWorkQuarter custWorkQuarter) {
		return custWorkQuarterDao.findExcelList(custWorkQuarter);
	}

	/**
	 * 查找款箱拆箱季度列表
	 * @return
	 */
	public List<SelectItem> findQuarterList(CustWorkQuarter custWorkQuarter){
		return custWorkQuarterDao.findQuarterList(custWorkQuarter);
	}
	
	
}