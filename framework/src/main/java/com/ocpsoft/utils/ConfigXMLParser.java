package com.ocpsoft.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.ocpsoft.dataBeans.Instruction;
import org.ocpsoft.keywords.KeywordFactory;

public class ConfigXMLParser implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Inject KeywordFactory factory;

	public final static String INSTRUCTION_SET_XML_TAG = "InstructionSet";
	public final static String INSTRUCTION_XML_TAG = "Instruction";
	public final static String KEYWORD_XML_TAG = "Keyword";
	public final static String INPUTS_LIST_XML_TAG = "InputsList";
	public final static String INPUT_XML_TAG = "Input";
	public final static String NON_CONFORMING_CODE_LINE_TAG = "CodeLine";
		
	//Assumes there is only 1 child with the nodeName
	public Node getSimpleXmlNode(String XMLdoc, String nodeName){
		Node xml = XMLParser.parse(XMLdoc);
		return xml.getSingle(nodeName);
	}
	
	/* SAMPLE XMLdoc:
	 * <Instruction>
	 * 		<Keyword>OpenBrowser</Keyword>
	 * 		<Inputs>
	 * 			<Input>index.jsp</Input>
	 * 			<Input>otherInputValue</Input>
	 * 		</Inputs>
	 * </Instruction>
	 * 
	 * or
	 * 
	 * <Instruction>
	 * 		<CodeLine>deploymentURL = new URL("http://localhost:8080/keword-testing/");</CodeLine>
	 * </Instruction>
	 */
	public Instruction getInstructionObjectFromXMLDoc(String XMLdoc){
		Instruction instruction = new Instruction();
		
		if(getSimpleXmlNode(XMLdoc, KEYWORD_XML_TAG) == null){
			instruction.setNonConformingCodeLine(getSimpleXmlNode(XMLdoc, KEYWORD_XML_TAG).getText());
			return instruction;
		}
		
		Node keywordNode = getSimpleXmlNode(XMLdoc, KEYWORD_XML_TAG);
		String keywordString = keywordNode.getText();
		ArrayList<String> inputs = getInstructionInputsFromXMLDoc(XMLdoc);
		
		instruction.setKeyword(factory.createKeyword(keywordString));
		instruction.setInputs(inputs);
		
		return instruction;
	}
	private Instruction getInstructionObjectFromXMLNode(Node instructionNode) {
		Instruction instruction = new Instruction();
		String keywordString = "";
		ArrayList<String> inputs = new ArrayList<String>();
		List<Node> childNodes = instructionNode.getChildren();
		for (Node node : childNodes) {
			if(node.getName().equals(KEYWORD_XML_TAG)){
				keywordString = node.getText();
			} else if(node.getName().equals(INPUTS_LIST_XML_TAG)){
				inputs = getInstructionInputsFromXMLNode(node);
			} else if(node.getName().equals(NON_CONFORMING_CODE_LINE_TAG)){
				instruction.setNonConformingCodeLine(node.getText());
			} else {
				System.out.println("Invalid XML Tag while looking to get Instruction object. Tag: " + node.getName() + " is invalid.  Not doing anything with it");
			}
		}

		instruction.setKeyword(factory.createKeyword(keywordString));
		instruction.setInputs(inputs);
		return instruction;
	}

	//Will get the inputs if you pass the XML of just the InputList, or the entire Instruction
	public ArrayList<String> getInstructionInputsFromXMLDoc(String XMLdoc) {
		Node inputsListNode = null;
		Node root = XMLParser.parse(XMLdoc);
		if(root.getName().equals(INPUTS_LIST_XML_TAG)){
			inputsListNode = root;
		} else {
			inputsListNode = getSimpleXmlNode(XMLdoc, INPUTS_LIST_XML_TAG);
		}
		return getInstructionInputsFromXMLNode(inputsListNode);
	}
	private ArrayList<String> getInstructionInputsFromXMLNode(Node inputsListNode) {
		List<Node> inputNodes = inputsListNode.getChildren();
		ArrayList<String> inputs = new ArrayList<>();
		for (Node node : inputNodes) {
			inputs.add(node.getText());
		}
		return inputs;
	}
	
	//Root Node must be <INSTRUCTION_SET_XML_TAG> with any number of <INSTRUCTION_XML_TAG>'s under it
	public ArrayList<Instruction> getInstructionSetFromXMLDoc(String XMLdoc){
		ArrayList<Instruction> instructionSet = new ArrayList<Instruction>();
		
		Node xml = XMLParser.parse(XMLdoc);
		if(INSTRUCTION_SET_XML_TAG.equals(xml.getName()) == false){
			return null;
		}
		List<Node> instructionNodes = xml.getChildren();
		for (Node node : instructionNodes) {
			instructionSet.add(getInstructionObjectFromXMLNode(node));
		}
		return instructionSet;
	}

	/********* BEGIN Method for generating the XML Documents*********/
	public String generateInstructionSetXMLDoc(ArrayList<String> keywords, ArrayList<ArrayList<String>> inputs){
		String xmlDoc = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<" + INSTRUCTION_SET_XML_TAG + ">\n";
		for (int x=0; x < keywords.size(); x++) {
			xmlDoc += generateInstructionXMLDoc(keywords.get(x), inputs.get(x)) + "\n";
		}
		xmlDoc += "</" + INSTRUCTION_SET_XML_TAG + ">";
		return xmlDoc;
	}
	public String generateInstructionXMLDoc(String keyword, ArrayList<String> inputs) {
		String xmlDoc = "\t<" + INSTRUCTION_XML_TAG + ">\n" +
				"\t\t<" + KEYWORD_XML_TAG + ">" + keyword + "</" + KEYWORD_XML_TAG + ">\n" +
				generateInputsXMLDoc(inputs) +
				"\n\t</" + INSTRUCTION_XML_TAG + ">";
		return xmlDoc;
	}
	public String generateInputsXMLDoc(ArrayList<String> inputs) {
		String xmlDoc = "\t\t<" + INPUTS_LIST_XML_TAG + ">\n";
		for (String input : inputs) {
			xmlDoc += "\t\t\t<" + INPUT_XML_TAG + ">" + input + "</" + INPUT_XML_TAG + ">\n";
		}
		xmlDoc += "\t\t</" + INPUTS_LIST_XML_TAG + ">";
		return xmlDoc;
	}
	/********* END Method for generating the XML Documents*********/
}
