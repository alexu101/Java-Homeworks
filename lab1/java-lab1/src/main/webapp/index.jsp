<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Hello World</title>
</head>
<body>
<h1>Lab 1</h1>
<h2>Compulsory</h2>
<form action="compulsory-servlet" method="get">
    <label for="inputString">Enter a string:</label>
    <input type="text" id="inputString" name="input" required>
    <input type="submit" value="Submit">
</form>
<h2>Homework</h2>
<form action="homework-servlet" method="get">
    <label for="numVertices">Enter vertices number:</label>
    <input type="text" id="numVertices" name="numVertices" required>
    <label for="numEdges">Enter edges number:</label>
    <input type="text" id="numEdges" name="numEdges" required>
    <input type="submit" value="Submit">
</form>
<h2>Bonus</h2>
<form action="bonus-servlet" method="get">
    <label for="vertices">Enter n:</label>
    <input type="text" id="vertices" name="vertices" required>
    <label for="firstK">Enter k:</label>
    <input type="text" id="firstK" name="k" required>
    <input type="submit" value="Submit">
</form>
</body>
</html>
