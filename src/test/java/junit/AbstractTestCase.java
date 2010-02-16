package junit;
import java.sql.Connection;
import java.sql.DriverManager;

import junit.framework.TestCase;

import org.resurged.Config;
import org.resurged.impl.Log;

public abstract class AbstractTestCase extends TestCase {
	protected Config configuration=new Config();
	protected Connection con = null;
	
	public AbstractTestCase(){
		Config.setLoggingStrategy(new Log.ConsoleLogger());
	}
	
	protected void openConnection() throws Exception {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		con = DriverManager.getConnection("jdbc:derby:MyDbTest;create=true");
	}
	
	protected void closeConnection() throws Exception {
		if (con != null)
			con.close();
		Log.info(this, "Connection closed");

		try {
			DriverManager.getConnection("jdbc:derby:MyDbTest;shutdown=true");
		} catch (Exception e) {}
	}
}
