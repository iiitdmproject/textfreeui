package com.example.kavin.caller;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by kavin on 12/4/17.
 */
public class CallLog implements java.io.Serializable {
    final private ArrayList<String> numbers;
    final private ArrayList<Date> times;

    public CallLog() {
        numbers = new ArrayList<String>();
        times = new ArrayList<Date>();
    }

    public void add(String number, Date time) {
        numbers.add(number);
        times.add(time);
    }

    public ArrayList<String> getNumbers() {
        return numbers;
    }
    public ArrayList<Date> getTimes() {
        return times;
    }
}
