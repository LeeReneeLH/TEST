package com.coffer.tools.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;

/**
 * Excl导出管理
 * @author LF
 * @version 2014-12-22
 */
public class ImprotExcelManager {

	/**
	 * Excel 2003
	 */
	private final static String XLS = "xls";
	/**
	 * Excel 2007
	 */
	private final static String XLSX = "xlsx";

	/**
	 * 
	 * @author LF
	 * @version 2014-12-23
	 * 
	 * @Description 由Excel文件的Sheet导出至List
	 * @param file
	 *            导出文件
	 * @param startRow
	 *            导出起始行
	 * @param keyValue
	 *            导出指定列
	 * @return
	 * @throws IOException
	 */
	public static List<Map<String, Object>> exportListFromExcel(File file, int startRow, String... keyValue)
			throws IOException {
		return exportListFromExcel(new FileInputStream(file), FilenameUtils.getExtension(file.getName()), startRow,
				keyValue);
	}

	/**
	 * 
	 * @author LF
	 * @version 2014-12-23
	 * 
	 * @Description 由Excel流的Sheet导出至List
	 * @param is
	 *            导出数据写出流
	 * @param extensionName
	 *            文件扩展名
	 * @param startRow
	 *            导出起始行
	 * @param keyValue
	 *            导出数据指定列
	 * @return
	 * @throws IOException
	 */
	private static List<Map<String, Object>> exportListFromExcel(InputStream is, String extensionName, int startRow,
			String... keyValue) throws IOException {

		Workbook workbook = null;

		if (extensionName.toLowerCase().equals(XLS)) {
			workbook = new HSSFWorkbook(is);
		} else if (extensionName.toLowerCase().equals(XLSX)) {
			workbook = new XSSFWorkbook(is);
		}
		// 获取sheet页数
		int sheetNumber = workbook.getNumberOfSheets();
		List<Map<String, Object>> list = Lists.newArrayList();
		// 获取sheet页数据
		for (int sheetNum = 0; sheetNum < sheetNumber; sheetNum++) {
			// 当前sheet页导出list数据
			List<Map<String, Object>> sheetList = exportListFromExcel(workbook, sheetNum, startRow, keyValue);
			// 将sheet数据整合整体list里
			if (!Collections3.isEmpty(sheetList)) {
				list.addAll(sheetList);
			}
		}
		return list;
	}

	/**
	 * 
	 * @author LF
	 * @version 2014-12-23
	 * 
	 * @Description 由指定的Sheet导出至List
	 * @param workbook
	 *            工作薄
	 * @param sheetNum
	 *            当前sheet页
	 * @param startRow
	 *            导出起始行
	 * @param keyValue
	 *            导出列Key值
	 * @return
	 */
	private static List<Map<String, Object>> exportListFromExcel(Workbook workbook, int sheetNum, int startRow,
			String... keyValue) {

		Sheet sheet = workbook.getSheetAt(sheetNum);

		// 解析公式结果
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

		List<Map<String, Object>> list = Lists.newArrayList();

		int minRowIx = startRow;
		// int minRowIx = sheet.getFirstRowNum();
		int maxRowIx = sheet.getLastRowNum();
		for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) {
			Map<String, Object> rowMap = Maps.newHashMap();
			Row row = sheet.getRow(rowIx);
			// 获取空白行
			if (row == null) {
				continue;
			}
			short minColIx = row.getFirstCellNum();
			short maxColIx = row.getLastCellNum();
			// 指定导出数据列
			if (keyValue != null && keyValue.length > 0) {
				maxColIx = (short) (keyValue.length - 1);
			}
			for (short colIx = minColIx; colIx <= maxColIx; colIx++) {
				// 获取索引
				int index = new Integer(colIx);
				// 取得单元格
				Cell cell = row.getCell(index);
				CellValue cellValue = evaluator.evaluate(cell);
				// 判断单元格内容是否为空
				if (cellValue == null) {
					continue;
				}
				// 获取map主键，无主键用index代替
				String key = String.valueOf(index);
				if (keyValue != null && index <= keyValue.length - 1) {
					key = keyValue[index];
				}
				// 过滤空主键
				if (StringUtils.isBlank(key)) {
					continue;
				}
				switch (cellValue.getCellType()) {
				case Cell.CELL_TYPE_BOOLEAN:
					rowMap.put(key, cellValue.getBooleanValue());
					break;
				case Cell.CELL_TYPE_NUMERIC:
					// 这里的日期类型会被转换为数字类型，需要判别后区分处理
					if (DateUtil.isCellDateFormatted(cell)) {
						rowMap.put(key, cell.getDateCellValue());
					} else {
						rowMap.put(key, cellValue.getNumberValue());
					}
					break;
				case Cell.CELL_TYPE_STRING:
					rowMap.put(key, cellValue.getStringValue());
					break;
				case Cell.CELL_TYPE_FORMULA:
					break;
				case Cell.CELL_TYPE_BLANK:
					break;
				case Cell.CELL_TYPE_ERROR:
					break;
				default:
					break;
				}
			}
			list.add(rowMap);
		}
		return list;
	}

}
