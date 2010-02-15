package basic;

import org.resurged.impl.classgen.asm.AsmGenerator;

public class AsmGeneratorAutoIncrTest extends AbstractAutoIncrTestCase {
	public AsmGeneratorAutoIncrTest(){
		configuration.setGenerator(new AsmGenerator());
	}
}
