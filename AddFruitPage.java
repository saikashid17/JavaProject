import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;

public class AddFruitPage extends JFrame {
    public AddFruitPage(DefaultTableModel model) {
        setTitle("Add New Fruit");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 10, 10));

        JLabel nameLbl = new JLabel("Fruit Name:");
        JTextField nameField = new JTextField();

        JLabel catLbl = new JLabel("Category:");
        JComboBox<String> categoryBox = new JComboBox<>(new String[]{
            "Tropical", "Berries", "Citrus", "Melons"
        });

        JLabel qtyLbl = new JLabel("Quantity:");
        JTextField qtyField = new JTextField();

        JLabel priceLbl = new JLabel("Unit Price:");
        JTextField priceField = new JTextField();

        JButton addBtn = new JButton("Add Fruit");
        addBtn.setBackground(new Color(60, 179, 113));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);

        add(nameLbl); add(nameField);
        add(catLbl); add(categoryBox);
        add(qtyLbl); add(qtyField);
        add(priceLbl); add(priceField);
        add(new JLabel()); add(addBtn);

        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String category = (String) categoryBox.getSelectedItem();
            try {
                int qty = Integer.parseInt(qtyField.getText());
                double price = Double.parseDouble(priceField.getText());

                model.addRow(new Object[]{
                    model.getRowCount() + 1,
                    name,
                    category,
                    qty,
                    String.format("%.2f", price)
                });
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric values.");
            }
        });

        setVisible(true);
    }
}