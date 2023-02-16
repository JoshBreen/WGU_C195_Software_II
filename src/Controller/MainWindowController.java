package Controller;


import Model.Appointment;
import Model.Customer;
import Model.MainReports;
import Utilities.DBQuery;
import Utilities.DBConnection;
import Utilities.MessageDisplay;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Joshua Breen C195 Project
 * Main window controller, displays the appointment schedule table, customer table. Has the buttons for Adding, Modifying, and Deleting
 * customers and appointments. Also displays the reports at the bottom and an alert at the top about upcoming appointments. Lambda Expression is run to help
 * consolidate the messages to all point to the one label in case things need to be changed in the future. Only one destination needs to be modified.
 */

public class MainWindowController implements Initializable {

    public TableView scheduleTable;
    public TableColumn apptIDCol;
    public TableColumn apptTitleCol;
    public TableColumn descriptionCol;
    public TableColumn apptLocCol;
    public TableColumn apptConCol;
    public TableColumn apptTypeCol;
    public TableColumn startDateCol;
    public TableColumn startTimeCol;
    public TableColumn endDateCol;
    public TableColumn endTimeCol;
    public RadioButton monthRadio;
    public ToggleGroup monthWeekGroup;
    public RadioButton weekRadio;
    public TableView reportTable;
    public Button addCustomerButton;
    public Button modCustomerButton;
    public Button deleteCustomerButton;
    public Button addApptButton;
    public Button modApptButton;
    public Button deleteApptButton;
    public RadioButton customerApptRadio;
    public ToggleGroup reportGroup;
    public RadioButton contactScheduleRadio;
    public Button exitButton;
    public Label mainTitleLbl;
    public Label upcomeApptLbl;
    public Label sortLbl;
    public Label customerLbl;
    public Label apptLbl;
    public Label rptLbl;
    public TableColumn customerIDApptCol;
    public TableColumn customerNameAppt;
    public TableColumn nameCustCol;
    public TableColumn addressCustCol;
    public TableColumn phoneCustCol;
    public TableColumn cIdCustCol;
    public TableColumn spCustCol;
    public TableColumn countCustCol;
    public TableView custTable;
    public TableColumn postalCodeCust;
    public TableColumn userNameAppt;
    private static String userLoginType;
    public Tab apptTabLbl;
    public Tab custTabLbl;
    private static Customer customerToMod;
    private static Connection conn = DBConnection.getConnection();
    private static Appointment apptToMod;
    private static ResourceBundle rb = ResourceBundle.getBundle("BreenScheduling", Locale.getDefault());
    private static LocalTime businessHoursClose = LocalTime.of(22,0);
    private static LocalTime businessHoursOpen = LocalTime.of(8,0);
    private static LocalDateTime now = LocalDateTime.now();
    private static ObservableList<MainReports> appointmentTypeAndMonth = FXCollections.observableArrayList();
    private static ObservableList<Appointment> apptCheck = FXCollections.observableArrayList();
    public TableColumn rptCol1;
    public TableColumn rptCol2;
    public TableColumn rptCol3;
    public TableColumn rptCol4;
    public TableColumn rptCol5;
    public TableColumn rptCol6;
    public TableColumn rptCol7;
    public TableColumn rptCol8;
    public TableColumn rptCol9;
    public TableColumn rptCol10;
    public RadioButton pastReportRadio;
    MessageDisplay message = s -> upcomeApptLbl.setText(s);

    /**
     *userLoginType gets the user info from the login screen. Tables, buttons, and labels are setup with their text in english or french based on selected language.
     * Upcomingappointmentcheck is run to fill in the message to alert the user if there is an upcoming appointment. Runs the typeReportPopulate to fill out the first report
     * that is displayed. Runs getAllCustomers so that it can populate the customer table with all the customers. Also runs monthViewPopulate and fillApptMonthTypeReport to finish
     * filling out the intial displayed information.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        userLoginType = LogInController.getUserLoginType();
        apptTabLbl.setText(rb.getString("apptLbl"));
        custTabLbl.setText(rb.getString("customerLbl"));
        customerNameAppt.setText(rb.getString("custName"));
        nameCustCol.setText(rb.getString("custName"));
        customerIDApptCol.setText(rb.getString("custId"));
        cIdCustCol.setText(rb.getString("custId"));
        userNameAppt.setText(rb.getString("user"));
        addressCustCol.setText(rb.getString("address"));
        postalCodeCust.setText(rb.getString("postalCode"));
        spCustCol.setText(rb.getString("stateProv"));
        phoneCustCol.setText(rb.getString("phoneNum"));
        countCustCol.setText(rb.getString("country"));
        mainTitleLbl.setText(rb.getString("mainTitle"));
        upcomeApptLbl.setText(rb.getString("upcomeAppt"));
        sortLbl.setText(rb.getString("sortLbl"));
        monthRadio.setText(rb.getString("month"));
        weekRadio.setText(rb.getString("week"));
        customerLbl.setText(rb.getString("customerLbl"));
        addCustomerButton.setText(rb.getString("addCustBtn"));
        modCustomerButton.setText(rb.getString("modCustBtn"));
        deleteCustomerButton.setText(rb.getString("delCustBtn"));
        apptLbl.setText(rb.getString("apptLbl"));
        addApptButton.setText(rb.getString("addApptBtn"));
        modApptButton.setText(rb.getString("modApptBtn"));
        deleteApptButton.setText(rb.getString("delApptBtn"));
        rptLbl.setText(rb.getString("rptLbl"));
        customerApptRadio.setText(rb.getString("custAppt"));
        contactScheduleRadio.setText(rb.getString("conSched"));
        apptConCol.setText(rb.getString("conCol"));
        apptIDCol.setText(rb.getString("apptidCol"));
        apptTitleCol.setText(rb.getString("titleCol"));
        descriptionCol.setText(rb.getString("desCol"));
        apptLocCol.setText(rb.getString("locCol"));
        apptTypeCol.setText(rb.getString("typeCol"));
        startDateCol.setText(rb.getString("startDateCol"));
        startTimeCol.setText(rb.getString("startTimeCol"));
        endDateCol.setText(rb.getString("endDateCol"));
        endTimeCol.setText(rb.getString("endTimeCol"));


        try {
            upcomingAppointmentCheck();
            typeReportPopulate();
            Customer.getAllCustomersFromDB();
            cIdCustCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("custID"));
            nameCustCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("custName"));
            addressCustCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("custAddress"));
            postalCodeCust.setCellValueFactory(new PropertyValueFactory<Customer, String>("custPostalCode"));
            phoneCustCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("custPhoneNumber"));
            spCustCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("custStateProvince"));
            countCustCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("custCountry"));
            custTable.setItems(Customer.getAllCustomersList());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        monthViewPopulate();
        try {
            fillApptMonthTypeReport();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }

    /**
     * Gets the opening business hour
     * @return businessHoursOpen
     */
    public static LocalTime getBusinessOpenHours(){
        return businessHoursOpen;
    }

    /**
     * Gets the closing business hour
     * @return businessHoursClose
     */
    public static LocalTime getBusinessCloseHours(){
        return businessHoursClose;
    }


    /**
     * Checks if there are any appointments in the next 15 minutes.
     * @throws SQLException
     */
    public void upcomingAppointmentCheck() throws SQLException {
        apptCheck.removeAll(apptCheck);
        Appointment.getUpcomingAppointmentsFromDB();
        apptCheck = Appointment.getUpcomingAppointments();
        if (apptCheck.size() > 0) {
            for(int i = 0; i < apptCheck.size(); i++){
                Appointment s = apptCheck.get(i);
                if(s.getStartDateTime().after(Timestamp.valueOf(now.plusSeconds(1))) && (s.getStartDateTime().before(Timestamp.valueOf(now.plusMinutes(15)))))
                {
                    message.messageDisplay(rb.getString("upcoming_appt") + rb.getString("appt_alert1") + s.getApptID() + rb.getString("appt_alert2") + s.getStartDate() + rb.getString("appt_alert3") + s.getStartTime());
                    break;
                }else{
                    message.messageDisplay(rb.getString("no_appt"));
                }
            }
        }else{
            message.messageDisplay(rb.getString("no_appt"));
        }
    }


    /**
     * Used to pass the selected customer to the modify customer window.
     * @return customer to modify in the modify window
     */
    public static Customer getCustomerToMod() {
        return customerToMod;
    }

    /**
     * Opens the modify customer window and saves the customer as customerToMod to be used in the modify customer
     * window.
     * @param actionEvent on clicking Modify Customer
     * @throws IOException
     */
    public void openModCustomer(ActionEvent actionEvent) throws IOException {
        if (custTable.getSelectionModel().getSelectedIndex() != -1) {
            customerToMod = (Customer) custTable.getSelectionModel().getSelectedItem();
            Parent root = FXMLLoader.load(getClass().getResource("/Controller/ModifyCustomerWindow.fxml"));
            Stage stage = (Stage) modCustomerButton.getScene().getWindow();
            Scene scene = new Scene(root, 380, 400);
            stage.setTitle(rb.getString("modCustBtn"));
            stage.setScene(scene);
            stage.show();
        }
    }


    /**
     * Opens the add customer window
     * @param actionEvent clicking add customer button
     * @throws IOException
     */
    public void openAddCustomer(ActionEvent actionEvent) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/Controller/AddCustomerWindow.fxml"));
        Stage stage = (Stage) addCustomerButton.getScene().getWindow();
        Scene scene = new Scene(root, 380, 400);
        stage.setTitle(rb.getString("addCustBtn"));
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Deletes customer and any appointments connected to that customer.
     * @param actionEvent on clicking Delete Customer button
     * @throws SQLException
     */
    public void deleteCustomer(ActionEvent actionEvent) throws SQLException {
        if (custTable.getSelectionModel().getSelectedIndex() != -1) {
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(null, rb.getString("delete_confirm_prompt_c"), rb.getString("confirm_delete"), dialogButton);
            if (dialogResult == JOptionPane.YES_OPTION) {
                Customer custToDel = (Customer) custTable.getSelectionModel().getSelectedItem();
                int custIDToDel = custToDel.getCustID();
                String apptDeleteStatement = "DELETE FROM appointments WHERE Customer_ID = ?";
                DBQuery.setPreparedStatement(conn, apptDeleteStatement);
                PreparedStatement psa = DBQuery.getPreparedStatement();
                psa.setInt(1, custIDToDel);
                psa.execute();
                String custDeleteStatement = "DELETE FROM customers WHERE Customer_ID = ?";
                DBQuery.setPreparedStatement(conn, custDeleteStatement);
                PreparedStatement psc = DBQuery.getPreparedStatement();
                psc.setInt(1, custIDToDel);
                psc.execute();
                Customer.getAllCustomersFromDB();
                Appointment.getAllAppointmentsFromDB();
                JOptionPane.showMessageDialog(null, rb.getString("custDel"));
            }
        }
    }




    /**
     * Deletes the selected appointment
     * @param actionEvent on clicking Delete Appointment
     * @throws SQLException
     */
    public void deleteAppointment(ActionEvent actionEvent) throws SQLException{
        if (scheduleTable.getSelectionModel().getSelectedIndex() != -1) {
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(null, rb.getString("delete_confirm_prompt_a"), rb.getString("confirm_delete"), dialogButton);
            if (dialogResult == JOptionPane.YES_OPTION) {
                Appointment apptToDel = (Appointment) scheduleTable.getSelectionModel().getSelectedItem();
                int apptIDToDel = apptToDel.getApptID();
                String apptDeleteStatement = "DELETE FROM appointments WHERE Appointment_ID = ?";
                DBQuery.setPreparedStatement(conn, apptDeleteStatement);
                PreparedStatement ps = DBQuery.getPreparedStatement();
                ps.setInt(1, apptIDToDel);
                ps.execute();
                Customer.getAllCustomersFromDB();
                Appointment.getAllAppointmentsFromDB();
                JOptionPane.showMessageDialog(null, rb.getString("apptDel"));
                if(monthRadio.isSelected()){
                    monthViewPopulate();
                }else{
                    weekViewPopulate();
                }
            }
        }
    }


    /**
     * Opens the Add Appointment window.
     * @param actionEvent on clicking Add Appointment button
     * @throws IOException
     */
    public void openAddAppt(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Controller/AddAppointmentWindow.fxml"));
        Stage stage = (Stage) addApptButton.getScene().getWindow();
        Scene scene = new Scene(root, 400, 500);
        stage.setTitle(rb.getString("addApptBtn"));
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Send the apptToMod to the ModAppointmentWindow
     * @return apptToMod
     */
    public static Appointment getApptToMod() {
        return apptToMod;
    }

    /**
     * Opens Mod Appointment with the selected Appointment to Mod
     * @param actionEvent on click of Mod Appointment button
     * @throws IOException
     */
    public void openModAppt(ActionEvent actionEvent) throws IOException {
        if(scheduleTable.getSelectionModel().getSelectedIndex() != -1) {
            apptToMod = (Appointment) scheduleTable.getSelectionModel().getSelectedItem();
            Parent root = FXMLLoader.load(getClass().getResource("/Controller/ModAppointmentWindow.fxml"));
            Stage stage = (Stage) modApptButton.getScene().getWindow();
            Scene scene = new Scene(root, 400, 500);
            stage.setTitle(rb.getString("modApptBtn"));
            stage.setScene(scene);
            stage.show();
        }
    }

    /**
     * Populates the schedule with the Month View when you click the month view radio button
     * @param actionEvent click month view radio button
     * @throws SQLException
     */
    public void showMonthView(ActionEvent actionEvent) throws SQLException {
        monthViewPopulate();

    }


    /**
     * Populates the scheduleTable with the Month View of appointments
     */
    public void monthViewPopulate(){
        scheduleTable.getItems().clear();
        try {
            Appointment.getMonthOfAppointmentsFromDB();
            apptIDCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("apptID"));
            apptTitleCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("apptTitle"));
            descriptionCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("apptDesc"));
            apptLocCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("apptLocation"));
            apptConCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("apptContact"));
            apptTypeCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("apptType"));
            startDateCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("startDate"));
            startTimeCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("startTime"));
            endDateCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("endDate"));
            endTimeCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("endTime"));
            customerIDApptCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("custID"));
            customerNameAppt.setCellValueFactory(new PropertyValueFactory<Appointment, String>("custName"));
            userNameAppt.setCellValueFactory(new PropertyValueFactory<Appointment, String>("userName"));
            scheduleTable.setItems(Appointment.getMonthAppointments());
            startDateCol.setSortType(TableColumn.SortType.ASCENDING);
            scheduleTable.getSortOrder().add(startDateCol);
            scheduleTable.sort();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Clicking wth Week View radio button activates week view populate and shows the week view in the scheduleTable
     * @param actionEvent click week view radio button
     */
    public void showWeekView(ActionEvent actionEvent) {
        weekViewPopulate();
    }

    /**
     * Populates the schedule table with the appropriate columns and the appointments for the current week. It runs
     * getWeekOfAppointmentsFromDB to get the appointments and populate the weekappointments list for the table.
     */
    public void weekViewPopulate(){
        scheduleTable.getItems().clear();
        try {
            Appointment.getWeekOfAppointmentsFromDB();
            apptIDCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("apptID"));
            apptTitleCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("apptTitle"));
            descriptionCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("apptDesc"));
            apptLocCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("apptLocation"));
            apptConCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("apptContact"));
            apptTypeCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("apptType"));
            startDateCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("startDate"));
            startTimeCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("startTime"));
            endDateCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("endDate"));
            endTimeCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("endTime"));
            customerIDApptCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("custID"));
            customerNameAppt.setCellValueFactory(new PropertyValueFactory<Appointment, String>("custName"));
            userNameAppt.setCellValueFactory(new PropertyValueFactory<Appointment, String>("userName"));
            scheduleTable.setItems(Appointment.getWeekAppointments());
            startDateCol.setSortType(TableColumn.SortType.ASCENDING);
            scheduleTable.getSortOrder().add(startDateCol);
            scheduleTable.sort();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    /**
     * Gets the type of appointment and counts those for each month and adds it to the appointmentTypeAndMonth list
     * @return null
     * @throws SQLException
     */
    public ObservableList fillApptMonthTypeReport() throws SQLException {
        appointmentTypeAndMonth.removeAll(appointmentTypeAndMonth);
        Connection conn = DBConnection.getConnection();
        String sqlStatement = "SELECT Type, MONTH(Start), YEAR(Start), COUNT(Type)\n" +
                "FROM appointments\n" +
                "GROUP BY Type, MONTH(Start), YEAR(Start)";
        DBQuery.setPreparedStatement(conn, sqlStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while(rs.next()){
            String apptType = rs.getString("Type");
            int month = rs.getInt("MONTH(Start)");
            int year = rs.getInt("YEAR(Start)");
            int count = rs.getInt("COUNT(Type)");
            appointmentTypeAndMonth.add(new MainReports(apptType, month, year, count));
            }
        return null;
    }

    /**
     * Click the Contact Schedule radio button to run ContactReportPopulate to fill the table with the contact Report
     * @param actionEvent click Contact Schedule radio button.
     * @throws SQLException
     */
    public void contactReport(ActionEvent actionEvent) throws SQLException {
        contactReportPopulate();
    }

    /**
     * Populates the report table with the Contact Report, displaying appointments by contact.
      * @throws SQLException
     */
    public void contactReportPopulate() throws SQLException {
        Appointment.getContactAppointmentsFromDB();
        rptCol1.setText(rb.getString("conCol"));
        rptCol2.setText(rb.getString("apptidCol"));
        rptCol3.setText(rb.getString("titleCol"));
        rptCol4.setText(rb.getString("typeCol"));
        rptCol5.setText(rb.getString("desCol"));
        rptCol6.setText(rb.getString("startDateCol"));
        rptCol7.setText(rb.getString("startTimeCol"));
        rptCol8.setText(rb.getString("endDateCol"));
        rptCol9.setText(rb.getString("endTimeCol"));
        rptCol10.setText(rb.getString("custName"));
        rptCol1.setCellValueFactory(new PropertyValueFactory<Appointment, String>("contactName"));
        rptCol2.setCellValueFactory(new PropertyValueFactory<Appointment, String>("apptID"));
        rptCol3.setCellValueFactory(new PropertyValueFactory<Appointment, String>("apptTitle"));
        rptCol4.setCellValueFactory(new PropertyValueFactory<Appointment, String>("apptType"));
        rptCol5.setCellValueFactory(new PropertyValueFactory<Appointment, String>("apptDesc"));
        rptCol6.setCellValueFactory(new PropertyValueFactory<Appointment, String>("startDate"));
        rptCol7.setCellValueFactory(new PropertyValueFactory<Appointment, String>("startTime"));
        rptCol8.setCellValueFactory(new PropertyValueFactory<Appointment, String>("endDate"));
        rptCol9.setCellValueFactory(new PropertyValueFactory<Appointment, String>("endTime"));
        rptCol10.setCellValueFactory(new PropertyValueFactory<Appointment, String>("custName"));
        reportTable.setItems(Appointment.getContactAppointments());
        reportTable.getSortOrder().add(rptCol1);
        reportTable.sort();
    }

    /**
     * Runs pastAppointmentPopulate when the past appointments radio button is clicked
     * @param actionEvent click past appointments radio button
     * @throws SQLException
     */
    public void pastAppointments(ActionEvent actionEvent) throws SQLException {
        pastAppointmentPopulate();
    }

    /**
     * Gets the past appointments from the DB and sets up the report table for Past Appointments
     * @throws SQLException
     */
    public void pastAppointmentPopulate() throws SQLException {
        Appointment.getPastAppointmentsFromDB();
        rptCol1.setText(rb.getString("apptidCol"));
        rptCol2.setText(rb.getString("titleCol"));
        rptCol3.setText(rb.getString("typeCol"));
        rptCol4.setText(rb.getString("desCol"));
        rptCol5.setText(rb.getString("startDateCol"));
        rptCol6.setText(rb.getString("startTimeCol"));
        rptCol7.setText(rb.getString("endDateCol"));
        rptCol8.setText(rb.getString("endTimeCol"));
        rptCol9.setText(rb.getString("custName"));
        rptCol10.setText(rb.getString("conCol"));
        rptCol1.setCellValueFactory(new PropertyValueFactory<Appointment, String>("apptID"));
        rptCol2.setCellValueFactory(new PropertyValueFactory<Appointment, String>("apptTitle"));
        rptCol3.setCellValueFactory(new PropertyValueFactory<Appointment, String>("apptType"));
        rptCol4.setCellValueFactory(new PropertyValueFactory<Appointment, String>("apptDesc"));
        rptCol5.setCellValueFactory(new PropertyValueFactory<Appointment, String>("startDate"));
        rptCol6.setCellValueFactory(new PropertyValueFactory<Appointment, String>("startTime"));
        rptCol7.setCellValueFactory(new PropertyValueFactory<Appointment, String>("endDate"));
        rptCol8.setCellValueFactory(new PropertyValueFactory<Appointment, String>("endTime"));
        rptCol9.setCellValueFactory(new PropertyValueFactory<Appointment, String>("custName"));
        rptCol10.setCellValueFactory(new PropertyValueFactory<Appointment, String>("contactName"));
        reportTable.setItems(Appointment.getPastAppointments());
    }

    /**
     * Clicking the type report radio button runs the populate for the report table.
     * @param actionEvent click typeReport radio button
     */
    public void typeReport(ActionEvent actionEvent) {
        typeReportPopulate();
    }

    /**
     * Populates the report table with the type report that shows the type of report by month year and count
     * of the amount of those types each month.
     */
    public void typeReportPopulate(){
        rptCol1.setText(rb.getString("typeCol"));
        rptCol2.setText(rb.getString("month"));
        rptCol3.setText(rb.getString("year"));
        rptCol4.setText(rb.getString("count"));
        rptCol5.setText("");
        rptCol6.setText("");
        rptCol7.setText("");
        rptCol8.setText("");
        rptCol9.setText("");
        rptCol10.setText("");
        rptCol1.setCellValueFactory(new PropertyValueFactory<MainReports, String>("apptType"));
        rptCol2.setCellValueFactory(new PropertyValueFactory<MainReports, String>("apptMonth"));
        rptCol3.setCellValueFactory(new PropertyValueFactory<MainReports, String>("apptYear"));
        rptCol4.setCellValueFactory(new PropertyValueFactory<MainReports, String>("counter"));
        rptCol5.setCellValueFactory(new PropertyValueFactory<Appointment, String>(""));
        rptCol6.setCellValueFactory(new PropertyValueFactory<Appointment, String>(""));
        rptCol7.setCellValueFactory(new PropertyValueFactory<Appointment, String>(""));
        rptCol8.setCellValueFactory(new PropertyValueFactory<Appointment, String>(""));
        rptCol9.setCellValueFactory(new PropertyValueFactory<Appointment, String>(""));
        rptCol10.setCellValueFactory(new PropertyValueFactory<Appointment, String>(""));
        reportTable.setItems(appointmentTypeAndMonth);
    }


}