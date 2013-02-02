package com.ocpsoft.projectStarter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.List;

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
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.keywords.Keyword;
import org.ocpsoft.keywords.Keyword.KEYWORD_PROCESS_TYPES;

import com.ocpsoft.constants.InputConstants;
import com.thoughtworks.selenium.DefaultSelenium;

public class HelperFileCreator {

	public static void createHelperClassViaParser(String filePath, String packageName) {
		JavaClass helperClass = JavaParser.create(JavaClass.class);

	      helperClass
	               .setName("Helper")
	               .setPackage(packageName);
	      helperClass.addImport(List.class);
	      helperClass.addImport(DefaultSelenium.class);
	      helperClass.addImport(URL.class);
	      helperClass.addImport(Assert.class);
	      helperClass.addImport(InputConstants.class);
	      helperClass.addImport(Visibility.class);
	      helperClass.addImport(File.class);
	      helperClass.addImport(Arquillian.class);
	      helperClass.addImport(ArquillianResource.class);
	      helperClass.addImport(RunWith.class);
	      helperClass.addImport(WebArchive.class);
	      helperClass.addImport(ShrinkWrap.class);
	      helperClass.addImport(EmptyAsset.class);
	      helperClass.addImport(Drone.class);
	      helperClass.addImport(PrintStream.class);
	      helperClass.addImport(FileOutputStream.class);
	      helperClass.addImport(Formatter.class);

	      helperClass
	      		.addField().setPrivate().setStatic(true).setType(String.class).setName("value").setLiteralInitializer("");

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
				writetoTest.print(Formatter.format(helperClass));
				writetoTest.close();
				System.out.println("Successfully created Helper.java file in " + packageName);
			}
			catch (Exception e) {
				System.err.println("Failure in doBeginTest: " + e);
			}
	}
}
