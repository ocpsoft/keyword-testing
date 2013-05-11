package org.ocpsoft.keywords;

import java.util.ArrayList;

import org.jboss.forge.parser.java.JavaClass;

import com.ocpsoft.utils.Constants.KEYWORD_KEYS;

public interface Keyword {
	
	public static enum KEYWORD_PROCESS_TYPES
	{
		MethodCall, DirectProcess
	}
	
	KEYWORD_KEYS getShortName();
	KEYWORD_PROCESS_TYPES getProcessType();
	String getAdditionalInputParams();
	ArrayList<Class<? extends Exception>> addThrowsToTest();
	
	//TODO: This is kind of a hack right now.  We're putting 2 methods into each keyword, but each will implement
	//One and only One of them.  Should really break this out into 2 different objects.
	//For now seperate them to know which is which via KEYWORD_PROCESS_TYPES to know which method below to use:
	void createKeywordHelperMethod(JavaClass helperClass);
	String performKeyword(JavaClass testClass, ArrayList<String> inputValues);
	
	/*NOTE: For creating new keywords, you must:
	1) Create the new Keyword class implementing Keyword.
	2) Choose if it is a DirectProcess or MethodCall keyword.
		- DirectProcess means the class itself will perform the keyword when it's called to be added.
		- MethodCall means a method will get added to Helper.java and a call to that method will be added to the test when it's called to be added.
	3) Add a new enum value to KEYWORD_KEYS
		- return that new value in the getShortName() method.
	4) Add an implementation, either in performKeyword() or createKeywordHelperMethod() depending on DirectProcess or MethodCall respectively.
	5) Add a KEYWORD_LONGNAME value for it.
		- The value that shows to the user in the dropdown.
	6) Add an array of Descriptions and Values for it.
		- Descriptions are the text telling the user what to enter as added params for this keyword
			ex: "with Test Name of:"
		- Values are the default values the input fields are initiallized to to help the user.
			ex: "testName"
	 */
}
