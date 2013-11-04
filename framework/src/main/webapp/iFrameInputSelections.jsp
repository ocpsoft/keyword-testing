<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<html>
<title>Inputs for selected keyword</title>
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


<div id='container'>
Select the inputs you would like for this keyword.<br />
<P />
</div>

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

<P />
<input type="submit" id="AddInstruction" value="Add Instruction" onClick='addInstruction()' />

<script type="text/javascript">
	function get(name){
	   if(name=(new RegExp('[?&]'+encodeURIComponent(name)+'=([^&]*)')).exec(location.search))
	      return decodeURIComponent(name[1]);
	}

	String.prototype.replaceAll = function (find, replace) {
	    var str = this;
	    return str.replace(new RegExp(find, 'g'), replace);
	};
	
	var keyword = get('keyword');
	showAllNecessaryInputs();



	
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

	function showAllNecessaryInputs(){
		//First clear all input fields
		hideAllInputs();

		//Now Display all input Descriptions based on the InputConstants Map
		//And Also Display all input default Values based on the InputConstants Map
		//Note: We have a JUnit to confirm that the arrays will always match up in numbers (Desc and Value),
				//So we can just use the same index for each of the arrays here.
		var keywordDescs = keywordDescMap[keyword].split(", ");
		var keywordVals = keywordValueMap[keyword].split(", ");
		var quoteReplaceStr = "<%=Constants.DOUBLE_QUOTE_REPLACEMENT%>";
		for (var i=0; i < keywordDescs.length; i++){
			showSpecificInput(i+1);
			document.getElementById("input" + (i+1) + "Desc").innerHTML = keywordDescs[i];
			document.getElementById("Input" + (i+1)).value = keywordVals[i].replaceAll(quoteReplaceStr, "\"");
		}
	}

	function addInstruction(){
		var className = "";
		if(keyword == "BeginClass"){
			className = document.getElementById("Input1").value;
		}
		if(keyword == "BeginTest"){
			parent.setTestCaseName(document.getElementById("Input1").value);
		}
		parent.addInstruction(className, getAllInputValuesAsXML());
	}

	
	function getAllInputValuesAsXML(){
		var inputArray = "<InputsList>";
		var count = 1;

		var e = document.getElementById("Input" + count);
		while (e != null){
			if (e.value != "assigned_null"){
				inputArray += "<Input>" + e.value + "</Input>";
			}
			count ++;
			e = document.getElementById("Input" + count);
		}
		inputArray = inputArray + "</InputsList>";
		return inputArray;
	}
</script>
</body>
</html>