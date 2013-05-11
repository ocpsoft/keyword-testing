package org.ocpsoft.keywords;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Visibility;
import org.jboss.forge.parser.java.util.Formatter;
import org.junit.Test;

import com.ocpsoft.utils.Constants;
import com.ocpsoft.utils.Constants.KEYWORD_KEYS;

public class BeginTestKeyword implements Keyword {

	@Override
	public KEYWORD_KEYS getShortName() {
		return KEYWORD_KEYS.BeginTest;
	}

	@Override
	public KEYWORD_PROCESS_TYPES getProcessType(){
		return KEYWORD_PROCESS_TYPES.DirectProcess;
	}

	@Override
	public ArrayList<Class<? extends Exception>> addThrowsToTest(){
		return null;
	}
	
	@Override
	@Deprecated
	public String getAdditionalInputParams(){
		return "";
	}

	@Override
	@Deprecated
	public void createKeywordHelperMethod(JavaClass helperClass){
	}

	
	@Override
	public String performKeyword(JavaClass testClass, ArrayList<String> inputValues) {
		
		testClass.addMethod().setName(inputValues.get(0))
				.setVisibility(Visibility.PUBLIC).setReturnTypeVoid()
				.addThrows(InterruptedException.class)
				.setBody("")
				.addAnnotation(Constants.TestAnnotationClass);
		
		try {
			PrintStream writetoTest = new PrintStream(new FileOutputStream(
					Constants.ROOT_FILE_PATH + testClass.getName() + ".java"));
			writetoTest.print(Formatter.format(testClass)); //TODO: This doesn't work, low priority to fix
			writetoTest.close();
		} catch (Exception e) {
			System.err.println("Failure in create new Test Case: " + e);
			return "ERROR: Could not create new Test.";
		}
		
		System.out.println("SUCCESS: New Test [" + inputValues.get(0) + "] created successfully.");
		return "SUCCESS: New Test [" + inputValues.get(0) + "] created successfully.";
	}

}
