package com.ocpsoft.projectStarter;

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.keywords.Keyword;
import org.ocpsoft.keywords.KeywordFactory;
import org.ocpsoft.services.MyWebService;

import com.ocpsoft.constants.InputConstants;

@RunWith(Arquillian.class)
public class StarterTest {//Begin Class

   private static final String WEBAPP_SRC = "src/main/webapp";
   
   @Deployment(testable = false) // testable = false to run as a client
	public static WebArchive createDeployment() {
	   MavenDependencyResolver resolver = DependencyResolvers.use(
			   MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");
		return ShrinkWrap.create(WebArchive.class, "Startup.war")
						.addClasses(MyWebService.class, InputConstants.class)
						.addClasses(KeywordFactory.class, Keyword.class, JavaClass.class, ServiceLoader.class, Iterators.class)
						.addAsLibraries(resolver.artifacts("org.jboss.forge:forge-parser-java")
									.resolveAsFiles())
						.addAsLibraries(resolver.artifacts("org.ocpsoft.common:common-api")
									.resolveAsFiles())									
						.addAsResource("META-INF/persistence.xml")
						.addAsWebResource(new File(WEBAPP_SRC, "index.jsp"))
						.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Test
	public void testSetupHelperFile() throws InterruptedException {//Begin Test Case
		/* This test removes (if any) Helper.java file,
		 * Then creates a new one.  We simply verify that a file was created successfully.
		 */
		
		String rootPath = "/home/fife/workspace/KeywordApp/src/test/java/com/ocpsoft/projectStarter/";
		File helperFile = new File(rootPath + "Helper.java");
		
		//First, if Helper.java exists, remove it so we can generate a fresh one we know will be up to date
		try{
			if(!helperFile.exists()){
				System.out.println("Helper.java does NOT exist, nothing to clear out.");
			}
			else{
				if(helperFile.delete()){
					System.out.println(helperFile.getName() + " is deleted!  We just made room to create a fresh one");
				}else{
					System.out.println("Delete operation is failed.");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Error msg: " + e);
		}
		
		HelperFileCreator.createHelperClassViaParser(rootPath, "com.ocpsoft.projectStarter");	
	}//End Test Case

}//End Class