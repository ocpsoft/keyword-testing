package org.ocpsoft.keywords;

import java.util.ArrayList;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Visibility;

import com.ocpsoft.utils.Constants.KEYWORD_KEYS;

public class OpenBrowserKeyword implements Keyword {

	public OpenBrowserKeyword() {
	}
	
	@Override
	public KEYWORD_KEYS shortName() {
		return KEYWORD_KEYS.OpenBrowser;
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
	public String additionalInputParams(){
		return ",deploymentURL";
	}
	
	@Override
	@Deprecated
	public String performKeyword(JavaClass testClass, ArrayList<String> inputValues) {
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
