package com.coffer.businesses.modules.store.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.store.v01.entity.StoBlankBillSelect;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.businesses.modules.store.v01.entity.StoRelevance;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 物品关联配置DAO接口
 * @author yuxixuan
 * @version 2015-09-11
 */
@MyBatisDao
public interface StoRelevanceDao extends CrudDao<StoRelevance> {

	/**
	 * 保存物品关联
	 * 
	 * @param stoRelevance
	 */
	void saveRelevance(StoRelevance stoRelevance);
	
	/**
	 * 删除物品关联（mysql用）
	 * 
	 * @param stoRelevance
	 */
	void delRelevanceMysql(StoRelevance stoRelevance);

	/**
	 * 保存物品关联（mysql用）
	 * 
	 * @param stoRelevance
	 */
	void saveRelevanceMysql(StoRelevance stoRelevance);

	/**
	 * 查询物品关联
	 * 
	 * @param stoRelevance
	 * @return
	 */
	List<StoRelevance> findListRelevance(StoRelevance stoRelevance);

	/**
	 * 根据groupId取得物品关联
	 * 
	 * @param groupId
	 * @return
	 */
	StoRelevance getRelevance(String groupId);

	/**
	 * 根据groupId删除物品关联
	 * 
	 * @param groupId
	 * @return
	 */
	void deleteRelevance(StoRelevance stoRelevance);

	/**
	 * 获取物品关联币种
	 * 
	 * @author yuxixuan
	 * @return
	 */
	List<StoDict> getReleCurrencyList(StoRelevance stoRelevance);

	/**
	 * 获取物品关联类别
	 * 
	 * @author yuxixuan
	 * @return
	 */
	List<StoDict> getReleClassificationList(StoRelevance stoRelevance);

	/**
	 * 获取物品关联套别
	 * 
	 * @author yuxixuan
	 * @return
	 */
	List<StoDict> getReleSetsList(StoRelevance stoRelevance);

	/**
	 * 获取物品关联材质
	 * 
	 * @author yuxixuan
	 * @return
	 */
	List<StoDict> getReleCashList(StoRelevance stoRelevance);

	/**
	 * 获取物品关联面值
	 * 
	 * @author yuxixuan
	 * @return
	 */
	List<StoDict> getReleDenominationList(StoRelevance stoRelevance);

	/**
	 * 获取物品关联单位
	 * 
	 * @author yuxixuan
	 * @return
	 */
	List<StoDict> getReleUnitList(StoRelevance stoRelevance);

	/**
	 * 根据币种和材质，获取面值选项
	 * 
	 * @param stoRelevance
	 * @return
	 */
	List<StoDict> getDenOptions(StoRelevance stoRelevance);

	/**
	 * 根据材质，获取单位选项
	 * 
	 * @param stoRelevance
	 * @return
	 */
	List<StoDict> getUnitOptions(StoRelevance stoRelevance);

	/**
	 * 获取字典表重空分类
	 * 
	 * @author yuxixuan
	 * @param stoBlankBillSelect
	 * @return
	 */
	List<StoDict> getBlankBillKindList(StoBlankBillSelect stoBlankBillSelect);

	/**
	 * 获取字典表重空类型
	 * 
	 * @author yuxixuan
	 * @param stoBlankBillSelect
	 * @return
	 */
	List<StoDict> getBlankBillTypeList(StoBlankBillSelect stoBlankBillSelect);

	/**
	 * 获取物品表重空分类
	 * 
	 * @author yuxixuan
	 * @param stoBlankBillSelect
	 * @return
	 */
	List<StoDict> getBlbiKindList(StoBlankBillSelect stoBlankBillSelect);

	/**
	 * 获取物品表重空类型
	 * 
	 * @author yuxixuan
	 * @param stoBlankBillSelect
	 * @return
	 */
	List<StoDict> getBlbiTypeList(StoBlankBillSelect stoBlankBillSelect);
}