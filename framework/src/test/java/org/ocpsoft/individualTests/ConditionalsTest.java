package org.ocpsoft.individualTests;

import java.io.File;
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
public class ConditionalsTest {//Begin Class

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

	private final String actionsFilePath = Constants.APP_UNDER_TEST__TEST_FILE_PATH + "Actions.java";
	
	@Test
	public void testBuildingATestWithAConditionalAndValidateUI() throws InterruptedException {//Begin Test Case
		/* This test builds a new suite and 1 new test via the app.
		 * That test will contain 2 conditional statements, 1 with only true case, and 1 with both true and false cases.
		 * Then verify the UI steps on the page from loading the Suite.
		 * Lastly, we perform a few move step |UP| and |DOWN| commands and verify output on each
		 */
		
		ParserExampleTest.removeClassFile(actionsFilePath);
		File actionsFile = new File(actionsFilePath);
		Assert.assertTrue("Actions File doesn't exist and we're starting fresh", !actionsFile.exists());
		
		TestUtility.OpenPageAndBeginNewSuiteAndTest(browser, deploymentURL);
		
		createFalseAction();
		createTrueAction();
		
		TestUtility.deleteTest(browser, null);
		
		buildTest();
		verifyCorrectTestStepsOnUITest("testName");
		
		browser.click("id=downLink_3");
		Thread.sleep(100);
		verifyTextInTestSuiteField("ERROR: Can not move the last step down.");
		browser.click("id=loadSuite");
		Thread.sleep(100);
		browser.click("id=upLink_1");
		Thread.sleep(100);
		verifyTextInTestSuiteField("ERROR: Can not move the first step up.");
		browser.click("id=loadSuite");
		Thread.sleep(100);
		
		browser.click("id=upLink_3");
		Thread.sleep(100);
		String expected = "Test Suite Named: MySuiteTest\n" +
				"testName\n" +
				"|UP| |DOWN| AssignVariable: with name: deploymentURL, with value of: new URL(\"http://localhost:8080/framework/\")\n" +
				"|UP| |DOWN| OpenBrowser: with Webpage of test Domain plus (OPTIONAL FIELD - " + 
				"if adding onto end of the domain): index.jsp\n" +
				"|UP| |DOWN| Conditional Branch: true/false condition to test: 0 == 1 - true case: trueAction - false case: falseAction\n" +
				"|UP| |DOWN| Conditional Branch: true/false condition to test: 0 == 0 - true case: trueAction - false case:";
		verifyTextInTestSuiteField(expected);

		browser.click("id=upLink_2");
		Thread.sleep(100);
		expected = "Test Suite Named: MySuiteTest\n" +
				"testName\n" +
				"|UP| |DOWN| AssignVariable: with name: deploymentURL, with value of: new URL(\"http://localhost:8080/framework/\")\n" +
				"|UP| |DOWN| Conditional Branch: true/false condition to test: 0 == 1 - true case: trueAction - false case: falseAction\n" +
				"|UP| |DOWN| OpenBrowser: with Webpage of test Domain plus (OPTIONAL FIELD - " + 
				"if adding onto end of the domain): index.jsp\n" +
				"|UP| |DOWN| Conditional Branch: true/false condition to test: 0 == 0 - true case: trueAction - false case:";
		verifyTextInTestSuiteField(expected);

		browser.click("id=upLink_3");
		Thread.sleep(100);
		expected = "Test Suite Named: MySuiteTest\n" +
				"testName\n" +
				"|UP| |DOWN| AssignVariable: with name: deploymentURL, with value of: new URL(\"http://localhost:8080/framework/\")\n" +
				"|UP| |DOWN| Conditional Branch: true/false condition to test: 0 == 1 - true case: trueAction - false case: falseAction\n" +
				"|UP| |DOWN| Conditional Branch: true/false condition to test: 0 == 0 - true case: trueAction - false case: \n" +
				"|UP| |DOWN| OpenBrowser: with Webpage of test Domain plus (OPTIONAL FIELD - " + 
				"if adding onto end of the domain): index.jsp";
		verifyTextInTestSuiteField(expected);

		browser.click("id=downLink_2");
		Thread.sleep(100);
		expected = "Test Suite Named: MySuiteTest\n" +
				"testName\n" +
				"|UP| |DOWN| AssignVariable: with name: deploymentURL, with value of: new URL(\"http://localhost:8080/framework/\")\n" +
				"|UP| |DOWN| Conditional Branch: true/false condition to test: 0 == 1 - true case: trueAction - false case: falseAction\n" +
				"|UP| |DOWN| OpenBrowser: with Webpage of test Domain plus (OPTIONAL FIELD - " + 
				"if adding onto end of the domain): index.jsp\n" +
				"|UP| |DOWN| Conditional Branch: true/false condition to test: 0 == 0 - true case: trueAction - false case:";
		verifyTextInTestSuiteField(expected);

		browser.click("id=downLink_1");
		Thread.sleep(100);
		expected = "Test Suite Named: MySuiteTest\n" +
				"testName\n" +
				"|UP| |DOWN| AssignVariable: with name: deploymentURL, with value of: new URL(\"http://localhost:8080/framework/\")\n" +
				"|UP| |DOWN| OpenBrowser: with Webpage of test Domain plus (OPTIONAL FIELD - " + 
				"if adding onto end of the domain): index.jsp\n" +
				"|UP| |DOWN| Conditional Branch: true/false condition to test: 0 == 1 - true case: trueAction - false case: falseAction\n" +
				"|UP| |DOWN| Conditional Branch: true/false condition to test: 0 == 0 - true case: trueAction - false case:";
		verifyTextInTestSuiteField(expected);
	}//End Test Case
	
	private void buildTest() throws InterruptedException {
		TestUtility.openPageAndBeginNewTest(browser, deploymentURL, null);
	    
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.OpenBrowser);
	    browser.select("id=keyword", "label=" + valToSelect);
	    Thread.sleep(100);
	    TestUtility.clickAddInstruction(browser);
	    Thread.sleep(100);
	    
	    //True condition only
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.ConditionalBranch);
	    browser.select("id=keyword", "label=" + valToSelect);
	    TestUtility.setSeleniumToIFrame(browser, "iFrameInputSelections");
	    browser.type("//input[@id='Input1']", "0==0");
	    browser.type("//input[@id='Input2']", "trueAction");
	    browser.type("//input[@id='Input3']", "");
	    browser.click("id=AddInstruction");
	    TestUtility.setSeleniumBackToMainPage(browser);
	    Thread.sleep(100);
	    
	    //True and false condition
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.ConditionalBranch);
	    browser.select("id=keyword", "label=" + valToSelect);
	    TestUtility.setSeleniumToIFrame(browser, "iFrameInputSelections");
	    browser.type("//input[@id='Input1']", "0==1");
	    browser.type("//input[@id='Input2']", "trueAction");
	    browser.type("//input[@id='Input3']", "falseAction");	    
	    browser.click("id=AddInstruction");
	    TestUtility.setSeleniumBackToMainPage(browser);
	    Thread.sleep(100);
	}

	private void exportToAction(String actionName) throws InterruptedException {
		browser.type("//input[@id='exportToActionName']", actionName);
		browser.click("id=exportToAction");
		TestUtility.waitForCallbackToComplete(browser, "SUCCESS: New Action [");
	}
	
	private void createTrueAction() throws InterruptedException {
		TestUtility.deleteTest(browser, null);
		TestUtility.openPageAndBeginNewTest(browser, deploymentURL, null);
		
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.EnterTextInInput);
	    browser.select("id=keyword", "label=" + valToSelect);
	    TestUtility.clickAddInstruction(browser);
	    Thread.sleep(100);
	    
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.SelectDropdownValue);
	    browser.select("id=keyword", "label=" + valToSelect);
	    TestUtility.clickAddInstruction(browser);
	    Thread.sleep(100);
	    
	    exportToAction("trueAction");
	}

	private void createFalseAction() throws InterruptedException {
		TestUtility.deleteTest(browser, null);
		TestUtility.openPageAndBeginNewTest(browser, deploymentURL, null);
		
	    String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.VerifyObjectIsDisplayed);
	    browser.select("id=keyword", "label=" + valToSelect);
	    TestUtility.clickAddInstruction(browser);
	    Thread.sleep(100);
	    
	    exportToAction("falseAction");
	}
	
	private void verifyCorrectTestStepsOnUITest(String testCaseName) {
		String expected = "Test Suite Named: MySuiteTest\n" +
				testCaseName + "\n" +
				"|UP| |DOWN| AssignVariable: with name: deploymentURL, with value of: new URL(\"http://localhost:8080/framework/\")\n" +
				"|UP| |DOWN| OpenBrowser: with Webpage of test Domain plus (OPTIONAL FIELD - " + 
				"if adding onto end of the domain): index.jsp\n" +
				"|UP| |DOWN| Conditional Branch: true/false condition to test: 0 == 0 - true case: trueAction - false case: \n" +
				"|UP| |DOWN| Conditional Branch: true/false condition to test: 0 == 1 - true case: trueAction - false case: falseAction";
		verifyTextInTestSuiteField(expected);
	}
	
	private void verifyTextInTestSuiteField(String expectedText){
		String value = TestUtility.getValue(browser, "div", "//div[@id='testSuite']");
		Assert.assertEquals(expectedText, value);
	}

}//End Class