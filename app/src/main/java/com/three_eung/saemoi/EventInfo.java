package com.three_eung.saemoi;

/**
 * Created by CH on 2018-02-18.
 */

public class EventInfo {
    private String id;
    private String yearmonth;
    private String day;
    private String inout;
    private int value;

    public EventInfo() { }

    public EventInfo(String inout, String yearmonth, String day, int value) {
        this.inout = inout;
        this.yearmonth = yearmonth;
        this.day = day;
        this.value = value;
    }

    public String getYearmonth() { return yearmonth; }

    public void setYearmonth(String yearmonth) { this.yearmonth = yearmonth; }

    public String getDay() { return day; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public void setDay(String day) { this.day = day; }

    public String getInout() { return inout; }

    public void setInout(String inout) { this.inout = inout; }

    public int getValue() { return value; }

    public void setValue(int value) { this.value = value; }
}
