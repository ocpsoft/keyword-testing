package org.ocpsoft.keywords;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class ClickKeyword implements Keyword {

	@Override
	public String getType() {
		return "Click";
	}

	@Override
	public String addInstruction(String testPath, String linkText, String linkPath,
			String desc3, String desc4, String value) {
		try{
			File f = new File(testPath);
			if(!f.exists()) { 
				return "FAILURE: ClassFile does not exist, can not add instruction.  Fix path of: " + testPath;
			}
			else{
				PrintStream writetoTest = new PrintStream(
					     new FileOutputStream(testPath, true)); 
				writetoTest.append("\n" +
									"\t\tbrowser.click(\"link=" + linkText + "\");\n" +
									"\t\tbrowser.waitForFrameToLoad(\"" + linkPath + "\", \"15000\");\n");
				writetoTest.close();
				return "SUCCESS";
			}
		}
		catch (Exception e) {
			System.err.println("Failure in doClick: " + e);
			return "FAILURE in Click Instruction: " + e;
		}
	}

	/* EXAMPLE:
	 *  browser.click("link=Get Your Info Here");
     *  browser.waitForFrameToLoad("myInfo.html", "15000");
	 */
	
}
