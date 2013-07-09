package org.ocpsoft.dataBeans;

import java.util.ArrayList;

import org.ocpsoft.keywords.Keyword;

import com.ocpsoft.utils.ConfigXMLParser;
import com.ocpsoft.utils.Constants;

public class Instruction {
	Keyword keyword;
	ArrayList<String> inputs;

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

	public String toString(){
		String inputsAsOneString = "";
		for (String input : getInputs()) {
			inputsAsOneString += input + Constants.LIST_DELIMITER;
		}
		if(inputsAsOneString.length() > 0){
			inputsAsOneString = inputsAsOneString.substring(0, inputsAsOneString.length() - Constants.LIST_DELIMITER.length()); //Remove last delimiter
		}
		return getKeyword().shortName() + Constants.OBJECT_DELIMITER + inputsAsOneString;
	}


	public String toXMLString(ConfigXMLParser xmlParser){
		return xmlParser.generateInstructionXMLDoc(getKeyword().shortName().toString(), getInputs());
	}
}