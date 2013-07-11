package org.oscposft.individualTests;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

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
	private final String actionsFilePath = Constants.APP_UNDER_TEST_ROOT_FILE_PATH + "Actions.java";

	@Test
	public void testBuildingATestExportingToActionsThenCreatingNewTestThatCallsTheAction() throws InterruptedException {//Begin Test Case
		/* This test builds a new suite and 1 new test via the app.
		 * Once constructed, it will click the [Export to Actions] button to make it an action called {ACTION_NAME}
		 * We will create a new test which calls the newly created action in it.
		 */

		//TODO: #DeploymentURL_HACK
		try {
			deploymentURL = new URL("http://localhost:8080/keword-testing/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		ParserExampleTest.removeClassFile(actionsFilePath);
		File actionsFile = new File(actionsFilePath);
		Assert.assertTrue("Actions File doesn't exist and we're starting fresh", !actionsFile.exists());
		
		buildTest();
		verifyCorrectTestStepsOnUI("testName");
		
		//Export the current Test as a new Action
		browser.type("//input[@id='exportToActionName']", ACTION_NAME);
		browser.click("id=exportToAction");
		Thread.sleep(600);//Give time for server to add all steps to Actions class as a new method
		
		verifyActionsFile();
		
		//Clear the entire class, then create a new test and try to call the newly created action
		browser.click("id=deleteSuite");
		createNewTest("myNewTest");
		callActionInTest(ACTION_NAME);
		
		//Verfiy the correct message on the UI for the import step
		String value = getValue("div", "//div[@id='testSuite']");
		String expected = "Test Suite Named: MySuiteTest\nmyNewTest\n|UP| |DOWN| Action Call: myAction";
		Assert.assertTrue("value should be [" + expected + "]\n\n\n value is [" + value + "]",
		    expected.equals(value));
		
	}//End Test Case
		
	private void callActionInTest(String actionName) throws InterruptedException {
	    String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.CallAction);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.type("//input[@id='Input1']", actionName);
	    browser.click("id=AddInstruction");
	    Thread.sleep(200);
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
							"deploymentURL = new URL(\"http://localhost:8080/keword-testing/\");" + 
							"Helper.OpenBrowser(browser, Arrays.asList(\"index.jsp\"), deploymentURL);" + 
							"Helper.EnterTextInInput(browser, Arrays.asList(" + 
									"\"//input[@id='className']\", \"Assigning Input Text\"));" + 
							"Helper.SelectDropdownValue(browser," + 
									"Arrays.asList(\"keyword\", \"Begin New Suite\"));";
		
		//TODO: Make this a better compare
		//HACK!!! I'm in a rush right now, just going to eliminate all spaces and compare.  Make it better later.
		expectedBody = expectedBody.replace(" ", "").replace("\n", "");
		String actualBody = exportedAction.getBody().replace(" ", "").replace("\n", "");
		Assert.assertEquals("Action method does not have correct body", expectedBody, actualBody);
	}

	private void buildTest() throws InterruptedException {
		browser.open(deploymentURL + "index.jsp");
		
		browser.click("id=BeginNewProject");
		
		browser.click("id=deleteSuite");
		createNewTest(null);
	    
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.OpenBrowser);
	    browser.select("id=keyword", "label=" + valToSelect);
	    Thread.sleep(100);
	    browser.click("id=AddInstruction");
	    Thread.sleep(200);
	    
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.EnterTextInInput);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.click("id=AddInstruction");
	    Thread.sleep(200);
	    
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.SelectDropdownValue);
	    browser.select("id=keyword", "label=" + valToSelect);
	    browser.click("id=AddInstruction");
	    Thread.sleep(200);
	}
	
	private void createNewTest(String testName) throws InterruptedException{
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.BeginClass);
		browser.select("id=keyword", "label=" + valToSelect);
	    browser.click("id=AddInstruction");
	    Thread.sleep(200);
	    
	    valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.BeginTest);
	    browser.select("id=keyword", "label=" + valToSelect);
	    if(testName != null && !testName.equals("")){
	    	browser.type("//input[@id='Input1']", testName);
	    }
	    browser.click("id=AddInstruction");
	    Thread.sleep(200);
	}
	
	private void verifyCorrectTestStepsOnUI(String testCaseName) {
		String value = getValue("div", "//div[@id='testSuite']");
		String expected = "Test Suite Named: MySuiteTest\n" +
				testCaseName + "\n" +
				"|UP| |DOWN| OpenBrowser: with Webpage of test Domain plus (OPTIONAL FIELD - " + 
				"if adding onto end of the domain): index.jsp\n" +
				"|UP| |DOWN| EnterTextInInput: with xPath of Input: //input[@id='className']" +
				", and Text to enter: Assigning Input Text\n" +
				"|UP| |DOWN| SelectDropdownValue: with Dropdown object's ID of: keyword, " +
				"selecting the value of: Begin New Suite";
//		Assert.assertTrue("value should be [" + expected + "]\n\n\n value is [" + value + "]",
//				expected.equals(value));
		Assert.assertEquals(expected, value);
	}
	

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
}//End Class