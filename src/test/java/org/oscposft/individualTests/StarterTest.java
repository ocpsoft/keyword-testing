package org.oscposft.individualTests;

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.keywords.Keyword;
import org.ocpsoft.keywords.KeywordFactory;
import org.ocpsoft.services.MyWebServiceImpl;

import com.ocpsoft.projectStarter.HelperFileCreator;
import com.ocpsoft.utils.Constants;

@RunWith(Arquillian.class)
public class StarterTest {//Begin Class

   @Deployment(testable = false) // testable = false to run as a client
	public static WebArchive createDeployment() {
		return ShrinkWrap.create(WebArchive.class, "Startup.war")
						.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
	}
   
  	String packageLocation = getClass().getPackage().toString().substring("package ".length());
  	String packagePath = packageLocation.replace(".", "/");
  	
	@Test
	public void testSetupHelperFile() throws InterruptedException {//Begin Test Case
		/* This test removes (if any) Helper.java file (in the current KeywordApp project,
		 * Then creates a new one.  We simply verify that a file was created successfully.
		 */
		
		String rootPath = Constants.ROOT_FILE_PATH;
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
		
		helperFile = new File(rootPath + "Helper.java");
		Assert.assertTrue(helperFile.exists() == false);
		HelperFileCreator.createHelperClassViaParser(rootPath, "com.example.domain");	
		helperFile = new File(rootPath + "Helper.java");
		Assert.assertTrue(helperFile.exists() == true);
		ParserExampleTest.removeClassFile(rootPath + "Helper.java");
	}//End Test Case

}//End Class