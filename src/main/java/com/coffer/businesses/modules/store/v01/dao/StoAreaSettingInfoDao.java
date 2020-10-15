/**
 * @author WangBaozhong
 * @version 2016年5月13日
 * 
 * 
 */
package com.coffer.businesses.modules.store.v01.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.store.v01.entity.StoAreaSettingInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 库房区域设定DAO
 * @author WangBaozhong
 *
 */
@MyBatisDao
public interface StoAreaSettingInfoDao extends CrudDao<StoAreaSettingInfo> {

	/**
	 * 根据库房区域ID取得区域信息
	 * @author WangBaozhong
	 * @version 2016年5月13日
	 * 
	 *  
	 * @param storeAreaId 库房区域ID
	 * @return 库房区域信息
	 */
	public StoAreaSettingInfo getByStoreAreaId(String storeAreaId);
	
	/**
     * 取得所有库区和库区内物品信息
     * @author chengshu
     * @version 2016年5月19日
     * 
     *  
     * @param sotreAred 查询条件
     * @return 库区和库区内物品信息
     */
    public List<StoAreaSettingInfo> findAreaAndGoodsNumList(StoAreaSettingInfo sotreAred);
	
	/**
	 * 根据机构ID和库区类型删除所有区域信息
	 *
	 * @author WangBaozhong
	 * @version 2016年5月16日
	 * 
	 *  
	 * @param officeId 机构ID
	 * @param storeAreaType 库区类型
	 */
	public void deleteAllByOfficeIdAndAreaType(@Param(value="officeId") String officeId, @Param(value="storeAreaType") String storeAreaType);
	
	/**
	 * 查询库区使用量
	 * @author WangBaozhong
	 * @version 2016年5月19日
	 * 
	 *  
	 * @param stoAreaSettingInfo 查询条件
	 * @return 库区使用量
	 */
	public List<StoAreaSettingInfo> findAreaActualStorageList(StoAreaSettingInfo stoAreaSettingInfo);
}
