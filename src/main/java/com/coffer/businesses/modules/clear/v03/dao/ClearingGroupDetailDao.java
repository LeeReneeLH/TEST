package com.coffer.businesses.modules.clear.v03.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.clear.v03.entity.ClearingGroup;
import com.coffer.businesses.modules.clear.v03.entity.ClearingGroupDetail;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 清分组管理明细DAO接口
 * 
 * @author XL
 * @version 2017-08-14
 */
@MyBatisDao
public interface ClearingGroupDetailDao extends CrudDao<ClearingGroupDetail> {

	/**
	 * 物理删除明细
	 * 
	 * @author XL
	 * @version 2017年8月14日
	 * @param clearingGroupDetail
	 * @return
	 */
	public int deletePhysical(ClearingGroupDetail clearingGroupDetail);

	/**
	 * 根据分组id查明细
	 * 
	 * @author XL
	 * @version 2017年8月23日
	 * @param groupId
	 * @return
	 */
	public List<ClearingGroupDetail> getByGroupId(String groupId);

	/**
	 * 查询分组未删除的明细
	 * 
	 * @author XL
	 * @version 2017年9月18日
	 * @param clearingGroup
	 * @return
	 */
	public List<ClearingGroupDetail> findListExist(ClearingGroup clearingGroup);

	/**
	 * 根据清分人员id查询明细
	 * 
	 * @author XL
	 * @version 2017年9月19日
	 * @param clearingGroup
	 * @return
	 */
	public ClearingGroupDetail getClGroupDetailByUser(@Param("clearGroupId") String clearGroupId,
			@Param("id") String id,
			@Param("groupType") String groupType, @Param("delFlag") String delFlag);

}