package org.ocpsoft.keywords;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.util.Formatter;

import com.ocpsoft.utils.Constants;
import com.ocpsoft.utils.Constants.KEYWORD_KEYS;
import com.ocpsoft.utils.Utility;

public class ConditionalBranchKeyword implements Keyword {

	public ConditionalBranchKeyword(){
	}
	
	@Override
	public KEYWORD_KEYS shortName() {
		return KEYWORD_KEYS.ConditionalBranch;
	}

	@Override
	public KEYWORD_PROCESS_TYPES processType(){
		return KEYWORD_PROCESS_TYPES.DirectProcess;
	}

	@Override
	public ArrayList<Class<? extends Exception>> addThrowsToTest(){
		return null;
	}
	
	@Override
	public String performKeyword(JavaClass testClass, String testCaseName, ArrayList<String> inputValues) {

		Method<JavaClass> testCase = Utility.getTestCaseMethodFromName(testCaseName, testClass);
		if(testCase == null){
			System.out.println("Could not perform " + shortName() + ".  Could not find testName: " + testCaseName);
			return "Could not perform " + shortName() + ".  Could not find testName: " + testCaseName;
		}
		String curBody = testCase.getBody();
		String condition = Utility.resolveVariables(inputValues.get(0));
		String trueAction = getActionNameFromInput(inputValues.get(1));
		String trueActionExtraInputs = Utility.resolveExtraInputs(getExtraInputsFromInput(inputValues.get(1)));
		String falseAction = getActionNameFromInput(inputValues.get(2));
		String falseActionExtraInputs = Utility.resolveExtraInputs(getExtraInputsFromInput(inputValues.get(2)));
		String newLines = "\nif(" + condition + ") {" +
						"Actions." + trueAction + "(deploymentURL, browser" + trueActionExtraInputs + ");";
		if(inputValues.get(2) == null || inputValues.get(2).equals("") || inputValues.get(2).equals("assigned_null")){
			newLines += "}";
		} else {
			newLines += "} else {" +
						"Actions." + falseAction + "(deploymentURL, browser" + falseActionExtraInputs + ");" +
						"}";
		}
		
		testCase
				.setBody(curBody + newLines);
		try {
			PrintStream writetoTest = new PrintStream(new FileOutputStream(
					Constants.APP_UNDER_TEST__TEST_FILE_PATH + testClass.getName() + ".java"));
			writetoTest.print(Formatter.format(testClass)); //TODO: This doesn't work, low priority to fix
			writetoTest.close();
		} catch (Exception e) {
			System.err.println("Failure in " + shortName() + ": " + e);
			return "ERROR: Could not execute " + shortName();
		}
		
		System.out.println("SUCCESS: " + shortName() + " for [" + inputValues.get(0) + "] created successfully.");
		return "SUCCESS: " + shortName() + " for [" + inputValues.get(0) + "] created successfully.";
	}

	private String getActionNameFromInput(String input){
		int cutoff = input.indexOf(Constants.OBJECT_DELIMITER);
		if(cutoff == -1){
			return input;
		}
		return input.substring(0, cutoff);
	}
	private String getExtraInputsFromInput(String input){
		int start = input.indexOf(Constants.OBJECT_DELIMITER);
		if(start == -1){
			return "";
		}
		start = input.indexOf(Constants.OBJECT_DELIMITER) + Constants.OBJECT_DELIMITER.length();
		return input.substring(start, input.length());
	}
	@Override
	@Deprecated
	public String additionalInputParams(){
		return "";
	}
	
	@Deprecated
	public void createKeywordHelperMethod(JavaClass helperClass){
	}

}
