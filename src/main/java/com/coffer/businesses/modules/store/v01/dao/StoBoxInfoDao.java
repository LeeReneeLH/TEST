package com.coffer.businesses.modules.store.v01.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 箱袋管理DAO接口
 * 
 * @author niguoyong
 * @version 2015-09-01
 */
@MyBatisDao
public interface StoBoxInfoDao extends CrudDao<StoBoxInfo> {

	/**
	 * @author niguoyong
	 * @date 2015-09-01
	 *
	 *       根据箱袋类型取得当前最大的箱袋编号
	 */
	/* 在String boxType前加@Param("boxType") 修改人:sg 修改日期:2017-11-01 begin */
	public String getCurrentMaxBoxNo(@Param("boxType") String boxType, @Param("dbName") String dbName);
	/* end */

	/**
	 * @author niguoyong
	 * @date 2015-09-01
	 * 
	 *       根据箱号或RFID取得箱子信息
	 */
	public List<StoBoxInfo> getBoxInfoByIdOrRfid(StoBoxInfo stoBoxInfo);

	/**
	 * @author niguoyong
	 * @date 2015-09-01
	 * 
	 *       根据箱号或RFID取得箱子信息
	 */

	public List<StoBoxInfo> getBoxByIdOrRfid(StoBoxInfo stoBoxInfo);

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-01
	 * 
	 *          箱袋绑定查询接口
	 * @param headInfo
	 * @return
	 */
	public List<StoBoxInfo> searchBoxInfoList(StoBoxInfo stoBoxInfo);

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-01
	 * 
	 * 
	 * @param headInfo
	 * @return
	 */
	public int updateBoxDelflag(StoBoxInfo stoBoxInfo);

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-01
	 * 
	 *          修改箱袋状态字段
	 * @param stoBoxInfo
	 */
	public void updateStatus(StoBoxInfo stoBoxInfo);

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-01
	 * 
	 *          批量修改箱袋状态字段
	 * @param stoBoxInfo
	 */
	public int updateStatusBatch(StoBoxInfo stoBoxInfo);

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-01
	 * 
	 *          钞箱使用情况查询
	 * @param headInfo
	 * @return
	 */
	public List<StoBoxInfo> getStoreBoxDetailReport(StoBoxInfo stoBoxInfo);

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-15
	 * 
	 *          钞箱使用情况查询
	 * @param headInfo
	 * @return
	 */
	public List<StoBoxInfo> findSqlBox(StoBoxInfo stoBoxInfo);

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-01
	 * 
	 *          箱袋绑定查询接口
	 * @param headInfo
	 * @return
	 */
	public List<StoBoxInfo> findBoxAndRouteList(StoBoxInfo stoBoxInfo);

	/**
	 * 
	 * @author chengshu
	 * @version 2015-12-15
	 * 
	 *          更新箱子出库预约时间
	 * @param stoBoxInfo
	 * @return
	 */
	public int updateOutdateBatch(StoBoxInfo stoBoxInfo);

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-01
	 * 
	 *          查询指定条件的箱袋信息
	 * @param headInfo
	 * @return
	 */
	public List<StoBoxInfo> findBoxList(StoBoxInfo stoBoxInfo);

	/***
	 * 
	 * Title: getBoxInfoByRfidAndBoxNo
	 * <p>
	 * Description: 根据箱号、RFID查询箱子明细信息
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param stoBoxInfo
	 *            箱号、RIFD查询条件
	 * @return 箱子明细信息 StoBoxInfo 返回类型
	 */
	public StoBoxInfo getBoxInfoByRfidAndBoxNo(StoBoxInfo stoBoxInfo);

	/***
	 * 
	 */
	public int updateInfo(StoBoxInfo stoBoxInfo);

	/**
	 * @author sg
	 * @date 2017-11-08
	 *
	 *       根据箱袋编号查询非删除的数据
	 */
	public StoBoxInfo getATM(String boxNo);
	
	/***
	 * PDA钞箱出库(入库) 更新箱子状态
	 * @author wxz
	 * @version 2017-11-15
	 */
	public int updateAtmStatus(StoBoxInfo stoBoxInfo);
	
	/***
	 * PDA根据金库机构id查询下属网点的所有在库尾箱
	 * @author SongYuanYang
	 * @version 2017-12-8
	 */
	public List<StoBoxInfo> findTailBoxList(String officeId);
	
	/***
	 * 根据箱袋编号物理删除箱子信息
	 * @author WQJ
	 * @version 2019-1-9
	 */
	public int realDelete(String boxNo);
	
	
	
}