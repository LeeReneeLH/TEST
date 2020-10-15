package com.coffer.businesses.modules.allocation.v01.dao;

import java.util.List;
import java.util.Map;

import com.coffer.businesses.modules.allocation.v01.entity.AllHandoverInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * @author Chengs
 *
 */
@MyBatisDao
public interface AllHandoverInfoDao extends CrudDao<AllHandoverInfo> {
	public List<Map<String,Object>> getHandoverTaskList(Map<String,Object> param);
	
	public AllHandoverInfo get(Map<String, Object> param);
	
	/**
	 * 
	 * @author chenxin
	 * @version 2015年10月16日
	 * 
	 * 根据流水号查询交接信息（测试用） 
	 * @param allId
	 * @return
	 */
	public AllHandoverInfo getByAllId(String allId);
	
	/**
	 * 
	 * @author wangbaozhong
	 * @version 2016年4月6日
	 * 
	 *  根据交接ID取得交接人员签名图片信息
	 * @param handoverId 交接ID
	 * @return 交接人员签名图片信息
	 */
	public AllHandoverInfo getSignInfoByHandoverId(AllHandoverInfo allHandoverInfo);
}
