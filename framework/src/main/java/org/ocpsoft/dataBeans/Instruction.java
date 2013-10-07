package org.ocpsoft.dataBeans;

import java.util.ArrayList;

import org.ocpsoft.keywords.Keyword;

import com.ocpsoft.utils.ConfigXMLParser;
import com.ocpsoft.utils.Constants;

public class Instruction {
	Keyword keyword;
	ArrayList<String> inputs;
	String nonConformingCodeLine;

	public Instruction() {
	}
	
	public Keyword getKeyword() {
		return keyword;
	}
	public void setKeyword(Keyword keyword) {
		this.keyword = keyword;
	}
	public ArrayList<String> getInputs() {
		return inputs;
	}
	public void setInputs(ArrayList<String> strings) {
		this.inputs = strings;
	}
	public void addInput(String input){
		this.inputs.add(input);
	}
	public String getNonConformingCodeLine() {
		return nonConformingCodeLine;
	}
	public void setNonConformingCodeLine(String nonConformingCodeLine) {
		this.nonConformingCodeLine = nonConformingCodeLine;
	}

	public String toString(){
		if(getNonConformingCodeLine() == null || getNonConformingCodeLine().equals("")){
			String inputsAsOneString = "";
			for (String input : getInputs()) {
				inputsAsOneString += input + Constants.LIST_DELIMITER;
			}
			if(inputsAsOneString.length() > 0){
				inputsAsOneString = inputsAsOneString.substring(0, inputsAsOneString.length() - Constants.LIST_DELIMITER.length()); //Remove last delimiter
			}
			return getKeyword().shortName() + Constants.OBJECT_DELIMITER + inputsAsOneString;
		}
		return getNonConformingCodeLine();
	}


	public String toXMLString(ConfigXMLParser xmlParser){
		if(getNonConformingCodeLine() == null || getNonConformingCodeLine().equals("")){
			return xmlParser.generateInstructionXMLDoc(getKeyword().shortName().toString(), getInputs());
		}
		return "\t<" + ConfigXMLParser.INSTRUCTION_XML_TAG + ">\n" +
				"\t\t<" + ConfigXMLParser.NON_CONFORMING_CODE_LINE_TAG + ">" + getNonConformingCodeLine() + 
												"</" + ConfigXMLParser.NON_CONFORMING_CODE_LINE_TAG + ">\n" +
				"\t</" + ConfigXMLParser.INSTRUCTION_XML_TAG + ">";
	}
}