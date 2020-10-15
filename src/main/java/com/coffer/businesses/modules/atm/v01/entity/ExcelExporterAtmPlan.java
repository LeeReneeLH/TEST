package com.coffer.businesses.modules.atm.v01.entity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.Encodes;
import com.coffer.core.common.utils.Reflections;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.tools.excel.template.ExpressUtils;
import com.coffer.tools.excel.templateex.ExpressUtilsEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 导出加钞计划
 * 
 * @author xl
 */
public class ExcelExporterAtmPlan {

	private static Logger log = LoggerFactory.getLogger(ExcelExporterAtmPlan.class);
	/** 每个sheet的名称 */
	public static final String SHEET_NAME_MAP_KEY = "sheetName";
	
	/** 每个sheet 标题 Map key**/
	public static final String SHEET_TITLE_MAP_KEY = "titleMap";
	
	/** 每个sheet 参数 Map key**/
	public static final String SHEET_PARAMETER_MAP_KEY = "parameterMap";
	
	/** 每个sheet 数据 Map key**/
	public static final String SHEET_DATA_LIST_MAP_KEY = "dataList";
	/** 每个sheet 数据 实体类名 Map key**/
	public static final String SHEET_DATA_ENTITY_CLASS_NAME_KEY = "dataEntityClassName";
	/** 每个sheet 数据 实体注解 Map key**/
	private static final String SHEET_DATA_ENTITY_ANNOTATION_LIST_KEY = "dataEntityAnnotationList";
	
	private static final String SHEET_STATIC_OBJECT_MAP_KEY = "staticObject";
	
	private static final String SHEET_PARAMETER_OBJECT_MAP_KEY = "parameterObjct";
	
	private static final String SHEET_FIELD_OBJECT_MAP_KEY = "fieldObjct";
	
	private static final String SHEET_VARIABLE_OBJECT_MAP_KEY = "variableObject";
	
	private static final String SHEET_TITLE_OBJECT_MAP_KEY = "titleObjct";

	private static final String SHEET_FIELD_DATA_LIST_MAP_KEY = "fieldDataList";
	
	/** workbook 中各sheet的数据*/
	private List<Map<String, Object>> outputdataInfoList = Lists.newArrayList();
	
	/**
	 * Sheet分页大小
	 */
	private static final int pSize = 1000;
	
	/**
	 * 工作薄对象
	 */
	private Workbook wb;
	
	private Map<String, Map<String, List<Cell>>> templateSheetObjectMap = Maps.newHashMap();
	
	/**
	 * Excel对应列字母编号
	 */
	private static List<String> charList = new ArrayList<String>();
	
	public ExcelExporterAtmPlan(List<Map<String, Object>> paramList) {
		
		for (Map<String, Object> tempMap : paramList) {
			if (tempMap.get(SHEET_DATA_ENTITY_CLASS_NAME_KEY) == null || "".equals(tempMap.get(SHEET_DATA_ENTITY_CLASS_NAME_KEY).toString())) {
				log.error("SHEET_DATA_ENTITY_CLASS_NAME_KEY 未设定！");
				continue;
			}
			String className = tempMap.get(SHEET_DATA_ENTITY_CLASS_NAME_KEY).toString();
			try {
				Class<?> cls = Class.forName(className);
				tempMap.putAll(filterAnnotationList(cls, 1));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				log.error(className + "：类信息不存在！");
				continue;
			}
		}
		this.outputdataInfoList = paramList;
	}
	
	/**
	 * 解析Excel模板
	 * 
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	public void createWorkBook(HttpServletRequest request,HttpServletResponse response, String templatePath, String templatefileName) {
		if (StringUtils.isEmpty(templatePath)) {
			log.error("Excel模板路径不能为空!");
		}

		InputStream is = request.getSession().getServletContext().getResourceAsStream(templatePath + templatefileName);

		if (ExpressUtilsEx.isEmpty(is)) {
			log.error("未找到模板文件,请确认模板路径是否正确[" + templatePath + "]");
		}

		try {
			this.wb = WorkbookFactory.create(is);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.setDataList();
		
		this.fill(response, templatefileName);
	}
	
	/**
	 * 
	 * Title: getCellObjFromSheet
	 * <p>
	 * Description: 取得单元格对象数据
	 * </p>
	 * 
	 * @author:xl
	 * @param sheet
	 * @return Map<String,List<Cell>> 返回类型
	 */
	private Map<String, List<Cell>> getCellObjFromSheet(Sheet sheet) {

		List<Cell> staticObject = null;
		List<Cell> parameterObjct = null;
		List<Cell> fieldObjct = null;
		List<Cell> variableObject = null;
		List<Cell> titleObject = null;

		Map<String, List<Cell>> cellObjectMap = Maps.newHashMap();
		if (ExpressUtilsEx.isNotEmpty(sheet)) {
			staticObject = Lists.newArrayList();
			parameterObjct = Lists.newArrayList();
			fieldObjct = Lists.newArrayList();
			variableObject = Lists.newArrayList();
			titleObject = Lists.newArrayList();

			int rows = sheet.getLastRowNum();
			for (int rowIndex = 0; rowIndex <= rows; rowIndex++) {
				if (null == sheet.getRow(rowIndex)) {
					continue;
				}
				Iterator<Cell> cells = sheet.getRow(rowIndex).cellIterator();
				while (cells.hasNext()) {
					Cell cell = cells.next();

					String cellContent = "";

					if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
						cellContent = cell.getStringCellValue().trim();
					} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
						cell.getNumericCellValue();
					}

					if (!StringUtils.isEmpty(cellContent)) {
						if (cellContent.indexOf("$P") != -1 || cellContent.indexOf("$p") != -1) {
							parameterObjct.add(cell);
						} else if (cellContent.indexOf("$F") != -1 || cellContent.indexOf("$f") != -1) {
							// 追加列表用的字段
							fieldObjct.add(cell);
						} else if (cellContent.indexOf("$T") != -1 || cellContent.indexOf("$t") != -1) {
							// 追加标题用的字段
							titleObject.add(cell);
						} else if (cellContent.indexOf("$V") != -1 || cellContent.indexOf("$v") != -1) {
							variableObject.add(cell);
						} else {
							staticObject.add(cell);
						}
					}
				}
			}

			cellObjectMap.put(SHEET_STATIC_OBJECT_MAP_KEY, staticObject);
			cellObjectMap.put(SHEET_PARAMETER_OBJECT_MAP_KEY, parameterObjct);
			cellObjectMap.put(SHEET_FIELD_OBJECT_MAP_KEY, fieldObjct);
			cellObjectMap.put(SHEET_VARIABLE_OBJECT_MAP_KEY, variableObject);
			cellObjectMap.put(SHEET_TITLE_OBJECT_MAP_KEY, titleObject);

			templateSheetObjectMap.put(sheet.getSheetName(), cellObjectMap);

		} else {
			log.error("Sheet Name：" + sheet.getSheetName() + "， 工作表对象不能为空!");
		}

		return cellObjectMap;

	}

	/**
	 * 
	 * Title: filterAnnotationList
	 * <p>
	 * Description: 过滤注解
	 * </p>
	 * 
	 * @author: xl
	 * @param cls
	 *            实体对象，通过annotation.ExportField获取标题
	 * @param type
	 *            导出类型（1:导出数据；2：导出模板）
	 * @param groups
	 *            导入分组
	 * @return List<Object[]> 返回类型
	 */
	private Map<String, Object> filterAnnotationList(Class<?> cls, int type, int... groups) {
		
		Map<String, Object> rtnMap = Maps.newHashMap();
		
		List<Object[]> annotationList = Lists.newArrayList();

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
	
		rtnMap.put(SHEET_DATA_ENTITY_ANNOTATION_LIST_KEY, annotationList);
		
		return rtnMap;
	}
	
	
	/**
	 * 添加数据（通过annotation.ExportField添加数据）
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void setDataList() {
		List<Map<String, Object>> fieldslist = null;
		
		for (Map<String, Object> sheetDataInfo : outputdataInfoList) {
			
			List<Object> dataList = (List<Object>)sheetDataInfo.get(SHEET_DATA_LIST_MAP_KEY);
			List<Object[]> annotationList = (List<Object[]>)sheetDataInfo.get(SHEET_DATA_ENTITY_ANNOTATION_LIST_KEY);
			fieldslist = Lists.newArrayList();
			for (Object e : dataList) {
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
			sheetDataInfo.put(SHEET_FIELD_DATA_LIST_MAP_KEY, fieldslist);
		}
		
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
	 * 数据填充 将ExcelData填入excel模板
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void fill(HttpServletResponse response, String fileName) {
		
		try {
			
			for (Map<String, Object> infoMap : outputdataInfoList) {
				String sheetName = com.coffer.core.common.utils.StringUtils.toString(infoMap.get(SHEET_NAME_MAP_KEY));
				
				int sheetNo = this.wb.getSheetIndex(sheetName);
				
				Sheet sheet = this.wb.getSheetAt(sheetNo);
				
				// Sheet中总的数据数
				int total_Size = 0;
				int sheet_Num = 0;
				if (infoMap.get(SHEET_FIELD_DATA_LIST_MAP_KEY) == null) {
					log.error(" fieldDataList is null");
					continue;
				}
				List<Map<String, Object>> fieldList = (List<Map<String, Object>>)infoMap.get(SHEET_FIELD_DATA_LIST_MAP_KEY);
				if (Collections3.isEmpty(fieldList)) {
					log.error(" fieldDataList is null");
					continue;
				}
				Map<String, Object> titleMap = null;
				if (infoMap.get(SHEET_TITLE_MAP_KEY) == null) {
					log.debug(" titleMap is null");
					titleMap = Maps.newHashMap();
				} else {
					titleMap = (Map<String, Object>)infoMap.get(SHEET_TITLE_MAP_KEY);
				}
				
				Map<String, List<Cell>> cellObjectMap = this.getCellObjFromSheet(sheet);
				
				if (cellObjectMap == null) {
					log.error(" cellObjectMap is null");
					continue;
				}
				
				Map<String, Object> parameterDto = null;
				
				if (infoMap.get(SHEET_PARAMETER_MAP_KEY) == null) {
					parameterDto = Maps.newHashMap();
				} else {
					parameterDto = (Map<String, Object>)infoMap.get(SHEET_PARAMETER_MAP_KEY);
				}
				
				List<Cell> statcObj = cellObjectMap.get(SHEET_STATIC_OBJECT_MAP_KEY);
				
				if (Collections3.isEmpty(statcObj)) {
					log.debug(" cFields is null");
					continue;
				}
				
				List<Cell> cFields = cellObjectMap.get(SHEET_FIELD_OBJECT_MAP_KEY);
				
				if (Collections3.isEmpty(cFields)) {
					cFields = Lists.newArrayList();
				}
				
				List<Cell> parameterObj = cellObjectMap.get(SHEET_PARAMETER_OBJECT_MAP_KEY);
				if (Collections3.isEmpty(parameterObj)) {
					parameterObj = Lists.newArrayList();
				}
				
				
				List<String> fields = new ArrayList<String>();
				for (Cell cell : cFields) {
					String key = cell.getStringCellValue().trim();
					fields.add(key);
				}
				
				total_Size = fieldList.size();
				sheet_Num = total_Size % pSize == 0 ? (total_Size / pSize) : (total_Size / pSize + 1);
				int start = 0;
				int end = total_Size / pSize <= 0 ? total_Size : pSize;
				
				fillStatics(cFields);

				fillParameters(sheet, parameterObj, parameterDto);
				
				List<Cell> titleObj = cellObjectMap.get(SHEET_TITLE_OBJECT_MAP_KEY);
				if (Collections3.isEmpty(titleObj)) {
					titleObj = Lists.newArrayList();
				}
				
				// 设置标题内容
				fillTitle(titleMap, titleObj);
				
				if (sheet_Num > 1) {
					for (int i = 0; i < (sheet_Num - 1); i++) {
						this.wb.cloneSheet(sheetNo);
					}
				}
				
				
				if (sheet_Num == 0) {
					sheet_Num = 1;
				}
				for (int i = 0; i < sheet_Num; i++) {
					if (i > 0) {
						sheet = wb.getSheet(sheetName + " (" + (i + 1) + ")");
					}
					List<Map<String, Object>> subList = fieldList.subList(start, end);
					
					Map<String, List<Cell>> tempCellObjectMap = this.getCellObjFromSheet(sheet);
					
					List<Cell> variableObj = tempCellObjectMap.get(SHEET_VARIABLE_OBJECT_MAP_KEY);
					
					List<Cell> fieldObj = tempCellObjectMap.get(SHEET_FIELD_OBJECT_MAP_KEY);
					
					if (Collections3.isEmpty(fieldObj)) {
						fieldObj = Lists.newArrayList();
					}
					// 设置列表字段内容
					fillFields(sheet, subList, fields, fieldObj,
							variableObj, parameterDto);
					
					
					start = end;
					end = end + ((total_Size - end) / pSize <= 0 ? (total_Size - end) : pSize);
				}
					
			}
			this.write(response, fileName);
		} catch (Exception e) {
			log.error("基于模板生成可写工作表出错了!");
			e.printStackTrace();
		}
	}
	
	/**
	 * 写入静态对象
	 */
	private void fillStatics(List<Cell> statics) {
		for (Cell cell: statics) {
			try {
				cell.setCellValue(cell.getStringCellValue().trim());
			} catch (Exception e) {
				log.error("写入静态对象发生错误!");
				e.printStackTrace();
			}
		}
	}

	/**
	 * 写入参数对象
	 */
	private void fillParameters(Sheet wSheet, List<Cell> parameters, Map<String, Object> parameterDto) {
		for (int i = 0; i < parameters.size(); i++) {
			Cell cell = (Cell) parameters.get(i);
			Row row = wSheet.getRow(cell.getRowIndex());
			Cell newCell = row.getCell(cell.getColumnIndex());
			String key = getKey(cell.getStringCellValue().trim());
			try {
				this.setCellValue(newCell, parameterDto.get(key));
			} catch (Exception e) {
				log.error("写入表格参数对象发生错误!");
				e.printStackTrace();
			}
		}
	}

	/**
	 * 写入表格字段对象
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void fillFields(Sheet wSheet, List fieldList, List<String> fields, List<Cell> cFields, List<Cell> variables, Map<String, Object> parameterDto) throws Exception {
		Row detailRow = null;
		for (int j = 0; j < fieldList.size(); j++) {
			Map<String, Object> field = (Map<String, Object>) fieldList.get(j);
			Cell cell = (Cell) cFields.get(0);
			
			if (j != 0) {
				if (ExpressUtils.isNotEmpty(variables)) {
					wSheet.shiftRows(cell.getRowIndex() + j, wSheet.getLastRowNum(), 1);
					detailRow = wSheet.createRow(cell.getRowIndex() + j);
					variables.clear();
					Iterator cells = wSheet.getRow(cell.getRowIndex() + j + 1).cellIterator();
					while (cells.hasNext()) {
						Cell vCell = (Cell) cells.next();
						String cellContent = vCell.getStringCellValue().trim();
						if (!StringUtils.isEmpty(cellContent)) {
							if (cellContent.indexOf("$V") != -1 || cellContent.indexOf("$v") != -1) {
								variables.add(vCell);
							}
						}
					}
				} else {
					detailRow = wSheet.createRow(cell.getRowIndex() + j);
				}
			}
			for (int i = 0; i < cFields.size(); i++) {
				cell = (Cell) cFields.get(i);
				Cell newCell = null;
				if (j != 0) {
					newCell = detailRow.createCell(cell.getColumnIndex());
					CellStyle style = wb.createCellStyle();
					style.cloneStyleFrom(cell.getCellStyle());
					newCell.setCellStyle(style);
				} else {
					newCell = wSheet.getRow(cell.getRowIndex()).getCell(cell.getColumnIndex());
				}
				String key = getKey(fields.get(i));
				List cList = new ArrayList();
				try {
					if (ExpressUtils.isEmpty(field.get(key)) && ExpressUtils.isFormula(key)) {
						String type = getType(fields.get(i));
						for (int f = 0; f < fields.size(); f++) {
							if (!getKey(fields.get(f)).equals(key)) {
								String fType = getType(fields.get(f));
								if (ExpressUtils.isNotEmpty(fType) && type.equalsIgnoreCase(type)) {
									cList.add(f);
								}
							}

						}
						String formula = key + "(";
						for (int f = 0; f < cList.size(); f++) {
							String cc = charList.get((int) cList.get(f));
							if (f == (cList.size() - 1)) {
								formula += cc + (newCell.getRowIndex() + 1) + ")";
							} else {
								formula += cc + (newCell.getRowIndex() + 1) + ",";
							}
						}
						newCell.setCellType(Cell.CELL_TYPE_FORMULA);
						newCell.setCellFormula(formula);
					} else {
						this.setCellValue(newCell, field.get(key));
					}
				} catch (Exception e) {
					log.error("写入表格字段对象发生错误!");
					e.printStackTrace();
				}
			}
		}

		int row = 0;
		row += fieldList.size();
		if (ExpressUtils.isEmpty(fieldList)) {
			if (ExpressUtils.isNotEmpty(cFields)) {
				Cell cell = (Cell) cFields.get(0);
				row = cell.getRowIndex();
				wSheet.removeRow(wSheet.createRow(row + 5));
				wSheet.removeRow(wSheet.createRow(row + 4));
				wSheet.removeRow(wSheet.createRow(row + 3));
				wSheet.removeRow(wSheet.createRow(row + 2));
				wSheet.removeRow(wSheet.createRow(row + 1));
				wSheet.removeRow(wSheet.createRow(row));
			}
		} else {
			
			Cell cell = (Cell) cFields.get(0);
			fillVariables(wSheet, cell.getRowIndex(), variables, parameterDto);
		}
		Row rows1;
		// 增加一行空行，用于筛选时，合计可以一直显示不消失。
		if(null != detailRow){
			rows1 = createRow(wSheet, detailRow.getRowNum() + 1);

		} else {
			rows1 = createRow(wSheet, 5);
		}
		Cell cells1 = rows1.createCell(0);
		Cell cells2 = rows1.createCell(4);
		cells1.setCellValue("制表人:");
		cells2.setCellValue("复核人:");
	}
	
	/**
	 * 找到需要插入的行数，并新建一个POI的row对象
	 * 
	 * @param sheet
	 * @param rowIndex
	 * @return
	 */
	private Row createRow(Sheet sheet, Integer rowIndex) {
		Row row = null;
		if (sheet.getRow(rowIndex) != null) {
			int lastRowNo = sheet.getLastRowNum();
			sheet.shiftRows(rowIndex, lastRowNo, 1);
		}
		row = sheet.createRow(rowIndex);
		return row;
	}
	
	/**
	 * 写入静态对象
	 */
	private void fillTitle(Map<String, Object> titleMap, List<Cell> titleCells) {
		for (Cell cell : titleCells) {
			try {
				cell.setCellValue(String.valueOf(titleMap.get(getKey(cell.toString()))));
			} catch (Exception e) {
				log.error("写入标题对象发生错误!");
				e.printStackTrace();
			}
		}
	}

	/**
	 * 写入变量对象
	 */
	private void fillVariables(Sheet wSheet, int start, List<Cell> variables, Map<String, Object> parameterDto) {
		
		for (Cell cell : variables) {
			
			Cell newCell = wSheet.getRow(cell.getRowIndex()).getCell(cell.getColumnIndex());
			String key = getKey(cell.getStringCellValue().trim());
			try {
				if (ExpressUtils.isEmpty(parameterDto.get(key))) {
					String cc = charList.get(newCell.getColumnIndex());
					//newCell.setCellType(Cell.CELL_TYPE_FORMULA);
					if("SUBTOTAL".equals(key)){
						newCell.setCellFormula(key + "(109," + cc + (start + 1) + ":" + cc + (cell.getRowIndex()) + ")");
					}else{
						newCell.setCellFormula(key + "(" + cc + (start + 1) + ":" + cc + (cell.getRowIndex()) + ")");
					}
					
				} else {
					this.setCellValue(newCell, parameterDto.get(key));
				}

			} catch (Exception e) {
				log.error("写入表格变量对象发生错误!");
				e.printStackTrace();
			}

		}
	}
	
	/**
	 * 获取模板键名
	 * 
	 * @param pKey
	 *            模板元标记
	 * @return 键名
	 */
	private static String getKey(String pKey) {
		String key = null;
		int index = pKey.indexOf(":");
		if (!ExpressUtils.isEmpty(pKey)) {
			if (index == -1) {
				key = pKey.substring(3, pKey.length() - 1);
			} else {
				key = pKey.substring(3, index);
			}
		}
		return key;
	}

	/**
	 * 获取模板类型名称
	 * 
	 * @param pType
	 * @return
	 */
	private static String getType(String pType) {
		String type = null;
		int index = pType.indexOf(":");
		if (ExpressUtils.isNotEmpty(pType)) {
			if (index != -1) {
				type = pType.substring(index + 1, index + 2);
			}
		}
		return type;
	}

	/**
	 * 判断类型设置单元格的值
	 * 
	 * @param cell
	 * @param val
	 */
	private void setCellValue(Cell cell, Object val) {
		try {
//			NumberFormat nf = NumberFormat.getInstance(Locale.CHINA);
			if (val == null) {
				cell.setCellValue("");
			} else if (val instanceof String) {
				cell.setCellValue((String) val);
			} else if (val instanceof Integer) {
				cell.setCellValue(Double.valueOf(val.toString()));
			} else if (val instanceof Long) {
				cell.setCellValue(Double.valueOf(val.toString()));
			} else if (val instanceof Double) {
				cell.setCellValue(Double.valueOf(val.toString()));
			} else if (val instanceof Float) {
				cell.setCellValue(Double.valueOf(val.toString()));
			} else if (val instanceof BigDecimal) {
				cell.setCellValue(Double.valueOf(val.toString()));
			} else if (val instanceof Date) {
				CellStyle style = cell.getCellStyle();
				DataFormat format = wb.createDataFormat();
				style.setDataFormat(format.getFormat("yyyy-MM-dd HH:mm:ss"));
				cell.setCellValue((Date) val);
			}
		} catch (Exception ex) {
			cell.setCellValue(val.toString());
		}
	}
	
	/**
	 * 输出到客户端
	 * 
	 * @param fileName
	 *            输出文件名
	 */
	public void write(HttpServletResponse response, String fileName) throws IOException {
		response.reset();
		response.setContentType("application/octet-stream; charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
		write(response.getOutputStream());
	}

	/**
	 * 输出数据流
	 * 
	 * @param os
	 *            输出数据流
	 */
	public void write(OutputStream os) throws IOException {
		wb.write(os);
	}
	
	/**
	 * 输出到文件
	 * 
	 * @param fileName
	 *            输出文件名
	 */
	public ExcelExporterAtmPlan writeFile(String name) throws FileNotFoundException, IOException {
		FileOutputStream os = new FileOutputStream(name);
		this.write(os);
		return this;
	}
	
//	/**
//	 * 导出测试
//	 */
//	public static void main(String[] args) throws Throwable {
//		List<Map<String, Object>> paramList = Lists.newArrayList();
//
//		// sheet 1
//		Map<String, Object> dataMap = Maps.newHashMap();
//		dataMap.put(SHEET_TITLE_MAP_KEY, "wang");
//
//		List<StoInfoReportEntity> wangList = Lists.newArrayList();
//		StoInfoReportEntity wangUser = new StoInfoReportEntity();
//		wangUser.setOfficeId("wang");
//		wangUser.setFilterCondition("wangbaozhong");
//
//		wangList.add(wangUser);
//		dataMap.put(SHEET_DATA_LIST_MAP_KEY, wangList);
//
//		dataMap.put(SHEET_DATA_ENTITY_CLASS_NAME_KEY, StoInfoReportEntity.class.getName());
//
//		paramList.add(dataMap);
//
//		// sheet 2
//		dataMap = Maps.newHashMap();
//		dataMap.put(SHEET_TITLE_MAP_KEY, "li");
//		List<StoInfoReportEntity> liList = Lists.newArrayList();
//		StoInfoReportEntity liUser = new StoInfoReportEntity();
//		liUser.setOfficeId("li");
//		liUser.setFilterCondition("liming");
//
//		liList.add(liUser);
//
//		dataMap.put(SHEET_DATA_LIST_MAP_KEY, liList);
//
//		dataMap.put(SHEET_DATA_ENTITY_CLASS_NAME_KEY, StoInfoReportEntity.class.getName());
//
//		paramList.add(dataMap);
//
//		ExcelExporterEx ee = new ExcelExporterEx(paramList);
//
//		ee.createWorkBook();
//
//		ee.writeFile("target/export.xls");
//
//		log.debug("Export success.");
//
//	}

	
}
