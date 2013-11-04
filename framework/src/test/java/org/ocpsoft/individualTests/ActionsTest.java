package org.ocpsoft.individualTests;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
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
public class ActionsTest {//Begin Class

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

	private final String ACTION_NAME = "myAction";
	private final String actionsFilePath = Constants.APP_UNDER_TEST__TEST_FILE_PATH + "Actions.java";

	@Test
	public void testBuildingATestExportingToActionsThenCreatingNewTestThatCallsTheAction() throws InterruptedException {//Begin Test Case
		/* This test builds a new suite and 1 new test via the app.
		 * Once constructed, it will click the [Export to Actions] button to make it an action called {ACTION_NAME}
		 * We will create a new test which calls the newly created action in it.
		 */
		
		ParserExampleTest.removeClassFile(actionsFilePath);
		File actionsFile = new File(actionsFilePath);
		Assert.assertTrue("Actions File doesn't exist and we're starting fresh", !actionsFile.exists());
		
		buildTest();
		verifyCorrectTestStepsOnUI("testName");
		
		//Export the current Test as a new Action
		exportToAction(ACTION_NAME);
		
		verifyActionsFile();
		
		//Clear the entire class, then create a new test and try to call the newly created action
		TestUtility.deleteTest(browser, null);
		TestUtility.openPageAndBeginNewTest(browser, deploymentURL, "myNewTest");
		callActionInTest(ACTION_NAME);
		
		//Verfiy the correct message on the UI for the import step
		String value = TestUtility.getValue(browser, "div", "//div[@id='testSuite']");
		String expected = "Test Suite Named: MySuiteTest\nmyNewTest\n|UP| |DOWN| AssignVariable: with name: deploymentURL, with value of: new URL(\"http://localhost:8080/framework/\")\n|UP| |DOWN| Action Call: myAction";
		Assert.assertEquals("value should be same", expected, value);
		
	}//End Test Case

	@Test
	public void testExportingNestedActionsThenCreatingNewTestThatCallsTheNestedAction() throws InterruptedException {//Begin Test Case
		/* This test builds a miniAction and a nestedMiniAction (which contains miniAction).
		 * We then create a new test that calls nestedMiniAction (which calls miniAction).
		 * Lastly, we will kick off the test through the [Run Tests] button and validate success.
		 */
		
		ParserExampleTest.removeClassFile(actionsFilePath);
		File actionsFile = new File(actionsFilePath);
		Assert.assertTrue("Actions File doesn't exist and we're starting fresh", !actionsFile.exists());
		
		TestUtility.OpenPageAndBeginNewSuiteAndTest(browser, deploymentURL);
		
		createMiniAction();
		createNestedMiniAction();
		
		TestUtility.createNewSuiteAndTest(browser, "testNestedActions");
		buildTest2();
		verifyCorrectTestStepsOnUITest2("testNestedActions");

		browser.click("id=RunTests");
		TestUtility.validateRunDidCompleteSuccessfully(browser, 15);
	}//End Test Case
	
	private void callActionInTest(String actionName) throws InterruptedException {
	    String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.CallAction);
	    browser.select("id=keyword", "label=" + valToSelect);
	    TestUtility.setSeleniumToIFrame(browser, "iFrameInputSelections");
	    browser.type("//input[@id='Input1']", actionName);
	    browser.click("id=AddInstruction");
	    TestUtility.setSeleniumBackToMainPage(browser);
	    Thread.sleep(100);
	}

	private void verifyActionsFile() {
		File actionsClassFile = new File(actionsFilePath);
		JavaClass actionsClass = null;
		try {
			actionsClass = (JavaClass) JavaParser.parse(actionsClassFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Assert.assertTrue("Error in Parsing actionsFile: " + e, false);
		}
		Method<JavaClass> exportedAction = actionsClass.getMethod(ACTION_NAME, "URL", "DefaultSelenium");
		
		Assert.assertTrue("Method does not exists in Actions.java: " + ACTION_NAME, exportedAction != null);
		Assert.assertTrue("Method does not have the correct signature", exportedAction.toSignature().equals("public myAction(URL, DefaultSelenium) : void"));
		
		String expectedBody = "" + 
							"deploymentURL=new URL(\"" + Constants.FRAMEWORK_LOCALHOST_URL + "\"); " + 
							"Helper.OpenBrowser(browser,Arrays.asList(\"index.jsp\"),deploymentURL); " + 
							"Helper.EnterTextInInput(browser,Arrays.asList(" + 
									"\"//input[@id='className']\",\"Assigning Input Text\")); " + 
							"Helper.SelectDropdownValue(browser," + 
									"Arrays.asList(\"keyword\",\"Begin New Suite\")); ";
		
		expectedBody = expectedBody.replace("\n", "");
		String actualBody = exportedAction.getBody().replace("\n", "");
		Assert.assertEquals("Action method does not have correct body", expectedBody, actualBody);
	}

	private void buildTest() throws InterruptedException {
		TestUtility.OpenPageAndBeginNewSuiteAndTest(browser, deploymentURL);
	    
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.OpenBrowser);
	    browser.select("id=keyword", "label=" + valToSelect);
	    Thread.sleep(100);
	    TestUtility.clickAddInstruction(browser);
	    Thread.sleep(100);
	    
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.EnterTextInInput);
	    browser.select("id=keyword", "label=" + valToSelect);
	    TestUtility.clickAddInstruction(browser);
	    Thread.sleep(100);
	    
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.SelectDropdownValue);
	    browser.select("id=keyword", "label=" + valToSelect);
	    TestUtility.clickAddInstruction(browser);
	    Thread.sleep(100);
	}

	private void buildTest2() throws InterruptedException {
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.OpenBrowser);
	    browser.select("id=keyword", "label=" + valToSelect);
	    Thread.sleep(100);
	    TestUtility.clickAddInstruction(browser);
	    Thread.sleep(100);
	    
	    callActionInTest("nestedMiniAction");
	}

	private void exportToAction(String actionName) throws InterruptedException {
		browser.type("//input[@id='exportToActionName']", actionName);
		browser.click("id=exportToAction");
		TestUtility.waitForCallbackToComplete(browser, "SUCCESS: New Action [");
	}
	
	private void createNestedMiniAction() throws InterruptedException {
		TestUtility.createNewSuiteAndTest(browser, null);
		
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.EnterTextInInput);
	    browser.select("id=keyword", "label=" + valToSelect);
	    TestUtility.clickAddInstruction(browser);
	    Thread.sleep(100);
	    
	    callActionInTest("miniAction");
	    
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.SelectDropdownValue);
	    browser.select("id=keyword", "label=" + valToSelect);
	    TestUtility.clickAddInstruction(browser);
	    Thread.sleep(100);
	    
	    exportToAction("nestedMiniAction");
	}

	private void createMiniAction() throws InterruptedException {
		TestUtility.createNewSuiteAndTest(browser, null);
		
	    String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.VerifyObjectIsDisplayed);
	    browser.select("id=keyword", "label=" + valToSelect);
	    TestUtility.clickAddInstruction(browser);
	    Thread.sleep(100);
	    
	    exportToAction("miniAction");
	}
	

	
	private void verifyCorrectTestStepsOnUI(String testCaseName) {
		String value = TestUtility.getValue(browser, "div", "//div[@id='testSuite']");
		String expected = "Test Suite Named: MySuiteTest\n" +
				testCaseName + "\n" +
				"|UP| |DOWN| AssignVariable: with name: deploymentURL, with value of: new URL(\"http://localhost:8080/framework/\")\n" +
				"|UP| |DOWN| OpenBrowser: with Webpage of test Domain plus (OPTIONAL FIELD - " + 
				"if adding onto end of the domain): index.jsp\n" +
				"|UP| |DOWN| EnterTextInInput: with xPath of Input: //input[@id='className']" +
				", and Text to enter: Assigning Input Text\n" +
				"|UP| |DOWN| SelectDropdownValue: with Dropdown object's ID of: keyword, " +
				"selecting the value of: Begin New Suite";
		Assert.assertEquals(expected, value);
	}

	private void verifyCorrectTestStepsOnUITest2(String testCaseName) {
		String value = TestUtility.getValue(browser, "div", "//div[@id='testSuite']");
		String expected = "Test Suite Named: MySuiteTest\n" +
				testCaseName + "\n" +
				"|UP| |DOWN| AssignVariable: with name: deploymentURL, with value of: new URL(\"http://localhost:8080/framework/\")\n" +
				"|UP| |DOWN| OpenBrowser: with Webpage of test Domain plus (OPTIONAL FIELD - " + 
				"if adding onto end of the domain): index.jsp\n" +
				"|UP| |DOWN| Action Call: nestedMiniAction";
		Assert.assertEquals(expected, value);
	}

}//End Class