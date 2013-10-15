package org.ocpsoft.keywords;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Visibility;
import org.jboss.forge.parser.java.util.Formatter;

import com.ocpsoft.utils.Constants;
import com.ocpsoft.utils.Constants.KEYWORD_KEYS;

public class BeginTestKeyword implements Keyword {

	public BeginTestKeyword(){
	}
	
	@Override
	public KEYWORD_KEYS shortName() {
		return KEYWORD_KEYS.BeginTest;
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
	@Deprecated
	public String additionalInputParams(){
		return "";
	}

	@Override
	@Deprecated
	public void createKeywordHelperMethod(JavaClass helperClass){
	}

	
	@Override
	public String performKeyword(JavaClass testClass, String nullTestName, ArrayList<String> inputValues) {
		
		testClass.addMethod().setName(inputValues.get(0))
				.setVisibility(Visibility.PUBLIC).setReturnTypeVoid()
				.addThrows(InterruptedException.class)
				
				//TODO: #DeploymentURL_HACK
				//TODO: This is a temporary measure.  We need to figure out a way of running the tests on our app server
				//without making the deployment name "keword-testing.war".  This is a temp stop-gap.
				//NOTE: This is going to show up as an error right now on the UI in the status section.
				.addThrows(MalformedURLException.class)
				.setBody("deploymentURL = new URL(\"" + Constants.FRAMEWORK_LOCALHOST_URL + "\");")
				
				.addAnnotation(Constants.TestAnnotationClass);
		
		try {
			PrintStream writetoTest = new PrintStream(new FileOutputStream(
					Constants.APP_UNDER_TEST__TEST_FILE_PATH + testClass.getName() + ".java"));
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
