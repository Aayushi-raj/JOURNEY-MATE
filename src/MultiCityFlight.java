import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

public class MultiCityFlight extends JFrame {
    private Connection connection;
    private JTextField fromField1, toField1, dateField1;
    private JTextField fromField2, toField2, dateField2;
    private JComboBox<String> classComboBox;
    private static final Color PRIMARY_COLOR = new Color(92, 194, 191); // #5cc2bf
    private static final Color SECONDARY_COLOR = new Color(49, 151, 149); // #319795
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255); // #F0F8FF
    private static final Color SIDEBAR_COLOR = new Color(227, 244, 243); // #e3f4f3
    private static final Font HEADER_FONT = new Font("Samarkan", Font.PLAIN, 36);
    private JPanel mainContent;

    public MultiCityFlight() {
        setTitle("Multicity Flight Search");
        setSize(1000, 800); // Adjusted frame size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // Changed layout to BorderLayout

        // Create the header
        JPanel header = createHeader();
        add(header, BorderLayout.NORTH); // Add header to the top

        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

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
        multicityButton.setSelected(true); // Set Multicity button as selected
        multicityButton.setBackground(PRIMARY_COLOR.darker()); // Darker color for the selected button

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
        mainContent = new JPanel(new BorderLayout()); // Use BorderLayout for main content
        mainContent.setBackground(BACKGROUND_COLOR);

        // Add the trip type panel to the main content
        mainContent.add(tripTypePanel, BorderLayout.NORTH); // Adjusted to ensure proper layout

        // Create a panel for the input fields
        JPanel inputPanel = createInputPanel();
        mainContent.add(inputPanel, BorderLayout.CENTER); // Add input panel to the center

        // Add main content to the center
        add(mainContent, BorderLayout.CENTER); // Adjusted to ensure proper layout

        connectToDatabase();
        setVisible(true);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new GridBagLayout()); // Changed to GridBagLayout
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel logoLabel = new JLabel("journey mate");
        logoLabel.setFont(HEADER_FONT);
        logoLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        header.add(logoLabel, gbc);

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
        aboutButton.addActionListener(e -> {
            new AboutJourneyMate().setVisible(true);
            this.dispose(); // Close the current MultiCityFlight window
        });

        buttonPanel.add(aboutButton);
        gbc.gridx = 1; // Adjusted position for About button
        header.add(buttonPanel, gbc); // Add the button panel to the right side of the header

        return header;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(4, 1, 0, 10));
        sidebar.setPreferredSize(new Dimension(100, getHeight())); // Fixed width and height
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 5));

        String[] menuItems = {"Flight", "Train", "Cab", "Bus"};
        String[] iconPaths = {"icons/flights.png", "icons/train.png", "icons/cab.png", "icons/bus.png"};

        for (int i = 0; i < menuItems.length; i++) {
            JButton menuButton = createSidebarButton(menuItems[i], iconPaths[i]);
            sidebar.add(menuButton);
        }

        return sidebar;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Increased padding between fields

        // Input fields for first leg
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("From :"), gbc);
        gbc.gridx = 1;
        fromField1 = createStyledTextField("FROM"); // {{ edit_1 }} Add placeholder
        inputPanel.add(fromField1, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("To :"), gbc);
        gbc.gridx = 1;
        toField1 = createStyledTextField("TO"); // {{ edit_2 }} Add placeholder
        inputPanel.add(toField1, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Date :"), gbc);
        gbc.gridx = 1;
        dateField1 = createStyledTextField("yyyy-mm-dd"); // {{ edit_3 }} Add placeholder
        inputPanel.add(dateField1, gbc);

        // Input fields for second leg
        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("From :"), gbc);
        gbc.gridx = 1;
        fromField2 = createStyledTextField("FROM"); // {{ edit_4 }} Add placeholder
        inputPanel.add(fromField2, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(new JLabel("To :"), gbc);
        gbc.gridx = 1;
        toField2 = createStyledTextField("TO"); // {{ edit_5 }} Add placeholder
        inputPanel.add(toField2, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        inputPanel.add(new JLabel("Date :"), gbc);
        gbc.gridx = 1;
        dateField2 = createStyledTextField("yyyy-mm-dd"); // {{ edit_6 }} Add placeholder
        inputPanel.add(dateField2, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        inputPanel.add(new JLabel("Class:"), gbc);
        gbc.gridx = 1;
        classComboBox = new JComboBox<>(new String[]{"Economy", "Business"});
        inputPanel.add(classComboBox, gbc);

        // Search button
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2; // Span across two columns
        JButton searchButton = new JButton("Search Multicity");
        searchButton.setPreferredSize(new Dimension(200, 40)); // Set preferred size
        searchButton.setBackground(new Color(26, 59, 93)); // #1a3b5d
        searchButton.setForeground(Color.WHITE); // Set text color
        searchButton.setFont(new Font("Arial", Font.BOLD, 14)); // Set font style
        searchButton.addActionListener(e -> searchFlights());
        inputPanel.add(searchButton, gbc);

        // {{ edit_1 }} Add Book button
        gbc.gridy = 8; // Move to the next row
        JButton bookButton = new JButton("BOOK");
        bookButton.setPreferredSize(new Dimension(200, 40)); // Set preferred size
        bookButton.setBackground(new Color(26, 59, 93)); // #1a3b5d
        bookButton.setForeground(Color.WHITE); // Set text color
        bookButton.setFont(new Font("Arial", Font.BOLD, 14)); // Set font style
        bookButton.addActionListener(e -> bookFlight()); // Add action listener
        inputPanel.add(bookButton, gbc);

        return inputPanel;
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

    private void searchFlights() {
        String from1 = fromField1.getText().trim();
        String to1 = toField1.getText().trim();
        String date1 = dateField1.getText().trim();
        String from2 = fromField2.getText().trim();
        String to2 = toField2.getText().trim();
        String date2 = dateField2.getText().trim();
        String travellerClass = (String) classComboBox.getSelectedItem();

        String query = "SELECT * FROM multicity WHERE first_from_city = ? AND first_to_city = ? AND first_flight_date = ? AND second_from_city = ? AND second_to_city = ? AND second_flight_date = ? AND traveller_class = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, from1);
            pstmt.setString(2, to1);
            pstmt.setString(3, date1);
            pstmt.setString(4, from2);
            pstmt.setString(5, to2);
            pstmt.setString(6, date2);
            pstmt.setString(7, travellerClass);
            ResultSet rs = pstmt.executeQuery();

            // Create a table to display results
            JTable flightTable = new JTable(buildTableModel(rs));
            JScrollPane scrollPane = new JScrollPane(flightTable);
            
            // Set the results window properties
            JFrame resultsFrame = new JFrame("Multicity Flight Details");
            resultsFrame.setSize(1200, 600); // Increased width for better visibility
            resultsFrame.setLayout(new BorderLayout());
            
            // Set background color
            resultsFrame.getContentPane().setBackground(new Color(227, 244, 243));
            scrollPane.setBackground(new Color(227, 244, 243));
            flightTable.setBackground(new Color(227, 244, 243));
            flightTable.setFont(new Font("Arial", Font.PLAIN, 14));
            
            // Set header background color
            JTableHeader header = flightTable.getTableHeader();
            header.setBackground(new Color(92, 194, 191));
            header.setForeground(Color.WHITE);
            header.setFont(new Font("Arial", Font.BOLD, 14));

            // Set preferred widths for columns
            TableColumnModel columnModel = flightTable.getColumnModel();
            columnModel.getColumn(0).setPreferredWidth(150); // first_flight_number
            columnModel.getColumn(1).setPreferredWidth(150); // first_flight_company
            columnModel.getColumn(2).setPreferredWidth(150); // first_departure_time
            columnModel.getColumn(3).setPreferredWidth(150); // first_arrival_time
            columnModel.getColumn(4).setPreferredWidth(150); // second_from_city
            columnModel.getColumn(5).setPreferredWidth(150); // second_to_city
            columnModel.getColumn(6).setPreferredWidth(150); // second_flight_date
            columnModel.getColumn(7).setPreferredWidth(150); // second_departure_time
            columnModel.getColumn(8).setPreferredWidth(150); // second_arrival_time
            columnModel.getColumn(9).setPreferredWidth(150); // second_flight_number
            columnModel.getColumn(10).setPreferredWidth(150); // second_flight_company
            columnModel.getColumn(11).setPreferredWidth(150); // traveller_class

            // Set the table to auto-resize columns based on content
            flightTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            // Add the table to the results frame
            resultsFrame.add(scrollPane, BorderLayout.CENTER);
            resultsFrame.setVisible(true);
            resultsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            resultsFrame.setLocationRelativeTo(this); // Center the window
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching flights.", "Error", JOptionPane.ERROR_MESSAGE);
        }
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
        SwingUtilities.invokeLater(MultiCityFlight::new);
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
}
