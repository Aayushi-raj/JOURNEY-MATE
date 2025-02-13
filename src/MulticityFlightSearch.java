import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class MulticityFlightSearch {
    private Connection connection;

    public MulticityFlightSearch(Connection connection) {
        this.connection = connection;
    }

    public ResultSet searchFlights(String from1, String to1, String date1, String from2, String to2, String date2, String travellerClass) {
        String query = "SELECT * FROM multicity WHERE first_from_city = ? AND first_to_city = ? AND first_flight_date = ? AND second_from_city = ? AND second_to_city = ? AND second_flight_date = ? AND traveller_class = ?";
        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, from1);
            pstmt.setString(2, to1);
            pstmt.setString(3, date1);
            pstmt.setString(4, from2);
            pstmt.setString(5, to2);
            pstmt.setString(6, date2);
            pstmt.setString(7, travellerClass);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error searching for multicity flights: " + e.getMessage(), "Search Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
    }
}
