package com.coffer.businesses.modules.doorOrder.v01.dao;


import com.coffer.businesses.modules.doorOrder.v01.entity.SaveType;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import org.apache.ibatis.annotations.Param;


/**
 * 款项类型DAO接口
 *
 * @author zhaohaoran
 * @version 2019-07-15
 */
@MyBatisDao
public interface SaveTypeDao extends CrudDao<SaveType> {

    SaveType getByTypeCodeAndMerchantId(@Param("typeCode") String typeCode, @Param("merchantId") String merchantId);
}
