import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains methods for generating source code from sql statements
 * 
 * @author Santosh Kumar D
 * 
 */
public class DBTools {

	/**
	 * Extracts the names in double quotes and takes 1st one for table name and
	 * the rest for column names
	 * 
	 * @param createTableSql
	 *            - sql statement
	 * @return - contract interfaces
	 */
	public static String getContractClassFromSql(String createTableSql) {
		Pattern pattern = Pattern.compile("\"\\w+\"");
		Matcher matcher = pattern.matcher(createTableSql);
		ArrayList<String> names = new ArrayList<String>(), variableNames = new ArrayList<String>();

		// extracting names
		while (matcher.find()) {
			String name = createTableSql.substring(matcher.start() + 1,
					matcher.end() - 1);
			names.add(name);
			variableNames.add(capitalize(name));
		}

		// generating columns
		StringBuilder contractStringBuilder = new StringBuilder();
		contractStringBuilder.append("interface " + names.get(0)
				+ "Columns { \n");
		for (int i = 1; i < names.size(); i++) {
			contractStringBuilder.append("\tString " + variableNames.get(i)
					+ " = \"" + names.get(i) + "\";\n");
		}
		contractStringBuilder.append("}\n\n");

		// generating query
		contractStringBuilder.append("interface " + names.get(0) + "Query {\n");
		contractStringBuilder.append("\tString [] columns= {")
				.append(names.get(0)).append("Columns.")
				.append(variableNames.get(1));
		for (int i = 2; i < names.size(); i++) {
			contractStringBuilder.append(", ").append(names.get(0))
					.append("Columns.").append(variableNames.get(i));
		}
		contractStringBuilder.append("};\n");
		for (int i = 1; i < names.size(); i++)
			contractStringBuilder.append("\tint " + variableNames.get(i)
					+ " = " + (i - 1) + ";\n");
		contractStringBuilder.append("\n}");

		return contractStringBuilder.toString();
	}

	/**
	 * Replaces small letters with capital letters and inserts underscore
	 * between two words
	 * 
	 * @param string
	 * @return
	 */
	private static String capitalize(String string) {
		StringBuilder stringBuilder = new StringBuilder();

		// inserting '_' between words
		for (int i = 0; i < string.length() - 1; i++) {
			char currentChar = string.charAt(i), nextChar = string
					.charAt(i + 1);
			stringBuilder.append(currentChar);
			if (Character.isLowerCase(currentChar)
					&& Character.isUpperCase(nextChar))
				stringBuilder.append('_');
		}
		stringBuilder.append(string.charAt(string.length() - 1));

		return stringBuilder.toString().toUpperCase();
	}

	public static void main(String[] args) {
		String sql = "CREATE TABLE \"Sites\" (\"s_id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , \"SiteID\" INTEGER,\"SiteName\" VARCHAR,\"Address1\" VARCHAR,\"Address2\" VARCHAR,\"Town\" VARCHAR,\"County\" VARCHAR,\"Postcode\" VARCHAR,\"Phone\" VARCHAR,\"RegionID\" INTEGER,\"CountryID\" INTEGER,\"Fixed\" BOOL,\"Pump\" BOOL,\"HGV\" BOOL,\"TwentyFourHour\" BOOL,\"LPG\" BOOL,\"RedDiesel\" BOOL,\"PolesignName\" VARCHAR,\"PolesignImg\" VARCHAR,\"Lon\" VARCHAR,\"Lat\" VARCHAR,\"DateAdded\" DATETIME,\"DateDeleted\" DATETIME,\"DateEdited\" DATETIME)";
		System.out.println(getContractClassFromSql(sql));
	}
}
