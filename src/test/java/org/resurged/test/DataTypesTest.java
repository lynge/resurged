package org.resurged.test;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.resurged.QueryObjectFactory;
import org.resurged.impl.Log;
import org.resurged.jdbc.DataSet;
import org.resurged.test.model.DataTypes;
import org.resurged.test.model.DataTypesDao;
import org.resurged.test.util.AbstractTestCase;


public class DataTypesTest extends AbstractTestCase {
	DataTypesDao dao=null;

	public DataTypesTest(int vendor, int generator) {
		super(vendor, generator);
	}
	
	public void init() throws Exception{
    	if(vendor!=DERBY)
    		return;
    	
		dao = QueryObjectFactory.createQueryObject(DataTypesDao.class, con, configuration);
		Log.info(this, "DataTypesDao loaded");

		int createResult = dao.createTable();
		Log.info(this, "Table create, rows affected: " + createResult);
	}

    public void cleanup() throws Exception{
    	if(vendor!=DERBY)
    		return;
    	
		int dropResult = dao.dropTable();
		Log.info(this, "Table dropped, rows affected: " + dropResult);
    }

    @Test
	public void testDataTypes() throws Exception{
    	if(vendor!=DERBY)
    		return;
    	
//		int rowsAffected = dao.insert(false, new Boolean(true), (byte)10, new Byte((byte)11), 'r', new Character('R'), (short)20, new Short((short)21), 30, new Integer(31), 40L, new Long(41L), 50.1f, new Float(51.2f), 60.3d, new Double(61.4), "foo", "FOO");
		Date now=new Date();
		java.sql.Date nowSqlDate=new java.sql.Date(now.getTime());
		
		int rowsAffected = dao.insert(false, new Boolean(true), Byte.MIN_VALUE, new Byte(Byte.MAX_VALUE), Short.MIN_VALUE, new Short((short)Short.MAX_VALUE), Integer.MIN_VALUE, new Integer(Integer.MAX_VALUE), Long.MIN_VALUE, new Long(Long.MAX_VALUE), 10.0f, new Float(Float.MAX_VALUE), 20.0d, new Double(30.0d), "foo", "זרו", now, nowSqlDate
);
		Log.info(this, "Row inserted, rows affected: " + rowsAffected);

		Assert.assertEquals(1, rowsAffected);
		
		DataSet<DataTypes> all = dao.getAll();
		Assert.assertEquals(1, all.size());
		for (DataTypes dto : all) {
			Assert.assertEquals(false, dto.isBoolean1());
			Assert.assertEquals(new Boolean(true), dto.getBoolean2());
			Assert.assertEquals(Byte.MIN_VALUE, dto.getByte1());
			Assert.assertEquals(new Byte(Byte.MAX_VALUE), dto.getByte2());
//			assertEquals('r', dto.getChar1());
//			assertEquals(new Character('R'), dto.getChar2());
			Assert.assertEquals(Short.MIN_VALUE, dto.getShort1());
			Assert.assertEquals(new Short(Short.MAX_VALUE), dto.getShort2());
			Assert.assertEquals(Integer.MIN_VALUE, dto.getInt1());
			Assert.assertEquals(new Integer(Integer.MAX_VALUE), dto.getInt2());
			Assert.assertEquals(Long.MIN_VALUE, dto.getLong1());
			Assert.assertEquals(new Long(Long.MAX_VALUE), dto.getLong2());
			Assert.assertEquals(10.0f, dto.getFloat1(), 0d);
			Assert.assertEquals(new Float(Float.MAX_VALUE), dto.getFloat2());
			Assert.assertEquals(20.0d, dto.getDouble1(), 0d);
			Assert.assertEquals(new Double(30.0d), dto.getDouble2());
			Assert.assertEquals("foo", dto.getString1().trim());
			Assert.assertEquals("זרו", dto.getString2());
			
//			assertEquals(now, dto.getDate1());
//			assertEquals(nowSqlDate, dto.getDate2());
			
			Log.info(this, dto.toString());
		}

		rowsAffected -= dao.deleteAll();
		Log.info(this, "Row deleted, rows left: " + rowsAffected);

		Assert.assertEquals(0, rowsAffected);
	}
}
