package com.ocpsoft.projectStarter;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Visibility;
import org.jboss.forge.parser.java.util.Formatter;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.keywords.Keyword;
import org.ocpsoft.keywords.Keyword.KEYWORD_PROCESS_TYPES;

import com.ocpsoft.constants.InputConstants;

public class HelperFileCreator {

	public static void createHelperClassViaParser(String filePath, String packageName) {
		JavaClass helperClass = JavaParser.create(JavaClass.class);

	      helperClass
	               .setName("Helper")
	               .setPackage(packageName);
	      helperClass.addImport(List.class);

	      helperClass
	      		.addField().setPrivate().setStatic(true).setType(String.class).setName("value").setLiteralInitializer("");

	      helperClass
	      		.addField().setPrivate().setStatic(true).setType(String.class).setName("rootPath").setStringInitializer(InputConstants.ROOT_FILE_PATH);
	      helperClass.addMethod()
	               .setName("getValue")
	               .setStatic(true)
	               .setVisibility(Visibility.PUBLIC)
	               .setReturnType(String.class)
	               .setParameters("DefaultSelenium browser, String objectType, String objectXPath")
	               .setBody("if(objectType.equals(\"select\")) {\n" +
							"	return browser.getSelectedLabel(objectXPath);\n" +
							"} else if(objectType.equals(\"input\")) {\n" +
							"	return browser.getValue(objectXPath);\n" +
							"} else {\n" +
							"	return browser.getText(objectXPath);\n" +
							"}");
	      
	      	//Go through every keyword and add each method for the MethodLoad Keywords to the Helper file
	      	@SuppressWarnings("unchecked")
			List<Keyword> allKeywords = Iterators.asList(ServiceLoader.load(Keyword.class));
			for (Keyword keyword : allKeywords) {
				if(keyword.getProcessType().equals(KEYWORD_PROCESS_TYPES.MethodCall)){
					//Add this keyword's HelperMethod to the class.
//					if(keyword.getShortName().equals(InputConstants.KEYWORD_KEYS.BeginClass.toString())){
						keyword.createKeywordHelperMethod(helperClass);
//					}
				}
			}
	      
			try{
				PrintStream writetoTest = new PrintStream(
					     new FileOutputStream(filePath + "Helper.java")); 
				writetoTest.print(resolveParserIssuesAndFormat(helperClass));
				writetoTest.close();
				System.out.println("Successfully created Helper.java file in " + packageName);
			}
			catch (Exception e) {
				System.err.println("Failure in doBeginTest: " + e);
			}
	}
	
	//TODO: Currently a number of issues with JavaParser.  We're going to hardcode replace statements to get around them for the time being.
	private static String resolveParserIssuesAndFormat(JavaClass helperClass){
		
		//TODO: Note, Formatting should be this easy, but for some reason it's not working
		//We'll have to come back to this later, this is a low priority
		String returnVal = Formatter.format(helperClass); 
		
		returnVal = returnVal.replaceAll("List inputValues", "List<String> inputValues");
		
		String beginClassMethodBody2 =
				  "testClass.addImport(Formatter.class);" +
				  "try{" +
				  		"PrintStream writetoTest = new PrintStream(" +
				  			"new FileOutputStream(rootPath + inputValues.get(0))); " +
				  		"writetoTest.print(Formatter.format(testClass));" +
				  		"writetoTest.close();" +
				  "}" +
				  "catch (Exception e) {" +
				  		"System.err.println(\"Failure in createTestClassViaParser: \" + e);" +
				  "}";
		returnVal = returnVal.replace("testClass.addImport(Formatter.class);", beginClassMethodBody2);
		
		String beginTestMethodBody2 = "JavaClass testClass;" +
    			"try{" +
    			"	File testClassFile = new File(rootPath" +
    			"			+ inputValues.get(1));" +
    			"	testClass = (JavaClass) JavaParser.parse(testClassFile);" +
    			"}" +
    			"catch (Exception e) {" +
    			"	System.out.println(\"Error in trying to get the testClass File - e:\" + e);" +
    			"	return;" +
    			"}";
		returnVal = returnVal.replace("JavaClass testClass;", beginTestMethodBody2);
		
		return returnVal; 
	}
}
