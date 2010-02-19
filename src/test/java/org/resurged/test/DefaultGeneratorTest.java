package org.resurged.test;

import org.junit.Assert;
import org.junit.Test;
import org.resurged.QueryObjectFactory;
import org.resurged.impl.Log;
import org.resurged.test.model.PersonDao;
import org.resurged.test.util.AbstractTestCase;


public class DefaultGeneratorTest extends AbstractTestCase {
	PersonDao dao=null;

	public DefaultGeneratorTest(int vendor, int generator) {
		super(vendor, generator);
	}
	
	public void init() throws Exception{
		dao = QueryObjectFactory.createQueryObject(PersonDao.class, con);
		Log.info(this, "PersonDao loaded");

		int createResult = dao.createTable();
		Log.info(this, "Table create, rows affected: " + createResult);
	}

    public void cleanup() throws Exception{
		int dropResult = dao.dropTable();
		Log.info(this, "Table dropped, rows affected: " + dropResult);
    }

    @Test
    public void testSimpleQueries() throws Exception {
		int rowsAffected = dao.insert(1, "Arthur", "Dent");
		Log.info(this, "Row inserted, rows affected: " + rowsAffected);

		Assert.assertEquals(1, rowsAffected);
	}

}
