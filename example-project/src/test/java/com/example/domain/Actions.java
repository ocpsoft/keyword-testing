package com.example.domain;

import java.util.Arrays;
import java.net.URL;
import com.thoughtworks.selenium.DefaultSelenium;
import java.net.MalformedURLException;

public class Actions {

	public static void myAction(URL deploymentURL, DefaultSelenium browser)
			throws MalformedURLException {
		deploymentURL = new URL("http://localhost:8080/framework/");
		Helper.OpenBrowser(browser, Arrays.asList("index.jsp"), deploymentURL);
		Helper.EnterTextInInput(browser, Arrays.asList(
				"//input[@id='className']", "Assigning Input Text"));
		Helper.SelectDropdownValue(browser,
				Arrays.asList("keyword", "Begin New Suite"));
	}
}