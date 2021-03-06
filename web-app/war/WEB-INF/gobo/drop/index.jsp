<!DOCTYPE html>
<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>Drop</title>
<script type="text/javascript" src="http://www.google.com/jsapi"></script>
<script type="text/javascript">
google.load("jquery", "1.4.2");
function initialize() {
	$("#checkall").click(function(){
		$("#form1 input[type='checkbox']").attr('checked', true);
        return false; 
	});
	$("#uncheckall").click(function(){
		$("#form1 input[type='checkbox']").attr('checked', false);
        return false; 
	});
	$("#form1").submit(function() {
		if($("#form1 input[type='checkbox']:checked").length == 0) {
			alert("Please select checkbox at least one.");
			return false;
		} else {
			return confirm("Are you sure to DELETE ALL PROPERTIES of the kind(s)");
		}
	});
}
google.setOnLoadCallback(initialize);
</script>
<link rel="stylesheet" href="/gobo/css/global.css" />
</head>
<body>
<h1>Gobo Tools</h1>
<h2>Drop</h2>
<div style="position:absolute; top:10px; right:10px;"><a href="../index.gobo">Menu</a></div>
<div id="main">
<div id="msg">Please select the kind(s) to drop.</div>
<form action="start.gobo" method="POST" id="form1">
<input type="button" value="Check All" id="checkall" />
<input type="button" value="Uncheck All" id="uncheckall" />
<ul>
<c:forEach items="${list}" var="row">
<li>
  <input type="checkbox" name="kindArray" value="${row.name}" id="kind_${row.name}" />
  <label for="kind_${row.name}">${row.name}&nbsp;/&nbsp;<fmt:formatNumber>${row.count}</fmt:formatNumber> records</label>
</li>
</c:forEach>
</ul>
<input type="submit" value="execute" />
</form>
</div>
</body>
</html>
