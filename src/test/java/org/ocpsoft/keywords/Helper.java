package org.ocpsoft.keywords;

import java.util.ArrayList;

import com.thoughtworks.selenium.DefaultSelenium;

public class Helper {

	public void ClickElement(DefaultSelenium browser, ArrayList<String> inputValues) {
		browser.click(inputValues.get(0) + "=" + inputValues.get(1));
		if(inputValues.get(0).equals("link")){
			browser.waitForFrameToLoad(inputValues.get(2), "15000");
		}
	}

}
