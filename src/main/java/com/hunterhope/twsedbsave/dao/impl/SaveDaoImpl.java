/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.dao.impl;

import com.hunterhope.twsedbsave.dao.SaveDao;
import com.hunterhope.twsedbsave.entity.StockEveryDayInfo;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author user
 */
public class SaveDaoImpl implements SaveDao {

    private final JdbcTemplate jdbcTemplate;

    public SaveDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int[] save(String tableName, List<StockEveryDayInfo> data) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void createTable(String tableName) {
        String sql = "CREATE TABLE IF NOT EXISTS %s (date TEXT,volume TEXT NOT NULL,open TEXT NOT NULL,hight TEXT NOT NULL,low TEXT NOT NULL,close TEXT NOT NULL,price_dif TEXT NOT NULL,PRIMARY KEY(date))";
        jdbcTemplate.execute(String.format(sql,tableName));
    }

    @Override
    public String queryLastDate(String tableName) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String queryLatestDate(String tableName) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<String> queryDates(String tableName, String yymmdd) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    /**
     * 給測試程式使用
     */
    public void dropTable(String tableName){
        String sql="DROP TABLE %s";
        jdbcTemplate.execute(String.format(sql, tableName));
    }
}
