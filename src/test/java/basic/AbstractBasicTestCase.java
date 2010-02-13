package basic;

import java.sql.Connection;
import java.sql.DriverManager;

import junit.framework.TestCase;

import org.resurged.QueryObjectFactory;
import org.resurged.jdbc.DataSet;

public abstract class AbstractBasicTestCase extends TestCase{
	protected Connection con = null;
	private PersonDao dao;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		con = DriverManager.getConnection("jdbc:derby:MyDbTest;create=true");
		
		dao = QueryObjectFactory.createQueryObject(PersonDao.class, con);
		System.out.println("dao loaded");

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
		} catch (Exception e) {}
	}
	
	public void testSimpleQueries() throws Exception {
		int rowsAffected = dao.insert(1, "Arthur", "Dent");
		System.out.println("Row inserted, rows affected: " + rowsAffected);

		rowsAffected += dao.insert(2, "Ford", "Prefect");
		System.out.println("Row inserted, rows affected: " + rowsAffected);

		rowsAffected += dao.insert(3, "Ford", "Prefect");
		System.out.println("Row inserted, rows affected: " + rowsAffected);

		assertEquals(3, rowsAffected);
		
		int affected = dao.update(2, "Zaphod", "Beeblebrox");
		System.out.println("Row updated, rows affected: " + affected);
		assertEquals(1, affected);

		DataSet<Person> all = dao.getAll();
		assertEquals(3, all.size());
		for (Person dto : all) {
			System.out.println(dto.toString());
		}

		DataSet<Person> some = dao.getSome(1);
		assertEquals(1, some.size());
		for (Person dto : some) {
			System.out.println(dto.toString());
		}

		rowsAffected -= dao.delete(1);
		System.out.println("Row deleted, rows left: " + rowsAffected);

		rowsAffected -= dao.deleteAll();
		System.out.println("Row deleted, rows left: " + rowsAffected);

		assertEquals(0, rowsAffected);
	}
}
