package basic;

import org.resurged.QueryObjectFactory;
import org.resurged.classgen.jdk6.JdkGenerator;

public class JdkGeneratorBasicTest extends AbstractBasicTestCase {
	public JdkGeneratorBasicTest(){
		QueryObjectFactory.setGenerator(new JdkGenerator());
	}
}
