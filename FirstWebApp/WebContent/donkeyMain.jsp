<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Donkey</title>
</head>
<body>
<h1>Get me to the Donkey/Skeleton Coast!</h1>
<form action="submitPage.jsp">
  How many days in advance would you like to be notified? <input type="text" name="notifyPeriod" value="7"> days
  <br>
  What is your minimum wave size? <input type="text" name="minimumSize" value="1.2">m
  <br>
  What is your email address? <input type="text" name="email" value="donkey@donkey.com">
  <br>
  <input type="submit" value="Get Pitted!!">
</form>
</body>
</html>