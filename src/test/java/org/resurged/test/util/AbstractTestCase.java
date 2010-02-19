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
import org.resurged.impl.classgen.asm.AsmGenerator;
import org.resurged.impl.classgen.jdk6.JdkGenerator;

@RunWith(Parameterized.class)
public abstract class AbstractTestCase { 
    private static final String[][] CONNECTION_PROPERTIES={
    	{"org.apache.derby.jdbc.EmbeddedDriver", "jdbc:derby:MyDbTest;create=true", "", ""},
    	{"com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/resurged", "resurged", "resurged"},
    	{"", "", "", ""},
    	{"oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@Mlocalhost:1521:resurged", "resurged", "resurged"}
    };
    
	public static final String[] VENDOR_NAMES={"DERBY","MYSQL","POSTGRES","ORACLE"};
	public static final int DERBY=0, MYSQL=1, POSTGRES=2, ORACLE=3;
	
	public static final String[] GENERATOR_NAMES={"ASM","JDK"};
	public static final int ASM=0, JDK=1;
	
	public static final int[] VENDORS={DERBY};
	public static final int[] GENERATORS={ASM, JDK};
	
    protected int generator;
	protected int vendor;
	
	protected Config configuration=new Config();
	private Connection con = null;
	private DataSource ds = null;
	
	public abstract void init() throws Exception;
    public abstract void cleanup() throws Exception;

    public AbstractTestCase(int vendor, int generator) {
		this.vendor = vendor;
		this.generator = generator;
		Config.setLoggingStrategy(new Log.ConsoleLogger());
    }
    
    @Before
    public void setup() throws Exception{
    	Log.info(this, "================= " + this.getClass().getSimpleName() + "-" + GENERATOR_NAMES[generator] + "@" + VENDOR_NAMES[vendor] + "=================");

    	switch (generator) {
			case ASM:
				configuration.setGenerator(new AsmGenerator());
				break;
			default:
				configuration.setGenerator(new JdkGenerator());
				break;
		}
    	
		init();
    }
    
    @After
    public void teardown() throws Exception{
    	cleanup();
    	
		if (con != null)
			con.close();
		Log.info(this, "Connection closed");

		try{
			if(vendor==DERBY)
				DriverManager.getConnection("jdbc:derby:MyDbTest;shutdown=true");
		}catch (Exception e) {}
    }

    @Parameters
    public static Collection<Object[]> data() {
        Collection<Object[]> data = new ArrayList<Object[]>();
        for (int i = 0; i < VENDORS.length; i++) {
        	for (int j = 0; j < GENERATORS.length; j++) {
                data.add(new Object[]{VENDORS[i], GENERATORS[j]});
			}
			
		}
        return data;
    }
    
    public Connection getConnection() throws Exception{
		if(con==null){
			Class.forName(CONNECTION_PROPERTIES[vendor][0]);
			con = DriverManager.getConnection(CONNECTION_PROPERTIES[vendor][1],CONNECTION_PROPERTIES[vendor][2], CONNECTION_PROPERTIES[vendor][3]);
			Log.info(this, VENDOR_NAMES[vendor] + " connection opened");
		}
    	return con;
    }
    
	public DataSource getDs() {
		if(ds==null){
			BasicDataSource basicDs = new BasicDataSource();
			basicDs.setDriverClassName(CONNECTION_PROPERTIES[vendor][0]);
			basicDs.setUrl(CONNECTION_PROPERTIES[vendor][1]);
			basicDs.setUsername(CONNECTION_PROPERTIES[vendor][2]);
			basicDs.setPassword(CONNECTION_PROPERTIES[vendor][3]);
			ds=basicDs;
			Log.info(this, "DataSource loaded");
		}
		return ds;
	}

}
