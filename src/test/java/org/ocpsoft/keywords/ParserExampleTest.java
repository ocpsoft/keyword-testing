package org.ocpsoft.keywords;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Visibility;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ocpsoft.constants.InputConstants;
import com.thoughtworks.selenium.DefaultSelenium;

@RunWith(Arquillian.class)
public class ParserExampleTest
{

	   	@Deployment // testable = false to run as a client
		public static WebArchive createDeployment() {
		   	MavenDependencyResolver resolver = DependencyResolvers.use(
				   MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");
			return ShrinkWrap.create(WebArchive.class)
							.addAsResource("META-INF/persistence.xml")
							.addAsLibraries(resolver.artifacts("org.jboss.forge:forge-parser-java")
									.resolveAsFiles())
							.addAsLibraries(resolver.artifacts("org.seleniumhq.selenium:selenium-java")
									.resolveAsFiles())
							.addAsLibraries(resolver.artifacts("org.seleniumhq.selenium:selenium-server")
									.resolveAsFiles())									
							.addAsLibraries(resolver.artifacts("org.jboss.arquillian.extension:arquillian-drone-impl")
									.resolveAsFiles())									
							.addAsLibraries(resolver.artifacts("org.jboss.arquillian.extension:arquillian-drone-selenium")
									.resolveAsFiles())									
							.addAsLibraries(resolver.artifacts("org.jboss.arquillian.extension:arquillian-drone-selenium-server")
									.resolveAsFiles())
							.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
		}
	   
	   
	@Test
	public void testParser() throws InterruptedException {
		String className = "ParseTest";
		removeClassFile(InputConstants.ROOT_FILE_PATH + className + ".java");
		createTestClassViaParser(InputConstants.ROOT_FILE_PATH + className + ".java", className);
	}
	
	private void createTestClassViaParser(String testPath, String className) {
		JavaClass testClass = JavaParser.create(JavaClass.class);
	
	      testClass
	               .setName(className)
	               .setPackage("com.example.domain")
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
	               .setBody("return ShrinkWrap.create(WebArchive.class, \"FBTutorialDemo.war\")\n" +
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
	
	public void removeClassFile(String filePath){
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