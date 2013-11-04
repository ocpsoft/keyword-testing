package org.ocpsoft.utils;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;

import com.ocpsoft.utils.Constants;
import com.ocpsoft.utils.Constants.KEYWORD_KEYS;
import com.thoughtworks.selenium.DefaultSelenium;

public class TestUtility {
	
	public static String getValue(DefaultSelenium browser, String objectType, String objectXPath){
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
	
	public static void OpenPageAndBeginNewSuiteAndTest(DefaultSelenium browser, URL deploymentURL) throws InterruptedException {
		//TODO: #DeploymentURL_HACK
		try {
			deploymentURL = new URL(Constants.FRAMEWORK_LOCALHOST_URL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
				
		browser.open(deploymentURL + "index.jsp");
		browser.click("id=deleteSuite");
		Thread.sleep(100);
		browser.click("id=BeginNewProject");
		Thread.sleep(100);
		
		createNewSuiteAndTest(browser, null);
	}

	public static void openPageAndBeginNewTest(DefaultSelenium browser, URL deploymentURL, String testName) throws InterruptedException {
		//TODO: #DeploymentURL_HACK
		try {
			deploymentURL = new URL(Constants.FRAMEWORK_LOCALHOST_URL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
				
		browser.open(deploymentURL + "index.jsp");
		createNewTest(browser, testName);
	}
	public static void createNewTest(DefaultSelenium browser, String testName) throws InterruptedException{
	    String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.BeginTest);
	    browser.select("id=keyword", "label=" + valToSelect);
	    setSeleniumToIFrame(browser, "iFrameInputSelections");
	    if(testName != null && !testName.equals("")){
	    	browser.type("//input[@id='Input1']", testName);
	    }
	    browser.click("id=AddInstruction");
	    setSeleniumBackToMainPage(browser);
	    waitForCallbackToComplete(browser, "|UP| |DOWN| AssignVariable: with name: deploymentURL");
	}
	
	public static void createNewSuiteAndTest(DefaultSelenium browser, String testName) throws InterruptedException{
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.BeginClass);
		browser.select("id=keyword", "label=" + valToSelect);
		clickAddInstruction(browser);
	    Thread.sleep(100);
	    
	    createNewTest(browser, testName);
	}

	public static void clickAddInstruction(DefaultSelenium browser) throws InterruptedException{
		setSeleniumToIFrame(browser, "iFrameInputSelections");
	    browser.click("id=AddInstruction");
	    setSeleniumBackToMainPage(browser);
	}
	public static void setSeleniumToIFrame(DefaultSelenium browser, String frameID) throws InterruptedException{
		browser.selectFrame("id=" + frameID);
		Thread.sleep(100);
	}
	public static void setSeleniumBackToMainPage(DefaultSelenium browser){
		browser.selectFrame("relative=top");
	}
	
	public static void deleteTest(DefaultSelenium browser, String testName){
		if(testName == null || testName.equals("")){
			browser.type("//input[@id='DeleteTestInput']", Constants.KEYWORD_VALUES.get(KEYWORD_KEYS.BeginTest).get(0));
		} else {
			browser.type("//input[@id='DeleteTestInput']", testName);
		}
		browser.click("id=deleteTest");
	}
	
	public static void waitUntilTestRunCompletes(DefaultSelenium browser, int timeOutSeconds) throws InterruptedException{
		int count = 0;
		if(timeOutSeconds <= 0){
			timeOutSeconds = 20;
		}
		while(browser.getElementHeight("id=RunTestsResultsImg").doubleValue() > 0 && count < timeOutSeconds * 20){
			Thread.sleep(50);
			count++;
		}
	}

	static int trialNumber = 0;
	public static boolean validateRunDidCompleteSuccessfully(DefaultSelenium browser, int timeOutSeconds) throws InterruptedException{
		waitUntilTestRunCompletes(browser, timeOutSeconds);
		if(!browser.isTextPresent("Build SUCCESSFUL!!!") && trialNumber < 1){
			//Try to run 1 more time, maybe there was a timing issue.
			browser.click("id=RunTests");
			trialNumber++;
			validateRunDidCompleteSuccessfully(browser, timeOutSeconds);
		}
		if(!browser.isTextPresent("Build SUCCESSFUL!!!"))
		{
			//Display what the issue is
			Assert.assertEquals("Build SUCCESSFUL!!!", getValue(browser, "div", "//div[@id='RunTestsResults']"));
			return false;
		}
		Assert.assertTrue("Build SUCCESSFUL!!!", true);
		trialNumber = 0;
		return true;
	}
	
	public static void waitForCallbackToComplete(DefaultSelenium browser, String browserText) throws InterruptedException{
		int count = 0;
		while(!browser.isTextPresent(browserText) && count < 2 * 20){
			Thread.sleep(50); //Wait up to 2 seconds to find the callback Text
			count++;
		}
	}

	public static void waitForCallbackToCompleteViaPropertyValue(DefaultSelenium browser, String objectType, String objectXPath, String neededValue) throws InterruptedException{
		int count = 0;
		while(!getValue(browser, objectType, objectXPath).equals(neededValue) && count < 2 * 20){
			Thread.sleep(50); //Wait up to 2 seconds to find the proper value of the object
			count++;
		}
	}
}
