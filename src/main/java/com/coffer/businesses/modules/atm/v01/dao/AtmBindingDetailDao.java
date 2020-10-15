package com.coffer.businesses.modules.atm.v01.dao;

import java.util.List;
import java.util.Map;

import com.coffer.businesses.modules.atm.v01.entity.AtmBindingDetail;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 绑定信息明细DAO接口
 * 
 * @author Lmeon
 * @version 2015-06-03
 */
@MyBatisDao
public interface AtmBindingDetailDao extends CrudDao<AtmBindingDetail> {

	/**
	 * 
	 * @author LLF
	 * @version 2015年6月10日
	 * 
	 *          查询冠字号码相应信息
	 * @param atmBindingDetail
	 * @return
	 */
	public List<AtmBindingDetail> findBsBusi(AtmBindingDetail atmBindingDetail);

	/**
	 * 
	 * @author LLF
	 * @version 2015年6月10日
	 * 
	 *          ATM机与冠字号信息绑定
	 * @param atmBindingDetail
	 */
	public void updateBsBusiAtmNo(AtmBindingDetail atmBindingDetail);
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年7月2日
	 * 
	 *  退库钞箱修改冠字号码表
	 * @param idObjs 冠字号码ID列表
	 */
	public void updateBsBusiBoxNo(List<String> idObjs);
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年7月2日
	 * 
	 *  查询清点钞箱绑定明细
	 * @param map
	 */
	public List<String> findByIdObj(Map<String,Object> map);
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年7月2日
	 * 
	 *  修改绑定明细钞箱清点状态
	 * @param map
	 */
	public void updateBindingDetailFlag(Map<String,Object> map);
	
	/**
	 * 
	 * @author wanglu
	 * @version 2017年11月15日
	 * 
	 *  修改绑定明细钞箱清点状态
	 * @param AtmBindingDetail
	 */
	public List<AtmBindingDetail> getAtmBindingDetailListByBoxNo(AtmBindingDetail atmBindingDetail);
	
	/**
	 * 根据绑定ID删除绑定详情
	 * @author wanglu
	 * @version 2017年11月124日
	 * @param AtmBindingDetail
	 */
	public void delDetailByBindingId(AtmBindingDetail atmBindingDetail);
}