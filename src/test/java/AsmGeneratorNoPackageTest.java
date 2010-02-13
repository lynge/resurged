

import org.resurged.QueryObjectFactory;
import org.resurged.classgen.asm.AsmGenerator;

public class AsmGeneratorNoPackageTest extends AbstractNoPackageTestCase {
	public AsmGeneratorNoPackageTest(){
		QueryObjectFactory.setGenerator(new AsmGenerator());
	}
}
