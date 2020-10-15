package com.coffer.tools.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFCellUtil;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.RegionUtil;

@SuppressWarnings("deprecation")
public class MSExcelManager {

	/**
	 * 工作薄对象
	 */
	private HSSFWorkbook wb;

	/**
	 * 工作表对象
	 */
	private HSSFSheet sheet;

	/**
	 * 样式列表
	 */
	private Map<String, HSSFCellStyle> styles;

	/**
	 * Excel導出文件路徑
	 */
	private static String targetFile = "E:\\workspace\\ExportExcelTest.xls";

	/**
	 * 当前行号
	 */
	private int rownum;

	public MSExcelManager() {

	}

	public MSExcelManager(String title) {
		this.initialize(title);
	}

	/**
	 * 初始化函数
	 * 
	 * @param title
	 *            表格标题，传“空值”，表示无标题
	 * 
	 */
	private void initialize(String title) {
		this.wb = new HSSFWorkbook();
		this.sheet = wb.createSheet("Export");
		this.styles = createStyles(wb);
		// Create title
		if (StringUtils.isNotBlank(title)) {
			HSSFRow titleRow = this.addRow(rownum++);
			titleRow.setHeightInPoints(30);
			HSSFCell titleCell = titleRow.createCell(0);
			titleCell.setCellStyle(styles.get("title"));
			titleCell.setCellValue(title);
			this.mergedRegion(titleRow.getRowNum(), titleRow.getRowNum(),
					titleRow.getRowNum(), 47);
		}
		for (int i = 0; i < 48; i++) {
			sheet.setColumnWidth(i, 1000);
		}

		List<CellRangeAddress> list = new ArrayList<CellRangeAddress>();

		// Create header
		HSSFRow headerOneRow = this.addRow(1);
		headerOneRow.setHeight((short) 1000);
		String[] headerOneList = { "序\r\n号", "客\r\n户\r\n类\r\n型",
				"商\r\n户\r\n名\r\n称", "商\r\n户\r\n账\r\n号", "门\r\n店\r\n名\r\n称",
				"门\r\n店\r\n代\r\n号", "上\r\n门\r\n收\r\n款\r\n类\r\n别",
				"上\r\n门\r\n收\r\n款\r\n次\r\n数", "上门收款金额明细",
				"金\r\n库\r\n清\r\n点\r\n人", "签\r\n约\r\n门\r\n店\r\n支\r\n行", "备\r\n注" };
		for (int i = 0; i < 8; i++) {
			this.addCell(headerOneRow, i, headerOneList[i],
					styles.get("headerV"));
		}

		list.add(this.mergedRegion(1, 6, 0, 0));
		list.add(this.mergedRegion(1, 6, 1, 1));
		list.add(this.mergedRegion(1, 6, 2, 2));
		list.add(this.mergedRegion(1, 6, 3, 3));
		list.add(this.mergedRegion(1, 6, 4, 4));
		list.add(this.mergedRegion(1, 6, 5, 5));
		list.add(this.mergedRegion(1, 6, 6, 6));
		list.add(this.mergedRegion(1, 6, 7, 7));

		this.addCell(headerOneRow, 8, headerOneList[8], styles.get("header"));
		list.add(this.mergedRegion(1, 1, 8, 44));

		this.addCell(headerOneRow, 45, headerOneList[9], styles.get("headerV"));
		this.addCell(headerOneRow, 46, headerOneList[10], styles.get("headerV"));
		this.addCell(headerOneRow, 47, headerOneList[11], styles.get("headerV"));
		list.add(this.mergedRegion(1, 6, 45, 45));
		list.add(this.mergedRegion(1, 6, 46, 46));
		list.add(this.mergedRegion(1, 6, 47, 47));

		// ----------------------------------------------------------------------------------

		HSSFRow headerTwoRow = this.addRow(2);
		// headerTwoRow.setHeight((short) 1000);
		String[] headerTwoList = { "纸币", "硬币", "金\r\n额\r\n合\r\n计" };

		this.addCell(headerTwoRow, 8, headerTwoList[0], styles.get("header"));
		this.addCell(headerTwoRow, 32, headerTwoList[1], styles.get("header"));
		this.addCell(headerTwoRow, 44, headerTwoList[2], styles.get("headerV"));

		list.add(this.mergedRegion(2, 2, 8, 31));
		list.add(this.mergedRegion(2, 2, 32, 43));
		list.add(this.mergedRegion(2, 6, 44, 44));

		// -----------------------------------------------------------------------------------

		HSSFRow headerThereRow = this.addRow(3);
		headerThereRow.setHeight((short) 1000);
		String[] headerThreeList = { "100元", "50元", "20元", "10元", "5元", "2元",
				"1元", "5角", "1角", "5分", "2分", "1分", "1元", "5角", "1角", "5分",
				"2分", "1分" };
		for (int i = 0, j = 0; i < headerThreeList.length; i++, j += 2) {
			this.addCell(headerThereRow, j + 8, headerThreeList[i],
					styles.get("header"));
		}

		for (int i = 0, j = 0; i < headerThreeList.length; i++, j += 2) {
			list.add(this.mergedRegion(3, 4, j + 8, j + 9));
		}
		// -----------------------------------------------------------------------------------

		HSSFRow headerFourRow = this.addRow(5);
		headerFourRow.setHeight((short) 1000);
		String[] headerFourList = { "数\r\n量", "金\r\n额" };
		for (int i = 0, j = 0; i < headerThreeList.length; i++, j += 2) {
			this.addCell(headerFourRow, j + 8, headerFourList[0],
					styles.get("headerV"));
			this.addCell(headerFourRow, j + 9, headerFourList[1],
					styles.get("headerV"));
		}

		for (int i = 0; i < (headerThreeList.length * headerFourList.length); i++) {
			list.add(this.mergedRegion(5, 6, i + 8, i + 8));
		}

		// 合并所有单元格后，给单元格添加边框，否则会产生覆盖异常
		for (int i = 0; i < list.size(); i++) {
			CellRangeAddress region = list.get(i);
			try {
				this.setBorder(region);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取HSSFWorkbook对象
	 * 
	 * @return
	 */
	public HSSFWorkbook getWB() {
		return wb;
	}

	/**
	 * 合并单元格
	 * 
	 * @param rowFrom
	 * @param rowTo
	 * @param colFrom
	 * @param colTo
	 */
	private CellRangeAddress mergedRegion(int rowFrom, int rowTo, int colFrom,
			int colTo) {
		CellRangeAddress region = new CellRangeAddress(rowFrom, rowTo, colFrom,
				colTo);
		sheet.addMergedRegion(region);
		return region;
	}

	/**
	 * 设置合并单元格边框
	 * 
	 * @param cellRangeAddress
	 * @throws Exception
	 */
	private void setBorder(CellRangeAddress cellRangeAddress) throws Exception {
		RegionUtil.setBorderLeft(styles.get("border").getBorderLeft(),
				cellRangeAddress, sheet, wb);
		RegionUtil.setBorderBottom(styles.get("border").getBorderBottom(),
				cellRangeAddress, sheet, wb);
		RegionUtil.setBorderRight(styles.get("border").getBorderRight(),
				cellRangeAddress, sheet, wb);
		RegionUtil.setBorderTop(styles.get("border").getBorderTop(),
				cellRangeAddress, sheet, wb);
	}

	/**
	 * 在每个字符后追加回车符
	 * 
	 * @param inputStr
	 * @return
	 */
	private String getStringWithEnter(String inputStr) {
		String[] inputStrs = inputStr.split("");
		String stringWithEnter = "";
		for (int x = 0; x < inputStrs.length; x++) {
			stringWithEnter += inputStrs[x] + "\r\n";
		}
		return stringWithEnter;
	}

	/**
	 * 创建行和单元格
	 * 
	 * @param currentRow
	 * @param rowNum
	 * @param colNum
	 * @param style
	 */

	private void createRow(int currentRow, int rowNum, int colNum,
			HSSFCellStyle style) {
		for (int rowIndex = currentRow; rowIndex < rowNum; rowIndex++) {
			HSSFRow row = sheet.createRow(rowIndex);
			for (int cellIndex = 0; cellIndex < colNum; cellIndex++) {
				HSSFCell cell = row.createCell(cellIndex);
				if (style != null) {
					cell.setCellStyle(style);
				}
				cell.setCellValue("");
			}
		}
	}

	/**
	 * 创建工作表中的行
	 * 
	 * @param rowIndex
	 * @return
	 */
	private HSSFRow addRow(int rowIndex) {
		return this.sheet.createRow(rowIndex);
	}

	/**
	 * 获取制定行数的行对象
	 * 
	 * @param rowIndex
	 * @return
	 */
	private HSSFRow getRow(int rowIndex) {
		return this.sheet.getRow(rowIndex);
	}

	/**
	 * 根据Value的类型给Cell赋值
	 * 
	 * @param cell
	 * @param val
	 * @param style
	 */
	private void setCellValue(HSSFCell cell, Object val, HSSFCellStyle style) {
		try {
			NumberFormat nf = NumberFormat.getInstance(Locale.CHINA);
			if (val == null) {
				cell.setCellValue("");
			} else if (val instanceof String) {
				cell.setCellValue((String) val);
			} else if (val instanceof Integer) {
				cell.setCellValue(nf.format(val));
			} else if (val instanceof Long) {
				cell.setCellValue(nf.format(val));
			} else if (val instanceof Double) {
				cell.setCellValue(nf.format(val));
			} else if (val instanceof Float) {
				cell.setCellValue(nf.format(val));
			} else if (val instanceof BigDecimal) {
				cell.setCellValue(nf.format(val));
			} else if (val instanceof Date) {
				if (style != null) {
					HSSFDataFormat format = wb.createDataFormat();
					style.setDataFormat(format.getFormat("yyyy-MM-dd HH:mm:ss"));
				}
				cell.setCellValue((Date) val);
			}
		} catch (Exception ex) {
			cell.setCellValue(val.toString());
		}
	}

	/**
	 * 取得有效的行数
	 * 
	 * @return
	 */
	private int getLastRowNum() {
		return sheet.getLastRowNum();
	}

	/**
	 * 添加一个单元格
	 * 
	 * @param row
	 *            添加的行
	 * @param column
	 *            添加列号
	 * @param val
	 *            添加值
	 * @param style
	 *            樣式
	 * @return 单元格对象
	 */
	private HSSFCell addCell(HSSFRow row, int column, Object val,
			HSSFCellStyle style) {
		HSSFCell cell = row.createCell(column);
		this.setCellValue(cell, val, style);
		cell.setCellStyle(style);
		return cell;
	}

	/**
	 * 添加一个单元格
	 * 
	 * @param row
	 *            添加的行
	 * @param column
	 *            添加列号
	 * @param val
	 *            添加值
	 * @param align
	 *            对齐方式（1：靠左；2：居中；3：靠右）
	 * @return 单元格对象
	 */
	private HSSFCell addCell(HSSFRow row, int column, Object val, int align) {
		HSSFCell cell = row.createCell(column);
		HSSFCellStyle style = styles.get("data"
				+ (align >= 1 && align <= 3 ? align : ""));
		this.setCellValue(cell, val, style);
		cell.setCellStyle(style);
		return cell;
	}

	/**
	 * 创建表格样式
	 * 
	 * @param wb
	 *            工作薄对象
	 * @return 样式列表
	 */
	private Map<String, HSSFCellStyle> createStyles(HSSFWorkbook wb) {
		Map<String, HSSFCellStyle> styles = new HashMap<String, HSSFCellStyle>();

		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFFont titleFont = wb.createFont();
		titleFont.setFontName("Arial");
		titleFont.setFontHeightInPoints((short) 16);
		titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(titleFont);
		styles.put("title", style);

		style = wb.createCellStyle();
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFFont dataFont = wb.createFont();
		dataFont.setFontName("Arial");
		dataFont.setFontHeightInPoints((short) 10);
		style.setFont(dataFont);
		styles.put("data", style);

		style = wb.createCellStyle();
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style.setBorderRight(HSSFCellStyle.BORDER_THICK);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(HSSFCellStyle.BORDER_THICK);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_THICK);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		style.setBorderBottom(HSSFCellStyle.BORDER_THICK);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		styles.put("border", style);

		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		style.cloneStyleFrom(styles.get("border"));
		style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		styles.put("data1", style);

		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		style.cloneStyleFrom(styles.get("border"));
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		styles.put("data2", style);

		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		style.cloneStyleFrom(styles.get("border"));
		style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		styles.put("data3", style);

		style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		// style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		HSSFFont headerFont = wb.createFont();
		headerFont.setFontName("Arial");
		headerFont.setFontHeightInPoints((short) 10);
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerFont.setColor(HSSFColor.BLACK.index);
		style.setFont(headerFont);
		styles.put("header", style);

		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("header"));
		style.setWrapText(true);
		styles.put("headerV", style);

		return styles;
	}

	public static void main(String[] args) {
		File file = new File(targetFile);
		try {
			FileOutputStream fOut = new FileOutputStream(file);
			String title = "上门收款商户业务量统计表";
			MSExcelManager msExcelManger = new MSExcelManager(title);
			HSSFWorkbook wb = msExcelManger.getWB();
			wb.write(fOut);
			fOut.flush();
			fOut.close();
			System.out.println("文件生成");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
