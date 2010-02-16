import java.sql.Connection;

import junit.AbstractTestCase;

import org.resurged.QueryObjectFactory;
import org.resurged.impl.Log;

public class AbstractNoPackageTestCase extends AbstractTestCase {
	private Connection con = null;
	private NoPackageDao dao;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		openConnection();
		
		dao = QueryObjectFactory.createQueryObject(NoPackageDao.class, con, configuration);

		int createResult = dao.createTable();
		Log.info(this, "Table create, rows affected: " + createResult);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		int dropResult = dao.dropTable();
		Log.info(this, "Table dropped, rows affected: " + dropResult);

		closeConnection();
	}

	// The primary purpose of this test is to ensure no exceptions are thrown
	// during runtime compilation, but still a few queries are performed, just in case
	public void testSimpleQueries() throws Exception {
		int insertResult = dao.insert(1, "John", "Doe");
		Log.info(this, "Row inserted, rows affected: " + insertResult);
	}
}
