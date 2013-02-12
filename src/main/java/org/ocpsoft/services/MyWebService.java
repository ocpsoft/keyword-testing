package org.ocpsoft.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
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
			Process p = Runtime.getRuntime().exec(
					"mvn test -PJBOSS_AS_REMOTE_7.X -f /home/fife/workspace/AppUnderTest/pom.xml");
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
			List<Member<JavaClass, ?>> allMembers = testClass.getMembers();
			String returnVal = "<font color='green'>Test Suite Named: " + className + "</font>";
			for (Member<JavaClass, ?> member : allMembers) {
				if(Utility.memberIsATestCase(member)){
					returnVal += "<P /><BR />" + Utility.generateUserReadableStepsFromMethod(member);
				}
				else {/*It's probably a variable or something, not an actual test method, we don't want that*/}
			}
			return returnVal;
		} catch (FileNotFoundException e1) {
			System.out.println("ERROR:Requested Class does not exist!!!");
			return "<font color='red'>ERROR: No such file Exists.  Could not grab Test Suite: " + className + "</font>";
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
			return returnVal.substring(0, returnVal.length() - 1); //Remove the last ","
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
	
	@Deprecated
	@GET
	@Path("/CopyTestIntoProject/{sourceFilePath}/{projectPath}/{testClass}")
	public String copyTestIntoProject(
			@PathParam("sourceFilePath") String sourceFilePath,
			@PathParam("projectPath") String projectPath,
			@PathParam("testClass") String testClass) {
		String rootPath = "/home/fife/workspace/";
		System.out.println("Moving TestClass: " + rootPath + testClass
				+ " into Project: " + rootPath + projectPath);
		File sourceFile = new File(rootPath + sourceFilePath);
		File destFile = new File(rootPath + projectPath
				+ "/src/test/java/com/example/domain/" + testClass);
		try {
			return copyAndOverrideFile(sourceFile, destFile);
		} catch (Exception e) {
			System.err.println("Failure in CopyTestIntoProject: " + e);
			return "Failure";
		}
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
	
	private static String copyAndOverrideFile(File sourceFile, File destFile)
			throws IOException {
		if (!sourceFile.exists()) {
			return "No Source File Found, No copy was done";
		}
		if (!destFile.exists()) {
			destFile.createNewFile();
		}
		FileChannel source = null;
		FileChannel destination = null;
		source = new FileInputStream(sourceFile).getChannel();
		destination = new FileOutputStream(destFile).getChannel();
		if (destination != null && source != null) {
			destination.transferFrom(source, 0, source.size());
		}
		if (source != null) {
			source.close();
		}
		if (destination != null) {
			destination.close();
		}

		return "Success";
	}

}