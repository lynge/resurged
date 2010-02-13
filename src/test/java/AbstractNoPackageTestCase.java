import java.sql.Connection;
import java.sql.DriverManager;

import junit.AbstractTestCase;

import org.resurged.Log;
import org.resurged.QueryObjectFactory;

public class AbstractNoPackageTestCase extends AbstractTestCase {
	private Connection con = null;
	private NoPackageDao dao;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		con = DriverManager.getConnection("jdbc:derby:MyDbTest;create=true");
		dao = QueryObjectFactory.createQueryObject(NoPackageDao.class, con);

		int createResult = dao.createTable();
		Log.info(this, "Table create, rows affected: " + createResult);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		int dropResult = dao.dropTable();
		Log.info(this, "Table dropped, rows affected: " + dropResult);

		if (con != null)
			con.close();
		Log.info(this, "Connection closed");

		try {
			DriverManager.getConnection("jdbc:derby:MyDbTest;shutdown=true");
		} catch (Exception e) {
		}
	}

	// The primary purpose of this test is to ensure no exceptions are thrown
	// during runtime compilation, but still a few queries are performed, just in case
	public void testSimpleQueries() throws Exception {
		int insertResult = dao.insert(1, "John", "Doe");
		Log.info(this, "Row inserted, rows affected: " + insertResult);
	}
}
