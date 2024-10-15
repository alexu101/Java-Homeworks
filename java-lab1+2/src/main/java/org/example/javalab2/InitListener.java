package org.example.javalab2;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;

@WebListener
public class InitListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        String prelude = sce.getServletContext().getInitParameter("prelude");
        String coda = sce.getServletContext().getInitParameter("coda");

        sce.getServletContext().setAttribute("prelude", prelude);
        sce.getServletContext().setAttribute("coda", coda);
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }
}
