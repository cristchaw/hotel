package user;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.*;

import config.DBConnection;

public class ViewFacilities extends JFrame {

    JTable table;

    DefaultTableModel model;

    public ViewFacilities() {

        setTitle("Room Facilities");
        setSize(800, 400);
        setLocationRelativeTo(null);

        String[] columns = {

            "Room Number",
            "Room Type",
            "Facilities"
        };

        model =
            new DefaultTableModel(columns, 0);

        table =
            new JTable(model);

        add(
            new JScrollPane(table),
            BorderLayout.CENTER
        );

        loadFacilities();

        setVisible(true);
    }

    private void loadFacilities() {

        try {

            Connection conn =
                DBConnection.getConnection();

            String sql =
                """
                SELECT
                    number,
                    type,
                    facilities
                FROM rooms
                """;

            Statement st =
                conn.createStatement();

            ResultSet rs =
                st.executeQuery(sql);

            while (rs.next()) {

                Object[] row = {

                    rs.getString("number"),

                    rs.getString("type"),

                    rs.getString("facilities")
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
}