package user;

import config.DBConnection;
import config.Session;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

import java.util.Date;
import java.text.SimpleDateFormat;

public class BookingForm extends JFrame {

    JComboBox<String> roomBox;

    JSpinner checkinSpinner;
    JSpinner checkoutSpinner;

    public BookingForm() {

        setTitle("Book Room");
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(
            new GridLayout(4, 2, 10, 10)
        );

        panel.add(new JLabel("Select Room"));

        roomBox = new JComboBox<>();

        loadRooms();

        panel.add(roomBox);

        panel.add(new JLabel("Check-in Date"));

        checkinSpinner = new JSpinner(
            new SpinnerDateModel()
        );

        JSpinner.DateEditor checkinEditor =
            new JSpinner.DateEditor(
                checkinSpinner,
                "yyyy-MM-dd"
            );

        checkinSpinner.setEditor(checkinEditor);

        panel.add(checkinSpinner);

        panel.add(new JLabel("Check-out Date"));

        checkoutSpinner = new JSpinner(
            new SpinnerDateModel()
        );

        JSpinner.DateEditor checkoutEditor =
            new JSpinner.DateEditor(
                checkoutSpinner,
                "yyyy-MM-dd"
            );

        checkoutSpinner.setEditor(checkoutEditor);

        panel.add(checkoutSpinner);

        JButton bookBtn = new JButton("Book");

        panel.add(bookBtn);

        add(panel);

        bookBtn.addActionListener(e -> bookRoom());

        setVisible(true);
    }

    private void loadRooms() {

        try {

            Connection conn = DBConnection.getConnection();

            String sql =
                "SELECT * FROM rooms WHERE availability=true";

            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {

                String item =
                    rs.getInt("id")
                    + " - Room "
                    + rs.getString("number")
                    + " ("
                    + rs.getString("type")
                    + ")";

                roomBox.addItem(item);
            }

            if (roomBox.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, "No rooms are currently available.");
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void bookRoom() {

        try {

            Connection conn = DBConnection.getConnection();

            if (roomBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Please select an available room first!");
                return;
            }
            String selected =
                roomBox.getSelectedItem().toString();

            int roomId =
                Integer.parseInt(selected.split(" - ")[0]);

            String customerSql =
                "SELECT * FROM customers WHERE user_id=?";

            PreparedStatement cps =
                conn.prepareStatement(customerSql);

            cps.setInt(1, Session.userId);

            ResultSet crs = cps.executeQuery();

            int customerId = 0;

            if (crs.next()) {

                customerId = crs.getInt("id");

            } else {

                JOptionPane.showMessageDialog(
                    this,
                    "Customer profile not found!"
                );

                return;
            }

            String sql =
                "INSERT INTO bookings(customer_id, room_id, checkin_date, checkout_date, status) VALUES (?, ?, ?, ?, 'PENDING')";

            PreparedStatement ps =
                conn.prepareStatement(sql);

            ps.setInt(1, customerId);
            ps.setInt(2, roomId);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            String checkin =
                sdf.format(
                    (Date) checkinSpinner.getValue()
                );

            String checkout =
                sdf.format(
                    (Date) checkoutSpinner.getValue()
                );

            ps.setString(3, checkin);
            ps.setString(4, checkout);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                this,
                "Booking Request Sent!"
            );

            dispose();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}