package org.ocpsoft.keywords;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class ClickElementKeyword implements Keyword {

	@Override
	public String getType() {
		return "ClickElement";
	}

	@Override
	public String addInstruction(String testPath, String locatorType, String elementkey,
			String expectedLinkPath, String desc4, String value) {
		try{
			File f = new File(testPath);
			if(!f.exists()) { 
				return "FAILURE: ClassFile does not exist, can not add instruction.  Fix path of: " + testPath;
			}
			else{
				PrintStream writetoTest = new PrintStream(
					     new FileOutputStream(testPath, true)); 
				writetoTest.append("\n" +
									"\t\tbrowser.click(\"" + locatorType + "=" + elementkey + "\");\n");
				if(locatorType.equals("link")){
					writetoTest.append("\t\tbrowser.waitForFrameToLoad(\"" + expectedLinkPath + "\", \"15000\");\n");
				}
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
	 *  browser.click("id=myButton");
     *  or
     *  browser.click("name=myButton");
     *  or
     *  browser.click("link=LinkTextHere");
     *  browser.waitForFrameToLoad("myInfo.html", "15000");
     *  or
     *  browser.click("css=input[name=myButton]");
     *  or
     *  browser.click("xpath=//input[@name=myButton' and @type='submit']");
	 */
	
}
