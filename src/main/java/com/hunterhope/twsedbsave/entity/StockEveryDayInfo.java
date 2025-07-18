/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.entity;

import java.util.Objects;

/**
 * 資料庫資料表的實體<br>
 * 由於資料表設計成每一支股票一個資料表，而每個資料表的欄位都相同，所以此類別不特定於某一個資料表<br>
 * 而是代表日成交資訊的某一資料表實體<br>
 * @author hunterhope
 */
public class StockEveryDayInfo {
    public static final String TABLE_NAME_PREFIX="STOCK_";
    private String date;
    private String volume;
    private String open;
    private String high;
    private String low;
    private String close;
    private String priceDif;

    public String getPriceDif() {
        return priceDif;
    }

    public void setPriceDif(String priceDif) {
        this.priceDif = priceDif;
    }

    public String getDate() {
        return date.trim();
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getHigh() {
        return high;
    }

    public void setHight(String high) {
        this.high = high;
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
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.date);
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
        final StockEveryDayInfo other = (StockEveryDayInfo) obj;
        return Objects.equals(this.date, other.date);
    }

    @Override
    public String toString() {
        return "StockEveryDayInfo{" + "date=" + date + ", volume=" + volume + ", open=" + open + ", high=" + high + ", low=" + low + ", close=" + close + ", priceDif=" + priceDif + '}';
    }

    public static String combinTableName(String stockId) {
        return StockEveryDayInfo.TABLE_NAME_PREFIX + stockId;
    }
}
