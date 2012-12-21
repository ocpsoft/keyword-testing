package org.ocpsoft.keywords;

import java.io.FileOutputStream;
import java.io.PrintStream;

public class BeginClassKeyword implements Keyword {

	@Override
	public String getType() {
		return "BeginClass";
	}

	@Override
	public String addInstruction(String testPath, String desc1, String desc2, String desc3, String desc4, String value) {
		try{
			PrintStream writetoTest = new PrintStream(
				     new FileOutputStream(testPath)); 
			writetoTest.println("package com.example.domain;");
			writetoTest.println();
			writetoTest.println("import java.io.File;");
			writetoTest.println("import java.net.URL;");
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
			writetoTest.println("public class " + desc1 + " {//Begin Class");
			writetoTest.println();
			writetoTest.println("   private static final String WEBAPP_SRC = \"src/main/webapp\";");
			writetoTest.println("   @Deployment(testable = false) // testable = false to run as a client");
			writetoTest.println("	public static WebArchive createDeployment() {");
			writetoTest.println("		return ShrinkWrap.create(WebArchive.class, \"FBTutorialDemo.war\")");
			writetoTest.println("						.addAsResource(\"META-INF/persistence.xml\")");
			writetoTest.println("						.addAsWebResource(new File(WEBAPP_SRC, \"index.html\"))");
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
			writetoTest.println("/*HELPER Methods and vars for other Keywords*/");
			writetoTest.println("private String value = \"\";");
			writetoTest.println("private String getValue(String objectType, String objectXPath){");
			writetoTest.println("\tif(objectType.equals(\"select\")) {");
			writetoTest.println("\t\treturn browser.getSelectedLabel(objectXPath);");
			writetoTest.println("\t} else {");
			writetoTest.println("\t\treturn browser.getText(objectXPath);");
			writetoTest.println("\t}");
			writetoTest.println("}");
			writetoTest.println();
			writetoTest.close();
			System.out.println("\nSUCCESS - Class now has beginning!!!");
			return "SUCCESS";
		}
		catch (Exception e) {
			System.err.println("Failure in doBeginTest: " + e);
			return "FAILURE in Beginning Class Instruction: " + e;
		}
	}
}
