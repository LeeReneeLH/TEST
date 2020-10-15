package com.coffer.tools.excel.template;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.coffer.core.common.utils.Reflections;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.utils.DictUtils;

/**
 * Excel导出器
 * 
 */
public class ExcelExporter {

	private static Logger log = LoggerFactory.getLogger(ExcelExporter.class);

	/**
	 * Excel模板路径
	 */
	private String templatePath;

	/**
	 * 参数对象
	 */
	private Map<String, Object> parametersDto = new HashMap<String, Object>();

	/**
	 * 字段对象
	 */
	private List<Map<String, Object>> fieldsList = new ArrayList<Map<String, Object>>();

	/**
	 * 注解列表（Object[]{ ExcelField, Field/Method }）
	 */
	private List<Object[]> annotationList = Lists.newArrayList();
	
	/**
	 * 标题用参数对象
	 */
	private Map<String, Object> titleDto = new HashMap<String, Object>();

	/**
	 * 构造函数
	 * 
	 * @param cls
	 *            实体对象，通过annotation.ExportField获取标题
	 */
	public ExcelExporter(Class<?> cls) {
		this(cls, 1);
	}

	/**
	 * 构造函数
	 * 
	 * @param cls
	 *            实体对象，通过annotation.ExportField获取标题
	 * @param type
	 *            导出类型（1:导出数据；2：导出模板）
	 * @param groups
	 *            导入分组
	 */
	public ExcelExporter(Class<?> cls, int type, int... groups) {
		// Get annotation field
		Field[] fs = cls.getDeclaredFields();
		for (Field f : fs) {
			ExcelField ef = f.getAnnotation(ExcelField.class);
			if (ef != null && (ef.type() == 0 || ef.type() == type)) {
				if (groups != null && groups.length > 0) {
					boolean inGroup = false;
					for (int g : groups) {
						if (inGroup) {
							break;
						}
						for (int efg : ef.groups()) {
							if (g == efg) {
								inGroup = true;
								annotationList.add(new Object[] { ef, f });
								break;
							}
						}
					}
				} else {
					annotationList.add(new Object[] { ef, f });
				}
			}
		}
		// Get annotation method
		Method[] ms = cls.getDeclaredMethods();
		for (Method m : ms) {
			ExcelField ef = m.getAnnotation(ExcelField.class);
			if (ef != null && (ef.type() == 0 || ef.type() == type)) {
				if (groups != null && groups.length > 0) {
					boolean inGroup = false;
					for (int g : groups) {
						if (inGroup) {
							break;
						}
						for (int efg : ef.groups()) {
							if (g == efg) {
								inGroup = true;
								annotationList.add(new Object[] { ef, m });
								break;
							}
						}
					}
				} else {
					annotationList.add(new Object[] { ef, m });
				}
			}
		}
		// Field sorting
		Collections.sort(annotationList, new Comparator<Object[]>() {
			public int compare(Object[] o1, Object[] o2) {
				return new Integer(((ExcelField) o1[0]).sort()).compareTo(new Integer(((ExcelField) o2[0]).sort()));
			};
		});
	}

	/**
	 * 添加数据（通过annotation.ExportField添加数据）
	 * 
	 * @return list 数据列表
	 */
	public <E> ExcelExporter setDataList(List<E> list) {
		List<Map<String, Object>> fieldslist = new ArrayList<Map<String, Object>>();
		for (E e : list) {
			Map<String, Object> fieldMap = new HashMap<String, Object>();
			for (Object[] os : annotationList) {
				ExcelField ef = (ExcelField) os[0];
				String fieldName = null;
				Object val = null;
				// Get entity value
				try {
					if (StringUtils.isNotBlank(ef.value())) {
						val = Reflections.invokeGetter(e, ef.value());
						fieldName = ef.value();
					} else {
						if (os[1] instanceof Field) {
							val = Reflections.invokeGetter(e, ((Field) os[1]).getName());
							fieldName = ((Field) os[1]).getName();
						} else if (os[1] instanceof Method) {
							val = Reflections.invokeMethod(e, ((Method) os[1]).getName(), new Class[] {},
									new Object[] {});
							fieldName = ((Method) os[1]).getName().substring(3);
						}
					}
					// If is dict, get dict label
					if (StringUtils.isNotBlank(ef.dictType())) {
						val = DictUtils.getDictLabel(val == null ? "" : val.toString(), ef.dictType(), "");
					}
				} catch (Exception ex) {
					// Failure to ignore
					log.info(ex.toString());
					val = "";
				}
				val = this.getFieldValue(val, ef.fieldType());
				fieldMap.put(fieldName.toLowerCase(), val);
			}
			fieldslist.add(fieldMap);
		}
		this.setFieldsList(fieldslist);
		return this;
	}

	/**
	 * 获取反射字段值(根据fieldType 字段判断)
	 * 
	 * @param val
	 * @param fieldType
	 * @return
	 */
	private Object getFieldValue(Object val, Class<?> fieldType) {
		try {
			if (val == null || val instanceof String || val instanceof Integer || val instanceof Long
					|| val instanceof Double || val instanceof Float || val instanceof BigDecimal
					|| val instanceof Date) {
				return val;
			} else {
				if (fieldType != Class.class) {
					val = fieldType.getMethod("setValue", Object.class).invoke(null, val);
				} else {
					val = Class
							.forName(
									this.getClass()
											.getName()
											.replaceAll(this.getClass().getSimpleName(),
													"fieldtype." + val.getClass().getSimpleName() + "Type"))
							.getMethod("setValue", Object.class).invoke("setValue", val);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return val;
	}

	/**
	 * 设置数据
	 * 
	 * @param pDto
	 *            参数集合
	 * @param pList
	 *            字段集合
	 */
	public void setData(Map<String, Object> pDto, List<Map<String, Object>> pList) {
		parametersDto = pDto;
		fieldsList = pList;
	}

	/**
	 * 导出Excel
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	public void export(HttpServletRequest request, HttpServletResponse response, String fileName) {
		ExcelData excelData = new ExcelData(parametersDto, fieldsList, titleDto);
		ExcelTemplate excelTemplate = new ExcelTemplate();
		excelTemplate.setTemplatePath(getTemplatePath());
		Workbook wb = excelTemplate.parse(request);
		ExcelFiller excelFiller = new ExcelFiller(excelTemplate, excelData);
		excelFiller.fill(response, fileName, wb);
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	public Map<String, Object> getParametersDto() {
		return parametersDto;
	}

	public void setParametersDto(Map<String, Object> parametersDto) {
		this.parametersDto = parametersDto;
	}

	public List<Map<String, Object>> getFieldsList() {
		return fieldsList;
	}

	public void setFieldsList(List<Map<String, Object>> fieldsList) {
		this.fieldsList = fieldsList;
	}

	public void setFieldMap(Map<String, Object> fieldMap) {
		this.fieldsList.add(fieldMap);
	}
	
	public void setTitleMap(Map<String, Object> titleMap) {
		this.titleDto = titleMap;
	}
	
	public Map<String, Object> getTitleMap(Map<String, Object> titleMap) {
		return titleDto;
	}
}
