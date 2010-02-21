package org.resurged.test;

import org.junit.Assert;
import org.junit.Test;
import org.resurged.QueryObjectFactory;
import org.resurged.impl.Log;
import org.resurged.test.model.BaseDao;
import org.resurged.test.util.AbstractTestCase;
import org.resurged.test.util.Generator;
import org.resurged.test.util.Vendor;


public class DefaultGeneratorTest extends AbstractTestCase {
	BaseDao dao=null;

	public DefaultGeneratorTest(Vendor vendor, Generator generator) {
		super(vendor, generator);
	}
	
	public void init() throws Exception{
		dao = QueryObjectFactory.createQueryObject(BaseDao.class, getConnection());
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
