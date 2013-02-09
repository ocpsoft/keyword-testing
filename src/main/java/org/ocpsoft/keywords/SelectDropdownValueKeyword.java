package org.ocpsoft.keywords;

import java.util.ArrayList;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Visibility;

public class SelectDropdownValueKeyword implements Keyword {

	@Override
	public String getShortName() {
		return "SelectDropdownValue";
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
