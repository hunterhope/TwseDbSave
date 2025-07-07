/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.dao.impl.test.other;

import com.hunterhope.twsedbsave.entity.StockEveryDayInfo;
import java.nio.file.Path;
import java.time.chrono.MinguoDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
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
    
    public static List<StockEveryDayInfo> createAnyYearTestDataForOneMonth() {
        Random r = ThreadLocalRandom.current();
        int yyy = r.nextInt(1, 1000);//3位數
        int month = r.nextInt(1, 13);//1~12月
        return createTestDataForOneMonth(yyy, month, r);
    }
    
    public static List<StockEveryDayInfo> createTestDataForOneMonth(int yyy, int month, Random r) {
        List<StockEveryDayInfo> data = new ArrayList<>();
        MinguoDate md = MinguoDate.of(yyy, month, 1);
        MinguoDate endMd = md.plus(1, ChronoUnit.MONTHS);
        do {
            data.add(createRandomStockEveryDayInfo(md, r));
            md = md.plus(1, ChronoUnit.DAYS);
        } while (md.isBefore(endMd));

        return data;
    }
    
    private static StockEveryDayInfo createRandomStockEveryDayInfo(MinguoDate md, Random r) {
        StockEveryDayInfo stockEveryDayInfo = new StockEveryDayInfo();
        stockEveryDayInfo.setDate(md.format(DateTimeFormatter.ofPattern("y-MM-dd")));
        stockEveryDayInfo.setOpen(String.format("%.2f", r.nextDouble(0.1, 1.0) * 100));
        stockEveryDayInfo.setHight(String.format("%.2f", r.nextDouble(0.1, 1.0) * 100));
        stockEveryDayInfo.setLow(String.format("%.2f", r.nextDouble(0.1, 1.0) * 100));
        stockEveryDayInfo.setClose(String.format("%.2f", r.nextDouble(0.1, 1.0) * 100));
        stockEveryDayInfo.setVolume(String.format("%d", r.nextInt(1000, 9999)));
        stockEveryDayInfo.setPriceDif(String.format("%.2f", r.nextDouble(0.1, 1.0) * 100));
        return stockEveryDayInfo;
    }
}
