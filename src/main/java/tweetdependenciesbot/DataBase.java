package tweetdependenciesbot;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static java.sql.DriverManager.getConnection;

public class DataBase
{
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL      = "jdbc:mysql://localhost/";
	static final String DB_NAME     = "tdbdb";

	private String user = "root";
	private String password = "";

	/** Initializes the database. */
	public DataBase(String user, String password) throws SQLException, ClassNotFoundException, IOException
	{
		this.user = user;
		this.password = password;

		System.out.println("Loading database drivers");
		Class.forName(JDBC_DRIVER);
		open();
		init();
		System.out.println("Connected with database!");
	}

	/** Creates the database if it does not exists. */
	public void init() throws SQLException, IOException
	{
		ResultSet dbs = stmt.executeQuery("SHOW DATABASES");
		boolean dbExists = false;
		while (dbs.next()) {
			if (dbs.getString(1).equals(DB_NAME))
				dbExists = true;
		}
		if (!dbExists) {
			System.out.println("Creating new database");
			close();
			String userDir = System.getProperty("user.dir");
			Process p = new ProcessBuilder(userDir + "/init_database.sh", user, password).start();
			System.out.println("Please start the program again.");
			System.exit(0);
		}
		stmt.executeQuery("USE " + DB_NAME);
	}

	/** Opens the connection with the database. */
	public void open() throws SQLException
	{
		con = getConnection(DB_URL, user, password);
		stmt = con.createStatement();
	}

	/** Must be called before the end of the program. */
	public void close() throws SQLException
	{
		stmt.close();
		con.close();
	}

	/** Clears the entire database. */
	public void clear() throws IOException, SQLException
	{
		close();
		String userDir = System.getProperty("user.dir");
		Process p = new ProcessBuilder(userDir + "/erase_database.sh", user, password).start();
		p.destroy();
		open();
	}

	/** Queries the database. */
	public ResultSet query(String q) throws SQLException
	{
		return stmt.executeQuery(q);
	}

	public void insert(String q) throws SQLException
	{
		stmt.executeUpdate(q);
	}

	private Connection con;
	private Statement stmt;
}
