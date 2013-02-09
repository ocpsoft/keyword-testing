package org.ocpsoft.keywords;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Visibility;
import org.jboss.forge.parser.java.util.Formatter;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

import com.ocpsoft.constants.InputConstants;
import com.thoughtworks.selenium.DefaultSelenium;

public class BeginClassKeyword implements Keyword {

	@Override
	public String getShortName() {
		return "BeginClass";
	}
	
	@Override
	public KEYWORD_PROCESS_TYPES getProcessType(){
		return KEYWORD_PROCESS_TYPES.DirectProcess;
	}
	
	@Override
	@Deprecated
	public String getAdditionalInputParams(){
		return "";
	}
	
	@Override
	public void createKeywordHelperMethod(JavaClass helperClass){
		String methodBody = "JavaClass testClass = JavaParser.create(JavaClass.class);" +
		  		  "testClass" + 
		  				".setName(inputValues.get(0))" +
		  				".setPackage(\"com.example.domain\")" +
		  				".addAnnotation(RunWith.class)" +
		  				".setLiteralValue(Arquillian.class.getSimpleName() + \".class\")" +
		  				".getOrigin().addImport(Arquillian.class);" +
		  				
				  "testClass" + 
				  		".addField().setPrivate()" +
				  		".setStatic(true).setFinal(true)" +
				  		".setType(String.class)" +
				  		".setName(\"WEBAPP_SRC\");" +

				  "testClass.getField(\"WEBAPP_SRC\")" +
				  		".setLiteralInitializer(\"\\\"src/main/webapp\\\"\");" +

				  "testClass.addMethod()" +
				  		".setName(\"createDeployment\")" +
				  		".setStatic(true)" +
				  		".setVisibility(Visibility.PUBLIC)" +
				  		".setReturnType(WebArchive.class)" +
				  		".setBody(\"return ShrinkWrap.create(WebArchive.class, \\\"FBTutorialDemo.war\\\")\" +" +
				  				"\".addAsResource(\\\"META-INF/persistence.xml\\\")\" +" +
				  				"\".addAsWebResource(new File(WEBAPP_SRC, \\\"index.html\\\"))\" +" +
				  				"\".addAsWebResource(new File(WEBAPP_SRC, \\\"index2.html\\\"))\" +" +
				  				"\".addAsWebResource(new File(WEBAPP_SRC, \\\"myInfo.html\\\"))\" +" +
				  				"\".addAsWebResource(new File(WEBAPP_SRC, \\\"friendsInfo.html\\\"))\" +" +
				  				"\".addAsWebInfResource(EmptyAsset.INSTANCE, \\\"beans.xml\\\");\")" +
				  		".addAnnotation(Deployment.class)" +
				  		".getOrigin().addImport(File.class);" +
				  "testClass.addImport(WebArchive.class);" +
				  "testClass.addImport(Deployment.class);" +
				  "testClass.addImport(ShrinkWrap.class);" +
				  "testClass.addImport(EmptyAsset.class);" +
				  "testClass.addImport(DefaultSelenium.class);" +

				  "testClass" +
				  		".addField().setType(DefaultSelenium.class)" +
				  		".setName(\"browser\").addAnnotation(Drone.class)" +
				  		".getOrigin().addImport(DefaultSelenium.class);" +
				  "testClass.addImport(Drone.class);" +

				  "testClass" +
				  		".addField().setType(URL.class)" +
				  		".setName(\"deploymentURL\").addAnnotation(ArquillianResource.class)" +
				  		".getOrigin().addImport(URL.class);" +
				  "testClass.addImport(ArquillianResource.class);" +

				  "testClass.addImport(PrintStream.class);" + 
				  "testClass.addImport(FileOutputStream.class);" +
				  "testClass.addImport(Formatter.class);"; //Note, this is a current workaround for Parser Errors with Try-Catch block.  Gets replaced later.
		
//		String methodBody2 =
//				  "try{" +
//				  		"PrintStream writetoTest = new PrintStream(" +
//				  			"new FileOutputStream(rootPath + inputValues.get(0))); " +
//				  		"writetoTest.print(Formatter.format(testClass));" +
//				  		"writetoTest.close();" +
//				  "}" +
//				  "catch (Exception e) {" +
//				  		"System.err.println(\"Failure in createTestClassViaParser: \" + e);" +
//				  "}";		

		helperClass.addMethod()
			.setName("BeginClass")
        	.setStatic(true)
        	.setVisibility(Visibility.PUBLIC)
        	.setReturnTypeVoid()
        	.setParameters("DefaultSelenium browser, List inputValues")
        	//TODO: Parser is not liking the try catch block right now.
        	.setBody( methodBody );
		
	}

	@Override
	@Deprecated
	public String performKeyword(JavaClass nullClass, ArrayList<String> inputValues) {
		JavaClass testClass = JavaParser.create(JavaClass.class);
		testClass.setName(inputValues.get(0)).setPackage("com.example.domain")
				.addAnnotation(RunWith.class)
				.setLiteralValue(Arquillian.class.getSimpleName() + ".class")
				.getOrigin().addImport(Arquillian.class);
		testClass.addField().setPrivate().setStatic(true).setFinal(true)
				.setType(String.class).setName("WEBAPP_SRC");
		testClass.getField("WEBAPP_SRC").setLiteralInitializer(
				"\"src/main/webapp\"");
		testClass
				.addMethod()
				.setName("createDeployment")
				.setStatic(true)
				.setVisibility(Visibility.PUBLIC)
				.setReturnType(WebArchive.class)
				.setBody(
						"return ShrinkWrap.create(WebArchive.class, \"FBTutorialDemo.war\")"
								+ ".addAsResource(\"META-INF/persistence.xml\")"
								+ ".addAsWebResource(new File(WEBAPP_SRC, \"index.html\"))"
								+ ".addAsWebResource(new File(WEBAPP_SRC, \"index2.html\"))"
								+ ".addAsWebResource(new File(WEBAPP_SRC, \"myInfo.html\"))"
								+ ".addAsWebResource(new File(WEBAPP_SRC, \"friendsInfo.html\"))"
								+ ".addAsWebInfResource(EmptyAsset.INSTANCE, \"beans.xml\");")
				.addAnnotation(Deployment.class).getOrigin()
				.addImport(File.class);
		testClass.addImport(WebArchive.class);
		testClass.addImport(Deployment.class);
		testClass.addImport(ShrinkWrap.class);
		testClass.addImport(EmptyAsset.class);
		testClass.addImport(DefaultSelenium.class);
		testClass.addField().setType(DefaultSelenium.class).setName("browser")
				.addAnnotation(Drone.class).getOrigin()
				.addImport(DefaultSelenium.class);
		testClass.addImport(Drone.class);
		testClass.addField().setType(URL.class).setName("deploymentURL")
				.addAnnotation(ArquillianResource.class).getOrigin()
				.addImport(URL.class);
		testClass.addImport(ArquillianResource.class);
		try {
			PrintStream writetoTest = new PrintStream(new FileOutputStream(
					InputConstants.ROOT_FILE_PATH + inputValues.get(0) + ".java"));
			writetoTest.print(Formatter.format(testClass)); //TODO: This doesn't work, low priority to fix
			writetoTest.close();
		} catch (Exception e) {
			System.err.println("Failure in create new TestClass: " + e);
			return "ERROR: Could not create new Class.";
		}
		
		System.out.println("SUCCESS: New Class [" + inputValues.get(0) + "] created successfully.");
		return "SUCCESS: New Class [" + inputValues.get(0) + "] created successfully.";
		
		/*OLD, NON-Parser way
		 * try{
			PrintStream writetoTest = new PrintStream(
				     new FileOutputStream(testPath)); 
			writetoTest.println("package com.example.domain;");
			writetoTest.println();
			writetoTest.println("import java.io.File;");
			writetoTest.println("import java.net.URL;");
			writetoTest.println("import java.util.Arrays;");
			writetoTest.println();
			writetoTest.println("import org.jboss.arquillian.container.test.api.Deployment;");
			writetoTest.println("import org.jboss.arquillian.drone.api.annotation.Drone;");
			writetoTest.println("import org.jboss.arquillian.junit.Arquillian;");
			writetoTest.println("import org.jboss.arquillian.test.api.ArquillianResource;");
			writetoTest.println("import org.jboss.shrinkwrap.api.ShrinkWrap;");
			writetoTest.println("import org.jboss.shrinkwrap.api.asset.EmptyAsset;");
			writetoTest.println("import org.jboss.shrinkwrap.api.spec.WebArchive;");
			writetoTest.println("import org.junit.Assert;");
			writetoTest.println("import org.junit.Test;");
			writetoTest.println("import org.junit.runner.RunWith;");
			writetoTest.println();
			writetoTest.println("import com.thoughtworks.selenium.DefaultSelenium;");
			writetoTest.println();
			writetoTest.println("@RunWith(Arquillian.class)");
			writetoTest.println("public class " + inputValues.get(0) + " {//Begin Class");
			writetoTest.println();
			writetoTest.println("   private static final String WEBAPP_SRC = \"src/main/webapp\";");
			writetoTest.println("   @Deployment(testable = false) // testable = false to run as a client");
			writetoTest.println("	public static WebArchive createDeployment() {");
			writetoTest.println("		return ShrinkWrap.create(WebArchive.class, \"FBTutorialDemo.war\")");
			writetoTest.println("						.addAsResource(\"META-INF/persistence.xml\")");
			writetoTest.println("						.addAsWebResource(new File(WEBAPP_SRC, \"index.html\"))");
			writetoTest.println("						.addAsWebResource(new File(WEBAPP_SRC, \"index2.html\"))");
			writetoTest.println("						.addAsWebResource(new File(WEBAPP_SRC, \"myInfo.html\"))");
			writetoTest.println("						.addAsWebResource(new File(WEBAPP_SRC, \"friendsInfo.html\"))");
			writetoTest.println("						.addAsWebInfResource(EmptyAsset.INSTANCE, \"beans.xml\");");
			writetoTest.println("	}");
			writetoTest.println();
			writetoTest.println("@Drone");
			writetoTest.println("DefaultSelenium browser;");
			writetoTest.println();
			writetoTest.println("@ArquillianResource");
			writetoTest.println("URL deploymentURL;");
			writetoTest.println();
			writetoTest.close();
			System.out.println("\nSUCCESS - Class now has beginning!!!");
		}
		catch (Exception e) {
			System.err.println("Failure in doBeginTest: " + e);
			return "FAILURE in Beginning Class Instruction: " + e;
		}
		
		testPath = InputConstants.ROOT_FILE_PATH + "Helper.java";
		//Create the Helper file so we can call a simple method for each keyword for each step in every test
		try{
			PrintStream writetoTest = new PrintStream(
				     new FileOutputStream(testPath)); 
			writetoTest.println("package com.example.domain;");
			writetoTest.println();
			writetoTest.println("import java.util.List;");
			writetoTest.println("import com.thoughtworks.selenium.DefaultSelenium;");
			writetoTest.println("import java.net.URL;");
			writetoTest.println("import org.junit.Assert;");
			writetoTest.println();
			writetoTest.println("public class Helper {\n\n");
			//Add any generic helper functions here:
			writetoTest.println("//HELPER Methods and vars for other Keywords");
			writetoTest.println("\tprivate static String value = \"\";");
			writetoTest.println("\tprivate static String getValue(DefaultSelenium browser, String objectType, String objectXPath){");
			writetoTest.println("\t\tif(objectType.equals(\"select\")) {");
			writetoTest.println("\t\t\treturn browser.getSelectedLabel(objectXPath);");
			writetoTest.println("\t\t} else if(objectType.equals(\"input\")) {");
			writetoTest.println("\t\t\treturn browser.getValue(objectXPath);");			
			writetoTest.println("\t\t} else {");
			writetoTest.println("\t\t\treturn browser.getText(objectXPath);");
			writetoTest.println("\t\t}");
			writetoTest.println("\t}");	
			writetoTest.println();
			writetoTest.println();

			//Go through every keyword and add each method for the MethodLoad Keywords to the Helper file
//			String returnVal = "";
//			for (Keyword keyword : allKeywords) {
//				if(keyword.getProcessType().equals(KEYWORD_PROCESS_TYPES.MethodCall)){
//					returnVal = keyword.createKeywordHelperMethod(writetoTest);
//					if(!returnVal.contains("SUCCESS")){
//						return returnVal; //There was an error, stop here and return it.
//					}
//				}
//			}
			
			//End the Helper class
			writetoTest.println("\n}");
			writetoTest.close();
			return "SUCCESS";
		}
		catch (Exception e) {
			System.err.println("Failure in doBeginTest: " + e);
			return "FAILURE in Beginning Class Instruction - Creating Helper.java file: " + e;
		}
		*/
	}
}
