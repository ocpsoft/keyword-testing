package org.ocpsoft.keywords;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class EndTestKeyword implements Keyword {

	@Override
	public String getType() {
		return "EndTest";
	}

	@Override
	public String addInstruction(String testPath, String desc1, String desc2,
			String desc3, String desc4, String value) {
		try{
			File f = new File(testPath);
			if(!f.exists()) { 
				return "FAILURE: ClassFile does not exist, can not add instruction.  Fix path of: " + testPath;
			}
			else{
				PrintStream writetoTest = new PrintStream(
					     new FileOutputStream(testPath, true)); 
				writetoTest.append("\t}//End Test Case\n");
				writetoTest.close();
				return "SUCCESS";
			}
		}
		catch (Exception e) {
			System.err.println("Failure in doEndTest: " + e);
			return "FAILURE in Ending Test Instruction: " + e;
		}
	}

}
