package basic;

import org.resurged.QueryObjectFactory;
import org.resurged.classgen.jdk6.JdkGenerator;

public class JdkGeneratorDataTypesTest extends AbstractDataTypesTestCase {
	public JdkGeneratorDataTypesTest(){
		QueryObjectFactory.setGenerator(new JdkGenerator());
		
//		ConsoleHandler handler = new ConsoleHandler();
//		handler.setLevel(Level.FINEST);
//		Log.getLogger().addHandler(handler);
//		Handler[] handlers = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).getHandlers();
//		for (int i = 0; i < handlers.length; i++) {
//			handlers[i].setLevel(Level.FINEST);
//		}
//		Log.debug(this, "test");
	}
}
