package com.coffer.businesses.common.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.coffer.businesses.modules.report.ReportConstant;
import com.coffer.businesses.modules.store.v01.dao.StoDictDao;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.core.common.utils.CacheUtils;
import com.coffer.core.common.utils.SpringContextHolder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 字典工具类
 * 
 * @author Clark
 * @version 2015-03-02
 */
public class GoodDictUtils {

	private static StoDictDao dictDao = SpringContextHolder.getBean(StoDictDao.class);

	public static final String CACHE_GOOD_DICT_MAP = "goodDictMap";
	public static final String CACHE_GOOD_DICT_MAP_WITH_FG = "goodDictMapWithFg";
	public static final String CACHE_GOOD_DICT_MAP_WITHOUT_FG = "goodDictMapWithOutFg";
	
	public static String getDictLabel(String value, String type, String defaultValue) {
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(value)) {
			for (StoDict dict : getDictList(type)) {
				if (type.equals(dict.getType()) && value.equals(dict.getValue())) {
					return dict.getLabel();
				}
			}
		}
		return defaultValue;
	}

	public static String getDictLabels(String values, String type, String defaultValue) {
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(values)) {
			List<String> valueList = Lists.newArrayList();
			for (String value : StringUtils.split(values, ",")) {
				valueList.add(getDictLabel(value, type, defaultValue));
			}
			return StringUtils.join(valueList, ",");
		}
		return defaultValue;
	}

	public static String getDictValue(String label, String type, String defaultLabel) {
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(label)) {
			for (StoDict dict : getDictList(type)) {
				if (type.equals(dict.getType()) && label.equals(dict.getLabel())) {
					return dict.getValue();
				}
			}
		}
		return defaultLabel;
	}

	public static List<StoDict> getDictList(String type) {
		@SuppressWarnings("unchecked")
		Map<String, List<StoDict>> dictMap = (Map<String, List<StoDict>>) CacheUtils.get(CACHE_GOOD_DICT_MAP);
		if (dictMap == null) {
			dictMap = Maps.newHashMap();
			StoDict param = new StoDict();
			param.setDelFlag(null);
			for (StoDict dict : dictDao.findList(param)) {
				List<StoDict> dictList = dictMap.get(dict.getType());
				if (dictList != null) {
					dictList.add(dict);
				} else {
					dictMap.put(dict.getType(), Lists.newArrayList(dict));
				}
			}
			CacheUtils.put(CACHE_GOOD_DICT_MAP, dictMap);
		}
		List<StoDict> dictList = dictMap.get(type);
		if (dictList == null) {
			dictList = Lists.newArrayList();
		}
		return dictList;
	}

	/**
	 * 
	 * @author LLF
	 * @version 2015年3月2日
	 * 
	 *          自定义下拉列表
	 * @param type
	 *            字典中数据类型
	 * @param flag
	 *            是否保留values数据
	 * @param values
	 *            需要保留或提出下拉列表中数据
	 * @return
	 */
	public static List<StoDict> getDictList(String type, boolean flag, String values) {

		List<StoDict> dictList = getDictList(type);
		if (StringUtils.isBlank(values)) {
			return dictList;
		}

		List<StoDict> newDictList = Lists.newArrayList();
		if (flag) {
			// 保留value值
			for (int j = 0; j < dictList.size(); j++) {
				if (values.indexOf(dictList.get(j).getValue()) >= 0) {
					newDictList.add(dictList.get(j));
				}
			}
		} else {
			// 去除value值
			for (int j = 0; j < dictList.size(); j++) {
				if (values.indexOf(dictList.get(j).getValue()) < 0) {
					newDictList.add(dictList.get(j));
				}
			}
		}
		return newDictList;
	}

	/**
	 * 获取特定币种的面值标签
	 * 
	 * @param currencyValue
	 *            币种的value
	 * @param denValue
	 *            面值的value
	 * @param defaultValue
	 *            默认值
	 * @return
	 */
	public static String getDenLabel(String currencyValue, String denValue, String defaultValue) {
		if (StringUtils.isNotBlank(currencyValue) && StringUtils.isNotBlank(denValue)) {
			return dictDao.getDenLabel(currencyValue, denValue);
		}
		return defaultValue;
	}
	
	/**
	 * @author chengshu
	 * @version 2015/09/23
	 *
	 * 设置面值种别：纸币/硬币
	 * @param currency 币种
	 * @param cash 材质
	 */
	public static String getDenominationType(String currency, String cash){
		
		// 人民币
		if(ReportConstant.Currency.RMB.equals(currency)){
			if(ReportConstant.CashType.PAPER.equals(cash)){
				return ReportConstant.DenominationType.RMB_PDEN;
			}else{
				return ReportConstant.DenominationType.RMB_HDEN;
			}

		// 美元
		}else if(ReportConstant.Currency.USD.equals(currency)){
			if(ReportConstant.CashType.PAPER.equals(cash)){
				return ReportConstant.DenominationType.USD_PDEN;
			}else{
				return ReportConstant.DenominationType.USD_HDEN;
			}

		// 欧元
		}else if(ReportConstant.Currency.EUR.equals(currency)){
			if(ReportConstant.CashType.PAPER.equals(cash)){
				return ReportConstant.DenominationType.EUR_PDEN;
			}else{
				return ReportConstant.DenominationType.EUR_HDEN;
			}

		// 英镑
		}else if(ReportConstant.Currency.GBP.equals(currency)){
			if(ReportConstant.CashType.PAPER.equals(cash)){
				return ReportConstant.DenominationType.GBP_PDEN;
			}else{
				return ReportConstant.DenominationType.GBP_HDEN;
			}

		// 日元
		}else if(ReportConstant.Currency.JPY.equals(currency)){
			if(ReportConstant.CashType.PAPER.equals(cash)){
				return ReportConstant.DenominationType.JPY_PDEN;
			}else{
				return ReportConstant.DenominationType.JPY_HDEN;
			}

		// 港币
		}else if(ReportConstant.Currency.HKD.equals(currency)){
			if(ReportConstant.CashType.PAPER.equals(cash)){
				return ReportConstant.DenominationType.HKD_PDEN;
			}else{
				return ReportConstant.DenominationType.HKD_HDEN;
			}
		}

		return "";
	}

	/**
	 * 取得字典数据标签（考虑delFlag）
	 * 
	 * @author yuxixuan
	 * @version 2016年1月19日
	 * 
	 * @param value
	 * @param type
	 * @param defaultValue
	 * @return
	 */
	public static String getDictLabelWithFg(String value, String type, String defaultValue) {
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(value)) {
			for (StoDict dict : getDictListWithFg(type)) {
				if (type.equals(dict.getType()) && value.equals(dict.getValue())) {
					return dict.getLabel();
				}
			}
		}
		return defaultValue;
	}

	/**
	 * 取得字典数据列表（考虑delFlag）
	 * 
	 * @author yuxixuan
	 * @version 2016年1月19日
	 * 
	 * @param type
	 * @return
	 */
	public static List<StoDict> getDictListWithFg(String type) {
		@SuppressWarnings("unchecked")
		Map<String, List<StoDict>> dictMap = (Map<String, List<StoDict>>) CacheUtils.get(CACHE_GOOD_DICT_MAP_WITH_FG);
		if (dictMap == null) {
			dictMap = Maps.newHashMap();
			StoDict param = new StoDict();
			for (StoDict dict : dictDao.findAllList(param)) {
				List<StoDict> dictList = dictMap.get(dict.getType());
				if (dictList != null) {
					dictList.add(dict);
				} else {
					dictMap.put(dict.getType(), Lists.newArrayList(dict));
				}
			}
			CacheUtils.put(CACHE_GOOD_DICT_MAP_WITH_FG, dictMap);
		}
		List<StoDict> dictList = dictMap.get(type);
		if (dictList == null) {
			dictList = Lists.newArrayList();
		}
		return dictList;
	}
	
	/**
	 * 
	 * Title: getDictListWithOutFg
	 * <p>Description: 取得表里的全部数据包括被删除的</p>
	 * @author:     wanghan
	 * @param type
	 * @return 
	 * List<StoDict>    返回类型
	 */
	public static List<StoDict> getDictListWithOutFg(String type) {
		@SuppressWarnings("unchecked")
		Map<String, List<StoDict>> dictMap = (Map<String, List<StoDict>>) CacheUtils.get(CACHE_GOOD_DICT_MAP_WITHOUT_FG);
		if (dictMap == null) {
			dictMap = Maps.newHashMap();
			StoDict param = new StoDict();
			for (StoDict dict : dictDao.findAllListOutFlag(param)) {
				List<StoDict> dictList = dictMap.get(dict.getType());
				if (dictList != null) {
					dictList.add(dict);
				} else {
					dictMap.put(dict.getType(), Lists.newArrayList(dict));
				}
			}
			CacheUtils.put(CACHE_GOOD_DICT_MAP_WITHOUT_FG, dictMap);
		}
		List<StoDict> dictList = dictMap.get(type);
		if (dictList == null) {
			dictList = Lists.newArrayList();
		}
		return dictList;
	}
}
