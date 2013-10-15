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

public class AssignVariableKeyword implements KeywordAssignment {

	private String curVariableName = "";

	public AssignVariableKeyword() {
	}
	
	@Override
	public KEYWORD_KEYS shortName() {
		return KEYWORD_KEYS.AssignVariable;
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
	public String additionalInputParams(){
		return "";
	}
	
	@Override
	public String performKeyword(JavaClass testClass, String testCaseName, ArrayList<String> inputValues) {

		Method<JavaClass> testCase = Utility.getTestCaseMethodFromName(testCaseName, testClass);
		if(testCase == null){
			System.out.println("Could not perform " + shortName() + ".  Could not find testName: " + testCaseName);
			return "Could not perform " + shortName() + ".  Could not find testName: " + testCaseName;
		}
		String curBody = testCase.getBody();
		setCurVariableName(inputValues.get(0));
		
		//NOTE: Framework does NOT support creating multiple variables of different types with the same name.
		//Therefore, if we find a variable with the specified name, we will assume your new value is acceptable for the type.
		if(Utility.isVariableAlreadyPresentInMethod(curBody, getCurVariableName()) == false){
			System.err.println("ERROR: Varibale does NOT exist, can not re-assign it.");
			return "ERROR: Varibale does NOT exist, can not re-assign it.";
		}
		
		String newLine = "\n" + determineNewLine(inputValues);
		
		testCase
				.setBody(curBody + newLine);
		try {
			PrintStream writetoTest = new PrintStream(new FileOutputStream(
					Constants.APP_UNDER_TEST__TEST_FILE_PATH + testClass.getName() + ".java"));
			writetoTest.print(Formatter.format(testClass)); //TODO: This doesn't work, low priority to fix
			writetoTest.close();
		} catch (Exception e) {
			System.err.println("Failure in " + shortName() + " : " + e);
			return "ERROR: Could not execute " + shortName();
		}
		
		System.out.println("SUCCESS: " + shortName() + " for [" + inputValues.get(0) + "] updated successfully.");
		return "SUCCESS: " + shortName() + " for [" + inputValues.get(0) + "] updated successfully.";
	}
	
	//0:Name, 1:NewValue
	private String determineNewLine(ArrayList<String> inputValues) {
		String newLine = inputValues.get(0) + " = ";
		if(inputValues.get(1).equals("String")){
			if(!inputValues.get(2).startsWith("\"")){
				newLine += "\"" + inputValues.get(2);
			}
			if(!inputValues.get(2).endsWith("\"")){
				newLine +=  "\"";
			}
			newLine += ";";
		} else {
			newLine += inputValues.get(2) + ";";
		}
		return newLine;
	}

	@Override
	@Deprecated
	public void createKeywordHelperMethod(JavaClass helperClass){
	}

	@Override
	public String variableName() {
		return getCurVariableName();
	}
	
	/* EXAMPLE:
	 * Note: This will re-assign a new value to an existing variable.
	 */

	
	public String getCurVariableName() {
		return curVariableName;
	}

	public void setCurVariableName(String newVariableName) {
		this.curVariableName = newVariableName;
	}
}
