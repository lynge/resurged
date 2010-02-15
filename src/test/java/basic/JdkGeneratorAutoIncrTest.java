package basic;

import org.resurged.impl.classgen.jdk6.JdkGenerator;

public class JdkGeneratorAutoIncrTest extends AbstractAutoIncrTestCase {
	public JdkGeneratorAutoIncrTest(){
		configuration.setGenerator(new JdkGenerator());
	}
}
