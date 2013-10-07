package com.ocpsoft.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.util.Formatter;

import com.ocpsoft.utils.Constants.KEYWORD_KEYS;

public class Utility {
	private static final String useThisToVerifyTestCaseName = "() throws InterruptedException";
	
	public static boolean memberIsATestCase(Member<JavaClass, ?> member){
		if(member.toString().contains(useThisToVerifyTestCaseName)){
			return true;
		}
		return false;
	}
	
	public static Member<JavaClass, ?> getMemberFromTestCaseName(String testName, String className){
		File testClassFile = new File(Constants.APP_UNDER_TEST__TEST_FILE_PATH + className + ".java");
		JavaClass testClass = null;
		try {
			testClass = (JavaClass) JavaParser.parse(testClassFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		List<Member<JavaClass, ?>> allMembers = testClass.getMembers();
		for (Member<JavaClass, ?> member : allMembers) {
			if(member.toString().contains(testName) && memberIsATestCase(member)){
				return member;
			}
		}
		return null;
	}
	
	public static Method<JavaClass> getTestCaseMethodFromName(String testName, JavaClass testClass){
		if(testClass == null){
			return null;
		}
		List<Method<JavaClass>> allMethods = testClass.getMethods();
		for (Method<JavaClass> method : allMethods) {
			if(method.getName().equalsIgnoreCase(testName)){
				return method;
			}
		}
		System.out.println("ERROR: Could not determine test with name: " + testName + ", in class: " + testClass.toString());
		return null;
	}
	
	public static String getTestCaseName(Member<JavaClass, ?> member){
		if(memberIsATestCase(member)){
			return member.getName();
		}
		return null;
	}
	
	public static boolean doesMemberMatchTestCaseName(Member<JavaClass, ?> member, String testCaseName){
		if(memberIsATestCase(member)){
			return getTestCaseName(member).equalsIgnoreCase(testCaseName);
		}
		return false;
	}
	
	public static JavaClass getJavaClass(String className){
		JavaClass testClass = null;
		try {
			File testClassFile = new File(Constants.APP_UNDER_TEST__TEST_FILE_PATH + className + ".java");
			testClass = (JavaClass) JavaParser.parse(testClassFile);
		} catch (Exception e) {
			System.out.println("Error in trying to get the testClass File for Processing a keyword: " + e);
			return null;
		}
		return testClass;
	}
	
	public static String generateUserReadableStepsFromMethod(Member<JavaClass, ?> member){
		String returnVal = "";
		String testName = Utility.getTestCaseName(member);
		returnVal+= "<B>" + testName + "</B><BR />";
		String[] steps  = getStepsFromMethod(member);
		String steptranslation = "";
		int index = 0;
		for (String step : steps) {
			if(!isEmptyStep(step)){
				steptranslation = reverseTranslateStep(step);
				if(steptranslation.startsWith("ERROR")){
					returnVal+= steptranslation + "<BR />";
				} else if(steptranslation.startsWith("IGNORE")){
					//Do nothing at all
				} else {
					returnVal+= generateMoveButton(testName, index, "up") + " " + 
						    	generateMoveButton(testName, index, "down") + " " + 
						    	steptranslation + "<BR />";
				}
			}
			index++;
		}
		return returnVal;
	}
	
	public static String generateMoveButton(String testName, int index, String direction){
		if(direction.equalsIgnoreCase("up")){
			return "<a href='#top' id='upLink_" + index + "' onClick=\"moveTestStep('" + testName + "', " + index + ", '" + direction + "')\">|UP|</a>";
		}
		else if(direction.equalsIgnoreCase("down")){
			return "<a href='#top' id='downLink_" + index + "' onClick=\"moveTestStep('" + testName + "', " + index + ", '" + direction + "')\">|DOWN|</a>";
		}
		return "ERROR: Invalid direction";
	}
	
	public static String[] getStepsFromMethod(Member<JavaClass, ?> member){
		if(member == null){
			return null;
		}
		String[] steps = member.getOrigin().getMethod(getTestCaseName(member)).getBody().split(";");
		String[] returnArray;
		if(steps[steps.length - 1].replace("\n", "").replace(" ", "").equals("")){
			returnArray = new String[ steps.length - 1 ];  //Last line is sometimes just a newline
		} else {
			returnArray = new String[ steps.length];
		}
		boolean stepIsInABlock = false;
		boolean stepEndsABlock = false;
		boolean stepInMiddleOfBlock = false;
		int pos = 0;
		for (int i = 0; i < returnArray.length; i++) {
			String curStep = steps[i];
			
			if(stepIsInABlock){
				stepInMiddleOfBlock = true;
			}
			if(curStep.contains("{")){
				stepIsInABlock = true;
			}
			if(curStep.contains("}")){
				stepEndsABlock = true;
				stepInMiddleOfBlock = false;
			}
			
			if(stepEndsABlock){
				//Add up to the } to the last step, and make curStep the remainder to continue adding the rest of this step
				String remainderOfBlock = curStep.substring(0, curStep.indexOf("}") + 1);
				pos--;
				returnArray[pos] = returnArray[pos] + clearNewLinesOutOfString(remainderOfBlock);
				pos++;
				curStep = curStep.substring(curStep.indexOf("}") + 1);
				stepIsInABlock = curStep.contains("{");
				stepEndsABlock = curStep.contains("}");
			}
			
			String nonBlankCurStep = curStep.replace("\n", "").replace(" ", "");
			if(stepIsInABlock && !nonBlankCurStep.equals("}") && !nonBlankCurStep.equals("")){
				//Add the ; back to the end of this statement, since this step is going in the middle of a block
				//Note the last step in the block is probably going to just be "}".
				curStep+= ";";
			}
			if(stepInMiddleOfBlock || curStep.replace(" ", "").contains("else{")){
				//This is part of a block or continuation of the prior step {conditional branch} step
				//Go back to the last step, and re-assign it with this step as a continuation
				pos--;
				returnArray[pos] = returnArray[pos] + clearNewLinesOutOfString(curStep);
			} else {
				returnArray[pos] = clearNewLinesOutOfString(curStep);
			}
			pos++;
		}
		
		if(returnArray[returnArray.length -1] == null){
			//We have at least 1 combined step, so our count is now off, remove all null steps
			int count = 0;
			for (String string : returnArray) {
				if(string != null){	count++; }
			}
			String[] curReturnArray = new String[count];
			pos = 0;
			for (int i = 0; i < returnArray.length; i++) {
				if(returnArray[i] != null){
					curReturnArray[pos] = returnArray[i];
					pos++;
				}
			}
			returnArray = curReturnArray;
		}
		return returnArray;
	}
	
	public static String reverseTranslateStep(String step){
		String keywordName;
		//Note: Some steps "if" conditions
		//Example: if({condition}) { Actions.trueCase(); } else { Actions.falseCase(); }
		if(step.contains("if (")){
			String condition = step.substring(step.indexOf("if (") + 4, step.indexOf(")"));
			String trueCondition = step.substring(step.indexOf("Actions.") + 8, step.indexOf("(", step.indexOf("Actions.")));
			String falseCondition = "";
			if(step.contains("else")){
				falseCondition = step.substring(step.indexOf("Actions.", step.indexOf("else")) + 8, step.indexOf("(", step.indexOf("else")));
			}
			return "<font color='blue'>" + Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.ConditionalBranch) + "</font>: " +
					"true/false condition to test: " + "<em><font color='purple'>" + condition + "</em></font>" +
					" - true case: " + "<em><font color='purple'>" + trueCondition + "</em></font>" +
					" - false case: " + "<em><font color='purple'>" + falseCondition + "</em></font>";
		}
		
		//Note: Some steps are Action calls like:
		//Actions.callSomeMethod(deploymentURL, browser)
		if(step.contains("Actions.")){
			String actionName = step.substring(step.indexOf("Actions.") + 8, step.indexOf("("));
			return "<font color='blue'>" + "Action Call" + "</font>: " +
					"<em><font color='purple'>" + actionName + "</em></font>";
		}
		
		//Note: All the rest of the steps look the same, like: 
		//Helper.OpenBrowser(browser,Arrays.asList("index.jsp","assigned_null","assigned_null","assigned_null"),deploymentURL);
		keywordName = step.substring(step.indexOf("Helper.") + 7, step.indexOf("("));
		String methodParams = step.substring(step.indexOf("(") + 1);
		String inputArrayString = methodParams.substring(methodParams.indexOf("(") + 1, methodParams.indexOf(")"));
		String[] inputArray = inputArrayString.split(",");
		
		List<String> thisKeywordsDescriptions = null;
		for(KEYWORD_KEYS key : KEYWORD_KEYS.values()){
			if(key.toString().equals(keywordName)){
				thisKeywordsDescriptions = Constants.KEYWORD_DESCRIPTIONS.get(key);
				break;
			}
		}
		if(thisKeywordsDescriptions == null){
			//TODO: #DeploymentURL_HACK
			//If this is from chaning the deployment URL, just completely ignore that line and don't print
			//anything in the UI status for it at all.
			if(step.startsWith("deploymentURL=new URL(")){
				return "IGNORE";
			}
			return "ERROR: Could not get back Descriptions for Keyword: " + keywordName;
		}
		
		String returnVal = "<font color='blue'>" + keywordName + "</font>: ";
		boolean removeLastChar = false;
		String currInput = "";
		for (int i = 0; i < inputArray.length; i++) {
			currInput = inputArray[i];
			if(currInput!=null && currInput.startsWith("\"") &&  currInput.endsWith("\"")){
				currInput = currInput.substring(1, currInput.length() - 1);
			}
			if(!stringIsNullValue(currInput)){
				returnVal += thisKeywordsDescriptions.get(i) + " <em><font color='purple'>" + currInput + "</font></em>, ";
				removeLastChar = true;
			}
		}
		
		if(removeLastChar){
			return returnVal.substring(0, returnVal.length() - 2);
		}
		else{
			return returnVal;
		}
	}
	
	public static boolean stringIsNullValue(String string){
		if(string==null || string.equalsIgnoreCase("null") || string.equalsIgnoreCase("assigned_null") ||
				string.equalsIgnoreCase("\"assigned_null\"") || string.equals("")){
			return true;
		}
		return false;
	}
	
	public static boolean isEmptyStep(String step){
		if(step==null || step.equals(" ") || step.equals("") || step.equalsIgnoreCase("\n") || step.equalsIgnoreCase("\n ")){
			return true;
		}
		return false;
	}
	
	public static String clearNewLinesOutOfString(String string){
		String returnVal = string.replaceAll("\n ", "");
		returnVal = returnVal.replaceAll("\n", "");
		if(returnVal.equals("")){
			return null;
		}
		return returnVal;
	}
	
	public static void rewriteFile(JavaClass testClass, String completePath){
		try{
			PrintStream writetoTest = new PrintStream(
				     new FileOutputStream(completePath)); 
			writetoTest.print(testClass);
			writetoTest.close();
			System.out.println("Successfully re-wrote file: " + completePath);
		}
		catch (Exception e) {
			System.err.println("Failure in trying to re-write file: " + e);
		}
	}

	public static String generateTestSuiteOutput(JavaClass testClass, String className){
		List<Member<JavaClass, ?>> allMembers = testClass.getMembers();
		String returnVal = "<font color='green'>Test Suite Named: <B>" + className + "</B></font>";
		for (Member<JavaClass, ?> member : allMembers) {
			if(Utility.memberIsATestCase(member)){
				returnVal += "<P /><BR />" + Utility.generateUserReadableStepsFromMethod(member);
			}
			else {/*It's probably a variable or something, not an actual test method, we don't want that*/}
		}
		return returnVal;
	}
	
	public static boolean isExceptionAlreadyThrown(Method<JavaClass> method, Class<? extends Exception> exceptionClassToAdd){
		List<String> allExceptions = method.getThrownExceptions();
		for (String exception : allExceptions) {
			if(exception.equalsIgnoreCase(exceptionClassToAdd.getSimpleName())){
				return true;
			}
		}
		return false;
	}
	
	//TODO: This doesn't work right now, low priority, at least find a work around at some point.
	public static String formatJavaClassFile(JavaClass classFile){
		String formattedText = Formatter.format(classFile);

		//HACK: For right now, Parser will not accept List<String>, only List.  Do a find/replace as input params
		formattedText = formattedText.replace("List inputValues", "List<String> inputValues");
		
		//MORE HACK: For the Helper file, we need to add a non-String initializer to an int field which the parser doesn't like
		formattedText = formattedText.replace("private static final int MAX_PAGE_LOAD_TIME_in_seconds", "private static final int MAX_PAGE_LOAD_TIME_in_seconds = 10");
		return formattedText;
	}
	
	public static ArrayList<String> convertListStringToArrayListString(List<String> strings){
		ArrayList<String> returnVal = new ArrayList<String>();
		for (String string : strings) {
			returnVal.add(string);
		}
		return returnVal;
	}

}
