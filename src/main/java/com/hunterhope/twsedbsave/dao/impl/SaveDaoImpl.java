/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.dao.impl;

import com.hunterhope.twsedbsave.dao.SaveDao;
import com.hunterhope.twsedbsave.entity.StockEveryDayInfo;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
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
        String sql = "INSERT INTO %s VALUES(?,?,?,?,?,?,?);";
        int[] rowEffect = jdbcTemplate.batchUpdate(String.format(sql, tableName),
                new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                StockEveryDayInfo info = data.get(i);
                ps.setString(1, info.getDate());
                ps.setString(2, info.getVolume());
                ps.setString(3, info.getOpen());
                ps.setString(4, info.getHight());
                ps.setString(5, info.getLow());
                ps.setString(6, info.getClose());
                ps.setString(7, info.getPriceDif());
            }

            @Override
            public int getBatchSize() {
                return data.size();
            }

        });

        return rowEffect;
    }

    @Override
    public void createTable(String tableName) {
        String sql = "CREATE TABLE IF NOT EXISTS %s (date TEXT,volume TEXT NOT NULL,open TEXT NOT NULL,hight TEXT NOT NULL,low TEXT NOT NULL,close TEXT NOT NULL,price_dif TEXT NOT NULL,PRIMARY KEY(date))";
        jdbcTemplate.execute(String.format(sql, tableName));
    }

    @Override
    public String queryLastDate(String tableName) {
        String sql="SELECT date FROM %s ORDER BY date LIMIT 1;";
        return jdbcTemplate.queryForObject(String.format(sql, tableName), String.class);
    }

    @Override
    public String queryLatestDate(String tableName) {
        String sql="SELECT date FROM %s ORDER BY date DESC LIMIT 1;";
        return jdbcTemplate.queryForObject(String.format(sql, tableName), String.class);
    }

    @Override
    public List<String> queryDates(String tableName, String yymmdd) throws SQLException {
        String endYYMMDD=new StringBuffer(yymmdd).replace(yymmdd.length()-2, yymmdd.length(), "31").toString();
        String sql="SELECT date FROM %s WHERE date >= '%s' AND date <= '%s';";
        return jdbcTemplate.queryForList(String.format(sql, tableName,yymmdd,endYYMMDD), String.class);
    }

    /**
     * 給測試程式使用
     */
    public void dropTable(String tableName) {
        String sql = "DROP TABLE %s";
        jdbcTemplate.execute(String.format(sql, tableName));
    }
}
