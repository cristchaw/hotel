package user;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import config.DBConnection;

public class ViewAvailableRooms extends JFrame {

    JTable table;

    public ViewAvailableRooms() {

        setTitle("Available Rooms");
        setSize(600, 400);
        setLocationRelativeTo(null);

        String[] columns = {
            "Number",
            "Type",
            "Facilities"
        };

        DefaultTableModel model =
            new DefaultTableModel(columns, 0);

        table = new JTable(model);

        try {

            Connection conn = DBConnection.getConnection();

            String sql =
                "SELECT * FROM rooms WHERE availability=true";

            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {

                Object[] row = {
                    rs.getString("number"),
                    rs.getString("type"),
                    rs.getString("facilities")
                };

                model.addRow(row);
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, e.getMessage());
        }

        add(new JScrollPane(table));

        setVisible(true);
    }
}