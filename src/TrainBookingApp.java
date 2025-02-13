import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*; // Import for SQL classes
import javax.swing.table.JTableHeader; // Add this import
import javax.swing.table.DefaultTableModel; // Add this import
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException; // Also handle this exception

public class TrainBookingApp extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(92, 194, 191);
    private static final Color SECONDARY_COLOR = new Color(49, 151, 149);
    private static final Color BACKGROUND_COLOR = new Color(227, 244, 243);
    private static final Color SIDEBAR_COLOR = new Color(213, 234, 233);
    private static final Font HEADER_FONT = new Font("Samarkan", Font.PLAIN, 36);
    private static final Font BUTTON_FONT = new Font("Inter", Font.BOLD, 14);
    private static final Font TAB_FONT = new Font("Inter", Font.BOLD, 14);
    
    
    private JTextField fromField, toField, dateField, pnrField, trainField;
    private JCheckBox freeCancellationCheckbox;
    private JButton searchButton, checkPNRButton, checkStatusButton;
    private JTabbedPane tabbedPane;
    private trainbackend backend;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/cab"; // Database URL
    private static final String USER = "root"; // Database username
    private static final String PASS = "akshat"; // Database password

    public TrainBookingApp() {
        // Set the selected tab color
        UIManager.put("TabbedPane.selectedBackground", new Color(49, 151, 149));
        UIManager.put("TabbedPane.selectedForeground", Color.WHITE); // Optional: Change text color

        setTitle("Journey Mate - Train Booking");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        backend = new trainbackend();

        createComponents();

        // Add action listener to the search button
        searchButton.addActionListener(e -> performSearch());

        setVisible(true);
    }

    private void createComponents() {
        // Header
        JPanel header = createHeader();
        add(header, BorderLayout.NORTH);

        // Main content panel (will contain sidebar and center content)
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(BACKGROUND_COLOR);

        // Sidebar
        JPanel sidebar = createSidebar();
        mainContent.add(sidebar, BorderLayout.WEST);

        // Center content
        JPanel centerContent = new JPanel(new BorderLayout());
        centerContent.setBackground(BACKGROUND_COLOR);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(TAB_FONT);
        tabbedPane.setBackground(BACKGROUND_COLOR);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());

        // Book Train Tickets tab
        JPanel bookTicketsPanel = createBookTicketsPanel();
        tabbedPane.addTab("Book Train Tickets", bookTicketsPanel);
        tabbedPane.setBackgroundAt(tabbedPane.getTabCount() - 1, new Color(92, 194, 191)); // Set color for Book Train Tickets tab

        // Check PNR Status tab
        JPanel checkPNRPanel = createCheckPNRPanel();
        tabbedPane.addTab("Check PNR Status", checkPNRPanel);
        tabbedPane.setBackgroundAt(tabbedPane.getTabCount() - 1, new Color(92, 194, 191)); // Set color for Check PNR Status tab

        // Live Train Status tab
        JPanel liveStatusPanel = createLiveStatusPanel();
        tabbedPane.addTab("Live Train Status", liveStatusPanel);
        tabbedPane.setBackgroundAt(tabbedPane.getTabCount() - 1, new Color(92, 194, 191)); // Set color for Live Train Status tab

        centerContent.add(tabbedPane, BorderLayout.CENTER);

        // Add gap between tabs and search section
        centerContent.add(Box.createRigidArea(new Dimension(0, 200)), BorderLayout.NORTH); // Added gap

        // Info section
        JPanel infoSection = createInfoSection();
        centerContent.add(infoSection, BorderLayout.SOUTH);

        mainContent.add(centerContent, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
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
            this.dispose(); // Close the current TrainBookingApp window
        });

        buttonPanel.add(aboutButton);
        header.add(buttonPanel, BorderLayout.EAST); // Add the button panel to the right side of the header

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
            
            if (menuItems[i].equals("Train")) {
                menuButton.setBackground(PRIMARY_COLOR.darker());
                menuButton.setForeground(Color.WHITE);
            } else if (menuItems[i].equals("Bus")) {
                menuButton.addActionListener(e -> openBusApp());
            } else if (menuItems[i].equals("Cab")) {
                menuButton.addActionListener(e -> openCabBookingApp());
            } else if (menuItems[i].equals("Flight")) {
                menuButton.addActionListener(e -> openOneWayFlight());
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
                if (text.equals("Train")) {
                    g2.setColor(PRIMARY_COLOR.darker());
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

    private void openFlightApp() {
        // Implement flight app opening logic here
        System.out.println("Opening Flight App");
    }

    
    private void openOneWayFlight() {
        SwingUtilities.invokeLater(() -> {
            new OneWayFlight().setVisible(true);
            this.dispose(); // Close the current TrainBookingApp window
        });
    }

    private JPanel createBookTicketsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        JPanel searchSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        searchSection.setBackground(Color.WHITE);
        searchSection.setBorder(new RoundedBorder(10));

        fromField = createStyledTextField("From");
        toField = createStyledTextField("To");
        dateField = createStyledTextField("Date (yyyy-mm-dd)");
        freeCancellationCheckbox = new JCheckBox("<html><center>FREE<br>Cancellation</center></html>");
        searchButton = createPrimaryStyledButton("SEARCH");
        
        // Add the "Book" button
        JButton bookButton = createPrimaryStyledButton("BOOK");
        bookButton.addActionListener(e -> confirmBooking());

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.add(fromField);
        inputPanel.add(new JLabel(new ImageIcon("icons/exchange.png"))); // Add exchange icon
        inputPanel.add(toField);
        inputPanel.add(dateField);

        searchSection.add(inputPanel);
        searchSection.add(freeCancellationCheckbox);
        searchSection.add(searchButton);
        searchSection.add(bookButton); // Add the book button to the search section

        panel.add(searchSection, BorderLayout.NORTH);

        return panel;
    }

    private void confirmBooking() {
        String from = fromField.getText();
        String to = toField.getText();
        String date = dateField.getText();

        // Check if fields are empty and show an error message if they are
        if (from.isEmpty() || to.isEmpty() || date.isEmpty() || from.equals("From") || to.equals("To") || date.equals("Date (yyyy-mm-dd)")) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create a confirmation message
        String message = String.format("Booking Confirmed!\nFrom: %s\nTo: %s\nDate: %s", from, to, date);
        
        // Display the confirmation in a dialog
        JOptionPane.showMessageDialog(this, message, "Booking Confirmation", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createCheckPNRPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        JPanel searchSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        searchSection.setBackground(Color.WHITE);
        searchSection.setBorder(new RoundedBorder(10));

        pnrField = createStyledTextField("Enter PNR Number");
        checkPNRButton = createPrimaryStyledButton("Check PNR");
        // Add action listener to the check PNR button
        checkPNRButton.addActionListener(e -> checkPNR());

        searchSection.add(pnrField);
        searchSection.add(checkPNRButton);

        panel.add(searchSection, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createLiveStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        JPanel searchSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        searchSection.setBackground(Color.WHITE);
        searchSection.setBorder(new RoundedBorder(10));

        trainField = createStyledTextField("Enter train number or name");
        checkStatusButton = createPrimaryStyledButton("Check Status");

        // Add action listener to the check status button
        checkStatusButton.addActionListener(e -> checkTrainStatus());

        searchSection.add(trainField);
        searchSection.add(checkStatusButton);

        panel.add(searchSection, BorderLayout.NORTH);

        return panel;
    }

    private void checkTrainStatus() {
        String trainNumber = trainField.getText(); // Get the train number from the text field
        String startDay = "0"; // Assuming startDay is 0, adjust as needed

        // New API details
        String apiUrl = "https://irctc1.p.rapidapi.com/api/v1/liveTrainStatus?trainNo=" + trainNumber + "&startDay=" + startDay;
        String apiKey = "985d57e23emshc899369c8a631d7p1e856djsn6784a213fa51"; // Your new API key

        try {
            // Create URL
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-RapidAPI-Key", apiKey);
            conn.setRequestProperty("X-RapidAPI-Host", "irctc1.p.rapidapi.com");

            // Handle the response
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse the JSON response
                JSONParser parser = new JSONParser();
                JSONObject jsonResponse = (JSONObject) parser.parse(response.toString());

                // Call the method to display the train status
                displayTrainStatus(jsonResponse);
            } else {
                // Handle error response
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                errorReader.close();

                JOptionPane.showMessageDialog(this, "Error: " + responseCode + "\n" + errorResponse.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayTrainStatus(JSONObject jsonResponse) {
        JSONObject data = (JSONObject) jsonResponse.get("data");

        // Extract relevant information
        String trainName = (String) data.get("train_name");
        String currentStationName = (String) data.get("current_station_name");
        String status = (String) data.get("status");
        String eta = (String) data.get("eta");
        String delay = String.valueOf(data.get("delay"));
        String statusAsOf = (String) data.get("status_as_of");

        // Display the information
        JFrame statusFrame = new JFrame("Train Status");
        statusFrame.setSize(400, 300);
        statusFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        statusFrame.setLayout(new BorderLayout());

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        infoPanel.add(new JLabel("Train Name: " + trainName));
        infoPanel.add(new JLabel("Current Station: " + currentStationName));
        infoPanel.add(new JLabel("Status: " + status));
        infoPanel.add(new JLabel("ETA: " + eta));
        infoPanel.add(new JLabel("Delay: " + delay + " mins"));
        infoPanel.add(new JLabel("Status As Of: " + statusAsOf));

        statusFrame.add(infoPanel, BorderLayout.CENTER);
        statusFrame.setVisible(true);
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField(15);
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
        textField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(5),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
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

    private JButton createPrimaryStyledButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setFont(BUTTON_FONT);
        button.setBorder(new RoundedBorder(5));
        return button;
    }

    private JPanel createInfoSection() {
        JPanel infoSection = new JPanel(new BorderLayout());
        infoSection.setBackground(Color.WHITE);
        infoSection.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("IRCTC authorised Ticket Booking");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 18));
        titleLabel.setForeground(new Color(229, 57, 53));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoSection.add(titleLabel, BorderLayout.NORTH);

        JLabel descriptionLabel = new JLabel("<html><center>Book IRCTC Train Tickets on Journey Mate and enjoy simple & superfast booking process with no service fee + no payment gateway charge.</center></html>");
        descriptionLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoSection.add(descriptionLabel, BorderLayout.CENTER);

        JPanel infoItems = new JPanel(new GridLayout(1, 3, 20, 0));
        infoItems.setBackground(Color.WHITE);
        infoItems.add(createInfoItem("IRCTC Authorised Partner", "Book your train tickets with confidence"));
        infoItems.add(createInfoItem("Instant Confirmation", "Receive your ticket confirmation in seconds"));
        infoItems.add(createInfoItem("100% Secure Payment", "All transactions are secure and protected"));
        infoSection.add(infoItems, BorderLayout.SOUTH);

        return infoSection;
    }

    private JPanel createInfoItem(String title, String description) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 14));
        item.add(titleLabel, BorderLayout.NORTH);

        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        item.add(descLabel, BorderLayout.CENTER);

        return item;
    }

    private static class RoundedBorder implements Border {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width-1, height-1, radius, radius);
        }
    }

   

    private void performSearch() {
        String from = fromField.getText();
        String to = toField.getText();
        String date = dateField.getText(); // Assuming date is in the format "yyyy-mm-dd"

        // Database search logic
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement("SELECT train_id, train_num, train_name, from_city, to_city, journey_date FROM train WHERE from_city = ? AND to_city = ? AND journey_date = ?")) {
             
            pstmt.setString(1, from);
            pstmt.setString(2, to);
            pstmt.setString(3, date);
            ResultSet rs = pstmt.executeQuery();

            // Create a new JFrame to display results
            JFrame resultsFrame = new JFrame("Search Results");
            resultsFrame.setSize(600, 400);
            resultsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            resultsFrame.setLayout(new BorderLayout());

            // Create a panel for better organization
            JPanel resultsPanel = new JPanel(new BorderLayout());
            resultsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

            // Prepare data for the table
            String[] columnNames = {"Train ID", "Train Number", "Train Name", "Departure", "Arrival", "Date"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            if (!rs.isBeforeFirst()) { // Check if there are results
                resultsPanel.add(new JLabel("No matching trains found."), BorderLayout.CENTER);
            } else {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("train_id"),
                        rs.getString("train_num"),
                        rs.getString("train_name"), // Ensure train name is fetched separately
                        rs.getString("from_city"),
                        rs.getString("to_city"),
                        rs.getString("journey_date")
                    });
                }

                // Create a JTable
                JTable resultsTable = new JTable(model);
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
                resultsPanel.add(scrollPane, BorderLayout.CENTER);
            }

            resultsFrame.add(resultsPanel, BorderLayout.CENTER);
            resultsFrame.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkPNR() {
        String pnr = pnrField.getText(); // Get the PNR number from the text field

        // Database search logic
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM pnr WHERE pnr_num = ?")) { // Adjust the query as per your table structure
             
            pstmt.setString(1, pnr);
            ResultSet rs = pstmt.executeQuery();

            // Create a new JFrame to display results
            JFrame resultsFrame = new JFrame("PNR Details");
            resultsFrame.setSize(600, 400);
            resultsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // Create a panel for the table
            JPanel resultsPanel = new JPanel(new BorderLayout());

            if (!rs.isBeforeFirst()) { // Check if there are results
                resultsPanel.add(new JLabel("No matching PNR found."), BorderLayout.CENTER);
            } else {
                // Ensure the data is structured correctly for the table model
                DefaultTableModel model = new DefaultTableModel(new Object[][]{}, new String[]{"PNR Number", "Train ID", "Passenger Name", "Status"});
                
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("pnr_num"),
                        rs.getInt("pnr_id"),
                        rs.getString("passenger_name"),
                        rs.getString("status")
                    });
                }

                // Create table
                JTable table = new JTable(model);
                table.setFillsViewportHeight(true);
                table.getTableHeader().setBackground(new Color(92, 194, 191)); // Pastel color for the header
                table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 14));
                table.setFont(new Font("Inter", Font.PLAIN, 12));
                table.setRowHeight(25);

                // Add table to scroll pane
                JScrollPane scrollPane = new JScrollPane(table);
                resultsPanel.add(scrollPane, BorderLayout.CENTER);
            }

            resultsFrame.add(resultsPanel);
            resultsFrame.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPNRResultsPage(String pnrResults) {
        JFrame resultsFrame = new JFrame("PNR Details");
        resultsFrame.setSize(600, 400);
        resultsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultsFrame.setLayout(new BorderLayout());

        // Create a panel for better organization
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // Create a title label
        JLabel titleLabel = new JLabel("PNR Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultsPanel.add(titleLabel);
        resultsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between title and results

        // Prepare data for the table
        String[] columnNames = {"PNR Number", "Train ID", "Passenger Name", "Status"};
        String[][] data = parsePNRResults(pnrResults); // Method to parse PNR results into a 2D array

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

        // Add the results panel to the frame
        resultsFrame.add(resultsPanel, BorderLayout.CENTER);
        resultsFrame.setVisible(true);
    }

    private String[][] parsePNRResults(String pnrResults) {
        // Split results into lines and then into columns
        String[] lines = pnrResults.split("\n");
        String[][] data = new String[lines.length][4]; // Assuming 4 columns

        for (int i = 0; i < lines.length; i++) {
            String[] parts = lines[i].split(", ");
            data[i][0] = parts[0].split(": ")[1]; // PNR Number
            data[i][1] = parts[1].split(": ")[1]; // Train ID
            data[i][2] = parts[2].split(": ")[1]; // Passenger Name
            data[i][3] = parts[3].split(": ")[1]; // Status
        }
        return data;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TrainBookingApp());
    }
}
