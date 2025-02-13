import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class RoundTripFlightSearch {
    private Connection connection;

    public RoundTripFlightSearch(Connection connection) {
        this.connection = connection;
    }

    public ResultSet searchFlights(String from, String to, String departureDate, String returnDate, String travellerClass) {
        String query = "SELECT * FROM roundtrip WHERE from_city = ? AND to_city = ? AND departure_date = ? AND return_date = ? AND traveller_class = ?";
        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, from);
            pstmt.setString(2, to);
            pstmt.setString(3, departureDate);
            pstmt.setString(4, returnDate);
            pstmt.setString(5, travellerClass);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error searching for round trip flights: " + e.getMessage(), "Search Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
    }
}
