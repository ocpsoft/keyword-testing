package org.ocpsoft.keywords;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Visibility;

public class UpdateTestDomainKeyword implements Keyword {

	@Override
	public String getShortName() {
		return "UpdateTestDomain";
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
          .setParameters("List inputValues")
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
