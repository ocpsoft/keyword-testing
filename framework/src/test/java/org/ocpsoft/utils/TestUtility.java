package org.ocpsoft.utils;

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
	
	public static void waitUntilTestRunCompletes(DefaultSelenium browser) throws InterruptedException{
		int count = 0;
		while(browser.getElementHeight("id=RunTestsResultsImg").doubleValue() > 0 && count < 1000){
			Thread.sleep(100);
			count++;
		}
	}
}
