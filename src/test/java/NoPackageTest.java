

import org.junit.Test;
import org.resurged.QueryObjectFactory;
import org.resurged.impl.Log;
import org.resurged.test.util.AbstractTestCase;


public class NoPackageTest extends AbstractTestCase {
	NoPackageDao dao=null;

	public NoPackageTest(int vendor, int generator) {
		super(vendor, generator);
	}
	
	public void init() throws Exception{
		dao = QueryObjectFactory.createQueryObject(NoPackageDao.class, getConnection(), configuration);
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
		int insertResult = dao.insert(1, "John", "Doe");
		Log.info(this, "Row inserted, rows affected: " + insertResult);
	}

}
