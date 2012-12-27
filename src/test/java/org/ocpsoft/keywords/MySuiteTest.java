package org.ocpsoft.keywords;

import java.io.File;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.services.MyWebService;

import com.thoughtworks.selenium.DefaultSelenium;

@RunWith(Arquillian.class)
public class MySuiteTest {//Begin Class

   private static final String WEBAPP_SRC = "src/main/webapp";
   
   @Deployment(testable = false) // testable = false to run as a client
	public static WebArchive createDeployment() {
		return ShrinkWrap.create(WebArchive.class, "KeywordApp.war")
						.addClasses(MyWebService.class)
						.addAsResource("META-INF/persistence.xml")
						.addAsWebResource(new File(WEBAPP_SRC, "index.html"))
						.addAsWebResource(new File(WEBAPP_SRC, "myLink.html"))
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
		
		browser.open(deploymentURL + "index.html");

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
		System.out.println("Check 2 - Value = [" + value + "]");
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
		
		browser.open(deploymentURL + "index.html");

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
	public void testKeywordDropdown() throws InterruptedException {//Begin Test Case
		/* This test covers selecting dropdown values
		 * tests properties of dropdowns. 
		 * tests properties of inputs.
		 * tests properties of divs (Text only, not innerHTML).
		 */
		
		String valToSelect = "Begin New Suite";
		//BEGIN NEW SUITE
		browser.open(deploymentURL + "index.html");

		value = getValue("select", "//select[@id='keyword']");
		String expected = valToSelect;
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input1']");
		expected = "MySuiteTest";
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
		valToSelect = "Begin New Test";
		browser.select("id=keyword", "label=" + valToSelect);

		value = getValue("select", "//select[@id='keyword']");
		expected = valToSelect;
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input1']");
		expected = "testName";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input1Desc']");
		expected = "with Test Name of:";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input4Desc']");
		expected = "Input 4";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));
		
		//OPEN BROWSER
		valToSelect = "Open Browser";
		browser.select("id=keyword", "label=" + valToSelect);

		value = getValue("select", "//select[@id='keyword']");
		expected = valToSelect;
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input1']");
		expected = "index.html";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input1Desc']");
		expected = "with Starting Webpage of:";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input2Desc']");
		expected = "Input 2";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));
		
		//CLICK WEB ELEMENT
		valToSelect = "Click Web Element";
		browser.select("id=keyword", "label=" + valToSelect);

		value = getValue("select", "//select[@id='keyword']");
		expected = "Click Web Element";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input3']");
		expected = "myInfo.html";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input2Desc']");
		expected = "and desired element key of:";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input4Desc']");
		expected = "Input 4";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		//ENTER TEXT IN BOX
		valToSelect = "Enter Text in Box";
		browser.select("id=keyword", "label=" + valToSelect);

		value = getValue("select", "//select[@id='keyword']");
		expected = "Enter Text in Box";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input1']");
		expected = "//input[@id='className']";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input2Desc']");
		expected = "and Text to enter:";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input3Desc']");
		expected = "Input 3";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));
		
		//VERIFY OBJECT PROPERTY
		valToSelect = "Verify Object Property";
		browser.select("id=keyword", "label=" + valToSelect);

		value = getValue("select", "//select[@id='keyword']");
		expected = "Verify Object Property";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input1']");
		expected = "Selected Value should be Begin New Suite";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input2Desc']");
		expected = "with object Type of:";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input4Desc']");
		expected = "with value to verify of:";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));
		
		//END TEST SUITE
		valToSelect = "End Test Suite";
		browser.select("id=keyword", "label=" + valToSelect);

		value = getValue("select", "//select[@id='keyword']");
		expected = "End Test Suite";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input1']");
		expected = "assigned_null";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input1Desc']");
		expected = "Input 1";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));
		
		//VERIFY OBJECT IS DISPLAYED
		valToSelect = "Verify Object Is Displayed";
		browser.select("id=keyword", "label=" + valToSelect);

		value = getValue("select", "//select[@id='keyword']");
		expected = "Verify Object Is Displayed";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input2']");
		expected = "div[@id='myFBdata']";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input1Desc']");
		expected = "with Verification Message of:";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input3Desc']");
		expected = "Input 3";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		//VERIFY OBJECT IS NOT DISPLAYED
		valToSelect = "Verify Object Is NOT Displayed";
		browser.select("id=keyword", "label=" + valToSelect);

		value = getValue("select", "//select[@id='keyword']");
		expected = "Verify Object Is NOT Displayed";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input1']");
		expected = "User should NOT see message [Error: invalid action]!";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input2Desc']");
		expected = "with XPath property to verify of:";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input4Desc']");
		expected = "Input 4";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		//SELECT DROPDOWN VALUE
		valToSelect = "Select Dropdown Value";
		browser.select("id=keyword", "label=" + valToSelect);

		value = getValue("select", "//select[@id='keyword']");
		expected = "Select Dropdown Value";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input2']");
		expected = "Begin New Suite";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input1Desc']");
		expected = "with Dropdown object's ID of:";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input4Desc']");
		expected = "Input 4";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		//END CURRENT TEST
		valToSelect = "End Current Test";
		browser.select("id=keyword", "label=" + valToSelect);

		value = getValue("select", "//select[@id='keyword']");
		expected = "End Current Test";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("input", "//input[@id='Input1']");
		expected = "assigned_null";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));

		value = getValue("div", "//div[@id='input1Desc']");
		expected = "Input 1";
		Assert.assertTrue("value should be [" + expected + "]",
				expected.equals(value));
	}//End Test Case

}//End Class