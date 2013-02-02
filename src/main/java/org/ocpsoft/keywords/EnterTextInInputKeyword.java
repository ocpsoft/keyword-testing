package org.ocpsoft.keywords;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Visibility;

import com.thoughtworks.selenium.DefaultSelenium;

public class EnterTextInInputKeyword implements Keyword {

	@Override
	public String getShortName() {
		return "EnterTextInInput";
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
	public String performKeyword(String testPath, ArrayList<String> inputValues) {
		return "";
	}
	
	@Override
	public void createKeywordHelperMethod(JavaClass helperClass){
		helperClass.addMethod()
          .setName("EnterTextInInput")
          .setStatic(true)
          .setVisibility(Visibility.PUBLIC)
          .setReturnTypeVoid()
          .setParameters("DefaultSelenium browser, List inputValues")
          .setBody("browser.type(inputValues.get(0), inputValues.get(1));"
        		  );
	}

	/* EXAMPLE:
	 *  browser.type("//input[@id='className']","Text to Enter");
	 */
	
}
