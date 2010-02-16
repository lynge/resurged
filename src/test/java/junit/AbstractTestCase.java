package junit;
import java.sql.Connection;
import java.sql.DriverManager;

import junit.framework.TestCase;

import org.resurged.Config;
import org.resurged.impl.Log;

public abstract class AbstractTestCase extends TestCase {
	protected Config configuration=new Config();
	protected Connection con = null;
	protected boolean useMysql=false;
	
	public AbstractTestCase(){
		Config.setLoggingStrategy(new Log.ConsoleLogger());
	}
	
	protected void openConnection() throws Exception {
		if(useMysql){
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/resurged","root", "no1knows");
			Log.info(this, "MySql connection opened");
		} else {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
			con = DriverManager.getConnection("jdbc:derby:MyDbTest;create=true");
			Log.info(this, "Derby connection opened");
		}
	}
	
	protected void closeConnection() throws Exception {
		if (con != null)
			con.close();
		Log.info(this, "Connection closed");

		try {
			if(!useMysql)
				DriverManager.getConnection("jdbc:derby:MyDbTest;shutdown=true");
		} catch (Exception e) {}
	}
}
