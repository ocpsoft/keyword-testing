package org.ocpsoft.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.ocpsoft.keywords.Keyword;
import org.ocpsoft.keywords.KeywordFactory;

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
		String rootPath = "/home/fife/workspace/AppUnderTest/src/test/java/com/example/domain/";
		System.out.println("Getting Test Suite: " + rootPath + className);

		File file = new File(rootPath + className);
		if(file.exists()) { 
			int ch;
			StringBuffer strContent = new StringBuffer("");
			FileInputStream fin = null;
			try {
				fin = new FileInputStream(file);
				while ((ch = fin.read()) != -1)
					strContent.append((char) ch);
				fin.close();
			} catch (Exception e) {
				System.out.println(e);
			}
			System.out.println(strContent.toString());
			return "<pre>" + strContent.toString() + "</pre>";
		}
		else {
			System.out.println("ERROR:Requested Class does not exist!!!");
			return "<font color='red'>ERROR: No such file Exists.  Could not grab Test Suite: " + className + "</font>";
		}
	}

	@POST
	@Path("/DeleteTestSuite/{className}")
	public String deleteTestSuite(@PathParam("className") String className) {
		String rootPath = "/home/fife/workspace/AppUnderTest/src/test/java/com/example/domain/";
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
	@Path("/NewInstruction/{keyword}/{className}/{input1}/{input2}/{input3}/{input4}")
	public String processNewInstruction(
			@PathParam("keyword") String keywordkey,
			@PathParam("className") String className,
			@PathParam("input1") String input1,
			@PathParam("input2") String input2,
			@PathParam("input3") String input3,
			@PathParam("input4") String input4) {
		try {
			input1 = decodeURLForBadChars(input1);
			input2 = decodeURLForBadChars(input2);
			input3 = decodeURLForBadChars(input3);
			input4 = decodeURLForBadChars(input4);
		} catch (Exception e) {
			System.out.println("ERROR: Exception in Encoding URL inputs!");
		}
		System.out.println("Processing New Instruction - Keyword: " + keywordkey
				+ ", input1: " + input1 + ", input2: " + input2 + ", className=" + className);
		String value = "";

		Keyword keyword = factory.createKeyword(keywordkey);
		String testPath = "/home/fife/workspace/AppUnderTest/src/test/java/com/example/domain/" + className + ".java";
		return keyword.addInstruction(testPath, input1, input2, input3, input4, value);
	}
	
	//TODO: This is a hack, find a better way of passing these "bad chars"
	private String decodeURLForBadChars(String url){
		return url.replace("&&&***", "//");
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