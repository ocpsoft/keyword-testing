package org.ocpsoft.keywords;

import java.io.File;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.services.MyWebService;

import com.ocpsoft.utils.Constants;
import com.ocpsoft.utils.Constants.KEYWORD_KEYS;
import com.thoughtworks.selenium.DefaultSelenium;

@RunWith(Arquillian.class)
public class MySuiteTest {//Begin Class

   private static final String WEBAPP_SRC = "src/main/webapp";
   
   @Deployment(testable = false) // testable = false to run as a client
	public static WebArchive createDeployment() {
	   MavenDependencyResolver resolver = DependencyResolvers.use(
			   MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");
		return ShrinkWrap.create(WebArchive.class, "KeywordApp.war")
						.addClasses(MyWebService.class, Constants.class, Keyword.class, KeywordFactory.class)
						.addAsResource("META-INF/persistence.xml")
						.addAsWebResource(new File(WEBAPP_SRC, "index.jsp"))
						.addAsWebResource(new File(WEBAPP_SRC, "myLink.html"))
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

/*HELPER Methods and vars for other Keywords*/
private String value = "";
private String getValue(String objectType, String objectXPath){
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

	@Test
	public void testButtonClicks() throws InterruptedException {//Begin Test Case
		/* This test covers the [Clear Divs] and [Delete Suite] buttons 
		 * Also tests Text (TODO:needs to do innerHTML) of Div elements.
		 */
		
		browser.open(deploymentURL + "index.jsp");

		value = getValue("div", "//div[@id='testSuite']");
		Assert.assertTrue("Value should be blank",
			"".equals(value));
		
		browser.type("//input[@id='className']", "MySampleSuiteTest");
		
		browser.click("id=deleteSuite");

		value = getValue("div", "//div[@id='testSuite']");
//		String expected = "<font color='orange'>WARNING: You tried to delete a file that does not exist. File [/home/fife/workspace/AppUnderTest/src/test/java/com/example/domain/MySampleSuiteTest.java] was already deleted.</font>";
		String expected = "WARNING: You tried to delete a file that does not exist. File [/home/fife/workspace/AppUnderTest/src/test/java/com/example/domain/MySampleSuiteTest.java] was already deleted.";
		System.out.println("Check 1 - Value = [" + value + "]");
		Assert.assertTrue("value should be [Warning] text",
				expected.equals(value));
		
		browser.click("id=clearDivs");
		
		value = getValue("div", "//div[@id='testSuite']");
		expected = "";
		Assert.assertTrue("value should be [blank]",
				expected.equals(value));		
	}//End Test Case


	@Test
	public void testLinkClicks() throws InterruptedException {//Begin Test Case
		/* This test covers clicking links
		 * tests assigning values to Input Boxes.
		 * tests properties of Input Boxes. 
		 * tests VerifyObjectIsDisplayed - div.
		 * tests VerifyObjectIsNotDislayed - input.
		 */
		
		browser.open(deploymentURL + "index.jsp");

		browser.click("link=Click to go to myLink");

		value = getValue("input", "//input[@id='myInput']");
		String expected = "";
		Assert.assertTrue("value should be [blank]",
				expected.equals(value));
		
		browser.type("//input[@id='myInput']","Text to Enter");
		
		value = getValue("input", "//input[@id='myInput']");
		expected = "Text to Enter";
		Assert.assertTrue("value should be [blank]",
				expected.equals(value));	
		
		browser.click("link=Click to go to Index");
		
		Assert.assertTrue("User should be on Index Page!",
			           browser.isElementPresent("xpath=//div[@id='entireInput1']"));
		
		Assert.assertFalse("User should be on Index Page!",
		           browser.isElementPresent("xpath=//input[@id='myInput']"));
	}//End Test Case


	@Test
	public void testKeywordDropdownInputs() throws InterruptedException {//Begin Test Case
		/* This test covers selecting dropdown values
		 * tests properties of dropdowns. 
		 * tests properties of inputs.
		 * tests properties of divs (Text only, not innerHTML).
		 */
		
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.BeginClass);
		//BEGIN NEW SUITE
		browser.open(deploymentURL + "index.jsp");

		value = getValue("select", "//select[@id='keyword']");
		String expected = valToSelect;
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input1']");
		expected = Constants.KEYWORD_VALUES.get(KEYWORD_KEYS.BeginClass).get(0);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input2']");
		expected = "assigned_null";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input3Desc']");
		expected = "Input 3";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		//BEGIN NEW TEST
		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.BeginTest);
		browser.select("id=keyword", "label=" + valToSelect);

		value = getValue("select", "//select[@id='keyword']");
		expected = valToSelect;
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input1']");
		expected = Constants.KEYWORD_VALUES.get(KEYWORD_KEYS.BeginTest).get(0);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input1Desc']");
		expected = Constants.KEYWORD_DESCRIPTIONS.get(KEYWORD_KEYS.BeginTest).get(0);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input4Desc']");
		expected = "Input 4";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));
		
		//OPEN BROWSER
		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.OpenBrowser);
		browser.select("id=keyword", "label=" + valToSelect);

		value = getValue("select", "//select[@id='keyword']");
		expected = valToSelect;
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input1']");
		expected = Constants.KEYWORD_VALUES.get(KEYWORD_KEYS.OpenBrowser).get(0);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input1Desc']");
		expected = Constants.KEYWORD_DESCRIPTIONS.get(KEYWORD_KEYS.OpenBrowser).get(0);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input2Desc']");
		expected = "Input 2";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));
		
		//CLICK WEB ELEMENT
		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.ClickElement);
		browser.select("id=keyword", "label=" + valToSelect);

		value = getValue("select", "//select[@id='keyword']");
		expected = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.ClickElement);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input3']");
		expected = Constants.KEYWORD_VALUES.get(KEYWORD_KEYS.ClickElement).get(2);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input2Desc']");
		expected = Constants.KEYWORD_DESCRIPTIONS.get(KEYWORD_KEYS.ClickElement).get(1);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input4Desc']");
		expected = "Input 4";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		//ENTER TEXT IN BOX
		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.EnterTextInInput);
		browser.select("id=keyword", "label=" + valToSelect);

		value = getValue("select", "//select[@id='keyword']");
		expected = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.EnterTextInInput);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input1']");
		expected = Constants.KEYWORD_VALUES.get(KEYWORD_KEYS.EnterTextInInput).get(0);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input2Desc']");
		expected = Constants.KEYWORD_DESCRIPTIONS.get(KEYWORD_KEYS.EnterTextInInput).get(1);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input3Desc']");
		expected = "Input 3";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));
		
		//VERIFY OBJECT PROPERTY
		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.VerifyObjectProperty);
		browser.select("id=keyword", "label=" + valToSelect);

		value = getValue("select", "//select[@id='keyword']");
		expected = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.VerifyObjectProperty);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input1']");
		expected = Constants.KEYWORD_VALUES.get(KEYWORD_KEYS.VerifyObjectProperty).get(0);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input2Desc']");
		expected = Constants.KEYWORD_DESCRIPTIONS.get(KEYWORD_KEYS.VerifyObjectProperty).get(1);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input4Desc']");
		expected = Constants.KEYWORD_DESCRIPTIONS.get(KEYWORD_KEYS.VerifyObjectProperty).get(3);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));
		
		//END TEST SUITE
		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.EndClass);
		browser.select("id=keyword", "label=" + valToSelect);

		value = getValue("select", "//select[@id='keyword']");
		expected = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.EndClass);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input1']");
		expected = "";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input1Desc']");
		expected = "";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));
		
		//VERIFY OBJECT IS DISPLAYED
		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.VerifyObjectIsDisplayed);
		browser.select("id=keyword", "label=" + valToSelect);

		value = getValue("select", "//select[@id='keyword']");
		expected = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.VerifyObjectIsDisplayed);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input2']");
		expected = Constants.KEYWORD_VALUES.get(KEYWORD_KEYS.VerifyObjectIsDisplayed).get(1);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input1Desc']");
		expected = Constants.KEYWORD_DESCRIPTIONS.get(KEYWORD_KEYS.VerifyObjectIsDisplayed).get(0);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input3Desc']");
		expected = "Input 3";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		//VERIFY OBJECT IS NOT DISPLAYED
		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.VerifyObjectIsNotDisplayed);
		browser.select("id=keyword", "label=" + valToSelect);

		value = getValue("select", "//select[@id='keyword']");
		expected = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.VerifyObjectIsNotDisplayed);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input1']");
		expected = Constants.KEYWORD_VALUES.get(KEYWORD_KEYS.VerifyObjectIsNotDisplayed).get(0);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input2Desc']");
		expected = Constants.KEYWORD_DESCRIPTIONS.get(KEYWORD_KEYS.VerifyObjectIsNotDisplayed).get(1);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input4Desc']");
		expected = "Input 4";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		//SELECT DROPDOWN VALUE
		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.SelectDropdownValue);
		browser.select("id=keyword", "label=" + valToSelect);

		value = getValue("select", "//select[@id='keyword']");
		expected = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.SelectDropdownValue);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input2']");
		expected = Constants.KEYWORD_VALUES.get(KEYWORD_KEYS.SelectDropdownValue).get(1);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input1Desc']");
		expected = Constants.KEYWORD_DESCRIPTIONS.get(KEYWORD_KEYS.SelectDropdownValue).get(0);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input4Desc']");
		expected = "Input 4";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		//END CURRENT TEST
		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.EndTest);
		browser.select("id=keyword", "label=" + valToSelect);

		value = getValue("select", "//select[@id='keyword']");
		expected = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.EndTest);
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input1']");
		expected = "";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input1Desc']");
		expected = "";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));
	}//End Test Case

	@Test
	public void testBuildingASuiteAndRunning() throws InterruptedException {//Begin Test Case
		/* This test builds a new suite via the app.
		 * Once constructed, it will click the [Run Tests] button to kick it off.
		 * 		NOT CURRENTLY KICKING OFF TEST SUITE.  Need to do something with port number, or other fix, see below.
		 */
		
		browser.open(deploymentURL + "index.jsp");

		browser.click("id=deleteSuite");
		String valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.BeginClass);
		//The Default
		browser.click("id=AddInstruction");

		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.BeginTest);
		browser.select("id=keyword", "label=" + valToSelect);
		browser.click("id=AddInstruction");
		
		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.OpenBrowser);
		browser.select("id=keyword", "label=" + valToSelect);
		browser.click("id=AddInstruction");

		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.EndTest);
		browser.select("id=keyword", "label=" + valToSelect);
		browser.click("id=AddInstruction");

		valToSelect = Constants.KEYWORD_LONGNAMES.get(KEYWORD_KEYS.EndClass);
		browser.select("id=keyword", "label=" + valToSelect);
		browser.click("id=AddInstruction");
		
		//browser.click("id=RunTests");
		//Can run tests, but can't have Selenium start up ANOTHER Selenium server, so build will Fail.
		//Assert.assertTrue("Build Success", browser.isTextPresent("BUILD SUCCESS"));
	}//End Test Case

	
}//End Class