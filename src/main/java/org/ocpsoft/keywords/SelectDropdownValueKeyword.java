package org.ocpsoft.keywords;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class SelectDropdownValueKeyword implements Keyword {

	@Override
	public String getType() {
		return "SelectDropdownValue";
	}

	@Override
	public String addInstruction(String testPath, String dropdownID, String textToSelect,
			String desc3, String desc4, String value) {
		try{
			File f = new File(testPath);
			if(!f.exists()) { 
				return "FAILURE: ClassFile does not exist, can not add instruction.  Fix path of: " + testPath;
			}
			else{
				PrintStream writetoTest = new PrintStream(
					     new FileOutputStream(testPath, true)); 
				writetoTest.append("\n" +
									"\t\tbrowser.select(\"id=" + dropdownID + "\", \"label=" + textToSelect + "\");\n");
				writetoTest.close();
				return "SUCCESS";
			}
		}
		catch (Exception e) {
			System.err.println("Failure in doClick: " + e);
			return "FAILURE in Click Instruction: " + e;
		}
	}

	/* EXAMPLE:
	 *  browser.select("id=keyword", "label=Begin New Suite");
	 */
	
}
