package Controller;

import Model.Appointment;
import Model.Customer;
import Utilities.DBConnection;
import Utilities.DBQuery;
import Utilities.MessageDisplay;
import com.mysql.cj.x.protobuf.MysqlxPrepare;
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
 * Modify Appointment controller, is used to modify and update the appointments in the database. Message Display Lambda
 * is used to consolidate the error messages to one location for easier updating.
 */

public class ModAppointmentWindowController implements Initializable {
    public Label modApptTitleLbl;
    public TextField modApptTitleTB;
    public Label modApptDescLbl;
    public TextField modApptDescTB;
    public Label modApptLocLbl;
    public TextField modApptLocatTB;
    public Label modApptTypeLbl;
    public TextField modApptTypeTB;
    public Label modApptStartDateLbl;
    public ComboBox modAptStartTimeCmb;
    public DatePicker modApptStartDatePicker;
    public Label modApptStartTimeLbl;
    public Label modApptEndDateLbl;
    public ComboBox modApptEndTimeCmb;
    public DatePicker modApptEndDatePicker;
    public Label modApptEndTimeLbl;
    public ComboBox modApptCustCmb;
    public Label modApptCustomerLbl;
    public ComboBox modApptContactCmb;
    public Label modApptContactLbl;
    public Button modApptSaveBtn;
    public Button modApptExitBtn;
    public Label modApptErrorLbl;
    public Label modApptID;
    private static String userLoginType;
    private static Connection conn = DBConnection.getConnection();
    private static ResourceBundle rb = ResourceBundle.getBundle("BreenScheduling", Locale.getDefault());
    private static ObservableList<String> allContacts = FXCollections.observableArrayList();
    private static ObservableList<String> allCustomers = FXCollections.observableArrayList();
    private static LocalTime start = LocalTime.of(0,0);
    private static Appointment apptToMod;
    private static int apptIdToMod;
    public TextField modApptIDTB;
    public Label modApptHeaderLbl;



    /**
     * Runs code to setup the time combo boxes, also fills in the Contacts and Customers combo boxes and gets the appointment info
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        modApptHeaderLbl.setText(rb.getString("modApptBtn"));
        modApptTitleLbl.setText(rb.getString("titleCol"));
        modApptDescLbl.setText(rb.getString("desCol"));
        modApptLocLbl.setText(rb.getString("locCol"));
        modApptTypeLbl.setText(rb.getString("typeCol"));
        modApptStartDateLbl.setText(rb.getString("startDateCol"));
        modApptStartTimeLbl.setText(rb.getString("startTimeCol"));
        modApptEndDateLbl.setText(rb.getString("endDateCol"));
        modApptEndTimeLbl.setText(rb.getString("endTimeCol"));
        modApptCustomerLbl.setText(rb.getString("customerLbl"));
        modApptContactLbl.setText(rb.getString("conCol"));
        modApptSaveBtn.setText(rb.getString("save"));
        modApptExitBtn.setText(rb.getString("exit"));
        modApptID.setText(rb.getString("apptID"));

        for (int i = 0; i < 24; i++){
            modAptStartTimeCmb.getItems().add(start);
            start = start.plusHours(1);
        }

        for (int i = 0; i < 24; i++){
            modApptEndTimeCmb.getItems().add(start);
            start = start.plusHours(1);
        }

        try {
            getContacts();
            getCustomers();
            getApptInfo();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            }
        }

    /**
     * Runs SQL code to get contacts from the DB and populates the combo box with the contacts
     * @throws SQLException
     */
    public void getContacts() throws SQLException{
        allContacts.removeAll(allContacts);
            String contactSQLString = "SELECT * FROM contacts";
            DBQuery.setPreparedStatement(conn, contactSQLString);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.execute();
            ResultSet rs = ps.getResultSet();
            while (rs.next()){
                String contactName = rs.getString("Contact_Name");
                allContacts.add(contactName);
            }
            modApptContactCmb.setItems(allContacts);
        }


    /**
     * Runs SQL code to get customers from the DB and populates the combo box with the customers
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
            modApptCustCmb.setItems(allCustomers);

        }

    /**
     * If modAppointment returns as true and the appointment was successfully modded it will close the window
     * and go back to the main window.
     * @param actionEvent on click of the save button
     * @throws IOException
     * @throws SQLException
     */
    public void saveModAppointment(ActionEvent actionEvent) throws IOException, SQLException {
        if(modAppointment() == true){
            Parent root = FXMLLoader.load(getClass().getResource("/Controller/MainWindow.fxml"));
            Stage stage = (Stage) modApptSaveBtn.getScene().getWindow();
            Scene scene = new Scene(root, 1300, 800);
            stage.setTitle(rb.getString("mainTitle"));
            stage.setScene(scene);
            stage.show();
        }
    }

    /**
     * Exits the mod appointment window and goes back to the main window
     * @param actionEvent on click of the exit button
     * @throws IOException
     */
    public void exitModAppointment(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Controller/MainWindow.fxml"));
        Stage stage = (Stage) modApptExitBtn.getScene().getWindow();
        Scene scene = new Scene(root, 1300, 800);
        stage.setTitle(rb.getString("mainTitle"));
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Takes the appointment info selected from the table. It pulls out the Appointment ID and populates the input boxes
     * with the information from the database for that appointment. It then converts the timestamp into a date and time to
     * fill the date and time input boxes. It also takes the Contact ID and Customer ID to populate those combo boxes with the
     * selected individuals.
     * @throws SQLException
     */
    public void getApptInfo() throws SQLException {
        apptToMod = MainWindowController.getApptToMod();
        apptIdToMod = apptToMod.getApptID();
        modApptIDTB.setText(String.valueOf(apptIdToMod));
        userLoginType = LogInController.getUserLoginType();
        modApptTitleTB.setText(apptToMod.getApptTitle());
        modApptDescTB.setText(apptToMod.getApptDesc());
        modApptLocatTB.setText(apptToMod.getApptLocation());
        modApptTypeTB.setText(apptToMod.getApptType());
        modApptStartDatePicker.setValue(apptToMod.getStartDate());
        modAptStartTimeCmb.setValue(apptToMod.getStartTime());
        modApptEndDatePicker.setValue(apptToMod.getEndDate());
        modApptEndTimeCmb.setValue(apptToMod.getEndTime());
        modApptContactCmb.setValue(apptToMod.getApptContact());
        modApptCustCmb.setValue(apptToMod.getCustName());
        }

    /**
     * It first checks to make sure all inputs boxes have a selection made or input typed in. After that it saves all the inputs and converts the time and date
     * into a time stamp. It runs SQL to update the appointment in the database. Returns True if it is successful.
     * @return true if successful
     * @throws SQLException
     */
    public boolean modAppointment() throws SQLException {
        boolean b = false;
        if(areTextInputsValid() == true && areCustomerAndContactSelected() == true && areTimesSelected() == true && doesEndComeAfterStartTime() == true) {
            String customerName = (String) modApptCustCmb.getSelectionModel().getSelectedItem();
            int customerID = convertCustomerNameToID(customerName); //still need to write
            String apptTitle = modApptTitleTB.getText();
            String apptDesc = modApptDescTB.getText();
            String apptLoc = modApptLocatTB.getText();
            String apptType = modApptTypeTB.getText();
            LocalTime startTime = (LocalTime) modAptStartTimeCmb.getSelectionModel().getSelectedItem();
            LocalTime endTime = (LocalTime) modApptEndTimeCmb.getSelectionModel().getSelectedItem();
            LocalDate startDate = modApptStartDatePicker.getValue();
            LocalDate endDate = modApptEndDatePicker.getValue();
            LocalDateTime start = LocalDateTime.of(startDate,startTime);
            LocalDateTime end = LocalDateTime.of(endDate,endTime);
            Timestamp startForDB = Timestamp.valueOf(start);
            Timestamp endForDB = Timestamp.valueOf(end);
            String contactName = (String) modApptContactCmb.getSelectionModel().getSelectedItem();
            int contactID = convertContactNameToID(contactName);
            Timestamp now = Timestamp.from(Instant.now());
            int userID = convertUsernameToID();

            if(appointmentInBusinessHours(startDate, startTime, endDate, endTime) == true && customerOccupiedCheck(customerID, start, end) == true){
                String updateAppointmentSQLStatement = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, " +
                        "Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
                DBQuery.setPreparedStatement(conn,updateAppointmentSQLStatement);
                PreparedStatement ps = DBQuery.getPreparedStatement();
                ps.setString(1,apptTitle);
                ps.setString(2,apptDesc);
                ps.setString(3,apptLoc);
                ps.setString(4,apptType);
                ps.setTimestamp(5,startForDB);
                ps.setTimestamp(6,endForDB);
                ps.setTimestamp(7,now);
                ps.setInt(8,userID);
                ps.setInt(9,customerID);
                ps.setInt(10,userID);
                ps.setInt(11,contactID);
                ps.setInt(12,apptIdToMod);
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
     * Checks to make sure the customer doesn't have other appointments during the scheduled time
     * @param customerID customer ID
     * @param start start time for appointment
     * @param end endtime for appointment
     * @return
     * @throws SQLException
     */
    private boolean customerOccupiedCheck(int customerID, LocalDateTime start, LocalDateTime end) throws SQLException {
        boolean b = true;
        modApptErrorLbl.setText("");
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
                modApptErrorLbl.setText(rb.getString("cust_appt_error"));
                b = false;
            }
        }
        return b;
    }

    /**
     * Checks to make sure the schedule appointment falls within business hours
     * @param startDate start date of the appointment
     * @param startTime start time of the appointment
     * @param endDate end date of the appointment
     * @param endTime end time of the appointment
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
            modApptErrorLbl.setText(rb.getString("bus_hours"));
        }

        return b;

    }

    /**
     * Checks to make sure all the text boxes have input
     * @return true if all text boxes have input
     */
    private boolean areTextInputsValid(){
        boolean b = false;
        if(modApptTitleTB.getText().length() != 0 && modApptDescTB.getText().length() != 0 && modApptLocatTB.getText().length() != 0
                && modApptTypeTB.getText().length() != 0) {
            b = true;
        } else {
            modApptErrorLbl.setText(rb.getString("input_error"));
        }
        return b;
    }

    /**
     * Checks to make sure times have been selected from the combo boxes
     * @return true if times have been selected
     */
    private boolean areTimesSelected(){
        boolean b = false;
        if(modAptStartTimeCmb.getSelectionModel().getSelectedItem() != null && modApptEndTimeCmb.getSelectionModel().getSelectedItem() != null){
            b = true;
        }else{
            modApptErrorLbl.setText(rb.getString("input_error"));
        }
        return b;
    }

    /**
     * Checks to make sure a customer and contact have been selected from the combo boxes
     * @return true if selections have been made
     */
    private boolean areCustomerAndContactSelected(){
        boolean b = false;
        if(modApptCustCmb.getSelectionModel().getSelectedItem() != null && modApptContactCmb.getSelectionModel().getSelectedItem() != null){
            b = true;
        }else{
            modApptErrorLbl.setText(rb.getString("input_error"));
        }
        return b;
    }

    /**
     * Converts username to User ID
     * @return User ID
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


    /**
     * Converts contact name to contact ID
     * @param contactName selected from the combo box
     * @return contact ID for the contact name that was input
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
     * Converts customer name to customer ID
     * @param customerName selected from the combo box
     * @return customer ID for the selected customer name
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

    /**
     * Checks that the end time comes after the start time
     * @return true if start comes before the end
     */
    private boolean doesEndComeAfterStartTime(){
        boolean b = false;
        LocalTime startTime = (LocalTime) modAptStartTimeCmb.getSelectionModel().getSelectedItem();
        LocalTime endTime = (LocalTime) modApptEndTimeCmb.getSelectionModel().getSelectedItem();
        LocalDate startDate = modApptStartDatePicker.getValue();
        LocalDate endDate = modApptEndDatePicker.getValue();
        LocalDateTime start = LocalDateTime.of(startDate,startTime);
        LocalDateTime end = LocalDateTime.of(endDate,endTime);
        if(end.isAfter(start)){
            b = true;
        }else{
            modApptErrorLbl.setText(rb.getString("sched_error"));
        }
        return b;
    }



}