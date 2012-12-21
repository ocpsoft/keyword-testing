package org.ocpsoft.keywords;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.invoke.SwitchPoint;

import javax.persistence.criteria.CriteriaBuilder.Case;

public class VerifyObjectPropertyKeyword implements Keyword {

	@Override
	public String getType() {
		return "VerifyObjectProperty";
	}

	@Override
	public String addInstruction(String testPath, String message, String objectType, String xpathOfObject,
			String valueToVerify, String value) {
		try{
			File f = new File(testPath);
			if(!f.exists()) { 
				return "FAILURE: ClassFile does not exist, can not add instruction.  Fix path of: " + testPath;
			}
			else{
				PrintStream writetoTest = new PrintStream(
					     new FileOutputStream(testPath, true)); 
				writetoTest.append("\n\t\t" + "value = getValue(\"" + objectType + "\", \"" + xpathOfObject + "\");");
				writetoTest.append("\n" +
						"\t\tAssert.assertTrue(\"" + message + "\",\n" +
						"\t\t\t\"" + valueToVerify + "\".equals(value));\n");
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
