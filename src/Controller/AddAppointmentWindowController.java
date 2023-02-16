package Controller;

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
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Joshua Breen C195 Project
 * AddAppointmentWindowController this window is used to add appointments to the database and the program. Sets allContacts
 * and allCustomers lists to be used for the combo boxes. MessageDisplay lambda is used to combine all error messages to
 * the label used up here to make it easier if there are future changes, instead of changing it at each use. Start time is used
 * for the combo boxes so they can start at 0:00.
 */

public class AddAppointmentWindowController implements Initializable {
    public Label addApptTitleLbl;
    public TextField addApptTitleTB;
    public Label addApptDescLbl;
    public TextField addApptDescTB;
    public Label addApptLocLbl;
    public TextField addApptLocatTB;
    public Label addApptTypeLbl;
    public TextField addApptTypeTB;
    public Label addApptStartDateLbl;
    public ComboBox addAptStartTimeCmb;
    public DatePicker addApptStartDatePicker;
    public Label addApptStartTimeLbl;
    public Label addApptEndDateLbl;
    public ComboBox addApptEndTimeCmb;
    public DatePicker addApptEndDatePicker;
    public Label addApptEndTimeLbl;
    public ComboBox addApptCustCmb;
    public Label addApptCustomerLbl;
    public ComboBox addApptContactCmb;
    public Label addApptContactLbl;
    public Button addApptSaveBtn;
    public Button addApptExitBtn;
    private static String userLoginType;
    public Label addApptErrorLbl;

    private static Connection conn = DBConnection.getConnection();
    private static ResourceBundle rb = ResourceBundle.getBundle("BreenScheduling", Locale.getDefault());
    private static ObservableList<String> allContacts = FXCollections.observableArrayList();
    private static ObservableList<String> allCustomers = FXCollections.observableArrayList();
    private static LocalTime start = LocalTime.of(0,0);
    public Label addAppHeaderLbl;
    MessageDisplay message = s -> addApptErrorLbl.setText(s);



    /***
     * Runs code to setup the time combo boxes with the list of applicable times, gets userLoginType, populates contacts
     * and customers
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        addAppHeaderLbl.setText(rb.getString("addApptBtn"));
        addApptTitleLbl.setText(rb.getString("titleCol"));
        addApptDescLbl.setText(rb.getString("desCol"));
        addApptLocLbl.setText(rb.getString("locCol"));
        addApptTypeLbl.setText(rb.getString("typeCol"));
        addApptStartDateLbl.setText(rb.getString("startDateCol"));
        addApptStartTimeLbl.setText(rb.getString("startTimeCol"));
        addApptEndDateLbl.setText(rb.getString("endDateCol"));
        addApptEndTimeLbl.setText(rb.getString("endTimeCol"));
        addApptCustomerLbl.setText(rb.getString("customerLbl"));
        addApptContactLbl.setText(rb.getString("conCol"));
        addApptSaveBtn.setText(rb.getString("save"));
        addApptExitBtn.setText(rb.getString("exit"));
        

        for (int i = 0; i < 24; i++){
            addAptStartTimeCmb.getItems().add(start);
            start = start.plusHours(1);

        }

        for (int i = 0; i < 24; i++){
            addApptEndTimeCmb.getItems().add(start);
            start = start.plusHours(1);

        }


        userLoginType = LogInController.getUserLoginType();
        try {
            getContacts();
            getCustomers();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    /**
     * Gets all contacts from the database to populate the Contact combo box
     * @throws SQLException
     */
    public void getContacts() throws SQLException {
        allContacts.removeAll(allContacts);
        String contactSQLString = "SELECT * FROM contacts";
        DBQuery.setPreparedStatement(conn, contactSQLString);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while(rs.next()){
            String contactName = rs.getString("Contact_Name");
            allContacts.add(contactName);
        }
        addApptContactCmb.setItems(allContacts);
    }

    /**
     * Gets Customers from the DB to populate the allCustomers ObservableList and puts that in the addApptCustCmb.
     * @throws SQLException
     */
    public void getCustomers() throws SQLException{
        allCustomers.removeAll(allCustomers);
        String customerSQLString = "SELECT * FROM customers";
        DBQuery.setPreparedStatement(conn, customerSQLString);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while(rs.next()){
            String customerName = rs.getString("Customer_Name");
            allCustomers.add(customerName);
        }
        addApptCustCmb.setItems(allCustomers);

    }


    /**
     * Saves new appointment to the Database
     * @param actionEvent on click of Save Button
     * @throws SQLException
     * @throws IOException
     */
    public void saveNewAppointment(ActionEvent actionEvent) throws SQLException, IOException {
        addApptErrorLbl.setText("");
        if(addAppointment() == true){
        Parent root = FXMLLoader.load(getClass().getResource("/Controller/MainWindow.fxml"));
        Stage stage = (Stage) addApptSaveBtn.getScene().getWindow();
        Scene scene = new Scene(root, 1300, 800);
        stage.setTitle(rb.getString("mainTitle"));
        stage.setScene(scene);
        stage.show();
        }
    }

    /**
     * Exits Add Appointment window back to main window
     * @param actionEvent on clicking Cancel Button
     * @throws IOException
     */
    public void exitAddAppointment(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Controller/MainWindow.fxml"));
        Stage stage = (Stage) addApptExitBtn.getScene().getWindow();
        Scene scene = new Scene(root, 1300, 800);
        stage.setTitle(rb.getString("mainTitle"));
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Takes the appointment information from the input fields, checks to make sure the hours are within business hours and if so adds it to the database
     * @return true if it adds it to the database
     * @throws SQLException
     */
    public boolean addAppointment() throws SQLException {
        boolean b = false;
        if(areTextInputsValid() == true && areCustomerAndContactSelected() == true && areTimesSelected() == true && doesEndComeAfterStartTime() == true) {
            String customerName = (String) addApptCustCmb.getSelectionModel().getSelectedItem();
            int customerID = convertCustomerNameToID(customerName);
            String apptTitle = addApptTitleTB.getText();
            String apptDesc = addApptDescTB.getText();
            String apptLoc = addApptLocatTB.getText();
            String apptType = addApptTypeTB.getText();
            LocalTime startTime = (LocalTime) addAptStartTimeCmb.getSelectionModel().getSelectedItem();
            LocalTime endTime = (LocalTime) addApptEndTimeCmb.getSelectionModel().getSelectedItem();
            LocalDate startDate = addApptStartDatePicker.getValue();
            LocalDate endDate = addApptEndDatePicker.getValue();
            LocalDateTime start = LocalDateTime.of(startDate,startTime);
            LocalDateTime end = LocalDateTime.of(endDate,endTime);
            Timestamp startForDB = Timestamp.valueOf(start);
            Timestamp endForDB = Timestamp.valueOf(end);


            if(appointmentInBusinessHours(startDate, startTime, endDate, endTime) == true && customerOccupiedCheck(customerID, start, end) == true){
                String contactName = (String) addApptContactCmb.getSelectionModel().getSelectedItem();
                int contactID = convertContactNameToID(contactName);
                Timestamp now = Timestamp.from(Instant.now());
                int userID = convertUsernameToID();
                String addAppointmentSQLStatement = "INSERT INTO appointments(Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
                DBQuery.setPreparedStatement(conn,addAppointmentSQLStatement);
                PreparedStatement ps = DBQuery.getPreparedStatement();
                ps.setString(1,apptTitle);
                ps.setString(2,apptDesc);
                ps.setString(3,apptLoc);
                ps.setString(4,apptType);
                ps.setTimestamp(5,startForDB);
                ps.setTimestamp(6,endForDB);
                ps.setTimestamp(7,now);
                ps.setString(8,userLoginType);
                ps.setTimestamp(9,now);
                ps.setString(10,userLoginType);
                ps.setInt(11,customerID);
                ps.setInt(12,userID);
                ps.setInt(13,contactID);
                ps.execute();
                if (ps.getUpdateCount() > 0){
                    System.out.println(ps.getUpdateCount() + rb.getString("row_affected"));
                    b = true;
                }else{
                    System.out.println(rb.getString("no_change"));
                }
            }
        }
        return b;
    }

    /**
     * Checks to make sure that the appointment falls within business hours
     * @param startDate startDate from appointment
     * @param startTime startTime from appointment
     * @param endDate endDate from appointment
     * @param endTime endTime from appointment
     * @return
     */
    private boolean appointmentInBusinessHours(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime){
        boolean b = false;
        ZoneId localZoneId = ZoneId.systemDefault();
        ZonedDateTime startLocalZDT = ZonedDateTime.of(startDate, startTime, localZoneId);
        ZonedDateTime endLocalZDT = ZonedDateTime.of(endDate, endTime, localZoneId);
        ZoneId officeZDT = ZoneId.of("America/New_York");
        ZonedDateTime startLocalToOffice = startLocalZDT.withZoneSameInstant(officeZDT);
        ZonedDateTime endLocalToOffice = endLocalZDT.withZoneSameInstant(officeZDT);
        LocalDateTime startOffice = startLocalToOffice.toLocalDateTime();
        LocalDateTime endOffice = endLocalToOffice.toLocalDateTime();
        LocalTime startForChecking = startOffice.toLocalTime();
        LocalTime endForChecking = endOffice.toLocalTime();
        LocalTime businessHoursOpen = MainWindowController.getBusinessOpenHours();
        LocalTime businessHoursClose = MainWindowController.getBusinessCloseHours();
        if(startForChecking.isAfter(businessHoursOpen.minusMinutes(1)) && startForChecking.isBefore(businessHoursClose) &&
                endForChecking.isAfter(businessHoursOpen.minusMinutes(1)) && endForChecking.isBefore(businessHoursClose)){
            b = true;

        } else {
            message.messageDisplay(rb.getString("bus_hours"));
        }

        return b;

    }

    /***
     * Checks to make sure that the text box inputs have inputs
     * @return true if text inputs are valid
     */
    private boolean areTextInputsValid(){
        boolean b = false;
        if(addApptTitleTB.getText().length() != 0 && addApptDescTB.getText().length() != 0 && addApptLocatTB.getText().length() != 0
                && addApptTypeTB.getText().length() != 0) {
            b = true;
        } else {
            message.messageDisplay(rb.getString("input_error"));
        }
        return b;
    }

    /***
     * checks to make sure Customer and Contact
     * @return true if combo boxes have a selection made
     */
    private boolean areCustomerAndContactSelected(){
        boolean b = false;
        if(addApptCustCmb.getSelectionModel().getSelectedItem() != null && addApptContactCmb.getSelectionModel().getSelectedItem() != null){
            b = true;
        }else{
            message.messageDisplay(rb.getString("input_error"));
        }
        return b;
    }


    /**
     * Checks to make sure the customer doesn't have other appointments during the scheduled time
     * @param customerID customer ID
     * @param start start time for appointment
     * @param end endtime for appointment
     * @return
     * @throws SQLException
     */
    private boolean customerOccupiedCheck(int customerID, LocalDateTime start, LocalDateTime end) throws SQLException {
        boolean b = true;
        addApptErrorLbl.setText("");
        int cID = customerID;
        Timestamp startTime = Timestamp.valueOf(start);
        Timestamp endTime = Timestamp.valueOf(end);
        String apptCheck = "SELECT * FROM appointments WHERE Customer_ID = ?";
        DBQuery.setPreparedStatement(conn, apptCheck);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.setInt(1,cID);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while(rs.next()){
            Timestamp dbStart = rs.getTimestamp("Start");
            Timestamp dbEnd = rs.getTimestamp("End");
            if((((startTime.equals(dbStart) || startTime.after(dbStart)) && startTime.before(dbEnd)) || ((endTime.equals(dbStart) || endTime.after(dbStart)) && endTime.before(dbEnd)))   || ((dbStart.after(startTime) && dbStart.before(endTime)) || (dbEnd.after(startTime) && dbEnd.before(endTime)) ) ){
                message.messageDisplay(rb.getString("cust_appt_error"));
                b = false;
            }
        }
        return b;
    }

    /***
     * Checks to make sure times are selected
     * @return true if times are selected
     */
    private boolean areTimesSelected(){
        boolean b = false;
        if(addAptStartTimeCmb.getSelectionModel().getSelectedItem() != null && addApptEndTimeCmb.getSelectionModel().getSelectedItem() != null){
            b = true;
        }else{
            message.messageDisplay(rb.getString("input_error"));
        }
        return b;
    }


    /***
     * Checks to make sure the end time comes after the start time
     * @return true if the start comes before the end
     */
    private boolean doesEndComeAfterStartTime(){
        boolean b = false;
        LocalTime startTime = (LocalTime) addAptStartTimeCmb.getSelectionModel().getSelectedItem();
        LocalTime endTime = (LocalTime) addApptEndTimeCmb.getSelectionModel().getSelectedItem();
        LocalDate startDate = addApptStartDatePicker.getValue();
        LocalDate endDate = addApptEndDatePicker.getValue();
        LocalDateTime start = LocalDateTime.of(startDate,startTime);
        LocalDateTime end = LocalDateTime.of(endDate,endTime);
        if(end.isAfter(start)){
            b = true;
        }else{
            message.messageDisplay(rb.getString("sched_error"));
        }
        return b;
    }


    /***
     * Converts Customer name into Customer ID and returns ID
     * @param customerName receives customer name
     * @return customer ID
     * @throws SQLException
     */
    private int convertCustomerNameToID(String customerName) throws SQLException {
        String getCustomerIDString = "SELECT * FROM customers WHERE Customer_Name = ?";
        int custID = 0;
        DBQuery.setPreparedStatement(conn, getCustomerIDString);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.setString(1, customerName);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while(rs.next()) {
            custID = rs.getInt("Customer_ID");
        }
        return custID;
    }

    /***
     * Gets the contact name to convert to contact ID
     * @param contactName receives contact name
     * @return contact ID
     * @throws SQLException
     */
    private int convertContactNameToID(String contactName) throws SQLException {
        String getContactIDString = "SELECT * FROM contacts WHERE Contact_Name = ?";
        int contactID = 0;
        DBQuery.setPreparedStatement(conn, getContactIDString);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.setString(1, contactName);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while(rs.next()) {
            contactID = rs.getInt("Contact_ID");
        }
        return contactID;

    }

    /**
     * Converts username to User ID
     * @return userID
     * @throws SQLException
     */
    private int convertUsernameToID() throws SQLException{
        String getUserIDString = "SELECT * FROM users WHERE User_Name = ?";
        int userID = 0;
        DBQuery.setPreparedStatement(conn, getUserIDString);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.setString(1,userLoginType);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while(rs.next()){
            userID = rs.getInt("User_ID");
        }
        return userID;
    }



}
