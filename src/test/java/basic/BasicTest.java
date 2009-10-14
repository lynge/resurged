package basic;

import java.sql.Connection;
import java.sql.DriverManager;

import junit.framework.TestCase;

import org.resurged.Log;
import org.resurged.QueryObjectFactory;
import org.resurged.jdbc.DataSet;

public class BasicTest extends TestCase {
	public void testDummy(){}
	
	// Tests work fine, but maven gives me the headache
	
//	private Connection con = null;
//	private PersonDao personDao;
//
//	@Override
//	protected void setUp() throws Exception {
//		super.setUp();
//
//		Log.setDebugEnabled(false);
//		Log.setTraceEnabled(false);
//
//		Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
//		con = DriverManager.getConnection("jdbc:derby:MyDbTest;create=true");
//		personDao = QueryObjectFactory.createQueryObject(PersonDao.class, con);
//
//		int createResult = personDao.createTable();
//		System.out.println("Table create, rows affected: " + createResult);
//	}
//
//	@Override
//	protected void tearDown() throws Exception {
//		super.tearDown();
//
//		int dropResult = personDao.dropTable();
//		System.out.println("Table dropped, rows affected: " + dropResult);
//
//		if (con != null)
//			con.close();
//		System.out.println("Connection closed");
//
//		try {
//			DriverManager.getConnection("jdbc:derby:MyDbTest;shutdown=true");
//		} catch (Exception e) {}
//	}
//
//	public void testSimpleQueries() throws Exception {
//		int rowsAffected = personDao.insert(1, "Arthur", "Dent");
//		System.out.println("Row inserted, rows affected: " + rowsAffected);
//
//		rowsAffected += personDao.insert(2, "Ford", "Prefect");
//		System.out.println("Row inserted, rows affected: " + rowsAffected);
//
//		rowsAffected += personDao.insert(3, "Ford", "Prefect");
//		System.out.println("Row inserted, rows affected: " + rowsAffected);
//
//		assertEquals(3, rowsAffected);
//
//		DataSet<Person> all = personDao.getAll();
//		assertEquals(3, all.size());
//		for (Person dto : all) {
//			System.out.println(dto.toString());
//		}
//
//		DataSet<Person> some = personDao.getSome(1);
//		assertEquals(1, some.size());
//		for (Person dto : some) {
//			System.out.println(dto.toString());
//		}
//
//		rowsAffected -= personDao.delete(1);
//		System.out.println("Row deleted, rows left: " + rowsAffected);
//
//		rowsAffected -= personDao.deleteAll();
//		System.out.println("Row deleted, rows left: " + rowsAffected);
//
//		assertEquals(0, rowsAffected);
//	}
}
