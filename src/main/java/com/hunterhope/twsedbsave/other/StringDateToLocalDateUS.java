/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.other;

import java.time.LocalDate;
import java.time.chrono.MinguoDate;

/**
 *
 * @author user
 */
public class StringDateToLocalDateUS {
    /**
     * @param sDate 格是要求yyy/mm/dd的字串
     */
    public LocalDate change(String sDate) {
        String[] ymd = sDate.split("/");
        return LocalDate.from(MinguoDate.of(
                Integer.parseInt(ymd[0]),
                Integer.parseInt(ymd[1]),
                Integer.parseInt(ymd[2])));
    }
}
