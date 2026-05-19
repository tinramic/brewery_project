package hr.algebra.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseUtils {

    private static final String DB_URL = "jdbc:sqlite:beer.db";

    private DatabaseUtils() {}

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        conn.createStatement().execute("PRAGMA foreign_keys = ON");
        return conn;
    }

    public static void initialize() {
        try {
            executeSqlFile("init.sql");
            System.out.println("Database initialized successfully");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public static void clearData() {
        try {
            executeSqlFile("clear.sql");
            System.out.println("Database cleared successfully");
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear database", e);
        }
    }

    private static void executeSqlFile(String fileName) throws IOException, SQLException {
        InputStream is = DatabaseUtils.class.getClassLoader().getResourceAsStream(fileName);
        if (is == null) {
            throw new IOException("SQL file not found: " + fileName);
        }

        String sql = new BufferedReader(new InputStreamReader(is))
                .lines()
                .collect(Collectors.joining("\n"));

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            for (String statement : sql.split(";")) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
        }
    }
}