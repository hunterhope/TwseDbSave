/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.service.data;

import com.hunterhope.twsedbsave.entity.StockEveryDayInfo;
import com.hunterhope.twsedbsave.service.exception.NotMatchDataException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author user
 */
public class OneMonthPrice {

    private String stat;
    private String date;
    private String title;
    private List<String> fields;
    private List<List<String>> data;

    private boolean hasData() {
        return "OK".equalsIgnoreCase(stat) && data != null;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public List<List<String>> getData() {
        return data;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.stat);
        hash = 23 * hash + Objects.hashCode(this.date);
        hash = 23 * hash + Objects.hashCode(this.title);
        hash = 23 * hash + Objects.hashCode(this.fields);
        hash = 23 * hash + Objects.hashCode(this.data);
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
        final OneMonthPrice other = (OneMonthPrice) obj;
        if (!Objects.equals(this.stat, other.stat)) {
            return false;
        }
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.fields, other.fields)) {
            return false;
        }
        return Objects.equals(this.data, other.data);
    }

    @Override
    public String toString() {
        return "OneMonthPrice{" + "stat=" + stat + ", date=" + date + ", title=" + title + ", fields=" + fields + ", data=" + data + '}';
    }

    public List<StockEveryDayInfo> convertToStockEveryDayInfo() throws NotMatchDataException {
        if (hasData()) {
            return data.stream().map(item -> {
                StockEveryDayInfo obj = new StockEveryDayInfo();
                obj.setDate(item.get(0).trim().replaceAll("/", "-"));
                obj.setVolume(item.get(1));
                obj.setOpen(item.get(3));
                obj.setHight(item.get(4));
                obj.setLow(item.get(5));
                obj.setClose(item.get(6));
                obj.setPriceDif(item.get(7));
                return obj;
            }).collect(Collectors.toList());
        }
        throw new NotMatchDataException(stat);
    }
}
