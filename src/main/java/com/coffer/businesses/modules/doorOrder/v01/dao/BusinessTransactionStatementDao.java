package com.coffer.businesses.modules.doorOrder.v01.dao;

import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.BusinessTransactionStatement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 交易报表DAO接口
 * @author yinkai
 * @version 2020-01-09
 */
@MyBatisDao
public interface BusinessTransactionStatementDao extends CrudDao<BusinessTransactionStatement> {

    List<BusinessTransactionStatement> getTransactionList(BusinessTransactionStatement businessTransactionStatement);

    int confirm(@Param("param") BusinessTransactionStatement businessTransactionStatement);
	
}