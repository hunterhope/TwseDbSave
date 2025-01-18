/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.dao.impl.test.other;

import java.nio.file.Path;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 *
 * @author user
 */
public class UtilityForTest {

    public static final Path SQLITE_DB_ROOT_PATH = Path.of("C:/Users/Public");

    public static JdbcTemplate createJdbcTemplate(String dbName) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:sqlite:" + SQLITE_DB_ROOT_PATH.resolve(dbName));//建議資料庫用在Public大家都可以存取的地方
        return new JdbcTemplate(dataSource);
    }
}
