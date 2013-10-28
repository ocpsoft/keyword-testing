package org.ocpsoft.individualTests;

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ocpsoft.projectStarter.ActionsFileCreator;
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
		
		String rootPath = Constants.APP_UNDER_TEST__TEST_FILE_PATH;
		String fileName = "Helper.java";
		
		ParserExampleTest.removeClassFile(rootPath + fileName);
		File helperFile = new File(rootPath + fileName);
		Assert.assertTrue(helperFile.exists() == false); //Make sure it's not there and we're starting from scratch
		
		HelperFileCreator.createHelperClassViaParser(rootPath, "com.example.domain");	
		helperFile = new File(rootPath + fileName);
		Assert.assertTrue(helperFile.exists() == true); //Make sure that the HelperFileCreator actually created a file
		
		ParserExampleTest.removeClassFile(rootPath + fileName);
		helperFile = new File(rootPath + fileName);
		Assert.assertTrue(helperFile.exists() == false); //Make sure it's gone again.
	}//End Test Case

	@Test
	public void testSetupActionsFile() throws InterruptedException {//Begin Test Case
		/* This test removes (if any) Actions.java file (in the current KeywordApp project,
		 * Then creates a new one.  We simply verify that a file was created successfully.
		 */
		
		String rootPath = Constants.APP_UNDER_TEST__TEST_FILE_PATH;
		String fileName = "Actions.java";
		
		ParserExampleTest.removeClassFile(rootPath + fileName);
		File actionsFile = new File(rootPath + fileName);
		Assert.assertTrue(actionsFile.exists() == false); //Make sure it's not there and we're starting from scratch
		
		ActionsFileCreator.createActionsClassViaParser(rootPath, "com.example.domain");	
		actionsFile = new File(rootPath + fileName);
		Assert.assertTrue(actionsFile.exists() == true); //Make sure that the ActionsFileCreator actually created a file
		
		ParserExampleTest.removeClassFile(rootPath + fileName);
		actionsFile = new File(rootPath + fileName);
		Assert.assertTrue(actionsFile.exists() == false); //Make sure it's gone again.
	}//End Test Case
	
}//End Class