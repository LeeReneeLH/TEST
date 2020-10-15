package com.coffer.businesses.modules.weChat.v03.dao;

import java.util.List;

import com.coffer.businesses.modules.weChat.v03.entity.Guest;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;


/**
 * 客户授权DAO接口
 *
 * @author qipeihong
 * @version 2017-04-18
 */
@MyBatisDao
public interface GuestDao extends CrudDao<Guest> {
    //修改授权状态
    int updategrantstatus(Guest guest);

    //通过微信号获取授权信息
    Guest getByopenID(Guest guest);

    //通过微信号修改信息
    int updateByopenId(Guest guest);

    //只通过微信号查询
    List<Guest> findListByOpenId(Guest guest);

    Guest getByUnionID(Guest guest);

}