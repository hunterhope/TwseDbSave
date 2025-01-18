/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.hunterhope.twsedbsave.dao;

import com.hunterhope.twsedbsave.entity.StockEveryDayInfo;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author user
 */
public interface QueryDao {

    public List<StockEveryDayInfo> selectAllDayInfo(String combinTableName) throws SQLException;
    
}
