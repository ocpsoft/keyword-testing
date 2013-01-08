package org.ocpsoft.keywords;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class EndClassKeyword implements Keyword {

	@Override
	public String getShortName() {
		return "EndClass";
	}

	@Override
	public KEYWORD_PROCESS_TYPES getProcessType(){
		return KEYWORD_PROCESS_TYPES.DirectProcess;
	}

	@Override
	public String getAdditionalInputParams(){
		return "";
	}
	
	@Override
	public String createKeywordHelperMethod(PrintStream writetoTest){
		return "";
	}
	
	@Override
	public String performKeyword(String testPath, ArrayList<String> inputValues) {
		try{
			File f = new File(testPath);
			if(!f.exists()) { 
				return "FAILURE: ClassFile does not exist, can not add instruction.  Fix path of: " + testPath;
			}
			else{
				PrintStream writetoTest = new PrintStream(
					     new FileOutputStream(testPath, true)); 
				writetoTest.append("\n}//End Class");
				writetoTest.close();
				return "SUCCESS";
			}
		}
		catch (Exception e) {
			System.err.println("Failure in doEndClass: " + e);
			return "FAILURE in Ending Class Instruction: " + e;
		}
	}

}
