package com.example.domain;

import java.util.List;
import java.net.URL;

import com.ocpsoft.utils.Constants;
import com.thoughtworks.selenium.DefaultSelenium;
import org.junit.Assert;
import java.net.MalformedURLException;

public class Helper {

	private static String value;
	private static final int MAX_PAGE_LOAD_TIME_in_seconds = 10;
	private static String rootPath = Constants.ROOT_FILE_PATH;

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

	static public URL UpdateTestDomain(List<String> inputValues)
			throws MalformedURLException {
		String domain = inputValues.get(0);
		if (domain.startsWith("http")) {
			return new URL(domain);
		} else if (domain.startsWith("www.")) {
			return new URL("http://" + domain);
		} else {
			System.out.println("ERROR: Invalid new domain.  Domain [" + domain + "] can NOT be set as a new Project Context.");
			return null;
		}
	}

	static public void ClickElement(DefaultSelenium browser,
			List<String> inputValues) {
		browser.click(inputValues.get(0) + "=" + inputValues.get(1));
		if (inputValues.get(2) != null) {
			if (inputValues.get(2).equalsIgnoreCase("assigned_null") == false) {
				browser.waitForPageToLoad(Integer
						.toString(MAX_PAGE_LOAD_TIME_in_seconds * 1000));
			}
		}
	}

	static public void EnterTextInInput(DefaultSelenium browser,
			List<String> inputValues) {
		browser.type(inputValues.get(0), inputValues.get(1));
	}

	static public void VerifyObjectIsDisplayed(DefaultSelenium browser,
			List<String> inputValues) {
		Assert.assertTrue(inputValues.get(0),
				browser.isElementPresent("xpath=//" + inputValues.get(1)));
	}

	static public void VerifyObjectProperty(DefaultSelenium browser,
			List<String> inputValues) {
		value = getValue(browser, inputValues.get(1), inputValues.get(2));
		Assert.assertTrue(inputValues.get(0), inputValues.get(3).equals(value));
	}

	static public void VerifyObjectIsNotDisplayed(DefaultSelenium browser,
			List<String> inputValues) {
		Assert.assertFalse(inputValues.get(0),
				browser.isElementPresent("xpath=//" + inputValues.get(1)));
	}

	static public void OpenBrowser(DefaultSelenium browser,
			List<String> inputValues, URL deploymentURL) {
		browser.open(deploymentURL + inputValues.get(0));
	}

	static public void SelectDropdownValue(DefaultSelenium browser,
			List<String> inputValues) {
		browser.select("id=" + inputValues.get(0),
				"label=" + inputValues.get(1));
	}
}