package basic;

import org.resurged.QueryObjectFactory;
import org.resurged.classgen.asm.AsmGenerator;

public class AsmGeneratorDataTypesTest extends AbstractDataTypesTestCase {
	public AsmGeneratorDataTypesTest(){
		QueryObjectFactory.setGenerator(new AsmGenerator());
	}
}
