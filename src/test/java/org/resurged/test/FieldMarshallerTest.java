package org.resurged.test;

import org.junit.Assert;
import org.junit.Test;
import org.resurged.QueryObjectFactory;
import org.resurged.impl.Log;
import org.resurged.impl.marshalling.FieldMarshallerFactory;
import org.resurged.jdbc.DataSet;
import org.resurged.test.model.oracle.OracleDataTypesDao;
import org.resurged.test.model.oracle.OracleDataTypesPojo;
import org.resurged.test.util.AbstractTestCase;
import org.resurged.test.util.Generator;
import org.resurged.test.util.MyFieldMarshaller;
import org.resurged.test.util.Vendor;


public class FieldMarshallerTest extends AbstractTestCase {
	OracleDataTypesDao dao=null;

	public FieldMarshallerTest(Vendor vendor, Generator generator) {
		super(vendor, generator);
	}
	
	public void init() throws Exception{
    	if(vendor!=Vendor.Oracle)
    		return;
    	
    	configuration.setFieldMarshallerClass(MyFieldMarshaller.class);
		dao = QueryObjectFactory.createQueryObject(OracleDataTypesDao.class, getConnection(), MyFieldMarshaller.class);
		Log.info(this, "OracleDao loaded");

		int createResult = dao.createTable();
		Log.info(this, "Table create, rows affected: " + createResult);
	}

    public void cleanup() throws Exception{
    	if(vendor!=Vendor.Oracle)
    		return;
    	
		int dropResult = dao.dropTable();
		Log.info(this, "Table dropped, rows affected: " + dropResult);
    }

    @Test
	public void testDataTypes() throws Exception{
    	if(vendor!=Vendor.Oracle)
    		return;
    	
		int rowsAffected = dao.insert("foo", Long.MAX_VALUE);

		Log.info(this, "Row inserted, rows affected: " + rowsAffected);

		Assert.assertEquals(1, rowsAffected);
		
		DataSet<OracleDataTypesPojo> all = dao.getAll();
		Assert.assertEquals(1, all.size());
		for (OracleDataTypesPojo dto : all) {
			Assert.assertEquals("foo", dto.getStr().trim());
			Assert.assertEquals(new Long(Long.MAX_VALUE), dto.getLng());
			
			Log.info(this, dto.toString());
		}

		rowsAffected -= dao.deleteAll();
		Log.info(this, "Row deleted, rows left: " + rowsAffected);

		Assert.assertEquals(0, rowsAffected);
	}
}