

import org.resurged.QueryObjectFactory;
import org.resurged.classgen.jdk6.JdkGenerator;

public class JdkGeneratorNoPackageTest extends AbstractNoPackageTestCase {
	public JdkGeneratorNoPackageTest(){
		QueryObjectFactory.setGenerator(new JdkGenerator());
	}
}
