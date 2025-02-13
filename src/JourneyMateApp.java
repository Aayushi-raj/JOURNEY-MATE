import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Collections; // Add this import statement
import javax.swing.table.JTableHeader; // Add this import statement



public class JourneyMateApp extends JFrame {
    private static final String URL = "jdbc:mysql://localhost:3306/cab";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "akshat";

    private static final Color PRIMARY_COLOR = new Color(92, 194, 191); // #5cc2bf
    private static final Color SECONDARY_COLOR = new Color(49, 151, 149); // #319795
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255); // #F0F8FF
    private static final Color SIDEBAR_COLOR = new Color(227, 244, 243); // #e3f4f3
    private static final Font HEADER_FONT = new Font("Samarkan", Font.PLAIN, 36);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

    private JButton flightButton;
    private Connection connection;

    private JPanel formFieldsPanel;

    private DefaultTableModel model; // Declare model at the class level

    private ButtonGroup tripTypeGroup; // Declare tripTypeGroup at the class level

    public JourneyMateApp() {
        // Establish database connection
        connectToDatabase();

        setTitle("Journey Mate");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        JLabel logo = new JLabel("journey mate");
        logo.setFont(new Font("Samarkan", Font.PLAIN, 36));
        logo.setForeground(PRIMARY_COLOR);
        // Header
        JPanel header = createHeader();
        header.add(logo, BorderLayout.WEST);

        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Main content
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(BACKGROUND_COLOR);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding around main content

        // Booking form
        JPanel bookingForm = createBookingForm();
        mainContent.add(bookingForm);

        add(mainContent, BorderLayout.CENTER);

        tripTypeGroup = new ButtonGroup(); // Initialize tripTypeGroup in the constructor

        // Add the header to the frame
        add(createHeader(), BorderLayout.NORTH);
    }

    private void connectToDatabase() {
        try {
            // Print out the classpath
            System.out.println("Classpath: " + System.getProperty("java.class.path"));

            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC driver loaded successfully");
            
            System.out.println("Attempting to connect to database...");
            System.out.println("URL: " + URL);
            System.out.println("Username: " + USERNAME);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Successfully connected to the database!");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC driver not found: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "MySQL JDBC driver not found: " + e.getMessage(), "Driver Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            System.out.println("Error loading MySQL JDBC driver or connecting to database:");
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load driver or connect to the database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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

        // Create a panel for the About button with padding
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10)); // Add padding to the right

        // Create "About" button
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

        // Add action listener to open AboutJourneyMate
        aboutButton.addActionListener(e -> {
            new AboutJourneyMate().setVisible(true);
            this.dispose(); // Close the current JourneyMateApp window
        });

        buttonPanel.add(aboutButton);
        header.add(buttonPanel, BorderLayout.EAST); // Add the button panel to the right side of the header

        return header;
    }

   

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(4, 1, 0, 10));
        sidebar.setPreferredSize(new Dimension(150, getHeight())); // Fixed width and height
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 5));

        String[] menuItems = {"Flight", "Train", "Cab", "Bus"};
        String[] iconPaths = {"icons/flights.png", "icons/train.png", "icons/cab.png", "icons/bus.png"};

        for (int i = 0; i < menuItems.length; i++) {
            JButton menuButton = createSidebarButton(menuItems[i], iconPaths[i]);
            
            // Add action listeners for each button
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
        button.setFont(new Font("Arial", Font.BOLD, 12)); // Smaller font
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);

        // Load icon
        try {
            ImageIcon icon = new ImageIcon(iconPath);
            Image img = icon.getImage();
            Image newImg = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH); // Set icon size to 30x30
            JLabel iconLabel = new JLabel(new ImageIcon(newImg));
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.add(iconLabel);
        } catch (Exception e) {
            System.out.println("Icon not found: " + e.getMessage());
        }

        // Add text below icon
        JLabel textLabel = new JLabel(text);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textLabel.setPreferredSize(new Dimension(100, 30)); // Set a fixed size for text labels
        button.add(Box.createRigidArea(new Dimension(0, 5))); // Add space between icon and text
        button.add(textLabel);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.BLACK);
            }
        });

        return button;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(SECONDARY_COLOR);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR));
        button.setFocusPainted(false);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(SECONDARY_COLOR);
                button.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
                button.setForeground(SECONDARY_COLOR);
            }
        });
        return button;
    }

    private JPanel createBookingForm() {
        JPanel bookingForm = new JPanel();
        bookingForm.setLayout(new BoxLayout(bookingForm, BoxLayout.Y_AXIS));
        bookingForm.setBackground(BACKGROUND_COLOR);
        bookingForm.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Trip type buttons
        JPanel tripTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tripTypePanel.setOpaque(false);
        String[] tripTypes = {"One Way", "Round Trip", "Multicity"};
        ButtonGroup tripTypeGroup = new ButtonGroup();

        // Create a panel for the form fields
        formFieldsPanel = new JPanel(new GridBagLayout());
        formFieldsPanel.setOpaque(false);

        for (String type : tripTypes) {
            JToggleButton button = new JToggleButton(type);
            button.setPreferredSize(new Dimension(120, 30));
            button.setBackground(Color.WHITE);
            button.setForeground(PRIMARY_COLOR);
            button.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
            tripTypeGroup.add(button);
            tripTypePanel.add(button);
            
            button.addActionListener(e -> {
                updateFormLayout(formFieldsPanel, type);
                bookingForm.revalidate();
                bookingForm.repaint();
                
                // Add this block to handle the "One Way" button click
                if (type.equals("One Way")) {
                    new OneWayFlight(); // Open One Way Flight window
                    this.dispose(); // Close current JourneyMateApp window
                }
            });
        } 

        bookingForm.add(tripTypePanel);
        
        // Add vertical space between trip type buttons and form fields
        bookingForm.add(Box.createRigidArea(new Dimension(0, 20)));

        bookingForm.add(formFieldsPanel);

        // Set the first button (One Way) as selected by default
        ((JToggleButton)tripTypePanel.getComponent(0)).setSelected(true);

        // Initial form layout (One Way by default)
        updateFormLayout(formFieldsPanel, "One Way");

        model = new DefaultTableModel(); // Initialize model here
        model.addColumn("Flight No");
        model.addColumn("Departure");
        model.addColumn("Arrival");
        model.addColumn("Date");
        model.addColumn("Class");

        return bookingForm;
    }

    private void updateFormLayout(JPanel formFieldsPanel, String tripType) {
        formFieldsPanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        if (tripType.equals("Multicity")) {
            for (int i = 0; i < 2; i++) {
                addFormField(formFieldsPanel, "FROM:", createPlaceholderTextField("FROM"), gbc);
                addFormField(formFieldsPanel, "TO:", createPlaceholderTextField("TO"), gbc);
                addFormField(formFieldsPanel, "DATE:", createPlaceholderTextField("yyyy-mm-dd"), gbc);
            }
        } else {
            addFormField(formFieldsPanel, "FROM:", createPlaceholderTextField("FROM"), gbc);
            addFormField(formFieldsPanel, "TO:", createPlaceholderTextField("TO"), gbc);
            addFormField(formFieldsPanel, "DEPARTURE DATE:", createPlaceholderTextField("yyyy-mm-dd"), gbc);

            if (tripType.equals("Round Trip")) {
                addFormField(formFieldsPanel, "RETURN DATE:", createPlaceholderTextField("yyyy-mm-dd"), gbc);
            }
        }

        // Traveller & Class dropdown
        String[] travellerOptions = {" Economy", " Business"};
        addFormField(formFieldsPanel, "TRAVELLER & CLASS:", new JComboBox<>(travellerOptions), gbc);

        // Search button
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton searchButton = createSearchButton(tripType);
        formFieldsPanel.add(searchButton, gbc);

        // User categories checkboxes
        gbc.gridy++;
        gbc.gridwidth = 2;
        JPanel categoriesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] categories = {"Defence Forces", "Students", "Senior Citizens", "Doctors Nurses"};
        for (String category : categories) {
            categoriesPanel.add(new JCheckBox(category));
        }
        formFieldsPanel.add(categoriesPanel, gbc);
    }

    private JButton createSearchButton(String tripType) {
        JButton searchButton = new JButton("SEARCH " + tripType.toUpperCase());
        searchButton.setPreferredSize(new Dimension(200, 40));
        searchButton.setBackground(new Color(26, 59, 93)); // #1a3b5d
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        searchButton.addActionListener(e -> {
            switch (tripType) {
                case "One Way":
                    searchOneWayFlights();
                    break;
                case "Round Trip":
                    searchRoundTripFlights();
                    break;
                case "Multicity":
                    searchMulticityFlights();
                    break;
            }
        });
        
        return searchButton;
    }

    private void searchOneWayFlights() {
        // Implement one-way flight search logic
        Component fromComp = getComponentByName(formFieldsPanel, "FROM:", 0);
        Component toComp = getComponentByName(formFieldsPanel, "TO:", 0);
        Component dateComp = getComponentByName(formFieldsPanel, "DEPARTURE DATE:", 0);
        Component classComp = getComponentByName(formFieldsPanel, "TRAVELLER & CLASS:", 0);

        String from = ((JTextField) fromComp).getText().trim();
        String to = ((JTextField) toComp).getText().trim();
        String departureDate = ((JTextField) dateComp).getText().trim();
        String travellerClass = "";
        if (classComp instanceof JComboBox<?>) {
            travellerClass = (String) ((JComboBox<?>) classComp).getSelectedItem();
        }

        if (from.isEmpty() || to.isEmpty() || departureDate.isEmpty() || travellerClass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields correctly.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        searchOneWay(from, to, departureDate, travellerClass);
    }

    private void searchRoundTripFlights() {
        // Implement round trip flight search logic
        Component fromComp = getComponentByName(formFieldsPanel, "FROM:", 0);
        Component toComp = getComponentByName(formFieldsPanel, "TO:", 0);
        Component dateComp = getComponentByName(formFieldsPanel, "DEPARTURE DATE:", 0);
        Component classComp = getComponentByName(formFieldsPanel, "TRAVELLER & CLASS:", 0);
        Component returnDateComp = getComponentByName(formFieldsPanel, "RETURN DATE:", 0);
        String returnDate = ((JTextField) returnDateComp).getText().trim();

        String from = ((JTextField) fromComp).getText().trim();
        String to = ((JTextField) toComp).getText().trim();
        String departureDate = ((JTextField) dateComp).getText().trim();
        String travellerClass = "";
        if (classComp instanceof JComboBox<?>) {
            travellerClass = (String) ((JComboBox<?>) classComp).getSelectedItem();
        }

        if (from.isEmpty() || to.isEmpty() || departureDate.isEmpty() || returnDate.isEmpty() || travellerClass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields correctly.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        searchRoundTrip(from, to, departureDate, returnDate, travellerClass);
    }

    private void searchMulticityFlights() {
        // Implement multicity flight search logic
        Component fromComp1 = getComponentByName(formFieldsPanel, "FROM:", 0);
        Component toComp1 = getComponentByName(formFieldsPanel, "TO:", 0);
        Component dateComp1 = getComponentByName(formFieldsPanel, "DATE:", 0);
        Component fromComp2 = getComponentByName(formFieldsPanel, "FROM:", 1);
        Component toComp2 = getComponentByName(formFieldsPanel, "TO:", 1);
        Component dateComp2 = getComponentByName(formFieldsPanel, "DATE:", 1);
        Component classComp = getComponentByName(formFieldsPanel, "TRAVELLER & CLASS:", 0);

        String from1 = ((JTextField) fromComp1).getText().trim();
        String to1 = ((JTextField) toComp1).getText().trim();
        String date1 = ((JTextField) dateComp1).getText().trim();
        String from2 = ((JTextField) fromComp2).getText().trim();
        String to2 = ((JTextField) toComp2).getText().trim();
        String date2 = ((JTextField) dateComp2).getText().trim();
        String travellerClass = (String) ((JComboBox<?>) classComp).getSelectedItem();

        if (from1.isEmpty() || to1.isEmpty() || date1.isEmpty() || 
            from2.isEmpty() || to2.isEmpty() || date2.isEmpty() || 
            travellerClass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields correctly.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        searchMulticity(from1, to1, date1, from2, to2, date2, travellerClass);
    }

    private void searchOneWay(String from, String to, String departureDate, String travellerClass) {
        String query = "SELECT * FROM flight WHERE from_city = ? AND to_city = ? AND departure_date = ? AND traveller_class = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, from);
            pstmt.setString(2, to);
            pstmt.setString(3, departureDate);
            pstmt.setString(4, travellerClass);
            ResultSet rs = pstmt.executeQuery();
            displayResults(rs, "One Way Flight Results", "One Way");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchRoundTrip(String from, String to, String departureDate, String returnDate, String travellerClass) {
        String query = "SELECT * FROM roundtrip WHERE from_city = ? AND to_city = ? AND departure_date = ? AND return_date = ? AND traveller_class = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, from);
            pstmt.setString(2, to);
            pstmt.setString(3, departureDate);
            pstmt.setString(4, returnDate);
            pstmt.setString(5, travellerClass);
            ResultSet rs = pstmt.executeQuery();
            displayResults(rs, "Round Trip Flight Results", "Round Trip");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching for round trip flights: " + e.getMessage(), "Search Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchMulticity(String from, String to, String departureDate, String secondFrom, String secondTo, String secondDate, String travellerClass) {
        String query = "SELECT * FROM multicity WHERE first_from_city = ? AND first_to_city = ? AND first_flight_date = ? AND second_from_city = ? AND second_to_city = ? AND second_flight_date = ? AND traveller_class = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, from);
            pstmt.setString(2, to);
            pstmt.setString(3, departureDate);
            pstmt.setString(4, secondFrom);
            pstmt.setString(5, secondTo);
            pstmt.setString(6, secondDate);
            pstmt.setString(7, travellerClass);
            ResultSet rs = pstmt.executeQuery();
            displayResults(rs, "Multicity Flight Results", "Multicity");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching for multicity flights: " + e.getMessage(), "Search Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayResults(ResultSet rs, String title, String tripType) {
        JFrame resultsFrame = new JFrame(title);
        resultsFrame.setSize(600, 400);
        resultsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultsFrame.setLayout(new BorderLayout());

        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultsPanel.add(titleLabel);
        resultsPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        String[] columnNames;
        if (tripType.equals("Round Trip")) {
            columnNames = new String[]{"Flight Number", "Flight Company", "From", "To", "Departure Date", "Departure Time", "Return Date", "Return Time", "Class"};
        } else {
            columnNames = new String[]{"Flight ID", "From", "To", "Date", "Class"};
        }
        
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        try {
            
                while (rs.next()) {
                    if (tripType.equals("Round Trip")) {
                        String flightNumber = rs.getString("flight_number");
                        String flightCompany = rs.getString("flight_company");
                        String from = rs.getString("from_city");
                        String to = rs.getString("to_city");
                        String departureDate = rs.getString("departure_date");
                        String departureTime = rs.getString("departure_time");
                        String returnDate = rs.getString("return_date");
                        String returnTime = rs.getString("return_time");
                        String travellerClass = rs.getString("traveller_class");

                        tableModel.addRow(new Object[]{flightNumber, flightCompany, from, to, departureDate, departureTime, returnDate, returnTime, travellerClass});
                    } else {
                        String flightId = rs.getString("flight_id");
                        String from = rs.getString("from_city");
                        String to = rs.getString("to_city");
                        String date = rs.getString("departure_date");
                        String travellerClass = rs.getString("traveller_class");

                        tableModel.addRow(new Object[]{flightId, from, to, date, travellerClass});
                    }
                }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JTable resultsTable = new JTable(tableModel);
        resultsTable.setFillsViewportHeight(true);
        resultsTable.setRowHeight(30);
        resultsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        resultsTable.setBackground(new Color(227, 244, 243));
        resultsTable.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JTableHeader header = resultsTable.getTableHeader();
        header.setBackground(new Color(92, 194, 191));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        resultsPanel.add(scrollPane);

        resultsFrame.add(resultsPanel, BorderLayout.CENTER);
        resultsFrame.setVisible(true);
    }

    private String getSelectedTripType() {
        for (AbstractButton button : Collections.list(tripTypeGroup.getElements())) {
            if (button.isSelected()) {
                return button.getText(); // Return the text of the selected button
            }
        }
        return "One Way"; // Default return value if none is selected
    }

    private JTextField createPlaceholderTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder);
        textField.setForeground(Color.GRAY); // Set placeholder color

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK); // Set text color to black when typing
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY); // Restore placeholder color
                    textField.setText(placeholder);
                }
            }
        });

        return textField;
    }

    private void openAboutPage() {
        SwingUtilities.invokeLater(() -> {
            new AboutJourneyMate().setVisible(true);
            this.setVisible(false); // Hide the current JourneyMateApp window
        });
    }

    private void openBusApp() {
        SwingUtilities.invokeLater(() -> {
            new BusApp().setVisible(true);
            this.dispose(); // Close the current JourneyMateApp window
        });
    }

    private void openCabBookingApp() {
        SwingUtilities.invokeLater(() -> {
            new CabBookingApp().setVisible(true);
            this.dispose(); // Close the current JourneyMateApp window
        });
    }

    private void openTrainBookingApp() {
        SwingUtilities.invokeLater(() -> {
            new TrainBookingApp().setVisible(true);
            this.dispose(); // Close the current JourneyMateApp window
        });
    }

    private void openSignupForm(String initialView) {
        SwingUtilities.invokeLater(() -> {
            SignupForm signupForm = new SignupForm(this);
            signupForm.showInitialView(initialView);
            signupForm.setVisible(true);
            this.setVisible(false); // Hide the JourneyMateApp window
        });
    }

    private void refreshJourneyMateApp() {
        SwingUtilities.invokeLater(() -> {
            new JourneyMateApp().setVisible(true);
            this.dispose(); // Close the current JourneyMateApp window
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JourneyMateApp app = new JourneyMateApp();
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

    private void addFormField(JPanel panel, String label, Component field, GridBagConstraints gbc) {
        gbc.gridx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
        gbc.gridy++;
    }
}
