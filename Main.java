package com.voting.blockchain;

import java.sql.Connection;
import java.util.Scanner;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        Connection conn = DatabaseUtil.connect();
        VotingSystem votingSystem = new VotingSystem(conn);
        Scanner scanner = new Scanner(System.in);
        
        // User Registration
        System.out.print("Enter your voter ID to register: ");
        String voterId = scanner.nextLine();
        votingSystem.registerUser(voterId);

        // Voting Process
        while (true) {
            System.out.print("Enter candidate name (or 'exit' to quit): ");
            String candidateName = scanner.nextLine();
            if (candidateName.equalsIgnoreCase("exit")) {
                break;
            }
            votingSystem.castVote(candidateName, voterId);
        }

        // Display all votes
        votingSystem.displayVotes();
        
        // Count votes
        votingSystem.countVotes();

        // Cleanup
        try {
            if (conn != null) {
                conn.close();
            }
            scanner.close();
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}