package admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

import config.DBConnection;

public class RoomCRUD extends JFrame {

    JTextField numberField;
    JTextField typeField;
    JTextField facilitiesField;

    JTable table;
    DefaultTableModel model;

    public RoomCRUD() {

        setTitle("Room Management");
        setSize(900, 500);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(
            new GridLayout(4, 2, 10, 10)
        );

        topPanel.add(new JLabel("Room Number"));

        numberField = new JTextField();

        topPanel.add(numberField);

        topPanel.add(new JLabel("Room Type"));

        typeField = new JTextField();

        topPanel.add(typeField);

        topPanel.add(new JLabel("Facilities"));

        facilitiesField = new JTextField();

        topPanel.add(facilitiesField);

        JButton addBtn =
            new JButton("Add Room");

        JButton updateBtn =
            new JButton("Update Room");

        JButton deleteBtn =
            new JButton("Delete Room");

        topPanel.add(addBtn);
        topPanel.add(updateBtn);
        topPanel.add(deleteBtn);

        add(topPanel, BorderLayout.NORTH);

        String[] columns = {

            "ID",
            "Number",
            "Type",
            "Facilities",
            "Available"
        };

        model =
            new DefaultTableModel(columns, 0);

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

        table.getSelectionModel().addListSelectionListener(e -> {

            int row = table.getSelectedRow();

            if (row != -1) {

                numberField.setText(
                    model.getValueAt(row, 1).toString()
                );

                typeField.setText(
                    model.getValueAt(row, 2).toString()
                );

                facilitiesField.setText(
                    model.getValueAt(row, 3).toString()
                );
            }
        });

        add(
            new JScrollPane(table),
            BorderLayout.CENTER
        );

        loadRooms();

        addBtn.addActionListener(e -> {
            addRoom();
        });

        updateBtn.addActionListener(e -> {
            updateRoom();
        });

        deleteBtn.addActionListener(e -> {
            deleteRoom();
        });

        setVisible(true);
    }

    private void loadRooms() {

        try {

            model.setRowCount(0);

            Connection conn =
                DBConnection.getConnection();

            Statement st =
                conn.createStatement();

            ResultSet rs =
                st.executeQuery(
                    "SELECT * FROM rooms"
                );

            while (rs.next()) {

                Object[] row = {

                    rs.getInt("id"),

                    rs.getString("number"),

                    rs.getString("type"),

                    rs.getString("facilities"),

                    rs.getBoolean("availability")
                };

                model.addRow(row);
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage()
            );
        }
    }

    private void addRoom() {

        try {

            if (
                numberField.getText().isEmpty() ||
                typeField.getText().isEmpty() ||
                facilitiesField.getText().isEmpty()
            ) {

                JOptionPane.showMessageDialog(
                    this,
                    "Please fill all fields!"
                );

                return;
            }

            Connection conn =
                DBConnection.getConnection();

            String sql =
                """
                INSERT INTO rooms(
                    number,
                    type,
                    facilities,
                    availability
                )
                VALUES (?, ?, ?, true)
                """;

            PreparedStatement ps =
                conn.prepareStatement(sql);

            ps.setString(
                1,
                numberField.getText()
            );

            ps.setString(
                2,
                typeField.getText()
            );

            ps.setString(
                3,
                facilitiesField.getText()
            );

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                this,
                "Room Added!"
            );

            numberField.setText("");
            typeField.setText("");
            facilitiesField.setText("");

            loadRooms();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage()
            );
        }
    }

    private void updateRoom() {

        try {

            int row =
                table.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(
                    this,
                    "Select room first!"
                );

                return;
            }

            int id =
                (int) model.getValueAt(row, 0);

            Connection conn =
                DBConnection.getConnection();

            String sql =
                """
                UPDATE rooms
                SET
                    number=?,
                    type=?,
                    facilities=?
                WHERE id=?
                """;

            PreparedStatement ps =
                conn.prepareStatement(sql);

            ps.setString(
                1,
                numberField.getText()
            );

            ps.setString(
                2,
                typeField.getText()
            );

            ps.setString(
                3,
                facilitiesField.getText()
            );

            ps.setInt(4, id);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                this,
                "Room Updated!"
            );

            numberField.setText("");
            typeField.setText("");
            facilitiesField.setText("");

            loadRooms();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage()
            );
        }
    }

    private void deleteRoom() {

        try {

            int row =
                table.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(
                    this,
                    "Select room first!"
                );

                return;
            }

            int id =
                (int) model.getValueAt(row, 0);

            Connection conn =
                DBConnection.getConnection();

            String sql =
                "DELETE FROM rooms WHERE id=?";

            PreparedStatement ps =
                conn.prepareStatement(sql);

            ps.setInt(1, id);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                this,
                "Room Deleted!"
            );

            loadRooms();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage()
            );
        }
    }
}