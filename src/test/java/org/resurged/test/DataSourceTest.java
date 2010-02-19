package org.resurged.test;

import org.junit.Assert;
import org.junit.Test;
import org.resurged.QueryObjectFactory;
import org.resurged.impl.Log;
import org.resurged.jdbc.DataSet;
import org.resurged.test.model.Person;
import org.resurged.test.model.PersonDao;
import org.resurged.test.util.AbstractTestCase;
import org.resurged.test.util.Generator;
import org.resurged.test.util.Vendor;

public class DataSourceTest extends AbstractTestCase {
	PersonDao dao=null;

	public DataSourceTest(Vendor vendor, Generator generator) {
		super(vendor, generator);
	}
	
	public void init() throws Exception{
    	if(vendor!=Vendor.Derby)
    		return;
    	
		dao = QueryObjectFactory.createQueryObject(PersonDao.class, getDs(), configuration);
		Log.info(this, "PersonDao loaded");

		int createResult = dao.createTable();
		Log.info(this, "Table create, rows affected: " + createResult);
	}

    public void cleanup() throws Exception{
    	if(vendor!=Vendor.Derby)
    		return;
    	
		int dropResult = dao.dropTable();
		Log.info(this, "Table dropped, rows affected: " + dropResult);
    }

    @Test
    public void testBasic() throws Exception {
    	if(vendor!=Vendor.Derby)
    		return;
    	
		int rowsAffected = dao.insert(1, "Arthur", "Dent");
		Log.info(this, "Row inserted, rows affected: " + rowsAffected);

		rowsAffected += dao.insert(2, "Ford", "Prefect");
		Log.info(this, "Row inserted, rows affected: " + rowsAffected);

		rowsAffected += dao.insert(3, "Ford", "Prefect");
		Log.info(this, "Row inserted, rows affected: " + rowsAffected);

		Assert.assertEquals(3, rowsAffected);
		
		int affected = dao.update(2, "Zaphod", "Beeblebrox");
		Log.info(this, "Row updated, rows affected: " + affected);
		Assert.assertEquals(1, affected);

		DataSet<Person> all = dao.getAll();
		Assert.assertEquals(3, all.size());
		for (Person dto : all) {
			Log.info(this, dto.toString());
		}

		DataSet<Person> some = dao.getSome(1);
		Assert.assertEquals(1, some.size());
		for (Person dto : some) {
			Log.info(this, dto.toString());
		}

		rowsAffected -= dao.delete(1);
		Log.info(this, "Row deleted, rows left: " + rowsAffected);

		rowsAffected -= dao.deleteAll();
		Log.info(this, "Row deleted, rows left: " + rowsAffected);

		Assert.assertEquals(0, rowsAffected);
    }

}
