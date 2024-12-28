/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.hunterhope.twsedbsave.dao.impl.test;

import com.hunterhope.twsedbsave.dao.impl.SaveDaoImpl;
import com.hunterhope.twsedbsave.entity.StockEveryDayInfo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 *
 * @author user
 */
public class SaveDaoImplTest {
    private final DriverManagerDataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final String SQLITE_DB_NAME="C:/Users/Public/twseTest.db";
    public SaveDaoImplTest() {
        dataSource=new DriverManagerDataSource();
        dataSource.setUrl("jdbc:sqlite:"+SQLITE_DB_NAME);//建議資料庫用在Public大家都可以存取的地方
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Test of save method, of class SaveDaoImpl.
     */
    @Test
    public void testSave() {
        System.out.println("save");
        String tableName = "";
        List<StockEveryDayInfo> data = null;
        SaveDaoImpl instance = null;
        int[] expResult = null;
        int[] result = instance.save(tableName, data);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * 測試利用jdbcTemplete建立一張資料表
     */
    @Test
    public void testCreateTable() throws Exception {
        System.out.print("測試利用jdbcTemplete建立一張資料表:");
        //準備物件
        String tableName = "stock_2323";
        SaveDaoImpl instance = new SaveDaoImpl(jdbcTemplate);
        //跑起來
        instance.createTable(tableName);
        //驗證
        instance.dropTable(tableName);
        System.out.println("成功");
        //刪除sqlite資料庫方式
        Files.deleteIfExists(Path.of(SQLITE_DB_NAME));
    }

    /**
     * Test of queryLastDate method, of class SaveDaoImpl.
     */
    @Test
    public void testQueryLastDate() {
        System.out.println("queryLastDate");
        String tableName = "";
        SaveDaoImpl instance = null;
        String expResult = "";
        String result = instance.queryLastDate(tableName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of queryLatestDate method, of class SaveDaoImpl.
     */
    @Test
    public void testQueryLatestDate() {
        System.out.println("queryLatestDate");
        String tableName = "";
        SaveDaoImpl instance = null;
        String expResult = "";
        String result = instance.queryLatestDate(tableName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of queryDates method, of class SaveDaoImpl.
     */
    @Test
    public void testQueryDates() throws Exception {
        System.out.println("queryDates");
        String tableName = "";
        String yymmdd = "";
        SaveDaoImpl instance = null;
        List<String> expResult = null;
        List<String> result = instance.queryDates(tableName, yymmdd);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
