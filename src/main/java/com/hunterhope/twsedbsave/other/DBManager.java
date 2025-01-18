/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.other;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 *
 * @author user
 */
public class DBManager {

    private static final JdbcTemplate jdbcTemplate;

    static {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:sqlite:C:/Users/Public/twsedb.db");//建議資料庫用在Public大家都可以存取的地方
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public static JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
