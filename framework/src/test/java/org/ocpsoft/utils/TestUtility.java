package org.ocpsoft.utils;

import org.junit.Assert;

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
