/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.hunterhope.twsedbsave.dao.impl.test;

import com.hunterhope.twsedbsave.dao.impl.SaveDaoImpl;
import com.hunterhope.twsedbsave.entity.StockEveryDayInfo;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Period;
import java.time.chrono.MinguoDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
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
    private final String SQLITE_DB_NAME = "C:/Users/Public/twseTest.db";

    public SaveDaoImplTest() {
        dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:sqlite:" + SQLITE_DB_NAME);//建議資料庫用在Public大家都可以存取的地方
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Test of save method, of class SaveDaoImpl.
     */
    @Test
    public void testSave() throws Exception{
        System.out.print("save測試:");
        //準備物件
        String tableName = "stock_2323";
        List<StockEveryDayInfo> data = createAnyYearTestDataForOneMonth();
        SaveDaoImpl instance = new SaveDaoImpl(jdbcTemplate);
        //建立表格
        instance.createTable(tableName);
        //預期結果
        int[] expResult = new int[data.size()];
        Arrays.fill(expResult, 1);
        //跑起來
        int[] result = instance.save(tableName, data);
        //驗證
        assertArrayEquals(expResult, result);
        //刪除資料庫
        Files.deleteIfExists(Path.of(SQLITE_DB_NAME));
        System.out.println("成功");
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

    private List<StockEveryDayInfo> createAnyYearTestDataForOneMonth() {
        List<StockEveryDayInfo> data = new ArrayList<>();
        Random r = ThreadLocalRandom.current();
        int yyy = r.nextInt(1, 1000);//3位數
        int month = r.nextInt(1, 13);//1~12月
        MinguoDate md = MinguoDate.of(yyy, month, 1);
        MinguoDate endMd = md.plus(1, ChronoUnit.MONTHS);
        do {
            data.add(createRandomStockEveryDayInfo(md, r));
            md = md.plus(1, ChronoUnit.DAYS);
        }while(md.isBefore(endMd));
        
        return data;
    }

    private StockEveryDayInfo createRandomStockEveryDayInfo(MinguoDate md, Random r) {
        StockEveryDayInfo stockEveryDayInfo = new StockEveryDayInfo();
        stockEveryDayInfo.setDate(md.format(DateTimeFormatter.ofPattern("yyy/MM/dd")));
        stockEveryDayInfo.setOpen(String.format("%.2f", r.nextDouble(0.1, 1.0)*100));
        stockEveryDayInfo.setHight(String.format("%.2f", r.nextDouble(0.1, 1.0)*100));
        stockEveryDayInfo.setLow(String.format("%.2f", r.nextDouble(0.1, 1.0)*100));
        stockEveryDayInfo.setClose(String.format("%.2f", r.nextDouble(0.1, 1.0)*100));
        stockEveryDayInfo.setVolume(String.format("%d", r.nextInt(1000, 9999)));
        stockEveryDayInfo.setPriceDif(String.format("%.2f", r.nextDouble(0.1, 1.0)*100));
        return stockEveryDayInfo;
    }

}
