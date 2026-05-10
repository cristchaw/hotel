package admin;

import config.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BookingCRUD extends JFrame {

    JTable table;
    DefaultTableModel model;

    public BookingCRUD() {

        setTitle("Booking Management");
        setSize(1100, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = new DefaultTableModel();

        model.setColumnIdentifiers(
            new String[]{
                "Booking ID",
                "Customer",
                "Room",
                "Check-in",
                "Check-out",
                "Status"
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

        loadBookings();

        JScrollPane scroll =
            new JScrollPane(table);

        JPanel btnPanel =
            new JPanel();

        JButton approveBtn =
            new JButton("Approve");

        JButton rejectBtn =
            new JButton("Reject");

        JButton checkinBtn =
            new JButton("Check In");

        JButton checkoutBtn =
            new JButton("Check Out");

        btnPanel.add(approveBtn);
        btnPanel.add(rejectBtn);
        btnPanel.add(checkinBtn);
        btnPanel.add(checkoutBtn);

        approveBtn.addActionListener(e -> {
            updateBooking("APPROVED");
        });

        rejectBtn.addActionListener(e -> {
            updateBooking("REJECTED");
        });

        checkinBtn.addActionListener(e -> {
            updateBooking("CHECKED_IN");
        });

        checkoutBtn.addActionListener(e -> {
            updateBooking("CHECKED_OUT");
        });

        add(scroll, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadBookings() {

        try {

            model.setRowCount(0);

            updateOverdue();

            Connection conn =
                DBConnection.getConnection();

            String sql =
                """
                SELECT
                    bookings.id,
                    customers.name,
                    rooms.number,
                    bookings.checkin_date,
                    bookings.checkout_date,
                    bookings.status
                FROM bookings
                JOIN customers
                    ON bookings.customer_id = customers.id
                JOIN rooms
                    ON bookings.room_id = rooms.id
                ORDER BY bookings.id DESC
                """;

            Statement st =
                conn.createStatement();

            ResultSet rs =
                st.executeQuery(sql);

            while (rs.next()) {

                model.addRow(new Object[]{

                    rs.getInt("id"),

                    rs.getString("name"),

                    rs.getString("number"),

                    rs.getTimestamp("checkin_date"),

                    rs.getTimestamp("checkout_date"),

                    rs.getString("status")
                });
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage()
            );
        }
    }

    private void updateOverdue() {

        try {

            Connection conn = DBConnection.getConnection();

            String sql =
                """
                UPDATE bookings
                SET status='OVERDUE'
                WHERE
                    status='CHECKED_IN'
                    AND NOW() > TIMESTAMP(DATE(checkout_date), '12:00:00')
                """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateBooking(String status) {

        try {

            int row =
                table.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(
                    this,
                    "Please select booking!"
                );

                return;
            }

            int bookingId =
                (int) model.getValueAt(row, 0);

            String roomNumber =
                model.getValueAt(row, 2).toString();

            String currentStatus =
                model.getValueAt(row, 5).toString();


            if (
                status.equals("CHECKED_IN") &&
                !currentStatus.equals("APPROVED")
            ) {

                JOptionPane.showMessageDialog(
                    this,
                    "Booking must be APPROVED first!"
                );

                return;
            }

            if (
                status.equals("CHECKED_OUT") &&
                !currentStatus.equals("CHECKED_IN") &&
                !currentStatus.equals("OVERDUE")
            ) {

                JOptionPane.showMessageDialog(
                    this,
                    "Customer must CHECK IN first!"
                );

                return;
            }

            if (
                status.equals("APPROVED") &&
                currentStatus.equals("REJECTED")
            ) {

                JOptionPane.showMessageDialog(
                    this,
                    "Rejected booking cannot be approved!"
                );

                return;
            }

            if (
                status.equals("APPROVED") &&
                currentStatus.equals("OVERDUE")
            ) {

                JOptionPane.showMessageDialog(
                    this,
                    "Overdue booking cannot be approved!"
                );

                return;
            }

            if (
                status.equals("REJECTED") &&
                (
                    currentStatus.equals("CHECKED_OUT") ||
                    currentStatus.equals("OVERDUE")
                )
            ) {

                JOptionPane.showMessageDialog(
                    this,
                    "Completed booking cannot be rejected!"
                );

                return;
            }

            if (
                currentStatus.equals(status)
            ) {

                JOptionPane.showMessageDialog(
                    this,
                    "Booking already in "
                    + status
                    + " status!"
                );

                return;
            }

            Connection conn =
                DBConnection.getConnection();


            String sql = "";

            if (status.equals("CHECKED_IN")) {

                sql =
                    """
                    UPDATE bookings
                    SET
                        status=?,
                        checkin_date=NOW()
                    WHERE id=?
                    """;

            }

            else if (status.equals("CHECKED_OUT")) {

                sql =
                    """
                    UPDATE bookings
                    SET
                        status=?,
                        checkout_date=NOW()
                    WHERE id=?
                    """;

            }

            else {

                sql =
                    """
                    UPDATE bookings
                    SET status=?
                    WHERE id=?
                    """;
            }

            PreparedStatement ps =
                conn.prepareStatement(sql);

            ps.setString(1, status);
            ps.setInt(2, bookingId);

            ps.executeUpdate();


            if (
                status.equals("APPROVED") ||
                status.equals("CHECKED_IN") ||
                status.equals("OVERDUE")
            ) {

                String roomSql =
                    """
                    UPDATE rooms
                    SET availability=false
                    WHERE number=?
                    """;

                PreparedStatement roomPs =
                    conn.prepareStatement(roomSql);

                roomPs.setString(1, roomNumber);

                roomPs.executeUpdate();
            }

            if (
                status.equals("CHECKED_OUT") ||
                status.equals("REJECTED")
            ) {

                String roomSql =
                    """
                    UPDATE rooms
                    SET availability=true
                    WHERE number=?
                    """;

                PreparedStatement roomPs =
                    conn.prepareStatement(roomSql);

                roomPs.setString(1, roomNumber);

                roomPs.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                this,
                "Booking Updated Successfully!"
            );

            loadBookings();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage()
            );
        }
    }
}

