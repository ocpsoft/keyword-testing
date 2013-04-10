package org.ocpsoft.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.util.Formatter;
import org.ocpsoft.keywords.Keyword;
import org.ocpsoft.keywords.Keyword.KEYWORD_PROCESS_TYPES;
import org.ocpsoft.keywords.KeywordFactory;

import com.ocpsoft.projectStarter.HelperFileCreator;
import com.ocpsoft.utils.Constants;
import com.ocpsoft.utils.Constants.KEYWORD_KEYS;
import com.ocpsoft.utils.Utility;

@Path("/webService")
@Stateful
@RequestScoped
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class MyWebService {

	@Inject KeywordFactory factory;
	String className = "";//Should be re-assigned on BeginClassKeyword
	
	@GET
	@Path("/{message}")
	public String echoService(@PathParam("message") String message) {
		System.out.println("GET on message");
		return message;
	}


	@GET
	@Path("/AllKeywordTypes")
	public String getAllKeywordtypes() {
		System.out.println("Get on all Keyword Types");
		return factory.getAllKeywordTypes().toString();
	}
	
	@GET
	@Path("/RunBuild")
	public String runBuild() {
		System.out.println("Run Build Request");
		// Java code to run the build
		boolean isSuccessful = false;
		String testLine = "";
		try {
			Process p;
			if(Constants.ROOT_FILE_PATH.startsWith("D:")){
				//Currently on WINDOWS development
				p = Runtime.getRuntime().exec("cmd.exe /C mvn test -PJBOSS_AS_REMOTE_7.X -f D:/_DEVELOPMENT_/projects/AppUnderTest/pom.xml");
			}
			else {
				//Currently on UNIX development
				p = Runtime.getRuntime().exec(
						"mvn test -PJBOSS_AS_REMOTE_7.X -f /home/fife/workspace/AppUnderTest/pom.xml");
			}
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
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

		} catch (IOException e1) {
		} catch (InterruptedException e2) {
		}

		System.out.println("Done");

		if (isSuccessful) {
			return "Build SUCCESSFUL!!!" + "<BR />" + testLine;
		} else {
			return "Build Failed... Check Console for error" + "<BR />"
					+ testLine;
		}
	}

	@GET
	@Path("/TestSuite/{className}")
	public String getTestSuite(@PathParam("className") String className) {
		String classPath = Constants.ROOT_FILE_PATH + className + ".java";
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

	@POST
	@Path("/MoveTestStep/{className}/{testCaseName}/{stepNumber}/{direction}")
	public String moveTestStep(@PathParam("className") String className,
			@PathParam("testCaseName") String testCaseName,
			@PathParam("stepNumber") int stepNumber,
			@PathParam("direction") String direction) {
		String completePath = Constants.ROOT_FILE_PATH + className + ".java";
		System.out.println("Moving Step [" + stepNumber + "] in test {" + testCaseName + "} dirction: " + direction);

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
						if(stepNumber <= 0){
							return "ERROR: Can not move the first step up.";
						}
						String tempStep = steps[stepNumber - 1];
						steps[stepNumber-1] = steps[stepNumber];
						steps[stepNumber] = tempStep;
					}
					String newBody = "";
					for (String step : steps) {
						newBody+=step + ";";
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
	
	@GET
	@Path("/ListOfTestMethodNames/{className}")
	public String getListOfTestMethodNames(@PathParam("className") String className) {
		String classPath = Constants.ROOT_FILE_PATH + className + ".java";
		System.out.println("Getting List of all Tests in Suite: " + classPath);

		try{
			File testClassFile = new File(Constants.ROOT_FILE_PATH + className + ".java");
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

	@POST
	@Path("/DeleteTestSuite/{className}")
	public String deleteTestSuite(@PathParam("className") String className) {
		String rootPath = Constants.ROOT_FILE_PATH;
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

	@POST
	@Path("/ExportTestCase/{className}/{testName}")
	public String exportTestCase(@PathParam("className") String className, @PathParam("testName") String testName) {
		String returnString = "";
		String rootPath = Constants.EXPORT_FILE_PATH;
		String fullFilePath = rootPath + testName + ".txt";
		System.out.println("Exporting Test Case: " + fullFilePath);

		File file = new File(fullFilePath);
		String content = getTestMethodCode(className, testName);
		content = FormatCodeForExportCSV(content);
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
		JavaClass testClass = null;
		testClass = javaClassExists(testClass, className);
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
	private String FormatCodeForExportCSV(String content){
		String returnString = "";
		if(content == null){
			return returnString;
		}
		content=content.replace("\n", "");
		content=content.replace("; ", ";");
		String[] statements = content.split(";");
		returnString+="Keyword,Inputs\n";
		for (String statement : statements) {
			if(!Utility.isEmptyStep(statement)){
				returnString+= getKeywordAndInputsFromCodeCallToHelperMethod(statement) + "\n";
			}
		}
		
		return returnString;
	}
	private String getKeywordAndInputsFromCodeCallToHelperMethod(String statement){
		String returnString = "";
		
		/*Sample statement:
		 * 		Helper.OpenBrowser(browser, Arrays.asList("index.jsp", "assigned_null",
				"assigned_null", "assigned_null"), deploymentURL);
		 */
		String keyword = statement.substring("Helper.".length(), statement.indexOf("("));
		String inputs = statement.substring(statement.indexOf("asList(") + "asList(".length(), statement.indexOf(")"));
		inputs = inputs.replace("\"", "");
		inputs = "[" + inputs + "]";
		returnString += keyword + "," + inputs;
		
		return returnString;
	}
	
	@POST
	@Path("StartNewProject")
	public String startNewProject(){
		//TODO: This should really create a totally new project, for now we'll just keep re-using AppUnderTest
		String rootPath = Constants.ROOT_FILE_PATH;
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
		return "SUCCESS";
	}
	
	@POST
	@Path("/NewInstruction/{keyword}/{className}/{testCaseName}/{inputArray}")
	public String processNewInstruction(
			@PathParam("keyword") String keywordkey,
			@PathParam("className") String className,
			@PathParam("testCaseName") String testCaseName,
			@PathParam("inputArray") String[] inputArray) {
		
		ArrayList<String> inputs = filterInputArrayIntoArraList(inputArray);
		System.out.println("Processing New Instruction - Keyword: " + keywordkey
				+ ", inputArray: " + inputs + ", className=" + className);

		Keyword keyword = factory.createKeyword(keywordkey);
		String testPath = Constants.ROOT_FILE_PATH + className + ".java";
		JavaClass testClass = null;
		String error = "";
		
		//Some Keywords are now Direct Process, and some get added via Method Calls
		if(KEYWORD_PROCESS_TYPES.DirectProcess.equals(keyword.getProcessType())){
			if(keyword.getShortName().equals(KEYWORD_KEYS.BeginClass.toString())){
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
				testClass = javaClassExists(testClass, className);
				if(testClass == null)
				{
					System.out.println("Error in trying to get the testClass File for DirectProcess of keyword.");
					return "ERROR - Did not process keyword: " + keyword.getShortName();
				}
			}
			
			return keyword.performKeyword(testClass, inputs);
		}
		else{
			//All other Keywords get processed by adding a call to their HelperClassMethods into the test itself
			testClass = javaClassExists(testClass, className);
			if(testClass != null) {
				Method<JavaClass> currentMethod = testClass.getMethod(testCaseName);
				if(currentMethod == null){
					System.out.println("Could not find current testCaseName method: " + testCaseName + ", keyword NOT processed");
					return "ERROR - Did not find the testCase Named: " + testCaseName + ", we could not process your last keyword: " + keyword.getShortName();
				}
				
				String newStep = "Helper." + keyword.getShortName() + "(browser, " + printOutArrayListAsList(inputs) + keyword.getAdditionalInputParams() + ");";
				currentMethod.setBody(currentMethod.getBody() + newStep);
				
				//Now re-write the actual File with the updates to the class file
				try {
					PrintStream writetoTest = new PrintStream(new FileOutputStream(
							Constants.ROOT_FILE_PATH + className + ".java"));
					writetoTest.print(Formatter.format(testClass)); //TODO: This doesn't work, low priority to fix
					writetoTest.close();
				} catch (Exception e) {
					System.err.println("Failure in writing out the new file for processing this keyword.  Error: " + e);
					return "ERROR: Could not process the last keyword.";
				}
				System.out.println("SUCCESS - Added method call for " + keyword.getShortName());
				return "SUCCESS";
			}
			else{
				System.out.println("Error in trying to get the testClass File for a MethodCall keyword.");
				System.out.println("Keyword: " + keyword.getShortName() + ", FAILED due to: " + error);
				return "ERROR - Did not process keyword: " + keyword.getShortName();
			}
		}
	}
	
	private JavaClass javaClassExists(JavaClass testClass, String className){
		try {
			File testClassFile = new File(Constants.ROOT_FILE_PATH + className + ".java");
			testClass = (JavaClass) JavaParser.parse(testClassFile);
		} catch (Exception e) {
			System.out.println("Error in trying to get the testClass File for Processing a keyword: " + e);
			return null;
		}
		return testClass;
	}
	
	private ArrayList<String> filterInputArrayIntoArraList(String[] inputArray){
		//For some reason, REST is passing the inputArray as [array], need to treat element(0) as the actual inputArray
		String[] tempArray = inputArray[0].split(", ");
		try {
			for (int i = 0; i < tempArray.length; i++) {
				tempArray[i] = decodeURLForBadChars(tempArray[i]);
			}
		} catch (Exception e) {
			System.out.println("ERROR: Exception in Encoding URL inputs!");
		}
		ArrayList<String> inputs = new ArrayList<String>(tempArray.length);
		for (String s : tempArray) {  
			inputs.add(s);  
		}
		
		return inputs;
	}
		
	private String printOutArrayListAsList(ArrayList<String> list){
		String returnVal = "Arrays.asList(";
		for (String element : list) {
			//TODO:Find a better way of passing variables, for now, do it with a simple replace
			if(element.startsWith(Constants.VARIABLE_INPUT_PREFIX)){
				//This is a variable, we want to preserve it with no quotes
				returnVal = returnVal + element.substring(Constants.VARIABLE_INPUT_PREFIX.length()) + ", ";
			}
			else {
				returnVal = returnVal + "\"" + element + "\", ";
			}
		}
		return returnVal.substring(0, returnVal.length() - 2) + ")";
	}
	
	//TODO: This is a hack, find a better way of passing these "bad chars"
	private String decodeURLForBadChars(String url){
		String returnVal = url.replace("&&&***", "//");
		returnVal = returnVal.replace("&**&", "/");
		return returnVal;
	}
	
}