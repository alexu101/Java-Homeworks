package org.example.javalab1;

import java.io.*;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "compulsoryServlet", value = "/compulsory-servlet")
public class CompulsoryServlet extends HttpServlet {

    public void init() {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        // Get the string parameter from the request
        String inputString = request.getParameter("input");

        // Generate HTML output
        PrintWriter out = response.getWriter();
        out.println("<html><body>");

        // If inputString is null or empty, handle that case
        if (inputString == null || inputString.isEmpty()) {
            out.println("<h2>No input string provided!</h2>");
        } else {
            out.println("<h2>Character List</h2>");
            out.println("<ol>");
            // Loop through each character and create an ordered list
            for (int i = 0; i < inputString.length(); i++) {
                out.println("<li>" + inputString.charAt(i) + "</li>");
            }
            out.println("</ol>");
        }

        out.println("</body></html>");
    }

    public void destroy() {
    }
}
