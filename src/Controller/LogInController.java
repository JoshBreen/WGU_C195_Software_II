package Controller;

import Utilities.DBQuery;
import Utilities.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Joshua Breen C195 Project
 * LoginController Used for logging into the app. Takes the username and password and checks it against the database. Also
 * saves the login attempts and gets locale info for language usage.
 */

public class LogInController implements Initializable {
    public Button loginButton;
    public Button exitButton;
    public TextField usernameTB;
    public TextField passwordTB;
    public Label loginErrorLabel;
    public Label locationLabel;
    public Label usernameLbl;
    public Label titleLabel;
    public Label passwordLabel;
    public Label locationLabelTitle;
    private static String userLoginType;
    ResourceBundle rb = ResourceBundle.getBundle("BreenScheduling", Locale.getDefault());
    String fileName = "src/login_activity.txt";


    /***
     * Sets the language variables to reflect English or French phrasing.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        String locale = ZoneId.systemDefault().getId();
        locationLabel.setText(locale);
        usernameLbl.setText(rb.getString("userName"));
        passwordLabel.setText(rb.getString("pass"));
        locationLabelTitle.setText(rb.getString("loc"));
        exitButton.setText(rb.getString("exit"));
        loginButton.setText(rb.getString("login"));
        titleLabel.setText(rb.getString("loginTitle"));
    }


    /***
     * Used to pass the user type to the main window
     * @return userLoginType to the main window to keep track of current user
     */
    public static String getUserLoginType(){
        return userLoginType;
    }


    /***
     * Connects to the Database and pulls the information from users, User_Name and Password. It checks the information in the userNameTB to the Users data and passwordTB to Password to
     * confirm valid login credentials. Creates entry in text file about successful and unsuccessful login attempts.
     * @param actionEvent when clickLogin button is clicked
     * @throws IOException
     * @throws SQLException
     */
    public void clickLogin(ActionEvent actionEvent) throws IOException, SQLException {


        //Creates connection for DB
        Connection conn = DBConnection.getConnection();

        //Gets Usernames and Passwords from the Database
        String selectStatement = "SELECT * FROM users";
        DBQuery.setPreparedStatement(conn, selectStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute();
        ResultSet rs = ps.getResultSet();

        //Creates the file/appends the file for login attempts
        FileWriter fWriter = new FileWriter(fileName, true);
        PrintWriter outputFile = new PrintWriter(fWriter);

        //Checks what the user input compared to the usernames and passwords stored in the database
        while(rs.next()){
            String userName = rs.getString("User_Name");
            String userPassword = rs.getString("Password");
            if(usernameTB.getText().equals(userName) && passwordTB.getText().equals(userPassword)){
                outputFile.println(rb.getString("log1") + usernameTB.getText() + " " + rb.getString("log2") + java.time.LocalDateTime.now());
                outputFile.close();
                userLoginType = usernameTB.getText();
                Parent root = FXMLLoader.load(getClass().getResource("/Controller/MainWindow.fxml"));
                Stage stage = (Stage) loginButton.getScene().getWindow();
                Scene scene = new Scene(root, 1300, 800);
                stage.setTitle(rb.getString("mainTitle"));
                stage.setScene(scene);
                stage.show();


            }else{
                loginErrorLabel.setText(rb.getString("loginError"));
                String use = usernameTB.getText();
                if(use.equals("")){
                    use = "Username_Blank";
                }
                outputFile.println(rb.getString("log1") + use + " " + rb.getString("log3") + java.time.LocalDateTime.now());
                outputFile.close();
                usernameTB.setText("");
                passwordTB.setText("");
            }
        }
    }

    /***
     * Exits the program
     * @param actionEvent closes the window on click of exit button.
     */
    public void clickExit(ActionEvent actionEvent) {

        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}
