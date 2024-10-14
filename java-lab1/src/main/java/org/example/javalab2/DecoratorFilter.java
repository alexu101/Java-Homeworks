package org.example.javalab2;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebFilter("/result.jsp")
public class DecoratorFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String prelude = (String) request.getServletContext().getAttribute("prelude");
        String coda = (String) request.getServletContext().getAttribute("coda");

        PrintWriter out = response.getWriter();
        out.println(prelude); // Add prelude at the beginning
        chain.doFilter(request, response);
        out.println(coda); // Add coda at the end
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }
}
