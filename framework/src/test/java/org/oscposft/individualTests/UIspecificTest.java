package org.oscposft.individualTests;

import java.net.MalformedURLException;
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
import org.ocpsoft.utils.TestUtility;

import com.ocpsoft.utils.Constants;
import com.ocpsoft.utils.Constants.KEYWORD_KEYS;
import com.thoughtworks.selenium.DefaultSelenium;

@RunWith(Arquillian.class)
public class UIspecificTest {//Begin Class

	/*NOTE: For best results:
	 * 		Make sure server is already started.
	 * 		Run the file as a JUnit Test with AS7_REMOTE container.
	 */
	
	@Deployment(testable = false) // testable = false client mode
	public static WebArchive createDeployment() {
		return ShrinkWrap.create(WebArchive.class, "actions-testing.war")
						.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Drone
	DefaultSelenium browser;
	
	@ArquillianResource
	URL deploymentURL;

	@Test
	public void testCreateNewTestWillAlwaysDefaultTestNameDropdown() throws InterruptedException {//Begin Test Case
		/* This test create a new test and verify the TestName Dropdown apears and is populated.
		 * It will then create a couple of new tests and ensure the value in the dropdown changes automatically each time.
		 * Delete the tests one at a time and verify the UI each time.  Leave 1 test remaining.
		 * Delete the entire suite and verify UI.
		 */

		//TODO: #DeploymentURL_HACK
		try {
			deploymentURL = new URL("http://localhost:8080/keword-testing/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		browser.open(deploymentURL + "index.jsp");
		browser.click("id=BeginNewProject");
		browser.click("id=deleteSuite");
		
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.BeginClass);
		browser.select("id=keyword", "label=" + valToSelect);
	    browser.click("id=AddInstruction");
	    Thread.sleep(200);
	    
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.BeginTest);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.click("id=AddInstruction");
	    Thread.sleep(400);
	    
	    //Verify testName dropdown
	    String value = TestUtility.getValue(browser, "select", "//select[@id='testCaseName']");
		Assert.assertEquals("testName", value);
		
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.BeginTest);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.type("//input[@id='Input1']", "otherTest");
	    browser.click("id=AddInstruction");
	    Thread.sleep(200);
	    
	    //Verify testName dropdown
	    value = TestUtility.getValue(browser, "select", "//select[@id='testCaseName']");
		Assert.assertEquals("otherTest", value);

	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.BeginTest);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.type("//input[@id='Input1']", "otherTest4567");
	    browser.click("id=AddInstruction");
	    Thread.sleep(200);
	    
	    //Verify testName dropdown
	    value = TestUtility.getValue(browser, "select", "//select[@id='testCaseName']");
		Assert.assertEquals("otherTest4567", value);
		
		

	    browser.type("//input[@id='DeleteTestInput']", "otherTest4567");
	    browser.click("id=deleteTest");
	    Thread.sleep(200);
	    //Verify Status of deleted test
	    value = TestUtility.getValue(browser, "div", "//div[@id='testSuite']");
		Assert.assertEquals("SUCCESS: Test Case Deleted named [otherTest4567].", value);

	    browser.type("//input[@id='DeleteTestInput']", "testName");
	    browser.click("id=deleteTest");
	    Thread.sleep(200);
	    //Verify Status of deleted test
	    value = TestUtility.getValue(browser, "div", "//div[@id='testSuite']");
		Assert.assertEquals("SUCCESS: Test Case Deleted named [testName].", value);
		

	    browser.click("id=deleteSuite");
	    Thread.sleep(200);
	    //Verify Status of deleted test Suite
	    value = TestUtility.getValue(browser, "div", "//div[@id='testSuite']");
		Assert.assertEquals("Successfully deleted all tests within " + Constants.APP_UNDER_TEST__TEST_FILE_PATH + "MySuiteTest.java", value);
	}//End Test Case
	
}//End Class