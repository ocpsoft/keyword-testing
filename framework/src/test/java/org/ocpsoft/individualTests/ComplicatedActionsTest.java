package org.ocpsoft.individualTests;

import java.io.File;
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

import com.ocpsoft.utils.Constants;
import com.ocpsoft.utils.Constants.KEYWORD_KEYS;
import com.thoughtworks.selenium.DefaultSelenium;

@RunWith(Arquillian.class)
public class ComplicatedActionsTest {//Begin Class

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
	
	/*Purpose of this test class is to create complex tests with many actions using as many
	 * different keywords as possible.  Creating multiple variables and making some parameters
	 * and some not when exporting them to an action.  We want to simulate real-world conditions
	 * with this class with as much complexity as possible.
	 */

	private final String ACTION_TYPE_INPUT = "setInputValue";
	private final String ACTION_CLICK_LOGIN = "clickLogin";
	private final String ACTION_VERIFY_SUCCESSFUL_LOGIN = "verifySuccessfulLogin";
	private final String ACTION_VERIFY_UNSUCCESSFUL_LOGIN = "verifyUnsuccessLogin";
	private final String ACTION_VERIFY_LOGIN = "verifyLogin";
	private final String ACTION_PERFORM_LOGIN = "performLogin";
	private final String actionsFilePath = Constants.APP_UNDER_TEST__TEST_FILE_PATH + "Actions.java";

	@Test
	public void testBuildTestToValidateSuccessAndNonSuccessfulLogins() throws InterruptedException {//Begin Test Case
		/* This test builds a new suite and 1 new test via the app.
		 * It will build multiple actions and nest some of them.
		 * We will finally run the test and ensure Build Success.
		 */
		
		ParserExampleTest.removeClassFile(actionsFilePath);
		File actionsFile = new File(actionsFilePath);
		Assert.assertTrue("Actions File doesn't exist and we're starting fresh", !actionsFile.exists());
		
		TestUtility.beginNewSuiteAndTest(browser, deploymentURL);
		
		createTypeInputAction();
		createClickLoginAction();
		createPerformLoginAction();
		createVerifySuccessLoginAction();
		createVerifyUnsuccessLoginAction();
		createVerifyLoginAction();
		
		//Start from scratch and use the Actions we created to make a really good test.
		TestUtility.deleteTest(browser, null);
		TestUtility.beginNewTest(browser, deploymentURL, null);
		
		//Note, this test goes against Login.html of the example-project (not index.jsp)
		//TODO: #DeploymentURL_HACK - we must set the URL to the example-project manually through an assignment 
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.AssignVariable);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.type("//input[@id='Input1']", "deploymentURL");
	    browser.type("//input[@id='Input2']", "new URL(\"http://localhost:8080/AppUnderTest/\")");
	    browser.click("id=AddInstruction");
	    Thread.sleep(100);
	    
		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.OpenBrowser);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.type("//input[@id='Input1']", "Login.html"); //Note: Login.html
	    Thread.sleep(100);
	    browser.click("id=AddInstruction");
	    Thread.sleep(100);
	    
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.CreateVariable);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.type("//input[@id='Input1']", "user");
	    browser.type("//input[@id='Input2']", "String");
	    browser.type("//input[@id='Input3']", "Craig");
	    browser.click("id=AddInstruction");
	    Thread.sleep(100);
	    
	    //Prove that we can pass in new variables we create and they cascade down
	    //Also prove that we can pass direct values in, and those values get set to the variables in the calls
	    String userInput = Constants.VARIABLE_INPUT_MARKER + "user" + Constants.VARIABLE_INPUT_MARKER;
		callActionInTest(ACTION_PERFORM_LOGIN, userInput + ", \"badPassword\"");
		callActionInTest(ACTION_VERIFY_LOGIN, "false, " + userInput);
		callActionInTest(ACTION_PERFORM_LOGIN, userInput + ", \"test123\"");
		callActionInTest(ACTION_VERIFY_LOGIN, "true, " + userInput);
		
		//Verfiy that we can run the test and validate non-successful and successful login attempts.
		browser.click("id=RunTests");
		TestUtility.validateRunDidCompleteSuccessfully(browser, 20);
		
	}//End Test Case

	private void createTypeInputAction() throws InterruptedException {
		//Build Action for Entering a value into an input box
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.CreateVariable);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.type("//input[@id='Input1']", "fieldID");
	    browser.type("//input[@id='Input2']", "String");
	    browser.type("//input[@id='Input3']", "UserIDInput");
	    browser.click("id=AddInstruction");
	    Thread.sleep(100);
	    
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.CreateVariable);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.type("//input[@id='Input1']", "value");
	    browser.type("//input[@id='Input2']", "String");
	    browser.type("//input[@id='Input3']", "Craig");
	    browser.click("id=AddInstruction");
	    Thread.sleep(100);
	    
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.EnterTextInInput);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.type("//input[@id='Input1']", "//input[@id='" + Constants.VARIABLE_INPUT_MARKER + "fieldID" + Constants.VARIABLE_INPUT_MARKER +"']");
	    browser.type("//input[@id='Input2']", Constants.VARIABLE_INPUT_MARKER + "value" + Constants.VARIABLE_INPUT_MARKER);
	    browser.click("id=AddInstruction");
	    Thread.sleep(100);
		
		//Export the current Test as a new Action
	    ArrayList<String> ckboxes = new ArrayList<String>();
	    ckboxes.add("ckbx_String_fieldID");
	    ckboxes.add("ckbx_String_value");
		exportToAction(ACTION_TYPE_INPUT, ckboxes);
	}

	private void createClickLoginAction() throws InterruptedException {
		TestUtility.deleteTest(browser, null);
		TestUtility.beginNewTest(browser, deploymentURL, null);
		
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.ClickElement);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.type("//input[@id='Input1']", "id");
	    browser.type("//input[@id='Input2']", "LoginButton");
	    browser.type("//input[@id='Input3']", "");
	    browser.click("id=AddInstruction");
	    Thread.sleep(100);

		//Export the current Test as a new Action
	    ArrayList<String> ckboxes = new ArrayList<String>();
		exportToAction(ACTION_CLICK_LOGIN, ckboxes);
	}

	private void createPerformLoginAction() throws InterruptedException {
		TestUtility.deleteTest(browser, null);
		TestUtility.beginNewTest(browser, deploymentURL, null);
		
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.CreateVariable);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.type("//input[@id='Input1']", "userName");
	    browser.type("//input[@id='Input2']", "String");
	    browser.type("//input[@id='Input3']", "Craig");
	    browser.click("id=AddInstruction");
	    Thread.sleep(100);
	    
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.CreateVariable);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.type("//input[@id='Input1']", "password");
	    browser.type("//input[@id='Input2']", "String");
	    browser.type("//input[@id='Input3']", "test123");
	    browser.click("id=AddInstruction");
	    Thread.sleep(100);
	    
		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.CallAction);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.type("//input[@id='Input1']", ACTION_TYPE_INPUT);
	    browser.type("//input[@id='Input2']", "\"UserIDInput\", " + Constants.VARIABLE_INPUT_MARKER + "userName" + Constants.VARIABLE_INPUT_MARKER);
	    browser.click("id=AddInstruction");
	    Thread.sleep(100);
	    
		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.CallAction);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.type("//input[@id='Input1']", ACTION_TYPE_INPUT);
	    browser.type("//input[@id='Input2']", "\"passwordInput\", " + Constants.VARIABLE_INPUT_MARKER + "password" + Constants.VARIABLE_INPUT_MARKER);
	    browser.click("id=AddInstruction");
	    Thread.sleep(100);

		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.CallAction);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.type("//input[@id='Input1']", ACTION_CLICK_LOGIN);
	    browser.type("//input[@id='Input2']", "");
	    browser.click("id=AddInstruction");
	    Thread.sleep(100);

		//Export the current Test as a new Action
	    ArrayList<String> ckboxes = new ArrayList<String>();
	    ckboxes.add("ckbx_String_userName");
	    ckboxes.add("ckbx_String_password");
		exportToAction(ACTION_PERFORM_LOGIN, ckboxes);
	}

	private void createVerifySuccessLoginAction() throws InterruptedException {
		TestUtility.deleteTest(browser, null);
		TestUtility.beginNewTest(browser, deploymentURL, null);
		
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.CreateVariable);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.type("//input[@id='Input1']", "userName");
	    browser.type("//input[@id='Input2']", "String");
	    browser.type("//input[@id='Input3']", "Craig");
	    browser.click("id=AddInstruction");
	    Thread.sleep(100);
	    
		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.VerifyObjectProperty);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.type("//input[@id='Input1']", "Should have successful login.");
	    browser.type("//input[@id='Input2']", "other");
	    browser.type("//input[@id='Input3']", "//div[@id='loggedInStatus']");
	    browser.type("//input[@id='Input4']", "Welcome " + Constants.VARIABLE_INPUT_MARKER + "userName" + Constants.VARIABLE_INPUT_MARKER + "!");
	    browser.click("id=AddInstruction");
	    Thread.sleep(100);

		//Export the current Test as a new Action
	    ArrayList<String> ckboxes = new ArrayList<String>();
	    ckboxes.add("ckbx_String_userName");
		exportToAction(ACTION_VERIFY_SUCCESSFUL_LOGIN, ckboxes);
	}

	private void createVerifyUnsuccessLoginAction() throws InterruptedException {
		TestUtility.deleteTest(browser, null);
		TestUtility.beginNewTest(browser, deploymentURL, null);

		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.VerifyObjectProperty);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.type("//input[@id='Input1']", "Should have successful login.");
	    browser.type("//input[@id='Input2']", "other");
	    browser.type("//input[@id='Input3']", "//div[@id='loggedInStatus']");
	    browser.type("//input[@id='Input4']", "Invalid login attempt");
	    browser.click("id=AddInstruction");
	    Thread.sleep(100);

		//Export the current Test as a new Action
	    ArrayList<String> ckboxes = new ArrayList<String>();
		exportToAction(ACTION_VERIFY_UNSUCCESSFUL_LOGIN, ckboxes);
	}

	private void createVerifyLoginAction() throws InterruptedException {
		TestUtility.deleteTest(browser, null);
		TestUtility.beginNewTest(browser, deploymentURL, null);
		
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.CreateVariable);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.type("//input[@id='Input1']", "shouldBeSuccessful");
	    browser.type("//input[@id='Input2']", "boolean");
	    browser.type("//input[@id='Input3']", "true");
	    browser.click("id=AddInstruction");
	    Thread.sleep(100);
	    
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.CreateVariable);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.type("//input[@id='Input1']", "userName");
	    browser.type("//input[@id='Input2']", "String");
	    browser.type("//input[@id='Input3']", "");
	    browser.click("id=AddInstruction");
	    Thread.sleep(100);
	    
		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.ConditionalBranch);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.type("//input[@id='Input1']", Constants.VARIABLE_INPUT_MARKER + "shouldBeSuccessful" + Constants.VARIABLE_INPUT_MARKER);
	    //TODO: Note at present we have to pass the extraInputs that these Actions require in the same UI Input as the Action itslef
	    //TODO: And just seperate it with the Constants.ObjectDelimiter
	    browser.type("//input[@id='Input2']", ACTION_VERIFY_SUCCESSFUL_LOGIN + Constants.OBJECT_DELIMITER + 
	    		Constants.VARIABLE_INPUT_MARKER + "userName" + Constants.VARIABLE_INPUT_MARKER);
	    browser.type("//input[@id='Input3']", ACTION_VERIFY_UNSUCCESSFUL_LOGIN);
	    browser.click("id=AddInstruction");
	    Thread.sleep(100);

		//Export the current Test as a new Action
	    ArrayList<String> ckboxes = new ArrayList<String>();
	    ckboxes.add("ckbx_boolean_shouldBeSuccessful");
	    ckboxes.add("ckbx_String_userName");
		exportToAction(ACTION_VERIFY_LOGIN, ckboxes);
	}
	
	private void callActionInTest(String actionName, String additionalInputs) throws InterruptedException {
	    String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.CallAction);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.type("//input[@id='Input1']", actionName);
	    browser.type("//input[@id='Input2']", additionalInputs);
	    browser.click("id=AddInstruction");
	    Thread.sleep(100);
	}

	private void exportToAction(String actionName, ArrayList<String> iDsOfvarsToClick) throws InterruptedException {
		browser.type("//input[@id='exportToActionName']", actionName);
		browser.click("id=exportToAction");
		if(iDsOfvarsToClick.size() > 0){
			TestUtility.waitForCallbackToComplete(browser, "Here are the variables being created.");
			//Click each var checkbox we need to
			for (String id : iDsOfvarsToClick) {
				browser.click("id=" + id);
			}
			Thread.sleep(50);
			browser.click("id=continueExportToAction");
		}
		TestUtility.waitForCallbackToComplete(browser, "SUCCESS: New Action [");
	}
	
}//End Class