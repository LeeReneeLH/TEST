package com.coffer.businesses.modules.atm.v01.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.CommonUtils;
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
import com.coffer.core.common.utils.StringUtils;
import com.google.common.collect.Lists;

/**
 * ATM机信息维护Service
 * 
 * @author wxz
 * @version 2017-11-3
 */
@Service
@Transactional(readOnly = true)
public class AtmInfoMaintainService extends CrudService<AtmInfoMaintainDao, AtmInfoMaintain> {

	public static final String CACHE_ATMBRAND_MAP = "atmBrandMap";
	public static final String CACHE_ATMTYPE_MAP = "atmTypeMap";

	/**
	 * 持久层对象
	 */
	@Autowired
	protected AtmInfoMaintainDao atmInfoMaintainDao;
	@Autowired
	protected AtmBrandsInfoDao atmBrandsInfoDao;

	public AtmInfoMaintain get(String id) {
		return super.get(id);
	}

	public List<AtmInfoMaintain> findList(AtmInfoMaintain atmInfoMaintain) {
		return super.findList(atmInfoMaintain);
	}

	public Page<AtmInfoMaintain> findPage(Page<AtmInfoMaintain> page, AtmInfoMaintain atmInfoMaintain) {
		return super.findPage(page, atmInfoMaintain);
	}

	@Transactional(readOnly = false)
	public void save(AtmInfoMaintain atmInfoMaintain) {
		atmInfoMaintain.setRfid(getRfid(atmInfoMaintain));
		// 添加品牌名称
		atmInfoMaintain.setAtmBrandsName(atmInfoMaintainDao.getAtmName(atmInfoMaintain).getAtmBrandsName());
		// 添加型号名称
		atmInfoMaintain.setAtmTypeName(atmInfoMaintainDao.getAtmName(atmInfoMaintain).getAtmTypeName());
		super.save(atmInfoMaintain);
		// 清理缓存
		CacheUtils.remove(CACHE_ATMBRAND_MAP);
		CacheUtils.remove(CACHE_ATMTYPE_MAP);
	}

	@Transactional(readOnly = false)
	public void delete(AtmInfoMaintain atmInfoMaintain) {
		super.delete(atmInfoMaintain);
		// 清理缓存
		CacheUtils.remove(CACHE_ATMBRAND_MAP);
		CacheUtils.remove(CACHE_ATMTYPE_MAP);
	}

	/**
	 * 获取ATM品牌信息列表
	 * 
	 * @author wxz
	 * @version 2017年11月3日
	 * 
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AtmInfoMaintain> getAtmInfoMaintainList() {
		// 获取缓存
		List<AtmInfoMaintain> atmBrands = (List<AtmInfoMaintain>) CacheUtils.get(CACHE_ATMBRAND_MAP);
		List<String> atmBrandsNo = Lists.newArrayList();
		List<AtmInfoMaintain> list = Lists.newArrayList();
		if (atmBrands == null || Collections3.isEmpty(atmBrands)) {
			// 获取不重复的ATM品牌信息列表
			atmBrands = atmInfoMaintainDao.findDistinctAtmBrandList(new AtmInfoMaintain());
			if (atmBrands != null && !Collections3.isEmpty(atmBrands)) {
				for (AtmInfoMaintain atmInfoMaintain : atmBrands) {
					if (atmInfoMaintain != null) {
						// 获取品牌编号
						String atmBrandNo = atmInfoMaintain.getAtmBrandsNo();
						if (!atmBrandsNo.contains(atmBrandNo)) {
							atmBrandsNo.add(atmBrandNo);
							list.add(atmInfoMaintain);
						}
					}
				}
				// 清除缓存
				atmBrands.clear();
				atmBrands.addAll(list);
				// 重新添加缓存
				CacheUtils.put(CACHE_ATMBRAND_MAP, atmBrands);
			}

		}
		return atmBrands;
	}

	/**
	 * 获取ATM型号信息列表
	 * 
	 * @author wxz
	 * @version 2017年11月3日
	 * 
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AtmInfoMaintain> getAtmTypesinfoList() {
		// 获取缓存
		List<AtmInfoMaintain> atmTypes = (List<AtmInfoMaintain>) CacheUtils.get(CACHE_ATMTYPE_MAP);

		if (atmTypes == null || Collections3.isEmpty(atmTypes)) {
			// 获取不重复的ATM型号信息列表
			atmTypes = atmInfoMaintainDao.findDistinctAtmTypeList(new AtmInfoMaintain());
			if (atmTypes == null) {
				atmTypes = Lists.newArrayList();
			}
			CacheUtils.put(CACHE_ATMTYPE_MAP, atmTypes);
		}

		return atmTypes;
	}

	/**
	 * 根据选择的品牌编号，获取联动品牌名称和联动型号编号
	 * 
	 * @author wxz
	 * @version 2017年11月3日
	 */
	public List<AtmBrandsInfo> findAtmTypeNameNo(AtmBrandsInfo atmBrandsInfo) {
		return atmBrandsInfoDao.findAtmTypeNameNo(atmBrandsInfo);
	}

	/**
	 * 生成ATM rfid号
	 * 
	 * @author wxz
	 * @version 2017年11月6日
	 */
	public String getRfid(AtmInfoMaintain atmInfoMaintain) {
		// 初始化回收箱
		String backBoxType = null;
		// 初始化取款箱
		String getBoxType = null;
		// 初始化存款箱
		String depositBoxType = null;
		// 初始化循环箱
		String cycleBoxType = null;

		// 获取品牌编号(取前四位)
		String atmBrandsNo = atmInfoMaintain.getAtmBrandsNo().substring(0, 4);
		
		AtmBrandsInfo atmBrands = new AtmBrandsInfo();
		atmBrands.setAtmTypeNo(atmInfoMaintain.getAtmTypeNo());

		// 根据型号编号获取详细信息
		List<AtmBrandsInfo> dataList = findAtmTypeNameNo(atmBrands);
		for (AtmBrandsInfo atmBrandsInfo : dataList) {
			// 判断回收箱是否存在(如果存在set 1 不存在 set 0)
			if (StringUtils.isNotBlank(atmBrandsInfo.getBackBoxType())) {
				backBoxType = atmBrandsInfo.getBackBoxType().substring(5);
			} else {
				backBoxType = "0";
			}
			// 判断取款箱是否存在(如果存在set 1 不存在 set 0)
			if (StringUtils.isNotBlank(atmBrandsInfo.getGetBoxType())) {
				getBoxType = atmBrandsInfo.getGetBoxType().substring(5);
			} else {
				getBoxType = "0";
			}
			// 判断存款箱是否存在(如果存在set 1 不存在 set 0)
			if (StringUtils.isNotBlank(atmBrandsInfo.getDepositBoxType())) {
				depositBoxType = atmBrandsInfo.getDepositBoxType().substring(5);
			} else {
				depositBoxType = "0";
			}
			// 判断循环箱是否存在(如果存在set 1 不存在 set 0)
			if (StringUtils.isNotBlank(atmBrandsInfo.getCycleBoxType())) {
				cycleBoxType = atmBrandsInfo.getCycleBoxType().substring(5);
			} else {
				cycleBoxType = "0";
			}
		}

		// 获取ATM机编号，如果位数不够8位进行左补齐
		String atmId = CommonUtils.fillSeqNo(Integer.parseInt(atmInfoMaintain.getAtmId()), 8);

		return atmBrandsNo + backBoxType + getBoxType + depositBoxType + cycleBoxType + atmId;
	}

	/**
	 * ATM机维护信息表的数据一致性验证
	 * 
	 * @param atmInfoMaintain
	 */
	public void checkVersion(AtmInfoMaintain atmInfoMaintain) {

		// 数据一致性验证(获取数据)
		AtmInfoMaintain oldData = get(atmInfoMaintain.getId());

		// 判断数据是否被删除
		if (!Constant.deleteFlag.Invalid.equals(oldData.getDelFlag())) {
			// 获取更新时间的查询结果并格式化
			String oldUpdateDate = DateUtils.formatDate(oldData.getUpdateDate(),
					Constant.Dates.FORMATE_YYYYMMDDHHMMSSSSSSSS);
			// 判断两次时间是否相等
			if (!oldUpdateDate.equals(atmInfoMaintain.getStrUpdateDate())) {
				throw new BusinessException("message.E4010", "", new String[] { oldData.getAtmId() });
			}
		} else {
			throw new BusinessException("message.E4011", "", new String[] { oldData.getAtmId() });
		}
	}

	/**
	 * @author sg 箱袋查询接口根据条件查询ATM机信息
	 * @param atmInfoMaintain
	 * @return
	 */
	public List<AtmInfoMaintain> searchAtmInfoMaintain(AtmInfoMaintain atmInfoMaintain) {
		return atmInfoMaintainDao.findList(atmInfoMaintain);
	}

	/**
	 * 根据ATM机rfid，获取ATM型号信息
	 * 
	 * @author XL
	 * @version 2017年11月21日
	 * @param rfid
	 * @return
	 */
	public AtmInfoMaintain findByAtmId(String rfid) {
		AtmInfoMaintain atmInfoMaintain = new AtmInfoMaintain();
		atmInfoMaintain.setRfid(rfid);
		return atmInfoMaintainDao.findByAtmId(atmInfoMaintain);
	}
	
	/**
	 * 根据atmId(ATM机编号)，获取ATM型号信息
	 * 
	 * @author wanglu
	 * @version 2017年11月24日
	 * @param atmNo
	 * @return
	 */
	public AtmInfoMaintain findInfoByAtmId(String atmNo) {
		AtmInfoMaintain atmInfoMaintain = new AtmInfoMaintain();
		atmInfoMaintain.setAtmId(atmNo);
		return atmInfoMaintainDao.findByAtmId(atmInfoMaintain);
	}

	/**
	 * 根据ATM机编号和柜员编号，获取ATM型号信息列表
	 * 
	 * @author XL
	 * @version 2017年12月19日
	 * @param atmInfoMaintain
	 * @return
	 */
	public List<AtmInfoMaintain> findByAtmNoAndTellerId(AtmInfoMaintain atmInfoMaintain) {
		return atmInfoMaintainDao.findByAtmNoAndTellerId(atmInfoMaintain);
	}
}