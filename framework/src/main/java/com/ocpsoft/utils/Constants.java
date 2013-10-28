package com.ocpsoft.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Constants {
	
	/***********************  UNIX FILE PATH *********************************************/
	//public static final String APP_UNDER_TEST_ROOT_FILE_PATH = "/home/fife/workspace/AppUnderTest/";
	//public static final String APP_UNDER_TEST__TEST_FILE_PATH = APP_UNDER_TEST_ROOT_FILE_PATH + "src/test/java/com/example/domain/";
	//public static final String KEYWORD_PROJECT_ROOT_FILE_PATH = "/home/fife/workspace/KeywordApp/";
	//public static final String EXPORT_FILE_PATH = "/home/fife/workspace/Keyword_Export_Files";
	
	/***********************  WINDOWWS FILE PATH *****************************************/
//	public static final String APP_UNDER_TEST_ROOT_FILE_PATH = "D:/DEVELOPMENT/projects/keword-testing/example-project/";
//	public static final String APP_UNDER_TEST__TEST_FILE_PATH = APP_UNDER_TEST_ROOT_FILE_PATH + "src/test/java/com/example/domain/";
//	public static final String KEYWORD_PROJECT_ROOT_FILE_PATH = "D:/DEVELOPMENT/projects/keword-testing/framework/";
//	public static final String EXPORT_FILE_PATH = "D:/DEVELOPMENT/projects/Keyword_Export_Files/";

	/***********************  LAPTOP FILE PATH *****************************************/
	public static final String APP_UNDER_TEST_ROOT_FILE_PATH = "C:/Development/projects/keyword-testing/example-project/";
	public static final String APP_UNDER_TEST__TEST_FILE_PATH = APP_UNDER_TEST_ROOT_FILE_PATH + "src/test/java/com/example/domain/";
	public static final String KEYWORD_PROJECT_ROOT_FILE_PATH = "C:/Development/projects/keyword-testing/framework/";
	public static final String EXPORT_FILE_PATH = "C:/Development/projects/Keyword_Export_Files/";
	
	
	
	public static final String FRAMEWORK_LOCALHOST_URL = "http://localhost:8080/framework/";
	public static final String VARIABLE_INPUT_MARKER = "||var||";
	public static final String CODE_INPUT_PREFIX = "||code||";
	public static final String LIST_DELIMITER = "##";
	public static final String OBJECT_DELIMITER = "~~";
	public static final String DOUBLE_QUOTE_REPLACEMENT = "_quote_";
	
	//Fully Qualified Paths for adding annotations to test files via the parser
	public static final String RunWithAnnotationClass = "org.junit.runner.RunWith";
	public static final String TestAnnotationClass = "org.junit.Test";
	public static final String AssertAnnotationClass = "org.junit.Assert";
	public static final String DeploymentAnnotationClass = "org.jboss.arquillian.container.test.api.Deployment";
	public static final String DefaultSeleniumAnnotationClass = "com.thoughtworks.selenium.DefaultSelenium";
	public static final String DroneAnnotationClass = "org.jboss.arquillian.drone.api.annotation.Drone";
	public static final String ArquillianAnnotationClass = "org.jboss.arquillian.junit.Arquillian";
	public static final String ArquillianResourceAnnotationClass = "org.jboss.arquillian.test.api.ArquillianResource";
	

	public static enum KEYWORD_KEYS
	{
		BeginClass, BeginTest, ClickElement, ConditionalBranch, EnterTextInInput, OpenBrowser, SelectDropdownValue, 
		VerifyObjectIsDisplayed, VerifyObjectIsNotDisplayed, VerifyObjectProperty, CallAction,
		CreateVariable, AssignVariable
	}
	public static final Map<KEYWORD_KEYS, String> KEYWORD_LONGNAMES = new LinkedHashMap<KEYWORD_KEYS, String>();
    static {
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.BeginClass, "Begin New Suite");
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.BeginTest, "Begin New Test");
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.OpenBrowser, "Open Browser");
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.ClickElement, "Click Web Element");
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.ConditionalBranch, "Conditional Branch");
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.EnterTextInInput, "Enter Text in Box");
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.VerifyObjectProperty, "Verify Object Property");
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.VerifyObjectIsDisplayed, "Verify Object Is Displayed");
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.VerifyObjectIsNotDisplayed, "Verify Object Is NOT Displayed");
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.SelectDropdownValue, "Select Dropdown Value");
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.CallAction, "Call Action");
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.CreateVariable, "Create new Variable");
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.AssignVariable, "Assign Variable Value");
    }
	public static Map<KEYWORD_KEYS, String> getKeywordLongnames() {
		return KEYWORD_LONGNAMES;
	}

	//NOTE: You can not use commas in a description, it will get broken out into a new input assignment on the HTML side!
	public static final Map<KEYWORD_KEYS, List<String>> KEYWORD_DESCRIPTIONS = new HashMap<KEYWORD_KEYS, List<String>>();
    static {
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.BeginClass, Arrays.asList("with Suite Name of:"));
    	
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.BeginTest, Arrays.asList("with Test Name of:"));
    	
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.ClickElement, Arrays.asList("with Identifyer [link/id/name/css/xpath] of:",
											    			   	"and desired element key of:", 
															   	"and desired Destination Path of (OPTIONAL FIELD - will wait for page to load):"));
    	
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.ConditionalBranch, Arrays.asList("with true/false condition of:",
											    			   	"Action to call in true case:", 
															   	"Action to call in false case (OPTIONAL FIELD):"));
    	
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.EnterTextInInput, Arrays.asList("with xPath of Input:",
				   												"and Text to enter:"));
    	
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.OpenBrowser, Arrays.asList("with Webpage of test Domain plus (OPTIONAL FIELD - if adding onto end of the domain):"));
    	
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.SelectDropdownValue, Arrays.asList("with Dropdown object's ID of:",
				 												"selecting the value of:"));
    	
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.VerifyObjectIsDisplayed, Arrays.asList("with Verification Message of:",
				  												"with XPath property to verify of:"));
    	
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.VerifyObjectIsNotDisplayed, Arrays.asList("with Verification Message of:",
				  												"with XPath property to verify of:"));
    	
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.VerifyObjectProperty, Arrays.asList("with Verification Message of:",
																"with object Type of:",
																"with XPath property to verify of:",
																"with value to verify of:"));
    	
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.CallAction, Arrays.asList("with Action Name:"));
    	
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.CreateVariable, Arrays.asList("variable name:", 
    															"varible type:", 
    															"with default value of:"));

    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.AssignVariable, Arrays.asList("variable name:",  
    															"to new value of:"));
    }
    
	public static Map<KEYWORD_KEYS, List<String>> getKeywordDescriptions() {
		return KEYWORD_DESCRIPTIONS;
	}
	
	
	public static final Map<KEYWORD_KEYS, List<String>> KEYWORD_VALUES = new HashMap<KEYWORD_KEYS, List<String>>();
    static {
    	KEYWORD_VALUES.put(KEYWORD_KEYS.BeginClass, Arrays.asList("MySuiteTest"));
    	
    	KEYWORD_VALUES.put(KEYWORD_KEYS.BeginTest, Arrays.asList("testName"));
    	
    	KEYWORD_VALUES.put(KEYWORD_KEYS.ClickElement, Arrays.asList("link","Get Your Info Here","myInfo.html"));
    	
    	KEYWORD_VALUES.put(KEYWORD_KEYS.ConditionalBranch, Arrays.asList("true/*condition here*/","trueCaseAction", "falseCaseAction"));

    	KEYWORD_VALUES.put(KEYWORD_KEYS.EnterTextInInput, Arrays.asList("//input[@id='className']","Assigning Input Text"));
    	
    	KEYWORD_VALUES.put(KEYWORD_KEYS.OpenBrowser, Arrays.asList("index.jsp"));
    	
    	KEYWORD_VALUES.put(KEYWORD_KEYS.SelectDropdownValue, Arrays.asList("keyword","Begin New Suite"));
    	
    	KEYWORD_VALUES.put(KEYWORD_KEYS.VerifyObjectIsDisplayed, Arrays.asList("User should be on index.jsp Page!","div[@id='RunTestsResults']"));
    	
    	KEYWORD_VALUES.put(KEYWORD_KEYS.VerifyObjectIsNotDisplayed, Arrays.asList("User should NOT see message [Error: invalid action]!","div[@id='myFBdata']"));
    	
    	KEYWORD_VALUES.put(KEYWORD_KEYS.VerifyObjectProperty, Arrays.asList("Selected Value should be Begin New Suite","select","//select[@id='keyword']","Begin New Suite"));
    	
    	KEYWORD_VALUES.put(KEYWORD_KEYS.CallAction, Arrays.asList("myAction"));
    	
    	KEYWORD_VALUES.put(KEYWORD_KEYS.CreateVariable, Arrays.asList("newVarName", "String", "initialization value"));
    	
    	KEYWORD_VALUES.put(KEYWORD_KEYS.AssignVariable, Arrays.asList("existingVarName", DOUBLE_QUOTE_REPLACEMENT + "My New Value" + DOUBLE_QUOTE_REPLACEMENT));
    }
    
	public static Map<KEYWORD_KEYS, List<String>> getKeywordValues() {
		return KEYWORD_VALUES;
	}
	
	public static String resolveValue(String value){
		return value.replace(DOUBLE_QUOTE_REPLACEMENT, "\"");
	}
}
