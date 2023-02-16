package Controller;

import Model.Customer;
import Utilities.DBConnection;
import Utilities.DBQuery;
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
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Joshua Breen C195 Project
 * Modifies the customer and updates the customer info in the database.
 */

public class ModifyCustomerWindowController implements Initializable {
    public Label modCustomerLbl;
    public Label custNameLBL;
    public TextField custNameTB;
    public Label addressLBL;
    public TextField addressTB;
    public Label postalCodeLBL;
    public TextField postalCodeTB;
    public Label phoneNumLBL;
    public TextField phoneNumTB;
    public Label countryLBL;
    public ComboBox countryCmbBox;
    public Label stateProvLBL;
    public ComboBox spComboBox;
    public Button saveModCustBtn;
    public Button exitModCstBtn;
    public Label modCustErrorLbl;
    public Label custIDLbl;
    public TextField custIDTB;
    ResourceBundle rb = ResourceBundle.getBundle("BreenScheduling", Locale.getDefault());
    private static ObservableList<String> allCountries = FXCollections.observableArrayList();
    private static ObservableList<String> stateProvinceList = FXCollections.observableArrayList();
    private static String selectedCountry;
    private static int selectedCountryInt;
    private static int divID;
    private static String selectedStateProvince;
    private static String userLoginType;
    private static Customer custToMod;
    private static int custIdToMod;

    /***
     * Runs code to setup the country combo box with the list of countries and brings over the user login type over from the Login
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        modCustomerLbl.setText(rb.getString("modCustBtn"));
        custNameLBL.setText(rb.getString("custName"));
        addressLBL.setText(rb.getString("address"));
        postalCodeLBL.setText(rb.getString("postalCode"));
        phoneNumLBL.setText(rb.getString("phoneNum"));
        countryLBL.setText(rb.getString("country"));
        stateProvLBL.setText(rb.getString("stateProv"));
        saveModCustBtn.setText(rb.getString("save"));
        exitModCstBtn.setText(rb.getString("exit"));
        custIDLbl.setText(rb.getString("custId"));

        try {
            getCustomerInfo();
            getCountries();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }


    /**
     * Populates the customer info into the fields and sets the initial divID. Then runs populateStateProvinces
     * to fill the state province combo list
     * @throws SQLException
     */
    public void getCustomerInfo() throws SQLException {
        userLoginType = LogInController.getUserLoginType();
        custToMod = MainWindowController.getCustomerToMod();
        custIdToMod = custToMod.getCustID();
        custIDTB.setText(String.valueOf(custIdToMod));
        divID = custToMod.getDivID();
        addressTB.setText(custToMod.getCustAddress());
        postalCodeTB.setText(custToMod.getCustPostalCode());
        phoneNumTB.setText(custToMod.getCustPhoneNumber());
        custNameTB.setText(custToMod.getCustName());
        spComboBox.setValue(custToMod.getCustStateProvince());
        countryCmbBox.setValue(custToMod.getCustCountry());
        selectedCountry = (String) countryCmbBox.getSelectionModel().getSelectedItem();
        populateStateProvinces();
    }


    /***
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

    /**
     * Based on the selected country it populates the state and province list combo box with the state and provinces in that
     * country.
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
        spComboBox.setPromptText("Select State/Province");

    }

    /**
     * When a country is selected in the combo box it runs populate state province so it has the appropriate states and provinces
     * for the country
     * @param actionEvent of selected country listed in the country combo box
     * @throws SQLException
     */
    public void selectCountryForSP(ActionEvent actionEvent) throws SQLException {
        selectedCountry = (String) countryCmbBox.getSelectionModel().getSelectedItem();
        populateStateProvinces();

    }

    /**
     * When a state or province is selected it gets the division ID from the database.
     * @param actionEvent selected a state or province from the combo box
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
     * Updates the modified customer in the database.
     * @param actionEvent of clicking save button
     * @throws SQLException
     * @throws IOException
     */
    public void saveModCustomer(ActionEvent actionEvent) throws SQLException, IOException {
        modCustErrorLbl.setText("");
        if(custNameTB.getText().length() != 0 && addressTB.getText().length() != 0 && postalCodeTB.getText().length()!= 0 && phoneNumTB.getText().length() !=0 &&
                spComboBox.getSelectionModel().getSelectedItem() != null && countryCmbBox.getSelectionModel().getSelectedItem() != null) {

            String custName = custNameTB.getText();
            String addressEntry = addressTB.getText();
            String postalCodeEntry = postalCodeTB.getText();
            String phoneNumEntry = phoneNumTB.getText();
            Timestamp now = Timestamp.from(Instant.now());
            Connection conn = DBConnection.getConnection();
            String sqlUpdateCustomer = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, " +
                    "Last_Update = ?, Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";
            DBQuery.setPreparedStatement(conn, sqlUpdateCustomer);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setString(1, custName);
            ps.setString(2, addressEntry);
            ps.setString(3, postalCodeEntry);
            ps.setString(4, phoneNumEntry);
            ps.setTimestamp(5, now);
            ps.setString(6, userLoginType);
            ps.setInt(7, divID);
            ps.setInt(8, custIdToMod);
            ps.execute();
            if (ps.getUpdateCount() > 0) {
                System.out.println(ps.getUpdateCount() + "rows affected");
            } else {
                System.out.println("No Change");
            }
            Parent root = FXMLLoader.load(getClass().getResource("/Controller/MainWindow.fxml"));
            Stage stage = (Stage) saveModCustBtn.getScene().getWindow();
            Scene scene = new Scene(root, 1300, 800);
            stage.setTitle(rb.getString("mainTitle"));
            stage.setScene(scene);
            stage.show();
        }else{
            modCustErrorLbl.setText("Please ensure all inputs are filled.");
        }

    }

    /**
     * Exits the mod customer window
     * @param actionEvent on clicking the exit button
     * @throws IOException
     */
    public void exitModCustomer(ActionEvent actionEvent) throws IOException {

            Parent root = FXMLLoader.load(getClass().getResource("/Controller/MainWindow.fxml"));
            Stage stage = (Stage) exitModCstBtn.getScene().getWindow();
            Scene scene = new Scene(root, 1300, 800);
            stage.setTitle(rb.getString("mainTitle"));
            stage.setScene(scene);
            stage.show();


    }
}
