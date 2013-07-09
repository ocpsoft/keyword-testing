package org.ocpsoft.keywords;

import java.util.ArrayList;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Visibility;

import com.ocpsoft.utils.Constants.KEYWORD_KEYS;

public class SelectDropdownValueKeyword implements Keyword {

	public SelectDropdownValueKeyword() {
	}
	
	@Override
	public KEYWORD_KEYS shortName() {
		return KEYWORD_KEYS.SelectDropdownValue;
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
	public String performKeyword(JavaClass testClass, ArrayList<String> inputValues) {
		return "";
	}
	
	@Override
	public void createKeywordHelperMethod(JavaClass helperClass){
		helperClass.addMethod()
          .setName("SelectDropdownValue")
          .setStatic(true)
          .setVisibility(Visibility.PUBLIC)
          .setReturnTypeVoid()
          .setParameters("DefaultSelenium browser, List inputValues")
          .setBody("browser.select(\"id=\" + inputValues.get(0), \"label=\" + inputValues.get(1));"
        		  );
	}

	/* EXAMPLE:
	 *  browser.select("id=keyword", "label=Begin New Suite");
	 */
	
}
