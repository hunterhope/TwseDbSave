/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.other;

import com.hunterhope.twsedbsave.dao.SaveDao;
import com.hunterhope.twsedbsave.entity.StockEveryDayInfo;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.chrono.MinguoDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author user
 */
public class RemoveDuplicateDataUS {

    public void action(String tableName, SaveDao saveDao, List<StockEveryDayInfo> data) throws SQLException {

        String saveYYMMDD = data.get(0).getDate();
        MinguoDate md = MinguoDate.from(new StringDateToLocalDateUS().change(saveYYMMDD).withDayOfMonth(1));
        //查詢資料庫此月份資料出來
        List<String> dbDates = saveDao.queryDates(tableName, md.format(DateTimeFormatter.ofPattern("yyy/MM/dd")));
        //排除重複資料
        data.removeIf(e -> dbDates.contains(e.getDate()));
    }
}
