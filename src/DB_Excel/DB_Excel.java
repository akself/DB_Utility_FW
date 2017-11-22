package DB_Excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DB_Excel {

	public static void toExcel(String Oradriver, String URL, String username, String password, String filepath,
			String CountsheetName) throws Exception {

		// Connecting to DB

		Class.forName(Oradriver);
		Connection connection = DriverManager.getConnection(URL, username, password);
		Statement statement = connection.createStatement();
		System.out.println("Connected to the Database");

		// Validating the count difference

		File file = new File(filepath);
		FileInputStream fileinput = new FileInputStream(file);
		Workbook workbook = WorkbookFactory.create(fileinput);
		Sheet sheet = workbook.getSheet(CountsheetName);
		int rowcount = sheet.getLastRowNum();
		System.out.println("Total Queries : " + rowcount);

		System.out.println("Validating mis match count");

		for (int row = 1; row <= rowcount; row++) {
			int cell = 1;
			int cell1 = 3;
			sheet.getRow(row).createCell(cell).setCellValue("");
			sheet.getRow(row).createCell(cell1).setCellValue("");
			FileOutputStream fileoutput = new FileOutputStream(file);
			workbook.write(fileoutput);
		}

		for (int row = 1; row <= rowcount; row++) {
			int cell = 1;

			if (sheet.getRow(row).getCell(2).getStringCellValue().equalsIgnoreCase("Y")) {

				String resultcount = sheet.getRow(row).getCell(0).getStringCellValue();

				System.out.println(
						"Execution Flag is 'Y'..Executing the Query: select count(*) from (" + resultcount + ")");
				ResultSet resultset = statement.executeQuery("select count(*) from (" + resultcount + ")");
				while (resultset.next()) {
					String report_countDB = resultset.getString("Count(*)");
					sheet.getRow(row).createCell(cell).setCellValue(report_countDB);

					if (report_countDB.equalsIgnoreCase("0")) {
						sheet.getRow(row).createCell(3).setCellValue("PASS");

					} else {
						sheet.getRow(row).createCell(3).setCellValue("FAIL");
					}

				}
				FileOutputStream fileoutput = new FileOutputStream(file);
				workbook.write(fileoutput);

				System.out.println("Difference in count between 2 tables written to excel");
			}
			if (sheet.getRow(row).getCell(2).getStringCellValue().equalsIgnoreCase("N")) {
				System.out.println("Execution Flag is N");
			}

			if (sheet.getRow(row).getCell(2).getStringCellValue().equalsIgnoreCase("")) {
				System.out.println("No execution flag available");

			}
		}

		// Getting and Writing the mismatch data

		System.out.println("Writing data...");
		for (int row1 = 1; row1 <= rowcount; row1++) {

			if (sheet.getRow(row1).getCell(2).getStringCellValue().equalsIgnoreCase("Y")) {
				DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy HH_mm_ss");
				Date date = new Date();
				String date1 = dateFormat.format(date);
				String path = System.getProperty("user.dir");

				File file1 = new File(
						DB_Excel.fromProperties(path + "/Config.properties", "fileout") + date1 + "_Query-" + row1);

				file1.mkdir();
				System.out.println("Folder Created Successfully");

				String resultcount1 = sheet.getRow(row1).getCell(0).getStringCellValue();
				System.out.println("Execution Flag is 'Y'..Executing the Query: " + resultcount1);

				ResultSet rs2 = statement.executeQuery(resultcount1);
				int rowindex = 0;

				XSSFWorkbook result_workbook = new XSSFWorkbook();
				FileOutputStream fileout = new FileOutputStream(
						new File(DB_Excel.fromProperties(path + "/Config.properties", "fileout") + date1 + "_Query-"
								+ row1 + "\\" + date1 + "_Query-" + row1 + ".xlsx"));

				Sheet sheet1 = result_workbook.createSheet("Output");

				ResultSetMetaData metadata1 = rs2.getMetaData();

				int columnCount2 = metadata1.getColumnCount();
				Row rr2 = sheet1.createRow(rowindex);

				for (int columnindex = 1; columnindex <= columnCount2; columnindex++) {

					Cell c2 = rr2.createCell(columnindex - 1);
					c2.setCellValue(metadata1.getColumnName(columnindex));
				}

				while (rs2.next()) {
					ResultSetMetaData metadata2 = rs2.getMetaData();

					// System.out.println(metadata1.getColumnName(1));

					int columnCount1 = metadata2.getColumnCount();

					Row rr1 = sheet1.createRow(rowindex + 1);

					for (int columnindex = 1; columnindex <= columnCount1; columnindex++) {

						String DB_values = rs2.getString(columnindex);
						Cell c2 = rr1.createCell(columnindex - 1);
						c2.setCellValue(DB_values);
					}

					rowindex++;
				}
				result_workbook.write(fileout);
				System.out.println("Difference in data between the 2 tables written in Excel");
				result_workbook.close();

			}
			if (sheet.getRow(row1).getCell(2).getStringCellValue().equalsIgnoreCase("N")) {
				System.out.println("Execution Flag is N");
			}

			if (sheet.getRow(row1).getCell(2).getStringCellValue().equalsIgnoreCase("")) {
				System.out.println("No execution flag available");
			}
		}

	}

	// Reading configuration data from property file

	public static String fromProperties(String filepath, String key) {
		String data = null;

		try {
			File f = new File(filepath);
			FileInputStream fis = new FileInputStream(f);
			Properties prop = new Properties();
			prop.load(fis);
			data = prop.getProperty(key);
		} catch (Exception e) {
			System.out.println(e);
		}
		return data;
	}
}
