import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class OneWayFlightBooking extends JFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/cab";
    private static final String USER = "root";
    private static final String PASS = "akshat";

    private static final Color PRIMARY_COLOR = new Color(92, 194, 191);
    private static final Color SECONDARY_COLOR = new Color(49, 151, 149);
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255);
    private static final Color SIDEBAR_COLOR = new Color(227, 244, 243);
    private static final Font HEADER_FONT = new Font("Samarkan", Font.PLAIN, 36);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

    private JButton flightButton;
    private Connection connection;
    private JPanel formFieldsPanel;

    public OneWayFlightBooking() {
        connectToDatabase();

        setTitle("Journey Mate - One-Way Flight Booking");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        add(createHeader(), BorderLayout.NORTH);
        add(createSidebar(), BorderLayout.WEST);

        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(BACKGROUND_COLOR);
        formFieldsPanel = createBookingForm();
        mainContent.add(formFieldsPanel, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
    }

    // ... (include all the methods from JourneyMateApp like createHeader, createSidebar, etc.)

    private JPanel createBookingForm() {
        JPanel bookingForm = new JPanel(new GridBagLayout());
        bookingForm.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        addFormField(bookingForm, "From:", createPlaceholderTextField("Enter origin city"), gbc);
        addFormField(bookingForm, "To:", createPlaceholderTextField("Enter destination city"), gbc);
        addFormField(bookingForm, "Date:", createPlaceholderTextField("YYYY-MM-DD"), gbc);
        addFormField(bookingForm, "Traveller Class:", createComboBox(new String[]{"Economy", "Business", "First Class"}), gbc);

        JButton searchButton = new JButton("Search Flights");
        searchButton.setBackground(PRIMARY_COLOR);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> searchOneWayFlights());
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        bookingForm.add(searchButton, gbc);

        return bookingForm;
    }

    private void searchOneWayFlights() {
        String fromCity = ((JTextField) getComponentByName(formFieldsPanel, "From:", 0)).getText();
        String toCity = ((JTextField) getComponentByName(formFieldsPanel, "To:", 0)).getText();
        String date = ((JTextField) getComponentByName(formFieldsPanel, "Date:", 0)).getText();
        String travellerClass = (String) ((JComboBox<?>) getComponentByName(formFieldsPanel, "Traveller Class:", 0)).getSelectedItem();

        try {
            String query = "SELECT * FROM oneway WHERE from_city = ? AND to_city = ? AND date = ? AND traveller_class = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, fromCity);
            pstmt.setString(2, toCity);
            pstmt.setString(3, date);
            pstmt.setString(4, travellerClass);

            ResultSet rs = pstmt.executeQuery();
            displayOneWayResults(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching for flights: " + e.getMessage(), "Search Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayOneWayResults(ResultSet rs) throws SQLException {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Flight Number");
        model.addColumn("Airline");
        model.addColumn("From");
        model.addColumn("To");
        model.addColumn("Date");
        model.addColumn("Departure Time");
        model.addColumn("Arrival Time");
        model.addColumn("Price");

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("flight_number"),
                rs.getString("airline"),
                rs.getString("from_city"),
                rs.getString("to_city"),
                rs.getString("date"),
                rs.getString("departure_time"),
                rs.getString("arrival_time"),
                rs.getDouble("price")
            });
        }

        JTable resultsTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));

        JFrame resultsFrame = new JFrame("One-Way Flight Search Results");
        resultsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultsFrame.add(scrollPane);
        resultsFrame.pack();
        resultsFrame.setLocationRelativeTo(this);
        resultsFrame.setVisible(true);
    }

    // ... (include other utility methods like getComponentByName, createPlaceholderTextField, etc.)

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OneWayFlightBooking app = new OneWayFlightBooking();
            app.setVisible(true);
        });
    }
}
