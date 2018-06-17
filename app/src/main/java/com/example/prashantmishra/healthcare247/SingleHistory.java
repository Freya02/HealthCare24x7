package com.example.prashantmishra.healthcare247;

/**
 * Created by Prashant Mishra on 17-06-2018.
 */

public class SingleHistory {
    String dest_address;
    String date;
    String time;
    String contact;

    public SingleHistory() {
    }

    public SingleHistory(String dest_address, String date, String time, String contact) {

        this.dest_address = dest_address;
        this.date = date;
        this.time = time;
        this.contact = contact;
    }

    public String getDest_address() {
        return dest_address;
    }

    public void setDest_address(String dest_address) {
        this.dest_address = dest_address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
