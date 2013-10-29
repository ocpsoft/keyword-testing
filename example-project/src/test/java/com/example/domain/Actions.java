package com.example.domain;


import java.util.Arrays;
import java.net.URL;
import com.thoughtworks.selenium.DefaultSelenium;
import java.net.MalformedURLException;public class Actions {

	public static void setInputValue(URL deploymentURL,DefaultSelenium browser,String fieldID,String value) throws MalformedURLException{deploymentURL=new URL("http://localhost:8080/framework/");Helper.EnterTextInInput(browser,Arrays.asList("//input[@id='" + fieldID+"']",value));}

	public static void clickLogin(URL deploymentURL,DefaultSelenium browser) throws MalformedURLException{deploymentURL=new URL("http://localhost:8080/framework/");Helper.ClickElement(browser,Arrays.asList("id","LoginButton",""));}

	public static void performLogin(URL deploymentURL,DefaultSelenium browser,String userName,String password) throws MalformedURLException{deploymentURL=new URL("http://localhost:8080/framework/");Actions.setInputValue(deploymentURL,browser,"UserIDInput",userName);Actions.setInputValue(deploymentURL,browser,"passwordInput",password);Actions.clickLogin(deploymentURL,browser);}

	public static void verifySuccessfulLogin(URL deploymentURL,DefaultSelenium browser,String userName) throws MalformedURLException{deploymentURL=new URL("http://localhost:8080/framework/");Helper.VerifyObjectProperty(browser,Arrays.asList("Should have successful login.","other","//div[@id='loggedInStatus']","Welcome " + userName+"!"));}

	public static void verifyUnsuccessLogin(URL deploymentURL,DefaultSelenium browser) throws MalformedURLException{deploymentURL=new URL("http://localhost:8080/framework/");Helper.VerifyObjectProperty(browser,Arrays.asList("Should have successful login.","other","//div[@id='loggedInStatus']","Invalid login attempt"));}

	public static void verifyLogin(URL deploymentURL,DefaultSelenium browser,boolean shouldBeSuccessful,String userName) throws MalformedURLException{deploymentURL=new URL("http://localhost:8080/framework/");if (shouldBeSuccessful){Actions.verifySuccessfulLogin(deploymentURL,browser,userName);} else {Actions.verifyUnsuccessLogin(deploymentURL,browser);};} }