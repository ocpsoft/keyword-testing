package org.ocpsoft.keywords;

import java.util.ArrayList;

import org.jboss.forge.parser.java.JavaClass;

public class EndTestKeyword implements Keyword {

	@Override
	public String getShortName() {
		return "EndTest";
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
	
	public void createKeywordHelperMethod(JavaClass helperClass){
	}
	
	@Override
	@Deprecated
	public String performKeyword(JavaClass testClass, ArrayList<String> inputValues) {
//		try{
//			File f = new File(testPath);
//			if(!f.exists()) { 
//				return "FAILURE: ClassFile does not exist, can not add instruction.  Fix path of: " + testPath;
//			}
//			else{
//				PrintStream writetoTest = new PrintStream(
//					     new FileOutputStream(testPath, true)); 
//				writetoTest.append("\n\t}//End Test Case\n");
//				writetoTest.close();
				return "SUCCESS";
//			}
//		}
//		catch (Exception e) {
//			System.err.println("Failure in doEndTest: " + e);
//			return "FAILURE in Ending Test Instruction: " + e;
//		}
	}

}
