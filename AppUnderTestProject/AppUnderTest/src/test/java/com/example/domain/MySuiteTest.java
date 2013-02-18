package com.example.domain;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.thoughtworks.selenium.DefaultSelenium;

@RunWith(Arquillian.class)
public class MySuiteTest {

	private static final String WEBAPP_SRC = "src/main/webapp";

	   @Deployment(testable = false) // testable = false to run as a client
		public static WebArchive createDeployment() {
		   MavenDependencyResolver resolver = DependencyResolvers.use(
				   MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");
			return ShrinkWrap.create(WebArchive.class)
				.addAsResource("META-INF/persistence.xml")
//				.addAsWebResource(new File(WEBAPP_SRC, "index.html"))
//				.addAsWebResource(new File(WEBAPP_SRC, "index2.html"))
//				.addAsWebResource(new File(WEBAPP_SRC, "myInfo.html"))
//				.addAsWebResource(new File(WEBAPP_SRC, "friendsInfo.html"))
							.addAsLibraries(resolver.artifacts("org.jboss.forge:forge-parser-java")
									.resolveAsFiles())
							.addAsLibraries(resolver.artifacts("org.seleniumhq.selenium:selenium-java")
									.resolveAsFiles())
							.addAsLibraries(resolver.artifacts("org.seleniumhq.selenium:selenium-server")
									.resolveAsFiles())									
							.addAsLibraries(resolver.artifacts("org.jboss.arquillian.extension:arquillian-drone-impl")
									.resolveAsFiles())									
							.addAsLibraries(resolver.artifacts("org.jboss.arquillian.extension:arquillian-drone-selenium")
									.resolveAsFiles())									
							.addAsLibraries(resolver.artifacts("org.jboss.arquillian.extension:arquillian-drone-selenium-server")
									.resolveAsFiles())
							.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
		}

	@Drone
	DefaultSelenium browser;

	@ArquillianResource
	URL deploymentURL;
	
	@Test
	public void testName() throws InterruptedException, MalformedURLException {
		
		deploymentURL = new URL("https://www.facebook.com");
//		browser = new DefaultSelenium("localhost" , 14444 , "*firefox" ,"http://");
		
		Helper.OpenBrowser(browser, Arrays.asList("www.facebook.com",
				"assigned_null", "assigned_null", "assigned_null"),
				deploymentURL);
		Helper.EnterTextInInput(browser, Arrays.asList("//input[@id='email']",
				"FBTestUser100@gmail.com", "assigned_null", "assigned_null"));
		Helper.EnterTextInInput(browser, Arrays.asList("//input[@id='pass']",
				"FBTestUser", "assigned_null", "assigned_null"));
		Helper.ClickElement(browser, Arrays.asList("id", "loginbutton",
				"https://www.facebook.com/?sk=welcome", "assigned_null"));
		Helper.VerifyObjectIsDisplayed(browser, Arrays.asList(
				"User should be logged in", "a[contains(text(), 'Bob Smith')]/@href",
				"assigned_null", "assigned_null"));
		Helper.ClickElement(browser, Arrays.asList("id", "userNavigationLabel",
				"assigned_null", "assigned_null"));
		Helper.ClickElement(browser, Arrays.asList("css",
				"input[type='submit']", "https://www.facebook.com", "assigned_null"));
		Helper.VerifyObjectIsDisplayed(browser, Arrays.asList(
				"User should be logged out", "input[@id='email']", "assigned_null",
				"assigned_null"));
	}
}