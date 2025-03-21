package org.test_automation.DBConnectivity;

import java.sql.*;

public class DBUtil {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/selenium_test_db";
    private static final String USER = "root"; // Replace with your DB username
    private static final String PASS = "Sharma"; // Replace with your DB password

    // Establish DB Connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // Save message to DB
    public static void forgetValidation(String username, String message, String status) {
        String query = "INSERT INTO forget_response_messages (username, message, status) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, message);
            pstmt.setString(3, status);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Response message saved to DB successfully.");
            }

        } catch (SQLException e) {
            System.out.println("Error saving to DB: " + e.getMessage());
        }
    }
    public static void userNameValidation(String username,String password, String message, String status) {
        String query = "INSERT INTO username_response_messages (username,password, message, status) VALUES (?,?, ?, ?)";

        System.out.println("Message Update "+message+"_"+username);
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, message);
            pstmt.setString(4, status);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Response message saved to DB successfully.");
            }

        } catch (SQLException e) {
            System.out.println("Error saving to DB: " + e.getMessage());
        }
    }

    public static void insertScenario(String scenarioDescription, String status) {

        String sql = "INSERT INTO test_scenarios (scenario_description, status) VALUES (?, ?)";


        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {


            pstmt.setString(1, scenarioDescription);
            pstmt.setString(2, status);

            // 4. Execute Insert
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("✅ Scenario stored successfully");
            } else {
                System.out.println("❌ Failed to store scenario");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
