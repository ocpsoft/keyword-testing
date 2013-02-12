package com.ocpsoft.utils;

import java.util.List;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Member;

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
	
	public static String generateUserReadableStepsFromMethod(Member<JavaClass, ?> member){
		String returnVal = "";
		returnVal+= "<B>" + Utility.getTestCaseName(member) + "</B><BR />";
		String[] steps  = member.getOrigin().getMethod(getTestCaseName(member)).getBody().split(";");
		for (String step : steps) {
			step = clearNewLinesOutOfString(step);
			if(!isEmptyStep(step)){
				returnVal+= "- - " + reverseTranslateStep(step) + "<BR />";
			}
		}
		return returnVal;
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
		return returnVal;
	}
	
}
