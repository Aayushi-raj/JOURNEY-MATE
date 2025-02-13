import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RentalDetailsPage extends JFrame {
    public RentalDetailsPage(ResultSet rs) throws SQLException {
        setTitle("Rental Details");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        addField(panel, "Rental ID:", rs.getString("rental_id"));
        addField(panel, "Location:", rs.getString("location"));
        addField(panel, "Start Time:", rs.getString("starttime"));
        addField(panel, "End Time:", rs.getString("endtime"));
        addField(panel, "Vehicle Type:", rs.getString("vehicle_type"));
        addField(panel, "Brand:", rs.getString("brand"));
        addField(panel, "Price per Hour:", rs.getString("priceperhour"));
        addField(panel, "Availability:", rs.getInt("availability") == 1 ? "Available" : "Not Available");

        add(panel);
    }

    private void addField(JPanel panel, String label, String value) {
        panel.add(new JLabel(label));
        panel.add(new JLabel(value));
    }
}
