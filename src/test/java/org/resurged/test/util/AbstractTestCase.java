package org.resurged.test.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collection;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.resurged.Config;
import org.resurged.impl.Log;

@RunWith(Parameterized.class)
public abstract class AbstractTestCase { 
    private static final String[][] CONNECTION_PROPERTIES={
    	{"org.apache.derby.jdbc.EmbeddedDriver", 	"jdbc:derby:MyDbTest;create=true", 				  "", ""},
    	{"com.mysql.jdbc.Driver", 					"jdbc:mysql://localhost:3306/resurged", 		  "resurged", "resurged"},
    	{"org.postgresql.Driver", 					"jdbc:postgresql://localhost/postgres", 		  "resurged", "resurged"},
        {"oracle.jdbc.driver.OracleDriver",         "jdbc:oracle:thin:@localhost:1521:resurged", 	  "resurged", "resurged"}
    };
    
	public static final Vendor[] VENDORS={Vendor.Derby, Vendor.MySql, Vendor.Postgres, Vendor.Oracle};
	
	private static Vendor[] vendors;
	protected Vendor vendor;
	
	protected Config configuration=new Config();
	private Connection con = null;
	private DataSource ds = null;
	
	public abstract void init() throws Exception;
    public abstract void cleanup() throws Exception;

    public AbstractTestCase(Vendor vendor) {
		this.vendor = vendor;
		Config.setLoggingStrategy(new Log.ConsoleLogger());
    }
    
    @Before
    public void setup() throws Exception{
    	Log.info(this, "================= " + this.getClass().getSimpleName() + "-" + vendor + "=================");
		init();
    }
    
    @After
    public void teardown() throws Exception{
    	cleanup();
    	
		if (con != null)
			con.close();
		Log.info(this, "Connection closed");

		try{
			if(vendor==Vendor.Derby)
				DriverManager.getConnection("jdbc:derby:MyDbTest;shutdown=true");
		}catch (Exception e) {}
    }

    @Parameters
    public static Collection<Object[]> data() {
    	ArrayList<Vendor> v=new ArrayList<Vendor>();
    	for (int i = 0; i < VENDORS.length; i++) {
			try {
				Class.forName(CONNECTION_PROPERTIES[VENDORS[i].intValue()][0]);
				v.add(VENDORS[i]);
			} catch (Exception e) {}
		}
    	vendors = v.toArray(new Vendor[0]);
    	
        Collection<Object[]> data = new ArrayList<Object[]>();
        for (int i = 0; i < vendors.length; i++) {
        	data.add(new Object[]{vendors[i]});
		}
        return data;
    }
    
    public Connection getConnection() throws Exception{
		if(con==null){
			Class.forName(CONNECTION_PROPERTIES[vendor.intValue()][0]);
			con = DriverManager.getConnection(CONNECTION_PROPERTIES[vendor.intValue()][1],CONNECTION_PROPERTIES[vendor.intValue()][2], CONNECTION_PROPERTIES[vendor.intValue()][3]);
			Log.info(this, vendor + " connection opened");
		}
    	return con;
    }
    
	public DataSource getDs() {
		if(ds==null){
			BasicDataSource basicDs = new BasicDataSource();
			basicDs.setDriverClassName(CONNECTION_PROPERTIES[vendor.intValue()][0]);
			basicDs.setUrl(CONNECTION_PROPERTIES[vendor.intValue()][1]);
			basicDs.setUsername(CONNECTION_PROPERTIES[vendor.intValue()][2]);
			basicDs.setPassword(CONNECTION_PROPERTIES[vendor.intValue()][3]);
			ds=basicDs;
			Log.info(this, "DataSource loaded");
		}
		return ds;
	}

}
