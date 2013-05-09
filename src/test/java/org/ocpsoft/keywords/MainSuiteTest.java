package org.ocpsoft.keywords;

import java.io.File;
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
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.keywords.Keyword.KEYWORD_PROCESS_TYPES;

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
		return ShrinkWrap.create(WebArchive.class, "keword-testing.war")
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
		
		browser.open(deploymentURL + "index.jsp");

		value = getValue("div", "//div[@id='testSuite']");
		Assert.assertTrue("Value should be blank",
			"".equals(value));
		
		browser.type("//input[@id='className']", "MySampleSuiteTest");
		
		browser.click("id=deleteSuite");

		value = getValue("div", "//div[@id='testSuite']");
//		String expected = "<font color='orange'>WARNING: You tried to delete a file that does not exist. File [" + Constants.ROOT_FILE_PATH + "MySampleSuiteTest.java] was already deleted.</font>";
		String expected = "WARNING: You tried to delete a file that does not exist. File [" + Constants.ROOT_FILE_PATH + "MySampleSuiteTest.java] was already deleted.";
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
	public void testUpdatingDomain() throws InterruptedException, MalformedURLException{//Begin Test Case
		/* This test covers UpdatingTestDomain keyword
		 * tests we can go to another URL and test that website
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
	public void testLinkClicks() throws InterruptedException {//Begin Test Case
		/* This test covers clicking links
		 * tests assigning values to Input Boxes.
		 * tests properties of Input Boxes. 
		 * tests VerifyObjectIsDisplayed - div.
		 * tests VerifyObjectIsNotDislayed - input.
		 */
		
		browser.open(deploymentURL + "index.jsp");

		browser.click("link=Click to go to myLink");
		Thread.sleep(200);//Allow the page to load
		
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
			
			for(int x = 0; x < Constants.KEYWORD_DESCRIPTIONS.get(keyword).size(); x ++) {
				value = getValue("div", "//div[@id='input" + (x+1) + "Desc']"); //UI starts counting divs and Inputs at 1
				expected = Constants.KEYWORD_DESCRIPTIONS.get(keyword).get(x);
				Assert.assertTrue("Description - value should be [" + expected + "]",
						expected.equals(value));
				
				value = getValue("input", "//input[@id='Input" + (x+1) + "']"); //UI starts counting divs and Inputs at 1
				expected = Constants.KEYWORD_VALUES.get(keyword).get(x);
				Assert.assertTrue("DefaultInputValue - value should be [" + expected + "]",
						expected.equals(value));
			}
			
			browser.click("id=AddInstruction");
			Thread.sleep(200);

			if(keyword.equals(Constants.KEYWORD_KEYS.BeginClass)){
				testSuiteName = Constants.KEYWORD_VALUES.get(keyword).get(0);
			}
			if(keyword.equals(Constants.KEYWORD_KEYS.BeginTest)){
				testCaseName = Constants.KEYWORD_VALUES.get(keyword).get(0);
				JavaClass testClass = null;
				testClass = Utility.javaClassExists(testClass, testSuiteName);
			}
			
			Keyword curKeyword = null;
//			curKeyword = KeywordFactory.createKeyword(keyword.toString());
			@SuppressWarnings("unchecked")
			List<Keyword> keywords = Iterators.asList(ServiceLoader.load(Keyword.class));
			for (Keyword key : keywords) {
				if(key.getShortName().equals(keyword)){
					curKeyword = key;
					break;
				}
			}
			if(curKeyword == null){
				Assert.assertTrue("Could not find correct keyword for: " + keyword.toString(), false);
			}
			if(curKeyword.getProcessType().equals(KEYWORD_PROCESS_TYPES.MethodCall)){
				//Validate the resulting line in the test file
				Member<JavaClass, ?> member = null;
				member = Utility.getMemberFromTestCaseName(testCaseName, testSuiteName);
				String[] steps = Utility.getStepsFromMethod(member);
				if(steps == null){
					Assert.assertTrue("Could not get any steps for test case to validate this added Instruction: " + keyword.toString(), false);
				}
				boolean validatedStep = false;
				String inputList = "";
				String additionalParams = "";
				for (String step : steps) {
					if(step.contains(keyword.toString())){
						//Validate this step
						//Example: Helper.OpenBrowser(browser,Arrays.asList("index.jsp"),deploymentURL);
						//ie: "Helper." + keyword.getShortName() + "(browser, " + printOutArrayListAsList(inputs) + keyword.getAdditionalInputParams() + ");";
						
						inputList = buildInputValueStringFromList(Constants.KEYWORD_VALUES.get(keyword));
						additionalParams = curKeyword.getAdditionalInputParams();
						value = step;
						expected = "Helper." + keyword.toString() + "(browser,Arrays.asList(" + inputList + ")" + additionalParams + ")";
						Assert.assertEquals("Keyword: "+ keyword.toString() + " failed adding correct step call to testCase.", expected, value);
						validatedStep = true;
						break;
					}
				}
				Assert.assertTrue("Need to find the right step to validate containing: " + keyword.toString(), validatedStep);
			}
		}
	}//End Test Case
	private String buildInputValueStringFromList(List<String> inputValueList){
		String returnVal = "";
		for (String input : inputValueList) {
			returnVal+="\"" + input + "\",";
		}
		
		return returnVal.substring(0, returnVal.length() - 1); //Remove last comma
	}
	
	@Test
	public void testBuildingASuiteAndViewing() throws InterruptedException {//Begin Test Case
		/* This test builds a new suite via the app.
		 * Once constructed, it will verify the testSuite div is displaying correctly on the UI.
		 */
		
		browser.open(deploymentURL + "index.jsp");

		browser.click("id=deleteSuite");
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.BeginClass);
		//The Default
		browser.click("id=AddInstruction");

		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.BeginTest);
		browser.select("id=keyword", "label=" + valToSelect);
		browser.click("id=AddInstruction");
		
		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.OpenBrowser);
		browser.select("id=keyword", "label=" + valToSelect);
		browser.click("id=AddInstruction");
		Thread.sleep(200);
		
		value = getValue("div", "//div[@id='testSuite']");
		String expected = "Test Suite Named: MySuiteTest\n" +
							"testName\n" +
							"|UP| |DOWN| OpenBrowser: with Webpage of test Domain plus (OPTIONAL FIELD - " + 
							"if adding onto end of the domain): index.jsp";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));
	}//End Test Case
	
	@Test
	public void testBuildingASuiteAndRunning() throws InterruptedException {//Begin Test Case
		/* This test builds a new suite via the app.
		 * Once constructed, it will click the [Run Tests] button to kick it off.
		 * 		NOT CURRENTLY KICKING OFF TEST SUITE.  Need to do something with port number, or other fix, see below.
		 */
		
		browser.open(deploymentURL + "index.jsp");

		browser.click("id=deleteSuite");
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.BeginClass);
		//The Default
		browser.click("id=AddInstruction");

		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.BeginTest);
		browser.select("id=keyword", "label=" + valToSelect);
		browser.click("id=AddInstruction");
		
		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.OpenBrowser);
		browser.select("id=keyword", "label=" + valToSelect);
		browser.click("id=AddInstruction");
		
		//browser.click("id=RunTests");
		//Can run tests, but can't have Selenium start up ANOTHER Selenium server, so build will Fail.
		//Assert.assertTrue("Build Success", browser.isTextPresent("BUILD SUCCESS"));
	}//End Test Case

	
}//End Class