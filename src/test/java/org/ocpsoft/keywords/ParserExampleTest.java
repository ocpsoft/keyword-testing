package org.ocpsoft.keywords;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Visibility;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ocpsoft.utils.Constants;
import com.ocpsoft.utils.Utility;
import com.thoughtworks.selenium.DefaultSelenium;

@RunWith(Arquillian.class)
public class ParserExampleTest
{

   	@Deployment(testable = false)
	public static WebArchive createDeployment() {
		return ShrinkWrap.create(WebArchive.class)
						.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
	}
	   
   	String packageLocation = getClass().getPackage().toString().substring("package ".length());
   	String packagePath = packageLocation.replace(".", "/");
	   
	@Test
	public void testParser() throws InterruptedException {
		String rootPath = Constants.KEYWORD_PROJECT_ROOT_FILE_PATH + "src/test/java/" + packagePath + "/";
		String className = "ParseTest";
		removeClassFile(rootPath + className + ".java");
		createTestClassViaParser(rootPath + className + ".java", className);
		File testClassFile = new File(rootPath + className + ".java");
		JavaClass testClass = null;
		try {
			testClass = (JavaClass) JavaParser.parse(testClassFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Assert.assertTrue("Error in testParser: " + e, false);
		}
		List<Member<JavaClass, ?>> allMembers = testClass.getMembers();
		Assert.assertTrue("Could not find static var, 2 other vars, and getDeployment method.", allMembers.size() == 4);
		removeClassFile(rootPath + className + ".java");
	}
	
	private void createTestClassViaParser(String testPath, String className) {
		JavaClass testClass = JavaParser.create(JavaClass.class);
	
	      testClass
	               .setName(className)
	               .setPackage(packageLocation)
	               .addAnnotation(RunWith.class).setLiteralValue(Arquillian.class.getSimpleName() + ".class")
	               .getOrigin().addImport(Arquillian.class);
	      
	      testClass
	      		.addField().setPrivate()
	      		.setStatic(true).setFinal(true)
	      		.setType(String.class)
	      		.setName("WEBAPP_SRC");
	      
	      testClass.getField("WEBAPP_SRC")
	      		.setLiteralInitializer("\"src/main/webapp\"");
	
	      testClass.addMethod()
	               .setName("getDeployment")
	               .setStatic(true)
	               .setVisibility(Visibility.PUBLIC)
	               .setReturnType(WebArchive.class)
	               .setBody("return ShrinkWrap.create(WebArchive.class)\n" +
						"\t.addAsResource(\"META-INF/persistence.xml\")\n" +
						"\t.addAsWebResource(new File(WEBAPP_SRC, \"index.html\"))\n" +
						"\t.addAsWebResource(new File(WEBAPP_SRC, \"index2.html\"))\n" +
						"\t.addAsWebResource(new File(WEBAPP_SRC, \"myInfo.html\"))\n" +
						"\t.addAsWebResource(new File(WEBAPP_SRC, \"friendsInfo.html\"))\n" +
						"\t.addAsWebInfResource(EmptyAsset.INSTANCE, \"beans.xml\");")
	               .addAnnotation(Deployment.class)
	               .getOrigin().addImport(File.class);
	      testClass.addImport(WebArchive.class);
	      testClass.addImport(ShrinkWrap.class);
	      testClass.addImport(EmptyAsset.class);
	      testClass.addImport(DefaultSelenium.class);
	      
	      testClass
	      		.addField().setType(DefaultSelenium.class)
	      		.setName("browser").addAnnotation(Drone.class)
	      		.getOrigin().addImport(DefaultSelenium.class);
	      testClass.addImport(Drone.class);
	      
	      testClass
	    		.addField().setType(URL.class)
	    		.setName("deploymentURL").addAnnotation(ArquillianResource.class)
	    		.getOrigin().addImport(URL.class);
	      testClass.addImport(ArquillianResource.class);
	      
			try{
				PrintStream writetoTest = new PrintStream(
					     new FileOutputStream(testPath)); 
				writetoTest.print(testClass.toString());
				writetoTest.close();
			}
			catch (Exception e) {
				System.err.println("Failure in createTestClassViaParser: " + e);
			}
	}
	
	public static void removeClassFile(String filePath){
		File classFile = new File(filePath);
		try{
			if(!classFile.exists()){
				System.out.println(filePath + " does NOT exist.  Nothing to Remove");
			}
			else{
				if(classFile.delete()){
					System.out.println(classFile.getName() + " is deleted!");
				}else{
					System.out.println("Delete operation is failed.");
					assertTrue(false);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
