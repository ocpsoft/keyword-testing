package com.example.domain;


import org.junit.runner.RunWith;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.container.test.api.Deployment;
import java.io.File;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import com.thoughtworks.selenium.DefaultSelenium;
import org.jboss.arquillian.drone.api.annotation.Drone;
import java.net.URL;
import org.jboss.arquillian.test.api.ArquillianResource;
import java.util.Arrays;
import java.lang.InterruptedException;
import org.junit.Test;@RunWith(Arquillian.class) public class MySuiteTest {

	private static final String WEBAPP_SRC = "src/main/webapp";
	@Drone
	DefaultSelenium browser;
	@ArquillianResource
	URL deploymentURL;

	@Deployment static public WebArchive createDeployment(){return ShrinkWrap.create(WebArchive.class,"FBTutorialDemo.war").addAsResource("META-INF/persistence.xml").addAsWebResource(new File(WEBAPP_SRC,"index.html")).addAsWebResource(new File(WEBAPP_SRC,"index2.html")).addAsWebResource(new File(WEBAPP_SRC,"myInfo.html")).addAsWebResource(new File(WEBAPP_SRC,"friendsInfo.html")).addAsWebInfResource(EmptyAsset.INSTANCE,"beans.xml");}

	@Test public void testName() throws InterruptedException{Helper.OpenBrowser(browser,Arrays.asList("index.jsp","assigned_null","assigned_null","assigned_null"),deploymentURL);} }