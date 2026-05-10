package auth;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import admin.AdminDashboard;
import user.UserDashboard;
import config.DBConnection;
import config.Session;

public class LoginForm extends JFrame {

    JTextField usernameField;
    JPasswordField passwordField;

    public LoginForm() {

        setTitle("Login");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.add(new JLabel("Username"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        panel.add(loginBtn);
        panel.add(registerBtn);

        add(panel);

        loginBtn.addActionListener(e -> login());

        registerBtn.addActionListener(e -> {
            new RegisterForm();
        });

        setVisible(true);
    }

    private void login() {

    try {

        Connection conn = DBConnection.getConnection();

        String sql =
            "SELECT * FROM users WHERE username=? AND password=?";

        PreparedStatement ps =
            conn.prepareStatement(sql);

        ps.setString(1, usernameField.getText());

        ps.setString(
            2,
            String.valueOf(passwordField.getPassword())
        );

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {

            Session.userId = rs.getInt("id");
            String checkCustomer =
                "SELECT * FROM customers WHERE user_id=?";

            PreparedStatement cps =
                conn.prepareStatement(checkCustomer);

            cps.setInt(1, Session.userId);

            ResultSet crs = cps.executeQuery();

            if (!crs.next()) {

                String insertCustomer =
                    "INSERT INTO customers(user_id, name) VALUES (?, ?)";

                PreparedStatement ips =
                    conn.prepareStatement(insertCustomer);

                ips.setInt(1, Session.userId);
                ips.setString(2, usernameField.getText());

                ips.executeUpdate();
            }
            Session.role = rs.getString("role");

            String role = rs.getString("role");

            JOptionPane.showMessageDialog(
                this,
                "Login Success!"
            );

            dispose();

            if (role.equals("ADMIN")) {

                new AdminDashboard();

            } else {

                new UserDashboard();
            }

        } else {

            JOptionPane.showMessageDialog(
                this,
                "Invalid username or password"
            );
        }

    } catch (Exception e) {

        JOptionPane.showMessageDialog(
            this,
            e.getMessage()
        );
    }
}
}   