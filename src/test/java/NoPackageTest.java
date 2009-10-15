import java.sql.Connection;
import java.sql.DriverManager;

import junit.framework.TestCase;

import org.resurged.QueryObjectFactory;

public class NoPackageTest extends TestCase {
	private Connection con = null;
	private NoPackageDao dao;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		con = DriverManager.getConnection("jdbc:derby:MyDbTest;create=true");
		dao = QueryObjectFactory.createQueryObject(NoPackageDao.class, con);

		int createResult = dao.createTable();
		System.out.println("Table create, rows affected: " + createResult);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		int dropResult = dao.dropTable();
		System.out.println("Table dropped, rows affected: " + dropResult);

		if (con != null)
			con.close();
		System.out.println("Connection closed");

		try {
			DriverManager.getConnection("jdbc:derby:MyDbTest;shutdown=true");
		} catch (Exception e) {
		}
	}

	// The primary purpose of this test is to ensure no exceptions are thrown
	// during runtime compilation, but still a few queries are performed, just in case
	public void testSimpleQueries() throws Exception {
		int insertResult = dao.insert(1, "John", "Doe");
		System.out.println("Row inserted, rows affected: " + insertResult);
	}
}
