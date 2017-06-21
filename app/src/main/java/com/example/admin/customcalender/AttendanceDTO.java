package com.example.admin.customcalender;

/**
 * Created by Admin on 29-05-2017.
 */

public class AttendanceDTO {
    String date;
    String statue;

    public AttendanceDTO(String date, String statue) {
        this.date = date;
        this.statue = statue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }

    @Override
    public String toString() {
        return "AttendanceDTO{" +
                "date='" + date + '\'' +
                ", statue='" + statue + '\'' +
                '}';
    }
}
