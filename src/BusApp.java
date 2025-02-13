import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel; // Add this import statement
import javax.swing.table.JTableHeader; // Add this import statement

public class BusApp extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(92, 194, 191); // #5cc2bf
    private static final Color SECONDARY_COLOR = new Color(49, 151, 149); // #319795
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255); // #F0F8FF (updated to match JourneyMateApp)
    private static final Color SIDEBAR_COLOR = new Color(227, 244, 243); // #e3f4f3
    private static final Font HEADER_FONT = new Font("Samarkan", Font.PLAIN, 36);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 14); // Alternative font

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/cab"; // Update with your database URL
    private static final String USER = "root"; // Update with your database username
    private static final String PASSWORD = "akshat"; // Update with your database password

    private Connection connectToDatabase() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    private JPanel content; // Declare content as a class-level variable

    public BusApp() {
        setTitle("Journey Mate");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR); // Set background color

        // Header
        JPanel header = createHeader(); // Updated to use createHeader method
        add(header, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = createSidebar(); // Updated to use createSidebar method
        add(sidebar, BorderLayout.WEST);

        // Initialize the content panel
        content = new JPanel();
        content.setLayout(new BorderLayout());
        content.setBackground(BACKGROUND_COLOR); // Updated to use BACKGROUND_COLOR
        add(content, BorderLayout.CENTER);

        initializeMainBusPage(); // Initialize the main bus page on startup

        // Search Section
        JPanel searchSection = new JPanel();
        searchSection.setLayout(new FlowLayout());
        
        // {{ edit_1 }} - Use createStyledTextField for placeholders
        JTextField fromInput = createStyledTextField("Enter source"); // Placeholder for "From"
        fromInput.setPreferredSize(new Dimension(200, 25));
        JTextField toInput = createStyledTextField("Enter destination"); // Placeholder for "To"
        toInput.setPreferredSize(new Dimension(200, 25));
        // {{ edit_1 }} - End of added code
        
        JTextField dateField = createStyledTextField("yyyy-mm-dd"); // Create a styled text field for date input
        dateField.setPreferredSize(new Dimension(200, 25));
        searchSection.add(new JLabel("From:"));
        searchSection.add(fromInput);
        searchSection.add(new JLabel("To:"));
        searchSection.add(toInput);
        searchSection.add(new JLabel("Date:"));
        searchSection.add(dateField);
        
        JButton searchButton = new JButton("Search Bus");
        searchButton.setBackground(new Color(26, 59, 93)); // #1a3b5d - Updated button color
        searchButton.setForeground(Color.WHITE); // Set text color to white
        searchSection.add(searchButton);
        searchSection.setBackground(BACKGROUND_COLOR); // Updated to use BACKGROUND_COLOR
        
        // Increase gap between header and buttons
        content.add(Box.createVerticalStrut(150), BorderLayout.NORTH); // Increased gap

        content.add(searchSection, BorderLayout.CENTER);

        // Centering the search section
        content.add(Box.createVerticalStrut(100), BorderLayout.NORTH); // Added gap

        // Government Buses Section
        JPanel govtBuses = new JPanel();
        govtBuses.setLayout(new GridLayout(0, 2, 10, 10)); // Added horizontal and vertical gaps
        
        // Add "Government Buses" label
        JLabel govtBusesLabel = new JLabel("<html><b><font size='5'>Government Buses</b></html>", SwingConstants.CENTER);
        govtBusesLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the label
        govtBuses.add(govtBusesLabel); // Add label directly to the govtBuses panel

        // Add empty space to center the label above the promo boxes
        JPanel emptyPanel = new JPanel();
        emptyPanel.setLayout(new GridLayout(1, 2)); // Create an empty panel for spacing
        emptyPanel.add(new JLabel()); // Add empty label for spacing
        emptyPanel.add(new JLabel()); // Add empty label for spacing
        govtBuses.add(emptyPanel); // Add empty panel to the govtBuses layout

        // Center the govtBuses panel within the content area
        JPanel centeredGovtBusesPanel = new JPanel();
        centeredGovtBusesPanel.setLayout(new BorderLayout());
        centeredGovtBusesPanel.add(govtBuses, BorderLayout.CENTER);
        content.add(centeredGovtBusesPanel, BorderLayout.SOUTH); // Add centered panel to the content area

        String[][] promoData = {
            {"APSRTC Buses", "1539 services including Garuda, Garuda Plus and more"},
            {"TSRTC Buses", "480 services including Himgaurav, Himmani and more"},
            {"Kerala State RTC", "11450 services including Garuda Plus, Rajdhani and more"},
            {"KTCL", "6000 services including Deluxe, Ordinary and more"},
            {"RSRTC", "1539 services including Garuda, Garuda Plus and more"},
            {"UPSRTC", "1738 services including Janrath, Shatabdi and more"}
        };
        for (String[] promo : promoData) {
            JPanel promoCard = new JPanel();
            promoCard.setLayout(new BoxLayout(promoCard, BoxLayout.Y_AXIS));
            promoCard.setBorder(BorderFactory.createLineBorder(new Color(70, 192, 188)));
            promoCard.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the box
            
            // Add vertical strut for gap
            promoCard.add(Box.createVerticalStrut(10)); // Increased gap above text

            JLabel nameLabel = new JLabel("<html><b>" + promo[0] + "</b></html>", SwingConstants.CENTER); // Bold company name
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the label
            JLabel descLabel = new JLabel(promo[1], SwingConstants.CENTER); // Center align description
            descLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the label
            promoCard.add(nameLabel);
            promoCard.add(descLabel);

            // Add "Book Now" label in red color with a link
            JLabel bookNowLabel = new JLabel("<html><a href='https://onlineksrtcswift.com/'><font color='red'>Book Now</font></a></html>", SwingConstants.CENTER);
            bookNowLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the label
            
            // Add mouse listener to open link
            bookNowLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        Desktop.getDesktop().browse(new URI("https://onlineksrtcswift.com/"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            // Add vertical strut for gap before "Book Now"
            promoCard.add(Box.createVerticalStrut(10)); // Increased gap before "Book Now"
            promoCard.add(bookNowLabel); // Add the "Book Now" label

            promoCard.setPreferredSize(new Dimension(50, 80)); // Increased size of promo boxes
            govtBuses.add(promoCard);
        }
        content.add(govtBuses, BorderLayout.SOUTH);

        // Update the ActionListener for the search button
        searchButton.addActionListener(e -> {
            String from = fromInput.getText();
            String to = toInput.getText();
            String date = dateField.getText(); // Get the date from the text field
            
            // Validate the date format (optional)
            if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid date in yyyy-mm-dd format.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
                return; // Exit if the date format is invalid
            }

            // Call the method to search buses and display results on a new page
            displaySearchResults(from, to, date);
        });

        setVisible(true);
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
            
            if (menuItems[i].equals("Bus")) {
                menuButton.setBackground(PRIMARY_COLOR.darker());
                menuButton.setForeground(Color.WHITE);
            } else if (menuItems[i].equals("Flight")) {
                menuButton.addActionListener(e -> openOneWayFlight());
            } else if (menuItems[i].equals("Train")) {
                menuButton.addActionListener(e -> openTrainBookingApp());
            } else if (menuItems[i].equals("Cab")) {
                menuButton.addActionListener(e -> openCabBookingApp());
            }
            
            sidebar.add(menuButton);
        }

        return sidebar;
    }

    private void openOneWayFlight() {
        SwingUtilities.invokeLater(() -> {
            new OneWayFlight().setVisible(true);
            this.dispose(); // Close the current BusApp window
        });
    }

    private void openCabBookingApp() {
        SwingUtilities.invokeLater(() -> {
            new CabBookingApp().setVisible(true);
            this.dispose(); // Close the current BusApp window
        });
    }

    private void openTrainBookingApp() {
        SwingUtilities.invokeLater(() -> {
            new TrainBookingApp().setVisible(true);
            this.dispose(); // Close the current BusApp window
        });
    }

    private JButton createSidebarButton(String text, String iconPath) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (text.equals("Bus")) {
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

    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField(10);
        textField.setText(placeholder); // Set placeholder text
        textField.setForeground(Color.GRAY); // Set placeholder color

        // Add focus listener to clear placeholder on focus
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK); // Change text color to black
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder); // Reset placeholder
                }
            }
        });

        return textField;
    }

    private void searchBuses(String from, String to, String date, JPanel content) { // Added content parameter
        // Implement the logic to search for buses based on the provided parameters
        try (Connection connection = connectToDatabase()) {
            String query = "SELECT * FROM bus WHERE from_city = ? AND to_city = ? AND departure = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, from);
            preparedStatement.setString(2, to);
            preparedStatement.setString(3, date);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            displayResults(resultSet, content); // Pass the content panel
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayResults(ResultSet resultSet, JPanel content) throws SQLException {
        // Clear previous results if any
        content.removeAll(); // Clear previous results

        // Create column names for the table
        String[] columnNames = {"From City", "To City", "Departure", "Company", "Time Taken", "Available Seats"};
        
        // Create a DefaultTableModel to hold the data
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        // Populate the table model with data from the ResultSet
        while (resultSet.next()) {
            String fromCity = resultSet.getString("from_city");
            String toCity = resultSet.getString("to_city");
            String departure = resultSet.getString("departure");
            String company = resultSet.getString("company");
            String timeTaken = resultSet.getString("time_taken");
            int availableSeats = resultSet.getInt("available_seats");

            // Add a row to the table model
            tableModel.addRow(new Object[]{fromCity, toCity, departure, company, timeTaken, availableSeats});
        }

        // Create a JTable with the table model
        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true); // Make the table fill the viewport height
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setBackground(new Color(227, 244, 243)); // Pastel color for the table background
        table.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // Set header background color
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(92, 194, 191)); // Pastel color for the header
        header.setForeground(Color.WHITE); // White text for contrast
        header.setFont(new Font("Arial", Font.BOLD, 14)); // Bold font for header

        // Center the table within a JScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 200)); // Set preferred size for the scroll pane
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // Add border to scroll pane

        // Center the table in the content panel
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        tablePanel.add(Box.createVerticalGlue()); // Add vertical glue for centering
        tablePanel.add(scrollPane);
        tablePanel.add(Box.createVerticalGlue()); // Add vertical glue for centering

        // Add the table panel to the content panel
        content.add(tablePanel, BorderLayout.CENTER); // Use the passed content panel
        content.revalidate(); // Refresh the content area
        content.repaint(); // Repaint to show new results
    }

    private void resetToMainBusPage() {
        System.out.println("Resetting to main bus page..."); // Debugging line
        content.removeAll(); // Clear previous results
        initializeMainBusPage(); // Call a method to set up the main bus page
        content.revalidate(); // Refresh the content area
        content.repaint(); // Repaint to show the main bus page
    }

    private void initializeMainBusPage() {
        System.out.println("Initializing main bus page..."); // Debugging line
        // Recreate the initial layout of the main bus page
        JPanel searchSection = new JPanel();
        searchSection.setLayout(new FlowLayout());
        JTextField fromInput = new JTextField(10);
        JTextField toInput = new JTextField(10);
        JTextField dateField = createStyledTextField("yyyy-mm-dd");
        searchSection.add(new JLabel("From:"));
        searchSection.add(fromInput);
        searchSection.add(new JLabel("To:"));
        searchSection.add(toInput);
        searchSection.add(new JLabel("Date:"));
        searchSection.add(dateField);
        
        JButton searchButton = new JButton("Search Bus");
        searchSection.add(searchButton);
        content.add(searchSection, BorderLayout.CENTER);
        
        // Add any other components needed for the main bus page
        content.revalidate(); // Refresh the content area
        content.repaint(); // Repaint to show the main bus page
    }

    // New method to display search results on a new JFrame
    private void displaySearchResults(String from, String to, String date) {
        JFrame resultsFrame = new JFrame("Search Results");
        resultsFrame.setSize(800, 400);
        resultsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultsFrame.setLayout(new BorderLayout());

        // Create a content panel for results
        JPanel resultsContent = new JPanel();
        resultsContent.setLayout(new BorderLayout());

        // Add a title label
        JLabel titleLabel = new JLabel("Search Results", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        resultsContent.add(titleLabel, BorderLayout.NORTH);

        // Search for buses and display results
        try (Connection connection = connectToDatabase()) {
            String query = "SELECT * FROM bus WHERE from_city = ? AND to_city = ? AND departure = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, from);
            preparedStatement.setString(2, to);
            preparedStatement.setString(3, date);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            displayResults(resultSet, resultsContent);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Add "Book" button below the table
        JButton bookButton = new JButton("Book");
        bookButton.setBackground(new Color(26, 59, 93)); // Same color as "Search Bus" button
        bookButton.setForeground(Color.WHITE); // Set text color to white
        bookButton.setPreferredSize(new Dimension(100, 30));
        bookButton.addActionListener(e -> {
            // Open a new page with the image
            JFrame bookingFrame = new JFrame("Bus Booking successful");
            bookingFrame.setSize(700, 550);
            bookingFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            bookingFrame.setLayout(new BorderLayout());

            // Load and display the image
            try {
                ImageIcon bookingImage = new ImageIcon("C:\\Users\\aksha\\OneDrive\\Desktop\\app_swing\\icons\\bus booking.png");
                JLabel imageLabel = new JLabel(bookingImage);
                bookingFrame.add(imageLabel, BorderLayout.CENTER);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(bookingFrame, "Image not found: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            bookingFrame.setVisible(true);
        });
        
        resultsContent.add(bookButton, BorderLayout.SOUTH);

        resultsFrame.add(resultsContent, BorderLayout.CENTER);
        resultsFrame.revalidate(); // Ensure layout updates
        resultsFrame.repaint(); // Repaint to show changes
        resultsFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BusApp().setVisible(true);
        });
    }
}
