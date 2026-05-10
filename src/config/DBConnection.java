package config;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {

        try {

            String url = "jdbc:mysql://localhost:3306/hotel_booking?serverTimezone=Asia/Jakarta";;
            String user = "root";
            String password = "";

            return DriverManager.getConnection(url, user, password);

        } catch (Exception e) {

            System.out.println(e.getMessage());
            return null;
        }
    }
}