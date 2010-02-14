package basic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;

import junit.AbstractTestCase;

import org.resurged.Log;
import org.resurged.QueryObjectFactory;
import org.resurged.jdbc.DataSet;

public abstract class AbstractDataTypesTestCase extends AbstractTestCase {
	protected Connection con = null;
	private DataTypesDao dao;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		con = DriverManager.getConnection("jdbc:derby:MyDbTest;create=true");
		
		dao = QueryObjectFactory.createQueryObject(DataTypesDao.class, con);
		Log.info(this, "DataTypesDao loaded");

		int createResult = dao.createTable();
		Log.info(this, "Table create, rows affected: " + createResult);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		int dropResult = dao.dropTable();
		Log.info(this, "Table dropped, rows affected: " + dropResult);

		if (con != null)
			con.close();
		Log.info(this, "Connection closed");

		try {
			DriverManager.getConnection("jdbc:derby:MyDbTest;shutdown=true");
		} catch (Exception e) {}
	}
	
	public void testDataTypes(){
//		int rowsAffected = dao.insert(false, new Boolean(true), (byte)10, new Byte((byte)11), 'r', new Character('R'), (short)20, new Short((short)21), 30, new Integer(31), 40L, new Long(41L), 50.1f, new Float(51.2f), 60.3d, new Double(61.4), "foo", "FOO");
		Date now=new Date();
		java.sql.Date nowSqlDate=new java.sql.Date(now.getTime());
		
		int rowsAffected = dao.insert(false, new Boolean(true), Byte.MIN_VALUE, new Byte(Byte.MAX_VALUE), Short.MIN_VALUE, new Short((short)Short.MAX_VALUE), Integer.MIN_VALUE, new Integer(Integer.MAX_VALUE), Long.MIN_VALUE, new Long(Long.MAX_VALUE), 10.0f, new Float(Float.MAX_VALUE), 20.0d, new Double(30.0d), "foo", "זרו", now, nowSqlDate
);
		Log.info(this, "Row inserted, rows affected: " + rowsAffected);

		assertEquals(1, rowsAffected);
		
		DataSet<DataTypes> all = dao.getAll();
		assertEquals(1, all.size());
		for (DataTypes dto : all) {
			assertEquals(false, dto.isBoolean1());
			assertEquals(new Boolean(true), dto.getBoolean2());
			assertEquals(Byte.MIN_VALUE, dto.getByte1());
			assertEquals(new Byte(Byte.MAX_VALUE), dto.getByte2());
//			assertEquals('r', dto.getChar1());
//			assertEquals(new Character('R'), dto.getChar2());
			assertEquals(Short.MIN_VALUE, dto.getShort1());
			assertEquals(new Short(Short.MAX_VALUE), dto.getShort2());
			assertEquals(Integer.MIN_VALUE, dto.getInt1());
			assertEquals(new Integer(Integer.MAX_VALUE), dto.getInt2());
			assertEquals(Long.MIN_VALUE, dto.getLong1());
			assertEquals(new Long(Long.MAX_VALUE), dto.getLong2());
			assertEquals(10.0f, dto.getFloat1());
			assertEquals(new Float(Float.MAX_VALUE), dto.getFloat2());
			assertEquals(20.0d, dto.getDouble1());
			assertEquals(new Double(30.0d), dto.getDouble2());
			assertEquals("foo", dto.getString1().trim());
			assertEquals("זרו", dto.getString2());
			
//			assertEquals(now, dto.getDate1());
//			assertEquals(nowSqlDate, dto.getDate2());
			
			Log.info(this, dto.toString());
		}

		rowsAffected -= dao.deleteAll();
		Log.info(this, "Row deleted, rows left: " + rowsAffected);

		assertEquals(0, rowsAffected);
	}
}
