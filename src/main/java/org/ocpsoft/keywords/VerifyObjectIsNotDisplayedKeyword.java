package org.ocpsoft.keywords;

import java.io.PrintStream;
import java.util.ArrayList;

public class VerifyObjectIsNotDisplayedKeyword implements Keyword {

	@Override
	public String getShortName() {
		return "VerifyObjectIsNotDisplayed";
	}

	@Override
	public KEYWORD_PROCESS_TYPES getProcessType(){
		return KEYWORD_PROCESS_TYPES.MethodCall;
	}

	@Override
	public String getAdditionalInputParams(){
		return "";
	}
	
	@Override
	public String performKeyword(String testPath, ArrayList<String> inputValues) {
		return "";
	}
	
	@Override
	public String createKeywordHelperMethod(PrintStream writetoTest) {
		try{
			writetoTest.append("\n\tpublic static void VerifyObjectIsNotDisplayed(DefaultSelenium browser, List<String> inputValues) {");
			writetoTest.append("\n" +
					"\t\tAssert.assertFalse(inputValues.get(0),\n" +
					"\t\t\tbrowser.isElementPresent(\"xpath=//\" + inputValues.get(1)));");
			writetoTest.append("\n\t}");
			return "SUCCESS";
		}
		catch (Exception e) {
			System.err.println("Failure in doVerify: " + e);
			return "FAILURE in Verify Instruction: " + e;
		}
	}

	/* EXAMPLE:
	 *  Assert.assertFalse("User should not see [Error: Invalid action]",
     *      browser.isElementPresent("xpath=//div[@id='myFBdata']"));
	 */
	
}
