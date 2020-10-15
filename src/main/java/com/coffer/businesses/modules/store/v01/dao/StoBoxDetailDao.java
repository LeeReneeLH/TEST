package com.coffer.businesses.modules.store.v01.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.store.v01.entity.StoBoxDetail;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

@MyBatisDao
public interface StoBoxDetailDao extends CrudDao<StoBoxDetail> {
	/**
	 * 
	 * @author xp
	 * @version 2017-7-5
	 * 
	 *          箱袋明细表的接口
	 * 
	 * 
	 */
	// 覆盖父类的同名方法 修改人：xp 修改时间：2017-7-6 begin
	public int delete(String id);
	// end

	// 追加根据箱子编号删除信息的方法 修改人：xp 修改时间：2017-7-6 begin
	public int deleteByBoxNo(String id);
	// end
	
	/**
	 * 
	 * Title: getDetailListByBoxNo
	 * <p>Description: 根据箱号 获取箱子明细列表</p>
	 * @author:     wangbaozhong
	 * @param boxNo 箱号
	 * @return 
	 * List<StoBoxDetail>    返回类型
	 */
	public List<StoBoxDetail> findDetailListByBoxNo(@Param(value = "boxNo")String boxNo);
}
