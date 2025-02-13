import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.JTableHeader; // Add this import statement

public class RoundTripFlight extends JFrame {
    private Connection connection;
    private JTextField fromField, toField, departureDateField, returnDateField;
    private JComboBox<String> classComboBox;

    private JPanel header; // Declare header panel
    private JPanel sidebar; // Declare sidebar panel
    private JPanel buttonPanel; // Declare button panel for One Way, Round Trip, Multicity

    private static final Color PRIMARY_COLOR = new Color(92, 194, 191); // #5cc2bf
    private static final Color SECONDARY_COLOR = new Color(49, 151, 149); // #319795
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255); // #F0F8FF

    private JButton oneWayButton; // Declare oneWayButton
    private JButton roundTripButton; // Declare roundTripButton
    private JButton multicityButton; // Declare multicityButton
    private static final Color TABLE_HEADER_COLOR = new Color(92, 194, 191); // Pastel header color
    private static final Color TABLE_BACKGROUND_COLOR = new Color(227, 244, 243); // Pastel table background color


    public RoundTripFlight() {
        setTitle("Round Trip Flight Search");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // Change to BorderLayout
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Create the header
        header = createHeader(); // Use the createHeader method
        add(header, BorderLayout.NORTH); // Add header to the top

        // Trip type buttons
        JPanel tripTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Ensure FlowLayout is used
        tripTypePanel.setOpaque(false); // Make sure the panel is transparent
        tripTypePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 10)); // Add padding to the trip type panel
        ButtonGroup tripTypeGroup = new ButtonGroup();

        // Create buttons
        JToggleButton oneWayButton = new JToggleButton("One Way");
        JToggleButton roundTripButton = new JToggleButton("Round Trip");
        JToggleButton multicityButton = new JToggleButton("Multicity");

        // Set button properties
        for (JToggleButton button : new JToggleButton[]{oneWayButton, roundTripButton, multicityButton}) {
            button.setPreferredSize(new Dimension(120, 30));
            button.setBackground(Color.WHITE);
            button.setForeground(PRIMARY_COLOR);
            button.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
            tripTypeGroup.add(button);
            tripTypePanel.add(button);
        }

        // Set the selected button to a darker color
        roundTripButton.setSelected(true); // Set Round Trip button as selected
        roundTripButton.setBackground(PRIMARY_COLOR.darker()); // Darker color for the selected button

        // Add action listeners to the trip type buttons
        oneWayButton.addActionListener(e -> {
            new OneWayFlight(); // Open One Way Flight window
            this.dispose(); // Close current window
        });

        roundTripButton.addActionListener(e -> {
            new RoundTripFlight(); // Open Round Trip Flight window
            this.dispose(); // Close current window
        });

        multicityButton.addActionListener(e -> {
            new MultiCityFlight(); // Open MultiCity Flight window
            this.dispose(); // Close current window
        });

        // Initialize mainContent
        JPanel mainContent = new JPanel(new BorderLayout()); // Use BorderLayout for main content
        mainContent.setBackground(BACKGROUND_COLOR);

        // Add the trip type panel to the main content
        mainContent.add(tripTypePanel, BorderLayout.NORTH); // Adjusted to ensure proper layout

        // Sidebar
        sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Create a panel for the input fields
        JPanel inputPanel = createInputPanel();
        mainContent.add(inputPanel, BorderLayout.CENTER); // Add input panel to the center
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding to the input panel

        // Add main content to the center
        add(mainContent, BorderLayout.CENTER); // Adjusted to ensure proper layout

        connectToDatabase();
        setVisible(true);
    }

    private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/cab";
            String username = "root";
            String password = "akshat";
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JLabel logoLabel = new JLabel("journey mate");
        logoLabel.setFont(new Font("Samarkan", Font.PLAIN, 36));
        logoLabel.setForeground(PRIMARY_COLOR);
        header.add(logoLabel, BorderLayout.WEST);

        // Create a panel for the About button with padding
        JPanel aboutButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aboutButtonPanel.setBackground(Color.WHITE);
        aboutButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10)); // Add padding to the right

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
            this.dispose(); // Close the current RoundTripFlight window
        });

        aboutButtonPanel.add(aboutButton);
        header.add(aboutButtonPanel, BorderLayout.EAST); // Add the button panel to the right side of the header

        return header;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Add vertical padding

        oneWayButton = new JButton("One Way");
        roundTripButton = new JButton("Round Trip");
        multicityButton = new JButton("Multicity");

        // Set preferred size for buttons
        Dimension buttonSize = new Dimension(120, 40);
        oneWayButton.setPreferredSize(buttonSize);
        roundTripButton.setPreferredSize(buttonSize);
        multicityButton.setPreferredSize(buttonSize);

        // Style buttons
        styleButton(oneWayButton);
        styleButton(roundTripButton);
        styleButton(multicityButton);

        // Add action listeners
        oneWayButton.addActionListener(e -> {
            System.out.println("One Way selected");
        });
        roundTripButton.addActionListener(e -> {
            System.out.println("Round Trip selected");
        });
        multicityButton.addActionListener(e -> {
            System.out.println("Multicity selected");
        });

        buttonPanel.add(oneWayButton);
        buttonPanel.add(roundTripButton);
        buttonPanel.add(multicityButton);

        return buttonPanel;
    }

    private void styleButton(JButton button) {
        button.setBackground(Color.WHITE);
        button.setForeground(PRIMARY_COLOR);
        button.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(100, 40)); // Set button size
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(4, 1, 0, 10));
        sidebar.setPreferredSize(new Dimension(100, 800)); // Set height to match JourneyMateApp
        sidebar.setBackground(new Color(227, 244, 243)); // SIDEBAR_COLOR
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 5)); // Reduced horizontal padding

        String[] menuItems = {"Flight", "Train", "Cab", "Bus"};
        String[] iconPaths = {"icons/flights.png", "icons/train.png", "icons/cab.png", "icons/bus.png"};

        for (int i = 0; i < menuItems.length; i++) {
            JButton menuButton = createSidebarButton(menuItems[i], iconPaths[i]);
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
        button.setFont(new Font("Arial", Font.BOLD, 1)); // Smaller font size
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Reduced padding
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);

        // Load icon
        try {
            ImageIcon icon = new ImageIcon(iconPath);
            Image img = icon.getImage();
            Image newImg = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH); // Smaller icon size
            JLabel iconLabel = new JLabel(new ImageIcon(newImg));
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.add(iconLabel);
        } catch (Exception e) {
            System.out.println("Icon not found: " + e.getMessage());
        }

        // Add text below icon
        JLabel textLabel = new JLabel(text);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
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

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Increased padding

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("FROM:"), gbc);

        gbc.gridx = 1;
        fromField = createStyledTextField("FROM"); // {{ edit_1 }} Added placeholder
        inputPanel.add(fromField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("TO:"), gbc);

        gbc.gridx = 1;
        toField = createStyledTextField("TO"); // {{ edit_2 }} Added placeholder
        inputPanel.add(toField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("DEPARTURE DATE:"), gbc);

        gbc.gridx = 1;
        departureDateField = createStyledTextField("yyyy-mm-dd"); // {{ edit_3 }} Added placeholder
        inputPanel.add(departureDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("RETURN DATE:"), gbc);

        gbc.gridx = 1;
        returnDateField = createStyledTextField("yyyy-mm-dd"); // {{ edit_4 }} Added placeholder
        inputPanel.add(returnDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(new JLabel("TRAVELLER & CLASS:"), gbc);

        gbc.gridx = 1;
        classComboBox = new JComboBox<>(new String[]{"Economy", "Business"});
        inputPanel.add(classComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2; // Span across two columns
        JButton searchButton = new JButton("SEARCH ROUND TRIP");
        searchButton.setBackground(new Color(26, 59, 93)); // Updated button color
        searchButton.setForeground(Color.WHITE);
        searchButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding to button
        searchButton.addActionListener(e -> searchFlights());
        inputPanel.add(searchButton, gbc);

        // {{ edit_1 }} Add Book button
        gbc.gridy = 6; // Move to the next row
        JButton bookButton = new JButton("BOOK");
        bookButton.setPreferredSize(new Dimension(200, 40)); // Set preferred size
        bookButton.setBackground(new Color(26, 59, 93)); // #1a3b5d
        bookButton.setForeground(Color.WHITE); // Set text color
        bookButton.setFont(new Font("Arial", Font.BOLD, 14)); // Set font style
        bookButton.addActionListener(e -> bookFlight()); // Add action listener
        inputPanel.add(bookButton, gbc);

        return inputPanel; // Return the input panel
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder);
        textField.setForeground(Color.GRAY); // Set placeholder color
        textField.setPreferredSize(new Dimension(200, 30)); // Set preferred size

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

    private void searchFlights() {
        String from = fromField.getText().trim();
        String to = toField.getText().trim();
        String departureDate = departureDateField.getText().trim();
        String returnDate = returnDateField.getText().trim();
        String travellerClass = (String) classComboBox.getSelectedItem();

        if (from.isEmpty() || to.isEmpty() || departureDate.isEmpty() || returnDate.isEmpty() || travellerClass == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields correctly.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "SELECT flight_number, flight_company, from_city, to_city, departure_date, return_date, traveller_class FROM roundtrip WHERE from_city = ? AND to_city = ? AND departure_date = ? AND return_date = ? AND traveller_class = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, from);
            pstmt.setString(2, to);
            pstmt.setString(3, departureDate);
            pstmt.setString(4, returnDate);
            pstmt.setString(5, travellerClass);
            ResultSet rs = pstmt.executeQuery();

            // Create a table to display results
            String[] columnNames = {"Flight Number", "Flight Company", "From", "To", "Departure Date", "Return Date", "Class"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            if (!rs.isBeforeFirst()) { // Check if there are results
                JOptionPane.showMessageDialog(this, "No matching flights found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("flight_number"),
                        rs.getString("flight_company"),
                        rs.getString("from_city"),
                        rs.getString("to_city"),
                        rs.getString("departure_date"),
                        rs.getString("return_date"),
                        rs.getString("traveller_class")
                    });
                }

                // Create a JTable
                JTable resultsTable = new JTable(model);
                resultsTable.setFillsViewportHeight(true);
                resultsTable.setRowHeight(30);
                resultsTable.setFont(new Font("Arial", Font.PLAIN, 14));
                resultsTable.setBackground(new Color(227, 244, 243)); // Pastel color for the table background

                // Set header background color
                JTableHeader header = resultsTable.getTableHeader();
                header.setBackground(new Color(92, 194, 191)); // Pastel color for the header
                header.setForeground(Color.WHITE); // White text for contrast
                header.setFont(new Font("Arial", Font.BOLD, 14)); // Bold font for header

                // Add a scroll pane for the table
                JScrollPane scrollPane = new JScrollPane(resultsTable);
            scrollPane.setPreferredSize(new Dimension(800, 400)); // Set preferred size for the scroll pane
            scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // Add border to scroll pane

                // Show the results in a dialog without the icon
                JOptionPane.showMessageDialog(this, scrollPane, "Round Trip Flight Details", JOptionPane.PLAIN_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching flights.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bookFlight() {
        // Prompt for input
        String fromCity = JOptionPane.showInputDialog(this, "Enter departure city:");
        String toCity = JOptionPane.showInputDialog(this, "Enter destination city:");
        String date = JOptionPane.showInputDialog(this, "Enter date (yyyy-mm-dd):");

        // Create a new JFrame for booking confirmation
        JFrame bookingFrame = new JFrame("Booking Confirmation");
        bookingFrame.setSize(300, 150); // Adjusted size for minimalism
        bookingFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        bookingFrame.setLayout(new GridLayout(4, 1, 5, 5)); // Use GridLayout for better organization

        // Create a message panel
        JPanel messagePanel = new JPanel(new GridLayout(4, 1)); // Organize labels in a grid
        messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        messagePanel.setBackground(new Color(240, 248, 255)); // Light background color

        // Add labels with centered text and color
        JLabel confirmationLabel = new JLabel("Your flight is scheduled and booked!", SwingConstants.CENTER);
        confirmationLabel.setForeground(new Color(26, 59, 93)); // Dark text color
        messagePanel.add(confirmationLabel);

        JLabel fromLabel = new JLabel("From: " + fromCity, SwingConstants.CENTER);
        fromLabel.setForeground(new Color(26, 59, 93)); // Dark text color
        messagePanel.add(fromLabel);

        JLabel toLabel = new JLabel("To: " + toCity, SwingConstants.CENTER);
        toLabel.setForeground(new Color(26, 59, 93)); // Dark text color
        messagePanel.add(toLabel);

        JLabel dateLabel = new JLabel("Date: " + date, SwingConstants.CENTER);
        dateLabel.setForeground(new Color(26, 59, 93)); // Dark text color
        messagePanel.add(dateLabel);

        bookingFrame.add(messagePanel);
        bookingFrame.getContentPane().setBackground(new Color(255, 255, 255)); // Set frame background color
        bookingFrame.setVisible(true);
    }

    private static TableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        Vector<String> columnNames = new Vector<>();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                row.add(rs.getObject(columnIndex));
            }
            data.add(row);
        }

        return new DefaultTableModel(data, columnNames);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RoundTripFlight::new);
    }
}
