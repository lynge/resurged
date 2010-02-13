package basic;

import java.sql.Connection;
import java.sql.DriverManager;

import junit.framework.TestCase;

import org.resurged.QueryObjectFactory;
import org.resurged.jdbc.DataSet;

public abstract class AbstractDataTypesTestCase extends TestCase {
	protected Connection con = null;
	private DataTypesDao dao;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		con = DriverManager.getConnection("jdbc:derby:MyDbTest;create=true");
		
		dao = QueryObjectFactory.createQueryObject(DataTypesDao.class, con);
		System.out.println("DataTypesDao loaded");

		int createResult = dao.createTable();
		System.out.println("Table create, rows affected: " + createResult);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		int dropResult = dao.dropTable();
		System.out.println("Table dropped, rows affected: " + dropResult);

		if (con != null)
			con.close();
		System.out.println("Connection closed");

		try {
			DriverManager.getConnection("jdbc:derby:MyDbTest;shutdown=true");
		} catch (Exception e) {}
	}
	
	public void testDataTypes(){
//		int rowsAffected = dao.insert(false, new Boolean(true), (byte)10, new Byte((byte)11), 'r', new Character('R'), (short)20, new Short((short)21), 30, new Integer(31), 40L, new Long(41L), 50.1f, new Float(51.2f), 60.3d, new Double(61.4), "foo", "FOO");
		int rowsAffected = dao.insert(false, new Boolean(true), (byte)10, new Byte((byte)11), (short)20, new Short((short)21), 30, new Integer(31), 40L, new Long(41L), 50.1f, new Float(51.2f), 60.3d, new Double(61.4), "foo", "FOO");
		System.out.println("Row inserted, rows affected: " + rowsAffected);

		assertEquals(1, rowsAffected);
		
		DataSet<DataTypes> all = dao.getAll();
		assertEquals(1, all.size());
		for (DataTypes dto : all) {
			assertEquals(false, dto.isBoolean1());
			assertEquals(new Boolean(true), dto.getBoolean2());
			assertEquals((byte)10, dto.getByte1());
			assertEquals(new Byte((byte)11), dto.getByte2());
//			assertEquals('r', dto.getChar1());
//			assertEquals(new Character('R'), dto.getChar2());
			assertEquals((short)20, dto.getShort1());
			assertEquals(new Short((short)21), dto.getShort2());
			assertEquals(30, dto.getInt1());
			assertEquals(new Integer(31), dto.getInt2());
			assertEquals(40L, dto.getLong1());
			assertEquals(new Long(41L), dto.getLong2());
			assertEquals(50.1f, dto.getFloat1());
			assertEquals(new Float(51.2f), dto.getFloat2());
			assertEquals(60.3d, dto.getDouble1());
			assertEquals(new Double(61.4), dto.getDouble2());
			assertEquals("foo", dto.getString1().trim());
			assertEquals("FOO", dto.getString2());
			System.out.println(dto.toString());
		}

		rowsAffected -= dao.deleteAll();
		System.out.println("Row deleted, rows left: " + rowsAffected);

		assertEquals(0, rowsAffected);
	}
}
