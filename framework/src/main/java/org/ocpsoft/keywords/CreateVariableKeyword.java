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

public class CreateVariableKeyword implements VariableKeywordInterface {

	public CreateVariableKeyword() {
	}
	
	@Override
	public KEYWORD_KEYS shortName() {
		return KEYWORD_KEYS.CreateVariable;
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
		
		//NOTE: Framework does NOT support creating multiple variables of different types with the same name.
		//Once you use a name for any variable, you can NOT create any other variable with that name within the same test.
		if(Utility.isVariableAlreadyPresentInMethod(curBody, inputValues.get(0))){
			System.err.println("ERROR: Varibale already exists, can not create it again.");
			return "ERROR: Varibale already exists, can not create it again.";
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
		
		System.out.println("SUCCESS: " + shortName() + " for [" + inputValues.get(0) + "] created successfully.");
		return "SUCCESS: " + shortName() + " for [" + inputValues.get(0) + "] created successfully.";
	}
	
	//0:Name, 1:Type, 2:DefaultValue
	@Override
	public String determineNewLine(ArrayList<String> inputValues) {
		String newLine = inputValues.get(1) + " " + inputValues.get(0) + " = " + determineAssignment(inputValues.get(1), inputValues.get(2)) + ";";
		return newLine;
	}

	private String determineAssignment(String varType, String defaultValue) {
		if(defaultValue.length() == 0){
			if(isPrimitiveType(varType)){
				return null;
			}
			return "new " + varType + "()";
		}
		if(varType.equals("String")){
			return "\"" + defaultValue + "\"";
		}
		return defaultValue;	
	}
	
	private Boolean isPrimitiveType(String varType){
		if(varType.equals("String") || varType.equals("int") || varType.equals("double")) {
			return true;
		}
		return false;
	}

	@Override
	@Deprecated
	public void createKeywordHelperMethod(JavaClass helperClass){
	}

	/* EXAMPLE:
	 * Note: This will add a single line to the end of the method creating a new variable and initializing it.
	 * 		If no default value is given, we will default the value to null if variable type is a primitive.
	 * 		If the variable type is NOT a primitive, we assume a default constructor exists, and we use it
	 */
	
}
