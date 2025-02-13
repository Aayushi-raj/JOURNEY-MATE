import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*; // Import for SQL classes
import javax.swing.table.JTableHeader; // Import JTableHeader

public class CabOutstation extends JFrame {
    private JPanel sidebar;
    private JPanel content;
    private JComboBox<String> fromCombo, toCombo, timeCombo;
    private JButton searchButton;

    private static final Color PRIMARY_COLOR = new Color(92, 194, 191); // #5cc2bf
    private static final Color SECONDARY_COLOR = new Color(49, 151, 149); // #319795
    private static final Color SIDEBAR_COLOR = new Color(227, 244, 243); // #e3f4f3
    private static final Font HEADER_FONT = new Font("Samarkan", Font.PLAIN, 36);

    private static final String DB_URL = "jdbc:mysql://localhost:3306/cab"; // Database URL
    private static final String USER = "root"; // Database username
    private static final String PASS = "akshat"; // Database password

    public CabOutstation() {
        setTitle("Journey Mate - Outstation Cab Booking");
        setSize(1000, 800);  // Increased window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create and add the header
        JPanel header = createHeader();
        add(header, BorderLayout.NORTH);

        createSidebar();
        createContent();

        setVisible(true);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        header.setPreferredSize(new Dimension(1000, 75)); 

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
            this.dispose(); // Close the current TrainBookingApp window
        });

        buttonPanel.add(aboutButton);
        header.add(buttonPanel, BorderLayout.EAST); // Add the button panel to the right side of the header

        return header;
    }


    private void styleButton(JButton button) {
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(49, 151, 149)); // #319795
        button.setFocusPainted(false);
        button.setBorderPainted(false);
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
                menuButton.setBackground(SECONDARY_COLOR);
                menuButton.setForeground(Color.WHITE);
            } else if (menuItems[i].equals("Flight")) {
                menuButton.addActionListener(e -> {
                    SwingUtilities.invokeLater(() -> {
                        new JourneyMateApp().setVisible(true);
                        this.dispose(); // Close the current CabOutstation window
                    });
                });
            } else if (menuItems[i].equals("Train")) {
                menuButton.addActionListener(e -> {
                    SwingUtilities.invokeLater(() -> {
                        new TrainBookingApp().setVisible(true);
                        this.dispose(); // Close the current CabOutstation window
                    });
                });
            } else if (menuItems[i].equals("Bus")) {
                menuButton.addActionListener(e -> {
                    SwingUtilities.invokeLater(() -> {
                        new BusApp().setVisible(true);
                        this.dispose(); // Close the current CabOutstation window
                    });
                });
            }
            
            sidebar.add(menuButton);
        }

        add(sidebar, BorderLayout.WEST);
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

        JPanel searchBox = createSearchBox();
        content.add(searchBox);
        content.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel statsPanel = createStatistics();
        content.add(statsPanel);

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
            tabButton.setBackground(i == 1 ? new Color(49, 151, 149) : new Color(92, 194, 191));
            tabButton.setForeground(Color.WHITE);
            tabButton.setFocusPainted(false);
            tabPanel.add(tabButton);
            
            if (tabs[i].equals("Daily")) {
                tabButton.addActionListener(e -> {
                    SwingUtilities.invokeLater(() -> new CabBookingApp());
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

    private JPanel createSearchBox() {
        JPanel searchBox = new JPanel();
        searchBox.setBackground(new Color(173, 226, 225)); // #ade2e1
        searchBox.setLayout(new GridBagLayout());
        searchBox.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        fromCombo = new JComboBox<>(new String[]{
            "Mumbai", "Delhi", "Bangalore", "Kolkata", "Chennai", "Hyderabad", 
            "Ahmedabad", "Pune", "Jaipur", "Lucknow", "Kanpur", "Nagpur", 
            "Indore", "Thane", "Bhopal", "Visakhapatnam", "Patna", "Vadodara", 
            "Ghaziabad", "Ludhiana", "Agra", "Nashik", "Faridabad", "Meerut", 
            "Rajkot", "Varanasi", "Srinagar", "Aurangabad", "Dhanbad", "Amritsar"
        });
        toCombo = new JComboBox<>(new String[]{
            "Mumbai", "Delhi", "Bangalore", "Kolkata", "Chennai", "Hyderabad", 
            "Ahmedabad", "Pune", "Jaipur", "Lucknow", "Kanpur", "Nagpur", 
            "Indore", "Thane", "Bhopal", "Visakhapatnam", "Patna", "Vadodara", 
            "Ghaziabad", "Ludhiana", "Agra", "Nashik", "Faridabad", "Meerut", 
            "Rajkot", "Varanasi", "Srinagar", "Aurangabad", "Dhanbad", "Amritsar"
        });
        timeCombo = new JComboBox<>(new String[]{
            "2024-11-09 00:00:00", "2024-11-09 01:00:00", "2024-11-09 02:00:00", "2024-11-09 03:00:00", "2024-11-09 04:00:00", "2024-11-09 05:00:00",
            "2024-11-09 06:00:00", "2024-11-09 07:00:00", "2024-11-09 08:00:00", "2024-11-09 09:00:00", "2024-11-09 10:00:00", "2024-11-09 11:00:00",
            "2024-11-09 12:00:00", "2024-11-09 13:00:00", "2024-11-09 14:00:00", "2024-11-09 15:00:00", "2024-11-09 16:00:00", "2024-11-09 17:00:00",
            "2024-11-09 18:00:00", "2024-11-09 19:00:00", "2024-11-09 20:00:00", "2024-11-09 21:00:00", "2024-11-09 22:00:00", "2024-11-09 23:00:00"

        });

        Dimension comboSize = new Dimension(200, 30);
        fromCombo.setPreferredSize(comboSize);
        toCombo.setPreferredSize(comboSize);
        timeCombo.setPreferredSize(comboSize);

        gbc.gridx = 0; gbc.gridy = 0;
        searchBox.add(new JLabel("From:"), gbc);
        gbc.gridx = 1;
        searchBox.add(fromCombo, gbc);
        gbc.gridx = 2;
        searchBox.add(new JLabel("To:"), gbc);
        gbc.gridx = 3;
        searchBox.add(toCombo, gbc);
        gbc.gridx = 4;
        searchBox.add(new JLabel("Time:"), gbc);
        gbc.gridx = 5;
        searchBox.add(timeCombo, gbc);

        searchButton = new JButton("SEARCH OUTSTATION");
        searchButton.setPreferredSize(new Dimension(200, 40));
        searchButton.setBackground(new Color(26, 59, 93)); // #1a3b5d
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 6;
        gbc.anchor = GridBagConstraints.CENTER;
        searchBox.add(searchButton, gbc);

        searchButton.addActionListener(e -> performSearch());

        return searchBox;
    }

    private JPanel createStatistics() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBackground(new Color(227, 244, 243));
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

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

        return statsPanel;
    }

    private void performSearch() {
        String from = (String) fromCombo.getSelectedItem();
        String to = (String) toCombo.getSelectedItem();
        String time = (String) timeCombo.getSelectedItem();

        // Database search logic
        StringBuilder results = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM cabdetails WHERE from_city = ? AND to_city = ? AND time = ?")) {
            
            pstmt.setString(1, from);
            pstmt.setString(2, to);
            pstmt.setString(3, time);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int cabId = rs.getInt("cab_id");
                String vehicleType = rs.getString("vehicle_type");
                String fromCity = rs.getString("from_city");
                String toCity = rs.getString("to_city");
                String cabTime = rs.getString("time");

                String result = String.format("Cab id: %d, Vehicle: %s, From: %s, To: %s, Time: %s", cabId, vehicleType, fromCity, toCity, cabTime);
                results.append(result).append("\n");
            }
        } catch (SQLException e) {
            results.append("Database error: ").append(e.getMessage());
        }

        // Show results on a new page
        showResultsPage(results.toString());
    }

    private void showResultsPage(String results) {
        JFrame resultsFrame = new JFrame("Search Results");
        resultsFrame.setSize(800, 400);
        resultsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultsFrame.setLayout(new BorderLayout());

        // Create a panel for better organization
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // Create a title label
        JLabel titleLabel = new JLabel("Availiable outstation cabs");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultsPanel.add(titleLabel);
        resultsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between title and results

        // Prepare data for the table
        String[] columnNames = {"Cab ID", "Vehicle Type", "From", "To", "Time"};
        String[][] data = parseResults(results); // Method to parse results into a 2D array

        // Create a JTable
        JTable resultsTable = new JTable(data, columnNames);
        resultsTable.setFillsViewportHeight(true);
        resultsTable.setRowHeight(30);
        resultsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        resultsTable.setBackground(new Color(227, 244, 243)); // Pastel color for the table background
        resultsTable.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // Set header background color
        JTableHeader header = resultsTable.getTableHeader();
        header.setBackground(new Color(92, 194, 191)); // Pastel color for the header
        header.setForeground(Color.WHITE); // White text for contrast
        header.setFont(new Font("Arial", Font.BOLD, 14)); // Bold font for header

        // Add a scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // Add border to scroll pane
        resultsPanel.add(scrollPane);

        // Add "Book" button below the table
        JButton bookButton = new JButton("Book");
        bookButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        bookButton.setPreferredSize(new Dimension(100, 40));
        bookButton.setBackground(new Color(26, 59, 93)); // #1a3b5d
        bookButton.setForeground(Color.WHITE);
        bookButton.setFocusPainted(false);
        bookButton.addActionListener(e -> {
            // Open a new page with the image
            showImagePage();
        });
        resultsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between table and button
        resultsPanel.add(bookButton);

        // Add the results panel to the frame
        resultsFrame.add(resultsPanel, BorderLayout.CENTER);
        resultsFrame.setVisible(true);
    }

    private void showImagePage() {
        JFrame imageFrame = new JFrame("Booking Image");
        imageFrame.setSize(800, 700); // Set the size of the frame
        imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        imageFrame.setLayout(new BorderLayout());

        // Load and display the image
        try {
            ImageIcon imageIcon = new ImageIcon("C:\\Users\\aksha\\OneDrive\\Desktop\\app_swing\\icons\\outstation_img.png");
            JLabel imageLabel = new JLabel(imageIcon);
            imageFrame.add(imageLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(imageFrame, "Image not found: " + e.getMessage());
        }

        imageFrame.setVisible(true);
    }

    private String[][] parseResults(String results) {
        // Split results into lines and then into columns
        String[] lines = results.split("\n");
        String[][] data = new String[lines.length][5]; // Assuming 5 columns

        for (int i = 0; i < lines.length; i++) {
            String[] parts = lines[i].split(", ");
            data[i][0] = parts[0].split(": ")[1]; // Cab ID
            data[i][1] = parts[1].split(": ")[1]; // Vehicle Type
            data[i][2] = parts[2].split(": ")[1]; // From
            data[i][3] = parts[3].split(": ")[1]; // To
            data[i][4] = parts[4].split(": ")[1]; // Time
        }
        return data;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CabOutstation());
    }
}
