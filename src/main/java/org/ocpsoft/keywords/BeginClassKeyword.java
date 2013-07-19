package org.ocpsoft.keywords;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Visibility;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import com.ocpsoft.utils.Constants;
import com.ocpsoft.utils.Constants.KEYWORD_KEYS;

public class BeginClassKeyword implements Keyword {

	public BeginClassKeyword(){
	}

	@Override
	public KEYWORD_KEYS shortName() {
		return KEYWORD_KEYS.BeginClass;
	}
	
	@Override
	public KEYWORD_PROCESS_TYPES processType(){
		return KEYWORD_PROCESS_TYPES.DirectProcess;
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
	public void createKeywordHelperMethod(JavaClass helperClass){
	}

	@Override
	public String performKeyword(JavaClass nullClass, String nullTestName, ArrayList<String> inputValues) {
		JavaClass testClass = JavaParser.create(JavaClass.class);
		testClass.setName(inputValues.get(0)).setPackage("com.example.domain")
				.addAnnotation(Constants.RunWithAnnotationClass)
				.setLiteralValue(Constants.ArquillianAnnotationClass + ".class")
				.getOrigin().addImport(Constants.ArquillianAnnotationClass);
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
								+ ".addAsWebInfResource(EmptyAsset.INSTANCE, \"beans.xml\");")
				.addAnnotation(Constants.DeploymentAnnotationClass).setLiteralValue("testable", "false").getOrigin() //TESTABLE = FALSE is KEY
				.addImport(File.class);
		
		//TODO: #DeploymentURL_HACK
		testClass.addImport(URL.class);
		
		testClass.addImport(WebArchive.class);
		testClass.addImport(Constants.DeploymentAnnotationClass);
		testClass.addImport(ShrinkWrap.class);
		testClass.addImport(EmptyAsset.class);
		testClass.addImport(Constants.DefaultSeleniumAnnotationClass);
		testClass.addField().setType(Constants.DefaultSeleniumAnnotationClass).setName("browser")
				.addAnnotation(Constants.DroneAnnotationClass).getOrigin();
		testClass.addImport(Constants.DroneAnnotationClass);
		testClass.addField().setType(URL.class).setName("deploymentURL")
				.addAnnotation(Constants.ArquillianResourceAnnotationClass).getOrigin()
				.addImport(URL.class);
		testClass.addImport(Constants.ArquillianResourceAnnotationClass);
		testClass.addImport(Arrays.class);
		try {
			PrintStream writetoTest = new PrintStream(new FileOutputStream(
					Constants.APP_UNDER_TEST__TEST_FILE_PATH + inputValues.get(0) + ".java"));
			
			writetoTest.print(testClass); //TODO: Format this
			writetoTest.close();
		} catch (Exception e) {
			System.err.println("Failure in create new TestClass: " + e);
			return "ERROR: Could not create new Class.";
		}
		
		System.out.println("SUCCESS: New Class [" + inputValues.get(0) + "] created successfully.");
		return "SUCCESS: New Class [" + inputValues.get(0) + "] created successfully.";
	}
}
