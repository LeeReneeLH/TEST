package com.coffer.external.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadJsonUtils {

	public static void main(String[] args) {
		String jsonFileName = "71_bank_100_01";
		String jsonStr = readJson(jsonFileName);
		System.out.println(jsonStr);
	}

	/**
	 * 读取JSON文件
	 * 
	 * @param jsonFileName
	 * @return
	 */
	public static String readJson(String jsonFileName) {
		String jsonPath = ReadJsonUtils.class.getClass().getResource("/").getPath() + "/json/";
		String extFileName = ".json";
		String fullFileName = jsonPath + jsonFileName + extFileName;

		String jsonStr = "";
		File file = new File(fullFileName);
		Scanner scanner = null;
		StringBuilder buffer = new StringBuilder();
		try {
			scanner = new Scanner(file, "utf-8");
			while (scanner.hasNextLine()) {
				buffer.append(scanner.nextLine().trim());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		jsonStr = buffer.toString();
		return jsonStr;
	}
}
