

import org.resurged.impl.classgen.asm.AsmGenerator;

public class AsmGeneratorNoPackageTest extends AbstractNoPackageTestCase {
	public AsmGeneratorNoPackageTest(){
		configuration.setGenerator(new AsmGenerator());
	}
}
