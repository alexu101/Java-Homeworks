package org.example.javalab2;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import jakarta.servlet.http.*;

@WebFilter("/input.jsp")
public class LogFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        String logMessage = String.format("Request received for input.jsp: HTTP Method: %s, IP: %s",
                req.getMethod(), req.getRemoteAddr());

        request.getServletContext().log(logMessage);


        chain.doFilter(request, response);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }
}