package org.ocpsoft.keywords;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Method;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.keywords.Keyword.KEYWORD_PROCESS_TYPES;
import org.ocpsoft.utils.TestUtility;

import com.ocpsoft.utils.Constants;
import com.ocpsoft.utils.Constants.KEYWORD_KEYS;
import com.ocpsoft.utils.Utility;
import com.thoughtworks.selenium.DefaultSelenium;

@RunWith(Arquillian.class)
public class MainSuiteTest {//Begin Class

	/*NOTE: For best results:
	 * 		Start up the server.
	 * 		Do a full publish of the Keyword Application
	 * 		Run the file as a JUnit Test with AS7_REMOTE container.
	 * 
	 * 		NOTE: Some tests are currently requiring a Publish
	 * 			  for EVERY/ANY run of the tests.
	 */
	
   @Deployment(testable = false) // testable = false to run as a client
	public static WebArchive createDeployment() {
		return ShrinkWrap.create(WebArchive.class, "mainTestSuite.war")
						.addClasses(Constants.class, Keyword.class, KeywordFactory.class)
						.addPackage("org.ocpsoft.keywords")
						.addPackage("org.ocpsoft.utils")
						.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
	}

@Drone
DefaultSelenium browser;

@ArquillianResource
URL deploymentURL;

@Inject
Instance<Keyword> keywords;

/*HELPER Methods and vars for other Keywords*/
private String value = "";
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

	@Test
	public void testButtonClicks() throws InterruptedException {//Begin Test Case
		/* This test covers the [Clear Divs] and [Delete Suite] buttons 
		 * Also tests Text (TODO:needs to do innerHTML) of Div elements.
		 */
		
		//TODO: #DeploymentURL_HACK
		try {
			deploymentURL = new URL(Constants.FRAMEWORK_LOCALHOST_URL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		browser.open(deploymentURL + "index.jsp");

		value = getValue("div", "//div[@id='testSuite']");
		Assert.assertTrue("Value should be blank",
			"".equals(value));
		
		browser.type("//input[@id='className']", "MySampleSuiteTest");
		
		browser.click("id=deleteSuite");

		value = getValue("div", "//div[@id='testSuite']");
//		String expected = "<font color='orange'>WARNING: You tried to delete a file that does not exist. File [" + Constants.ROOT_FILE_PATH + "MySampleSuiteTest.java] was already deleted.</font>";
		String expected = "WARNING: You tried to delete a file that does not exist. File [" + Constants.APP_UNDER_TEST__TEST_FILE_PATH + "MySampleSuiteTest.java] was already deleted.";
		System.out.println("Check 1 - Value = [" + value + "]");
		Assert.assertTrue("value should be [Warning] text",
				expected.equals(value));
		
		browser.click("id=clearDivs");
		
		value = getValue("div", "//div[@id='testSuite']");
		expected = "";
		Assert.assertTrue("value should be [blank]",
				expected.equals(value));		
	}//End Test Case

	@Test
	public void testOutsideWebsites() throws InterruptedException, MalformedURLException{//Begin Test Case
		/* This test ensures we can test outside 
		 * websites by updating the deploymentURL
		 * uses www.facebook.com
		 */
		
		URL placeholderDeploymentURL = deploymentURL;
		
		//Facebook right now is returning a bad security cookie to server for some reason, use another site for now.
//		deploymentURL = new URL("https://www.facebook.com");
//		browser.open(deploymentURL.toString());
//		Assert.assertTrue("User should be on Facebook.com",
//				browser.isElementPresent("xpath=//input[@id='email']"));
		
		deploymentURL = new URL("http://www.msn.com");
		browser.open(deploymentURL.toString());
		Assert.assertTrue("User should be on Facebook.com",
				browser.isElementPresent("xpath=//input[@id='q']"));
		
		deploymentURL = placeholderDeploymentURL;
	}//End Test Case
	
	@Test
	public void testUpdateTestDomainKeyword() throws InterruptedException {//Begin Test Case
		/* This test validates UpdateTestDomain Keyword call gets added correctly 
		 * Also verifies that we add this keyword's addThrowsToTest once and only once
		 * No matter how many times we add UpdateTestDomain as a step in a test.
		 */

		TestUtility.OpenPageAndBeginNewSuiteAndTest(browser, deploymentURL);
		
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.AssignVariable);
	    browser.select("id=keyword", "label=" + valToSelect);
	    TestUtility.setSeleniumToIFrame(browser, "iFrameInputSelections");
	    browser.type("//input[@id='Input1']", "deploymentURL");
	    browser.type("//input[@id='Input3']", "\"http://www.facebook.com\"");
	    browser.click("id=AddInstruction");
	    TestUtility.setSeleniumBackToMainPage(browser);
	    Thread.sleep(100);
		
		String testSuiteName = Constants.KEYWORD_VALUES.get(KEYWORD_KEYS.BeginClass).get(0);
		String testCaseName = Constants.KEYWORD_VALUES.get(KEYWORD_KEYS.BeginTest).get(0);
		
		//Validate we added the throws MalformedURLException to the test
		JavaClass testClass = null;
		testClass = Utility.getJavaClass(testSuiteName);
		Method<JavaClass> testMethod = testClass.getMethod(testCaseName);
		Assert.assertTrue("Test should now throw MalformedURLException", testMethod.getThrownExceptions().contains("MalformedURLException"));
		Assert.assertTrue("Test should have 2 total Exceptions", testMethod.getThrownExceptions().size() == 2);
		
		//Add another Update, make sure that line is in there correctly too, and make sure we only have 1 Malformed Exception on the testcase (not 2).
		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.AssignVariable);
	    browser.select("id=keyword", "label=" + valToSelect);
	    TestUtility.setSeleniumToIFrame(browser, "iFrameInputSelections");
	    browser.type("//input[@id='Input1']", "deploymentURL");
	    browser.type("//input[@id='Input3']", "\"http://www.cnn.com\"");
	    browser.click("id=AddInstruction");
	    TestUtility.setSeleniumBackToMainPage(browser);
	    Thread.sleep(100);
		
		testClass = Utility.getJavaClass(testSuiteName);
		testMethod = testClass.getMethod(testCaseName);
		int size = testMethod.getThrownExceptions().size();
		Assert.assertTrue("Test should still only have 2 total Exceptions, we have: " + size + ", " + testMethod.getThrownExceptions(), size == 2);
	}//End Test Case
	
	@Test
	public void testLinkClicks() throws InterruptedException {//Begin Test Case
		/* This test covers clicking links
		 * tests assigning values to Input Boxes.
		 * tests properties of Input Boxes. 
		 * tests VerifyObjectIsDisplayed - div.
		 * tests VerifyObjectIsNotDislayed - input.
		 */

		//TODO: #DeploymentURL_HACK
		try {
			deploymentURL = new URL(Constants.FRAMEWORK_LOCALHOST_URL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		browser.open(deploymentURL + "index.jsp");

		browser.click("link=Click to go to myLink");
		Thread.sleep(100); //Selenium Error's if it can't access document if HTML page insn't fully loaded
		TestUtility.waitForCallbackToComplete(browser, "Click to go to Index");
		
		value = getValue("input", "//input[@id='myInput']");
		String expected = "";
		Assert.assertTrue("value should be [blank]",
				expected.equals(value));
		
		browser.type("//input[@id='myInput']","Text to Enter");
		
		value = getValue("input", "//input[@id='myInput']");
		expected = "Text to Enter";
		Assert.assertTrue("value should be [blank]",
				expected.equals(value));	
		
		browser.click("link=Click to go to Index");
		Thread.sleep(100); //Selenium Error's if it can't access document if HTML page insn't fully loaded
		TestUtility.waitForCallbackToComplete(browser, "Click to go to myLink");
		
		Assert.assertTrue("User should be on Index Page!",
			           browser.isElementPresent("xpath=//div[@id='entireInput1']"));
		
		Assert.assertFalse("User should be on Index Page!",
		           browser.isElementPresent("xpath=//input[@id='myInput']"));
	}//End Test Case

	@Test
	public void testAllKeywordDropdownDescsValuesAndResultingTestFileLines() throws InterruptedException {//Begin Test Case
		/* This test covers selecting all keyword dropdown values
		 * tests properties of all input descriptions of all keywords.
		 * tests properties of all input values of all keywords.
		 * tests resulting lines in the new arquillian test created from adding the keyword with default values.
		 */

		//TODO: #DeploymentURL_HACK
		try {
			deploymentURL = new URL(Constants.FRAMEWORK_LOCALHOST_URL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		String valToSelect = "";
		String expected = "";
		browser.open(deploymentURL + "index.jsp");
		String testSuiteName = "";
		String testCaseName = "";
		browser.click("id=deleteSuite"); //Make sure we start fresh
		
		for (KEYWORD_KEYS keyword : Constants.KEYWORD_KEYS.values()) {
			valToSelect = Constants.KEYWORD_LONGNAMES.get(keyword);
			browser.select("id=keyword", "label=" + valToSelect);
			
			value = getValue("select", "//select[@id='keyword']");
			expected = valToSelect;
			Assert.assertTrue("Keyword - value should be [" + expected + "]",
					expected.equals(value));
			
			TestUtility.setSeleniumToIFrame(browser, "iFrameInputSelections");
			for(int x = 0; x < Constants.KEYWORD_DESCRIPTIONS.get(keyword).size(); x ++) {
				value = getValue("div", "//div[@id='input" + (x+1) + "Desc']"); //UI starts counting divs and Inputs at 1
				expected = Constants.KEYWORD_DESCRIPTIONS.get(keyword).get(x);
				Assert.assertTrue("Description - value should be [" + expected + "]",
						expected.equals(value));
				
				value = getValue("input", "//input[@id='Input" + (x+1) + "']"); //UI starts counting divs and Inputs at 1
				expected = Constants.resolveValue(Constants.KEYWORD_VALUES.get(keyword).get(x)); //Note we do this manually in the index.jsp file
				Assert.assertEquals("DefaultInputValue incorrect for Keyword: " + keyword + ", Input: " + x, expected, value);
			}
			
			browser.click("id=AddInstruction");
			TestUtility.setSeleniumBackToMainPage(browser);
			Thread.sleep(100);

			if(keyword.equals(Constants.KEYWORD_KEYS.BeginClass)){
				testSuiteName = Constants.KEYWORD_VALUES.get(keyword).get(0);
			}
			if(keyword.equals(Constants.KEYWORD_KEYS.BeginTest)){
				testCaseName = Constants.KEYWORD_VALUES.get(keyword).get(0);
			}
			
			Keyword curKeyword = getKeyword(keyword);
			if(curKeyword == null){
				Assert.assertTrue("Could not find correct keyword for: " + keyword.toString(), false);
			}
			if(curKeyword.processType().equals(KEYWORD_PROCESS_TYPES.MethodCall)){
				//Validate the resulting line in the test file
				validateTestCaseContainsCorrectStep(keyword, testCaseName, testSuiteName, null);
			}
		}
	}//End Test Case
	private Keyword getKeyword(KEYWORD_KEYS keyword) {
		Keyword curKeyword = null;
		@SuppressWarnings("unchecked")
		List<Keyword> keywords = Iterators.asList(ServiceLoader.load(Keyword.class));
		for (Keyword key : keywords) {
			if(key.shortName().equals(keyword)){
				curKeyword = key;
				break;
			}
		}
		return curKeyword;
	}
	private Keyword getKeyword(String keyword) {
		Keyword curKeyword = null;
		@SuppressWarnings("unchecked")
		List<Keyword> keywords = Iterators.asList(ServiceLoader.load(Keyword.class));
		for (Keyword key : keywords) {
			if(key.shortName().toString().equals(keyword)){
				curKeyword = key;
				break;
			}
		}
		return curKeyword;
	}
	private void validateTestCaseContainsCorrectStep(KEYWORD_KEYS keywordKey, String testCaseName, String testSuiteName, String optionallyStepMustContainThisString) {
		Member<JavaClass, ?> member = null;
		member = Utility.getMemberFromTestCaseName(testCaseName, testSuiteName);
		String[] steps = Utility.getStepsFromMethod(member);
		if(steps == null){
			Assert.assertTrue("Could not get any steps for test case to validate this added Instruction: " + keywordKey.toString(), false);
		}
		boolean validatedStep = false;
		String inputList = "";
		String additionalParams = "";
		for (String step : steps) {
			if(isStepWeAreLookingFor(step, keywordKey.toString(), optionallyStepMustContainThisString)){
				//Validate this default step
				//Example: Helper.OpenBrowser(browser,Arrays.asList("index.jsp"),deploymentURL);
				//ie: "Helper." + keyword.getShortName() + "(browser, " + printOutArrayListAsList(inputs) + keyword.getAdditionalInputParams() + ");";
				//NOTE: For Variable keywords, we'll have to create them a different way
				String actualValue = step;
				
				inputList = buildInputValueStringFromList(Constants.KEYWORD_VALUES.get(keywordKey));
				Keyword keyword = getKeyword(keywordKey);
				additionalParams = keyword.additionalInputParams();
				String expected = "";
				if(keyword instanceof VariableKeywordInterface){
					expected = ((VariableKeywordInterface) keyword).determineNewLine(
								Utility.convertStringToArrayListString(inputList, ","));
				} else {
					expected = "Helper." + keywordKey.toString() + "(browser,Arrays.asList(" + inputList + ")" + additionalParams + ")";
				}
				Assert.assertEquals("Keyword: "+ keywordKey.toString() + " failed adding correct step call to testCase.", expected, actualValue);
				validatedStep = true;
				break;
			}
		}
		Assert.assertTrue("Need to find the right step to validate containing: " + keywordKey.toString(), validatedStep);
	}
	private boolean isStepWeAreLookingFor(String step, String keyword, String optionallyStepMustContainThisString) {
		boolean containsKeyword = stepDoesContainKeyword(step, keyword);
		boolean containsPhrase;
		if(optionallyStepMustContainThisString == null){
			containsPhrase = true;
		}
		else {
			containsPhrase = step.contains(optionallyStepMustContainThisString);
		}
		return (containsKeyword && containsPhrase);
	}
	private boolean stepDoesContainKeyword(String step, String keyword) {
		Keyword actualKeyword = getKeyword(keyword);
		if(actualKeyword instanceof VariableKeywordInterface){
			if(actualKeyword instanceof CreateVariableKeyword){
				return Utility.isVariableCreationStep(step);
			} else if (actualKeyword instanceof AssignVariableKeyword) {
				return Utility.isVariableAssignmentORcreationStep(step) && !Utility.isVariableCreationStep(step);
			} else {
				System.err.println("Unknown instance of VariableKeywordInterface");
				return false;
			}
		}
		return step.contains(keyword);
	}

	private String buildInputValueStringFromList(List<String> inputValueList){
		String returnVal = "";
		for (String input : inputValueList) {
			returnVal+="\"" + input + "\"" + Constants.LIST_DELIMITER;
		}
		
		returnVal = returnVal.replace(Constants.LIST_DELIMITER, ",");
		return returnVal.substring(0, returnVal.length() - 1); //Remove last comma
	}
	
	@Test
	public void testBuildingASuiteAndViewingAndRunning() throws InterruptedException {//Begin Test Case
		/* This test builds a new suite via the app.
		 * Once constructed, it will verify the testSuite div is displaying correctly on the UI.
		 * Finally, it will click the [Run Tests] button to kick it off.
		 * We then test to make sure the test we created in example-project is run successfully.
		 */
		
		TestUtility.OpenPageAndBeginNewSuiteAndTest(browser, deploymentURL);
		
		String valToSelect;
		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.OpenBrowser);
		browser.select("id=keyword", "label=" + valToSelect);
		TestUtility.clickAddInstruction(browser);
		Thread.sleep(100);
		
		value = getValue("div", "//div[@id='testSuite']");
		String expected = "Test Suite Named: MySuiteTest\n" +
							"testName\n" +
							"|UP| |DOWN| AssignVariable: with name: deploymentURL, with value of: new URL(\"http://localhost:8080/framework/\")\n" +
							"|UP| |DOWN| OpenBrowser: with Webpage of test Domain plus (OPTIONAL FIELD - " + 
							"if adding onto end of the domain): index.jsp";
		Assert.assertEquals(expected, value);
		
		browser.click("id=RunTests");
		TestUtility.validateRunDidCompleteSuccessfully(browser, 12);
	}//End Test Case

}//End Class