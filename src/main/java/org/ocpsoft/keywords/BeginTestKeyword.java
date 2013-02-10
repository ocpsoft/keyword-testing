package org.ocpsoft.keywords;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Visibility;
import org.jboss.forge.parser.java.util.Formatter;
import org.junit.Test;

import com.ocpsoft.constants.InputConstants;

public class BeginTestKeyword implements Keyword {

	@Override
	public String getShortName() {
		return "BeginTest";
	}

	@Override
	public KEYWORD_PROCESS_TYPES getProcessType(){
		return KEYWORD_PROCESS_TYPES.DirectProcess;
	}
	
	@Override
	@Deprecated
	public String getAdditionalInputParams(){
		return "";
	}

	@Override
	public void createKeywordHelperMethod(JavaClass helperClass){
		helperClass.addMethod()
          .setName("BeginTest")
          .setStatic(true)
          .setVisibility(Visibility.PUBLIC)
          .setBody( "JavaClass testClass;" +
//        			"try{" +
//        			"	File testClassFile = new File(rootPath" +
//        			"			+ inputValues.get(1));" +
//        			"	testClass = (JavaClass) JavaParser.parse(testClassFile);" +
//        			"}" +
//        			"catch (Exception e) {" +
//        			"	System.out.println(\"Error in trying to get the testClass File - e:\" + e);" +
//        			"	return;" +
//        			"}" +
        			"testClass.addMethod().setName(inputValues.get(0))" +
        			"	.setVisibility(Visibility.PUBLIC).setReturnTypeVoid()" +
        			"	.addThrows(InterruptedException.class)" +
        			"	.addAnnotation(Test.class);"
        		  )
        		  
          .setReturnTypeVoid()
          .setParameters("DefaultSelenium browser, List inputValues");		
	}

	
	@Override
	public String performKeyword(JavaClass testClass, ArrayList<String> inputValues) {
		
		testClass.addMethod().setName(inputValues.get(0))
				.setVisibility(Visibility.PUBLIC).setReturnTypeVoid()
				.addThrows(InterruptedException.class)
				.setBody("")
				.addAnnotation(Test.class);
		
		try {
			PrintStream writetoTest = new PrintStream(new FileOutputStream(
					InputConstants.ROOT_FILE_PATH + testClass.getName() + ".java"));
			writetoTest.print(Formatter.format(testClass)); //TODO: This doesn't work, low priority to fix
			writetoTest.close();
		} catch (Exception e) {
			System.err.println("Failure in create new Test Case: " + e);
			return "ERROR: Could not create new Test.";
		}
		
		System.out.println("SUCCESS: New Test [" + inputValues.get(0) + "] created successfully.");
		return "SUCCESS: New Test [" + inputValues.get(0) + "] created successfully.";
		
		
		/*OLD - NON-Parser way
		 * try{
			File f = new File(testPath);
			if(!f.exists()) { 
				return "FAILURE: ClassFile does not exist, can not add instruction.  Fix path of: " + testPath;
			}
			else{
				PrintStream writetoTest = new PrintStream(
						new FileOutputStream(testPath, true)); 
				writetoTest.append("\n" +
						"\t@Test\n" +
						"\tpublic void "+ inputValues.get(0) +"() throws InterruptedException {//Begin Test Case\n");
				
				writetoTest.close();
				System.out.println("\nSUCCESS - Test now has beginning!!!");
				return "SUCCESS";
			}
		}
		catch (Exception e) {
			System.err.println("Failure in doBeginTest: " + e);
			return "FAILURE in Beginning Class Instruction: " + e;
		}
		*/
	}

}
