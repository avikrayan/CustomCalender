package com.example.admin.customcalender;

/**
 * Created by Admin on 30-05-2017.
 */

public class MonthDTO {
    String month, present, absent, leave;

    public MonthDTO(String month, String present, String absent, String leave) {
        this.month = month;
        this.present = present;
        this.absent = absent;
        this.leave = leave;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getPresent() {
        return present;
    }

    public void setPresent(String present) {
        this.present = present;
    }

    public String getAbsent() {
        return absent;
    }

    public void setAbsent(String absent) {
        this.absent = absent;
    }

    public String getLeave() {
        return leave;
    }

    public void setLeave(String leave) {
        this.leave = leave;
    }

    @Override
    public String toString() {
        return "MonthDTO{" +
                "month='" + month + '\'' +
                ", present='" + present + '\'' +
                ", absent='" + absent + '\'' +
                ", leave='" + leave + '\'' +
                '}';
    }
}
