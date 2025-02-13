import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.JTableHeader;

public class CabRentals extends JFrame {
    private JPanel contentPane;
    private List<Rental> rentals;

    // Add this line at the beginning of the CabRentals class
    private static final Font HEADER_FONT = new Font("Samarkan", Font.PLAIN, 36); // Define the font
    private static final Color PRIMARY_COLOR = new Color(49, 151, 149); // Define the primary color
    private static final Color SIDEBAR_COLOR = new Color(227, 244, 243); // Define the sidebar color
    private static final Color SECONDARY_COLOR = new Color(92, 194, 191); // Define the secondary color

    public CabRentals() {
        setTitle("Journey Mate - Cab Rentals");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLayout(new BorderLayout());

        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(new Color(227, 244, 243)); // #e3f4f3
        setContentPane(contentPane);

        // Header
        JPanel headerPanel = createHeaderPanel();
        contentPane.add(headerPanel, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebarPanel = createSidebarPanel();
        contentPane.add(sidebarPanel, BorderLayout.WEST);

        // Main content
        JPanel mainContentPanel = createMainContentPanel();
        contentPane.add(mainContentPanel, BorderLayout.CENTER);

        // Initialize rentals data
        initializeRentals();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        headerPanel.setPreferredSize(new Dimension(1000, 75)); 

        JLabel logoLabel = new JLabel("journey mate");
        logoLabel.setFont(HEADER_FONT); // Use the same font as in TrainBookingApp
        logoLabel.setForeground(SECONDARY_COLOR); // Use the same color as in TrainBookingApp
        headerPanel.add(logoLabel, BorderLayout.WEST);

        // Create a panel for the About button with padding
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10)); // Add padding to the right

        // Create "About" button
        JButton aboutButton = createStyledButton("About");
        aboutButton.setFont(new Font("Arial", Font.BOLD, 14)); // Set font size to match TrainBookingApp
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
            this.dispose(); // Close the current CabRentals window
        });

        buttonPanel.add(aboutButton);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        return headerPanel;
    }
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(new Color(49, 151, 149)); // #319795
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(new Color(49, 151, 149)));
        button.setFocusPainted(false);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(49, 151, 149));
                button.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
                button.setForeground(new Color(49, 151, 149));
            }
        });
        return button;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(4, 1, 0, 10));
        sidebar.setPreferredSize(new Dimension(100, getHeight())); // Reduced width
        sidebar.setBackground(SIDEBAR_COLOR); // Use the same color as in CabBookingApp
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 5)); // Reduced horizontal padding

        String[] menuItems = {"Flight", "Train", "Cab", "Bus"};
        String[] iconPaths = {"icons/flights.png", "icons/train.png", "icons/cab.png", "icons/bus.png"};

        for (int i = 0; i < menuItems.length; i++) {
            JButton menuButton = createSidebarButton(menuItems[i], iconPaths[i]);
            
            if (menuItems[i].equals("Cab")) {
                menuButton.setBackground(SECONDARY_COLOR); // Use the same color as in CabBookingApp
                menuButton.setForeground(Color.WHITE);
                // Remove the hover effect for the Cab button
                menuButton.removeMouseListener(menuButton.getMouseListeners()[0]);
            } else if (menuItems[i].equals("Flight")) {
                menuButton.addActionListener(e -> openJourneyMateApp());
            } else if (menuItems[i].equals("Train")) {
                menuButton.addActionListener(e -> openTrainBookingApp());
            } else if (menuItems[i].equals("Bus")) {
                menuButton.addActionListener(e -> openBusApp());
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
                if (text.equals("Cab")) {
                    g2.setColor(new Color(49, 151, 149)); // Darker color for Cab
                } else if (getModel().isPressed()) {
                    g2.setColor(new Color(92, 194, 191).darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(92, 194, 191));
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

    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(227, 244, 243)); // #e3f4f3
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add spacing before tabs
        mainPanel.add(Box.createRigidArea(new Dimension(0, 100)));

        // Tabs
        JPanel tabsPanel = createTabsPanel();
        mainPanel.add(tabsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Search box
        JPanel searchBoxPanel = createSearchBoxPanel();
        mainPanel.add(searchBoxPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Statistics
        JPanel statisticsPanel = createStatisticsPanel();
        mainPanel.add(statisticsPanel);

        return mainPanel;
    }

    private JPanel createTabsPanel() {
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tabPanel.setOpaque(false);
        tabPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        String[] tabs = {"Daily", "Outstation", "Rentals"};
        for (int i = 0; i < tabs.length; i++) {
            JButton tabButton = new JButton(tabs[i]);
            tabButton.setPreferredSize(new Dimension(120, 40));  // Increased button size
            tabButton.setForeground(Color.WHITE);
            tabButton.setBackground(i == 2 ? new Color(49, 151, 149) : new Color(92, 194, 191));
            tabButton.setBorderPainted(false);
            tabButton.setFocusPainted(false);
            tabPanel.add(tabButton);

            if (tabs[i].equals("Outstation")) {
                tabButton.addActionListener(e -> {
                    SwingUtilities.invokeLater(() -> new CabOutstation());
                    this.dispose();
                });
            }

            if (tabs[i].equals("Daily")) {
                tabButton.addActionListener(e -> {
                    SwingUtilities.invokeLater(() -> new CabBookingApp());
                    this.dispose();
                });
            }
            
            tabPanel.add(tabButton);
        }

        return tabPanel;
    }

    private JPanel createSearchBoxPanel() {
        JPanel searchBoxPanel = new JPanel();
        searchBoxPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 70));
        searchBoxPanel.setBackground(new Color(173, 226, 225)); // #ade2e1

        JTextField locationField = new JTextField("Enter Location");
        locationField.setPreferredSize(new Dimension(200, 30));

        addPlaceholderBehavior(locationField, "Enter Location");

        String[] times1 = {"Start Time", "12:00 AM", "01:00 AM", "02:00 AM", "03:00 AM", "04:00 AM", "05:00 AM", "06:00 AM", "07:00 AM", "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM", "06:00 PM", "07:00 PM", "08:00 PM", "09:00 PM", "10:00 PM", "11:00 PM"};
        JComboBox<String> startTimeComboBox = new JComboBox<>(times1);
        String[] times2 = {"End Time", "12:00 AM", "01:00 AM", "02:00 AM", "03:00 AM", "04:00 AM", "05:00 AM", "06:00 AM", "07:00 AM", "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM", "06:00 PM", "07:00 PM", "08:00 PM", "09:00 PM", "10:00 PM", "11:00 PM"};
        JComboBox<String> endTimeComboBox = new JComboBox<>(times2);

        JButton searchButton = new JButton("SEARCH RENTALS");
        searchButton.setPreferredSize(new Dimension(200, 30)); // Elongate the button
        searchButton.setBackground(new Color(26, 59, 93)); // #1a3b5d
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);

        searchButton.addActionListener(e -> {
            String location = locationField.getText().trim();
            String startTime = (String) startTimeComboBox.getSelectedItem();
            String endTime = (String) endTimeComboBox.getSelectedItem();
            searchRentals(location, startTime, endTime);
        });

        searchBoxPanel.add(locationField);
        searchBoxPanel.add(startTimeComboBox);
        searchBoxPanel.add(endTimeComboBox);
        searchBoxPanel.add(searchButton);

        return searchBoxPanel;
    }

    private void initializeRentals() {
        rentals = new ArrayList<>();
        rentals.add(new Rental(1, "Bangalore", "08:00:00", "12:00:00", "Bike", "Yamaha", 200.00, true));
        rentals.add(new Rental(2, "Mumbai", "10:00:00", "14:00:00", "Car", "Honda", 600.00, false));
        rentals.add(new Rental(3, "Delhi", "09:30:00", "17:30:00", "Scooter", "Vespa", 150.00, true));
        rentals.add(new Rental(4, "Hyderabad", "11:00:00", "19:00:00", "Car", "Ford", 550.00, true));
        rentals.add(new Rental(5, "Kolkata", "08:30:00", "13:30:00", "Bike", "Royal Enfield", 300.00, false));
        rentals.add(new Rental(6, "Chennai", "00:00:00", "12:00:00", "Car", "Toyota", 500.00, true));
        rentals.add(new Rental(7, "SRM University", "09:00:00", "18:00:00", "Car", "Toyota", 1600.00, true));
        rentals.add(new Rental(8, "SRM University", "09:00:00", "18:00:00", "Car", "Suzuki", 1400.00, true));
        rentals.add(new Rental(9, "SRM University", "09:00:00", "18:00:00", "Bike", "Royal Enfield", 1080.00, false));
        rentals.add(new Rental(10, "SRM University", "09:00:00", "18:00:00", "Scooter", "Vespa", 540.00, true));
        // Add more rentals as per your table
    }

    private void searchRentals(String location, String startTime, String endTime) {
        List<Rental> matchingRentals = new ArrayList<>();
        for (Rental rental : rentals) {
            if (rental.getLocation().equalsIgnoreCase(location.trim())) {
                matchingRentals.add(rental);
            }
        }

        if (!matchingRentals.isEmpty()) {
            SwingUtilities.invokeLater(() -> new RentalResultsPage(matchingRentals));
        } else {
            JOptionPane.showMessageDialog(this, 
                "No rentals found for the location: " + location, 
                "Availiable Rentals", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JPanel createStatisticsPanel() {
        JPanel statisticsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statisticsPanel.setOpaque(false);
        statisticsPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));

        String[][] stats = {
            {"500+", "Rides Completed"},
            {"350+", "Happy Customers"},
            {"100+", "Available Cabs"}
        };

        for (String[] stat : stats) {
            JPanel statBox = new JPanel();
            statBox.setLayout(new BoxLayout(statBox, BoxLayout.Y_AXIS));
            statBox.setBackground(Color.WHITE);
            statBox.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            JLabel numberLabel = new JLabel(stat[0]);
            numberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            numberLabel.setFont(new Font("Arial", Font.BOLD, 24));

            JLabel descriptionLabel = new JLabel(stat[1]);
            descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 16));

            statBox.add(Box.createVerticalGlue());
            statBox.add(numberLabel);
            statBox.add(Box.createRigidArea(new Dimension(0, 5)));
            statBox.add(descriptionLabel);
            statBox.add(Box.createVerticalGlue());

            statisticsPanel.add(statBox);
        }

        return statisticsPanel;
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

    private void openJourneyMateApp() {
        SwingUtilities.invokeLater(() -> new JourneyMateApp());
        this.dispose();
    }

    private void openTrainBookingApp() {
        SwingUtilities.invokeLater(() -> new TrainBookingApp());
        this.dispose();
    }

    private void openBusApp() {
        SwingUtilities.invokeLater(() -> new BusBookingApp());
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CabRentals().setVisible(true));
    }
}

class Rental {
    private int rentalId;
    private String location;
    private String startTime;
    private String endTime;
    private String vehicleType;
    private String brand;
    private double pricePerHour;
    private boolean availability;

    // Constructor
    public Rental(int rentalId, String location, String startTime, String endTime, String vehicleType, String brand, double pricePerHour, boolean availability) {
        this.rentalId = rentalId;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.vehicleType = vehicleType;
        this.brand = brand;
        this.pricePerHour = pricePerHour;
        this.availability = availability;
    }

    // Getters
    public int getRentalId() { return rentalId; }
    public String getLocation() { return location; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getVehicleType() { return vehicleType; }
    public String getBrand() { return brand; }
    public double getPricePerHour() { return pricePerHour; }
    public boolean isAvailable() { return availability; }
}

class RentalResultsPage extends JFrame {
    public RentalResultsPage(List<Rental> rentals) {
        setTitle("Search Results");
        setSize(900, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create a panel for better organization
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // Create a title label
        JLabel titleLabel = new JLabel("Availiable Rentals");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultsPanel.add(titleLabel);
        resultsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between title and results

        // Prepare data for the table
        String[] columnNames = {"Rental ID", "Location", "Start Time", "End Time", "Vehicle Type", "Brand", "Price", "Availability"};
        Object[][] data = new Object[rentals.size()][8];

        for (int i = 0; i < rentals.size(); i++) {
            Rental rental = rentals.get(i);
            data[i] = new Object[]{
                rental.getRentalId(),
                rental.getLocation(),
                rental.getStartTime(),
                rental.getEndTime(),
                rental.getVehicleType(),
                rental.getBrand(),
                rental.getPricePerHour(),
                rental.isAvailable() ? "Available" : "Not Available"
            };
        }

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

        // Add a "Book" button below the table
        JButton bookButton = new JButton("Book");
        bookButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        bookButton.setPreferredSize(new Dimension(100, 30));
        bookButton.setBackground(new Color(26, 59, 93)); // Match the color of the search button
        bookButton.setForeground(Color.WHITE);
        bookButton.setFocusPainted(false);
        bookButton.addActionListener(e -> {
            // Open a new page with the booking success image
            openBookingSuccessPage();
        });
        resultsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between table and button
        resultsPanel.add(bookButton);

        // Add the results panel to the frame
        add(resultsPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void openBookingSuccessPage() {
        JFrame bookingSuccessFrame = new JFrame("Booking Successful");
        bookingSuccessFrame.setSize(800, 775);
        bookingSuccessFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        bookingSuccessFrame.setLayout(new BorderLayout());

        // Load and display the image
        try {
            ImageIcon successIcon = new ImageIcon("C:\\Users\\aksha\\OneDrive\\Desktop\\app_swing\\icons\\booking success.jpg");
            JLabel imageLabel = new JLabel(successIcon);
            bookingSuccessFrame.add(imageLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Image not found: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        bookingSuccessFrame.setVisible(true);
    }
}

// Add this class at the end of the file
class BusBookingApp extends JFrame {
    public BusBookingApp() {
        setTitle("Bus Booking");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Add components and functionality for bus booking here
        setVisible(true);
    }
}
