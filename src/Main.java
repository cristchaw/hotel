import javax.swing.UIManager;
import auth.LoginForm;

public class Main {

    public static void main(String[] args) {

        try {

            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName()
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        new LoginForm();
    }
}