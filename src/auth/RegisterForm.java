package auth;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

import config.DBConnection;

public class RegisterForm extends JFrame {

    JTextField nameField;
    JTextField phoneField;
    JTextField emailField;
    JTextField usernameField;

    JPasswordField passwordField;

    public RegisterForm() {

        setTitle("Register");
        setSize(450, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(
            new GridLayout(6, 2, 10, 10)
        );

        panel.add(new JLabel("Full Name"));

        nameField = new JTextField();

        panel.add(nameField);

        panel.add(new JLabel("Phone"));

        phoneField = new JTextField();

        panel.add(phoneField);

        panel.add(new JLabel("Email"));

        emailField = new JTextField();

        panel.add(emailField);

        panel.add(new JLabel("Username"));

        usernameField = new JTextField();

        panel.add(usernameField);

        panel.add(new JLabel("Password"));

        passwordField = new JPasswordField();

        panel.add(passwordField);

        JButton registerBtn =
            new JButton("Register");

        panel.add(registerBtn);

        add(panel);

        registerBtn.addActionListener(e -> {
            register();
        });

        setVisible(true);
    }

    private void register() {

        try {

            Connection conn =
                DBConnection.getConnection();

            if (
                nameField.getText().isEmpty() ||
                phoneField.getText().isEmpty() ||
                emailField.getText().isEmpty() ||
                usernameField.getText().isEmpty() ||
                String.valueOf(
                    passwordField.getPassword()
                ).isEmpty()
            ) {

                JOptionPane.showMessageDialog(
                    this,
                    "Please fill all fields!"
                );

                return;
            }

            String sql =
                "INSERT INTO users(username, password, role) VALUES (?, ?, 'USER')";

            PreparedStatement ps =
                conn.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
                );

            ps.setString(
                1,
                usernameField.getText()
            );

            ps.setString(
                2,
                String.valueOf(
                    passwordField.getPassword()
                )
            );

            ps.executeUpdate();

            ResultSet rs =
                ps.getGeneratedKeys();

            int userId = 0;

            if (rs.next()) {

                userId = rs.getInt(1);
            }

            String customerSql =
                """
                INSERT INTO customers(
                    user_id,
                    name,
                    phone,
                    email
                )
                VALUES (?, ?, ?, ?)
                """;

            PreparedStatement cps =
                conn.prepareStatement(customerSql);

            cps.setInt(1, userId);

            cps.setString(
                2,
                nameField.getText()
            );

            cps.setString(
                3,
                phoneField.getText()
            );

            cps.setString(
                4,
                emailField.getText()
            );

            cps.executeUpdate();

            JOptionPane.showMessageDialog(
                this,
                "Register Success!"
            );

            dispose();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage()
            );
        }
    }
}