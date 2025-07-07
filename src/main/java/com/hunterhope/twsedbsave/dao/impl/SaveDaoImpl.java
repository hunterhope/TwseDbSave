/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.dao.impl;

import com.hunterhope.twsedbsave.dao.SaveDao;
import com.hunterhope.twsedbsave.entity.StockEveryDayInfo;
import com.hunterhope.twsedbsave.other.StringDateMinguoDateToLocalDateUS;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.chrono.MinguoDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.jdbc.UncategorizedSQLException;
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
    public int[] save(String tableName, List<StockEveryDayInfo> data) throws SQLException {
        try {
            String sql = "INSERT INTO %s VALUES(?,?,?,?,?,?,?);";
            int[] rowEffect = jdbcTemplate.batchUpdate(String.format(sql, tableName),
                    new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    StockEveryDayInfo info = data.get(i);
                    ps.setString(1, info.getDate());
                    ps.setString(2, info.getVolume());
                    ps.setString(3, info.getOpen());
                    ps.setString(4, info.getHigh());
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
        } catch (UncategorizedSQLException ex) {
            if (ex.getMessage().contains("no such table")) {
                createTable(tableName);
                return save(tableName, data);

            } else if (ex.getMessage().contains("SQLITE_CONSTRAINT_PRIMARYKEY")) {
                removeDuplicateData(tableName, data);
                return save(tableName, data);
            }
            throw new SQLException(ex);
        }
    }

    private void removeDuplicateData(String tableName, List<StockEveryDayInfo> data) throws SQLException {
        String saveYYMMDD = data.get(0).getDate();
        MinguoDate md = MinguoDate.from(new StringDateMinguoDateToLocalDateUS().change(saveYYMMDD).withDayOfMonth(1));
        //查詢資料庫此月份資料出來
        List<String> dbDates = queryDates(tableName, md.format(DateTimeFormatter.ofPattern("y-MM-dd")));
        //排除重複資料
        data.removeIf(e -> dbDates.contains(e.getDate()));
    }

    @Override
    public void createTable(String tableName) throws SQLException {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS %s (date TEXT,volume TEXT NOT NULL,open TEXT NOT NULL,high TEXT NOT NULL,low TEXT NOT NULL,close TEXT NOT NULL,price_dif TEXT NOT NULL,PRIMARY KEY(date))";
            jdbcTemplate.execute(String.format(sql, tableName));
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public String queryLastDate(String tableName) throws SQLException {
        try {
            String sql = "SELECT date FROM %s ORDER BY date LIMIT 1;";
            return jdbcTemplate.queryForObject(String.format(sql, tableName), String.class);
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public String queryLatestDate(String tableName) throws SQLException {
        try {
            String sql = "SELECT date FROM %s ORDER BY date DESC LIMIT 1;";
            return jdbcTemplate.queryForObject(String.format(sql, tableName), String.class);
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public List<String> queryDates(String tableName, String yymmdd) throws SQLException {
        try {
            String endYYMMDD = new StringBuffer(yymmdd).replace(yymmdd.length() - 2, yymmdd.length(), "31").toString();
            String sql = "SELECT date FROM %s WHERE date >= '%s' AND date <= '%s';";
            System.out.println("sql="+String.format(sql, tableName, yymmdd, endYYMMDD));
            return jdbcTemplate.queryForList(String.format(sql, tableName, yymmdd, endYYMMDD), String.class);
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * 給測試程式使用
     */
    public void dropTable(String tableName) {
        String sql = "DROP TABLE %s";
        jdbcTemplate.execute(String.format(sql, tableName));
    }
}
