package org.ocpsoft.individualTests;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Visibility;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.dataBeans.Instruction;
import org.ocpsoft.keywords.Keyword;
import org.ocpsoft.services.MyWebServiceInterface;

import com.ocpsoft.utils.ConfigXMLParser;
import com.ocpsoft.utils.Constants;
import com.thoughtworks.selenium.DefaultSelenium;

@RunWith(Arquillian.class)
public class ParserExampleTest
{

   	@Deployment(testable = false) //false = run as client
	public static WebArchive createDeployment() {
		return ShrinkWrap.create(WebArchive.class)
						.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	String webServiceTargetURL_hardcode = Constants.FRAMEWORK_LOCALHOST_URL + "rest";	
	
   	String packageLocation = getClass().getPackage().toString().substring("package ".length());
   	String packagePath = packageLocation.replace(".", "/");
	   
	@Test
	public void testParser() throws InterruptedException {
		String rootPath = Constants.KEYWORD_PROJECT_ROOT_FILE_PATH + "src/test/java/" + packagePath + "/";
		String className = "ParseTest";
		removeClassFile(rootPath + className + ".java");
		createTestClassViaParser(rootPath + className + ".java", className);
		File testClassFile = new File(rootPath + className + ".java");
		JavaClass testClass = null;
		try {
			testClass = (JavaClass) JavaParser.parse(testClassFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Assert.assertTrue("Error in testParser: " + e, false);
		}
		List<Member<JavaClass, ?>> allMembers = testClass.getMembers();
		Assert.assertTrue("Could not find all of: static var, 2 other vars, and getDeployment method.", allMembers.size() == 4);
		removeClassFile(rootPath + className + ".java");
	}
	private void createTestClassViaParser(String testPath, String className) {
		JavaClass testClass = JavaParser.create(JavaClass.class);
	
	      testClass
	               .setName(className)
	               .setPackage(packageLocation)
	               .addAnnotation(RunWith.class).setLiteralValue(Arquillian.class.getSimpleName() + ".class")
	               .getOrigin().addImport(Arquillian.class);
	      
	      testClass
	      		.addField().setPrivate()
	      		.setStatic(true).setFinal(true)
	      		.setType(String.class)
	      		.setName("WEBAPP_SRC");
	      
	      testClass.getField("WEBAPP_SRC")
	      		.setLiteralInitializer("\"src/main/webapp\"");
	
	      testClass.addMethod()
	               .setName("getDeployment")
	               .setStatic(true)
	               .setVisibility(Visibility.PUBLIC)
	               .setReturnType(WebArchive.class)
	               .setBody("return ShrinkWrap.create(WebArchive.class)\n" +
						"\t.addAsResource(\"META-INF/persistence.xml\")\n" +
						"\t.addAsWebResource(new File(WEBAPP_SRC, \"index.html\"))\n" +
						"\t.addAsWebResource(new File(WEBAPP_SRC, \"index2.html\"))\n" +
						"\t.addAsWebResource(new File(WEBAPP_SRC, \"myInfo.html\"))\n" +
						"\t.addAsWebResource(new File(WEBAPP_SRC, \"friendsInfo.html\"))\n" +
						"\t.addAsWebInfResource(EmptyAsset.INSTANCE, \"beans.xml\");")
	               .addAnnotation(Deployment.class)
	               .getOrigin().addImport(File.class);
	      testClass.addImport(WebArchive.class);
	      testClass.addImport(ShrinkWrap.class);
	      testClass.addImport(EmptyAsset.class);
	      testClass.addImport(DefaultSelenium.class);
	      
	      testClass
	      		.addField().setType(DefaultSelenium.class)
	      		.setName("browser").addAnnotation(Drone.class)
	      		.getOrigin().addImport(DefaultSelenium.class);
	      testClass.addImport(Drone.class);
	      
	      testClass
	    		.addField().setType(URL.class)
	    		.setName("deploymentURL").addAnnotation(ArquillianResource.class)
	    		.getOrigin().addImport(URL.class);
	      testClass.addImport(ArquillianResource.class);
	      
			try{
				PrintStream writetoTest = new PrintStream(
					     new FileOutputStream(testPath)); 
				writetoTest.print(testClass.toString());
				writetoTest.close();
			}
			catch (Exception e) {
				System.err.println("Failure in createTestClassViaParser: " + e);
			}
	}
	public static void removeClassFile(String filePath){
		File classFile = new File(filePath);
		try{
			if(!classFile.exists()){
				System.out.println(filePath + " does NOT exist.  Nothing to Remove");
			}
			else{
				if(classFile.delete()){
					System.out.println(classFile.getName() + " is deleted!");
				}else{
					System.out.println("Delete operation is failed.");
					assertTrue(false);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	

	@Test
	public void testXMLParserGetInstruction() throws InterruptedException {
		//Basic Keyword
		ArrayList<String> expectedInputs = new ArrayList<>();
		expectedInputs.add("index.jsp");
		Keyword expectedKeyword = generateKeyword("OpenBrowser");
		String xmlDoc = generateInstructionXMLDoc("OpenBrowser", expectedInputs);
		Instruction i = generateInstructionObject(xmlDoc);
		
		Assert.assertTrue(expectedKeyword.getClass().equals(i.getKeyword().getClass()));
		Assert.assertTrue(expectedInputs.equals(i.getInputs()));
		
		//More complicated Keyword
		expectedInputs = new ArrayList<>();
		expectedInputs.add("Selected Value should be Begin New Suite");
		expectedInputs.add("select");
		expectedInputs.add("//select[@id='keyword']");
		expectedInputs.add("Begin New Suite");
		xmlDoc = generateInstructionXMLDoc("VerifyObjectProperty", expectedInputs);
		i = generateInstructionObject(xmlDoc);
		expectedKeyword = generateKeyword("VerifyObjectProperty");
		Assert.assertTrue(expectedKeyword.getClass().equals(i.getKeyword().getClass()));
		Assert.assertTrue(expectedInputs.equals(i.getInputs()));
	}
	private MyWebServiceInterface generateService(){
		MyWebServiceInterface service = ProxyFactory.create(MyWebServiceInterface.class, webServiceTargetURL_hardcode);
		return service;
	}
	private Keyword generateKeyword(String shortName) {
        MyWebServiceInterface service = generateService();
        Keyword keyword = service.generateKeyword(shortName);
		return keyword;
	}
	private ArrayList<Instruction> generateInstructionSet(String xmlDoc) {
		xmlDoc = encodeURLComponent(xmlDoc);
        MyWebServiceInterface service = generateService();
        ArrayList<Instruction> instructions = service.generateInstructionSetFromXMLDoc(xmlDoc);
		return instructions;
	}
	private Instruction generateInstructionObject(String xmlDoc) {
		xmlDoc = encodeURLComponent(xmlDoc);
		MyWebServiceInterface service = generateService();
        Instruction instruction = service.generateInstructionFromXMLDoc(xmlDoc);
		return instruction;
	}
	

	@Test
	public void testXMLParserGetInputs() throws InterruptedException {
		//Basic Input List
		ConfigXMLParser xmlParser = new ConfigXMLParser();
		ArrayList<String> expectedInputs = new ArrayList<>();
		expectedInputs.add("index.jsp");
		String xmlDoc = xmlParser.generateInputsXMLDoc(expectedInputs);
		ArrayList<String> list = xmlParser.getInstructionInputsFromXMLDoc(xmlDoc);
		Assert.assertTrue(expectedInputs.equals(list));
		
		//More complicated Input List
		expectedInputs = new ArrayList<>();
		expectedInputs.add("Selected Value should be Begin New Suite");
		expectedInputs.add("select");
		expectedInputs.add("//select[@id='keyword']");
		expectedInputs.add("Begin New Suite");
		xmlDoc = xmlParser.generateInputsXMLDoc(expectedInputs);
		list = xmlParser.getInstructionInputsFromXMLDoc(xmlDoc);
		Assert.assertTrue(expectedInputs.equals(list));
	}
	
	@Test
	public void testXMLParserGetInstructionSet() throws InterruptedException {
		ArrayList<String> expectedInputs1 = new ArrayList<String>();
		expectedInputs1.add("index.jsp");
		
		Keyword expectedKeyword1 = generateKeyword("OpenBrowser");
		
		ArrayList<String> expectedInputs2 = new ArrayList<String>();
		expectedInputs2.add("Selected Value should be Begin New Suite");
		expectedInputs2.add("select");
		expectedInputs2.add("//select[@id='keyword']");
		expectedInputs2.add("Begin New Suite");
		Keyword expectedKeyword2 = generateKeyword("VerifyObjectProperty");

		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("OpenBrowser");
		keywords.add("VerifyObjectProperty");
		ArrayList<ArrayList<String>> inputs = new ArrayList<ArrayList<String>>();
		inputs.add(expectedInputs1);
		inputs.add(expectedInputs2);
		String xmlDoc = generateInstructionSetXMLDoc(keywords, inputs);
		ArrayList<Instruction> instructions = generateInstructionSet(xmlDoc);
		Assert.assertTrue(instructions.size() == 2);
		Assert.assertTrue(expectedKeyword1.getClass().equals(instructions.get(0).getKeyword().getClass()));
		Assert.assertTrue(expectedKeyword2.getClass().equals(instructions.get(1).getKeyword().getClass()));
		Assert.assertTrue(expectedInputs1.equals(instructions.get(0).getInputs()));
		Assert.assertTrue(expectedInputs2.equals(instructions.get(1).getInputs()));
	}
	
	
	//NOTE: For now these are simply copied from ConfigXMLParser for ease of use in the test.
	/********* BEGIN Method for generating the XML Documents*********/
	public String generateInstructionSetXMLDoc(ArrayList<String> keywords, ArrayList<ArrayList<String>> inputs){
		ConfigXMLParser parser = new ConfigXMLParser();
		String xmlDoc = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<" + parser.INSTRUCTION_SET_XML_TAG + ">\n";
		for (int x=0; x < keywords.size(); x++) {
			xmlDoc += generateInstructionXMLDoc(keywords.get(x), inputs.get(x)) + "\n";
		}
		xmlDoc += "</" + parser.INSTRUCTION_SET_XML_TAG + ">";
		return xmlDoc;
	}
	public String generateInstructionXMLDoc(String keyword, ArrayList<String> inputs) {
		ConfigXMLParser parser = new ConfigXMLParser();
		String xmlDoc = "\t<" + parser.INSTRUCTION_XML_TAG + ">\n" +
				"\t\t<" + parser.KEYWORD_XML_TAG + ">" + keyword + "</" + parser.KEYWORD_XML_TAG + ">\n" +
				generateInputsXMLDoc(inputs) +
				"\n\t</" + parser.INSTRUCTION_XML_TAG + ">";
		return xmlDoc;
	}
	public String generateInputsXMLDoc(ArrayList<String> inputs) {
		ConfigXMLParser parser = new ConfigXMLParser();
		String xmlDoc = "\t\t<" + parser.INPUTS_LIST_XML_TAG + ">\n";
		for (String input : inputs) {
			xmlDoc += "\t\t\t<" + parser.INPUT_XML_TAG + ">" + input + "</" + parser.INPUT_XML_TAG + ">\n";
		}
		xmlDoc += "\t\t</" + parser.INPUTS_LIST_XML_TAG + ">";
		return xmlDoc;
	}
	/********* END Method for generating the XML Documents*********/
	
	//TODO: This is a hack, find a better way of passing these "bad chars"
	private static String encodeURLComponent(String url){
		String returnVal = url.replace("//", "^^^***");
		returnVal = returnVal.replace("/", "^**^");
		returnVal = returnVal.replace("\\", "*^*");//NOTE: We need one \ to escape, so this is replacing with only "\"
		returnVal = returnVal.replace("\t", "").replace("\n", "").replace(" ", "%20");
		
		return returnVal;
	}
}
