package basic;

import java.sql.Connection;
import java.sql.DriverManager;

import junit.AbstractTestCase;

import org.resurged.QueryObjectFactory;
import org.resurged.impl.Log;

public class DefaultGeneratorTest extends AbstractTestCase{
	protected Connection con = null;
	private PersonDao dao;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		con = DriverManager.getConnection("jdbc:derby:MyDbTest;create=true");
		
		dao = QueryObjectFactory.createQueryObject(PersonDao.class, con, configuration);
		Log.info(this, "PersonDao loaded");

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
		} catch (Exception e) {}
	}
	
	public void testSimpleQueries() throws Exception {
		int rowsAffected = dao.insert(1, "Arthur", "Dent");
		Log.info(this, "Row inserted, rows affected: " + rowsAffected);

		assertEquals(1, rowsAffected);
	}
}
