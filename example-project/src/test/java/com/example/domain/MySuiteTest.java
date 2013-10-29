package com.example.domain;


import org.junit.runner.RunWith;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.container.test.api.Deployment;
import java.io.File;
import java.net.URL;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import com.thoughtworks.selenium.DefaultSelenium;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.test.api.ArquillianResource;
import java.util.Arrays;
import java.lang.InterruptedException;
import java.net.MalformedURLException;
import org.junit.Test;@RunWith(org.jboss.arquillian.junit.Arquillian.class) public class MySuiteTest {

	private static final String WEBAPP_SRC = "src/main/webapp";
	@Drone
	DefaultSelenium browser;
	@ArquillianResource
	URL deploymentURL;

	@Deployment(testable=false) static public WebArchive createDeployment(){return ShrinkWrap.create(WebArchive.class,"FBTutorialDemo.war").addAsWebInfResource(EmptyAsset.INSTANCE,"beans.xml");}

	@Test public void testName() throws InterruptedException,MalformedURLException{deploymentURL=new URL("http://localhost:8080/framework/");deploymentURL=new URL("http://localhost:8080/AppUnderTest/");Helper.OpenBrowser(browser,Arrays.asList("Login.html"),deploymentURL);String user="Craig";Actions.performLogin(deploymentURL,browser,user,"badPassword");Actions.verifyLogin(deploymentURL,browser,false,user);Actions.performLogin(deploymentURL,browser,user,"test123");Actions.verifyLogin(deploymentURL,browser,true,user);} }