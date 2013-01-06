package org.ocpsoft.keywords;

import java.util.ArrayList;

public interface Keyword {

	String getType();
	String addInstruction(String testPath, ArrayList<String> inputValues);
	
	/*NOTE: For creating new keywords, you must:
	 * 1) Create the new Keyword Class to implement this interface
	 * 2) Update the InputConstants class to add the Key, LongName, InputDescriptions, and InputValues
	 * 3) Update src/main/resources/META-INF/services/org.ocpsoft.keywords.Keyword to include the newly created file
	 */
}
