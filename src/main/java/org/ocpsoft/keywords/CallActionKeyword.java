package org.ocpsoft.keywords;

import java.util.ArrayList;

import org.jboss.forge.parser.java.JavaClass;

import com.ocpsoft.utils.Constants.KEYWORD_KEYS;

public class CallActionKeyword implements Keyword {

	public CallActionKeyword(){
	}
	
	@Override
	public KEYWORD_KEYS shortName() {
		return KEYWORD_KEYS.CallAction;
	}

	@Override
	public KEYWORD_PROCESS_TYPES processType(){
		return KEYWORD_PROCESS_TYPES.UniqueProcess;
	}

	@Override
	public ArrayList<Class<? extends Exception>> addThrowsToTest(){
		return null;
	}
	
	@Override
	@Deprecated
	public String additionalInputParams(){
		return "";
	}

	@Override
	@Deprecated
	public void createKeywordHelperMethod(JavaClass helperClass){
	}

	
	@Override
	public String performKeyword(JavaClass testClass, String testCaseName, ArrayList<String> inputValues) {
		System.out.println("SUCCESS: New Call to Actions [" + inputValues.get(0) + "] added successfully.");
		return "SUCCESS: New Call to Actions [" + inputValues.get(0) + "] added successfully.";
	}

}
