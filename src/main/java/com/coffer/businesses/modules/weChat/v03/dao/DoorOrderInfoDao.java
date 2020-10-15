package com.coffer.businesses.modules.weChat.v03.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 预约明细DAO接口
 * 
 * @author iceman
 * @version 2017-02-13
 */
@MyBatisDao
public interface DoorOrderInfoDao extends CrudDao<DoorOrderInfo> {



	/**
	 * 操作管理员密码的取得
	 * @author wl
	 * @version 2017年04月21日
	 * @param userName;
	 * @return long
	 */
	public String findManagePassword(@Param(value = "userName") String userName);

	/**
	 * 根据预约日期查询数据
	 * 
	 * @author qph
	 * @version 2017年04月21日
	 * @param orderdate,doorid;
	 * @return List
	 */
	public DoorOrderInfo getByorderDate(@Param(value = "orderdate") String orderdate,@Param(value = "doorid")String doorid);
	/**
	 * 更改总金额
	 * @author qph
	 * @version 2017年04月21日
	 * @param
	 * @return
	 */
	public int updateAmount(DoorOrderInfo doorOrderInfo);
	/**
	 * 更新预约状态（更改为已确认）
	 * @author qph
	 * @version 2017年04月25日
	 * @param
	 * @return
	 */
	public int updateStatusconfirm(DoorOrderInfo doorOrderInfo);

	/**
	 * PC端查询该门店当天是否有预约
	 * @author qph
	 * @version 2017年05月3日
	 * @param
	 * @return
	 */
	public List<DoorOrderInfo> getByorderDate1(DoorOrderInfo doorOrderInfo);

    /**
     * 获取设备余额信息
     * @author yinkai
     * @param eqpId 设备ID
     * @return
     *  AMOUNT:总金额
     *  EQUIPMENT_ID:设备ID
     *  ORDER_ID:存款单号（预约主表流水）
     *  COUNT_ZHANG:设备余额总张数（不算封包存款）
     */
	public Map<String,Object> getEquipmentBalanceInfo(@Param("eqpId") String eqpId);

    /**
     * 查找其他正在存款的设备是否有相同的包号
     * @author yinkai
     * @param bagNo 款袋编号
     * @return 款袋编号列表
     */
	public List<String> getRepeatBagNo(@Param("bagNo") String bagNo,@Param("eqpId") String eqpId);

	/**
	 * 查找当前包号是否有正在登记或确认状态的存款
	 * @author yinkai
	 * @param bagNo 款袋编号
	 * @return 款袋编号列表
	 */
	public List<DoorOrderInfo> getNonTaskDownOrder(@Param("bagNo")String bagNo);
	
	
	/**
	 * 
	 * Title: getDoorListByUser
	 * <p>
	 * Description: 根据用户获取缴存列表
	 * </p>
	 * 
	 * @author: lihe
	 * @return List<DoorOrderInfo> 返回类型
	 */
	public List<DoorOrderInfo> getDoorListByUser(DoorOrderInfo doorOrderInfo);

	/**
	 *
	 * Title: getByCondition
	 * <p>
	 * Description: 条件查询
	 * </p>
	 *
	 * @author: yinkai
	 * @return DoorOrderInfo 返回类型
	 */
	public DoorOrderInfo getByCondition(DoorOrderInfo doorOrderInfo);
	
	
	/**
     * 获取设备当前清分状态是否有'在用'
     * @author ZXK
     * @param eqpId 设备ID
     * @return
     */
	public List<DoorOrderInfo> getEquipmentStatus(@Param("eqpId") String eqpId);
	
	/**
	 * 根据机具状态获取对应存款信息
	 *
	 * Description: 
	 *
	 * @author: GJ
	 * @param info
	 * @return
	 */
	public List<DoorOrderInfo> getInfoByEqpIds(DoorOrderInfo info);
}

