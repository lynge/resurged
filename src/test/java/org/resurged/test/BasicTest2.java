package org.resurged.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.resurged.QueryObjectFactory;
import org.resurged.impl.Log;
import org.resurged.jdbc.DataSet;
import org.resurged.test.model.BaseDao;
import org.resurged.test.model.BasePojo;

public class BasicTest2 {
	
	private BaseDao dao;
	
	@Before
	public void init() throws ClassNotFoundException, SQLException {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		Connection connection = DriverManager.getConnection("jdbc:derby:MyDbTest;create=true");
		dao = QueryObjectFactory.createQueryObject(BaseDao.class, connection);
		dao.createTable();
	}
	
	@After
	public void removeTable() {
		dao.dropTable();
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
