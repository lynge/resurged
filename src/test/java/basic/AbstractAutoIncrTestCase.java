package basic;

import junit.AbstractTestCase;

import org.resurged.QueryObjectFactory;
import org.resurged.impl.Log;
import org.resurged.jdbc.DataSet;

public abstract class AbstractAutoIncrTestCase extends AbstractTestCase{
	private PersonDao dao;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		openConnection();
		
		dao = QueryObjectFactory.createQueryObject(PersonDao.class, con, configuration);
		Log.info(this, "PersonDao loaded");

		int createResult =(useMysql)? dao.createTableAutoIncrMySql() : dao.createTableAutoIncr();
		Log.info(this, "Table create, rows affected: " + createResult);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		int dropResult = dao.dropTable();
		Log.info(this, "Table dropped, rows affected: " + dropResult);

		closeConnection();
	}
	
	public void testReturnKeys(){
//		DataSet<PersonKey> keys = dao.insertReturnKeysNoKeys("Arthur", "Dent");
//		for(PersonKey key : keys)
//			System.out.println(key.getId());
//
//		DataSet<PersonKey> keys = dao.insertReturnKeysDriversDefined("Arthur", "Dent");
//		for(PersonKey key : keys)
//			System.out.println(key.id);
//
//		keys = dao.insertReturnKeysColSpecified("Arthur", "Dent");
//		for(PersonKey key : keys)
//			System.out.println(key.getId());
	}
	
}
