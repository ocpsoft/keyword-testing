package com.ocpsoft.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InputConstants {
	
	public static enum KEYWORD_KEYS
	{
		BeginClass, BeginTest, ClickElement, EndClass, EndTest, EnterTextInInput, OpenBrowser, SelectDropdownValue, VerifyObjectIsDisplayed, 
		VerifyObjectIsNotDisplayed, VerifyObjectProperty 
	}
	public static final Map<KEYWORD_KEYS, String> KEYWORD_LONGNAMES = new LinkedHashMap<KEYWORD_KEYS, String>();
    static {
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.BeginClass, "Begin New Suite");
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.BeginTest, "Begin New Test");
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.OpenBrowser, "Open Browser");
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.ClickElement, "Click Web Element");
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.EnterTextInInput, "Enter Text in Box");
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.VerifyObjectProperty, "Verify Object Property");
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.VerifyObjectIsDisplayed, "Verify Object Is Displayed");
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.VerifyObjectIsNotDisplayed, "Verify Object Is NOT Displayed");
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.SelectDropdownValue, "Select Dropdown Value");
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.EndTest, "End Current Test");
    	KEYWORD_LONGNAMES.put(KEYWORD_KEYS.EndClass, "End Test Suite");
    }
	public static Map<KEYWORD_KEYS, String> getKeywordLongnames() {
		return KEYWORD_LONGNAMES;
	}


	public static final Map<KEYWORD_KEYS, List<String>> KEYWORD_DESCRIPTIONS = new HashMap<KEYWORD_KEYS, List<String>>();
    static {
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.BeginClass, Arrays.asList("with Suite Name of:"));
    	
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.BeginTest, Arrays.asList("with Test Name of:"));
    	
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.ClickElement, Arrays.asList("with Identifyer [link/id/name/css/xpath] of:",
											    			   "and desired element key of:", 
															   "and desired Destination Path of (FOR Identifyers of link ONLY):"));
    	
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.EndClass, new ArrayList<String>());
    	
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.EndTest, new ArrayList<String>());
    	
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.EnterTextInInput, Arrays.asList("with xPath of Input:",
				   												   "and Text to enter:"));
    	
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.OpenBrowser, Arrays.asList("with Starting Webpage of:"));
    	
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.SelectDropdownValue, Arrays.asList("with Dropdown object's ID of:",
				 													  "selecting the value of:"));
    	
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.VerifyObjectIsDisplayed, Arrays.asList("with Verification Message of:",
				  														  "with XPath property to verify of:"));
    	
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.VerifyObjectIsNotDisplayed, Arrays.asList("with Verification Message of:",
				  															 "with XPath property to verify of:"));
    	
    	KEYWORD_DESCRIPTIONS.put(KEYWORD_KEYS.VerifyObjectProperty, Arrays.asList("with Verification Message of:",
																	   "with object Type of:",
																	   "with XPath property to verify of:",
																	   "with value to verify of:"));
    }
    
	public static Map<KEYWORD_KEYS, List<String>> getKeywordDescriptions() {
		return KEYWORD_DESCRIPTIONS;
	}
	
	
	public static final Map<KEYWORD_KEYS, List<String>> KEYWORD_VALUES = new HashMap<KEYWORD_KEYS, List<String>>();
    static {
    	KEYWORD_VALUES.put(KEYWORD_KEYS.BeginClass, Arrays.asList("MySuiteTest"));
    	
    	KEYWORD_VALUES.put(KEYWORD_KEYS.BeginTest, Arrays.asList("testName"));
    	
    	KEYWORD_VALUES.put(KEYWORD_KEYS.ClickElement, Arrays.asList("link",
											    		 "Get Your Info Here", 
														 "myInfo.html"));
    	
    	KEYWORD_VALUES.put(KEYWORD_KEYS.EndClass, new ArrayList<String>());
    	
    	KEYWORD_VALUES.put(KEYWORD_KEYS.EndTest, new ArrayList<String>());
    	
    	KEYWORD_VALUES.put(KEYWORD_KEYS.EnterTextInInput, Arrays.asList("//input[@id='className']",
				   											 "Assigning Input Text"));
    	
    	KEYWORD_VALUES.put(KEYWORD_KEYS.OpenBrowser, Arrays.asList("index.jsp"));
    	
    	KEYWORD_VALUES.put(KEYWORD_KEYS.SelectDropdownValue, Arrays.asList("keyword",
				 												"Begin New Suite"));
    	
    	KEYWORD_VALUES.put(KEYWORD_KEYS.VerifyObjectIsDisplayed, Arrays.asList("User should be on MyInfo Page!",
				  													"div[@id='myFBdata']"));
    	
    	KEYWORD_VALUES.put(KEYWORD_KEYS.VerifyObjectIsNotDisplayed, Arrays.asList("User should NOT see message [Error: invalid action]!",
				  													   "div[@id='myFBdata']"));
    	
    	KEYWORD_VALUES.put(KEYWORD_KEYS.VerifyObjectProperty, Arrays.asList("Selected Value should be Begin New Suite",
																	   "select",
																	   "//select[@id='keyword']",
																	   "Begin New Suite"));
    }
    
	public static Map<KEYWORD_KEYS, List<String>> getKeywordValues() {
		return KEYWORD_VALUES;
	}
}