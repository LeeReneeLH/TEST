package com.coffer.businesses.modules.weChat.v03.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.doorOrder.v01.entity.DepositTimeAnalysis;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderDetail;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 门店预约DAO接口
 * 
 * @author Iceman
 * @version 2017-02-13
 */
@MyBatisDao
public interface DoorOrderDetailDao extends CrudDao<DoorOrderDetail> {

	public int deleteByOrderId(DoorOrderDetail doorOrderDetail);

	public int addList(List<DoorOrderDetail> doorOrderDetailList);

	public List<DoorOrderDetail> getDetailByTickerTape(@Param("tickerTape") String tickerTape);
	


	/**
	 * 根据订单编号和凭条查询信息
	 * 
	 * @param orderId
	 * @param tickerNo
	 * @author zxk
	 * @return
	 */
	public List<DoorOrderDetail> findDetailByOrderIdAndTicker(@Param("orderId") String orderId,
			@Param("tickerNo") String tickerNo);

	/**
	 * 通过id 保存(修改)图片
	 * 
	 * @param doorOrderDetail
	 * @author zxk
	 * @version 2019-8-20
	 * @return
	 */
	public int changePhotoById(DoorOrderDetail doorOrderDetail);
	/**
	 * 
	 * Title: getDepositTimeAnalysisList
	 * <p>
	 * Description: 获取存款时间分析统计报表数据
	 * </p>
	 * 
	 * @author: gzd
	 * @version 2020-01-15
	 * @param depositTimeAnalysis
	 * @return List<DepositTimeAnalysis> 返回类型
	 */
	public List<DepositTimeAnalysis> getDepositTimeAnalysisList(DepositTimeAnalysis depositTimeAnalysis);

	
	/**
	 * 根据凭条查询 凭条金额明细列表
	 * 
	 * @param tickerTape
	 * @author ZXK
	 * @version 2020-6-3
	 * @return
	 */
	public DoorOrderDetail getTickrtapeInfo(@Param("tickerTape") String tickerTape);
	
	/**
	 * 根据预约编号 查询最新业务备注
	 * 
	 * @param order_id
	 * @author HZY
	 * @version 2020-06-24
	 * @return
	 */
	public DoorOrderDetail getRemarks(@Param("order_id") String order_id);
	
	/**
	 * 根据凭条 将amount 明细存入Detail中
	 * 
	 * @param tickerTapeList
	 * @author HZY
	 * @version 2020-07-30
	 * @return
	 */
	public int updateDetailAmount(@Param("tickerTapeList") String[] tickerTapeList);
}