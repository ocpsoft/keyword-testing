package org.ocpsoft.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.PathParam;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.Visibility;
import org.jboss.forge.parser.java.util.Formatter;
import org.ocpsoft.dataBeans.Instruction;
import org.ocpsoft.keywords.AssignVariableKeyword;
import org.ocpsoft.keywords.CallActionKeyword;
import org.ocpsoft.keywords.Keyword;
import org.ocpsoft.keywords.Keyword.KEYWORD_PROCESS_TYPES;
import org.ocpsoft.keywords.KeywordFactory;
import org.ocpsoft.keywords.VariableKeywordInterface;

import com.ocpsoft.projectStarter.ActionsFileCreator;
import com.ocpsoft.projectStarter.HelperFileCreator;
import com.ocpsoft.utils.ConfigXMLParser;
import com.ocpsoft.utils.Constants;
import com.ocpsoft.utils.Constants.KEYWORD_KEYS;
import com.ocpsoft.utils.Utility;

public class MyWebServiceImpl implements MyWebServiceInterface{

	@Inject KeywordFactory factory;
	@Inject ConfigXMLParser xmlParser;
	
	String className = "";//Should be re-assigned on BeginClassKeyword
	
	public String echoService(@PathParam("message") String message) {
		System.out.println("GET on message");
		return message;
	}

	public Keyword generateKeyword(@PathParam("shortName") String shortName) {
		System.out.println("Generating a keyword for: " + shortName);
		Keyword keyword = factory.createKeyword(shortName);
		if(keyword == null){
			System.out.println("GenerateKeyword ERROR - Invalid shortName: " + shortName + ", could not generate a valid keyword.");
		}
		System.out.println("Returning generated keyword: " + keyword);
		return keyword;
	}

	public Instruction generateInstructionFromXMLDoc(@PathParam("xmlDoc") String xmlDoc) {
		System.out.println("Getting Instruction From XML String Document");
		xmlDoc = decodeURLComponent(xmlDoc);
		Instruction instruction = xmlParser.getInstructionObjectFromXMLDoc(xmlDoc);
		System.out.println("Got Instruction: " + instruction);
		return instruction;
	}

	public ArrayList<Instruction> generateInstructionSetFromXMLDoc(@PathParam("xmlDoc") String xmlDoc) {
		System.out.println("Getting Instruction Set From XML String Document");
		xmlDoc = decodeURLComponent(xmlDoc);
		ArrayList<Instruction> instructions = xmlParser.getInstructionSetFromXMLDoc(xmlDoc);
		System.out.println("Got Instruction Set: " + instructions);
		return instructions;
	}
	
	public String getAllKeywordtypes() {
		System.out.println("Get on all Keyword Types");
		return factory.getAllKeywordTypes().toString();
	}
	
	public String runBuild() {
		System.out.println("Run Build Request");
		// Java code to run the build
		boolean isSuccessful = false;
		String testLine = "";
		try {
			Process p;
			if(Constants.APP_UNDER_TEST_ROOT_FILE_PATH.startsWith("D:") ||
					Constants.APP_UNDER_TEST_ROOT_FILE_PATH.startsWith("C:")){
				//Currently on WINDOWS development
				p = Runtime.getRuntime().exec("cmd.exe /C mvn test -PJBOSS_AS_REMOTE_7.X -f " + Constants.APP_UNDER_TEST_ROOT_FILE_PATH + "pom.xml");
				//Example: mvn test -PJBOSS_AS_REMOTE_7.X -f C:\Development\projects\AppUnderTest\pom.xml
			}
			else {
				//Currently on UNIX development
				p = Runtime.getRuntime().exec(
						"mvn test -PJBOSS_AS_REMOTE_7.X -f " + Constants.APP_UNDER_TEST_ROOT_FILE_PATH + "pom.xml");
			}
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			BufferedReader errorReader = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			String errorLine = errorReader.readLine();
			while (errorLine != null) {
				System.out.println("MAVEN TEST RUN ERROR: " + errorLine);
				errorLine = reader.readLine();
			}
			
			String line = reader.readLine();
			while (line != null) {
				System.out.println(line);
				if (line.contains("[INFO] BUILD SUCCESS")) {
					isSuccessful = true;
				}
				if (line.contains("Tests run: ")) {
					testLine = line;
				}
				line = reader.readLine();
			}

			p.destroy();
		} catch (Exception e) {
			System.out.println("ERROR - Failure to build: " + e);
			return "Build Failed... Check Console for error";
		}

		System.out.println("Done Build Request - isSuccessful: " + isSuccessful + " - testLine: " + testLine);

		if (isSuccessful) {
			return "Build SUCCESSFUL!!!" + "<BR />" + testLine;
		} else {
			return "Build Failed... Check Console for error" + "<BR />"
					+ testLine;
		}
	}

	public String getTestSuite(@PathParam("className") String className) {
		String classPath = Constants.APP_UNDER_TEST__TEST_FILE_PATH + className + ".java";
		System.out.println("Getting Test Suite: " + classPath);
		
		File file = new File(classPath);
		//We want to take the file and using the parser display something user friendly back to the page
		//User's don't care about the code under it all, they just want to see keywords in steps.
		try {
			JavaClass testClass = (JavaClass) JavaParser.parse(file);
			return Utility.generateTestSuiteOutput(testClass, className);
		} catch (FileNotFoundException e1) {
			System.out.println("ERROR:Requested Class does not exist!!!");
			return "<font color='red'>ERROR: No such file Exists.  Could not grab Test Suite: " + className + "</font>";
		}
	}

	public String moveTestStep(@PathParam("className") String className,
			@PathParam("testCaseName") String testCaseName,
			@PathParam("stepNumber") int stepNumber,
			@PathParam("direction") String direction) {
		String completePath = Constants.APP_UNDER_TEST__TEST_FILE_PATH + className + ".java";
		System.out.println("Moving Step [" + stepNumber + "] in test {" + testCaseName + "} dirction: " + direction);
		
		//TODO: #DeploymentURL_HACK
		//Even though we are displaying the AssignKeyword for deploymentURL,
		//We don't actually want to be able to move that line.  It MUST be the first in the test.
		int top = 1; //If we could move the first line, then top should be 0.

		File file = new File(completePath);
		try {
			JavaClass testClass = (JavaClass) JavaParser.parse(file);
			List<Member<JavaClass, ?>> allMembers = testClass.getMembers();
			for (Member<JavaClass, ?> member : allMembers) {
				if(Utility.doesMemberMatchTestCaseName(member, testCaseName)){
					String[] steps = Utility.getStepsFromMethod(member);
					if(direction.equalsIgnoreCase("down")){
						if(stepNumber >= steps.length - 1){
							return "ERROR: Can not move the last step down.";
						}
						String tempStep = steps[stepNumber + 1];
						steps[stepNumber+1] = steps[stepNumber];
						steps[stepNumber] = tempStep;
					}
					else if(direction.equalsIgnoreCase("up")){
						if(stepNumber <= top){
							return "ERROR: Can not move the first step up.";
						}
						String tempStep = steps[stepNumber - 1];
						steps[stepNumber-1] = steps[stepNumber];
						steps[stepNumber] = tempStep;
					}
					String newBody = "";
					for (String step : steps) {
						newBody += step;
						//Add the ; back at the end of the statement, unless this step is really a block
						if(!step.endsWith("}")){
							newBody += ";";
						}
					}
					testClass.getMethod(Utility.getTestCaseName(member))
						.setBody(newBody);
					Utility.rewriteFile(testClass, completePath);
					
					//Lastly, return the output for the newly updated file
					return Utility.generateTestSuiteOutput(testClass, className);
				}
			}
			
			return "ERROR: Could not find TestCase with Name: " + testCaseName + ", so we could not move the step.";
		} catch (FileNotFoundException e1) {
			System.out.println("ERROR:Requested Class does not exist!!!");
			return "<font color='red'>ERROR: No such file Exists.  Could not move the step!</font>";
		}
	}
	
	public String getListOfTestMethodNames(@PathParam("className") String className) {
		String classPath = Constants.APP_UNDER_TEST__TEST_FILE_PATH + className + ".java";
		System.out.println("Getting List of all Tests in Suite: " + classPath);

		try{
			File testClassFile = new File(Constants.APP_UNDER_TEST__TEST_FILE_PATH + className + ".java");
			JavaClass testClass = (JavaClass) JavaParser.parse(testClassFile);
			List<Member<JavaClass, ?>> allMembers = testClass.getMembers();
			String returnVal = "";
			for (Member<JavaClass, ?> member : allMembers) {
				if(Utility.memberIsATestCase(member)){
					returnVal+= Utility.getTestCaseName(member) + ",";
				}
				else {/*It's probably a variable or something, not an actual test method, we don't want that*/}
			}
			if(returnVal.equals("")){
				return "";
			} else {
				return returnVal.substring(0, returnVal.length() - 1); //Remove the last ","
			}
		}
		catch (Exception e) {
			System.out.println("ERROR: Could not find file: " + className + ", Could not get list of Tests!");
			return "";
		}
	}

	public String deleteTestSuite(@PathParam("className") String className) {
		String rootPath = Constants.APP_UNDER_TEST__TEST_FILE_PATH;
		System.out.println("Deleting Test Suite: " + rootPath + className);

		File file = new File(rootPath + className);
		try{
			if(!file.exists()){
				System.out.println("Trying to delete file that does NOT exist.");
				return "<font color='orange'>WARNING: You tried to delete a file that does not exist. File [" +
						rootPath + className + "] was already deleted.";
			}
			if(file.delete()){
				System.out.println(file.getName() + " is deleted!");
				return "Successfully deleted all tests within " + rootPath + className;
			}else{
				System.out.println("Delete operation is failed.");
				return "<font color='red'>ERROR: Delete operation has failed.  The file [" + rootPath + className + "] might still exist.</font>";
			}
	
		}catch(Exception e){
			e.printStackTrace();
			return "<font color='red'>ERROR in the delete process.  System error: " + e + "</font>";
		}
	}
	
	public String deleteTestCase(@PathParam("className") String className, @PathParam("testCaseName") String testCaseName){
		System.out.println("Deleting Test Case: " + testCaseName);

		JavaClass testClass = Utility.getJavaClass(className);
		Method<JavaClass> testCaseMethod = Utility.getTestCaseMethodFromName(testCaseName, testClass);
		if(testCaseMethod == null) {
			System.out.println("ERROR: Could not retrieve test: " + testCaseName + " to delete.  No action was taken.");
			return "ERROR: Could not retrieve test: " + testCaseName + " to delete.  No action was taken.";
		}
		String testClassText = testClass.toString();
		testClass.removeMethod(testCaseMethod);
		if(testClass.toString().equals(testClassText)){
			System.out.println("ERROR: Removing test from class did not change the class at all.  No action was taken");
			return "ERROR: Removing test from class did not change the class at all.  No action was taken";
		}
		
		try {
			PrintStream writetoTest = new PrintStream(new FileOutputStream(
					Constants.APP_UNDER_TEST__TEST_FILE_PATH + testClass.getName() + ".java"));
			writetoTest.print(Formatter.format(testClass)); //TODO: This doesn't work, low priority to fix
			writetoTest.close();
		} catch (Exception e) {
			System.err.println("Failure in create deleting Test Case: " + e);
			return "ERROR: Could not delete Test Case.";
		}
		
		System.out.println("SUCCESS: Test Case Deleted named [" + testCaseName + "].");
		return "SUCCESS: Test Case Deleted named [" + testCaseName + "].";
	}
	
	public String exportTestToAction(@PathParam("testClassName") String testClassName, 
			@PathParam("testName") String testName, 
			@PathParam("actionName") String actionName) {
		
		System.out.println("Exporting a test [" + testName + "] to a new Action [" + actionName + "].");
		String testBody = "";
		JavaClass testClass = Utility.getJavaClass(testClassName);
		if(testClass != null) {
			testBody = testClass.getMethod(testName).getBody();
		} else {
			System.out.println("ERROR: Could not retrieve " + testName + ".java class.  Could not export your new Action [" + actionName + "]");
			return "ERROR: Could not retrieve " + testName + ".java class.  Could not export your new Action [" + actionName + "]";
		}
		
		JavaClass actionsClass = Utility.getJavaClass("Actions");
		if(actionsClass != null) {
			actionsClass.addMethod().setName(actionName)
			.setVisibility(Visibility.PUBLIC).setReturnTypeVoid()
			.setStatic(true)
			
			//TODO: #DeploymentURL_HACK
			.addThrows(MalformedURLException.class)
						
			.setParameters("URL deploymentURL, DefaultSelenium browser")
			.setBody(testBody);
			
			try {
				PrintStream writetoTest = new PrintStream(new FileOutputStream(
						Constants.APP_UNDER_TEST__TEST_FILE_PATH + actionsClass.getName() + ".java"));
				writetoTest.print(Formatter.format(actionsClass)); //TODO: This doesn't work, low priority to fix
				writetoTest.close();
			} catch (Exception e) {
				System.err.println("Failure in create new Test Case: " + e);
				return "ERROR: Could not create new Test.";
			}
			
			System.out.println("SUCCESS: New Action [" + actionName + "] created successfully.");
			return "SUCCESS: New Action [" + actionName + "] created successfully.";
		} else {
			System.out.println("ERROR: Could not retrieve Actions.java class.  Could not export your new Action [" + actionName + "]");
			return "ERROR: Could not retrieve Actions.java class.  Could not export your new Action [" + actionName + "]";
		}
	}
	
	public String exportTestCase(@PathParam("className") String className, @PathParam("testName") String testName) {
		String returnString = "";
		String rootPath = Constants.EXPORT_FILE_PATH;
		String fullFilePath = rootPath + testName + ".txt";
		System.out.println("Exporting Test Case: " + fullFilePath);

		File file = new File(fullFilePath);
		String content = getTestMethodCode(className, testName);
		content = FormatCodeStatementsToXMLInstructions(content);
		if(content.equals("")){
			return "<font color='red'>ERROR: Test [" + testName + "] in class [" + className + "] has no code." +
					"Either class or test do not exist.</font>";
		}
 
		try (FileOutputStream fop = new FileOutputStream(file)) {
 
			if (file.exists()) {
				returnString+= "<font color='orange'>WARNING: File already existed.  We are deleting the old file.</font><BR />";
				file.delete();
			}
			file.createNewFile();
			
			byte[] contentInBytes = content.getBytes();
 
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
 
			System.out.println("Export complete successfully.");
			returnString+= "<font color='green'><B>File Created!</B></font><BR />";
 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return returnString;
	}
	private String getTestMethodCode(String className, String testName){
		JavaClass testClass = Utility.getJavaClass(className);
		if(testClass == null) {
			return null;
		}
		Method<JavaClass> currentMethod = testClass.getMethod(testName);
		if(currentMethod == null){
			System.out.println("Could not find current testCaseName: " + testName + ", could not get Method's code.");
			return null;
		}
		return currentMethod.getBody();
	}
	private String FormatCodeStatementsToXMLInstructions(String methodBody){
		String returnString = "";
		if(methodBody == null){
			return returnString;
		}
		methodBody=methodBody.replace("\n", "");
		methodBody=methodBody.replace("; ", ";");
		String[] statements = methodBody.split(";");
		
		//Build the XML document as a string
		returnString+="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<" + ConfigXMLParser.INSTRUCTION_SET_XML_TAG + ">\n";
		for (String statement : statements) {
			if(!Utility.isEmptyStep(statement)){
				Instruction i = getInstructionFromCodeStatement(statement);
				if(i != null){
					//TODO: #DeploymentURL_HACK
					ArrayList<String> deployURLInputs = new ArrayList<String>();
					deployURLInputs.add("deploymentURL");
					deployURLInputs.add("new URL(\"" + Constants.FRAMEWORK_LOCALHOST_URL + "\")");
					if(i.getKeyword() instanceof AssignVariableKeyword && i.getInputs().equals(deployURLInputs)){
						//do nothing for #DeploymentURL_HACK
						//We don't want to translate this step as something to export.
					} else {
						returnString+=  i.toXMLString(xmlParser) + "\n";
					}
				}
			}
		}
		returnString+="</" + ConfigXMLParser.INSTRUCTION_SET_XML_TAG + ">";
		return returnString;
	}
	private Instruction getInstructionFromCodeStatement(String statement){
		
		/*Sample statement:
		 * 		Helper.OpenBrowser(browser, Arrays.asList("index.jsp"), deploymentURL);
		 * 
		 * 		Or direct Code lines like the following:
		 * 		Actions.myActionCall(deploymentURL, browser)
		 * 
		 * 		Or variable creation or assignments
		 */
		String keyword = "";
		Instruction instruction = null;
		
		if(statement.contains("Helper.")){
			keyword = statement.substring("Helper.".length(), statement.indexOf("("));
			//TODO: Note: This makes assumption that there are no ")" chars in any of the input elements.
			String inputsString = statement.substring(statement.indexOf("asList(") + "asList(".length(), statement.indexOf(")"));
			inputsString = inputsString.replace("\"", "");
			//TODO: Note: This makes assumption that there are no "," chars in any of the input elements.
			ArrayList<String> inputs = Utility.convertStringToArrayListString(inputsString, ",");
			
			instruction = new Instruction();
			instruction.setKeyword(factory.createKeyword(keyword));
			instruction.setInputs(inputs);
			return instruction;
		} else if(statement.contains("Actions.")){
			instruction = new Instruction();
			instruction.setNonConformingCodeLine(statement);
			return instruction;
		} else if(Utility.isVariableAssignmentORcreationStep(statement)){
			if(Utility.isVariableCreationStep(statement)){
				keyword = Constants.KEYWORD_KEYS.CreateVariable.toString();
				String varType = (String) statement.subSequence(0, statement.indexOf(" "));
				String varName = (String) statement.subSequence(statement.indexOf(" "), statement.indexOf("="));
				String initialization = statement.substring(statement.indexOf("=") + 1);
				String inputsString = varName.trim() + Constants.LIST_DELIMITER + varType.trim() + Constants.LIST_DELIMITER + initialization.trim();
				ArrayList<String> inputs = Utility.convertStringToArrayListString(inputsString, Constants.LIST_DELIMITER);
				
				instruction = new Instruction();
				instruction.setKeyword(factory.createKeyword(keyword));
				instruction.setInputs(inputs);
				return instruction;
			}
			//Must be Assignment Keyword
			keyword = Constants.KEYWORD_KEYS.AssignVariable.toString();
			String varName = (String) statement.subSequence(0, statement.indexOf("="));
			String assignValue = statement.substring(statement.indexOf("=") + 1);
			String inputsString = varName.trim() + Constants.LIST_DELIMITER + assignValue.trim();
			ArrayList<String> inputs = Utility.convertStringToArrayListString(inputsString, Constants.LIST_DELIMITER);
			
			instruction = new Instruction();
			instruction.setKeyword(factory.createKeyword(keyword));
			instruction.setInputs(inputs);
			return instruction;
		} else {
			//TODO: Might need to make anything in the else block just a nonConformingCodeLine
			System.out.println("ERROR - Can't parse into Instrcution object. Unknown type of statement:" + statement);
			return null;
		}
	}
	
	public String importTestSteps(@PathParam("className") String className, 
			@PathParam("testName") String testName, @PathParam("importFile") String importFilePath) {
		importFilePath = decodeURLComponent(importFilePath);
		String returnString = "";
		System.out.println("Importing steps from file: " + importFilePath + ", into test: " + testName);

		File file = new File(importFilePath);
		if(!file.exists()){
			return "<font color='red'>ERROR: Import file specified: " + importFilePath + ", does NOT exist.  Could not import any steps.</font>";
		}

		ArrayList<Instruction> importInstructions = getInstructionsFromXMLFile(importFilePath);
		if(importInstructions == null){
			return "<font color='red'>ERROR: Import file specified: " + importFilePath + ", does NOT contain valid steps.  <BR />" +
					"Could not parse input file (or it's blank).  Import not completed</font>";
		}
	  
		returnString+="Just finished importing the following Keywords:<BR />";
		int count = 0;
		for (Instruction instruction : importInstructions) {
			//We don't want to try to import the header of [Keyword~~Inputs] from the input file.
			addInstructionToTest(className, testName, instruction);
			if(instruction.getKeyword() != null){
				returnString+=instruction.getKeyword().shortName() + "<BR />";
			} else if(instruction.getNonConformingCodeLine() != null){
				returnString+=instruction.getNonConformingCodeLine() + "<BR />";
			} else {
				returnString+="ERROR with instruction: " + instruction;
			}
			count++;
		}
	  
		System.out.println("We just added a total of [" + count + "] instructions from the import file.");
		returnString+="<P />[" + count + "] total instructions have been imported.";
		return returnString;
	}
	private ArrayList<Instruction> getInstructionsFromXMLFile(String filepath){
		/*Sample File:
			<InstructionSet>
				<Instruction>
					<Keyword>VerifyObjectProperty</Keyword>
					<InputsList>
						<Input>Selected Value should be Begin New Suite</Input>
						<Input>select</Input>
						<Input>//select[@id='keyword']</Input>
						<Input>Begin New Suite</Input>
					</InputsList>
				</Instruction>
				<Instruction>
					...
				</Instruction>
				<Instruction>
					...
				</Instruction>
			</InstructionSet>
		 */
		String xmlDoc = "";
		try (BufferedReader br = new BufferedReader(new FileReader(filepath)))
		{
			String curFileLine;
			while ((curFileLine = br.readLine()) != null) {
				xmlDoc += curFileLine + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return xmlParser.getInstructionSetFromXMLDoc(xmlDoc);
	}
	
	public String startNewProject(){
		//TODO: This should really create a totally new project, for now we'll just keep re-using AppUnderTest
		String rootPath = Constants.APP_UNDER_TEST__TEST_FILE_PATH;
		File helperFile = new File(rootPath + "Helper.java");
		
		//First, if Helper.java exists, remove it so we can generate a fresh one we know will be up to date
		try{
			if(!helperFile.exists()){
				System.out.println("Helper.java does NOT exist, nothing to clear out.");
			}
			else{
				if(helperFile.delete()){
					System.out.println(helperFile.getName() + " is deleted!  We just made room to create a fresh one");
				}else{
					System.out.println("Delete operation is failed.");
					return "<font color='red'>ERROR: Delete operation of Helper.java has failed.  The file [" + rootPath + "Helper.java" + "] might still exist.  New project NOT setup/created!!!</font>";
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return "<font color='red'>ERROR in the delete process of original Helper.java file.  System error: " + e + "</font>";
		}
		
		HelperFileCreator.createHelperClassViaParser(rootPath, "com.example.domain");
		ActionsFileCreator.createActionsClassViaParser(rootPath, "com.example.domain");
		return "SUCCESS";
	}
	
	public String processNewInstruction(
			@PathParam("keyword") String keywordkey,
			@PathParam("className") String className,
			@PathParam("testCaseName") String testCaseName,
			@PathParam("inputArrayXML") String inputArrayXML) {
		inputArrayXML = decodeURLComponent(inputArrayXML);
		
		ArrayList<String> inputs = xmlParser.getInstructionInputsFromXMLDoc(inputArrayXML);
		System.out.println("Processing New Instruction - Keyword: " + keywordkey
				+ ", inputArray: " + inputs + ", className=" + className);

		Keyword keyword = factory.createKeyword(keywordkey);
		String testPath = Constants.APP_UNDER_TEST__TEST_FILE_PATH + className + ".java";
		Instruction instruction = new Instruction();
		instruction.setKeyword(keyword);
		instruction.setInputs(inputs);
		JavaClass testClass = null;
		
		//Some Keywords are now Direct Process, and some get added via Method Calls
		if(KEYWORD_PROCESS_TYPES.DirectProcess.equals(keyword.processType())){
			if(keyword.shortName().equals(KEYWORD_KEYS.BeginClass)){
				//BeginClass will Create a new testClass, so it shouldn't exist yet, clear it out if it does
				//TODO: If it exists already, we should throw up a warning and ask the user if they want it cleared/overriden or left as is.
				File file = new File(testPath);
				try{
					if(!file.exists()){
						//We're good in this case.  File does not exist, BeginClass will create it
					}
					else if(file.delete()){
						//For now, auto delete the class if it already exists.
						System.out.println("We already had a test class called [" + className + "], we just removed it so we could make a new one.");
					}else{
						System.out.println("ERROR: We already had a test class called [" + className + "], we TRIED to remove it, but found an error.  Old class may still exist.");
						return "ERROR: Could not delete existing class named [" + className + "], could therefore not create the new class.";
					}
				} catch(Exception e){
					System.out.println("ERROR: In determining if New Test Suite named [" + className + "] existed, and/or if we deleted it.");
					return "ERROR: Could not create new class.";
				}
			}
			else {
				//Any other DirectProcess besides BeginClass will need the file to already exist!
				testClass = Utility.getJavaClass(className);
				if(testClass == null)
				{
					System.out.println("Error in trying to get the testClass File for DirectProcess of keyword.");
					return "ERROR - Did not process keyword: " + keyword.shortName();
				}
			}
			
			return keyword.performKeyword(testClass, testCaseName, inputs);
		}
		else if(KEYWORD_PROCESS_TYPES.UniqueProcess.equals(keyword.processType())){
			//Each UniqueProcess keyword needs to add specific code directly to the testcase itself
			System.out.println(keyword.shortName().toString() +"," + inputs.get(0) + ", " + testCaseName);
			switch (keyword.shortName().toString()) {
			case "CallAction":
				String actionName = inputs.get(0);
				return processCallActionKeyword(className, testCaseName, actionName);
			default:
				System.out.println("ERROR - Unknown keyword with UniqueProcess type: " + keyword.shortName().toString());
				return "ERROR - Unknown keyword with UniqueProcess type: " + keyword.shortName().toString();
			}
			
		}
		else{
			//All other Keywords get processed by adding a call to their HelperClassMethods into the test itself
			return addInstructionToTest(className, testCaseName, instruction);
		}
	}
		
	private String processCallActionKeyword(String className, String testName, String actionName) {
		JavaClass testClass = Utility.getJavaClass(className);
		if(testClass != null) {
			Method<JavaClass> currentMethod = testClass.getMethod(testName);
			if(currentMethod == null){
				System.out.println("Could not find current testCaseName method: " + testName + ", keyword NOT processed");
				return "ERROR - Did not find the testCase Named: " + testName + ", we could not process your last keyword: " + KEYWORD_KEYS.CallAction;
			}
		  
			String newStep = "Actions." + actionName + "(deploymentURL, browser);";
			currentMethod.setBody(currentMethod.getBody() + newStep);
		  
			addThrowsToMethodIfNeededForKeyword(new CallActionKeyword(), currentMethod); //In case we ever need to add some later.
		  
			return reWriteTestClassFile(className, testClass);
		}
		else{
			System.out.println("Error in trying to get the testClass File for a MethodCall keyword.");
			System.out.println("Keyword: " + KEYWORD_KEYS.CallAction + ", FAILED due to: testClass being null.");
			return "ERROR - Did not process keyword: " + KEYWORD_KEYS.CallAction;
		}
	}

	private String printOutArrayListAsList(ArrayList<String> list){
		String returnVal = "Arrays.asList(";
		for (String element : list) {
			if(element.contains(Constants.VARIABLE_INPUT_MARKER)){
				element = resolveVariables(element);
				returnVal = returnVal + element + ", ";
			}
			else {
				returnVal = returnVal + "\"" + element + "\", ";
			}
		}
		return returnVal.substring(0, returnVal.length() - 2) + ")"; //Remove last [, ] from the list
	}
	private String resolveVariables(String currInput) {
		String newInput = currInput;
		while(newInput.contains(Constants.VARIABLE_INPUT_MARKER)){
			int posOfFirstMarker = newInput.indexOf(Constants.VARIABLE_INPUT_MARKER);
			int posOfSecondMarker = newInput.indexOf(Constants.VARIABLE_INPUT_MARKER, posOfFirstMarker + 1);
			String beginning = newInput.substring(0, posOfFirstMarker);
			String var = newInput.substring(posOfFirstMarker + Constants.VARIABLE_INPUT_MARKER.length(), posOfSecondMarker);
			String end = newInput.substring(posOfSecondMarker + Constants.VARIABLE_INPUT_MARKER.length());
			//add quotes to the input as needed
			if(beginning.length() != 0){
				var = "\" + " + var;
				if(!beginning.startsWith("\"")){
					beginning = "\"" + beginning;
				}
			}
			if(end.length() != (newInput.length() - Constants.VARIABLE_INPUT_MARKER.length())){
				var += " + \"";
				if(!end.endsWith("\"")){
					end += "\"";
				}
			}
			newInput = beginning + var + end;
		}
		return newInput;
	}
	
	private String addInstructionToTest(String className, String testName, Instruction instruction){
		System.out.println("Adding Instruction to testcase: " + instruction +"\n To test: " + testName);
		
		JavaClass testClass = Utility.getJavaClass(className);
		Keyword keyword = instruction.getKeyword();
		ArrayList<String> inputs = instruction.getInputs();
		if(testClass != null) {
		  Method<JavaClass> currentMethod = testClass.getMethod(testName);
		  if(currentMethod == null){
		    System.out.println("Could not find current testCaseName method: " + testName + ", keyword NOT processed");
		    return "ERROR - Did not find the testCase Named: " + testName + ", we could not process your last keyword: " + keyword.shortName();
		  }
		  
		  String newStep = "";
		  if(instruction.getNonConformingCodeLine() == null || instruction.getNonConformingCodeLine().equals("")){
			  if(keyword instanceof VariableKeywordInterface){
				  newStep = ((VariableKeywordInterface) keyword).determineNewLine(instruction.getInputs());
			  } else {
				  newStep = "Helper." + keyword.shortName() + "(browser, " + printOutArrayListAsList(inputs) + keyword.additionalInputParams() + ");";
			  }
			  addThrowsToMethodIfNeededForKeyword(keyword, currentMethod);
		  } else {
			  newStep = instruction.getNonConformingCodeLine();
		  }
		  currentMethod.setBody(currentMethod.getBody() + newStep);
		  
		  return reWriteTestClassFile(className, testClass);
		}
		else{
		  System.out.println("Error in trying to get the testClass File for a MethodCall keyword.");
		  System.out.println("Keyword: " + keyword.shortName() + ", FAILED due to: testClass being null.");
		  return "ERROR - Did not process keyword: " + keyword.shortName();
		}
	}
	private void addThrowsToMethodIfNeededForKeyword(Keyword keyword, Method<JavaClass> currentMethod) {
		if(keyword.addThrowsToTest() != null){
		    //This keyword needs to add certain Exceptions onto the throws of our current test
		    for (Class<? extends Exception> exceptionClassToAdd : keyword.addThrowsToTest()) {
		    	@SuppressWarnings("unused")
		    	List<String> throwsClasses = currentMethod.getThrownExceptions();
		    	if(Utility.isExceptionAlreadyThrown(currentMethod, exceptionClassToAdd) == false){
		    		currentMethod.addThrows(exceptionClassToAdd);
		    	}
		    }
		}
	}
	private String reWriteTestClassFile(String className, JavaClass testClass) {
		try {
			PrintStream writetoTest = new PrintStream(new FileOutputStream(
		        Constants.APP_UNDER_TEST__TEST_FILE_PATH + className + ".java"));
		    writetoTest.print(Formatter.format(testClass)); //TODO: This doesn't work, low priority to fix
		    writetoTest.close();
		} catch (Exception e) {
		    System.err.println("Failure in writing out the new file for processing this keyword.  Error: " + e);
		    return "ERROR: Could not process the last keyword.";
		}
		System.out.println("SUCCESS - Added new line of code to the test case");
		return "SUCCESS";
	}

	//TODO: This is a hack, find a better way of passing these "bad chars"
	private String decodeURLComponent(String url){
		String returnVal = url.replace("^^^***", "//");
		returnVal = returnVal.replace("^**^", "/");
		returnVal = returnVal.replace("*^*", "\\");//NOTE: We need one \ to escape, so this is replacing with only "\"
		returnVal = returnVal.replace("%20", " ");
		
		return returnVal;
	}
	
}