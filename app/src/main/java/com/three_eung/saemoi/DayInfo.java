package com.three_eung.saemoi;

import java.util.ArrayList;

/**
 * Created by CH on 2018-02-18.
 */

public class DayInfo {
    private String day;
    private int income = 0;
    private int outcome = 0;
    private int total = 0;
    private ArrayList<EventInfo> eventList = null;

    public String getDay() { return day; }

    public void setDay(String day) { this.day = day; }

    public ArrayList<EventInfo> getEventList() { return eventList; }

    public void setEventList(ArrayList<EventInfo> eventList) { this.eventList = eventList; }

    public int getIncome() { return income; }

    public int getOutcome() { return outcome; }

    public void setIncome(int income) { this.income += income; }

    public void setOutcome(int outcome) { this.outcome += outcome; }

    public void updateItem(EventInfo eventInfo) {
        if(eventInfo.getInout().equals("income")) {
            income += eventInfo.getValue();
        } else if(eventInfo.getInout().equals("outcome")) {
            outcome += eventInfo.getValue();
        }

        total = income - outcome;

        if(eventList == null) {
            eventList = new ArrayList<>();
        }
        eventList.add(eventInfo);
    }

    public void removeItem(EventInfo eventInfo) {
        if(eventInfo.getInout().equals("income")) {
            income -= eventInfo.getValue();
        } else if(eventInfo.getInout().equals("outcome")) {
            outcome -= eventInfo.getValue();
        }

        total = income - outcome;

        if(eventList != null) {
            for(EventInfo tmp : eventList) {
                if(tmp.getId().equals(eventInfo.getId())) {
                    eventList.remove(tmp);
                    break;
                }
            }
        }
    }
}
