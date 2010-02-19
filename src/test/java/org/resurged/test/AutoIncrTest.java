package org.resurged.test;

import junit.framework.Assert;

import org.junit.Test;
import org.resurged.QueryObjectFactory;
import org.resurged.impl.Log;
import org.resurged.jdbc.DataSet;
import org.resurged.test.model.PersonDao;
import org.resurged.test.model.PersonKey;
import org.resurged.test.util.AbstractTestCase;
import org.resurged.test.util.Generator;
import org.resurged.test.util.Vendor;


public class AutoIncrTest extends AbstractTestCase {
	PersonDao dao=null;

	public AutoIncrTest(Vendor vendor, Generator generator) {
		super(vendor, generator);
	}
	
	public void init() throws Exception{
    	if(vendor!=Vendor.MySql)
    		return;
    	
		dao = QueryObjectFactory.createQueryObject(PersonDao.class, getConnection(), configuration);
		Log.info(this, "PersonDao loaded");

		int createResult =(vendor==Vendor.MySql)? dao.createTableAutoIncrMySql() : dao.createTableAutoIncr();
		Log.info(this, "Table create, rows affected: " + createResult);
	}

    public void cleanup() throws Exception{
    	if(vendor!=Vendor.MySql)
    		return;
    	
		int dropResult = dao.dropTable();
		Log.info(this, "Table dropped, rows affected: " + dropResult);
    }

    @Test
	public void testReturnKeys(){
    	if(vendor!=Vendor.MySql)
    		return;
    	
//		DataSet<PersonKey> keys = dao.insertReturnKeysNoKeys("Arthur", "Dent");
//		for(PersonKey key : keys)
//			System.out.println(key.id);

    	DataSet<PersonKey> keys = dao.insertReturnKeysDriversDefined("Arthur", "Dent");
		for(PersonKey key : keys)
			Assert.assertEquals(1, key.id);

//		keys = dao.insertReturnKeysColSpecified("Arthur", "Dent");
//		for(PersonKey key : keys)
//			System.out.println(key.id);
	}
    
}
