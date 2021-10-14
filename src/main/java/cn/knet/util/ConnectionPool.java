package cn.knet.util;

import javax.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

public class ConnectionPool implements DataSource {
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String dbUrl = "jdbc:mysql://localhost:3306/db_blog?useSSL=true";
    private static final String userName  = "root";
    private static final String password= "123456";

    private LinkedList<Connection> pool;

    private Connection getOneConnection() {
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(dbUrl,userName,password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
    public Connection getConnection() throws SQLException {
        if(pool==null){
            pool = new LinkedList<>();
            for(int i=0;i<5;i++){
                pool.add(getOneConnection());
            }
        }
        if(pool.size()<=0){
            return getOneConnection();
        }
        return pool.remove();
    }
    public void close(Connection connection){
        pool.add(connection);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
