package org.ocpsoft.keywords;

public interface Keyword {

	String getType();
	String addInstruction(String testPath, String desc1, String desc2, String desc3, String desc4, String value);
}
