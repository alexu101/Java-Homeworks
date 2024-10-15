<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Collections, java.util.List" %>
<%@ page import="org.graph4j.*, java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <title>Shuffled Lines</title>
</head>
<body>
<h1>Shuffled Lines</h1>
<ul>
    <%
        List<String> lines = (List<String>) session.getAttribute("fileLines");
        if (lines != null) {
            Collections.shuffle(lines); // Shuffle the lines
            for (String line : lines) {
                out.println("<li>" + line + "</li>");
            }
        } else {
            out.println("<p>No file uploaded!</p>");
        }
    %>
</ul>
<%
    Map<Edge, Integer> edgeColors = (Map<Edge, Integer>) session.getAttribute("edgeColors");

    if (edgeColors != null) {
        out.println("<h1>Edge Coloring Results</h1>");
        for (Edge edge : edgeColors.keySet()) {
            int source = edge.source() +1;
            int target = edge.target() +1;
            out.println("<p>Edge (" + source + ", " + target + ") -> Color: " + edgeColors.get(edge) + "</p>");
        }
    } else {
        out.println("<p>No edge coloring result available.</p>");
    }
%>
</body>
</html>
