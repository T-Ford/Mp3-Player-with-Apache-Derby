
import java.sql.*;

public class DBase {
	private Connection conn;
	private boolean isopen;

	// Attempt to connect to the JavaDB directory database.
	public DBase() {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			// Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
			conn = DriverManager.getConnection("jdbc:derby:directory");
			// conn =
			// DriverManager.getConnection("jdbc:derby:jar:(C:\Users\tford\workspace\MP3
			// Player)");
			conn.setAutoCommit(false);
		} catch (Exception e) {
			conn = null;
		}
		isopen = (conn != null);
	}

	// Test whether the database is open.
	public boolean isopen() {
		return isopen;
	}

	// Close the database connection.
	public void close() {
		if (!isopen)
			return;
		try {
			conn.close();
		} catch (Exception e) {
		}
		isopen = false;
		conn = null;
	}

	// This method gets all the entities of "dPath" and returns them in a string to be auto loaded once the application is opened
	public String[] getDirectory() {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		String sql;
		String dir = null;
		String[] read = new String[10];
		int index = 0;

		// Return if the database is closed.
		isopen();

		try {
			// Create a PreparedStatement for the query.
			sql = "SELECT Directory.DPath FROM Directory";
			stmt = conn.prepareStatement(sql);

			// Execute the query and obtain the result set.
			rset = stmt.executeQuery();

			// Process each row in a loop.
			while (rset.next()) {
				dir = rset.getString(1);
				read[index] = dir;
				index++;
			}
			stmt.close();
			conn.commit();
		} catch (Exception e) {
			// System.out.printf("%s%n", e.getMessage());
			try {
				stmt.close();
			} catch (Exception err) {
			}
			try {
				conn.rollback();
			} catch (Exception err) {
			}
		}
		return read;
	}

	public void updateDirectory(String d) {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		String sql;
		String directory = d;
		int id = -1;
		// Return of the database is closed
		isopen();

		try {
			sql = "SELECT Directory.Id FROM Directory";
			stmt = conn.prepareStatement(sql);
			rset = stmt.executeQuery();
			while (rset.next()) {
				id = rset.getInt(1);
			}
			id++;

			sql = "INSERT INTO Directory (id, DPath) " + "VALUES (?, ?)";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			stmt.setString(2, directory);

			stmt.executeUpdate();
			System.out.printf("%n");
			// Display the musician was added or not
			stmt.close();
			conn.commit();
		} catch (Exception e) {
			// System.out.printf("%s%n", e.getMessage());
			try {
				stmt.close();
			} catch (Exception err) {
			}
			try {
				conn.rollback();
			} catch (Exception err) {
			}
		}
	}

	public void showAll() {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		String d;
		String sql;

		isopen();

		try {
			// Create a PreparedStatement for the query.
			sql = "SELECT Directory.DPath FROM Directory";
			stmt = conn.prepareStatement(sql);

			// Execute the query and obtain the result set.
			rset = stmt.executeQuery();

			// Prints all stored directory paths as strings to the console
			while (rset.next()) {
				d = rset.getString(1);
				System.out.printf(d + "\n");
			}
			stmt.close();
			conn.commit();
		} catch (Exception e) {
			try {
				stmt.close();
			} catch (Exception err) {
			}
			try {
				conn.rollback();
			} catch (Exception err) {
			}
		}
	}

	// These method deletes all rows in the database
	public void deleteAll() {
		PreparedStatement stmt = null;
		String sql;

		isopen();

		try {

			sql = "DELETE FROM Directory WHERE Directory.Id >= 0";
			stmt = conn.prepareStatement(sql);

			stmt.executeUpdate();

			stmt.close();
			conn.commit();
		} catch (Exception e) {
			try {
				stmt.close();
			} catch (Exception err) {
			}
			try {
				conn.rollback();
			} catch (Exception err) {
			}
		}
	}
}