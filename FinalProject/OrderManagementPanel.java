import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderManagementPanel extends JPanel {
    String[][] fruits = {
        {"Apple", "Banana", "Mango", "Pineapple", "Papaya", "Guava"},
        {"Strawberry", "Blueberry", "Raspberry", "Blackberry", "Cranberry", "Gooseberry"},
        {"Orange", "Lemon", "Lime", "Grapefruit", "Tangerine", "Pomelo"},
        {"Watermelon", "Cantaloupe", "Honeydew", "Galia", "Dragon", "Santa Claus"}
    };

    double[][] prices = {
        {10, 5, 12, 15, 14, 8},
        {25, 30, 28, 27, 26, 24},
        {8, 6, 5.5, 7.5, 9, 10},
        {20, 22, 21, 23, 24, 25}
    };

    JTable table;
    DefaultTableModel model;
    JTextArea receipt;
    JTextField cashField;
    JTextField custNameField; // new customer name field
    JLabel totalLabel, balanceLabel;
    JComboBox<String> unitBox;
    double total = 0;
    JButton printBtn;
    JButton backToStoreBtn;
    JTextField discountField;  // For discount input
    JButton applyDiscountBtn;  // For applying the discount

    private InventoryManagementPanel inventoryPanel; // Reference inventory panel
    private SalesLogPage salesLogPage; // Reference sales log panel

    public OrderManagementPanel(ActionListener backListener, InventoryManagementPanel inventoryPanel, SalesLogPage salesLogPage) {
        this.inventoryPanel = inventoryPanel;
        this.salesLogPage = salesLogPage;
        setLayout(null);
        setPreferredSize(new Dimension(1600, 800));

        JPanel panel = createShopPanel();
        panel.setBounds(0, 0, 1920, 1080);
        add(panel);

        backToStoreBtn = new JButton("Back to Store");
        backToStoreBtn.setBounds(1310, 670, 150, 30);
        backToStoreBtn.setBackground(new Color(0, 120, 215));
        backToStoreBtn.setForeground(Color.WHITE);
        backToStoreBtn.setFocusPainted(false);
        backToStoreBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backToStoreBtn.addActionListener(backListener);
        panel.add(backToStoreBtn);
    }

    private JPanel createShopPanel() {
        JPanel panel = new JPanel(null) {
            Image background = new ImageIcon("backgrounds/Shop_bg.jpg").getImage();
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        };

      // Discount Section
        JLabel discountLabel = new JLabel("Discount (%):");
        discountLabel.setBounds(1000, 460, 120, 30);
        discountLabel.setForeground(Color.WHITE);
        panel.add(discountLabel);

        discountField = new JTextField();
        discountField.setBounds(1125, 460, 60, 30);
        panel.add(discountField);

        applyDiscountBtn = new JButton("Apply Discount");
        applyDiscountBtn.setBounds(1200, 460, 150, 30);
        applyDiscountBtn.setBackground(new Color(0, 150, 0));
        applyDiscountBtn.setForeground(Color.WHITE);
        applyDiscountBtn.addActionListener(e -> applyDiscount());
        panel.add(applyDiscountBtn);

        JLabel title = new JLabel("DYP Fruit Shop", JLabel.CENTER);
        title.setBounds(0, 0, 1920, 50);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setOpaque(true);
        title.setBackground(new Color(255, 153, 51));
        title.setForeground(Color.WHITE);
        panel.add(title);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(50, 70, 900, 400);
        panel.add(tabbedPane);

        String[] categories = {"Tropical", "Berries", "Citrus", "Melones"};

        for (int i = 0; i < fruits.length; i++) {
            JPanel fruitPanel = new JPanel(new GridLayout(2, 3, 10, 10));
            fruitPanel.setBackground(new Color(255, 255, 204));
            for (int j = 0; j < fruits[i].length; j++) {
                String name = fruits[i][j];
                double price = prices[i][j];
                String imagePath = "fruit_images/" + name.toLowerCase().replace(" ", "") + ".jpg";

                ImageIcon icon = null;
                try {
                    icon = new ImageIcon(imagePath);
                    if (icon.getIconWidth() <= 0) {
                        System.err.println("Image not found or invalid: " + imagePath);
                        icon = null;
                    } else {
                        Image scaledImage = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                        icon = new ImageIcon(scaledImage);
                    }
                } catch (Exception ex) {
                    System.err.println("Error loading image: " + imagePath + " : " + ex.getMessage());
                    icon = null;
                }

                JButton btn = new JButton("<html><center>" + name + "<br>₹" + price + "/unit</center></html>", icon);
                btn.setHorizontalTextPosition(SwingConstants.CENTER);
                btn.setVerticalTextPosition(SwingConstants.BOTTOM);
                btn.setBackground(new Color(255, 204, 153));

                int row = i;
                int col = j;
                btn.addActionListener(e -> addToCart(name, prices[row][col]));
                fruitPanel.add(btn);
            }
            tabbedPane.add(categories[i], fruitPanel);
        }

        model = new DefaultTableModel(new String[]{"Fruit", "Qty", "Price"}, 0);
        table = new JTable(model);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBounds(1000, 70, 400, 200);
        panel.add(tableScroll);

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBounds(1000, 280, 100, 30);
        deleteBtn.setBackground(Color.RED);
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.addActionListener(e -> deleteItem());
        panel.add(deleteBtn);

        JButton cartBtn = new JButton("Cart");
        cartBtn.setBounds(1110, 280, 100, 30);
        cartBtn.setBackground(Color.BLUE);
        cartBtn.setForeground(Color.WHITE);
        cartBtn.addActionListener(e -> showCartDialog());
        panel.add(cartBtn);

        JButton resetBtn = new JButton("Reset");
        resetBtn.setBounds(1220, 280, 100, 30);
        resetBtn.setBackground(Color.GRAY);
        resetBtn.setForeground(Color.WHITE);
        resetBtn.addActionListener(e -> resetCart());
        panel.add(resetBtn);

        unitBox = new JComboBox<>(new String[]{"1", "2", "5", "10"});
        unitBox.setBounds(1400, 280, 60, 30);
        panel.add(unitBox);

        JLabel custNameLabel = new JLabel("Customer Name:");
        custNameLabel.setBounds(1000, 330, 120, 30);
        custNameLabel.setForeground(Color.WHITE);
        panel.add(custNameLabel);

        custNameField = new JTextField();
        custNameField.setBounds(1125, 330, 210, 30);
        panel.add(custNameField);

        JLabel cashLabel = new JLabel("Cash Received:");
        cashLabel.setBounds(1000, 370, 120, 30);
        cashLabel.setForeground(Color.WHITE);
        panel.add(cashLabel);

        cashField = new JTextField();
        cashField.setBounds(1125, 370, 100, 30);
        panel.add(cashField);

        JButton payBtn = new JButton("Pay");
        payBtn.setBounds(1235, 370, 100, 30);
        payBtn.setBackground(Color.GREEN);
        payBtn.addActionListener(e -> pay());
        panel.add(payBtn);

        totalLabel = new JLabel("Total: ₹0.00");
        totalLabel.setBounds(1000, 410, 200, 30);
        totalLabel.setForeground(Color.WHITE);
        panel.add(totalLabel);

        balanceLabel = new JLabel("Balance: ₹0.00");
        balanceLabel.setBounds(1200, 410, 200, 30);
        balanceLabel.setForeground(Color.WHITE);
        panel.add(balanceLabel);

        receipt = new JTextArea();
        receipt.setEditable(false);
        receipt.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane receiptScroll = new JScrollPane(receipt);
        receiptScroll.setBounds(1000, 450, 400, 200);
        panel.add(receiptScroll);

        printBtn = new JButton("Print");
        printBtn.setBounds(1000, 670, 100, 30);
        printBtn.setEnabled(false);
        printBtn.setBackground(new Color(244, 194, 194));
        printBtn.setForeground(Color.WHITE);
        printBtn.addActionListener(e -> printReceipt());
        panel.add(printBtn);

        return panel;
    }

    void addToCart(String name, double pricePerUnit) {
        int qty = Integer.parseInt((String) unitBox.getSelectedItem());
        int availableStock = inventoryPanel.getStock(name);

        if (qty > availableStock) {
            JOptionPane.showMessageDialog(this, "Out of Stock! Available quantity for " + name + ": " + availableStock,
                    "Out of Stock", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double price = pricePerUnit * qty;
        model.addRow(new Object[]{name, qty, price});
        total += price;
        totalLabel.setText("Total: ₹" + String.format("%.2f", total));

        // Reduce inventory stock
        inventoryPanel.reduceStock(name, qty);
    }

    void deleteItem() {
        int row = table.getSelectedRow();
        if (row != -1) {
            double price = (double) model.getValueAt(row, 2);
            String fruitName = (String) model.getValueAt(row, 0);
            int qty = (int) model.getValueAt(row, 1);

            total -= price;
            model.removeRow(row);
            totalLabel.setText("Total: ₹" + String.format("%.2f", total));

            // Return the quantity back to inventory stock
            inventoryPanel.reduceStock(fruitName, -qty); // negative to add back stock
        }
    }

    void pay() {
        try {
            String customerName = custNameField.getText().trim();
            if (customerName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter customer name!");
                return;
            }
            double cash = Double.parseDouble(cashField.getText());
            if (cash < total) {
                JOptionPane.showMessageDialog(this, "Insufficient Cash!");
                return;
            }
            double balance = cash - total;
            balanceLabel.setText("Balance: ₹" + String.format("%.2f", balance));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());

            for (int i = 0; i < model.getRowCount(); i++) {
                String fruitName = (String) model.getValueAt(i, 0);
                int quantity = (int) model.getValueAt(i, 1);
                double price = (double) model.getValueAt(i, 2);
                salesLogPage.addSale(today, fruitName, quantity, price, customerName);
            }

            // Generate receipt including Customer Name
            receipt.setText("========= DYP Fruit Shop =========\n");
            receipt.append("Customer: " + customerName + "\n\n");
            for (int i = 0; i < model.getRowCount(); i++) {
                receipt.append(model.getValueAt(i, 0) + " x " + model.getValueAt(i, 1) + " = ₹" + model.getValueAt(i, 2) + "\n");
            }
            receipt.append("\n----------------------------------\n");
            receipt.append("Total: ₹" + String.format("%.2f", total) + "\n");
            receipt.append("Cash: ₹" + String.format("%.2f", cash) + "\n");
            receipt.append("Balance: ₹" + String.format("%.2f", balance) + "\n");
            receipt.append("==================================\n");
            printBtn.setEnabled(true);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid cash amount!");
        }
    }

    void printReceipt() {
        try {
            receipt.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Print failed!");
        }
    }

    void resetCart() {
        model.setRowCount(0);
        total = 0;
        totalLabel.setText("Total: ₹0.00");
        balanceLabel.setText("Balance: ₹0.00");
        cashField.setText("");
        custNameField.setText("");
        receipt.setText("");
        printBtn.setEnabled(false);
    }

    void showCartDialog() {
        JDialog cartDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Cart", Dialog.ModalityType.APPLICATION_MODAL);
        JTable cartTable = new JTable(model);
        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartDialog.add(cartScroll);
        cartDialog.setSize(400, 300);
        cartDialog.setLocationRelativeTo(this);
        cartDialog.setVisible(true);
    }
   void applyDiscount() {
        try {
            String discountText = discountField.getText().trim();
            if (!discountText.isEmpty()) {
                double discount = Double.parseDouble(discountText);
                if (discount < 0 || discount > 100) {
                    JOptionPane.showMessageDialog(this, "Discount must be between 0% and 100%.");
                    return;
                }
                double discountAmount = (total * discount) / 100;
                total -= discountAmount;
                totalLabel.setText("Total: ₹" + String.format("%.2f", total));
                JOptionPane.showMessageDialog(this, "Discount applied: ₹" + String.format("%.2f", discountAmount));
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid discount percentage!");
        }
    }
}