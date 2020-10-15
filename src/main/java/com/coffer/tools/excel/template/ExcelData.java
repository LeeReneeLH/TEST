package com.coffer.tools.excel.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel数据对象
 */
public class ExcelData {

	/**
	 * Excel参数元数据对象
	 */
	private Map<String, Object> parametersDto;

	/**
	 * Excel集合元对象
	 */
	private List<Map<String,Object>> fieldsList;
	
	/**
	 * 标题用参数对象
	 */
	private Map<String, Object> titleDto = new HashMap<String, Object>();

	/**
	 * 构造函数
	 * 
	 * @param pDto
	 *            元参数对象
	 * @param pList
	 *            集合元对象
	 */
	public ExcelData(Map<String,Object> pDto, List<Map<String,Object>> pList, Map<String, Object> titleDto) {
		setParametersDto(pDto);
		setFieldsList(pList);
		setTitleDto(titleDto);
	}

	public Map<String,Object> getParametersDto() {
		return parametersDto;
	}

	public void setParametersDto(Map<String,Object>  parametersDto) {
		this.parametersDto = parametersDto;
	}

	public List<Map<String,Object> > getFieldsList() {
		return fieldsList;
	}

	public void setFieldsList(List<Map<String,Object> > fieldsList) {
		this.fieldsList = fieldsList;
	}
	
	public void setTitleDto(Map<String, Object> titleMap) {
		this.titleDto = titleMap;
	}
	
	public Map<String, Object> getTitleMap() {
		return titleDto;
	}

}
