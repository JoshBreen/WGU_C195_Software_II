package Model;

import Utilities.DBConnection;
import Utilities.DBQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Joshua Breen C195 Project
 * Customer class, stores the customer information and gets all info from the database for customers
 */

public class Customer {
    private String custName;
    private String custAddress;
    private String custPhoneNumber;
    private int custID;
    private String custPostalCode;
    private int divID;
    private String custStateProvince;
    private String custCountry;
    private static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();


    /***
     * Our customer class, information is pulled from the database
     * @param custID Customer_ID from the customer database
     * @param custName Customer_Name from the customer database
     * @param custAddress Address from the customer database
     * @param custPostalCode Postal_Code from the customer database
     * @param custPhoneNumber Phone from the customer database
     * @param divID Division_ID from the customer database
     * @param custStateProvince Divison from first_level_divisions database
     * @param custCountry Country from countries database
     */
    public Customer(int custID, String custName, String custAddress, String custPostalCode, String custPhoneNumber, int divID, String custStateProvince, String custCountry){
        this.custID = custID;
        this.custName = custName;
        this.custAddress = custAddress;
        this.custPostalCode = custPostalCode;
        this.custPhoneNumber = custPhoneNumber;
        this.divID = divID;
        this.custStateProvince = custStateProvince;
        this.custCountry = custCountry;


    }

    /***
     * Accesses the Database and pulls all the customer info.
     * @return null
     * @throws SQLException
     */
    public static ObservableList getAllCustomersFromDB() throws SQLException {
        allCustomers.removeAll(allCustomers);
        Connection conn = DBConnection.getConnection();
        String sqlStatement = "SELECT * FROM customers";
        DBQuery.setPreparedStatement(conn, sqlStatement);
        PreparedStatement cs = DBQuery.getPreparedStatement();
        cs.execute();
        ResultSet crs = cs.getResultSet();

        while(crs.next()){
            int custID = crs.getInt("Customer_ID");
            String custName = crs.getString("Customer_Name");
            String custAddress = crs.getString("Address");
            String custPostalCode = crs.getString("Postal_Code");
            String custPhoneNumber = crs.getString("Phone");
            int divID = crs.getInt("Division_ID");
            String custStateProvince = convertDivIDToFirstLevel(divID);
            String country = convertDivIDToCountry(divID);

            allCustomers.add(new Customer(custID, custName, custAddress, custPostalCode, custPhoneNumber, divID, custStateProvince, country));
        }

        return null;
    }

    /***
     * It takes the Division ID and uses the first_level_divisions database to determine the State/Province the ID represents and returns
     * that as custStateProvince to be inserted in the Customer
     * @param divID from the Customer entry in the database
     * @return null
     * @throws SQLException
     */
    public static String convertDivIDToFirstLevel(int divID) throws SQLException {
        String firstLevelSelect = "SELECT * FROM first_level_divisions WHERE Division_ID = ?";
        Connection conn = DBConnection.getConnection();
        DBQuery.setPreparedStatement(conn, firstLevelSelect);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.setInt(1,divID);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while(rs.next()) {
            String firstDiv = rs.getString("Division");
            return firstDiv;
        }
        return null;
    }

    /***
     * It takes the Division ID and uses the first_level_divisions database to determine the country code, it then uses that country code in the countries
     * database to determine which country to use for the Customer
     * @param divID
     * @return null
     * @throws SQLException
     */
    public static String convertDivIDToCountry(int divID) throws SQLException {
        String firstLevelSelect = "SELECT * FROM first_level_divisions WHERE Division_ID = ?";
        Connection conn = DBConnection.getConnection();
        DBQuery.setPreparedStatement(conn, firstLevelSelect);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.setInt(1,divID);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while(rs.next()) {
            int countryID = rs.getInt("COUNTRY_ID");
            String countrySelect = "SELECT * FROM countries WHERE Country_ID = ?";
            DBQuery.setPreparedStatement(conn, countrySelect);
            PreparedStatement cs = DBQuery.getPreparedStatement();
            cs.setInt(1,countryID);
            cs.execute();
            ResultSet srs = cs.getResultSet();
            while(srs.next()) {
                String country = srs.getString("Country");
                return country;
            }
        }

        return null;
    }

    /**
     * Getter for All Customers List
     * @return allCustomers
     */
    public static ObservableList<Customer> getAllCustomersList(){
        return allCustomers;
    }

    /**
     * Getter for Customer ID
     * @return custID
     */
    public int getCustID(){
        return custID;
    }

    /**
     * Getter for Customer Name
     * @return custName
     */
    public String getCustName(){
        return custName;
    }

    /**
     * Getter Customer Address
     * @return custAddress
     */
    public String getCustAddress(){
        return custAddress;
    }

    /**
     * Getter for Customer Postal Code
     * @return custPostalCode
     */
    public String getCustPostalCode(){
        return custPostalCode;
    }

    /**
     * Getter for Customer Phone Number
     * @return custPhoneNumber
     */
    public String getCustPhoneNumber(){
        return custPhoneNumber;
    }

    /**
     * Getter for Division ID
     * @return divID
     */
    public int getDivID(){
        return divID;
    }

    /**
     * Getter for Customer State/Province
     * @return custStateProvince
     */
    public String getCustStateProvince(){
        return custStateProvince;
    }

    /**
     * Getter for Customer Country
     * @return custCountry
     */
    public String getCustCountry(){
        return custCountry;
    }


}
