package org.ocpsoft.keywords;

import java.io.PrintStream;
import java.util.ArrayList;

public class SelectDropdownValueKeyword implements Keyword {

	@Override
	public String getShortName() {
		return "SelectDropdownValue";
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
			writetoTest.append("\n\tpublic static void SelectDropdownValue(DefaultSelenium browser, List<String> inputValues) {");
			writetoTest.append("\n\t\tbrowser.select(\"id=\" + inputValues.get(0), \"label=\" + inputValues.get(1));");
			writetoTest.append("\n\t}");
			return "SUCCESS";
		}
		catch (Exception e) {
			System.err.println("Failure in doClick: " + e);
			return "FAILURE in Click Instruction: " + e;
		}
	}

	/* EXAMPLE:
	 *  browser.select("id=keyword", "label=Begin New Suite");
	 */
	
}
