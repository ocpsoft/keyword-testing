package org.ocpsoft.services;

import java.util.ArrayList;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.ocpsoft.dataBeans.Instruction;
import org.ocpsoft.keywords.Keyword;

@Path("/webService")
@Stateful
@RequestScoped
@Consumes({ "application/json" })
@Produces("application/xml")
public interface MyWebServiceInterface {

	String className = "";//Should be re-assigned on BeginClassKeyword
	
	@GET
	@Path("/{message}")
	public String echoService(@PathParam("message") String message);

	@GET
	@Produces("application/json") 
	@Path("/GetKeywordObject/{shortName}")
	public Keyword generateKeyword(@PathParam("shortName") String shortName);

	@GET
	@Produces("application/json")
	@Path("/GetInstructionObjectFromXMLDoc/{xmlDoc}")
	public Instruction generateInstructionFromXMLDoc(@PathParam("xmlDoc") String xmlDoc);

	@GET
	@Produces("application/json")
	@Path("/GetInstructionSetFromXMLDoc/{xmlDoc}")
	public ArrayList<Instruction> generateInstructionSetFromXMLDoc(@PathParam("xmlDoc") String xmlDoc);
	
	@GET
	@Path("/AllKeywordTypes")
	public String getAllKeywordtypes();
	
	@GET
	@Path("/RunBuild")
	public String runBuild();

	@GET
	@Path("/TestSuite/{className}")
	public String getTestSuite(@PathParam("className") String className);

	@POST
	@Path("/MoveTestStep/{className}/{testCaseName}/{stepNumber}/{direction}")
	public String moveTestStep(@PathParam("className") String className,
			@PathParam("testCaseName") String testCaseName,
			@PathParam("stepNumber") int stepNumber,
			@PathParam("direction") String direction);
	
	@GET
	@Path("/ListOfTestMethodNames/{className}")
	public String getListOfTestMethodNames(@PathParam("className") String className);

	@POST
	@Path("/DeleteTestSuite/{className}")
	public String deleteTestSuite(@PathParam("className") String className);
	
	@POST
	@Path("/ExportTestToAction/{testClassName}/{testName}/{actionName}")
	public String exportTestToAction(@PathParam("testClassName") String testClassName, 
			@PathParam("testName") String testName, 
			@PathParam("actionName") String actionName);
	
	@POST
	@Path("/ExportTestCase/{className}/{testName}")
	public String exportTestCase(@PathParam("className") String className, @PathParam("testName") String testName);
	
	@POST
	@Path("/ImportTestSteps/{className}/{testName}/{importFile}")
	public String importTestSteps(@PathParam("className") String className, 
			@PathParam("testName") String testName, @PathParam("importFile") String importFilePath);
	
	@POST
	@Path("StartNewProject")
	public String startNewProject();
	
	@POST
	@Path("/NewInstruction/{keyword}/{className}/{testCaseName}/{inputArrayXML}")
	public String processNewInstruction(
			@PathParam("keyword") String keywordkey,
			@PathParam("className") String className,
			@PathParam("testCaseName") String testCaseName,
			@PathParam("inputArrayXML") String inputArrayXML);
		
}