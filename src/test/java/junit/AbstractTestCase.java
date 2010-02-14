package junit;
import junit.framework.TestCase;
import org.resurged.Log;

public abstract class AbstractTestCase extends TestCase {
	public AbstractTestCase(){
		Log.setLogger(new Log.ConsoleLogger());
	}
}
