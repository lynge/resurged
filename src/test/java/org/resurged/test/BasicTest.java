package org.resurged.test;

import org.junit.Assert;
import org.junit.Test;
import org.resurged.QueryObjectFactory;
import org.resurged.impl.Log;
import org.resurged.jdbc.DataSet;
import org.resurged.test.model.BasePojo;
import org.resurged.test.model.BaseDao;
import org.resurged.test.util.AbstractTestCase;
import org.resurged.test.util.Vendor;


public class BasicTest extends AbstractTestCase {
	BaseDao dao=null;

	public BasicTest(Vendor vendor) {
		super(vendor);
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
    public void testBasic() throws Exception {
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

		DataSet<BasePojo> all = dao.getAll();
		Assert.assertEquals(3, all.size());
		for (BasePojo dto : all) {
			Log.info(this, dto.toString());
		}

		DataSet<BasePojo> some = dao.getSome(1);
		Assert.assertEquals(1, some.size());
		for (BasePojo dto : some) {
			Log.info(this, dto.toString());
		}

		rowsAffected -= dao.delete(1);
		Log.info(this, "Row deleted, rows left: " + rowsAffected);

		rowsAffected -= dao.deleteAll();
		Log.info(this, "Row deleted, rows left: " + rowsAffected);

		Assert.assertEquals(0, rowsAffected);
    }

}
