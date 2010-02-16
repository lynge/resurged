package basic;

import java.sql.DriverManager;

import junit.AbstractTestCase;

import org.apache.derby.jdbc.EmbeddedDataSource40;
import org.resurged.QueryObjectFactory;
import org.resurged.impl.Log;
import org.resurged.jdbc.DataSet;

public abstract class AbstractDataSourceTestCase extends AbstractTestCase{
	private PersonDao dao;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		EmbeddedDataSource40 ds = new EmbeddedDataSource40(); 
        ds.setDatabaseName("MyDbTest;create=true");
        ds.setCreateDatabase("create");
        
		dao = QueryObjectFactory.createQueryObject(PersonDao.class, ds, configuration);
		Log.info(this, "PersonDao loaded: " + dao);

		int createResult = dao.createTable();
		Log.info(this, "Table create, rows affected: " + createResult);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		int dropResult = dao.dropTable();
		Log.info(this, "Table dropped, rows affected: " + dropResult);

		try {
			DriverManager.getConnection("jdbc:derby:MyDbTest;shutdown=true");
		} catch (Exception e) {}
	}
	
	public void testSimpleQueries() throws Exception {
		int rowsAffected = dao.insert(1, "Arthur", "Dent");
		Log.info(this, "Row inserted, rows affected: " + rowsAffected);

		rowsAffected += dao.insert(2, "Ford", "Prefect");
		Log.info(this, "Row inserted, rows affected: " + rowsAffected);

		rowsAffected += dao.insert(3, "Ford", "Prefect");
		Log.info(this, "Row inserted, rows affected: " + rowsAffected);

		assertEquals(3, rowsAffected);
		
		int affected = dao.update(2, "Zaphod", "Beeblebrox");
		Log.info(this, "Row updated, rows affected: " + affected);
		assertEquals(1, affected);

		DataSet<Person> all = dao.getAll();
		assertEquals(3, all.size());
		for (Person dto : all) {
			Log.info(this, dto.toString());
		}

		DataSet<Person> some = dao.getSome(1);
		assertEquals(1, some.size());
		for (Person dto : some) {
			Log.info(this, dto.toString());
		}

		rowsAffected -= dao.delete(1);
		Log.info(this, "Row deleted, rows left: " + rowsAffected);

		rowsAffected -= dao.deleteAll();
		Log.info(this, "Row deleted, rows left: " + rowsAffected);

		assertEquals(0, rowsAffected);
	}
}
