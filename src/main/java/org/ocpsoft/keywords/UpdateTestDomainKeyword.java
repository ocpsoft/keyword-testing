package org.ocpsoft.keywords;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Visibility;

import com.ocpsoft.utils.Constants.KEYWORD_KEYS;

public class UpdateTestDomainKeyword implements KeywordAssignment {

	public UpdateTestDomainKeyword() {
	}
	
	@Override
	public KEYWORD_KEYS shortName() {
		return KEYWORD_KEYS.UpdateTestDomain;
	}

	@Override
	public KEYWORD_PROCESS_TYPES processType(){
		return KEYWORD_PROCESS_TYPES.MethodCall;
	}

	@Override
	public ArrayList<Class<? extends Exception>> addThrowsToTest(){
		ArrayList<Class<? extends Exception>> exceptionList = new ArrayList<Class<? extends Exception>>();
		exceptionList.add(MalformedURLException.class);
		return exceptionList;
	}

	@Override
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
          .setName("UpdateTestDomain")
          .setStatic(true)
          .setVisibility(Visibility.PUBLIC)
          .setReturnType(URL.class)
          .setParameters("DefaultSelenium browser, List inputValues")
          .addThrows(MalformedURLException.class)
          .setBody( "String domain = inputValues.get(0);" +
					"if(domain.startsWith(\"http\")){" +
					"	return new URL(domain);" +
					"}" +
					"else if(domain.startsWith(\"www.\")){" +
					"	return new URL(\"http://\" + domain);" +
					"}" +
					"else {" +
					"	System.out.println(\"ERROR: Invalid new domain.  Domain [\" + domain + \"] can NOT be set as a new Project Context.\");" +
					"	return null;" +
					"}"
        		  );
	}

	@Override
	public String variableName() {
		return "deploymentURL";
	}
	
	/* EXAMPLE:
	 * 	static public URL UpdateTestDomain(List<String> inputValues) throws MalformedURLException{
		String domain = inputValues.get(0);
		if(domain.startsWith("http")){
			return new URL(domain);
		}
		else if(domain.startsWith("www.")){
			return new URL("http://" + domain);
		}
		else {
			System.out.println("ERROR: Invalid new domain.  Domain [" + domain + "] can NOT be set as a new Project Context.");
			return null;
		}
	}
	 */

}
