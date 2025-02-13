import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.JTableHeader; // {{ edit_1 }}
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class OneWayFlight extends JFrame {
    private Connection connection;
    private JTextField fromField, toField, dateField;
    private JComboBox<String> classComboBox;
    private static final Color PRIMARY_COLOR = new Color(92, 194, 191); // #5cc2bf
    private static final Color SECONDARY_COLOR = new Color(49, 151, 149); // #319795
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255); // #F0F8FF
    private static final Color SIDEBAR_COLOR = new Color(227, 244, 243); // #e3f4f3
    private static final Font HEADER_FONT = new Font("Samarkan", Font.PLAIN, 36);
    private static final Color TABLE_HEADER_COLOR = new Color(92, 194, 191); // Pastel header color
    private static final Color TABLE_BACKGROUND_COLOR = new Color(227, 244, 243); // Pastel table background color

   

    // Declare mainContent at the class level
    private JPanel mainContent; // {{ edit_1 }}

    public OneWayFlight() {
        setTitle("One Way Flight Search");
        setSize(1000, 800); // Adjusted frame size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // Changed layout to BorderLayout

        // Create the header
        JPanel header = createHeader();
        add(header, BorderLayout.NORTH); // Add header to the top

        // Trip type buttons
        JPanel tripTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Ensure FlowLayout is used
        tripTypePanel.setOpaque(false); // Make sure the panel is transparent
        tripTypePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 10)); // {{ edit_1 }} Add padding to the trip type panel
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
        oneWayButton.setSelected(true); // Set One Way button as selected
        oneWayButton.setBackground(PRIMARY_COLOR.darker()); // Darker color for the selected button

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

        // Ensure the frame is visible
        revalidate(); // Refresh the layout
        repaint(); // Repaint the frame

        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Create a panel for the input fields
        JPanel inputPanel = createInputPanel();
        mainContent.add(inputPanel, BorderLayout.CENTER); // Add input panel to the center
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // {{ edit_2 }} Add padding to the input panel

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
            this.dispose(); // Close the current OneWayFlight window
        });

        buttonPanel.add(aboutButton);
        gbc.gridx = 1; // Adjusted position for About button
        header.add(buttonPanel, gbc); // Add the button panel to the right side of the header

        return header;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(4, 1, 0, 10));
        sidebar.setPreferredSize(new Dimension(100, getHeight())); // Reduced width
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 5)); // Reduced horizontal padding

        String[] menuItems = {"Flight", "Train", "Cab", "Bus"};
        String[] iconPaths = {"icons/flights.png", "icons/train.png", "icons/cab.png", "icons/bus.png"};

        for (int i = 0; i < menuItems.length; i++) {
            JButton menuButton = createSidebarButton(menuItems[i], iconPaths[i]);
            
            if (menuItems[i].equals("Flight")) {
                menuButton.setBackground(PRIMARY_COLOR.darker());
                menuButton.setForeground(Color.WHITE);
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
            Image newImg = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH); // Slightly larger icon
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

    private void openCabBookingApp() {
        SwingUtilities.invokeLater(() -> {
            new CabBookingApp().setVisible(true);
            this.dispose(); // Close the current TrainBookingApp window
        });
    }

    private void openBusApp() {
        SwingUtilities.invokeLater(() -> {
            new BusApp().setVisible(true);
            this.dispose(); // Close the current TrainBookingApp window
        });
    }
    private void openTrainBookingApp() {
        SwingUtilities.invokeLater(() -> {
            new TrainBookingApp().setVisible(true);
            this.dispose(); // Close the current JourneyMateApp window
        });
    }

    private void openJourneyMateApp() {
        SwingUtilities.invokeLater(() -> {
            new JourneyMateApp().setVisible(true);
            this.dispose(); // Close the current TrainBookingApp window
        });
    }


    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout()); // Use GridBagLayout for input fields
        inputPanel.setBackground(BACKGROUND_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add some padding
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally
        gbc.weightx = 1.0; // Allow components to grow horizontally

        // From field
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("FROM:"), gbc);
        gbc.gridx = 1;
        fromField = createStyledTextField("FROM");
        inputPanel.add(fromField, gbc);

        // To field
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("TO:"), gbc);
        gbc.gridx = 1;
        toField = createStyledTextField("TO");
        inputPanel.add(toField, gbc);

        // Departure Date field
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("DEPARTURE DATE:"), gbc);
        gbc.gridx = 1;
        dateField = createStyledTextField("yyyy-mm-dd");
        inputPanel.add(dateField, gbc);

        // Class selection
        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("TRAVELLER & CLASS:"), gbc);
        gbc.gridx = 1;
        classComboBox = new JComboBox<>(new String[]{"Economy", "Business"});
        classComboBox.setPreferredSize(new Dimension(200, 30)); // Set preferred size
        inputPanel.add(classComboBox, gbc);

        // Search button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Span across two columns
        JButton searchButton = new JButton("SEARCH ONE WAY");
        searchButton.setPreferredSize(new Dimension(200, 40)); // Set preferred size
        searchButton.setBackground(new Color(26, 59, 93)); // #1a3b5d
        searchButton.setForeground(Color.WHITE); // Set text color
        searchButton.setFont(new Font("Arial", Font.BOLD, 14)); // Set font style
        searchButton.addActionListener(e -> searchFlights());
        inputPanel.add(searchButton, gbc);

        // {{ edit_1 }} Add Book button
        gbc.gridy = 5; // Move to the next row
        JButton bookButton = new JButton("BOOK");
        bookButton.setPreferredSize(new Dimension(100, 40)); // Set preferred size
        bookButton.setBackground(new Color(26, 59, 93)); // #1a3b5d
        bookButton.setForeground(Color.WHITE); // Set text color
        bookButton.setFont(new Font("Arial", Font.BOLD, 14)); // Set font style
        bookButton.addActionListener(e -> bookFlight()); // Add action listener
        inputPanel.add(bookButton, gbc);

        // Add checkboxes for traveller categories
        gbc.gridy = 6;
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        String[] categories = {"Defence Forces", "Students", "Senior Citizens", "Doctors Nurses"};
        for (String category : categories) {
            checkboxPanel.add(new JCheckBox(category));
        }
        inputPanel.add(checkboxPanel, gbc);

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
        String from = fromField.getText().trim();
        String to = toField.getText().trim();
        String date = dateField.getText().trim();
        String travellerClass = (String) classComboBox.getSelectedItem();

        if (from.isEmpty() || to.isEmpty() || date.isEmpty() || travellerClass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields correctly.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Updated query to match the new database schema
        String query = "SELECT * FROM flight WHERE from_city = ? AND to_city = ? AND departure_date = ? AND traveller_class = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, from);
            pstmt.setString(2, to);
            pstmt.setString(3, date);
            pstmt.setString(4, travellerClass); // Ensure this matches the database column
            ResultSet rs = pstmt.executeQuery();

            // Create a new JFrame to display results
            JFrame resultsFrame = new JFrame("Flight Details");
            resultsFrame.setSize(600, 400);
            resultsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            resultsFrame.setLayout(new BorderLayout());

            // Create a panel for better organization
            JPanel resultsPanel = new JPanel(new BorderLayout());
            resultsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

            // Prepare data for the table
            String[] columnNames = {"Flight Number", "From", "To", "Flight Company", "Price", "Departure Date", "Traveller Class"}; // Updated column names
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            if (!rs.isBeforeFirst()) { // Check if there are results
                resultsPanel.add(new JLabel("No matching flights found."), BorderLayout.CENTER);
            } else {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("flight_number"),
                        rs.getString("from_city"),
                        rs.getString("to_city"),
                        rs.getString("flight_company"),
                        rs.getDouble("price"),
                        rs.getString("departure_date"),
                        rs.getString("traveller_class") // Added traveller_class to the table
                    });
                }

                // Create a JTable
                JTable resultsTable = new JTable(model);
                resultsTable.setFillsViewportHeight(true);
                resultsTable.setRowHeight(30);
                resultsTable.setFont(new Font("Arial", Font.PLAIN, 14));
                resultsTable.setBackground(TABLE_BACKGROUND_COLOR); // Pastel color for the table background

                // Set header background color
                JTableHeader header = resultsTable.getTableHeader();
                header.setBackground(TABLE_HEADER_COLOR); // Pastel color for the header
                header.setForeground(Color.WHITE); // White text for contrast
                header.setFont(new Font("Arial", Font.BOLD, 14)); // Bold font for header

                // Add a scroll pane for the table
                JScrollPane scrollPane = new JScrollPane(resultsTable);
                scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // Add border to scroll pane

                // Add the scroll pane to the results frame
                resultsFrame.add(scrollPane, BorderLayout.CENTER);
                resultsFrame.setVisible(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching flights.", "Error", JOptionPane.ERROR_MESSAGE);
        }
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
        bookingFrame.setSize(400, 200);
        bookingFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        bookingFrame.setLayout(new BorderLayout());

        // Create a message panel
        JPanel messagePanel = new JPanel();
        messagePanel.add(new JLabel("Your flight is scheduled and booked!"));
        messagePanel.add(new JLabel("From: " + fromCity));
        messagePanel.add(new JLabel("To: " + toCity));
        messagePanel.add(new JLabel("Date: " + date));

        bookingFrame.add(messagePanel, BorderLayout.CENTER);
        bookingFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OneWayFlight::new);
    }
}
