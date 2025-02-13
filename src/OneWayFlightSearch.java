import javax.swing.*;
import java.sql.*;

public class OneWayFlightSearch {
    private static final String URL = "jdbc:mysql://localhost:3306/cab";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "akshat";

    public static void searchOneWay(String from, String to, String departureDate, String travellerClass) {
        String query = "SELECT * FROM oneway_flights WHERE from_city = ? AND to_city = ? AND departure_date = ? AND traveller_class = ?";
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, from);
            pstmt.setString(2, to);
            pstmt.setString(3, departureDate);
            pstmt.setString(4, travellerClass);
            ResultSet rs = pstmt.executeQuery();
            displayResults(rs, "One Way Flight Results");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error searching for one-way flights: " + e.getMessage(), "Search Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void displayResults(ResultSet rs, String title) {
        // Implement the display logic here (similar to the existing displayResults method)
        // You may want to customize this for one-way flights specifically
    }
}
