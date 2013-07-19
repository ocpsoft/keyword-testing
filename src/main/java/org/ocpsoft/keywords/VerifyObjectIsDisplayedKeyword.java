package org.ocpsoft.keywords;

import java.util.ArrayList;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Visibility;

import com.ocpsoft.utils.Constants.KEYWORD_KEYS;

public class VerifyObjectIsDisplayedKeyword implements Keyword {

	public VerifyObjectIsDisplayedKeyword() {
	}
	
	@Override
	public KEYWORD_KEYS shortName() {
		return KEYWORD_KEYS.VerifyObjectIsDisplayed;
	}

	@Override
	public KEYWORD_PROCESS_TYPES processType(){
		return KEYWORD_PROCESS_TYPES.MethodCall;
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
	public String performKeyword(JavaClass testClass, String testCaseName, ArrayList<String> inputValues) {
		return "";
	}
	
	@Override
	public void createKeywordHelperMethod(JavaClass helperClass){
		helperClass.addMethod()
          .setName("VerifyObjectIsDisplayed")
          .setStatic(true)
          .setVisibility(Visibility.PUBLIC)
          .setReturnTypeVoid()
          .setParameters("DefaultSelenium browser, List inputValues")
          .setBody( "Assert.assertTrue(inputValues.get(0)," + 
        		  		"browser.isElementPresent(\"xpath=//\" + inputValues.get(1)));"
        		  );
	}

	/* EXAMPLE:
	 *  Assert.assertTrue("User should be on MyInfo Page!",
     *      browser.isElementPresent("xpath=//div[@id='myFBdata']"));
	 */
	
}
