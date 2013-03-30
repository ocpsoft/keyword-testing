<!DOCTYPE html>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://www.facebook.com/2008/fbml">
   <head>
       <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
       <title>Welcome to The Keyword Framework</title>
   </head>
   <body>

		<%-- scriptlets --%>
		<%@ page import="com.ocpsoft.utils.Constants" %>

		
		<!-- Here are all the descriptions<BR /> -->
		<script type="text/javascript">
   		var curKeyword = "";
   		var keywordDescMap = {};
   		var keywordValueMap = {};
   		var MAX_NUMBER_OF_KEYWORD_DESCRIPTIONS = 0;
    	</script>
		<%
		List<String> descriptions = null;
		for (Map.Entry<Constants.KEYWORD_KEYS, List<String>> keywordDesc : Constants.KEYWORD_DESCRIPTIONS.entrySet()) {
			Constants.KEYWORD_KEYS keyword = keywordDesc.getKey();
			descriptions = keywordDesc.getValue();
		%>
			<!-- UNCOMMENT TO LIST ALL KEYWORDS -->
			<%-- <P />Keyword: <%=keyword %><BR /> --%>
	    	<script type="text/javascript">
    		var curDescList = "";
    		curKeyword = "<%=keyword%>";
	    	</script>
		    <%
		    for (int i = 0; i < descriptions.size(); i++) {
		    %>
		    	<script type="text/javascript">
		    	curDescList = curDescList + "<%=descriptions.get(i)%>" + ", ";
		    	</script>		    	
		    <%
		    }
		    %>
	    	<script type="text/javascript">
	    	keywordDescMap[curKeyword] = curDescList.substring(0, curDescList.length - 2);
		   	</script>
   		<%
   		}
   		%><!-- <P />Here are all the values<BR /> --><%
 		for (Map.Entry<Constants.KEYWORD_KEYS, List<String>> keywordValue : Constants.KEYWORD_VALUES.entrySet()) {
 			Constants.KEYWORD_KEYS keyword = keywordValue.getKey();
 			List<String> values = keywordValue.getValue();
   		%>
	    	<script type="text/javascript">
    		var curValueList = "";
    		curKeyword = "<%=keyword%>";
    		var tempMaxKeywordDescriptions = 0;
	    	</script>
    	
	    	<!-- UNCOMMENT TO LIST ALL KEYWORD INPUT VALUES -->
	    	<%-- <P />Keyword: <%=keyword %><BR /> --%>
    	
	    	<%
	    	for (int i = 0; i < values.size(); i++) {
	    	%>
		    
			    <!-- UNCOMMENT TO LIST ALL KEYWORD INPUT VALUES -->
			    <%-- <%=i%>: <script type="text/javascript">
				var descList = keywordDescMap[curKeyword].split(",");
			    document.write(descList[<%=i%>]);</script><BR />
			    _______ <%=values.get(i)%><BR /> --%>
		    
		    
		    	<script type="text/javascript">
	    		curValueList = curValueList + "<%=values.get(i)%>" + ", ";
	    		tempMaxKeywordDescriptions++;
		    	</script>		    	
	    	<%
		    }
		    %>
	    	<script type="text/javascript">
	    	keywordValueMap[curKeyword] = curValueList.substring(0, curValueList.length - 2); //Take off last ", "
	    	if(MAX_NUMBER_OF_KEYWORD_DESCRIPTIONS < tempMaxKeywordDescriptions){
	    		MAX_NUMBER_OF_KEYWORD_DESCRIPTIONS = tempMaxKeywordDescriptions
	    	}
		   	</script>
   		<%
   		}
   		%>
   		<!-- <script type="text/javascript">
	    document.write('MAX_NUMBER_OF_KEYWORD_DESCRIPTIONS : ' + MAX_NUMBER_OF_KEYWORD_DESCRIPTIONS);
		</script> -->
	
       <center>
       <H1>Welcome to The Keyword Framework</H1><P />
       <a href="myLink.html">Click to go to myLink</a><BR />
	   <P />
	   <input type="submit" value="Begin New Project" onClick='startNewProject()'><P />
       Current Suite is: <input type="text" id="className" />
       <input type="submit" value="LoadSuite" onClick='getTestSuite()'/><P />
       <P />
       </center>
       <div id="testCaseDiv">
       Current Test Case is: 
       <select id="testCaseName"></select>
		</div>
		<BR />
       The User will 
       	<select id="keyword" onchange="instruction_Selected()">
       		<%
       			for (Map.Entry<Constants.KEYWORD_KEYS, String> keywordOption : Constants.KEYWORD_LONGNAMES.entrySet()) {
       		%>
    			<option value="<%=keywordOption.getKey().toString()%>"><%=keywordOption.getValue()%></option>
    			<%
    		}
       		%>
		</select>
		<P />
       <div id='entireInput1'>
       		<div id="input1Desc">with Input 1: </div> 
       		<input type="text" id="Input1" /><P />
       </div>
       <div id='entireInput2'>
	       <div id="input2Desc">with Input 2: </div> 
    	   <input type="text" id="Input2" /><P />
       </div>
       <div id='entireInput3'>
	       <div id="input3Desc">with Input 3: </div>
    	   <input type="text" id="Input3" /><P />
       </div>
       <div id='entireInput4'>
	       <div id="input4Desc">with Input 4: </div>
     	  <input type="text" id="Input4" /><P />
       </div>         
       <input type="submit" id="AddInstruction" value="Add Instruction" onClick='addInstruction()' /><P />
       <center>
       <div id="InstructionStatus"></div><P />
       <input type="submit" id="RunTests" value="Run Tests" onClick='runTests()'/><P />
       <div id="RunTests"></div><P />
       <BR /><P /><BR />
       </center>
       <input type="submit" id="clearDivs" value="Clear Console" onClick='clearDivs()'/>
       <input type="submit" id="deleteSuite" onClick='deleteSuite()'/><br />
       			
		The test currently looks as follows:
		<div id="testSuite"></div>


        <script type="text/javascript">

		function instruction_Selected(){
			var dropdown = document.getElementById("keyword");
			var keyword = dropdown.options[dropdown.selectedIndex].value;
			hideAllInputs();
			if(keyword == "BeginClass"){
				updateTestCaseNames("NewClass", null);
			}
			showAllNecessaryInputs(keyword);
		}

		function hideAllInputs(){
			//Make sure you clear to +1 since we started the elements out at 1 (not 0)
			for(var x=1; x < MAX_NUMBER_OF_KEYWORD_DESCRIPTIONS +1; x++){
				hideSpecificInput(x);
			}
		}

		function showSpecificInput(inputNum){
			var entireInput = document.getElementById("entireInput" + inputNum);
			var inputDesc = document.getElementById("input" + inputNum + "Desc");
			var input = document.getElementById("Input" + inputNum);			
			entireInput.style.display = 'block';
			inputDesc.style.visibility = 'visible';
			input.style.visibility = 'visible';		
		}

		function hideSpecificInput(inputNum){
			var entireInput = document.getElementById("entireInput" + inputNum);
			var inputDesc = document.getElementById("input" + inputNum + "Desc");
			var input = document.getElementById("Input" + inputNum);			
			entireInput.style.display = 'none';
			inputDesc.innerHTML = "Input " + inputNum;
			input.value = "assigned_null";	
		}


		function showAllNecessaryInputs(keyword){
			//First clear all input fields
			hideAllInputs();

			//Now Display all input Descriptions based on the InputConstants Map
			//And Also Display all input default Values based on the InputConstants Map
			//Note: We have a JUnit to confirm that the arrays will always match up in numbers (Desc and Value),
					//So we can just use the same index for each of the arrays here.
			var keywordDescs = keywordDescMap[keyword].split(", ");
			var keywordVals = keywordValueMap[keyword].split(", ");
			for (var i=0; i < keywordDescs.length; i++){
				showSpecificInput(i+1);
				document.getElementById("input" + (i+1) + "Desc").innerHTML = keywordDescs[i];
				document.getElementById("Input" + (i+1)).value = keywordVals[i];
			}
		}

		function doGET(GetURL){
			var myObj = null;
			var xmlhttp = null;
			if (window.XMLHttpRequest) {
				xmlhttp = new XMLHttpRequest();
				if ( typeof xmlhttp.overrideMimeType != 'undefined') {
					xmlhttp.overrideMimeType('text/json');
				}
			} else if (window.ActiveXObject) {
				xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
			} else {
				alert('Your browser does not support xmlhttprequests. Sorry.');
			}

			xmlhttp.open('GET', GetURL, true);
			xmlhttp.setRequestHeader('Content-Type', 'application/json');
			xmlhttp.send(null);

			xmlhttp.onreadystatechange = function() {
				if (xmlhttp.readyState == 4) {
					if(xmlhttp.status == 200) {
						//myObj = eval ( '(' + xmlhttp.responseText + ')' );
						myObj = xmlhttp.responseText;
						if(GetURL.indexOf("/RunBuild") != -1){
							document.getElementById('RunTests').innerHTML = myObj;
						}
						else if(GetURL.indexOf("/TestSuite") != -1){
							document.getElementById('testSuite').innerHTML = myObj;
						}
						return myObj;
					}
					else {
						if(isDEBUG){
							alert("GET Fail - status: " + xmlhttp.status + " - " + xmlhttp.responseText);
						}
					}
				} else {
					// wait for the call to complete
				}
			};
		return null;
		}//doGET

		function doPOST(POSTURL, JSONInput, isAsync) {
			var myObj = null;
			var xmlhttp = null;
			if (window.XMLHttpRequest) {
				xmlhttp = new XMLHttpRequest();
				if ( typeof xmlhttp.overrideMimeType != 'undefined') {
					xmlhttp.overrideMimeType('application/json');
				}
			} else if (window.ActiveXObject) {
				xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
			} else {
				alert('Your browser does not support xmlhttprequests. Sorry.');
			}

			xmlhttp.open('POST', POSTURL, isAsync);
			xmlhttp.setRequestHeader('Content-Type', 'application/json');
			xmlhttp.send(JSONInput);
			
			xmlhttp.onreadystatechange = function() {
				if (xmlhttp.readyState == 4) {
					if(xmlhttp.status == 200) {
						myObj = xmlhttp.responseText;
						return myObj;
					}
					else {
						alert("POST Fail - status: " + xmlhttp.status + " - " + xmlhttp.responseText);
					}
				} else {
				// wait for the call to complete
				}
			};
			//This would be wierd, but if we get here and we have a response, return what we got and not null
			if(xmlhttp.responseText.length > 0){
				return xmlhttp.responseText;
			}
			return null;
		}//doPOST


		function runTests() {
			var GETurl = 'rest/webService/RunBuild';
			var returnVal = doGET(GETurl);
			return returnVal;
		}

		function startNewProject() {
			var POSTurl = 'rest/webService/StartNewProject';
			var returnVal = doPOST(POSTurl);
			return returnVal;
		}

		function getTestSuite() {
			var className = document.getElementById("className").value;
			var GETurl = 'rest/webService/TestSuite/' + className;
			var returnVal = doGET(GETurl);
			document.getElementById("deleteSuite").value = "Delete Suite : " + document.getElementById("className").value;
			return returnVal;
		}


		function moveTestStep(testCaseName, stepNumber, direction) {
			var POSTurl = 'rest/webService/MoveTestStep/' + document.getElementById("className").value +'/'+testCaseName+'/'+stepNumber+'/'+direction;
			var returnVal = doPOSTwithCallback(POSTurl, "", true, updateTestSuiteDisplay);
			return returnVal;
		}
		
		function getTestCaseNames() {
			var className = document.getElementById("className").value;
			var GETurl = 'rest/webService/ListOfTestMethodNames/' + className;
			var returnVal = doGET(GETurl);
			return returnVal;
		}

		function postInstruction(keyword, input1, input2, input3, input4){
			var className = document.getElementById("className").value;
			var testCaseSelectObj = document.getElementById("testCaseName");
			try{
				var testCaseName = testCaseSelectObj.options[testCaseSelectObj.selectedIndex].text;
			}
			catch(err){ testCaseName = "assigned_null"; }
			var inputArray = input1 + ", " + input2 + ", " + input3 + ", " + input4;
			var POSTurl = 'rest/webService/NewInstruction/' + 
				encodeURLComp(keyword) + '/' + encodeURLComp(className) + '/' + encodeURLComp(testCaseName) + '/' +
				encodeURLComp(inputArray);
			var returnVal = doPOST(POSTurl, "", false);
			if(keyword == "BeginTest"){
				updateTestCaseNames("NewTest", input1);
			}
			return returnVal;
		}

		function updateTestSuiteDisplay(testSuiteText){
			document.getElementById('testSuite').innerHTML = testSuiteText;
		}

		function updateTestCaseNames(action, newTestName){
			selectDiv = document.getElementById('testCaseDiv');
			selectObject = document.getElementById('testCaseName');
			if(action == "NewClass"){
				selectDiv.style.visibility = 'hidden';				
			}
			else if(action == "NewTest"){
				selectDiv.style.visibility = 'visible';		
				var className = document.getElementById("className").value;
				var GETurl = 'rest/webService/ListOfTestMethodNames/' + className;
				doGETwithCallback(GETurl, performTestCaseSelectOptionUpdate, newTestName);
			}
		}

		function performTestCaseSelectOptionUpdate(newOptions, newTestName){
			var methodList = newOptions.split(",");
			selectObject.options.length = methodList.length;
			for (var i=0; i < methodList.length; i++){
				selectObject.options[i] = new Option(methodList[i],i);
			}
			//Now select the new one by default
			for (var i=0; i < methodList.length; i++){
				if(methodList[i] == newTestName){
					selectObject.selectedIndex = i;
				}
			}
		}

		function doGETwithCallback(GetURL, callbackFunction, newTestName){
			var myObj = null;
			var xmlhttp = null;
			if (window.XMLHttpRequest) {
				xmlhttp = new XMLHttpRequest();
				if ( typeof xmlhttp.overrideMimeType != 'undefined') {
					xmlhttp.overrideMimeType('text/json');
				}
			} else if (window.ActiveXObject) {
				xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
			} else {
				alert('Your browser does not support xmlhttprequests. Sorry.');
			}

			xmlhttp.open('GET', GetURL, true);
			xmlhttp.setRequestHeader('Content-Type', 'application/json');
			xmlhttp.send(null);

			xmlhttp.onreadystatechange = function() {
				if (xmlhttp.readyState == 4) {
					if(xmlhttp.status == 200) {
						//myObj = eval ( '(' + xmlhttp.responseText + ')' );
						myObj = xmlhttp.responseText;
						if(GetURL.indexOf("/RunBuild") != -1){
							document.getElementById('RunTests').innerHTML = myObj;
						}
						else if(GetURL.indexOf("/TestSuite") != -1){
							document.getElementById('testSuite').innerHTML = myObj;
						}
						callbackFunction(myObj, newTestName);
					}
					else {
						if(isDEBUG){
							alert("GET Fail - status: " + xmlhttp.status + " - " + xmlhttp.responseText);
						}
					}
				} else {
					// wait for the call to complete
				}
			};
		return null;
		}//doGET
		

		function doPOSTwithCallback(POSTURL, JSONInput, isAsync, callbackFunction){
			var myObj = null;
			var xmlhttp = null;
			if (window.XMLHttpRequest) {
				xmlhttp = new XMLHttpRequest();
				if ( typeof xmlhttp.overrideMimeType != 'undefined') {
					xmlhttp.overrideMimeType('application/json');
				}
			} else if (window.ActiveXObject) {
				xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
			} else {
				alert('Your browser does not support xmlhttprequests. Sorry.');
			}

			xmlhttp.open('POST', POSTURL, isAsync);
			xmlhttp.setRequestHeader('Content-Type', 'application/json');
			xmlhttp.send(JSONInput);
			
			xmlhttp.onreadystatechange = function() {
				if (xmlhttp.readyState == 4) {
					if(xmlhttp.status == 200) {
						//myObj = eval ( '(' + xmlhttp.responseText + ')' );
						myObj = xmlhttp.responseText;
						callbackFunction(myObj);
					}
					else {
						if(isDEBUG){
							alert("POST Fail - status: " + xmlhttp.status + " - " + xmlhttp.responseText);
						}
					}
				} else {
					// wait for the call to complete
				}
			};
		return null;
		}//doPOST
		
		function encodeURLComp(component){
			//TODO: For some reason encodeURIComponent isn't working for WebService, seems like Java is Decoding it before the @Path takes a look, don't know why...
			var newString
			newString = component.replace("//", "&&&***");
			newString = newString.split('/').join('&**&');
			return newString;
		}
		function clearDivs(){
			document.getElementById('RunTests').innerHTML = "";
			document.getElementById('testSuite').innerHTML = "";
		}

		function deleteSuite(){
			var className = document.getElementById("className").value;
			var POSTurl = 'rest/webService/DeleteTestSuite/' + className + '.java';
			var returnVal = doPOST(POSTurl, "", false);
			clearDivs();
			document.getElementById('testSuite').innerHTML = returnVal;		
		}

		function addInstruction(){
			var e = document.getElementById("keyword");
			var keyword = e.options[e.selectedIndex].value;
			e = document.getElementById("Input1");
			var input1 = e.value;
			e = document.getElementById("Input2");
			var input2 = e.value;
			e = document.getElementById("Input3");
			var input3 = e.value;
			e = document.getElementById("Input4");
			var input4 = e.value;						
			//alert("keyword = " + keyword + ", input1: " + input1 + ", input2: " + input2);

			if(keyword == "BeginClass"){
				document.getElementById("className").value = document.getElementById("Input1").value;
			}
			
			if(typeof(document.getElementById("className").value) === 'undefined' || document.getElementById("className").value == ''){
				document.getElementById('testSuite').innerHTML = "<font color = 'red'>ERROR: You must start a Test Sutie First.  No instruction was added.</font>"
			}
			else{
				postInstruction(keyword, input1, input2, input3, input4);
				getTestSuite();
			}
		}

		hideAllInputs();
		document.getElementById('testCaseDiv').style.visibility = 'hidden';	
		showAllNecessaryInputs("BeginClass");
		document.getElementById("Input1").value = "MySuiteTest"; //Default, will be changed when AddInstruction of BeginClass
		document.getElementById("className").value = "MySuiteTest"; //Default, will be changed when AddInstruction of BeginClass
	   	document.getElementById("deleteSuite").value = "Delete Suite : " + document.getElementById("className").value;
		</script>
       
</body>
</html>
