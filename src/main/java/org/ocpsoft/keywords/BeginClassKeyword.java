package org.ocpsoft.keywords;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.ocpsoft.constants.InputConstants;

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
	public String createKeywordHelperMethod(PrintStream writetoTest){
		return "";
	}
	
	@Override
	public String getAdditionalInputParams(){
		return "";
	}
	
	@Inject
	Instance<Keyword> allKeywords;

	@Override
	public String performKeyword(String testPath, ArrayList<String> inputValues) {
		try{
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
		
		testPath = InputConstants.FILE_LOCATION + "Helper.java";
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
			writetoTest.println("/*HELPER Methods and vars for other Keywords*/");
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
			String returnVal = "";
			for (Keyword keyword : allKeywords) {
				if(keyword.getProcessType().equals(KEYWORD_PROCESS_TYPES.MethodCall)){
					returnVal = keyword.createKeywordHelperMethod(writetoTest);
					if(!returnVal.contains("SUCCESS")){
						return returnVal; //There was an error, stop here and return it.
					}
				}
			}
			
			//End the Helper class
			writetoTest.println("\n}");
			writetoTest.close();
			return "SUCCESS";
		}
		catch (Exception e) {
			System.err.println("Failure in doBeginTest: " + e);
			return "FAILURE in Beginning Class Instruction - Creating Helper.java file: " + e;
		}
		
	}
}
