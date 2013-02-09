package org.ocpsoft.keywords;

import java.util.ArrayList;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Visibility;

public class VerifyObjectPropertyKeyword implements Keyword {

	@Override
	public String getShortName() {
		return "VerifyObjectProperty";
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
          .setName("VerifyObjectProperty")
          .setStatic(true)
          .setVisibility(Visibility.PUBLIC)
          .setReturnTypeVoid()
          .setParameters("DefaultSelenium browser, List inputValues")
          .setBody( "value = getValue(browser, inputValues.get(1), inputValues.get(2));" +
        		  	"Assert.assertTrue(inputValues.get(0)," + 
        		  		"inputValues.get(3).equals(value));"
        		  );
	}

	/* EXAMPLE:
	 *  value = getValue(browser, [select|input|other], "//select[@id='keyword']");
	 *  Assert.assertTrue("Selected Value should be Begin New Suite",
     *      "Begin New Suite".equals(value));
	 */
	

}
