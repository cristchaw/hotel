package admin;

import javax.swing.*;
import java.awt.*;

import auth.LoginForm;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {

        setTitle("Admin Dashboard");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(
            new GridLayout(5, 1, 15, 15)
        );

        JButton roomBtn =
            new JButton("Manage Rooms");

        JButton customerBtn =
            new JButton("Manage Customers");

        JButton bookingBtn =
            new JButton("Manage Bookings");

        JButton logoutBtn =
            new JButton("Logout");

        panel.add(roomBtn);
        panel.add(customerBtn);
        panel.add(bookingBtn);
        panel.add(logoutBtn);

        add(panel);

        roomBtn.addActionListener(e -> {
            new RoomCRUD();
        });

        customerBtn.addActionListener(e -> {
            new CustomerCRUD();
        });

        bookingBtn.addActionListener(e -> {
            new BookingCRUD();
        });

        logoutBtn.addActionListener(e -> {

            dispose();

            new LoginForm();
        });

        setVisible(true);
    }
}
