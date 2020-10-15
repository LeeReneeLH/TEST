package com.coffer.tools.excel.template;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.Encodes;

/**
 * Excel数据填充器
 * 
 */
public class ExcelFiller {

	private static Log log = LogFactory.getLog(ExcelFiller.class);

	/**
	 * Excel模板对象
	 */
	private ExcelTemplate excelTemplate = null;

	/**
	 * Excel数据对象
	 */
	private ExcelData excelData = null;

	/**
	 * 工作薄对象
	 */
	private Workbook wb;

	/**
	 * 工作表对象
	 */
	private Sheet wSheet;

	/**
	 * Sheet分页大小
	 */
	private static int pSize = 1000;

	/**
	 * Sheet数量
	 */
	private int sheet_Num = 0;

	/**
	 * Sheet中总的数据数
	 */
	private int total_Size = 0;

	/**
	 * 26个大写字母
	 */
	private static String[] character = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
			"P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

	/**
	 * Excel对应列字母编号
	 */
	private static List<String> charList = new ArrayList<String>();

	public ExcelFiller() {
	}

	/**
	 * 构造函数
	 * 
	 * @param pExcelTemplate
	 * @param pExcelData
	 */
	public ExcelFiller(ExcelTemplate pExcelTemplate, ExcelData pExcelData) {
		setExcelData(pExcelData);
		setExcelTemplate(pExcelTemplate);
		createCharacterList();
	}

	/**
	 * 创建Excel对应类字母列表
	 */
	private void createCharacterList() {
		int len = character.length;
		for (int i = 0; i < len; i++) {
			charList.add(character[i]);
		}
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len; j++) {
				String c = character[i] + character[j];
				charList.add(c);
			}
		}
	}

	/**
	 * 数据填充 将ExcelData填入excel模板
	 * 
	 * 
	 */
	@SuppressWarnings({ "rawtypes" })
	public void fill(HttpServletResponse response, String fileName, Workbook wb) {
		List fieldList = getExcelData().getFieldsList();
		Map<String, Object> titleMap = getExcelData().getTitleMap();
		List cFields = getExcelTemplate().getFieldObjct();
		List<String> fields = new ArrayList<String>();
		for (int i = 0; i < cFields.size(); i++) {
			Cell cell = (Cell) cFields.get(i);
			String key = cell.getStringCellValue().trim();
			fields.add(key);
		}
		total_Size = fieldList.size();
		sheet_Num = total_Size % pSize == 0 ? (total_Size / pSize) : (total_Size / pSize + 1);
		int index = 0;
		int start = 0;
		int end = total_Size / pSize <= 0 ? total_Size : pSize;
		try {
			if (ExcelConstant.EXCEL_TYPE_03.equals(Global.getConfig("excel.export.template.type"))) {
				this.wb = (HSSFWorkbook) wb;
			} else if (ExcelConstant.EXCEL_TYPE_07.equals(Global.getConfig("excel.export.template.type"))) {
				this.wb = (XSSFWorkbook) wb;
			}
			for (int i = 0; i < sheet_Num; i++, index++) {
				this.wb.cloneSheet(0);
				// this.wb.setSheetName(i, "Demo_" + (i + 1));
			}
			if (index != 0) {
				this.wb.removeSheetAt(index);
			}
			if (sheet_Num == 0) {
				sheet_Num = 1;
			}
			for (int i = 0; i < sheet_Num; i++, index++) {
				this.wSheet = wb.getSheetAt(i);
				List subList = fieldList.subList(start, end);
				fillStatics();
				fillParameters();
				// 设置列表字段内容
				fillFields(subList, fields, index);
				// 设置标题内容
				fillTitle(titleMap);
				start = end;
				end = end + ((total_Size - end) / pSize <= 0 ? (total_Size - end) : pSize);
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
	@SuppressWarnings("rawtypes")
	private void fillStatics() {
		List statics = getExcelTemplate().getStaticObject();
		for (int i = 0; i < statics.size(); i++) {
			Cell cell = (Cell) statics.get(i);
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
	@SuppressWarnings({ "rawtypes" })
	private void fillParameters() {
		List parameters = getExcelTemplate().getParameterObjct();
		Map<String, Object> parameterDto = getExcelData().getParametersDto();
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
	private void fillFields(List fieldList, List<String> fields, int index) throws Exception {
		List cFields = getExcelTemplate().getFieldObjct();
		List variables = getExcelTemplate().getVariableObject();
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
			fillVariables(cell.getRowIndex());
		}

		// 增加一行空行，用于筛选时，合计可以一直显示不消失。
		if(null != detailRow){
			wSheet.createRow(detailRow.getRowNum());			
		}
	}
	
	/**
	 * 写入静态对象
	 */
	@SuppressWarnings("rawtypes")
	private void fillTitle(Map<String, Object> titleDate) {
		List titleCells = getExcelTemplate().getTitleObjct();
		for (int i = 0; i < titleCells.size(); i++) {
			Cell cell = (Cell) titleCells.get(i);
			try {
				cell.setCellValue(String.valueOf(titleDate.get(getKey(cell.toString()))));
			} catch (Exception e) {
				log.error("写入标题对象发生错误!");
				e.printStackTrace();
			}
		}
	}

	/**
	 * 写入变量对象
	 */
	@SuppressWarnings({ "rawtypes" })
	private void fillVariables(int start) {
		
		List variables = getExcelTemplate().getVariableObject();
		Map<String, Object> parameterDto = getExcelData().getParametersDto();

		for (int i = 0; i < variables.size(); i++) {
			Cell cell = (Cell) variables.get(i);
			
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

	public ExcelTemplate getExcelTemplate() {
		return excelTemplate;
	}

	public void setExcelTemplate(ExcelTemplate excelTemplate) {
		this.excelTemplate = excelTemplate;
	}

	public ExcelData getExcelData() {
		return excelData;
	}

	public void setExcelData(ExcelData excelData) {
		this.excelData = excelData;
	}
}
