package com.voting.blockchain;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {
    private static final String URL = "jdbc:postgresql://localhost:5432/voting_system"; // Your database URL
    private static final String USER = "postgres"; // Your database username
    private static final String PASSWORD = "14725800Sa,"; // Your database password

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection to database established!");
            createTables(conn);
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
        return conn;
    }

    private static void createTables(Connection conn) {
        String createVotesTable = "CREATE TABLE IF NOT EXISTS votes (" +
                "id SERIAL PRIMARY KEY, " +
                "candidate_name VARCHAR(255) NOT NULL, " +
                "voter_id VARCHAR(255) NOT NULL UNIQUE, " +
                "vote_hash VARCHAR(64) NOT NULL, " +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id SERIAL PRIMARY KEY, " +
                "voter_id VARCHAR(255) NOT NULL UNIQUE" +
                ")";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createVotesTable);
            stmt.execute(createUsersTable);
            System.out.println("Tables created successfully!");
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }
}