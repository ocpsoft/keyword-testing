package org.ocpsoft.keywords;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Iterators;

public class KeywordTest {

	@Test
	public void test() {
		List<Keyword> keywords = Iterators.asList(ServiceLoader.load(Keyword.class));
		Assert.assertFalse(keywords.isEmpty());
	}

}
