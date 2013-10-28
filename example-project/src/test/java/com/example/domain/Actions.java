package com.example.domain;


import java.util.Arrays;
import java.net.URL;
import com.thoughtworks.selenium.DefaultSelenium;
import java.net.MalformedURLException;public class Actions {

	public static void miniAction(URL deploymentURL,DefaultSelenium browser) throws MalformedURLException{deploymentURL=new URL("http://localhost:8080/framework/");Helper.VerifyObjectIsDisplayed(browser,Arrays.asList("User should be on index.jsp Page!","div[@id='RunTestsResults']"));}

	public static void nestedMiniAction(URL deploymentURL,DefaultSelenium browser) throws MalformedURLException{deploymentURL=new URL("http://localhost:8080/framework/");Actions.miniAction(deploymentURL,browser);Helper.SelectDropdownValue(browser,Arrays.asList("keyword","Begin New Suite"));} }