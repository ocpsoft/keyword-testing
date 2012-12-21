package org.ocpsoft.keywords;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class VerifyObjectIsDisplayedKeyword implements Keyword {

	@Override
	public String getType() {
		return "VerifyObjectIsDisplayed";
	}

	@Override
	public String addInstruction(String testPath, String message, String elementTest,
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
						"\t\tAssert.assertTrue(\"" + message + "\",\n" +
						"\t\t\tbrowser.isElementPresent(\"xpath=//" + elementTest + "\"));\n");
				writetoTest.close();
				return "SUCCESS";
			}
		}
		catch (Exception e) {
			System.err.println("Failure in doVerify: " + e);
			return "FAILURE in Verify Instruction: " + e;
		}
	}

	/* EXAMPLE:
	 *  Assert.assertTrue("User should be on MyInfo Page!",
     *      browser.isElementPresent("xpath=//div[@id='myFBdata']"));
	 */
	
}
