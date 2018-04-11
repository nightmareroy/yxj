/*
 * Copyright © 2017 qeng.cn All Rights Reserved.
 * 
 * 感谢您加入清源科技，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/qeng/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归清源科技所有，我们将保留所有权利;
 * 4、凡侵犯清源科技相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package cn.qeng.gm.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Excel工具类.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
public class ExcelUtils {
	private static final String XLSX = "xlsx";
	private static final String XLS = "xls";

	public static List<String[]> readData(InputStream inputStream, String filename, int startRowNum) throws IOException {
		try (Workbook workbook = initWorkbook(inputStream, filename)) {
			Sheet sheet = workbook.getSheetAt(0);// 第一个Sheet
			List<String[]> result = new ArrayList<>(sheet.getLastRowNum());
			for (int rowNum = startRowNum; rowNum <= sheet.getLastRowNum(); rowNum++) {
				Row row = sheet.getRow(rowNum);
				if (row == null) { // 如果此列为空就跳过
					continue;
				}

				String[] data = new String[row.getLastCellNum()];
				for (int i = 0; i < row.getLastCellNum(); i++) {
					data[i] = getCellValue(row.getCell(i));
				}
				result.add(data);
			}
			return result;
		}
	}

	private static String getCellValue(Cell cell) {
		if (cell == null) {
			return "";
		}
		switch (cell.getCellTypeEnum()) {
		case NUMERIC:
			return new DataFormatter().formatCellValue(cell);
		case STRING:
			return cell.getRichStringCellValue().getString();
		case FORMULA:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				Date date = cell.getDateCellValue();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				return sdf.format(date);
			} else {
				return new DataFormatter().formatCellValue(cell);
			}
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		default:
			return cell.getStringCellValue();
		}
	}

	private static Workbook initWorkbook(InputStream inputStream, String filename) throws IOException {
		String suffix = filename.substring(filename.indexOf(".") + 1);
		if (XLSX.equals(suffix)) {
			return new XSSFWorkbook(inputStream);
		} else if (XLS.equals(suffix)) {
			return new HSSFWorkbook(inputStream);
		} else {
			throw new RuntimeException("非法文件名，请上传Excel...");
		}
	}
}