package com.coffer.businesses.modules.store.v02.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.store.v02.entity.StoOriginalBanknote;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 原封新券管理DAO接口
 * @author LLF
 * @version 2016-05-30
 */
@MyBatisDao
public interface StoOriginalBanknoteDao extends CrudDao<StoOriginalBanknote> {
	
	/**
	 * 
	 * @author LLF
	 * @version 2016年5月31日
	 * 
	 *  出库列表查询
	 * @param stoOriginalBanknote
	 * @return
	 */
	public List<StoOriginalBanknote> findOutList(StoOriginalBanknote stoOriginalBanknote);
	
	/**
	 * 
	 * Title: getOriginalBanknoteById
	 * <p>Description: 根据原封新券箱号及所属机构查询原封新券信息</p>
	 * @author:     wangbaozhong
	 * @param id
	 * @param officeId
	 * @return 
	 * StoOriginalBanknote    返回类型
	 */
	public StoOriginalBanknote getOriginalBanknoteById(@Param(value="id") String id, @Param(value="officeId") String officeId);
}