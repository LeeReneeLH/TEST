package com.coffer.businesses.modules.store.v01.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 人员管理DAO接口
 * 
 * @author niguoyong
 * @date 2015-09-06
 */
@MyBatisDao
public interface StoEscortInfoDao extends CrudDao<StoEscortInfo> {

	/**
	 * @author niguoyong
	 * @date 2015-09-06
	 *
	 *       修改人员绑定
	 */
	public void updateBinding(StoEscortInfo stoEscortInfo);

	/**
	 * @author niguoyong
	 * @date 2015-09-06
	 *
	 *       取得所有人员信息
	 */
	public List<StoEscortInfo> findBankEscortList(String escortType);

	/**
	 * @author niguoyong
	 * @date 2015-09-06
	 *
	 *       根据身份证取得人员信息
	 */
	public StoEscortInfo findByIdcardNo(String idcardNo);

	/**
	 * @author niguoyong
	 * @date 2015-09-06
	 *
	 *       根据用户ID取得人员信息
	 */
	public StoEscortInfo findByUserId(String userId);

	/**
	 * @author niguoyong
	 * @param list
	 * @date 2015-09-06
	 *
	 *       根据人员类型取得人员信息
	 */
	public List<StoEscortInfo> findEscortInfoByUserType(@Param("searchDate") String searchDate,
			@Param("officeId") String officeId, @Param("userTypes") List<String> userTypes,
			@Param("dbName") String dbName);

	/**
	 * @author wangbaozhong
	 * @param list
	 * @date 2015-09-06
	 *
	 *       根据人员类型,机构ID 获取人行人员指纹信息
	 */
	public List<StoEscortInfo> findPbocEscortInfoByOfficeId(@Param("searchDate") String searchDate,
			@Param("officeId") String officeId, @Param("userTypes") List<String> userTypes,
			@Param("officeTypes") List<String> officeTypes, @Param("dbName") String dbName);

	/**
	 * @author niguoyong
	 * @date 2015-09-06
	 *
	 *       删除人员信息
	 */
	public int deleteById(@Param("escortId") String escortId);

	/**
	 * 
	 * @author LLF
	 * @version 2016年5月10日 获取当前人员类型最大RFID数量
	 * 
	 * @param escortType
	 * @return
	 */
	public int getSeqNo(@Param("escortType") String escortType);

	/**
	 * @author sg
	 * @param list
	 * @date 2015-09-06
	 *
	 *       根据人员类型取得人员信息
	 */
	public List<StoEscortInfo> findBankUserList(@Param("officeId") String officeId);

	/**
	 * 取得人员照片信息
	 * 
	 * @author xp
	 * @param officeId
	 * @param userType
	 * @param searchDate
	 */
	public List<StoEscortInfo> findPhotoList(@Param("officeId") String officeId, @Param("searchDate") String searchDate,
			@Param("dbName") String dbName);

	/**
	 * @author liuyaowen
	 *
	 *         修改人员绑定
	 */
	public void updateBindingRfid(StoEscortInfo stoEscortInfo);

	/**
	 * @author liuyaowen
	 *
	 *         根据rfid取得人员信息
	 */
	public StoEscortInfo findByRfid(@Param("rfid") String rfid);

	/**
	 * @author liuyaowen
	 *
	 *         根据idcardno取得人员信息
	 */
	public List<StoEscortInfo> findLikeByIdcardNo(StoEscortInfo stoEscortInfo);

	/**
	 * @author sg
	 * @date 2017-11-24
	 *
	 *       根据escortId取得未删除的人员信息
	 */
	public StoEscortInfo findByEscortId(String escortId);
}