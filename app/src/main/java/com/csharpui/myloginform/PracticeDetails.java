package com.csharpui.myloginform;

public class PracticeDetails {
    private String name,days,timings,fees;
    public PracticeDetails(String name,String days, String timings, String fees) {
        this.name = name;
        this.timings = timings;
        this.fees = fees;
        this.days = days;
    }
    public String getPracticeName(){return name;}
    public String getPracticeDays(){return days;}

    public String getTimings(){return timings;}

    public String getPracticeFees() {
        return fees;
    }

}
