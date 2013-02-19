package com.example.domain;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import junit.framework.Assert;

import com.thoughtworks.selenium.DefaultSelenium;

public class Helper_manuallyCreated {

	private static String value;
	private static final int MAX_PAGE_LOAD_TIME_in_seconds = 10;
	private static String rootPath = "/home/fife/workspace/AppUnderTest/src/test/java/com/example/domain/";

	static public String getValue(DefaultSelenium browser, String objectType,
			String objectXPath) {
		if (objectType.equals("select")) {
			return browser.getSelectedLabel(objectXPath);
		} else if (objectType.equals("input")) {
			return browser.getValue(objectXPath);
		} else {
			return browser.getText(objectXPath);
		}
	}
	
	static public URL UpdateTestDomain(List<String> inputValues) throws MalformedURLException{
		String domain = inputValues.get(0);
		if(domain.startsWith("http")){
			return new URL(domain);
		}
		else if(domain.startsWith("www.")){
			return new URL("http://" + domain);
		}
		else {
			System.out.println("ERROR: Invalid new domain.  Domain [" + domain + "] can NOT be set as a new Project Context.");
			return null;
		}
	}

	static public void SelectDropdownValue(DefaultSelenium browser,
			List<String> inputValues) {
		browser.select("id=" + inputValues.get(0),
				"label=" + inputValues.get(1));
	}

	static public void OpenBrowser(DefaultSelenium browser, List<String> inputValues,
			URL deploymentURL) {
		browser.open(deploymentURL + inputValues.get(0));
	}

	static public void EnterTextInInput(DefaultSelenium browser,
			List<String> inputValues) {
		browser.type(inputValues.get(0), inputValues.get(1));
	}

	static public void VerifyObjectProperty(DefaultSelenium browser,
			List<String> inputValues) {
		value = getValue(browser, inputValues.get(1), inputValues.get(2));
		Assert.assertTrue(inputValues.get(0), inputValues.get(3).equals(value));
	}

	static public void ClickElement(DefaultSelenium browser, List<String> inputValues) {
		browser.click(inputValues.get(0) + "=" + inputValues.get(1));
		if(inputValues.get(2) != null){
			if(inputValues.get(2).equalsIgnoreCase("assigned_null") == false){
				//wait for the  page to load
				browser.waitForPageToLoad(Integer.toString(MAX_PAGE_LOAD_TIME_in_seconds * 1000));
				//browser.waitForFrameToLoad(inputValues.get(2), "3000");
			}
		}
	}

	static public void VerifyObjectIsDisplayed(DefaultSelenium browser,
			List<String> inputValues) {
		Assert.assertTrue(inputValues.get(0),
				browser.isElementPresent("xpath=//" + inputValues.get(1)));
	}

	static public void VerifyObjectIsNotDisplayed(DefaultSelenium browser,
			List<String> inputValues) {
		Assert.assertFalse(inputValues.get(0),
				browser.isElementPresent("xpath=//" + inputValues.get(1)));
	}
}