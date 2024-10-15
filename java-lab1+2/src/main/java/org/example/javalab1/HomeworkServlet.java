package org.example.javalab1;

import java.io.*;
import java.util.Enumeration;
import java.util.Random;
import java.util.logging.*;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "homeworkServlet", value = "/homework-servlet")
public class HomeworkServlet extends HttpServlet {
    public void init() {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logReqDetails(request);
        PrintWriter out = response.getWriter();
        String isDesktopAccess = request.getParameter("desktop");
        int numVertices = Integer.parseInt(request.getParameter("numVertices"));
        int numEdges = Integer.parseInt(request.getParameter("numEdges"));

        if(numEdges > (numVertices * (numVertices -1)) /2){
            out.println("<h1>Too many edges according to the vertices number</h1>");
        }
        else{
            int [][] adjacencyMatrix = generateRandomGraphAdjacencyMatrix(numVertices, numEdges);

            if ("true".equalsIgnoreCase(isDesktopAccess)){
                response.setContentType("text/plain");

                out.println("Adjacency Matrix:");
                for (int[] row : adjacencyMatrix) {
                    for (int value : row) {
                        out.print(value + " ");
                    }
                    out.println();
                }

            }else{
                response.setContentType("text/html");
                out.println("<html><body>");
                out.println("<h1>Adjacency Matrix</h1>");
                out.println("<table border='1'>");

                for(int i = 0; i < adjacencyMatrix.length; i++){
                    out.println("<tr>");
                    for(int j = 0; j < adjacencyMatrix[i].length; j++){
                        out.println("<td>");
                        out.println(adjacencyMatrix[i][j]);
                        out.println("</td>");
                    }
                    out.println("</tr>");
                }
                out.println("</table>");
                out.println("</body></html>");
            }

        }
    }

    protected int[][] generateRandomGraphAdjacencyMatrix(int numVertices, int numEdges){
        int [][] adjacencyMatrix = new int[numVertices][numVertices];
        Random rand = new Random();
        int edgeCount = 0;

        while(edgeCount < numEdges){
            int vertex1 = rand.nextInt(numVertices);
            int vertex2 = rand.nextInt(numVertices);

            if(vertex1 != vertex2 && adjacencyMatrix[vertex1][vertex2] == 0){
                adjacencyMatrix[vertex1][vertex2] = 1;
                adjacencyMatrix[vertex2][vertex1] = 1;
                edgeCount++;
            }
        }

        return adjacencyMatrix;
    }

    protected void logReqDetails(HttpServletRequest request){
        String method = request.getMethod();
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String language = request.getHeader("Accept-Language");
        StringBuilder parameters = new StringBuilder();
        Enumeration<String> parameterNames = request.getParameterNames();
        while(parameterNames.hasMoreElements()){
            String parameterName = (String)parameterNames.nextElement();
            parameters.append(parameterName).append("=").append(request.getParameter(parameterName)).append("; ");
        }
        String logMessage = String.format("HTTP Method: %s, IP: %s, User-Agent: %s, Languages: %s, Parameters: %s",
                method, ip, userAgent, language, parameters.toString());
        getServletContext().log(logMessage);
    }

    public void destroy(){
    }
}
