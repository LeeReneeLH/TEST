package com.coffer.businesses.modules.cloudPlatform.v04.dao;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.cloudPlatform.v04.entity.EyeCheckEscortInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;;

/**
 * 双目识别人员信息DAO接口
 * 
 * @author XL
 * @version 2018-12-07
 */
@MyBatisDao
public interface EyeCheckEscortInfoDao extends CrudDao<EyeCheckEscortInfo> {
	/**
	 * 根据身份证号和归属机构获取人员信息
	 * 
	 * @author wangqingjie
	 * @version 2018-12-07
	 */
	public EyeCheckEscortInfo getEscortFromIdcardAndOfficeId(@Param("idcardNo") String idcardNo,@Param("officeId") String officeId);

}