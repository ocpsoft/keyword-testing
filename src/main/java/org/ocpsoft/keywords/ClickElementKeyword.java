package org.ocpsoft.keywords;

import java.io.PrintStream;
import java.util.ArrayList;

public class ClickElementKeyword implements Keyword {

	@Override
	public String getShortName() {
		return "ClickElement";
	}

	@Override
	public KEYWORD_PROCESS_TYPES getProcessType(){
		return KEYWORD_PROCESS_TYPES.MethodCall;
	}
	
	@Override
	public String performKeyword(String testPath, ArrayList<String> inputValues) {
		return "";
	}

	@Override
	public String getAdditionalInputParams(){
		return "";
	}
	
	@Override
	public String createKeywordHelperMethod(PrintStream writetoTest) {
		try{
			writetoTest.append("\n\tpublic static void ClickElement(DefaultSelenium browser, List<String> inputValues) {");
			writetoTest.append("\n\t\tbrowser.click(inputValues.get(0) + \"=\" + inputValues.get(1));");
			writetoTest.append("\n\t\tif(inputValues.get(0).equals(\"link\")){");
			writetoTest.append("\n\t\t\tbrowser.waitForFrameToLoad(inputValues.get(2), \"15000\");");
			writetoTest.append("\n\t\t}");
			writetoTest.append("\n\t}");
			return "SUCCESS";
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
