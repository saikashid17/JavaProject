import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class InventoryManagementPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private HashMap<String, Integer> inventoryStock;
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;

    public InventoryManagementPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // Title
        JLabel titleLabel = new JLabel("Inventory Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        inventoryStock = new HashMap<>();
        initializeStock();

        // Table data and model
        String[] columnNames = {"ID", "Name", "Category", "Quantity", "Unit Price"};
        Object[][] data = {
            {1, "Apple", "Tropical", 12, 42.13},
            {2, "Banana", "Tropical", 17, 48.69},
            {3, "Mango", "Tropical", 2, 35.48},
            {4, "Pineapple", "Tropical", 11, 18.09},
            {5, "Papaya", "Tropical", 1, 52.75},
            {6, "Guava", "Tropical", 9, 47.77},
            {7, "Strawberry", "Berries", 16, 16.17},
            {8, "Blueberry", "Berries", 7, 18.18},
            {9, "Raspberry", "Berries", 13, 10.14},
            {10, "Blackberry", "Berries", 11, 11.46},
            {11, "Cranberry", "Berries", 20, 27.32},
            {12, "Gooseberry", "Berries", 18, 47.86},
            {13, "Orange", "Citrus", 5, 40.00},
            {14, "Lemon", "Citrus", 1, 58.36},
            {15, "Lime", "Citrus", 16, 33.91},
            {16, "Grapefruit", "Citrus", 3, 32.04},
            {17, "Tangerine", "Citrus", 4, 14.06},
            {18, "Pomelo", "Citrus", 18, 44.16},
            {19, "Watermelon", "Melons", 10, 15.99},
            {20, "Cantaloupe", "Melons", 1, 13.77},
            {21, "Honeydew", "Melons", 9, 31.37},
            {22, "Galia", "Melons", 14, 10.09},
            {23, "Dragon", "Melons", 20, 33.69},
            {24, "Santa Claus", "Melons", 7, 23.05}
        };

        model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4; // Quantity and Unit Price editable
            }
        };

        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.setSelectionBackground(new Color(255, 204, 204));

        // Cell renderer: pink background if quantity < 10
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    try {
                        int quantity = Integer.parseInt(table.getModel().getValueAt(row, 3).toString());
                        if (quantity < 10) {
                            c.setBackground(new Color(255, 200, 200));
                        } else {
                            c.setBackground(Color.WHITE);
                        }
                    } catch (NumberFormatException e) {
                        c.setBackground(Color.WHITE);
                    }
                } else {
                    c.setBackground(table.getSelectionBackground());
                }
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);

        // Top panel: search field + button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        searchBtn.setBackground(new Color(255, 111, 97));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        searchBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                searchBtn.setBackground(new Color(255, 74, 57));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                searchBtn.setBackground(new Color(255, 111, 97));
            }
        });
        searchBtn.addActionListener(e -> filterTable());
        topPanel.add(new JLabel("Search Fruit:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);

        // Bottom panel: Add, Edit, Delete, Back buttons
        JPanel bottomPanel = new JPanel();

        JButton addBtn = new JButton("Add");
        configureButton(addBtn);
        addBtn.addActionListener(e -> addFruit());

        JButton editBtn = new JButton("Edit");
        configureButton(editBtn);
        editBtn.addActionListener(e -> editSelectedFruit());

        JButton deleteBtn = new JButton("Delete");
        configureButton(deleteBtn);
        deleteBtn.addActionListener(e -> deleteSelectedFruit());

        JButton backBtn = new JButton("Back to Store");
        configureButton(backBtn);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "STORE"));

        bottomPanel.add(addBtn);
        bottomPanel.add(editBtn);
        bottomPanel.add(deleteBtn);
        bottomPanel.add(backBtn);
        bottomPanel.setOpaque(false);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Update internal stock when Quantity changes
        model.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (col == 3) { // Quantity updated
                String fruitName = model.getValueAt(row, 1).toString();
                try {
                    int newQty = Integer.parseInt(model.getValueAt(row, 3).toString());
                    inventoryStock.put(fruitName, newQty);
                    table.repaint();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid quantity entered.");
                }
            }
        });
    }

    private void configureButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBackground(new Color(255, 111, 97));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { btn.setBackground(new Color(255, 74, 57)); }
            public void mouseExited(MouseEvent evt) { btn.setBackground(new Color(255, 111, 97)); }
        });
    }

    private void filterTable() {
        String searchText = searchField.getText().toLowerCase().trim();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        if (searchText.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, 1));
        }
    }

    private void addFruit() {
        // Simple input dialogs for demo; can be replaced by a custom form
        try {
            String name = JOptionPane.showInputDialog(this, "Enter fruit name:");
            if (name == null || name.trim().isEmpty()) return;
            String category = JOptionPane.showInputDialog(this, "Enter category:");
            if (category == null) category = "";
            String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity:");
            if (qtyStr == null) return;
            int qty = Integer.parseInt(qtyStr);
            String priceStr = JOptionPane.showInputDialog(this, "Enter unit price:");
            if (priceStr == null) return;
            double price = Double.parseDouble(priceStr);

            int nextId = model.getRowCount() + 1;
            model.addRow(new Object[]{nextId, name, category, qty, price});
            inventoryStock.put(name, qty);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number input.");
        }
    }

    private void editSelectedFruit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a fruit to edit.");
            return;
        }
        row = table.convertRowIndexToModel(row);
        try {
            String currentName = model.getValueAt(row, 1).toString();
            String name = JOptionPane.showInputDialog(this, "Edit fruit name:", currentName);
            if (name == null || name.trim().isEmpty()) return;
            String currentCategory = model.getValueAt(row, 2).toString();
            String category = JOptionPane.showInputDialog(this, "Edit category:", currentCategory);
            if (category == null) category = "";
            String currentQtyStr = model.getValueAt(row, 3).toString();
            String qtyStr = JOptionPane.showInputDialog(this, "Edit quantity:", currentQtyStr);
            if (qtyStr == null) return;
            int qty = Integer.parseInt(qtyStr);
            String currentPriceStr = model.getValueAt(row, 4).toString();
            String priceStr = JOptionPane.showInputDialog(this, "Edit unit price:", currentPriceStr);
            if (priceStr == null) return;
            double price = Double.parseDouble(priceStr);

            model.setValueAt(name, row, 1);
            model.setValueAt(category, row, 2);
            model.setValueAt(qty, row, 3);
            model.setValueAt(price, row, 4);

            inventoryStock.put(name, qty);
            table.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number input.");
        }
    }

    private void deleteSelectedFruit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a fruit to delete.");
            return;
        }
        row = table.convertRowIndexToModel(row);
        String name = model.getValueAt(row, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete '" + name + "'?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            model.removeRow(row);
            inventoryStock.remove(name);
            table.repaint();
        }
    }

    private void initializeStock() {
        inventoryStock.put("Apple", 12);
        inventoryStock.put("Banana", 17);
        inventoryStock.put("Mango", 2);
        inventoryStock.put("Pineapple", 11);
        inventoryStock.put("Papaya", 1);
        inventoryStock.put("Guava", 9);
        inventoryStock.put("Strawberry", 16);
        inventoryStock.put("Blueberry", 7);
        inventoryStock.put("Raspberry", 13);
        inventoryStock.put("Blackberry", 11);
        inventoryStock.put("Cranberry", 20);
        inventoryStock.put("Gooseberry", 18);
        inventoryStock.put("Orange", 5);
        inventoryStock.put("Lemon", 1);
        inventoryStock.put("Lime", 16);
        inventoryStock.put("Grapefruit", 3);
        inventoryStock.put("Tangerine", 4);
        inventoryStock.put("Pomelo", 18);
        inventoryStock.put("Watermelon", 10);
        inventoryStock.put("Cantaloupe", 1);
        inventoryStock.put("Honeydew", 9);
        inventoryStock.put("Galia", 14);
        inventoryStock.put("Dragon", 20);
        inventoryStock.put("Santa Claus", 7);
    }

    public int getStock(String fruitName) {
        return inventoryStock.getOrDefault(fruitName, 0);
    }

    public void reduceStock(String fruitName, int quantity) {
        int currentStock = inventoryStock.getOrDefault(fruitName, 0);
        int updatedStock = Math.max(0, currentStock - quantity);
        inventoryStock.put(fruitName, updatedStock);
        table.repaint();
    }
}