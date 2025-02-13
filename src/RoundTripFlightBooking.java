import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class RoundTripFlightBooking extends JFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/cab";
    private static final String USER = "root";
    private static final String PASS = "akshat";

    private static final Color PRIMARY_COLOR = new Color(92, 194, 191); // #5cc2bf
    private static final Color SECONDARY_COLOR = new Color(49, 151, 149); // #319795
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255); // #F0F8FF
    private static final Color SIDEBAR_COLOR = new Color(227, 244, 243); // #e3f4f3
    private static final Font HEADER_FONT = new Font("Samarkan", Font.PLAIN, 36);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

    private JButton flightButton;
    private Connection connection;
    private JPanel formFieldsPanel;
    private DefaultTableModel model;

    public RoundTripFlightBooking() {
        connectToDatabase();

        setTitle("Journey Mate - Round Trip Flight Booking");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        add(createHeader(), BorderLayout.NORTH);
        add(createSidebar(), BorderLayout.WEST);

        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(BACKGROUND_COLOR);
        mainContent.add(createBookingForm(), BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
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

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JLabel logoLabel = new JLabel("journey mate");
        logoLabel.setFont(HEADER_FONT);
        logoLabel.setForeground(PRIMARY_COLOR);
        header.add(logoLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        JButton aboutButton = new JButton("About");
        aboutButton.setFont(new Font("Arial", Font.BOLD, 14));
        aboutButton.setForeground(SECONDARY_COLOR);
        aboutButton.setBackground(Color.WHITE);
        aboutButton.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR));
        aboutButton.setFocusPainted(false);
        aboutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                aboutButton.setBackground(SECONDARY_COLOR);
                aboutButton.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                aboutButton.setBackground(Color.WHITE);
                aboutButton.setForeground(SECONDARY_COLOR);
            }
        });
        aboutButton.addActionListener(e -> openAboutPage());
        buttonPanel.add(aboutButton);

        header.add(buttonPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(4, 1, 0, 10));
        sidebar.setPreferredSize(new Dimension(100, getHeight()));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 5));

        String[] menuItems = {"Flight", "Train", "Cab", "Bus"};
        String[] iconPaths = {"icons/flights.png", "icons/train.png", "icons/cab.png", "icons/bus.png"};

        for (int i = 0; i < menuItems.length; i++) {
            JButton menuButton = createSidebarButton(menuItems[i], iconPaths[i]);
            
            if (menuItems[i].equals("Flight")) {
                flightButton = menuButton;
                flightButton.setBackground(PRIMARY_COLOR.darker());
                flightButton.setForeground(Color.WHITE);
            } else if (menuItems[i].equals("Bus")) {
                menuButton.addActionListener(e -> openBusApp());
            } else if (menuItems[i].equals("Cab")) {
                menuButton.addActionListener(e -> openCabBookingApp());
            } else if (menuItems[i].equals("Train")) {
                menuButton.addActionListener(e -> openTrainBookingApp());
            }
            
            sidebar.add(menuButton);
        }

        return sidebar;
    }

    private JButton createSidebarButton(String text, String iconPath) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(PRIMARY_COLOR.darker());
                } else if (getModel().isRollover() || text.equals("Flight")) {
                    g2.setColor(PRIMARY_COLOR);
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setLayout(new BoxLayout(button, BoxLayout.Y_AXIS));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);

        try {
            ImageIcon icon = new ImageIcon(new ImageIcon(iconPath).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
            button.setIcon(icon);
        } catch (Exception e) {
            e.printStackTrace();
        }

        button.setText(text);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);

        return button;
    }

    private JPanel createBookingForm() {
        JPanel bookingForm = new JPanel(new GridBagLayout());
        bookingForm.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        addFormField(bookingForm, "From:", createPlaceholderTextField("Enter origin city"), gbc);
        addFormField(bookingForm, "To:", createPlaceholderTextField("Enter destination city"), gbc);
        addFormField(bookingForm, "Departure Date:", createPlaceholderTextField("YYYY-MM-DD"), gbc);
        addFormField(bookingForm, "Return Date:", createPlaceholderTextField("YYYY-MM-DD"), gbc);
        addFormField(bookingForm, "Traveller Class:", createComboBox(new String[]{"Economy", "Business", "First Class"}), gbc);

        JButton searchButton = new JButton("Search Flights");
        searchButton.setBackground(PRIMARY_COLOR);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> searchRoundTripFlights());
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        bookingForm.add(searchButton, gbc);

        return bookingForm;
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

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setBackground(Color.WHITE);
        return comboBox;
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

    private void searchRoundTripFlights() {
        String fromCity = ((JTextField) getComponentByName(formFieldsPanel, "From:", 0)).getText();
        String toCity = ((JTextField) getComponentByName(formFieldsPanel, "To:", 0)).getText();
        String departureDate = ((JTextField) getComponentByName(formFieldsPanel, "Departure Date:", 0)).getText();
        String returnDate = ((JTextField) getComponentByName(formFieldsPanel, "Return Date:", 0)).getText();
        String travellerClass = (String) ((JComboBox<?>) getComponentByName(formFieldsPanel, "Traveller Class:", 0)).getSelectedItem();

        try {
            String query = "SELECT * FROM roundtrip WHERE from_city = ? AND to_city = ? AND departure_date = ? AND return_date = ? AND traveller_class = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, fromCity);
            pstmt.setString(2, toCity);
            pstmt.setString(3, departureDate);
            pstmt.setString(4, returnDate);
            pstmt.setString(5, travellerClass);

            ResultSet rs = pstmt.executeQuery();
            displayRoundTripResults(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching for flights: " + e.getMessage(), "Search Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayRoundTripResults(ResultSet rs) throws SQLException {
        model = new DefaultTableModel();
        model.addColumn("Flight Number");
        model.addColumn("Flight Company");
        model.addColumn("From");
        model.addColumn("To");
        model.addColumn("Departure Date");
        model.addColumn("Departure Time");
        model.addColumn("Return Date");
        model.addColumn("Return Time");
        model.addColumn("Traveller Class");

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("flight_number"),
                rs.getString("flight_company"),
                rs.getString("from_city"),
                rs.getString("to_city"),
                rs.getString("departure_date"),
                rs.getString("departure_time"),
                rs.getString("return_date"),
                rs.getString("return_time"),
                rs.getString("traveller_class")
            });
        }

        JTable resultsTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));

        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        JFrame resultsFrame = new JFrame("Round Trip Flight Search Results");
        resultsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultsFrame.setSize(850, 500);
        resultsFrame.setLayout(new BorderLayout());
        resultsFrame.add(resultsPanel, BorderLayout.CENTER);
        resultsFrame.setVisible(true);
    }

    private void openAboutPage() {
        SwingUtilities.invokeLater(() -> {
            new AboutJourneyMate().setVisible(true);
            this.dispose();
        });
    }

    private void openBusApp() {
        SwingUtilities.invokeLater(() -> {
            new BusApp().setVisible(true);
            this.dispose();
        });
    }

    private void openCabBookingApp() {
        SwingUtilities.invokeLater(() -> {
            new CabBookingApp().setVisible(true);
            this.dispose();
        });
    }

    private void openTrainBookingApp() {
        SwingUtilities.invokeLater(() -> {
            new TrainBookingApp().setVisible(true);
            this.dispose();
        });
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
            RoundTripFlightBooking app = new RoundTripFlightBooking();
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