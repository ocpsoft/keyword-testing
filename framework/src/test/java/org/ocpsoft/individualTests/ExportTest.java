package org.ocpsoft.individualTests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.utils.TestUtility;

import com.ocpsoft.utils.ConfigXMLParser;
import com.ocpsoft.utils.Constants;
import com.ocpsoft.utils.Constants.KEYWORD_KEYS;
import com.ocpsoft.utils.Utility;
import com.thoughtworks.selenium.DefaultSelenium;

@RunWith(Arquillian.class)
public class ExportTest {//Begin Class

	/*NOTE: For best results:
	 * 		Start up the server.
	 * 		Do a full publish of the Keyword Application
	 * 		Run the file as a JUnit Test with AS7_REMOTE container.
	 */
	
	@Deployment(testable = false) // testable = false client mode
	public static WebArchive createDeployment() {
		return ShrinkWrap.create(WebArchive.class, "export-testing.war")
						.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Drone
	DefaultSelenium browser;
	
	@ArquillianResource
	URL deploymentURL;

	private String exportFilePath = Constants.EXPORT_FILE_PATH + Constants.KEYWORD_VALUES.get(KEYWORD_KEYS.BeginTest).get(0) + ".txt";

	@Test
	public void testBuildingATestExportingItThenImportingItBack() throws InterruptedException {//Begin Test Case
		/* This test builds a new suite and 1 new test via the app.
		 * Once constructed, it will click the [Export Current Test] button to export it to file.
		 * We will then open a new FileStream to read the file contents and verify they are correct.
		 */

		buildTest();
		System.out.println("***************** Built test successfully ****************");
		verifyCorrectTestStepsOnUI("testName");
		
		browser.click("id=exportTestCase");
		TestUtility.waitForCallbackToComplete(browser, "File Created!");
		
		verifyOutputFileOfExport();
		System.out.println("***************** Verified UI message of Export successfully ****************");
		
		//Clear the entire class, then create a new test and try to import the thing we just exported
		browser.click("id=deleteSuite");
		importTestFromFile("testViaImport");
		System.out.println("***************** Imported test back successfully ****************");
		
		//Verfiy the correct message on the UI for the import step
		String value = TestUtility.getValue(browser, "div", "//div[@id='testSuite']");
		String expected = "Just finished importing the following Keywords:\n"+
				  Constants.KEYWORD_KEYS.OpenBrowser.toString() + "\n"+
				  Constants.KEYWORD_KEYS.VerifyObjectIsNotDisplayed.toString() + "\n"+
				  Constants.KEYWORD_KEYS.VerifyObjectProperty.toString() + "\n"+
		          "[3] total instructions have been imported.";
		Assert.assertTrue("value should be [" + expected + "]\n\n\n value is [" + value + "]",
		    expected.equals(value));
		
		//Now load the suite that's there from the import and make sure it matches the same UI test steps
		browser.click("id=loadSuite");
		Thread.sleep(100);
		verifyCorrectTestStepsOnUI("testViaImport");
	}//End Test Case
		
	private void buildTest() throws InterruptedException {
		TestUtility.OpenPageAndBeginNewSuiteAndTest(browser, deploymentURL);
	    
	    String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.OpenBrowser);
	    browser.select("id=keyword", "label=" + valToSelect);
	    Thread.sleep(100);
	    TestUtility.clickAddInstruction(browser);
	    Thread.sleep(100);
	    
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.VerifyObjectIsNotDisplayed);
	    browser.select("id=keyword", "label=" + valToSelect);
	    TestUtility.clickAddInstruction(browser);
	    Thread.sleep(100);
	    
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.VerifyObjectProperty);
	    browser.select("id=keyword", "label=" + valToSelect);
	    TestUtility.clickAddInstruction(browser);
	    Thread.sleep(100);
	}
	
	private void verifyCorrectTestStepsOnUI(String testCaseName) {
		String value = TestUtility.getValue(browser, "div", "//div[@id='testSuite']");
		String expected = "Test Suite Named: MySuiteTest\n" +
				testCaseName + "\n" +
				"|UP| |DOWN| AssignVariable: with name: deploymentURL, with value of: new URL(\"http://localhost:8080/framework/\")\n" +
				"|UP| |DOWN| OpenBrowser: with Webpage of test Domain plus (OPTIONAL FIELD - " + 
				"if adding onto end of the domain): index.jsp\n" +
				"|UP| |DOWN| VerifyObjectIsNotDisplayed: with Verification Message of: User " +
				"should NOT see message [Error: invalid action]!, with XPath property to verify of: div[@id='myFBdata']\n" +
				"|UP| |DOWN| VerifyObjectProperty: with Verification Message of: Selected Value should be Begin New Suite, " +
				"with object Type of: select, with XPath property to verify of: //select[@id='keyword'], with value to verify of: Begin New Suite";
		Assert.assertEquals("value should be equal", expected, value);
	}
	
	private void verifyOutputFileOfExport() {
		//TODO: #DeploymentURL_HACK
		//Note: We need to specifically NOT export the AssignVariableKeyword step for the deploymentURL
		//Since this is auto imported at the beginning of every test (and we dont want 2 when we import these steps)
		ArrayList<String> inputs1 = Utility.convertListStringToArrayListString(Constants.KEYWORD_VALUES.get(KEYWORD_KEYS.OpenBrowser));
		ArrayList<String> inputs2 = Utility.convertListStringToArrayListString(Constants.KEYWORD_VALUES.get(KEYWORD_KEYS.VerifyObjectIsNotDisplayed));
		ArrayList<String> inputs3 = Utility.convertListStringToArrayListString(Constants.KEYWORD_VALUES.get(KEYWORD_KEYS.VerifyObjectProperty));
		
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add(KEYWORD_KEYS.OpenBrowser.toString());
		keywords.add(KEYWORD_KEYS.VerifyObjectIsNotDisplayed.toString());
		keywords.add(KEYWORD_KEYS.VerifyObjectProperty.toString());
		ArrayList<ArrayList<String>> inputs = new ArrayList<ArrayList<String>>();
		inputs.add(inputs1);
		inputs.add(inputs2);
		inputs.add(inputs3);
		ConfigXMLParser xmlParser = new ConfigXMLParser();
		String expectedFile = xmlParser.generateInstructionSetXMLDoc(keywords, inputs);
		expectedFile += "\n"; //Since the entire file is going to have an extra newLine, make one here too since it's easier.

		String entireFile = "";
		File file = new File(exportFilePath);
		if(file.exists()){
			try (BufferedReader br = new BufferedReader(new FileReader(file)))
			{
				String sCurrentLine;
				while ((sCurrentLine = br.readLine()) != null) {
					entireFile+=sCurrentLine + "\n";
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("File does not exist after clicking the Export button.");
			Assert.assertTrue("Export did not work. Fail test...", false);
		}
		
		Assert.assertEquals("Exported file should match entire file", expectedFile, entireFile);
	}

	private void importTestFromFile(String testName) throws InterruptedException {
		TestUtility.createNewSuiteAndTest(browser, testName);
		
		browser.type("//input[@id='ImportInput1']", exportFilePath);
		browser.click("id=importSteps");
		TestUtility.waitForCallbackToComplete(browser, "Just finished importing the following Keywords:");
	}
}//End Class