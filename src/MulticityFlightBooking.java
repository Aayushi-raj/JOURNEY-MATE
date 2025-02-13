import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class MulticityFlightBooking extends JFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/cab";
    private static final String USER = "root";
    private static final String PASS = "akshat";

    private static final Color PRIMARY_COLOR = new Color(92, 194, 191);
    private static final Color SECONDARY_COLOR = new Color(49, 151, 149);
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255);

    private Connection connection;
    private JPanel formPanel;

    public MulticityFlightBooking() {
        setTitle("Multicity Flight Booking");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        connectToDatabase();
        initComponents();
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Successfully connected to the database!");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        JLabel titleLabel = new JLabel("Multicity Flight Booking");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        add(new JScrollPane(formPanel), BorderLayout.CENTER);

        createBookingForm();
    }

    private void createBookingForm() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        addFormField(formPanel, "FROM (First City):", createPlaceholderTextField("Enter origin"), gbc);
        addFormField(formPanel, "TO (First City):", createPlaceholderTextField("Enter destination"), gbc);
        addFormField(formPanel, "DATE (First Flight):", createPlaceholderTextField("DD/MM/YYYY"), gbc);
        addFormField(formPanel, "FROM (Second City):", createPlaceholderTextField("Enter origin"), gbc);
        addFormField(formPanel, "TO (Second City):", createPlaceholderTextField("Enter destination"), gbc);
        addFormField(formPanel, "DATE (Second Flight):", createPlaceholderTextField("DD/MM/YYYY"), gbc);

        String[] classes = {"Economy", "Premium Economy", "Business", "First Class"};
        JComboBox<String> classComboBox = new JComboBox<>(classes);
        addFormField(formPanel, "TRAVELLER CLASS:", classComboBox, gbc);

        JButton searchButton = new JButton("Search Flights");
        searchButton.setBackground(SECONDARY_COLOR);
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(e -> searchMulticityFlights());
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(searchButton, gbc);
    }

    private void searchMulticityFlights() {
        String firstFromCity = ((JTextField) getComponentByName(formPanel, "FROM (First City):", 0)).getText().trim();
        String firstToCity = ((JTextField) getComponentByName(formPanel, "TO (First City):", 0)).getText().trim();
        String firstFlightDate = ((JTextField) getComponentByName(formPanel, "DATE (First Flight):", 0)).getText().trim();
        String secondFromCity = ((JTextField) getComponentByName(formPanel, "FROM (Second City):", 0)).getText().trim();
        String secondToCity = ((JTextField) getComponentByName(formPanel, "TO (Second City):", 0)).getText().trim();
        String secondFlightDate = ((JTextField) getComponentByName(formPanel, "DATE (Second Flight):", 0)).getText().trim();
        String travellerClass = (String) ((JComboBox<?>) getComponentByName(formPanel, "TRAVELLER CLASS:", 0)).getSelectedItem();

        if (firstFromCity.isEmpty() || firstToCity.isEmpty() || firstFlightDate.isEmpty() || 
            secondFromCity.isEmpty() || secondToCity.isEmpty() || secondFlightDate.isEmpty() || 
            travellerClass == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (PreparedStatement pstmt = connection.prepareStatement(
                     "SELECT * FROM multicity WHERE first_from_city = ? AND first_to_city = ? AND first_flight_date = ? " +
                     "AND second_from_city = ? AND second_to_city = ? AND second_flight_date = ? AND traveller_class = ?")) {
            
            pstmt.setString(1, firstFromCity);
            pstmt.setString(2, firstToCity);
            pstmt.setString(3, firstFlightDate);
            pstmt.setString(4, secondFromCity);
            pstmt.setString(5, secondToCity);
            pstmt.setString(6, secondFlightDate);
            pstmt.setString(7, travellerClass.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                displayMulticityResults(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching for flights: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayMulticityResults(ResultSet rs) throws SQLException {
        DefaultTableModel model = new DefaultTableModel(new String[]{
            "First From", "First To", "First Date", "First Departure", "First Arrival", "First Flight No",
            "First Airline", "Second From", "Second To", "Second Date", "Second Departure", "Second Arrival",
            "Second Flight No", "Second Airline", "Class"
        }, 0);

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("first_from_city"),
                rs.getString("first_to_city"),
                rs.getString("first_flight_date"),
                rs.getString("first_departure_time"),
                rs.getString("first_arrival_time"),
                rs.getString("first_flight_number"),
                rs.getString("first_flight_company"),
                rs.getString("second_from_city"),
                rs.getString("second_to_city"),
                rs.getString("second_flight_date"),
                rs.getString("second_departure_time"),
                rs.getString("second_arrival_time"),
                rs.getString("second_flight_number"),
                rs.getString("second_flight_company"),
                rs.getString("traveller_class")
            });
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        JFrame resultsFrame = new JFrame("Multicity Flight Results");
        resultsFrame.add(scrollPane);
        resultsFrame.setSize(1000, 400);
        resultsFrame.setVisible(true);
    }

    private JTextField createPlaceholderTextField(String placeholder) {
        JTextField textField = new JTextField(15);
        textField.setForeground(Color.GRAY);
        textField.setText(placeholder);
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
        return textField;
    }

    private void addFormField(JPanel panel, String label, Component field, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }

    private Component getComponentByName(Container container, String name, int occurrence) {
        int found = 0;
        for (Component component : container.getComponents()) {
            if (component instanceof JLabel) {
                String labelText = ((JLabel) component).getText().trim().toLowerCase();
                if (labelText.startsWith(name.toLowerCase())) {
                    if (found == occurrence) {
                        int index = container.getComponentZOrder(component);
                        if (index + 1 < container.getComponentCount()) {
                            return container.getComponent(index + 1);
                        }
                    }
                    found++;
                }
            }
        }
        System.out.println("Component not found: " + name + " (occurrence: " + occurrence + ")");
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MulticityFlightBooking app = new MulticityFlightBooking();
            app.setVisible(true);
        });
    }

    @Override
    public void dispose() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing database connection:");
            e.printStackTrace();
        }
        super.dispose();
    }
}
