package com.three_eung.saemoi;

/**
 * Created by CH on 2018-03-19.
 */

public class DailyList {
    private String id;
    private String inout;
    private String day;
    private String yearmonth;
    private int value;

    public void setId(String id) { this.id = id; }

    public String getId() { return id; }

    public void setYearmonth(String yearmonth) {
        this.yearmonth = yearmonth;
    }

    public String getYearmonth() {
        return yearmonth;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setInout(String inout) { this.inout = inout; }

    public String getInout() { return inout; }

    public void setDay(String day) { this.day = day; }

    public String getDay() { return day; }
}
