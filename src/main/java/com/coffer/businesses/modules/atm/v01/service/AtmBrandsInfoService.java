package com.coffer.businesses.modules.atm.v01.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.atm.v01.dao.AtmBrandsInfoDao;
import com.coffer.businesses.modules.atm.v01.dao.AtmInfoMaintainDao;
import com.coffer.businesses.modules.atm.v01.entity.AtmBrandsInfo;
import com.coffer.businesses.modules.atm.v01.entity.AtmInfoMaintain;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.CacheUtils;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.google.common.collect.Lists;

/**
 * ATM品牌参数管理Service
 * 
 * @author wxz
 * @version 2017-11-02
 */
@Service
@Transactional(readOnly = true)
public class AtmBrandsInfoService extends CrudService<AtmBrandsInfoDao, AtmBrandsInfo> {
	
	//缓存常量
	public static final String CACHE_ATMBRAND_MAP = "atmBrandMap";
	public static final String CACHE_ATMTYPE_MAP = "atmTypeMap";

	/**
	 * 持久层对象
	 */
	@Autowired
	protected AtmBrandsInfoDao atmBrandsInfoDao;
	@Autowired
	protected AtmInfoMaintainDao atmInfoMaintainDao;

	public AtmBrandsInfo get(String id) {
		return super.get(id);
	}

	public List<AtmBrandsInfo> findList(AtmBrandsInfo atmBrandsInfo) {
		return super.findList(atmBrandsInfo);
	}

	public Page<AtmBrandsInfo> findPage(Page<AtmBrandsInfo> page, AtmBrandsInfo atmBrandsInfo) {
		return super.findPage(page, atmBrandsInfo);
	}

	@Transactional(readOnly = false)
	public void save(AtmBrandsInfo atmBrandsInfo) {
		super.save(atmBrandsInfo);
		// 清理缓存
		CacheUtils.remove(CACHE_ATMBRAND_MAP);
		CacheUtils.remove(CACHE_ATMTYPE_MAP);
	}

	@Transactional(readOnly = false)
	public void delete(AtmBrandsInfo atmBrandsInfo) {
		super.delete(atmBrandsInfo);
		// 清理缓存
		CacheUtils.remove(CACHE_ATMBRAND_MAP);
		CacheUtils.remove(CACHE_ATMTYPE_MAP);
	}

	/**
	 * 获取ATM品牌信息列表
	 * 
	 * @author wxz
	 * @version 2017年11月02日
	 * 
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AtmBrandsInfo> getAtmBrandsinfoList() {
		// 获取缓存
		List<AtmBrandsInfo> atmBrands = (List<AtmBrandsInfo>) CacheUtils.get(CACHE_ATMBRAND_MAP);
		List<String> atmBrandsNo = Lists.newArrayList();
		List<AtmBrandsInfo> list = Lists.newArrayList();
		if (atmBrands == null || Collections3.isEmpty(atmBrands)) {
			// 获取不重复的ATM品牌信息列表
			atmBrands = atmBrandsInfoDao.findDistinctAtmBrandList(new AtmBrandsInfo());
			// 遍历ATM品牌信息
			for(AtmBrandsInfo atm : atmBrands){
				// 品牌名称拼接 (格式 ： 品牌名称：品牌编号)
				atm.setAtmBrandsName(atm.getAtmBrandsName() + " : " +atm.getAtmBrandsNo());
			}
			if (atmBrands != null && !Collections3.isEmpty(atmBrands)) {
				for (AtmBrandsInfo atmBrandsInfo : atmBrands) {
					if (atmBrandsInfo != null) {
						// 获取品牌编号
						String atmBrandNo = atmBrandsInfo.getAtmBrandsNo();
						if (!atmBrandsNo.contains(atmBrandNo)) {
							atmBrandsNo.add(atmBrandNo);
							list.add(atmBrandsInfo);
						}
					}
				}
				atmBrands.clear();
				atmBrands.addAll(list);
				CacheUtils.put(CACHE_ATMBRAND_MAP, atmBrands);
			}

		}
		return atmBrands;
	}

	/**
	 * 获取ATM型号信息列表
	 * 
	 * @author wxz
	 * @version 2017年11月02日
	 * 
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AtmBrandsInfo> getAtmTypesinfoList() {
		// 获取缓存
		List<AtmBrandsInfo> atmTypes = (List<AtmBrandsInfo>) CacheUtils.get(CACHE_ATMTYPE_MAP);

		if (atmTypes == null || Collections3.isEmpty(atmTypes)) {
			// 获取不重复的ATM型号信息列表
			atmTypes = atmBrandsInfoDao.findDistinctAtmTypeList(new AtmBrandsInfo());
			
			if (atmTypes == null) {
				atmTypes = Lists.newArrayList();
			} else {
				// 遍历ATM型号信息
				for(AtmBrandsInfo atm : atmTypes){
					// 型号名称拼接 (格式 ： 型号名称：型号编号)
					atm.setAtmTypeName(atm.getAtmTypeName() + " : " +atm.getAtmTypeNo());
				}
			}
			CacheUtils.put(CACHE_ATMTYPE_MAP, atmTypes);
		}

		return atmTypes;
	}
	
	/**
	 * 品牌型号表的数据一致性验证
	 * @author wxz
	 * @version 2017-11-02
	 * @param atmBrandsInfo
	 */
	public void checkVersion(AtmBrandsInfo atmBrandsInfo) {

		// 数据一致性验证(获取数据)
		AtmBrandsInfo oldData = get(atmBrandsInfo.getId());

		// 判断数据是否被删除
		if (!Constant.deleteFlag.Invalid.equals(oldData.getDelFlag())) {
			// 获取更新时间的查询结果并格式化
			String oldUpdateDate = DateUtils.formatDate(oldData.getUpdateDate(),
					Constant.Dates.FORMATE_YYYYMMDDHHMMSSSSSSSS);
			// 判断两次时间是否相等
			if (!oldUpdateDate.equals(atmBrandsInfo.getStrUpdateDate())) {
				throw new BusinessException("message.E4012", "", new String[] { oldData.getAtmBrandsNo() });
			}
		} else {
			throw new BusinessException("message.E4013", "", new String[] { oldData.getAtmBrandsNo() });
		}
	}
	
	/**
	 * 获取ATM型号信息列表
	 * 
	 * @author wxz
	 * @version 2017年12月16日
	 * 
	 * 
	 * @return
	 */
	public List<AtmBrandsInfo> findDistinctAtmTypeList(AtmBrandsInfo atmBrandsInfo){
		return atmBrandsInfoDao.findDistinctAtmTypeList(atmBrandsInfo);
	}
	
	/**
	 * 品牌型号删除判断(查询要删除的品牌型号是否已创建ATM机)
	 * @author wxz
	 * @version 2018年1月4日
	 * @param atmInfoMaintain
	 * @return
	 */
	public List<AtmInfoMaintain> findByNo(AtmInfoMaintain atmInfoMaintain){
		return atmInfoMaintainDao.findByNo(atmInfoMaintain);
	}

	/**
	 * 根据ATM机编号获取品牌
	 * 
	 * @author XL
	 * @version 2018年01月05日
	 * @param atmId
	 * @return
	 */
	public AtmBrandsInfo findByAtmNo(String atmId) {
		List<AtmBrandsInfo> atmBrandsInfoList = atmBrandsInfoDao.findByAtmNo(atmId);
		if (Collections3.isEmpty(atmBrandsInfoList)) {
			return null;
		}
		return atmBrandsInfoList.get(0);
	}

}