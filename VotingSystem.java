package com.voting.blockchain;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class VotingSystem {
    private Connection conn;

    public VotingSystem(Connection connection) {
        this.conn = connection;
    }

    public void registerUser(String voterId) {
        String SQL = "INSERT INTO users (voter_id) VALUES (?) ON CONFLICT (voter_id) DO NOTHING"; // Prevent duplicates
        try (PreparedStatement psmt = conn.prepareStatement(SQL)) {
            psmt.setString(1, voterId);
            int rowsAffected = psmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User registered successfully!");
            } else {
                System.out.println("User already registered.");
            }
        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
        }
    }

    public void castVote(String candidateName, String voterId) {
        String voteHash = generateVoteHash(candidateName, voterId);
        String SQL = "INSERT INTO votes (candidate_name, voter_id, vote_hash) VALUES (?, ?, ?)";
        try (PreparedStatement psmt = conn.prepareStatement(SQL)) {
            psmt.setString(1, candidateName);
            psmt.setString(2, voterId);
            psmt.setString(3, voteHash);
            psmt.executeUpdate();
            System.out.println("Vote cast successfully! Hash: " + voteHash);
        } catch (SQLException e) {
            System.out.println("Error casting vote: " + e.getMessage());
        }
    }

    private String generateVoteHash(String candidateName, String voterId) {
        try {
            String input = candidateName + voterId;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }

    public void countVotes() {
        String SQL = "SELECT candidate_name, COUNT(*) AS vote_count FROM votes GROUP BY candidate_name";
        try (PreparedStatement psmt = conn.prepareStatement(SQL);
             ResultSet rs = psmt.executeQuery()) {
            System.out.println("Vote Counts:");
            while (rs.next()) {
                System.out.println("Candidate: " + rs.getString("candidate_name") + ", Votes: " + rs.getInt("vote_count"));
            }
        } catch (SQLException e) {
            System.out.println("Error counting votes: " + e.getMessage());
        }
    }

    public void displayVotes() {
        String SQL = "SELECT * FROM votes";
        try (PreparedStatement psmt = conn.prepareStatement(SQL);
             ResultSet rs = psmt.executeQuery()) {
            System.out.println("All votes:");
            while (rs.next()) {
                System.out.println("Candidate: " + rs.getString("candidate_name") + ", Voter ID: " + rs.getString("voter_id") + ", Hash: " + rs.getString("vote_hash"));
            }
        } catch (SQLException e) {
            System.out.println("Error displaying votes: " + e.getMessage());
        }
    }
}