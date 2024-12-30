/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.hunterhope.twsedbsave.dao.impl.test;

import com.hunterhope.twsedbsave.dao.impl.SaveDaoImpl;
import com.hunterhope.twsedbsave.entity.StockEveryDayInfo;
import com.hunterhope.twsedbsave.other.StringDateToLocalDateUS;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.time.LocalDate;
import java.time.chrono.MinguoDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 *
 * @author user
 */
public class SaveDaoImplTest {

    private final Path SQLITE_DB_ROOT_PATH = Path.of("C:/Users/Public");

    public SaveDaoImplTest() {
    }

    /**
     * 測試save功能
     */
    @Test
    public void testSave() throws Exception {
        String dbName = "testSave.db";
        try {
            System.out.print("save測試:");
            //準備物件
            String tableName = "stock_2323";

            List<StockEveryDayInfo> data = createAnyYearTestDataForOneMonth();
            SaveDaoImpl instance = new SaveDaoImpl(createJdbcTemplate(dbName));
            //建立表格
            instance.createTable(tableName);
            //預期結果
            int[] expResult = new int[data.size()];
            Arrays.fill(expResult, 1);
            //跑起來
            int[] result = instance.save(tableName, data);
            //驗證
            assertArrayEquals(expResult, result);
            System.out.println("成功");
        } finally {
            //刪除資料庫
            Files.deleteIfExists(SQLITE_DB_ROOT_PATH.resolve(dbName));
        }
    }

    /**
     * 測試存入資料時,發生沒有該表格存在，會丟出SQLSyntaxErrorException
     */
    @Test
    public void testSave_throw_SQLSyntaxErrorException() throws Exception {
        String dbName = "testSave_throw_SQLSyntaxErrorException.db";
        try {
            System.out.print("save時，無該表格例外發生測試:");
            //準備物件
            String tableName = "stock_2323";

            List<StockEveryDayInfo> data = createAnyYearTestDataForOneMonth();
            SaveDaoImpl instance = new SaveDaoImpl(createJdbcTemplate(dbName));
            //跑起來
            instance.save(tableName, data);
            fail("沒發生例外");

        } catch (SQLSyntaxErrorException ex) {
            //驗證
            System.out.println("成功");
        } finally {
            //刪除資料庫
            Files.deleteIfExists(SQLITE_DB_ROOT_PATH.resolve(dbName));
        }
    }
    /**
     * 測試存入資料時，發生資料重複問題，丟出SQLIntegrityConstraintViolationException
     */
    @Test
    public void testSave_throw_SQLIntegrityConstraintViolationException()throws Exception{
        String dbName = "testSave_throw_SQLIntegrityConstraintViolationException.db";
        try {
            System.out.print("save時，資料有重複丟出例外:");
            //準備物件
            String tableName = "stock_2323";
            
            List<StockEveryDayInfo> data = createAnyYearTestDataForOneMonth();
            SaveDaoImpl instance = new SaveDaoImpl(createJdbcTemplate(dbName));
            //建立表格
            instance.createTable(tableName);
            //建立預設資料
            instance.save(tableName, data);
            //跑起來，存入重複資料
            instance.save(tableName, data);
            fail("沒發生例外");

        } catch (SQLIntegrityConstraintViolationException ex) {
            //驗證
            System.out.println("成功");
        } finally {
            //刪除資料庫
            Files.deleteIfExists(SQLITE_DB_ROOT_PATH.resolve(dbName));
        }
    }
    /**
     * 測試利用jdbcTemplete建立一張資料表
     */
    @Test
    public void testCreateTable() throws Exception {
        String dbName = "testCreateTable.db";
        try {
            System.out.print("測試利用jdbcTemplete建立一張資料表:");
            //準備物件
            String tableName = "stock_2323";
            SaveDaoImpl instance = new SaveDaoImpl(createJdbcTemplate(dbName));
            //跑起來
            instance.createTable(tableName);
            //驗證
            instance.dropTable(tableName);
            System.out.println("成功");
        } finally {
            //刪除sqlite資料庫方式
            Files.deleteIfExists(SQLITE_DB_ROOT_PATH.resolve(dbName));
        }

    }

    /**
     * 測試查詢資料庫內最舊日期
     */
    @Test
    public void testQueryLastDate() throws Exception {
        String dbName = "testQueryLastDate.db";
        try {
            System.out.print("測試查詢資料庫內最舊日期:");
            //建立物件
            String tableName = "stock_2323";

            SaveDaoImpl instance = new SaveDaoImpl(createJdbcTemplate(dbName));
            //準備待測資料
            instance.createTable(tableName);
            List<StockEveryDayInfo> data1 = createTestDataForOneMonth(113, 12, ThreadLocalRandom.current());
            List<StockEveryDayInfo> data2 = createTestDataForOneMonth(112, 12, ThreadLocalRandom.current());
            instance.save(tableName, data1);
            instance.save(tableName, data2);
            String expResult = "112/12/01";
            //跑起來
            String result = instance.queryLastDate(tableName);
            //驗證
            assertEquals(expResult, result);
            System.out.println("成功");
        } finally {
            //刪除資料庫
            Files.deleteIfExists(SQLITE_DB_ROOT_PATH.resolve(dbName));
        }
    }

    /**
     * 測試取得最新日期
     */
    @Test
    public void testQueryLatestDate() throws Exception {
        String dbName = "testQueryLatestDate.db";
        try {
            System.out.print("測試查詢資料庫內最新日期:");
            //建立物件
            String tableName = "stock_2323";
            SaveDaoImpl instance = new SaveDaoImpl(createJdbcTemplate(dbName));
            //準備待測資料
            instance.createTable(tableName);
            List<StockEveryDayInfo> data1 = createTestDataForOneMonth(113, 12, ThreadLocalRandom.current());
            List<StockEveryDayInfo> data2 = createTestDataForOneMonth(112, 12, ThreadLocalRandom.current());
            instance.save(tableName, data1);
            instance.save(tableName, data2);
            String expResult = "113/12/31";
            //跑起來
            String result = instance.queryLatestDate(tableName);
            //驗證
            assertEquals(expResult, result);
            System.out.println("成功");
        } finally {
            //刪除資料庫
            Files.deleteIfExists(SQLITE_DB_ROOT_PATH.resolve(dbName));
        }
    }

    /**
     * 測試取得指定某年月的資料
     */
    @Test
    public void testQueryDates() throws Exception {
        String dbName = "testQueryDates.db";
        try {
            System.out.print("測試取得指定某年月的資料:");
            //準備物件
            String tableName = "stock_2323";
            String yymmdd = "113/12/01";
            SaveDaoImpl instance = new SaveDaoImpl(createJdbcTemplate(dbName));
            //建立測試資料
            instance.createTable(tableName);
            List<StockEveryDayInfo> data1 = createTestDataForOneMonth(113, 12, ThreadLocalRandom.current());
            List<StockEveryDayInfo> data2 = createTestDataForOneMonth(113, 11, ThreadLocalRandom.current());
            instance.save(tableName, data1);
            instance.save(tableName, data2);
            List<String> expResult = createOneMonthDate(yymmdd);

            List<String> result = instance.queryDates(tableName, yymmdd);
            assertEquals(expResult, result);
            System.out.println("成功");
        } finally {
            //刪除資料庫
            Files.deleteIfExists(SQLITE_DB_ROOT_PATH.resolve(dbName));
        }
    }

    private JdbcTemplate createJdbcTemplate(String dbName) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:sqlite:" + SQLITE_DB_ROOT_PATH.resolve(dbName));//建議資料庫用在Public大家都可以存取的地方
        return new JdbcTemplate(dataSource);
    }

    private List<StockEveryDayInfo> createAnyYearTestDataForOneMonth() {
        Random r = ThreadLocalRandom.current();
        int yyy = r.nextInt(1, 1000);//3位數
        int month = r.nextInt(1, 13);//1~12月
        return createTestDataForOneMonth(yyy, month, r);
    }

    private List<StockEveryDayInfo> createTestDataForOneMonth(int yyy, int month, Random r) {
        List<StockEveryDayInfo> data = new ArrayList<>();
        MinguoDate md = MinguoDate.of(yyy, month, 1);
        MinguoDate endMd = md.plus(1, ChronoUnit.MONTHS);
        do {
            data.add(createRandomStockEveryDayInfo(md, r));
            md = md.plus(1, ChronoUnit.DAYS);
        } while (md.isBefore(endMd));

        return data;
    }

    private StockEveryDayInfo createRandomStockEveryDayInfo(MinguoDate md, Random r) {
        StockEveryDayInfo stockEveryDayInfo = new StockEveryDayInfo();
        stockEveryDayInfo.setDate(md.format(DateTimeFormatter.ofPattern("yyy/MM/dd")));
        stockEveryDayInfo.setOpen(String.format("%.2f", r.nextDouble(0.1, 1.0) * 100));
        stockEveryDayInfo.setHight(String.format("%.2f", r.nextDouble(0.1, 1.0) * 100));
        stockEveryDayInfo.setLow(String.format("%.2f", r.nextDouble(0.1, 1.0) * 100));
        stockEveryDayInfo.setClose(String.format("%.2f", r.nextDouble(0.1, 1.0) * 100));
        stockEveryDayInfo.setVolume(String.format("%d", r.nextInt(1000, 9999)));
        stockEveryDayInfo.setPriceDif(String.format("%.2f", r.nextDouble(0.1, 1.0) * 100));
        return stockEveryDayInfo;
    }

    private List<String> createOneMonthDate(String yymmdd) {
        LocalDate s = new StringDateToLocalDateUS().change(yymmdd);
        List<String> result = new ArrayList<>();
        LocalDate e = s.plusMonths(1);
        do {
            result.add(MinguoDate.from(s).format(DateTimeFormatter.ofPattern("yyy/MM/dd")));
            s = s.plusDays(1);
        } while (s.isBefore(e));
        return result;
    }

}
