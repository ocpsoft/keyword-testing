package org.ocpsoft.keywords;

import java.util.ArrayList;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Visibility;

public class VerifyObjectIsNotDisplayedKeyword implements Keyword {

	@Override
	public String getShortName() {
		return "VerifyObjectIsNotDisplayed";
	}

	@Override
	public KEYWORD_PROCESS_TYPES getProcessType(){
		return KEYWORD_PROCESS_TYPES.MethodCall;
	}

	@Override
	@Deprecated
	public String getAdditionalInputParams(){
		return "";
	}
	
	@Override
	@Deprecated
	public String performKeyword(JavaClass testClass, ArrayList<String> inputValues) {
		return "";
	}
	
	@Override
	public void createKeywordHelperMethod(JavaClass helperClass){
		helperClass.addMethod()
          .setName("VerifyObjectIsNotDisplayed")
          .setStatic(true)
          .setVisibility(Visibility.PUBLIC)
          .setReturnTypeVoid()
          .setParameters("DefaultSelenium browser, List inputValues")
          .setBody( "Assert.assertFalse(inputValues.get(0)," + 
        		  		"browser.isElementPresent(\"xpath=//\" + inputValues.get(1)));"
        		  );
	}

	/* EXAMPLE:
	 *  Assert.assertFalse("User should not see [Error: Invalid action]",
     *      browser.isElementPresent("xpath=//div[@id='myFBdata']"));
	 */
	
}
