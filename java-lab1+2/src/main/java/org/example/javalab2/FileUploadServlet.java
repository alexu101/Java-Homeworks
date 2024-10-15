package org.example.javalab2;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import org.graph4j.*;

@WebServlet(name = "fileUploadServlet", value = "/file-upload-servlet")
@MultipartConfig
public class FileUploadServlet extends HttpServlet {
    private static final String SECRET_KEY = "6LdYXF8qAAAAAHKQ91ZLFnIldyiXJH8lHwqzvz7N";


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String recaptchaResponse = request.getParameter("g-recaptcha-response");
        boolean isCaptchaValid = validateCaptcha(recaptchaResponse);

        System.out.println("isCaptchaValid = " + isCaptchaValid);

        if (!isCaptchaValid) {
            request.setAttribute("errorMessage", "CAPTCHA validation failed. Please try again.");
            request.getRequestDispatcher("input.jsp").forward(request, response); // Redirect back to the form
            return;
        }

        Part filePart = request.getPart("textFile");
        List<String> fileLines = new ArrayList<>();

        try (InputStream inputStream = filePart.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileLines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to read file. Please try again.");
            request.getRequestDispatcher("input.jsp").forward(request, response); // Redirect back to the form
            return;
        }

        Graph graph = parseDimacsGraph(fileLines);
        Map<Edge,Integer> edgeCOlors = greedyEdgeColoring(graph);

        HttpSession session = request.getSession();
        session.setAttribute("fileLines", fileLines);
        session.setAttribute("edgeColors",edgeCOlors);

        // Redirect to the result page
        response.sendRedirect("result.jsp");
    }

    private boolean validateCaptcha(String recaptchaResponse) {
        try {
            String urlString = "https://www.google.com/recaptcha/api/siteverify";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            String postData = "secret=" + SECRET_KEY + "&response=" + recaptchaResponse;
            byte[] postDataBytes = postData.getBytes("UTF-8");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

            var outputStream = conn.getOutputStream();
            outputStream.write(postDataBytes);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                responseBuilder.append(inputLine);
            }
            System.out.println("Response from reCAPTCHA: " + responseBuilder.toString());
            return responseBuilder.toString().contains("\"success\": true");

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Graph parseDimacsGraph(List<String> fileLines) {
        int vertices = 0;
        List<int[]> edges = new ArrayList<>();

        for (String line : fileLines) {
            if (line.startsWith("p")) {
                String[] parts = line.split(" ");
                vertices = Integer.parseInt(parts[2]);
            } else if (line.startsWith("e")) {
                String[] parts = line.split(" ");
                int u = Integer.parseInt(parts[1]) - 1;  // Subtract 1 to make zero-indexed
                int v = Integer.parseInt(parts[2]) - 1;
                edges.add(new int[]{u, v});
            }
        }

        Graph graph = GraphBuilder.numVertices(vertices).buildGraph();
        for (int[] edge : edges) {
            graph.addEdge(edge[0], edge[1]);
        }

        return graph;
    }

    private Map<Edge, Integer> greedyEdgeColoring(Graph graph){
        Map<Edge, Integer> edgeColors = new HashMap<>();

        boolean[] available = new boolean[graph.edges().length];

        edgeColors.put(graph.edges()[0], 0); // first color to first edge

        for(int i = 1; i< graph.edges().length; i++){
            Edge currentEdge = graph.edges()[i];

            Arrays.fill(available, false);

            //check adjacent edges for same color
            for (Edge adjacentEdge : graph.edgesOf(currentEdge.source())) {
                if (edgeColors.containsKey(adjacentEdge)) {
                    available[edgeColors.get(adjacentEdge)] = true; // mark color unavailable
                }
            }
            for (Edge adjacentEdge : graph.edgesOf(currentEdge.target())) {
                if (edgeColors.containsKey(adjacentEdge)) {
                    available[edgeColors.get(adjacentEdge)] = true;
                }
            }

            int color;
            for (color = 0; color < graph.edges().length; color++) {
                if (available[color] == false) {
                    break;
                }
            }

            edgeColors.put(currentEdge, color);
        }
        return edgeColors;

    }

}
