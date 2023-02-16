package Controller;

import Model.MainReports;
import Utilities.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Joshua Breen C195 Project
 * Main Java file for the Schedule App
 */
public class Main extends Application {

    ResourceBundle rb = ResourceBundle.getBundle("BreenScheduling", Locale.getDefault());





    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("LogIn.fxml"));
        primaryStage.setTitle(rb.getString("loginTitle"));
        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();
    }

    /**
     * Runs the main program and starts and ends the database connection
     * @param args
     */
    public static void main(String[] args) {

        DBConnection.startConnection();

        launch(args);
        DBConnection.closeConnection();
    }
}
