package basic;

import org.resurged.impl.classgen.asm.AsmGenerator;

public class AsmGeneratorDataSourceTest extends AbstractDataSourceTestCase {
	public AsmGeneratorDataSourceTest(){
		configuration.setGenerator(new AsmGenerator());
	}
}
