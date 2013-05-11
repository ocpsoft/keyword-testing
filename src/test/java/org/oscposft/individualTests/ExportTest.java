package org.oscposft.individualTests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

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
import org.ocpsoft.keywords.Keyword;
import org.ocpsoft.keywords.KeywordFactory;

import com.ocpsoft.utils.Constants;
import com.ocpsoft.utils.Constants.KEYWORD_KEYS;
import com.thoughtworks.selenium.DefaultSelenium;

@RunWith(Arquillian.class)
public class ExportTest {//Begin Class

	/*NOTE: For best results:
	 * 		Start up the server.
	 * 		Do a full publish of the Keyword Application
	 * 		Run the file as a JUnit Test with AS7_REMOTE container.
	 */
	
   @Deployment(testable = false) // testable = false to run as a client
	public static WebArchive createDeployment() {
		return ShrinkWrap.create(WebArchive.class, "keword-testing.war")
						.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
	}

@Drone
DefaultSelenium browser;

@ArquillianResource
URL deploymentURL;

private String getValue(String objectType, String objectXPath){
	if(objectType.equals("select")) {
		return browser.getSelectedLabel(objectXPath);
	} else if(objectType.equals("input")) {
		return browser.getValue(objectXPath);
	} else if(objectType.equals("div")) {
		//TODO: Really should do innerHTML, but having problems getting that right now.
		//Text will have to be good enough right now, there is no method to get innerHTML.
		return browser.getText(objectXPath);
	} else {
		return browser.getText(objectXPath);
	}
}

	private String exportFilePath = Constants.EXPORT_FILE_PATH + Constants.KEYWORD_VALUES.get(KEYWORD_KEYS.BeginTest).get(0) + ".txt";

	@Test
	public void testBuildingATestAndExportingIt() throws InterruptedException {//Begin Test Case
		/* This test builds a new suite and 1 new test via the app.
		 * Once constructed, it will click the [Export Current Test] button to export it to file.
		 * We will then open a new FileStream to read the file contents and verify they are correct.
		 */
		
		browser.open(deploymentURL + "index.jsp");

		browser.click("id=deleteSuite");
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.BeginClass);
		browser.click("id=AddInstruction");

		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.BeginTest);
		browser.select("id=keyword", "label=" + valToSelect);
		browser.click("id=AddInstruction");
		
		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.OpenBrowser);
		browser.select("id=keyword", "label=" + valToSelect);
		browser.click("id=AddInstruction");

		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.VerifyObjectIsNotDisplayed);
		browser.select("id=keyword", "label=" + valToSelect);
		browser.click("id=AddInstruction");
		Thread.sleep(200);
		
		//Verify UI is correct:
		String value = getValue("div", "//div[@id='testSuite']");
		String expected = "Test Suite Named: MySuiteTest\n" +
							"testName\n" +
							"|UP| |DOWN| OpenBrowser: with Webpage of test Domain plus (OPTIONAL FIELD - " + 
							"if adding onto end of the domain): index.jsp\n" +
							"|UP| |DOWN| VerifyObjectIsNotDisplayed: with Verification Message of: User " +
							"should NOT see message [Error: invalid action]!, with XPath property to verify of: div[@id='myFBdata']";
		Assert.assertTrue("value should be [" + expected + "]\n\n\n value is [" + value + "]",
				expected.equals(value));
		
		browser.click("id=exportTestCase");
		
		//Now verify that the file was created and the contents are correct.
		Thread.sleep(1000);//Give time for server to create the file
		
		String input1 = "[" + Constants.KEYWORD_VALUES.get(KEYWORD_KEYS.OpenBrowser).get(0) + "]";
		String input2 = "[" + Constants.KEYWORD_VALUES.get(KEYWORD_KEYS.VerifyObjectIsNotDisplayed).get(0) + "," +
				Constants.KEYWORD_VALUES.get(KEYWORD_KEYS.VerifyObjectIsNotDisplayed).get(1) + "]";
		String expectedFile = 	"Keyword,Inputs\n" +
								KEYWORD_KEYS.OpenBrowser + "," + input1 + "\n" +
								KEYWORD_KEYS.VerifyObjectIsNotDisplayed + "," + input2 + "\n";
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
		
		Assert.assertTrue("File should match pre-defined file string\n" +
				"actual file:\n" + entireFile + "\n\nexpected:\n" + expectedFile, expectedFile.equals(entireFile));
	}//End Test Case

	
}//End Class