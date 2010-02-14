

import org.resurged.impl.classgen.jdk6.JdkGenerator;

public class JdkGeneratorNoPackageTest extends AbstractNoPackageTestCase {
	public JdkGeneratorNoPackageTest(){
		configuration.setGenerator(new JdkGenerator());
	}
}
