package junit;
import junit.framework.TestCase;
import org.resurged.Log;

public class AbstractTestCase extends TestCase {
	public AbstractTestCase(){
		Log.setLogger(new Log.ConsoleLogger());
	}
}
