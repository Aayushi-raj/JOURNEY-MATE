import java.awt.*;
import java.sql.*;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage; // Add this import statement
import javax.imageio.ImageIO; // Add this import statement
import java.io.File; // Add this import statement
import java.io.IOException; // Add this import statement

public class SignupForm extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JButton loginButton, registerButton;
    private JPanel loginPanel, registerPanel;
    private JourneyMateApp parentApp;

    private final Color MAIN_COLOR = new Color(92, 194, 191); // #5CC2BF
    private final Color TEXT_COLOR = new Color(119, 119, 119); // #777
    private final Font MAIN_FONT = new Font("Sans-serif", Font.PLAIN, 14);

    private Connection conn;
    private JTextField firstNameField, lastNameField, emailField, usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;

    // Declare these fields as instance variables
    private JTextField userIdField, emailIdField;
    private JPasswordField registerPasswordField;

    private BufferedImage backgroundImage; // Declare the backgroundImage variable
    private JLabel imageLabel; // Declare imageLabel as an instance variable

    public SignupForm(JourneyMateApp parentApp) {
        try {
            backgroundImage = ImageIO.read(new File("icons/@bg2.jpg")); // Check if this path is correct
            System.out.println("Background image loaded successfully."); // Debug statement
        } catch (IOException e) {
            System.err.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }

        this.parentApp = parentApp;
        initDatabase();
        setTitle("Signup Form");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set background image
        setContentPane(new JPanel(new BorderLayout())); // Change to BorderLayout
        getContentPane().setLayout(new BorderLayout());

        // Create form box
        JPanel formBox = new JPanel();
        formBox.setLayout(new BorderLayout());
        formBox.setBackground(Color.WHITE);
        formBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        formBox.setPreferredSize(new Dimension(380, 460));

        // Load the new image for the right side
        ImageIcon originalIcon = new ImageIcon("C:\\Users\\aksha\\OneDrive\\Desktop\\app_swing\\icons\\bg2.jpg"); // Load the original image
        Image scaledImage = originalIcon.getImage().getScaledInstance(400, 600, Image.SCALE_SMOOTH); // Increase the size of the image
        imageLabel = new JLabel(new ImageIcon(scaledImage)); // Create a label with the scaled image
        imageLabel.setLayout(new BorderLayout()); // Set layout to BorderLayout for the image label
        imageLabel.setBackground(Color.WHITE); // Set background color to white
        imageLabel.setOpaque(true); // Make the label opaque to show the background color

        // Add form box to the left and image label to the right
        getContentPane().add(formBox, BorderLayout.WEST); // Add form to the left
        getContentPane().add(imageLabel, BorderLayout.EAST); // Add image to the right

        // Remove any empty borders or margins
        imageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // Remove borders
        formBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // Remove borders from form box

        // Create button box
        JPanel buttonBox = new JPanel();
        buttonBox.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        buttonBox.setBackground(Color.WHITE);

        loginButton = createStyledButton("Sign In");
        registerButton = createStyledButton("Register");
        buttonBox.add(loginButton);
        buttonBox.add(registerButton);
        formBox.add(buttonBox, BorderLayout.NORTH);

        // Create card panel for login and register forms
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        formBox.add(cardPanel, BorderLayout.CENTER);

        // Create login panel
        loginPanel = createLoginPanel();
        cardPanel.add(loginPanel, "login");

        // Create register panel
        registerPanel = createRegisterPanel();
        cardPanel.add(registerPanel, "register");

        // Add action listeners to buttons
        loginButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "login");
            loginButton.setForeground(MAIN_COLOR.darker()); // Darker color for active "Sign In" button
            registerButton.setForeground(Color.BLACK); // Default color for "Register" button
        });
        registerButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "register");
            registerButton.setForeground(MAIN_COLOR.darker()); // Darker color for active "Register" button
            loginButton.setForeground(Color.BLACK); // Default color for "Sign In" button
        });

        // Show login panel by default
        cardLayout.show(cardPanel, "login");

        // Add form box to main content pane
        getContentPane().add(formBox, BorderLayout.CENTER);

        // Add window listener to show parent app when this form is closed
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                parentApp.setVisible(true);
            }
        });
    }

    // Add this constructor
    public SignupForm(AboutJourneyMate aboutJourneyMate) {
        // Initialize the SignupForm with reference to AboutJourneyMate if needed
        this.parentApp = null; // or handle as needed
        initDatabase();
        setTitle("Signup Form");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set background image
        setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Draw the image
                } else {
                    System.out.println("Background image is null."); // Debug statement
                }
            }
        });
        getContentPane().setLayout(new BorderLayout());

        // Create form box
        JPanel formBox = new JPanel();
        formBox.setLayout(new BorderLayout());
        formBox.setBackground(Color.WHITE);
        formBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        formBox.setPreferredSize(new Dimension(380, 460));

        // Create button box
        JPanel buttonBox = new JPanel();
        buttonBox.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        buttonBox.setBackground(Color.WHITE);

        loginButton = createStyledButton("Sign In");
        registerButton = createStyledButton("Register");
        buttonBox.add(loginButton);
        buttonBox.add(registerButton);
        formBox.add(buttonBox, BorderLayout.NORTH);

        // Initialize card layout and panel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        formBox.add(cardPanel, BorderLayout.CENTER);

        // Create login panel
        loginPanel = createLoginPanel();
        cardPanel.add(loginPanel, "login");

        // Create register panel
        registerPanel = createRegisterPanel();
        cardPanel.add(registerPanel, "register");

        // Add action listeners to buttons
        loginButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "login");
            loginButton.setForeground(MAIN_COLOR.darker()); // Darker color for active "Sign In" button
            registerButton.setForeground(Color.BLACK); // Default color for "Register" button
        });
        registerButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "register");
            registerButton.setForeground(MAIN_COLOR.darker()); // Darker color for active "Register" button
            loginButton.setForeground(Color.BLACK); // Default color for "Sign In" button
        });

        // Show login panel by default
        cardLayout.show(cardPanel, "login");

        // Add form box to main content pane
        getContentPane().add(formBox, BorderLayout.CENTER);

        // Add window listener to show parent app when this form is closed
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (aboutJourneyMate != null) {
                    aboutJourneyMate.setVisible(true);
                }
            }
        });
    }

    private void initDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:user_data.db");
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "first_name TEXT NOT NULL, " +
                    "last_name TEXT NOT NULL, " +
                    "username TEXT NOT NULL, " +
                    "email TEXT NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "role TEXT NOT NULL)");
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(MAIN_FONT);
        button.setForeground(Color.BLACK);
        button.setBackground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Draw the image
                } else {
                    System.out.println("Background image is null."); // Debug statement
                }
            }
        };
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JTextField userNameField = createStyledTextField("User Name");
        userNameField.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if(userNameField.getText().equals("User Name")){
                    userNameField.setText("");
                }
            }
        });
        panel.add(userNameField, gbc);

        gbc.gridy = 1;
        JPasswordField passwordField = new JPasswordField("Password");
        passwordField.setFont(MAIN_FONT);
        passwordField.setForeground(TEXT_COLOR);
        passwordField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        passwordField.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if(new String(passwordField.getPassword()).equals("Password")){
                    passwordField.setText("");
                }
            }
        });
        panel.add(passwordField, gbc);

        gbc.gridy = 2;
        JCheckBox rememberPassword = new JCheckBox("Remember Password");
        rememberPassword.setFont(MAIN_FONT);
        rememberPassword.setForeground(TEXT_COLOR);
        panel.add(rememberPassword, gbc);

        gbc.gridy = 3;
        JButton loginButton = createStyledSubmitButton("Log in");
        loginButton.addActionListener(e -> {
            String userName = userNameField.getText();
            String password = new String(passwordField.getPassword());
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cab", "root", "akshat");
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM signin WHERE username=? AND password=?");
                stmt.setString(1, userName);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    // Credentials match, open JourneyMateApp
                    JourneyMateApp app = new JourneyMateApp();
                    app.setVisible(true);
                    dispose(); // Close the current SignupForm window
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                System.out.println("Error connecting to database: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Database connection error.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(loginButton, gbc);

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Ensure this is called
                } else {
                    System.out.println("Background image is null."); // Debug statement
                }
            }
        };
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        userIdField = createStyledTextField("User Id"); // Use instance variable
        addPlaceholderBehavior(userIdField, "User Id");
        panel.add(userIdField, gbc);

        gbc.gridy = 1;
        emailIdField = createStyledTextField("Email Id"); // Use instance variable
        addPlaceholderBehavior(emailIdField, "Email Id");
        panel.add(emailIdField, gbc);

        gbc.gridy = 2;
        registerPasswordField = new JPasswordField("Enter password"); // Use instance variable
        registerPasswordField.setFont(MAIN_FONT);
        registerPasswordField.setForeground(TEXT_COLOR);
        registerPasswordField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        addPlaceholderBehavior(registerPasswordField, "Enter password");
        panel.add(registerPasswordField, gbc);

        gbc.gridy = 3;
        JCheckBox agreeTerms = new JCheckBox("I agree to the terms");
        agreeTerms.setFont(MAIN_FONT);
        agreeTerms.setForeground(TEXT_COLOR);
        panel.add(agreeTerms, gbc);

        gbc.gridy = 4;
        JButton signUpButton = createStyledSubmitButton("Sign-up");
        signUpButton.addActionListener(e -> handleSignUp());
        panel.add(signUpButton, gbc);

        return panel;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder);
        textField.setFont(MAIN_FONT);
        textField.setForeground(TEXT_COLOR);
        textField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        return textField;
    }

    private JButton createStyledSubmitButton(String text) {
        JButton button = new JButton(text);
        button.setFont(MAIN_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(MAIN_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void handleLogin() {
        // Here you would typically validate the login credentials
        // For now, we'll just close this form and show the JourneyMateApp
        parentApp.setVisible(true);
        this.dispose();
    }

    private void handleSignUp() {
        // Retrieve user input from registration fields
        String userId = userIdField.getText();
        String emailId = emailIdField.getText();
        String password = new String(registerPasswordField.getPassword()); // Correctly retrieve password

        // Insert user data into the signin table
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cab", "root", "akshat");
            String sql = "INSERT INTO signin (username, emailid, password) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
            stmt.setString(2, emailId);
            stmt.setString(3, password);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            System.out.println("Error inserting into database: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Database error during registration.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showInitialView(String initialView) {
        if ("login".equals(initialView)) {
            cardLayout.show(cardPanel, "login");
            loginButton.setForeground(MAIN_COLOR.darker()); // Darker color for active "Sign In" button
            registerButton.setForeground(Color.BLACK); // Default color for "Register" button
        } else if ("register".equals(initialView)) {
            cardLayout.show(cardPanel, "register");
            registerButton.setForeground(MAIN_COLOR.darker()); // Darker color for active "Register" button
            loginButton.setForeground(Color.BLACK); // Default color for "Sign In" button
        }
    }

    private void addPlaceholderBehavior(JTextField textField, String placeholder) {
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(TEXT_COLOR);
                    textField.setText(placeholder);
                }
            }
        });
    }

    // Overloaded method for JPasswordField
    private void addPlaceholderBehavior(JPasswordField passwordField, String placeholder) {
        passwordField.setEchoChar((char) 0); // Show text instead of dots
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (new String(passwordField.getPassword()).equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setEchoChar('•'); // Set echo char to hide text
                    passwordField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (new String(passwordField.getPassword()).isEmpty()) {
                    passwordField.setForeground(TEXT_COLOR);
                    passwordField.setText(placeholder);
                    passwordField.setEchoChar((char) 0); // Show text instead of dots
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JourneyMateApp app = new JourneyMateApp();
            new SignupForm(app).setVisible(true);
        });
    }
}
