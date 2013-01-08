package org.ocpsoft.keywords;

import java.io.PrintStream;
import java.util.ArrayList;

public class OpenBrowserKeyword implements Keyword {

	@Override
	public String getShortName() {
		return "OpenBrowser";
	}

	@Override
	public KEYWORD_PROCESS_TYPES getProcessType(){
		return KEYWORD_PROCESS_TYPES.MethodCall;
	}

	@Override
	public String getAdditionalInputParams(){
		return ", deploymentURL";
	}
	
	@Override
	public String performKeyword(String testPath, ArrayList<String> inputValues) {
		return "";
	}
	
	@Override
	public String createKeywordHelperMethod(PrintStream writetoTest){
		try{
			writetoTest.append("\n\tpublic static void OpenBrowser(DefaultSelenium browser, List<String> inputValues, URL deploymentURL) {");
			writetoTest.append("\n\t\tbrowser.open(deploymentURL + inputValues.get(0));\n");
			writetoTest.append("\n\t}");
			return "SUCCESS";
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
