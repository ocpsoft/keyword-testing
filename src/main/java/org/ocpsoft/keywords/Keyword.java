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
	
	//TODO: This is kind of a hack right now.  We're putting 2 methods into each keyword, but each will implement
	//One and only One of them.  Should really break this out into 2 different objects.
	//For now seperate them to know which is which via KEYWORD_PROCESS_TYPES to know which method below to use:
	void createKeywordHelperMethod(JavaClass helperClass);
	String performKeyword(JavaClass testClass, ArrayList<String> inputValues);
	
	/*NOTE: For creating new keywords, you must:
	 * 1) Create the new Keyword Class to implement this interface
	 * 2) Update the InputConstants class to add the Key, LongName, InputDescriptions, and InputValues
	 * 3) Update src/main/resources/META-INF/services/org.ocpsoft.keywords.Keyword to include the newly created file
	 */
}
