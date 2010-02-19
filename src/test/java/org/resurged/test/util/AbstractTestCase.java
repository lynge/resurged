package org.resurged.test.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collection;

import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedDataSource40;
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
	public static final String[] VENDOR_NAMES={"DERBY","MYSQL","POSTGRES","ORACLE"};
	public static final int DERBY=0, MYSQL=1, POSTGRES=2, ORACLE=3;
	
	public static final String[] GENERATOR_NAMES={"ASM","JDK"};
	public static final int ASM=0, JDK=1;
	
	public static final int[] VENDORS={DERBY};
	public static final int[] GENERATORS={ASM, JDK};
	
    protected int generator;
	protected int vendor;
	
	protected Config configuration=new Config();
	protected Connection con = null;
	protected DataSource ds = null;
	
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
		
		switch (vendor) {
			case MYSQL:
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/resurged","root", "no1knows");
				Log.info(this, "MySql connection opened");
				break;
			default:
				Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
				con = DriverManager.getConnection("jdbc:derby:MyDbTest;create=true");
				Log.info(this, "Derby connection opened");

				ds = new EmbeddedDataSource40(); 
				EmbeddedDataSource40 derbyDs = (EmbeddedDataSource40) ds;
				derbyDs.setDatabaseName("MyDbTest;create=true");
				derbyDs.setCreateDatabase("create");
				Log.info(this, "Derby datasource opened");
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

}
