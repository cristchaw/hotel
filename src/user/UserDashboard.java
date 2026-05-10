package user;

import javax.swing.*;
import java.awt.*;

import auth.LoginForm;

public class UserDashboard extends JFrame {

    public UserDashboard() {

        setTitle("User Dashboard");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(
            new GridLayout(5, 1, 15, 15)
        );

        JButton facilityBtn =
            new JButton("View Available Rooms");

        JButton bookingBtn =
            new JButton("Book Room");

        JButton historyBtn =
            new JButton("My Bookings");

        JButton logoutBtn =
            new JButton("Logout");

        panel.add(facilityBtn);
        panel.add(bookingBtn);
        panel.add(historyBtn);
        panel.add(logoutBtn);

        add(panel);
        facilityBtn.addActionListener(e -> {

            new ViewAvailableRooms();
        });

        bookingBtn.addActionListener(e -> {

            new BookingForm();
        });

        historyBtn.addActionListener(e -> {

            new MyBookings();
        });

        logoutBtn.addActionListener(e -> {

            dispose();

            new LoginForm();
        });

        setVisible(true);
    }
}