package user;

import config.DBConnection;
import config.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.*;

import java.text.SimpleDateFormat;

public class MyBookings extends JFrame {

    JTable table;
    DefaultTableModel model;

    public MyBookings() {

        setTitle("My Bookings");
        setSize(900, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = new DefaultTableModel();

        model.setColumnIdentifiers(
            new String[]{

                "Room",
                "Type",
                "Check-in",
                "Check-out",
                "Status"
            }
        );

        table = new JTable(model);

        loadBookings();

        add(
            new JScrollPane(table),
            BorderLayout.CENTER
        );

        setVisible(true);
    }

    private void loadBookings() {

        try {

            model.setRowCount(0);

            Connection conn =
                DBConnection.getConnection();

            String customerSql =
                """
                SELECT id
                FROM customers
                WHERE user_id=?
                """;

            PreparedStatement cps =
                conn.prepareStatement(customerSql);

            cps.setInt(1, Session.userId);

            ResultSet crs =
                cps.executeQuery();

            int customerId = 0;

            if (crs.next()) {

                customerId =
                    crs.getInt("id");
            }

            String sql =
                """
                SELECT
                    rooms.number,
                    rooms.type,
                    bookings.checkin_date,
                    bookings.checkout_date,
                    bookings.status
                FROM bookings
                JOIN rooms
                    ON bookings.room_id = rooms.id
                WHERE bookings.customer_id=?
                ORDER BY bookings.id DESC
                """;

            PreparedStatement ps =
                conn.prepareStatement(sql);

            ps.setInt(1, customerId);

            ResultSet rs =
                ps.executeQuery();

            SimpleDateFormat sdf =
                new SimpleDateFormat(
                    "dd MMM yyyy HH:mm"
                );

            while (rs.next()) {

                Timestamp checkin =
                    rs.getTimestamp(3);

                Timestamp checkout =
                    rs.getTimestamp(4);

                model.addRow(new Object[]{

                    rs.getString(1),

                    rs.getString(2),

                    checkin != null
                        ? sdf.format(checkin)
                        : "-",

                    checkout != null
                        ? sdf.format(checkout)
                        : "-",

                    rs.getString(5)
                });
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage()
            );
        }
    }
}