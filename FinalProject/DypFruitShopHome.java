import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class DypFruitShopHome extends JFrame {

    private Image backgroundImage;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private InventoryManagementPanel inventoryPanel; // Declare inventoryPanel here
    private SalesLogPage salesLogPage; // Declare sales log panel here

    public DypFruitShopHome() {
        setTitle("DYP Fruit Shop");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false);

        setLocationRelativeTo(null);
        setResizable(false);

        try {
            backgroundImage = ImageIO.read(new File("fruit.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
            backgroundImage = null;
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createHomePagePanel(), "HOME");

        // Create inventory panel first and keep reference
        inventoryPanel = new InventoryManagementPanel(cardLayout, mainPanel);
        mainPanel.add(inventoryPanel, "INVENTORY");

        mainPanel.add(createVisitStorePanel(), "STORE");

        // Create sales log panel first and keep reference
        salesLogPage = new SalesLogPage(cardLayout, mainPanel);
mainPanel.add(salesLogPage, "SALES");

// Add sample sales AFTER full construction to avoid NPE
SwingUtilities.invokeLater(() -> {
    salesLogPage.addSampleSales();
});

       
        // Pass inventoryPanel and salesLogPage to OrderManagementPanel
        mainPanel.add(createOrderManagementPanel(inventoryPanel, salesLogPage), "ORDER");

        setContentPane(mainPanel);
    }

    private JPanel createHomePagePanel() {
        BackgroundPanel homePanel = new BackgroundPanel();
        homePanel.setLayout(new GridBagLayout());
        homePanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("DYP Fruit Shop");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 70));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel welcomeLabel = new JLabel("Welcome to our fresh and juicy fruit store!");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Brush Script MT", Font.ITALIC, 35));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton visitButton = new JButton("Visit Store");
        visitButton.setFont(new Font("Segoe UI", Font.BOLD, 28));
        visitButton.setBackground(new Color(255, 111, 97));
        visitButton.setForeground(Color.WHITE);
        visitButton.setFocusPainted(false);
        visitButton.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        visitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        visitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                visitButton.setBackground(new Color(255, 74, 57));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                visitButton.setBackground(new Color(255, 111, 97));
            }
        });
        visitButton.addActionListener(e -> cardLayout.show(mainPanel, "STORE"));

        gbc.gridy = 0;
        homePanel.add(titleLabel, gbc);
        gbc.gridy = 1;
        homePanel.add(welcomeLabel, gbc);
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        homePanel.add(visitButton, gbc);

        return homePanel;
    }

    private JPanel createVisitStorePanel() {
        JPanel storePanel = new JPanel(new BorderLayout());
        storePanel.setBackground(new Color(240, 248, 255));

        JLabel header = new JLabel("Store Management", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 36));
        header.setForeground(new Color(30, 30, 30));
        header.setBorder(BorderFactory.createEmptyBorder(30, 10, 20, 10));
        storePanel.add(header, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        gbc.gridx = 0; gbc.gridy = 0;
        gridPanel.add(createRoundedCard("Inventory", "INVENTORY", "📦"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gridPanel.add(createRoundedCard("Orders", "ORDER", "🛒"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gridPanel.add(createRoundedCard("Sales", "SALES", "💰"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gridPanel.add(createRoundedCard("Home", "HOME", "⬅"), gbc);

        storePanel.add(gridPanel, BorderLayout.CENTER);
        return storePanel;
    }

    private JPanel createRoundedCard(String text, String cardName, String iconEmoji) {
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.setColor(new Color(160, 160, 160));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 40, 40);
                g2.dispose();
            }
        };

        card.setOpaque(false);
        card.setBackground(new Color(255, 255, 255, 230));
        card.setPreferredSize(new Dimension(280, 160));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel iconLabel = new JLabel(iconEmoji, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel textLabel = new JLabel(text, SwingConstants.CENTER);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        textLabel.setForeground(Color.DARK_GRAY);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(textLabel);
        card.add(Box.createVerticalGlue());

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(255, 240, 230));
                card.repaint();
            }

            public void mouseExited(MouseEvent e) {
                card.setBackground(new Color(255, 255, 255, 230));
                card.repaint();
            }

            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, cardName);
            }
        });

        return card;
    }

    private JPanel createOrderManagementPanel(InventoryManagementPanel inventoryPanel, SalesLogPage salesLogPage) {
        return new OrderManagementPanel(e -> cardLayout.show(mainPanel, "STORE"), inventoryPanel, salesLogPage);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DypFruitShopHome app = new DypFruitShopHome();
            app.setVisible(true);
        });
    }

    // BackgroundPanel class inside DypFruitShopHome
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            try {
                backgroundImage = ImageIO.read(new File("fruit.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}