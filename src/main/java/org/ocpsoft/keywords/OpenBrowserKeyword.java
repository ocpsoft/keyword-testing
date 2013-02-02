package org.ocpsoft.keywords;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Visibility;

import com.thoughtworks.selenium.DefaultSelenium;

public class OpenBrowserKeyword implements Keyword {

	@Override
	public String getShortName() {
		return "OpenBrowser";
	}

	@Override
	public KEYWORD_PROCESS_TYPES getProcessType(){
		return KEYWORD_PROCESS_TYPES.MethodCall;
	}

	@Override
	public String getAdditionalInputParams(){
		return ", deploymentURL";
	}
	
	@Override
	@Deprecated
	public String performKeyword(String testPath, ArrayList<String> inputValues) {
		return "";
	}
	
	@Override
	public void createKeywordHelperMethod(JavaClass helperClass){
		helperClass.addMethod()
          .setName("OpenBrowser")
          .setStatic(true)
          .setVisibility(Visibility.PUBLIC)
          .setReturnTypeVoid()
          .setParameters("DefaultSelenium browser, List inputValues, URL deploymentURL")
          .setBody("browser.open(deploymentURL + inputValues.get(0));"
        		  );
	}
	
	/* EXAMPLE:
	 * browser.open(deploymentURL + "index.html");
	 */

}
