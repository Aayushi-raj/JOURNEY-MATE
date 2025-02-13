import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.net.URI;
import java.io.IOException;

public class CabBookingApp extends JFrame {
    private JPanel sidebar;
    private JPanel content;
    private JTextField pickupLocation;
    private JTextField destination;
    private static final Color SIDEBAR_COLOR = new Color(227, 244, 243); // #e3f4f3
    private static final Color PRIMARY_COLOR = new Color(92, 194, 191); // #5cc2bf
    private static final Color SECONDARY_COLOR = new Color(49, 151, 149); // #319795
    private static final Font HEADER_FONT = new Font("Samarkan", Font.PLAIN, 36);
    
    

    public CabBookingApp() {
        setTitle("Journey Mate - Cab Booking");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create the header
        JPanel header = createHeader();
        add(header, BorderLayout.NORTH); // Add header to the top

        // Ensure the header is visible
        header.setVisible(true); // Ensure the header is explicitly set to visible

        createSidebar();
        createContent();

        setVisible(true);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        header.setPreferredSize(new Dimension(1000, 75)); // Reduced the height of the header from 100 to 70
        // header.setVisible(true); // Remove this line to ensure the header is visible by default

        JLabel logoLabel = new JLabel("journey mate");
        logoLabel.setFont(HEADER_FONT); // Keep the same font
        logoLabel.setForeground(PRIMARY_COLOR);
        header.add(logoLabel, BorderLayout.WEST);

        // Create a panel for the About button with padding
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10)); // Add padding to the right

        // Create "About" button
        JButton aboutButton = new JButton("About");
        aboutButton.setFont(new Font("Arial", Font.BOLD, 14)); // Keep the same font size
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
            this.dispose(); // Close the current CabBookingApp window
        });

        buttonPanel.add(aboutButton);
        header.add(buttonPanel, BorderLayout.EAST); // Add the button panel to the right side of the header

        return header;
    }


    private void styleButton(JButton button) {
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private JButton createSidebarButton(String text, String iconPath) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (text.equals("Cab")) {
                    g2.setColor(SECONDARY_COLOR);
                } else if (getModel().isPressed()) {
                    g2.setColor(PRIMARY_COLOR.darker());
                } else if (getModel().isRollover()) {
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

    private void createSidebar() {
        sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(4, 1, 0, 10));
        sidebar.setPreferredSize(new Dimension(100, getHeight())); // Reduced width
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 5)); // Reduced horizontal padding

        String[] menuItems = {"Flight", "Train", "Cab", "Bus"};
        String[] iconPaths = {"icons/flights.png", "icons/train.png", "icons/cab.png", "icons/bus.png"};

        for (int i = 0; i < menuItems.length; i++) {
            JButton menuButton = createSidebarButton(menuItems[i], iconPaths[i]);
            
            if (menuItems[i].equals("Cab")) {
                // Set the Cab button to a darker color
                menuButton.setBackground(PRIMARY_COLOR.darker());
                menuButton.setForeground(Color.WHITE);
            } else if (menuItems[i].equals("Bus")) {
                menuButton.addActionListener(e -> openBusApp());
            } else if (menuItems[i].equals("Train")) {
                menuButton.addActionListener(e -> openTrainBookingApp());
            } else if (menuItems[i].equals("Flight")) {
                menuButton.addActionListener(e -> openOneWayFlight());
            }
            
            sidebar.add(menuButton);
        }

        add(sidebar, BorderLayout.WEST);
    }

    private void openOneWayFlight() {
        SwingUtilities.invokeLater(() -> {
            new OneWayFlight().setVisible(true);
            this.dispose(); // Close the current CabBookingApp window
        });
    }

    private void openBusApp() {
        SwingUtilities.invokeLater(() -> {
            new BusApp().setVisible(true);
            this.dispose(); // Close the current CabBookingApp window
        });
    }

    private void openTrainBookingApp() {
        SwingUtilities.invokeLater(() -> {
            new TrainBookingApp().setVisible(true);
            this.dispose(); // Close the current CabBookingApp window
        });
    }

    private void createContent() {
        content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(227, 244, 243)); // #e3f4f3
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Increase the gap between header and tabs
        content.add(Box.createRigidArea(new Dimension(0, 100)));

        JPanel tabPanel = createTabPanel();
        content.add(tabPanel);
        content.add(Box.createRigidArea(new Dimension(0, 20)));

        createSearchSection();
        
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        
        createStatistics();

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createTabPanel() {
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tabPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        String[] tabs = {"Daily", "Outstation", "Rentals"};
        for (int i = 0; i < tabs.length; i++) {
            JButton tabButton = new JButton(tabs[i]);
            tabButton.setPreferredSize(new Dimension(120, 40));
            tabButton.setBackground(i == 0 ? new Color(49, 151, 149) : new Color(92, 194, 191));
            tabButton.setForeground(Color.WHITE);
            tabButton.setFocusPainted(false);
            
            if (tabs[i].equals("Outstation")) {
                tabButton.addActionListener(e -> {
                    SwingUtilities.invokeLater(() -> new CabOutstation());
                    this.dispose();
                });
            }

            if (tabs[i].equals("Rentals")) {
                tabButton.addActionListener(e -> {
                    SwingUtilities.invokeLater(() -> new CabRentals().setVisible(true));
                    this.dispose();  // Close the current window
                });
            }
            
            
            tabPanel.add(tabButton);
        }
        return tabPanel;
    }

    private void createSearchSection() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBackground(new Color(173, 226, 225)); // #ade2e1

        pickupLocation = new JTextField("Enter Pick-Up Location", 20);
        destination = new JTextField("Enter Destination", 20);

        // Add focus listeners to both text fields
        addPlaceholderBehavior(pickupLocation, "Enter Pick-Up Location");
        addPlaceholderBehavior(destination, "Enter Destination");

        JButton searchButton = new JButton("SEARCH DAILY CABS");
        JButton newButton = new JButton("BOOK NOW !"); // New button

        // Increase the size of the text fields and button
        Dimension componentSize = new Dimension(1000, 40);
        pickupLocation.setPreferredSize(componentSize);
        destination.setPreferredSize(componentSize);
        searchButton.setPreferredSize(new Dimension(250, 40));
        newButton.setPreferredSize(new Dimension(500, 40)); // Set size for new button

        searchButton.setBackground(new Color(26, 59, 93)); // #1a3b5d
        searchButton.setForeground(Color.WHITE);
        newButton.setBackground(new Color(26, 59, 93)); // #1a3b5d
        newButton.setForeground(Color.WHITE);
        newButton.setFocusPainted(false); // Set focus painted for new button

        // Add ActionListener to the search button
        searchButton.addActionListener(e -> searchCabs());
        // Add ActionListener for the new button
        newButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://candid-lamington-2a9ceb.netlify.app/"));
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Unable to open the URL.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 80));
        topRow.setBackground(new Color(173, 226, 225)); // #ade2e1
        topRow.add(pickupLocation);
        topRow.add(new JLabel("→")); // Simplified representation of the car icon
        topRow.add(destination);
        topRow.add(searchButton);

        searchPanel.add(topRow);
        searchPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add space between rows

        // Center the new button
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonRow.setBackground(new Color(173, 226, 225)); // #ade2e1
        buttonRow.add(newButton);
        searchPanel.add(buttonRow);

        content.add(searchPanel);
    }

    private void createStatistics() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBackground(new Color(227, 244, 243)); // #e3f4f3

        String[][] stats = {
            {"1,000+", "Rides Completed"},
            {"750+", "Happy Customers"},
            {"100+", "Available Cabs"}
        };

        for (String[] stat : stats) {
            JPanel statItem = new JPanel();
            statItem.setLayout(new BoxLayout(statItem, BoxLayout.Y_AXIS));
            statItem.setBackground(Color.WHITE);
            statItem.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            JLabel number = new JLabel(stat[0]);
            number.setAlignmentX(Component.CENTER_ALIGNMENT);
            number.setFont(new Font("Arial", Font.BOLD, 24));

            JLabel description = new JLabel(stat[1]);
            description.setAlignmentX(Component.CENTER_ALIGNMENT);

            statItem.add(Box.createVerticalGlue());
            statItem.add(number);
            statItem.add(description);
            statItem.add(Box.createVerticalGlue());

            statsPanel.add(statItem);
        }

        content.add(Box.createRigidArea(new Dimension(0, 20)));
        content.add(statsPanel);
    }

    private void addPlaceholderBehavior(JTextField textField, String placeholder) {
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                }
            }
        });
    }

    private void searchCabs() {
        String pickup = pickupLocation.getText();
        String dest = destination.getText();

        String url = "jdbc:mysql://localhost:3306/cab";
        String user = "root";
        String password = "akshat";

        String query = "SELECT * FROM cab_details WHERE location = ? AND destination = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, pickup);
            stmt.setString(2, dest);

            ResultSet rs = stmt.executeQuery();

            // Create a new window to display results
            JFrame resultFrame = new JFrame("Search Results");
            resultFrame.setSize(800, 400);
            resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            resultFrame.setLayout(new BorderLayout());

            // Create a panel for better organization
            JPanel resultsPanel = new JPanel();
            resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
            resultsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

            // Create a title label
            JLabel titleLabel = new JLabel("Availiable Daily Cabs");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            resultsPanel.add(titleLabel);
            resultsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between title and results

            // Define column names
            String[] columnNames = {"Cab ID", "Vehicle Type", "Location", "Destination", "Minutes Away", "Fare Estimate"};

            // Use a DefaultTableModel to hold the data
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            while (rs.next()) {
                int cabId = rs.getInt("cab_id");
                String vehicleType = rs.getString("vehicle_type");
                String location = rs.getString("location");
                String destination = rs.getString("destination");
                int minsAway = rs.getInt("mins_away");
                double fareEstimate = rs.getDouble("fare_estimate");

                // Add row to the table model
                tableModel.addRow(new Object[]{cabId, vehicleType, location, destination, minsAway, fareEstimate});
            }

            // Create a JTable with the table model
            JTable table = new JTable(tableModel);
            table.setFillsViewportHeight(true);
            table.setRowHeight(30);
            table.setFont(new Font("Arial", Font.PLAIN, 14));
            table.setBackground(new Color(227, 244, 243)); // Pastel color for the table background
            table.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            // Set header background color
            JTableHeader header = table.getTableHeader();
            header.setBackground(new Color(92, 194, 191)); // Pastel color for the header
            header.setForeground(Color.WHITE); // White text for contrast
            header.setFont(new Font("Arial", Font.BOLD, 14)); // Bold font for header

            // Add a scroll pane for the table
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // Add border to scroll pane
            resultsPanel.add(scrollPane);

            // Add the results panel to the frame
            resultFrame.add(resultsPanel, BorderLayout.CENTER);
            resultFrame.setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error accessing database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CabBookingApp());
    }
}
