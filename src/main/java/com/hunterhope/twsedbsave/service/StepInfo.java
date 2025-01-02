/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.service;

import java.util.Objects;

/**
 *
 * @author user
 */
public class StepInfo {
    private final String stockId;
    private final String msg;

    public StepInfo(String stockId, String msg) {
        this.stockId = stockId;
        this.msg = msg;
    }

    public String getStockId() {
        return stockId;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.stockId);
        hash = 23 * hash + Objects.hashCode(this.msg);
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
        final StepInfo other = (StepInfo) obj;
        if (!Objects.equals(this.stockId, other.stockId)) {
            return false;
        }
        return Objects.equals(this.msg, other.msg);
    }
    
    
}
