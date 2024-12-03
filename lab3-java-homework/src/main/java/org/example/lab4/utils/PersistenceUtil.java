package org.example.lab4.utils;


import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@ApplicationScoped
public class PersistenceUtil {

    @Resource(name="jdbc/routingResource")
    private DataSource ds;

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
