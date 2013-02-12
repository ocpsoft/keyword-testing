package com.ocpsoft.utils;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Member;
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
	
	public static String generateUserReadableStepsFromMethod(Member<JavaClass, ?> member){
		String returnVal = "";
		String testName = Utility.getTestCaseName(member);
		returnVal+= "<B>" + testName + "</B><BR />";
		String[] steps  = getStepsFromMethod(member);
		int index = 0;
		for (String step : steps) {
			if(!isEmptyStep(step)){
				returnVal+= generateMoveButton(testName, index, "up") + " " + 
						    generateMoveButton(testName, index, "down") + " " + 
						    reverseTranslateStep(step) + "<BR />";
			}
			index++;
		}
		return returnVal;
	}
	
	public static String generateMoveButton(String testName, int index, String direction){
		if(direction.equalsIgnoreCase("up")){
			return "<a href='#top' onClick=\"moveTestStep('" + testName + "', " + index + ", '" + direction + "')\">|UP|</a>";
		}
		else if(direction.equalsIgnoreCase("down")){
			return "<a href='#top' onClick=\"moveTestStep('" + testName + "', " + index + ", '" + direction + "')\">|DOWN|</a>";
		}
		return "ERROR: Invalid direction";
	}
	
	public static String[] getStepsFromMethod(Member<JavaClass, ?> member){
		String[] steps = member.getOrigin().getMethod(getTestCaseName(member)).getBody().split(";");
		String[] returnArray = new String[ steps.length - 1 ];  //Last line is always just a newline
		for (int i = 0; i < returnArray.length; i++) {
			returnArray[i] = clearNewLinesOutOfString(steps[i]);
		}
		return returnArray;
	}
	
	public static String reverseTranslateStep(String step){
		//Note: All the steps look the same, like: 
		//Helper.OpenBrowser(browser,Arrays.asList("index.jsp","assigned_null","assigned_null","assigned_null"),deploymentURL);
		String keywordName = step.substring(7, step.indexOf("("));
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
			return "ERROR: Could not get back Descriptions for Keyword: " + keywordName;
		}
		
		String returnVal = "<font color='blue'>" + keywordName + "</font>: ";
		boolean removeLastChar = false;
		for (int i = 0; i < inputArray.length; i++) {
			if(!stringIsNullValue(inputArray[i])){
				returnVal += thisKeywordsDescriptions.get(i) + " <em>" + inputArray[i] + "</em>, ";
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
				string.equalsIgnoreCase("\"assigned_null\"")){
			return true;
		}
		return false;
	}
	
	public static boolean isEmptyStep(String step){
		if(step==null || step.equalsIgnoreCase("") || step.equalsIgnoreCase("\n") || step.equalsIgnoreCase("\n ")){
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
	
	//TODO: This doesn't work right now, low priority, at least find a work around at some point.
	public static String formatJavaClassFile(JavaClass classFile){
		return Formatter.format(classFile);
	}
	
	
}
