package basic;

import org.resurged.impl.classgen.jdk6.JdkGenerator;

public class JdkGeneratorDataSourceTest extends AbstractDataSourceTestCase {
	public JdkGeneratorDataSourceTest(){
		configuration.setGenerator(new JdkGenerator());
	}
}
