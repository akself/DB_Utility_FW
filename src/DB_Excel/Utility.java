package DB_Excel;

public class Utility {

	public static void main(String[] args) {

		String path = System.getProperty("user.dir");

		String filein = DB_Excel.fromProperties(path + "/Config.properties", "filein");
		String Oradriver = DB_Excel.fromProperties(path + "/Config.properties", "Oradriver");
		String URL = DB_Excel.fromProperties(path + "/Config.properties", "URL");
		String username = DB_Excel.fromProperties(path + "/Config.properties", "username");
		String password = DB_Excel.fromProperties(path + "/Config.properties", "password");
		String CountsheetName = DB_Excel.fromProperties(path + "/Config.properties", "CountsheetName");

		try {
			DB_Excel.toExcel(Oradriver, URL, username, password, filein, CountsheetName);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
