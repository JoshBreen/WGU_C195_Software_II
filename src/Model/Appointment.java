package Model;

import Utilities.DBConnection;
import Utilities.DBQuery;
import Utilities.GetNowFormatted;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Joshua Breen C195 Project
 * Class to store appointments, and populates the ObservableList with all appointments through the various methods
 * needed from the database. Added a lambda expression to get the current system time and convert it into code that will
 * be accepted by SQL. It will help if we updated other commands to pull now from the system and makes the code for getting
 * the current appointments for the alert more efficient but also getting the past appointments to be from before the system
 * time.
 */
public class Appointment {


    private int apptID;
    private String apptTitle;
    private String apptDesc;
    private String apptLocation;
    private String apptContact;
    private String apptType;
    private Timestamp startDateTime;
    private LocalDate startDate;
    private LocalTime startTime;
    private Timestamp endDateTime;
    private LocalDate endDate;
    private LocalTime endTime;
    private int custID;
    private String custName;
    private int userID;
    private int contactID;
    private String contactName;
    private String userName;
    private static ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private static ObservableList<Appointment> monthAppointments = FXCollections.observableArrayList();
    private static ObservableList<Appointment> weekAppointments = FXCollections.observableArrayList();
    private static ObservableList<Appointment> contactAppointments = FXCollections.observableArrayList();
    private static ObservableList<Appointment> pastAppointments = FXCollections.observableArrayList();
    private static ObservableList<Appointment> upcomingAppointments = FXCollections.observableArrayList();

    public static GetNowFormatted timeConvert = () -> {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        String s = now.format(format);
        return s;
    };


    /**
     * Creates the Appointment Class
     * @param apptID Appointment ID - int
     * @param apptTitle Appointment Title - String
     * @param apptDesc Appointment Description - String
     * @param apptLocation Appointment Location - String
     * @param apptContact Appointment Contact - String
     * @param apptType Appointment Type - String
     * @param startDateTime Appointment Start Time - LocalTime
     * @param startDate Appointment Start Date - LocalDate
     * @param startTime Appointment Start - Timestamp
     * @param endDateTime Appointment End Time - Timestamp
     * @param endDate Appointment End Date - LocalDate
     * @param endTime Appointment End Time - LocalTime
     * @param custID Appointment Customer ID - int
     * @param custName Appointment Customer Name - String
     * @param userID Appointment User ID - int
     * @param contactID Appointment Contact ID - int
     * @param contactName Appointment Contact Name - String
     * @param userName Appointment User Name - String
     */
    public Appointment(int apptID, String apptTitle, String apptDesc, String apptLocation, String apptContact, String apptType, Timestamp startDateTime,
                       LocalDate startDate, LocalTime startTime, Timestamp endDateTime, LocalDate endDate, LocalTime endTime, int custID,
                       String custName, int userID, int contactID, String contactName, String userName){
        this.apptID = apptID;
        this.apptTitle = apptTitle;
        this.apptDesc = apptDesc;
        this.apptLocation = apptLocation;
        this.apptContact = apptContact;
        this.apptType = apptType;
        this.startDateTime = startDateTime;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDateTime = endDateTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.custID = custID;
        this.custName = custName;
        this.userID = userID;
        this.contactID = contactID;
        this.contactName = contactName;
        this.userName = userName;
    }

    /**
     * Gets upcoming appointments from the DB and populates upcoming Appointments list
     * @return null
     * @throws SQLException
     */
    public static ObservableList getUpcomingAppointmentsFromDB() throws SQLException {
        upcomingAppointments.removeAll(upcomingAppointments);
        String formatDateTime = timeConvert.getNow();
        Connection conn = DBConnection.getConnection();
        String sqlStatement = "SELECT * FROM appointments WHERE Start > ?";
        DBQuery.setPreparedStatement(conn, sqlStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.setString(1,formatDateTime);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while(rs.next()){
            int apptID = rs.getInt("Appointment_ID");
            String apptTitle = rs.getString("Title");
            String apptDesc = rs.getString("Description");
            String apptLocation = rs.getString("Location");
            int contactID = rs.getInt("Contact_ID");
            String contactName = getContactNameFromID(contactID);
            String apptType = rs.getString("Type");
            Timestamp startDateTime = rs.getTimestamp("Start");
            LocalDate startDate = startDateTime.toLocalDateTime().toLocalDate();
            LocalTime startTime = startDateTime.toLocalDateTime().toLocalTime();
            Timestamp endDateTime = rs.getTimestamp("End");
            LocalDate endDate = endDateTime.toLocalDateTime().toLocalDate();
            LocalTime endTime = endDateTime.toLocalDateTime().toLocalTime();
            int custID = rs.getInt("Customer_ID");
            String custName = getCustomerNameFromID(custID);
            int userID = rs.getInt("User_ID");
            String userName = getUserNameFromID(userID);
            upcomingAppointments.add(new Appointment(apptID, apptTitle, apptDesc, apptLocation, contactName, apptType, startDateTime, startDate,
                    startTime, endDateTime, endDate, endTime, custID, custName, userID, contactID, contactName, userName));

        }
        return null;
    }



    /**
     * Clears the ObservableList of all appointments then populates it with the most current list of appointments from
     * the database.
     * @return null
     * @throws SQLException
     */
    public static ObservableList getAllAppointmentsFromDB() throws SQLException{
        allAppointments.removeAll(allAppointments);
        Connection conn = DBConnection.getConnection();
        String sqlStatement = "SELECT * FROM appointments";
        DBQuery.setPreparedStatement(conn, sqlStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while(rs.next()){
            int apptID = rs.getInt("Appointment_ID");
            String apptTitle = rs.getString("Title");
            String apptDesc = rs.getString("Description");
            String apptLocation = rs.getString("Location");
            int contactID = rs.getInt("Contact_ID");
            String contactName = getContactNameFromID(contactID);
            String apptType = rs.getString("Type");
            Timestamp startDateTime = rs.getTimestamp("Start");
            LocalDate startDate = startDateTime.toLocalDateTime().toLocalDate();
            LocalTime startTime = startDateTime.toLocalDateTime().toLocalTime();
            Timestamp endDateTime = rs.getTimestamp("End");
            LocalDate endDate = endDateTime.toLocalDateTime().toLocalDate();
            LocalTime endTime = endDateTime.toLocalDateTime().toLocalTime();
            int custID = rs.getInt("Customer_ID");
            String custName = getCustomerNameFromID(custID);
            int userID = rs.getInt("User_ID");
            String userName = getUserNameFromID(userID);
            allAppointments.add(new Appointment(apptID, apptTitle, apptDesc, apptLocation, contactName, apptType, startDateTime, startDate,
                    startTime, endDateTime, endDate, endTime, custID, custName, userID, contactID, contactName, userName));

        }
        return null;
    }


    /**
     * Get all currently active appointments from the Database
     * @return null
     * @throws SQLException
     */
    public static ObservableList getContactAppointmentsFromDB() throws SQLException{
        contactAppointments.removeAll(contactAppointments);
        Connection conn = DBConnection.getConnection();
        String sqlStatement = "SELECT * FROM appointments WHERE End >= CURRENT_TIMESTAMP()";
        DBQuery.setPreparedStatement(conn, sqlStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while(rs.next()){
            int apptID = rs.getInt("Appointment_ID");
            String apptTitle = rs.getString("Title");
            String apptDesc = rs.getString("Description");
            String apptLocation = rs.getString("Location");
            int contactID = rs.getInt("Contact_ID");
            String contactName = getContactNameFromID(contactID);
            String apptType = rs.getString("Type");
            Timestamp startDateTime = rs.getTimestamp("Start");
            LocalDate startDate = startDateTime.toLocalDateTime().toLocalDate();
            LocalTime startTime = startDateTime.toLocalDateTime().toLocalTime();
            Timestamp endDateTime = rs.getTimestamp("End");
            LocalDate endDate = endDateTime.toLocalDateTime().toLocalDate();
            LocalTime endTime = endDateTime.toLocalDateTime().toLocalTime();
            int custID = rs.getInt("Customer_ID");
            String custName = getCustomerNameFromID(custID);
            int userID = rs.getInt("User_ID");
            String userName = getUserNameFromID(userID);
            contactAppointments.add(new Appointment(apptID, apptTitle, apptDesc, apptLocation, contactName, apptType, startDateTime, startDate,
                        startTime, endDateTime, endDate, endTime, custID, custName, userID, contactID, contactName, userName));
        }
        return null;
    }

    /**
     * Gets all past appointments from the database to populate the past appointments list
     * @return null
     * @throws SQLException
     */
    public static ObservableList getPastAppointmentsFromDB() throws SQLException{
        pastAppointments.removeAll(pastAppointments);
        Connection conn = DBConnection.getConnection();
        String s = timeConvert.getNow();
        String sqlStatement = "SELECT * FROM appointments WHERE End < ?";
        DBQuery.setPreparedStatement(conn, sqlStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.setString(1,s);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while(rs.next()){
            int apptID = rs.getInt("Appointment_ID");
            String apptTitle = rs.getString("Title");
            String apptDesc = rs.getString("Description");
            String apptLocation = rs.getString("Location");
            int contactID = rs.getInt("Contact_ID");
            String contactName = getContactNameFromID(contactID);
            String apptType = rs.getString("Type");
            Timestamp startDateTime = rs.getTimestamp("Start");
            LocalDate startDate = startDateTime.toLocalDateTime().toLocalDate();
            LocalTime startTime = startDateTime.toLocalDateTime().toLocalTime();
            Timestamp endDateTime = rs.getTimestamp("End");
            LocalDate endDate = endDateTime.toLocalDateTime().toLocalDate();
            LocalTime endTime = endDateTime.toLocalDateTime().toLocalTime();
            int custID = rs.getInt("Customer_ID");
            String custName = getCustomerNameFromID(custID);
            int userID = rs.getInt("User_ID");
            String userName = getUserNameFromID(userID);
            pastAppointments.add(new Appointment(apptID, apptTitle, apptDesc, apptLocation, contactName, apptType, startDateTime, startDate,
                    startTime, endDateTime, endDate, endTime, custID, custName, userID, contactID, contactName, userName));
        }
        return null;
    }

    /**
     * Gets appointments for the month from the database and populates the list
     * @return null
     * @throws SQLException
     */
    public static ObservableList getMonthOfAppointmentsFromDB() throws SQLException{
        monthAppointments.removeAll(monthAppointments);
        Connection conn = DBConnection.getConnection();
        String sqlStatement = "SELECT * FROM appointments";
        DBQuery.setPreparedStatement(conn, sqlStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute();
        ResultSet rs = ps.getResultSet();
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();
        while(rs.next()){
            Timestamp startDateTime = rs.getTimestamp("Start");
            LocalDate startDate = startDateTime.toLocalDateTime().toLocalDate();
            if(startDate.getMonthValue() == month && startDate.getYear() == year){
                int apptID = rs.getInt("Appointment_ID");
                String apptTitle = rs.getString("Title");
                String apptDesc = rs.getString("Description");
                String apptLocation = rs.getString("Location");
                int contactID = rs.getInt("Contact_ID");
                String contactName = getContactNameFromID(contactID);
                String apptType = rs.getString("Type");
                LocalTime startTime = startDateTime.toLocalDateTime().toLocalTime();
                Timestamp endDateTime = rs.getTimestamp("End");
                LocalDate endDate = endDateTime.toLocalDateTime().toLocalDate();
                LocalTime endTime = endDateTime.toLocalDateTime().toLocalTime();
                int custID = rs.getInt("Customer_ID");
                String custName = getCustomerNameFromID(custID);
                int userID = rs.getInt("User_ID");
                String userName = getUserNameFromID(userID);
                monthAppointments.add(new Appointment(apptID, apptTitle, apptDesc, apptLocation, contactName, apptType, startDateTime, startDate,
                    startTime, endDateTime, endDate, endTime, custID, custName, userID, contactID, contactName, userName));}

        }
        return null;
    }

    /**
     * Determines the day of the week, then gets the appointments for that week and populates the list
     * @return
     * @throws SQLException
     */
    public static ObservableList getWeekOfAppointmentsFromDB() throws SQLException{
        weekAppointments.removeAll(weekAppointments);
        LocalDate t = LocalDate.now();
        DayOfWeek day = t.getDayOfWeek();
        LocalDate startOfWeek = t;
        if(day == DayOfWeek.SUNDAY){
            startOfWeek = t;
        }
        if(day == DayOfWeek.MONDAY){
            startOfWeek = t.minusDays(1);
        }
        if(day == DayOfWeek.TUESDAY){
            startOfWeek = t.minusDays(2);
        }
        if(day == DayOfWeek.WEDNESDAY){
            startOfWeek = t.minusDays(3);
        }
        if(day == DayOfWeek.THURSDAY){
            startOfWeek = t.minusDays(4);
        }
        if(day == DayOfWeek.FRIDAY){
            startOfWeek = t.minusDays(5);
        }
        if(day == DayOfWeek.SATURDAY){
            startOfWeek = t.minusDays(6);
        }

        Connection conn = DBConnection.getConnection();
        String sqlStatement = "SELECT * FROM appointments";
        DBQuery.setPreparedStatement(conn, sqlStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute();
        ResultSet rs = ps.getResultSet();
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();
        while(rs.next()){
            Timestamp startDateTime = rs.getTimestamp("Start");
            LocalDate startDate = startDateTime.toLocalDateTime().toLocalDate();
            if(startDate.isAfter(startOfWeek.minusDays(1)) && startDate.isBefore(startOfWeek.plusDays(7))){
                int apptID = rs.getInt("Appointment_ID");
                String apptTitle = rs.getString("Title");
                String apptDesc = rs.getString("Description");
                String apptLocation = rs.getString("Location");
                int contactID = rs.getInt("Contact_ID");
                String contactName = getContactNameFromID(contactID);
                String apptType = rs.getString("Type");
                LocalTime startTime = startDateTime.toLocalDateTime().toLocalTime();
                Timestamp endDateTime = rs.getTimestamp("End");
                LocalDate endDate = endDateTime.toLocalDateTime().toLocalDate();
                LocalTime endTime = endDateTime.toLocalDateTime().toLocalTime();
                int custID = rs.getInt("Customer_ID");
                String custName = getCustomerNameFromID(custID);
                int userID = rs.getInt("User_ID");
                String userName = getUserNameFromID(userID);
                weekAppointments.add(new Appointment(apptID, apptTitle, apptDesc, apptLocation, contactName, apptType, startDateTime, startDate,
                        startTime, endDateTime, endDate, endTime, custID, custName, userID, contactID, contactName, userName));}

        }
        return null;
    }

    /**
     * Inputs the Contact Name to get the Contact ID from the Database.
     * @param contactID inputs the contact ID
     * @return contact name connected to the contact ID
     * @throws SQLException
     */
    public static String getContactNameFromID(int contactID) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String sqlStatement = "SELECT * FROM contacts WHERE Contact_ID = ?";
        DBQuery.setPreparedStatement(conn, sqlStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.setInt(1,contactID);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while(rs.next()){
            String contactName = rs.getString("Contact_Name");
            return contactName;
        }

        return null;
    }

    /**
     * Converts customer ID into Customer Name
     * @param customerID inputs the customer ID
     * @return customer name
     * @throws SQLException
     */
    public static String getCustomerNameFromID(int customerID) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String sqlStatement = "SELECT * FROM customers WHERE Customer_ID = ?";
        DBQuery.setPreparedStatement(conn, sqlStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.setInt(1,customerID);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while(rs.next()){
            String custName = rs.getString("Customer_Name");
            return custName;
        }

        return null;
    }

    /**
     * Convers the User ID into User Name
     * @param userID inputs the user ID
     * @return the User Name
     * @throws SQLException
     */
    public static String getUserNameFromID(int userID) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String sqlStatement = "SELECT * FROM users WHERE User_ID = ?";
        DBQuery.setPreparedStatement(conn, sqlStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.setInt(1,userID);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while(rs.next()){
            String userName = rs.getString("User_Name");
            return userName;
        }
    return null;
    }

    /**
     * Gets appointment ID
     * @return appointment ID
     */
    public int getApptID(){
        return apptID;
    }

    /**
     * Getter for Appointment Title
     * @return apptTitle
     */
    public String getApptTitle(){
        return apptTitle;
    }

    /**
     * Getter for Appointment Description
     * @return apptDesc
     */
    public String getApptDesc(){
        return apptDesc;
    }

    /**
     * Getter for appointment location
     * @return apptLocation
     */
    public String getApptLocation(){
        return apptLocation;
    }

    /**
     * Getter for Appointment Contact
     * @return apptContact
     */
    public String getApptContact(){
        return apptContact;
    }

    /**
     * Getter for Appointment Type
     * @return apptType
     */
    public String getApptType(){
        return apptType;
    }

    /**
     * Getter for Start Date Time
     * @return startDateTime
     */
    public Timestamp getStartDateTime(){
        return startDateTime;
    }

    /**
     * Getter for Start Date
     * @return startDate
     */
    public LocalDate getStartDate(){
        return startDate;
    }

    /**
     * Getter for Start Time
     * @return startTime
     */
    public LocalTime getStartTime(){
        return startTime;
    }

    /**
     * Getter for End Date Time
     * @return endDateTime
     */
    public Timestamp getEndDateTime(){
        return endDateTime;
    }

    /**
     * Getter for End Date
     * @return endDate
     */
    public LocalDate getEndDate(){
        return endDate;
    }

    /**
     * Getter for End Time
     * @return endTime
     */
    public LocalTime getEndTime(){
        return endTime;
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
     * @return
     */
    public String getCustName(){
        return custName;
    }

    /**
     * Getter for User ID
     * @return userID
     */
    public int getUserID(){
        return userID;
    }

    /**
     * Getter for Contact ID
     * @return contactID
     */
    public int getContactID(){
        return contactID;
    }

    /**
     * Getter for Contact Name
     * @return contactName
     */
    public String getContactName(){
        return contactName;
    }

    /**
     * Getter for User Name
     * @return userName
     */
    public String getUserName(){
        return userName;
    }

    /**
     * Getter for all appointments list
     * @return allAppointments
     */
    public static ObservableList<Appointment> getAppointmentList(){
        return allAppointments;
    }

    /**
     * Getter for Month Appointment list
     * @return monthAppointments
     */
    public static ObservableList<Appointment> getMonthAppointments(){
        return monthAppointments;
    }

    /**
     * Getter for Week Appointments list
     * @return weekAppointments
     */
    public static ObservableList<Appointment> getWeekAppointments() {
        return weekAppointments;
    }

    /**
     * Getter for Contact Appointments list
     * @return contactAppointments
     */
    public static ObservableList<Appointment> getContactAppointments(){
        return contactAppointments;
    }

    /**
     * Getter for Past Appointments List
     * @return pastAppointments
     */
    public static ObservableList<Appointment> getPastAppointments(){
        return pastAppointments;
    }

    /**
     * Getter for Upcoming Appointments list
     * @return upcomingAppointments
     */
    public static ObservableList<Appointment> getUpcomingAppointments() {
        return upcomingAppointments;
    }



}
