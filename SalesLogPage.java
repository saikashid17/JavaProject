import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileWriter;
import java.io.IOException;

public class SalesLogPage extends JPanel {
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel dailyTotalLabel, monthlyTotalLabel;
    private JTextField searchField;

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public SalesLogPage(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));

        JLabel title = new JLabel("Sales Log Report", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        String[] columns = {"Day", "Date", "Fruit Name", "Quantity", "Total Price", "Customer Name"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        bottomPanel.setBackground(new Color(240, 248, 255));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(new Color(240, 248, 255));

        JLabel filterLabel = new JLabel("Filter by Fruit:");
        filterFieldStyle(filterLabel);

        searchField = new JTextField(15);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void insertUpdate(DocumentEvent e) { filterTable(); }
        });

        JButton exportBtn = new JButton("Export CSV");
        styleButton(exportBtn);
        exportBtn.addActionListener(e -> exportCSV());

        JButton backBtn = new JButton("Back to Store");
        styleButton(backBtn);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "STORE"));

        filterPanel.add(filterLabel);
        filterPanel.add(searchField);
        filterPanel.add(exportBtn);
        filterPanel.add(backBtn);

        JPanel totalPanel = new JPanel(new GridLayout(1, 2));
        totalPanel.setBackground(new Color(240, 248, 255));

        dailyTotalLabel = new JLabel("Daily Total: ₹0");
        monthlyTotalLabel = new JLabel("Monthly Total: ₹0");

        labelStyle(dailyTotalLabel);
        labelStyle(monthlyTotalLabel);

        totalPanel.add(dailyTotalLabel);
        totalPanel.add(monthlyTotalLabel);

        bottomPanel.add(filterPanel);
        bottomPanel.add(totalPanel);

        add(bottomPanel, BorderLayout.SOUTH);

        updateTotals();
    }

    // Regular method: used for sales
    public void addSale(String dateStr, String fruitName, int qty, double price, String customer) {
        String dayLabel = calculateDayLabel(dateStr);
        tableModel.addRow(new Object[]{dayLabel, dateStr, fruitName, qty, price, customer});
        updateTotals();
    }

    // Overloaded method: used for discount or custom entry
    public void addSale(String day, String date, String fruitName, String qty, String price, String customer) {
        tableModel.addRow(new Object[]{day, date, fruitName, qty, price, customer});
        updateTotals();
    }

    // Adds sample sales entries for testing
    public void addSampleSales() {
        addSale("Today", "2025-05-11", "Apple", "5", "50.00", "John Doe");
        addSale("Today", "2025-05-11", "Mango", "3", "36.00", "Jane Smith");
        addSale("Yesterday", "2025-05-10", "Orange", "4", "32.00", "George Brown");
    }

    private String calculateDayLabel(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(dateStr);
            Date today = sdf.parse(sdf.format(new Date()));

            long diff = today.getTime() - date.getTime();
            long daysDiff = diff / (1000 * 60 * 60 * 24);

            if (daysDiff == 0) return "Today";
            else if (daysDiff == 1) return "Yesterday";
            else if (daysDiff == 2) return "Day Before Yesterday";
            else return daysDiff + " days ago";
        } catch (Exception e) {
            return "";
        }
    }

    private void filterFieldStyle(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(new Color(60, 60, 60));
    }

    private void labelStyle(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(new Color(30, 144, 255));
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(new Color(255, 111, 97));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(255, 74, 57));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(255, 111, 97));
            }
        });
    }

    private void styleTable() {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(135, 206, 250));
        table.getTableHeader().setForeground(Color.BLACK);
    }

    private void filterTable() {
        String search = searchField.getText().toLowerCase();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        if (search.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + search, 2));
        }
        updateTotals();
    }

    private void updateTotals() {
        double dailyTotal = 0;
        double monthlyTotal = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        String currentMonth = today.substring(0, 7);

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String date = tableModel.getValueAt(i, 1).toString();
            double total = 0;
            try {
                total = Double.parseDouble(tableModel.getValueAt(i, 4).toString());
            } catch (NumberFormatException e) {
                continue;
            }

            if (date.equals(today)) {
                dailyTotal += total;
            }
            if (date.startsWith(currentMonth)) {
                monthlyTotal += total;
            }
        }

        dailyTotalLabel.setText("Daily Total: ₹" + String.format("%.2f", dailyTotal));
        monthlyTotalLabel.setText("Monthly Total: ₹" + String.format("%.2f", monthlyTotal));
    }

    private void exportCSV() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save CSV File");
            fileChooser.setSelectedFile(new java.io.File("sales_log.csv"));
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                FileWriter writer = new FileWriter(fileToSave);

                writer.append("Day,Date,Fruit Name,Quantity,Total Price,Customer Name\n");

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        writer.append(tableModel.getValueAt(i, col).toString());
                        if (col < tableModel.getColumnCount() - 1) writer.append(",");
                    }
                    writer.append("\n");
                }

                writer.flush();
                writer.close();
                JOptionPane.showMessageDialog(this, "CSV file exported successfully!");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error exporting CSV: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
