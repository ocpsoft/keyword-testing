package org.oscposft.individualTests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.keywords.Keyword;

import com.ocpsoft.utils.Constants;

public class KeywordTest {

	@Test
	public void testAllKeywordsAreDefinedAndGetLoaded() {
		/* Verify that we have a Keyword_Key defined in the Constants class for each Keyword Class we have
		 */		
		@SuppressWarnings("unchecked")
		List<Keyword> keywords = Iterators.asList(ServiceLoader.load(Keyword.class));
		Assert.assertFalse(keywords.isEmpty());
		Assert.assertTrue(keywords.size() == Constants.KEYWORD_KEYS.values().length);
	}
	
	@Test
	public void testSanityCheckOnInputConstantsMapSizes() throws InterruptedException {//Begin Test Case
		/* This test covers that the size of all the InputConstants Maps are the same
		 * We must guarantee that each map has the same number of keys to descriptions to values to longNames.
		 */
		Assert.assertTrue("Keys are same size as LongNames", 
				Constants.KEYWORD_KEYS.values().length == Constants.KEYWORD_LONGNAMES.size());
		
		Assert.assertTrue("LongNames is same size as Descriptions", 
				Constants.KEYWORD_LONGNAMES.size() == Constants.KEYWORD_DESCRIPTIONS.size());

		Assert.assertTrue("Descriptions is same size as Values", 
				Constants.KEYWORD_DESCRIPTIONS.size() == Constants.KEYWORD_VALUES.size());
	}//End Test Case
	
}
