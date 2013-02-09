package org.ocpsoft.keywords;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Visibility;

public class BeginTestKeyword implements Keyword {

	@Override
	public String getShortName() {
		return "BeginTest";
	}

	@Override
	public KEYWORD_PROCESS_TYPES getProcessType(){
		return KEYWORD_PROCESS_TYPES.MethodCall;
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
	@Deprecated
	public String performKeyword(String testPath, ArrayList<String> inputValues) {
		try{
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
	}

}
