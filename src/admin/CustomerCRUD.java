package admin;

import config.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CustomerCRUD extends JFrame {

    JTable table;
    DefaultTableModel model;

    public CustomerCRUD() {

        setTitle("Customer Management");
        setSize(700, 400);
        setLocationRelativeTo(null);

        model = new DefaultTableModel();

        model.setColumnIdentifiers(
            new String[]{
                "ID",
                "Name",
                "Phone",
                "Email"
            }
        );

        table = new JTable(model);
        
        table.getColumnModel()
            .getColumn(0)
            .setMinWidth(0);

        table.getColumnModel()
            .getColumn(0)
            .setMaxWidth(0);

        table.getColumnModel()
            .getColumn(0)
            .setWidth(0);

        loadCustomers();

        JScrollPane scroll =
            new JScrollPane(table);

        JButton deleteBtn =
            new JButton("Delete Customer");

        deleteBtn.addActionListener(e -> {
            deleteCustomer();
        });

        add(scroll, BorderLayout.CENTER);
        add(deleteBtn, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadCustomers() {

        try {

            Connection conn =
                DBConnection.getConnection();

            String sql =
                "SELECT * FROM customers";

            Statement st =
                conn.createStatement();

            ResultSet rs =
                st.executeQuery(sql);

            while (rs.next()) {

                model.addRow(new Object[]{

                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email")
                });
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage()
            );
        }
    }

    private void deleteCustomer() {

        try {

            int row =
                table.getSelectedRow();

            int id =
                (int) model.getValueAt(row, 0);

            Connection conn =
                DBConnection.getConnection();

            String sql =
                "DELETE FROM customers WHERE id=?";

            PreparedStatement ps =
                conn.prepareStatement(sql);

            ps.setInt(1, id);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                this,
                "Customer Deleted!"
            );

            model.setRowCount(0);

            loadCustomers();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage()
            );
        }
    }
}