package Model;


/**
 * Joshua Breen C195 Project
 * MainReports class that was used for Meeting Type by Month with a Count of how many each month
 */
public class MainReports {

    private int counter;
    private String apptType;
    private int apptMonth;
    private int apptYear;

    /**
     * Creator for main reports, reports back on the month and type
     * @param apptType Appointment Type
     * @param apptMonth Appointment Month
     * @param apptYear Appointment Year
     * @param counter Count of how many appointments happened that month
     */
    public MainReports(String apptType, int apptMonth, int apptYear, int counter){
        this.apptType = apptType;
        this.apptMonth = apptMonth;
        this.apptYear = apptYear;
        this.counter = counter;
    }

    /**
     * Getter for Appointment Type
     * @return apptType
     */
    public String getApptType(){
        return apptType;
    }

    /**
     * Getter for appointment month
     * @return apptMonth
     */
    public int getApptMonth(){
        return apptMonth;
    }

    /**
     * Getter for appointment year
     * @return apptYear
     */
    public int getApptYear(){
        return apptYear;
    }

    /**
     * Getter for counter
     * @return counter
     */
    public int getCounter(){
        return counter;
    }

    /**
     * Setter for counter
     * @param counter
     */
    public void setCounter(int counter) {
        this.counter = counter;
    }


}
