package org.resurged.test;

import org.junit.Assert;
import org.junit.Test;
import org.resurged.QueryObjectFactory;
import org.resurged.impl.Log;
import org.resurged.jdbc.DataSet;
import org.resurged.test.model.InheritanceChildDao;
import org.resurged.test.model.InheritanceChildPojo;
import org.resurged.test.util.AbstractTestCase;
import org.resurged.test.util.Generator;
import org.resurged.test.util.Vendor;


public class InheritanceTest extends AbstractTestCase {
	InheritanceChildDao dao=null;

	public InheritanceTest(Vendor vendor, Generator generator) {
		super(vendor, generator);
	}
	
	public void init() throws Exception{
		dao = QueryObjectFactory.createQueryObject(InheritanceChildDao.class, getConnection(), configuration);
		Log.info(this, "InheritanceParentDao loaded");

		int createResult = dao.createTable();
		Log.info(this, "Table create, rows affected: " + createResult);
	}

    public void cleanup() throws Exception{
		int dropResult = dao.dropTable();
		Log.info(this, "Table dropped, rows affected: " + dropResult);
    }

    @Test
    public void testInheritance() throws Exception {
		int rowsAffected = dao.insert(1, "Arthur", "Dent");
		Log.info(this, "Row inserted, rows affected: " + rowsAffected);

		rowsAffected += dao.insert(2, "Ford", "Prefect");
		Log.info(this, "Row inserted, rows affected: " + rowsAffected);

		rowsAffected += dao.insert(3, "Ford", "Prefect");
		Log.info(this, "Row inserted, rows affected: " + rowsAffected);

		Assert.assertEquals(3, rowsAffected);

		DataSet<InheritanceChildPojo> result = dao.getByName("Prefect");
		Assert.assertEquals(2, result.size());
		for (InheritanceChildPojo dto : result) {
			Log.info(this, dto.toString());
		}
    }

}
