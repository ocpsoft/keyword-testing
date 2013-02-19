package org.ocpsoft.keywords;

import java.util.ArrayList;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Visibility;

public class ClickElementKeyword implements Keyword {

	@Override
	public String getShortName() {
		return "ClickElement";
	}

	@Override
	public KEYWORD_PROCESS_TYPES getProcessType(){
		return KEYWORD_PROCESS_TYPES.MethodCall;
	}
	
	@Override
	@Deprecated
	public String performKeyword(JavaClass testClass, ArrayList<String> inputValues) {
		return "";
	}

	@Override
	@Deprecated
	public String getAdditionalInputParams(){
		return "";
	}
	
	public void createKeywordHelperMethod(JavaClass helperClass){
		helperClass.addMethod()
          .setName("ClickElement")
          .setStatic(true)
          .setVisibility(Visibility.PUBLIC)
          .setReturnTypeVoid()
          .setParameters("DefaultSelenium browser, List inputValues")
          .setBody(	"browser.click(inputValues.get(0) + \"=\" + inputValues.get(1));" +
					"if(inputValues.get(2) != null){" +
					"	if(inputValues.get(2).equalsIgnoreCase(\"assigned_null\") == false){" +
					"		/*wait for the  page to load*/" +
					"		browser.waitForPageToLoad(Integer.toString(MAX_PAGE_LOAD_TIME_in_seconds * 1000));" +
					"	}" +
					"}"
        		  );

	}

	/* EXAMPLE:
	 *  browser.click("id=myButton");
     *  or
     *  browser.click("name=myButton");
     *  or
     *  browser.click("link=LinkTextHere");
     *  browser.waitForFrameToLoad("myInfo.html", "15000");
     *  or
     *  browser.click("css=input[name=myButton]");
     *  or
     *  browser.click("xpath=//input[@name=myButton' and @type='submit']");
	 */
	
	/* 	browser.click(inputValues.get(0) + "=" + inputValues.get(1));
		if(inputValues.get(2) != null){
			if(inputValues.get(2).equalsIgnoreCase("assigned_null") == false){
				//wait for the  page to load
				browser.waitForPageToLoad(Integer.toString(MAX_PAGE_LOAD_TIME_in_seconds * 1000));
				//browser.waitForFrameToLoad(inputValues.get(2), "3000");
			}
		} */
	
}
