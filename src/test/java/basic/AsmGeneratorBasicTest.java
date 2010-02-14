package basic;

import org.resurged.impl.classgen.asm.AsmGenerator;

public class AsmGeneratorBasicTest extends AbstractBasicTestCase {
	public AsmGeneratorBasicTest(){
		configuration.setGenerator(new AsmGenerator());
	}
//	public static void main(String[] args){
//		try {
//			ASMifierClassVisitor.main(new String[]{"basic.PersonDaoResurged"});
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
