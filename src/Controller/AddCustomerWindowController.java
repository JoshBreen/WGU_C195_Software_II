package Controller;

import Model.Customer;
import Utilities.DBConnection;
import Utilities.DBQuery;
import Utilities.MessageDisplay;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Joshua Breen C195 Project
 * AddCustomerWindowController Used to add customers to the database. MessageDisplay lambda is used to consolidate
 * the error messages to go to one place in case future updates require changing the error label. So you don't have to change
 * it at all the other locations.
 */

public class AddCustomerWindowController implements Initializable {
    public Label addCustomerLbl;
    public Label custNameLBL;
    public TextField custNameTB;
    public Label addressLBL;
    public TextField addressTB;
    public Label postalCodeLBL;
    public Button exitAddCstBtn;
    public TextField postalCodeTB;
    public Label phoneNumLBL;
    public TextField phoneNumTB;
    public Label countryLBL;
    public Label stateProvLBL;
    public ComboBox spComboBox;
    public Button saveAddCustBtn;
    public ComboBox countryCmbBox;
    public Label addCustErrorLbl;
    ResourceBundle rb = ResourceBundle.getBundle("BreenScheduling", Locale.getDefault());
    private static ObservableList<String> allCountries = FXCollections.observableArrayList();
    private static ObservableList<String> stateProvinceList = FXCollections.observableArrayList();
    private static String selectedCountry;
    private static int selectedCountryInt;
    private static int divID;
    private static String selectedStateProvince;
    private static String userLoginType;
    MessageDisplay message = s -> addCustErrorLbl.setText(s);

    /***
     * Runs code to setup the country combo box with the list of countries and brings over the user login type over from the Login
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addCustomerLbl.setText(rb.getString("addCustBtn"));
        custNameLBL.setText(rb.getString("custName"));
        addressLBL.setText(rb.getString("address"));
        postalCodeLBL.setText(rb.getString("postalCode"));
        exitAddCstBtn.setText(rb.getString("exit"));
        phoneNumLBL.setText(rb.getString("phoneNum"));
        countryLBL.setText(rb.getString("country"));
        stateProvLBL.setText(rb.getString("stateProv"));
        saveAddCustBtn.setText(rb.getString("save"));


        try {
            getCountries();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        userLoginType = LogInController.getUserLoginType();
    }

    /**
     * Accesses the Database to fill up the allCountries list and the combo box
     * @throws SQLException
     */
    public void getCountries() throws SQLException {
        Connection conn = DBConnection.getConnection();
        String sqlStatement = "SELECT * FROM countries";
        DBQuery.setPreparedStatement(conn, sqlStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while(rs.next()){
            String country = rs.getString("Country");
            allCountries.add(country);
        }
        countryCmbBox.setItems(allCountries);
    }


    /***
     * Sets selected Country and runs code to populate State and provinces.
     * @param actionEvent selecting country from combo box
     * @throws SQLException
     */
    public void selectCountryForSP(ActionEvent actionEvent) throws SQLException {
        selectedCountry = (String) countryCmbBox.getSelectionModel().getSelectedItem();
        populateStateProvinces();
    }

    /***
     * Clears the stateProvinceList to stop it from duplicating items if you change country. Runs SQL to pull all first_level_divisions
     * based on the country selected and populates the combo box. It clears the list when you click a different country to avoid doubling up the list or
     * including wrong state/provinces.
     * @throws SQLException
     */
    public void populateStateProvinces() throws SQLException {
        stateProvinceList.removeAll(stateProvinceList);
        Connection conn = DBConnection.getConnection();
        String sqlCountryStatement = "SELECT * FROM countries WHERE Country = ?";
        DBQuery.setPreparedStatement(conn, sqlCountryStatement);
        PreparedStatement cps = DBQuery.getPreparedStatement();
        cps.setString(1,selectedCountry);
        cps.execute();
        ResultSet crs = cps.getResultSet();
        while(crs.next()){
            selectedCountryInt = crs.getInt("Country_ID");
        }
        String sqlStateProvinceStatement = "SELECT * FROM first_level_divisions WHERE COUNTRY_ID = ?";
        DBQuery.setPreparedStatement(conn, sqlStateProvinceStatement);
        PreparedStatement sPps = DBQuery.getPreparedStatement();
        sPps.setInt(1,selectedCountryInt);
        sPps.execute();
        ResultSet sPrs = sPps.getResultSet();
        while(sPrs.next()){
            String stateProvinceFromDB = sPrs.getString("Division");
            stateProvinceList.add(stateProvinceFromDB);
        }
        spComboBox.setItems(stateProvinceList);
        spComboBox.setDisable(false);
        spComboBox.setPromptText(rb.getString("stateProv"));
    }

    /**
     * Pulls the Division ID from the database based on the inputted data. Searches the DB based on which state/province is selected
     * for the Division ID
     * @param actionEvent click on state/province in combo box
     * @throws SQLException
     */
    public void getDivIDFromDB(ActionEvent actionEvent) throws SQLException {
        selectedStateProvince = (String) spComboBox.getSelectionModel().getSelectedItem();
        Connection conn = DBConnection.getConnection();
        String sqlDivStatement = "SELECT * FROM first_level_divisions WHERE Division = ?";
        DBQuery.setPreparedStatement(conn, sqlDivStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.setString(1,selectedStateProvince);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            divID = rs.getInt("Division_ID");
        }

    }

    /**
     * Pulls the information from the Text Boxes, and uses the combo box selection to determine the Division ID to populate the Datebase with a new customer
     * @param actionEvent click Save Button
     * @throws SQLException
     * @throws IOException
     */
    public void saveAddCustomer(ActionEvent actionEvent) throws SQLException, IOException {
        addCustErrorLbl.setText("");
        if(custNameTB.getText().length() != 0 && addressTB.getText().length() != 0 && postalCodeTB.getText().length()!= 0 && phoneNumTB.getText().length() !=0 &&
                spComboBox.getSelectionModel().getSelectedItem() != null && countryCmbBox.getSelectionModel().getSelectedItem() != null) {
            String custName = custNameTB.getText();
            String addressEntry = addressTB.getText();
            String postalCodeEntry = postalCodeTB.getText();
            String phoneNumEntry = phoneNumTB.getText();
            Timestamp now = Timestamp.from(Instant.now());
            Connection conn = DBConnection.getConnection();
            String sqlAddCustomer = "INSERT INTO customers(Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) " +
                    "VALUES(?,?,?,?,?,?,?,?,?)";
            DBQuery.setPreparedStatement(conn, sqlAddCustomer);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setString(1, custName);
            ps.setString(2, addressEntry);
            ps.setString(3, postalCodeEntry);
            ps.setString(4, phoneNumEntry);
            ps.setTimestamp(5, now);
            ps.setString(6, userLoginType);
            ps.setTimestamp(7, now);
            ps.setString(8, userLoginType);
            ps.setInt(9, divID);
            ps.execute();
            if (ps.getUpdateCount() > 0) {
                System.out.println(ps.getUpdateCount() + rb.getString("row_affected"));
            } else {
                System.out.println(rb.getString("no_change"));
            }
            Parent root = FXMLLoader.load(getClass().getResource("/Controller/MainWindow.fxml"));
            Stage stage = (Stage) saveAddCustBtn.getScene().getWindow();
            Scene scene = new Scene(root, 1300, 800);
            stage.setTitle(rb.getString("mainTitle"));
            stage.setScene(scene);
            stage.show();
        } else {
            message.messageDisplay(rb.getString("input_error"));
        }

    }

    /**
     * Exits back to the main window
     * @param actionEvent click the exit button
     * @throws IOException
     */
    public void exitAddCustomer(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Controller/MainWindow.fxml"));
        Stage stage = (Stage) exitAddCstBtn.getScene().getWindow();
        Scene scene = new Scene(root, 1300, 800);
        stage.setTitle(rb.getString("mainTitle"));
        stage.setScene(scene);
        stage.show();
    }
}
