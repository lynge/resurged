package basic;

import org.resurged.impl.classgen.jdk6.JdkGenerator;

public class JdkGeneratorDataTypesTest extends AbstractDataTypesTestCase {
	public JdkGeneratorDataTypesTest(){
		configuration.setGenerator(new JdkGenerator());
	}
}
