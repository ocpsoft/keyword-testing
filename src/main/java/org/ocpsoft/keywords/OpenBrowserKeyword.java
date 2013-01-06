package org.ocpsoft.keywords;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class OpenBrowserKeyword implements Keyword {

	@Override
	public String getType() {
		return "OpenBrowser";
	}

	@Override
	public String addInstruction(String testPath, ArrayList<String> inputValues) {
		try{
			File f = new File(testPath);
			if(!f.exists()) { 
				return "FAILURE: ClassFile does not exist, can not add instruction.  Fix path of: " + testPath;
			}
			else{
				PrintStream writetoTest = new PrintStream(
					     new FileOutputStream(testPath, true)); 
				writetoTest.append("\t\tbrowser.open(deploymentURL + \"" + inputValues.get(0) + "\");\n");
				writetoTest.close();
				return "SUCCESS";
			}
		}
		catch (Exception e) {
			System.err.println("Failure in doOpenBrowser: " + e);
			return "FAILURE in OpenBrowser Instruction: " + e;
		}
	}
	
	/* EXAMPLE:
	 * browser.open(deploymentURL + "index.html");
	 */

}
