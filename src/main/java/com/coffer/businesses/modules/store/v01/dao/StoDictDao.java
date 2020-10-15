package com.coffer.businesses.modules.store.v01.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 物品字典DAO接口
 *
 * @author Ray
 * @version 2015-09-08
 */
@MyBatisDao
public interface StoDictDao extends CrudDao<StoDict> {

    public List<StoDict> findTypeList(StoDict dict);

    /**
     * 获取特定币种的面值标签
     *
     * @param currencyValue
     * @param denValue
     * @return
     */
    String getDenLabel(@Param("currencyValue") String currencyValue, @Param("denValue") String denValue);

    /**
     * @return
     * @author LLF
     * @version 2015年10月10日
     * <p>
     * 盘点同步字典信息
     */
    List<StoDict> getStockCountDictBySearchDate(@Param("searchDate") String searchDate, @Param("dbName") String dbName);

    /**
     * 恢复
     *
     * @param entity
     * @return
     */
    int revert(StoDict dict);

    /**
     * Title: findAllListOutFlag
     * <p>Description: 查询全部数据包括被删除的</p>
     *
     * @param dict
     * @return List<StoDict>    返回类型
     * @author: wanghan
     */
    public List<StoDict> findAllListOutFlag(StoDict dict);

    /**
     * 获取币种的所有面值
     *
     * @param currencies 币种
     * @return
     */
    List<StoDict> findAllDenomination(Map<String,Object> params);

}