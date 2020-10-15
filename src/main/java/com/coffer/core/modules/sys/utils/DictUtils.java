package com.coffer.core.modules.sys.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.coffer.core.common.utils.CacheUtils;
import com.coffer.core.common.utils.SpringContextHolder;
import com.coffer.core.modules.sys.dao.DictDao;
import com.coffer.core.modules.sys.entity.Dict;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 字典工具类
 * 
 * @author Clark
 * @version 2015-03-02
 */
public class DictUtils {

	private static DictDao dictDao = SpringContextHolder.getBean(DictDao.class);

	public static final String CACHE_DICT_MAP = "dictMap";

	public static String getDictLabel(String value, String type, String defaultValue) {
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(value)) {
			for (Dict dict : getDictList(type)) {
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
			for (Dict dict : getDictList(type)) {
				if (type.equals(dict.getType()) && label.equals(dict.getLabel())) {
					return dict.getValue();
				}
			}
		}
		return defaultLabel;
	}

	public static List<Dict> getDictList(String type) {
		@SuppressWarnings("unchecked")
		Map<String, List<Dict>> dictMap = (Map<String, List<Dict>>) CacheUtils.get(CACHE_DICT_MAP);
		if (dictMap == null) {
			dictMap = Maps.newHashMap();
			for (Dict dict : dictDao.findAllList(new Dict())) {
				List<Dict> dictList = dictMap.get(dict.getType());
				if (dictList != null) {
					dictList.add(dict);
				} else {
					dictMap.put(dict.getType(), Lists.newArrayList(dict));
				}
			}
			CacheUtils.put(CACHE_DICT_MAP, dictMap);
		}
		List<Dict> dictList = dictMap.get(type);
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
	public static List<Dict> getDictList(String type, boolean flag, String values) {

		List<Dict> dictList = getDictList(type);
		if (StringUtils.isBlank(values)) {
			return dictList;
		}

		List<Dict> newDictList = Lists.newArrayList();
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

	public static String getDictLabelWithCss(String value, String type, String defaultValue, boolean withCSS) {
		if (!withCSS) {
			return getDictLabel(value, type, defaultValue);
		}
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(value)) {
			for (Dict dict : getDictList(type)) {
				if (type.equals(dict.getType()) && value.equals(dict.getValue())) {
					String label = "<label";
					if (StringUtils.isNotBlank(dict.getCssStyle())) {
						label = label + " style=\"" + dict.getCssStyle() + "\"";
					}
					if (StringUtils.isNotBlank(dict.getCssClass())) {
						label = label + " class=\"" + dict.getCssClass() + "\"";
					}
					label = label + ">" + dict.getLabel() + "</label>";
					return label;
				}
			}
		}
		return defaultValue;
	}
}
