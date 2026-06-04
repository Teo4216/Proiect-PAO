package com.pao.proiect.bank.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;


public class DatabaseConnection {

    private static volatile DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() throws SQLException {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (is == null) {
                throw new RuntimeException(
                    "Fisierul db.properties nu a fost gasit in classpath. " +
                    "In IntelliJ: marcheaza folderul 'src/main/resources' ca Resources Root.");
            }
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Eroare citire db.properties: " + e.getMessage(), e);
        }

        String url      = props.getProperty("db.url");
        String user     = props.getProperty("db.user",     "");
        String password = props.getProperty("db.password", "");

        if (url == null || url.isBlank()) {
            throw new RuntimeException("db.url nu este configurat in db.properties");
        }

        this.connection = DriverManager.getConnection(url, user, password);

        if (url.startsWith("jdbc:sqlite")) {
            try (Statement st = this.connection.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
        }
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        if (instance.connection.isClosed()) {
            synchronized (DatabaseConnection.class) {
                instance = new DatabaseConnection();
            }
        }
        return instance;
    }

    public Connection getConnection() { return connection; }
}
