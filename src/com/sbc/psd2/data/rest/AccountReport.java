package com.sbc.psd2.data.rest;


import java.util.ArrayList;

public class AccountReport {

    private ArrayList<Transactions> booked;
    private ArrayList<Transactions> pending;

    public AccountReport() {
    }

    public AccountReport(ArrayList<Transactions> booked, ArrayList<Transactions> pending) {
        this.booked = booked;
        this.pending = pending;
    }

    public ArrayList<Transactions> getBooked() {
        return booked;
    }

    public void setBooked(ArrayList<Transactions> booked) {
        this.booked = booked;
    }

    public ArrayList<Transactions> getPending() {
        return pending;
    }

    public void setPending(ArrayList<Transactions> pending) {
        this.pending = pending;
    }
}
