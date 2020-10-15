package com.coffer.tools.excel.template;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * 导出Excel的模板对象
 * 
 */
public class ExcelTemplate {

	private Log log = LogFactory.getLog(ExcelTemplate.class);

	private List<Cell> staticObject = null;
	private List<Cell> parameterObjct = null;
	private List<Cell> fieldObjct = null;
	private List<Cell> titleObjct = null;
	private List<Cell> variableObject = null;
	private String templatePath = null;

	public ExcelTemplate(String pTemplatePath) {
		templatePath = pTemplatePath;
	}

	public ExcelTemplate() {
	}

	/**
	 * 解析Excel模板
	 * 
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	public Workbook parse(HttpServletRequest request) {
		staticObject = new ArrayList<Cell>();
		parameterObjct = new ArrayList<Cell>();
		fieldObjct = new ArrayList<Cell>();
		variableObject = new ArrayList<Cell>();
		titleObjct = new ArrayList<Cell>();
		if (StringUtils.isEmpty(templatePath)) {
			log.error("Excel模板路径不能为空!");
		}

//		// Start
//		File file = new File(templatePath);
//		InputStream is = null;
//		try {
//			is = new FileInputStream(file);
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		// End
		InputStream is = request.getSession().getServletContext().getResourceAsStream(templatePath);

		if (ExpressUtils.isEmpty(is)) {
			log.error("未找到模板文件,请确认模板路径是否正确[" + templatePath + "]");
		}

		Workbook workbook = null;

		try {
			workbook = WorkbookFactory.create(is);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Sheet sheet = workbook.getSheetAt(0);
		if (ExpressUtils.isNotEmpty(sheet)) {
			int rows = sheet.getLastRowNum();
			for (int k = 0; k <= rows; k++) {
				if(null == sheet.getRow(k)){
					continue;
				}
				Iterator<Cell> cells = sheet.getRow(k).cellIterator();
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
							this.addParameterObjct(cell);
						} else if (cellContent.indexOf("$F") != -1 || cellContent.indexOf("$f") != -1) {
							// 追加列表用的字段
							this.addFieldObjct(cell);
						} else if (cellContent.indexOf("$T") != -1 || cellContent.indexOf("$t") != -1) {
							// 追加标题用的字段
							this.addTitleObjct(cell);
						} else if (cellContent.indexOf("$V") != -1 || cellContent.indexOf("$v") != -1) {
							this.addVariableObject(cell);
						} else {
							this.addStaticObject(cell);
						}
					}
				}
			}
		} else {
			log.error("模板工作表对象不能为空!");
		}
		return workbook;
	}

	/**
	 * 增加一个静态文本对象
	 */
	public void addStaticObject(Cell cell) {
		staticObject.add(cell);
	}

	/**
	 * 增加一个参数对象
	 */
	public void addParameterObjct(Cell cell) {
		parameterObjct.add(cell);
	}

	/**
	 * 增加一个字段对象
	 */
	public void addFieldObjct(Cell cell) {
		fieldObjct.add(cell);
	}
	
	/**
	 * 增加一个标题字段
	 */
	public void addTitleObjct(Cell cell) {
		titleObjct.add(cell);
	}

	/**
	 * 增加一个变量对象
	 * 
	 * @param cell
	 */
	public void addVariableObject(Cell cell) {
		variableObject.add(cell);
	}

	public List<Cell> getStaticObject() {
		return staticObject;
	}

	public List<Cell> getParameterObjct() {
		return parameterObjct;
	}

	public List<Cell> getFieldObjct() {
		return fieldObjct;
	}
	
	public List<Cell> getTitleObjct() {
		return titleObjct;
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	public List<Cell> getVariableObject() {
		return variableObject;
	}
}
