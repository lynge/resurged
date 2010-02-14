package junit;
import junit.framework.TestCase;

import org.resurged.Config;
import org.resurged.impl.Log;

public abstract class AbstractTestCase extends TestCase {
	protected Config configuration=new Config();
	
	public AbstractTestCase(){
		Config.setLoggingStrategy(new Log.ConsoleLogger());
	}
}
