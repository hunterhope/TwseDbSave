/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.dao.impl;

import com.hunterhope.twsedbsave.dao.QueryDao;
import com.hunterhope.twsedbsave.entity.StockEveryDayInfo;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;

/**
 *
 * @author user
 */
public class QueryDaoImpl implements QueryDao {

    private final JdbcClient jdbcClient;

    public QueryDaoImpl(JdbcTemplate jdbcTemplate) {
        jdbcClient = JdbcClient.create(jdbcTemplate);
    }

    @Override
    public <T> List<T> selectAllDayInfo(String tableName, Class<T> dataClass) throws SQLException {
        String sql = "SELECT * FROM %s ORDER BY date;";
        try {
            return jdbcClient.sql(String.format(sql, tableName))
                    .query(dataClass).list();
        } catch (RuntimeException ex) {
            throw new SQLException(ex);
        }
    }

}
