/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.dao.impl.test;

import java.util.Objects;

/**
 *
 * @author user
 */
public class DataClassTemp {
    private String date;
    private String open;
    private String hight;
    private String low;
    private String close;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getHight() {
        return hight;
    }

    public void setHight(String hight) {
        this.hight = hight;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.date);
        hash = 97 * hash + Objects.hashCode(this.open);
        hash = 97 * hash + Objects.hashCode(this.hight);
        hash = 97 * hash + Objects.hashCode(this.low);
        hash = 97 * hash + Objects.hashCode(this.close);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataClassTemp other = (DataClassTemp) obj;
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        if (!Objects.equals(this.open, other.open)) {
            return false;
        }
        if (!Objects.equals(this.hight, other.hight)) {
            return false;
        }
        if (!Objects.equals(this.low, other.low)) {
            return false;
        }
        return Objects.equals(this.close, other.close);
    }

    @Override
    public String toString() {
        return "DataClassTemp{" + "date=" + date + ", open=" + open + ", hight=" + hight + ", low=" + low + ", close=" + close + '}';
    }
    
    
}
