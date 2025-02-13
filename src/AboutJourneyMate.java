import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI; // Add this import statement
 // Ensure this import is correctly placed
import java.awt.font.TextAttribute; // Add this import statement
import java.util.HashMap; // Add this import statement
import java.util.Map; // Add this import statement

public class AboutJourneyMate extends JFrame {

    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255); // #F0F8FF
    private static final Color HEADER_COLOR = new Color(92, 194, 191); // #5CC2BF
    private static final Color SUBHEADER_COLOR = new Color(49, 151, 149); // #319795
    private static final Color SECTION_BACKGROUND = new Color(227, 244, 243); // #E3F4F3
    private static final Color SECONDARY_COLOR = new Color(49, 151, 149); 
    public AboutJourneyMate() {
        setTitle("About Journey Mate");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Create Sign In and Register buttons
        JPanel authButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        authButtonPanel.setOpaque(false);
        
        JButton signInButton = createStyledButton("SIGN IN");
        JButton registerButton = createStyledButton("REGISTER");

        Dimension buttonSize = new Dimension(100, 30);
        signInButton.setPreferredSize(buttonSize);
        registerButton.setPreferredSize(buttonSize);

        // Add action listeners to the buttons
        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SignupForm signupForm = new SignupForm(AboutJourneyMate.this);
                signupForm.showInitialView("login");
                signupForm.setVisible(true);
                setVisible(false);
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SignupForm signupForm = new SignupForm(AboutJourneyMate.this);
                signupForm.showInitialView("register");
                signupForm.setVisible(true);
                setVisible(false);
            }
        });

    

        authButtonPanel.add(signInButton);
        authButtonPanel.add(registerButton);

        // Add the auth button panel to the main panel
        mainPanel.add(authButtonPanel);

        // Title
        JLabel titleLabel = new JLabel("About Journey Mate");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(HEADER_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Welcome text
        JTextArea welcomeText = createTextArea("Welcome to Journey Mate! We are your one-stop solution for all your travel needs. Whether you are booking a flight, a train ride, or a cab—be it for daily commute, an outstation trip, or a rental—you can find it all here.");

        // Services section
        JPanel servicesPanel = createSection("Our Services:", createServicesList());

        // Why Choose Us section
        JPanel whyChooseUsPanel = createSection("Why Choose Us?", createWhyChooseUsList());

        // Final text
        JTextArea finalText = createTextArea("Let Journey Mate make your travels more comfortable, affordable, and stress-free. Happy traveling!");

        // Contact Us label
        JLabel contactUsLabel = new JLabel("Contact Us");
        contactUsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        contactUsLabel.setForeground(Color.RED);
        contactUsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add underline to the font
        Font font = contactUsLabel.getFont();
        Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>(font.getAttributes());
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        contactUsLabel.setFont(font.deriveFont(attributes));

        // Add a mouse listener to make the label clickable
        contactUsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://www.instagram.com/journeymate_2024/"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Add components to main panel
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(welcomeText);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(servicesPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(whyChooseUsPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(finalText);
        mainPanel.add(Box.createVerticalStrut(20)); // Add some space before the contact label
        mainPanel.add(contactUsLabel); // Add the contact us label

        // Scroll pane for main panel
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane);
    }

    private JTextArea createTextArea(String text) {
        JTextArea textArea = new JTextArea(text);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setOpaque(false);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        return textArea;
    }

    private JPanel createSection(String title, JComponent content) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(SECTION_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(SUBHEADER_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(content);

        return panel;
    }

    private JPanel createServicesList() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(createBulletPoint("Cab Bookings:", "Daily Rides, Outstation Trips, Car Rentals"));
        panel.add(createBulletPoint("Flight Bookings:", "Find the best flights at unbeatable prices"));
        panel.add(createBulletPoint("Train Bookings:", "Book your train journeys with ease"));
        panel.add(createBulletPoint("Bus Bookings:", "Book your bus journeys in a click"));

        return panel;
    }

    private JPanel createWhyChooseUsList() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(createBulletPoint("Seamless Booking Experience:", "Our user-friendly interface ensures quick and easy bookings"));
        panel.add(createBulletPoint("Affordable Pricing:", "Enjoy exclusive offers and discounts on flights, trains, cabs and buses"));
        panel.add(createBulletPoint("24/7 Support:", "We are available round the clock to assist with any of your travel needs"));

        return panel;
    }

    private JPanel createBulletPoint(String title, String description) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("• " + title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(titleLabel);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(descLabel);

        return panel;
    }

    private JLabel createPromoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(HEADER_COLOR);
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
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

 

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AboutJourneyMate().setVisible(true);
            }
        });
    }
}
