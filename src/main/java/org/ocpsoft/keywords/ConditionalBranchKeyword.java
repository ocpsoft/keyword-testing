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
			System.out.println("Could not perform Conditional Branch.  Could not find testName: " + testCaseName);
			return "Could not perform Conditional Branch.  Could not find testName: " + testCaseName;
		}
		String curBody = testCase.getBody();
		String newLines = "\nif(" + inputValues.get(0) + ") {" +
						"Actions." + inputValues.get(1) + "(deploymentURL, browser);";
		if(inputValues.get(2) == null || inputValues.get(2).equals("") || inputValues.get(2).equals("assigned_null")){
			newLines += "}";
		} else {
			newLines += "} else {" +
						"Actions." + inputValues.get(2) + "(deploymentURL, browser);" +
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
			System.err.println("Failure in Conditional Branch: " + e);
			return "ERROR: Could not execute Conditional Branch.";
		}
		
		System.out.println("SUCCESS: Conditional Branch for [" + inputValues.get(0) + "] created successfully.");
		return "SUCCESS: Conditional Branch for [" + inputValues.get(0) + "] created successfully.";
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
