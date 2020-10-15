/**
 * @author WangBaozhong
 * @version 2016年5月17日
 * 
 * 
 */
package com.coffer.businesses.modules.store.v01.dao;


import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 库区物品摆放DAO
 * @author WangBaozhong
 *
 */
@MyBatisDao
public interface StoGoodsLocationInfoDao extends CrudDao<StoGoodsLocationInfo> {

	/**
	 * 根据区域ID查询区域内库存统计信息
	 * @author WangBaozhong
	 * @version 2016年5月16日
	 * 
	 *  
	 * @param areaId 区域ID
	 * @return 库存统计信息
	 */
	public StoGoodsLocationInfo findAreaStoreStatisticsInfoByAreaID(@Param(value="areaId") String areaId);
	
	/**
	 * 根据机构ID和库区类型查询库区内存放物品数量
	 * @author WangBaozhong
	 * @version 2016年5月18日
	 * 
	 *  
	 * @param officeId 机构ID
	 * @param storeAreaType 库区类型
	 * @return 存放物品数量
	 */
	public int getGoodsCountByOfficeIdAndAreaType(@Param(value="officeId") String officeId, @Param(value="storeAreaType") String storeAreaType);
	
	/**
	 * 根据库区ID查询库区内存放物品数量
	 * @author WangBaozhong
	 * @version 2016年5月18日
	 * 
	 *  
	 * @param areaId 库区ID
	 * @return 存放物品数量
	 */
	public int getGoodsCountByAreaId(@Param(value="areaId") String areaId);
	
	/**
	 * 根据物品ID 取得物品入库最小日期以及物品总数量
	 * @author WangBaozhong
	 * @version 2016年5月23日
	 * 
	 *  
	 * @param goodsId 物品ID
	 * @return 物品入库最小日期以及物品总数量
	 */
	public StoGoodsLocationInfo findMinDateAndCntByGoodsId(@Param(value="goodsId") String goodsId, @Param(value="officeId") String officeId);
	
	/**
	 * 根据物品ID和日期范围 查询物品位置信息列表
	 * @author WangBaozhong
	 * @version 2016年5月23日
	 * 
	 *  
	 * @param stoGoodsLocationInfo 物品ID和日期范围
	 * @return 物品位置信息列表
	 */
	public List<StoGoodsLocationInfo> findGoodsListByGoodsIdAndDate(StoGoodsLocationInfo stoGoodsLocationInfo);
	
	/**
	 * 根据物品ID变更物品预订状态
	 * @author WangBaozhong
	 * @version 2016年6月2日
	 * 
	 *  
	 * @param stoGoodsLocationInfo 更新信息
	 * @return 变更数量
	 */
	public int updateReserveGoodsStatus(StoGoodsLocationInfo stoGoodsLocationInfo);
	
	/**
	 * 根据RFID修改物品状态
	 * @author WangBaozhong
	 * @version 2016年6月2日
	 * 
	 *  
	 * @param stoGoodsLocationInfo 更新信息
	 * @return 变更数量
	 */
	public int updateReserveGoodsStatusByRfid(StoGoodsLocationInfo stoGoodsLocationInfo);
	
	/**
	 * 根据RFID和出入库流水单号查询物品所在库区位置信息
	 * @author WangBaozhong
	 * @version 2016年6月7日
	 * 
	 *  
	 * @param stoGoodsLocationInfo 查询条件
	 * @return 物品所在库区位置信息
	 */
	public StoGoodsLocationInfo getByAllIDAndRfid(StoGoodsLocationInfo stoGoodsLocationInfo);
	
	/**
	 * 根据RFID修改物品出库状态
	 * @author WangBaozhong
	 * @version 2016年6月8日
	 * 
	 *  
	 * @return 更新数量
	 */
	public int updateOutStoreGoodsStatusByRfid(StoGoodsLocationInfo stoGoodsLocationInfo);
	
	/**
	 * 
	 * @author LLF
	 * @version 2016年6月14日
	 * 
	 *  人行出入库任务查询接口-统计任务库区信息
	 * @param allList
	 * @return
	 */
	public List<Map<String,Object>> findStoreAreaByInStoreId(@Param(value="allList") List<PbocAllAllocateInfo> allList);

    /**
     * 
     * @author chengshu
     * @version 2016年6月14日
     * 
     *  更新RFID库区
     * @param stoGoodsLocationInfo
     * @return
     */
    public int updateRfidStoreArea(StoGoodsLocationInfo stoGoodsLocationInfo);
    
    /**
     * 
     * Title: getCountByRfid
     * <p>Description: 取得库区内相同RFID数量</p>
     * @author:     wangbaozhong
     * @param rfid
     * @param officeId 机构ID
     * @return 
     * int    返回类型
     */
    public int getCountByRfid(@Param(value="rfid") String rfid, @Param(value="officeId") String officeId);
    
    /**
     * 
     * Title: deleteByRfidAndOfficeId
     * <p>Description: 按Rfid和所属机构ID删除库区物品（初始化库区 或倒库用  将 物品审定为 清理库区 ）</p>
     * @author:     wangbaozhong
     * @param rfid
     * @param officeId 所属机构ID
     * @return 
     * int    返回类型
     */
    public int deleteByRfidAndOfficeId(@Param(value="rfid") String rfid, @Param(value="officeId") String officeId);

    /**
     * 通过原rfid号替换成新的rfid号
     * @author caixiaojie
     * @param srcRfid 原RFID编号
     * @param dstRfid 新RFID编号
     * @param userId  更新者Id
     * @param userName 更新者姓名
     * @return 更新数量
     */
    public int updateRfidInfo(@Param(value="srcRfid") String srcRfid,@Param(value="dstRfid") String dstRfid,@Param(value="userId") String userId,@Param(value="userName") String userName,@Param(value="updateDate" ) Date updateDate);
    
    /**
     * 
     * Title: getReservedGoodsCountByAreaId
     * <p>Description: 查询库区下已预订物品数量 </p>
     * @author:     wangbaozhong
     * @param storeAreaId 库区ID
     * @return 库区下已预订物品数量
     * int    返回类型
     */
    public int getReservedGoodsCountByAreaId(@Param(value="storeAreaId") String storeAreaId);
    
    /**
     * 
     * Title: updateUnUsedGoodsStatusByLocationId
     * <p>Description: 更新库区下未使用物品为清理状态</p>
     * @author:     wangbaozhong
     * @param stoGoodsLocationInfo	更新信息
     * @return 更新数量
     * int    返回类型
     */
    public int updateUnUsedGoodsStatusByLocationId(StoGoodsLocationInfo stoGoodsLocationInfo);
    
    /**
     * 
     * Title: findListForInterface
     * <p>Description: 接口用查询位置信息类表</p>
     * @author:     wangbaozhong
     * @param stoGoodsLocationInfo
     * @return 物品位置信息列表
     * List<StoGoodsLocationInfo>    返回类型
     */
    public List<StoGoodsLocationInfo> findListForInterface(StoGoodsLocationInfo stoGoodsLocationInfo);
}
