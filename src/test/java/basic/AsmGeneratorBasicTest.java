package basic;

import org.resurged.QueryObjectFactory;
import org.resurged.classgen.asm.AsmGenerator;

public class AsmGeneratorBasicTest extends AbstractBasicTestCase {
	public AsmGeneratorBasicTest(){
		QueryObjectFactory.setGenerator(new AsmGenerator());
	}
}
