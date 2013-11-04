package org.ocpsoft.individualTests;

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
public class VariablesTest {//Begin Class

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
	
	//NOTE: Framework does NOT support creating multiple variables of different types with the same name.
	//Once you use a name for any variable, you can NOT create any other variable with that name within the same test.

	@Test
	public void testCreatingNewStringVariableWithDefaultDoVerificationReAssignAndReVerify() throws InterruptedException {//Begin Test Case
		/* This test creates a new variable
		 * Performs onScreen Verification based on default value of variable (object Not present)
		 * Then ReAssigns the value of the variable
		 * Then rePerforms the onScreen verification again (object IS present)
		 * Runs the test and verifies we were successful
		 */

		buildTest();
		verifyCorrectTestStepsOnUI("testName");
		
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.AssignVariable);
	    browser.select("id=keyword", "label=" + valToSelect);
	    TestUtility.setSeleniumToIFrame(browser, "iFrameInputSelections");
	    browser.type("//input[@id='Input1']", "myVariable");
	    browser.type("//input[@id='Input2']", "\"RunTestsResults\"");
	    browser.click("id=AddInstruction");
	    TestUtility.setSeleniumBackToMainPage(browser);
	    Thread.sleep(100);
	    
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.VerifyObjectIsDisplayed);
	    browser.select("id=keyword", "label=" + valToSelect);
	    TestUtility.setSeleniumToIFrame(browser, "iFrameInputSelections");
	    browser.type("//input[@id='Input2']", "div[@id='||var||myVariable||var||']");
	    browser.click("id=AddInstruction");
	    TestUtility.setSeleniumBackToMainPage(browser);
	    Thread.sleep(100);
	    
	    verifyCorrectTestStepsOnUITest2("testName");
	    
		browser.click("id=RunTests");
		TestUtility.validateRunDidCompleteSuccessfully(browser, 10);
	}//End Test Case

	@Test
	public void testCreatingNewDoubleVariableWhenOneAlreadyExists() throws InterruptedException {//Begin Test Case
		/* This test creates a new Double variable
		 * Then tries to create the same variable again
		 * Should get an error condition
		 */

		TestUtility.OpenPageAndBeginNewSuiteAndTest(browser, deploymentURL);
		
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.OpenBrowser);
	    browser.select("id=keyword", "label=" + valToSelect);
	    Thread.sleep(100);
	    TestUtility.clickAddInstruction(browser);
	    Thread.sleep(100);
	    
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.CreateVariable);
	    browser.select("id=keyword", "label=" + valToSelect);
	    TestUtility.setSeleniumToIFrame(browser, "iFrameInputSelections");
	    browser.type("//input[@id='Input1']", "myDouble");
	    browser.type("//input[@id='Input2']", "Double");
	    browser.type("//input[@id='Input3']", "3.14159");
	    browser.click("id=AddInstruction");
	    TestUtility.setSeleniumBackToMainPage(browser);
	    Thread.sleep(100);
	    
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.CreateVariable);
	    browser.select("id=keyword", "label=" + valToSelect);
	    TestUtility.setSeleniumToIFrame(browser, "iFrameInputSelections");
	    browser.type("//input[@id='Input1']", "myDouble");
	    browser.type("//input[@id='Input2']", "Double");
	    browser.type("//input[@id='Input3']", "0.99999");
	    browser.click("id=AddInstruction");
	    TestUtility.setSeleniumBackToMainPage(browser);
	    Thread.sleep(100);
	    
	    String value = TestUtility.getValue(browser, "div", "//div[@id='testSuite']");
		String expected = "ERROR: Varibale already exists, can not create it again.";
		Assert.assertEquals(expected, value);
	}

	@Test
	public void testAssigningVariableThatDoesNotExists() throws InterruptedException {//Begin Test Case
		/* This test tries to assign a variable that was never created.
		 * Should get an error condition
		 */
		
		TestUtility.OpenPageAndBeginNewSuiteAndTest(browser, deploymentURL);
		
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.OpenBrowser);
	    browser.select("id=keyword", "label=" + valToSelect);
	    Thread.sleep(100);
	    TestUtility.clickAddInstruction(browser);
	    Thread.sleep(100);
	    
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.AssignVariable);
	    browser.select("id=keyword", "label=" + valToSelect);
	    TestUtility.setSeleniumToIFrame(browser, "iFrameInputSelections");
	    browser.type("//input[@id='Input1']", "myVar");
	    browser.type("//input[@id='Input2']", "This should error");
	    browser.click("id=AddInstruction");
	    TestUtility.setSeleniumBackToMainPage(browser);
	    Thread.sleep(100);
	    
	    String value = TestUtility.getValue(browser, "div", "//div[@id='testSuite']");
		String expected = "ERROR: Varibale does NOT exist, can not re-assign it.";
		Assert.assertEquals(expected, value);
	}
	
	
	private void buildTest() throws InterruptedException {
		TestUtility.OpenPageAndBeginNewSuiteAndTest(browser, deploymentURL);
	    
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.OpenBrowser);
	    browser.select("id=keyword", "label=" + valToSelect);
	    Thread.sleep(100);
	    TestUtility.clickAddInstruction(browser);
	    Thread.sleep(100);
	    
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.CreateVariable);
	    browser.select("id=keyword", "label=" + valToSelect);
	    TestUtility.setSeleniumToIFrame(browser, "iFrameInputSelections");
	    browser.type("//input[@id='Input1']", "myVariable");
	    browser.type("//input[@id='Input3']", "SomeWrongValue");
	    browser.click("id=AddInstruction");
	    TestUtility.setSeleniumBackToMainPage(browser);
	    Thread.sleep(100);
	    
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.VerifyObjectIsNotDisplayed);
	    browser.select("id=keyword", "label=" + valToSelect);
	    TestUtility.setSeleniumToIFrame(browser, "iFrameInputSelections");
	    browser.type("//input[@id='Input2']", "div[@id='||var||myVariable||var||']");
	    browser.click("id=AddInstruction");
	    TestUtility.setSeleniumBackToMainPage(browser);
	    Thread.sleep(100);
	}

	private void verifyCorrectTestStepsOnUI(String testCaseName) {
		String value = TestUtility.getValue(browser, "div", "//div[@id='testSuite']");
		String expected = "Test Suite Named: MySuiteTest\n" +
				testCaseName + "\n" +
				"|UP| |DOWN| AssignVariable: with name: deploymentURL, with value of: new URL(\"http://localhost:8080/framework/\")\n" +
				"|UP| |DOWN| OpenBrowser: with Webpage of test Domain plus (OPTIONAL FIELD - " + 
				"if adding onto end of the domain): index.jsp\n" +
				"|UP| |DOWN| CreateVariable: with name: myVariable, with value of: \"SomeWrongValue\"\n" +
				"|UP| |DOWN| VerifyObjectIsNotDisplayed: with Verification Message of: User should NOT see message [Error: invalid action]!, " +
				"with XPath property to verify of: div[@id='\" + myVariable + \"']";
		Assert.assertEquals(expected, value);
	}

	private void verifyCorrectTestStepsOnUITest2(String testCaseName) {
		String value = TestUtility.getValue(browser, "div", "//div[@id='testSuite']");
		String expected = "Test Suite Named: MySuiteTest\n" +
				testCaseName + "\n" +
				"|UP| |DOWN| AssignVariable: with name: deploymentURL, with value of: new URL(\"http://localhost:8080/framework/\")\n" +
				"|UP| |DOWN| OpenBrowser: with Webpage of test Domain plus (OPTIONAL FIELD - " + 
				"if adding onto end of the domain): index.jsp\n" +
				"|UP| |DOWN| CreateVariable: with name: myVariable" +
				", with value of: \"SomeWrongValue\"\n" +
				"|UP| |DOWN| VerifyObjectIsNotDisplayed: with Verification Message of: User should NOT see message [Error: invalid action]!, " +
				"with XPath property to verify of: div[@id='\" + myVariable + \"']\n" +
				"|UP| |DOWN| AssignVariable: with name: myVariable" +
				", with value of: \"RunTestsResults\"\n" +		
				"|UP| |DOWN| VerifyObjectIsDisplayed: with Verification Message of: User should be on index.jsp Page!, " +
				"with XPath property to verify of: div[@id='\" + myVariable + \"']";
		Assert.assertEquals(expected, value);
	}

}//End Class