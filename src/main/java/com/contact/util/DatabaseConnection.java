package com.contact.util;

import java.io.*;
import java.sql.*;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:database/contacts.db";
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(DB_URL);
            System.out.println("Database connected!");
            
            System.out.println("DB absolute path: " +
                    new java.io.File("database/contacts.db").getAbsolutePath());
            
            initializeDatabase();
        } catch (Exception e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private void initializeDatabase() {
        try {
        	InputStream is = getClass().getClassLoader().getResourceAsStream("db/init.sql");

            if (is == null) {
                // Fallback: when running from IDE and resources are not on classpath
                File file = new File("src/main/resources/db/init.sql");
                if (file.exists()) {
                    is = new FileInputStream(file);
                    System.out.println("init.sql loaded from filesystem: " + file.getAbsolutePath());
                } else {
                    throw new RuntimeException("init.sql NOT FOUND on classpath: db/init.sql AND not found at src/main/resources/db/init.sql");
                }
            } else {
                System.out.println("init.sql loaded from classpath: db/init.sql");
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.startsWith("--")) {
                    sql.append(line).append(" ");
                }
            }
            reader.close();

            try (Statement stmt = connection.createStatement()) {
                for (String statement : sql.toString().split(";")) {
                    if (!statement.trim().isEmpty()) {
                        stmt.execute(statement.trim());
                    }
                }
                System.out.println("Database initialized!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        debugIndexes();
    }
    
    private void debugIndexes() {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("PRAGMA index_list('person');")) {
            System.out.println("Indexes on person:");
            while (rs.next()) {
                System.out.println(" - " + rs.getString("name") + " (unique=" + rs.getInt("unique") + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}