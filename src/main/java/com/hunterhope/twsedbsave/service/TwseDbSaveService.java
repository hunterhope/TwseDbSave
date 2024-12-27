/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.service;

import com.hunterhope.twsedbsave.other.WaitClock;
import com.hunterhope.jsonrequest.service.JsonRequestService;
import com.hunterhope.jsonrequest.service.UrlAndQueryString;
import com.hunterhope.jsonrequest.exception.DataClassFieldNameErrorException;
import com.hunterhope.jsonrequest.exception.NoInternetException;
import com.hunterhope.jsonrequest.exception.ResponseEmptyException;
import com.hunterhope.jsonrequest.exception.ServerMaintainException;
import com.hunterhope.twsedbsave.dao.SaveDao;
import com.hunterhope.twsedbsave.entity.StockEveryDayInfo;
import com.hunterhope.twsedbsave.other.RemoveDuplicateDataUS;
import com.hunterhope.twsedbsave.other.StringDateToLocalDateUS;
import com.hunterhope.twsedbsave.service.data.OneMonthPrice;
import com.hunterhope.twsedbsave.service.exception.NotMatchDataException;
import com.hunterhope.twsedbsave.service.exception.TwseDbSaveException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.chrono.MinguoDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author user
 */
public class TwseDbSaveService {

    private final JsonRequestService jrs;
    private final SaveDao saveDao;
    private final String TWSE_STOCK_PRICE_BASE_URL = "https://www.twse.com.tw/rwd/zh/afterTrading/STOCK_DAY";//?date=20240331&stockNo=2323&response=json
    private final WaitClock waitClock;
    private final StringDateToLocalDateUS sdToLdUS;

    public TwseDbSaveService() {
        this.jrs = new JsonRequestService();
        this.saveDao = null;
        this.waitClock = new WaitClock();
        this.sdToLdUS=new StringDateToLocalDateUS();
    }

    public TwseDbSaveService(JsonRequestService jrs, SaveDao saveDao, WaitClock waitClock1) {
        this.jrs = jrs;
        this.saveDao = saveDao;
        this.waitClock = waitClock1;
        this.sdToLdUS=new StringDateToLocalDateUS();
    }

    /**
     * 上網爬指定股票指定開始日期，爬指定幾個月份
     *
     * @param stockId 股票代碼
     * @param stateDate 開始日期
     * @param months 開始日期後爬幾個月(包含開始日期月份)
     * @throws TwseDbSaveException 包裝底層例外用
     * @throws NotMatchDataException 查詢回來如果沒有符合的資料，有可能是此股票代號錯誤，或沒有該股票的交易紀錄了
     */
    public void crawl(String stockId, LocalDate stateDate, int months) throws TwseDbSaveException, NotMatchDataException {
        String tableName = combinTableName(stockId);
        List<StockEveryDayInfo> data = null;
        //建立UrlAndQueryString
        UrlAndQueryString qs = new UrlAndQueryString(TWSE_STOCK_PRICE_BASE_URL);
        qs.addParam("stockNo", stockId);
        qs.addParam("response", "json");
        for (int i = 0; i < months; i++) {
            qs.addParam("date", stateDate.minusMonths(i).format(DateTimeFormatter.BASIC_ISO_DATE));
            try {
                //上網爬資料
                OneMonthPrice omp = jrs.getData(qs, OneMonthPrice.class);
                if (omp.hasData()) {
                    //轉換成資料庫表格形式
                    data = convert(omp);
                    //存入資料庫
                    saveDataToDb(tableName, data);
                } else {
                    throw new NotMatchDataException(omp.getStat());
                }
            } catch (NoInternetException | ServerMaintainException | DataClassFieldNameErrorException | ResponseEmptyException ex) {
                throw new TwseDbSaveException(ex);
            } catch (SQLSyntaxErrorException ex) {
                try {
                    //建立表格
                    saveDao.createTable(tableName);
                    //在存入資料庫一次
                    saveDataToDb(tableName, data);//基本上來到這邊data不應該是null,所以不檢查
                } catch (SQLException ex1) {
                    throw new RuntimeException(ex1);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            //每次上網爬資料間隔5~10秒
            if (months > 1) {//只抓取1個月則不用等
                waitClock.waitForSecurity(5, 11);
            }
        }
    }

    private void saveDataToDb(String tableName, List<StockEveryDayInfo> data) throws SQLException {
        try {
            saveDao.save(tableName, data);
        } catch (SQLIntegrityConstraintViolationException ex) { //捕捉重複資料產生的例外
            new RemoveDuplicateDataUS().action(tableName, saveDao, data);
            //在存入資料庫一次
            saveDao.save(tableName, data);
        }
    }

    private List<StockEveryDayInfo> convert(OneMonthPrice omp) {
        return omp.getData().stream()
                .map(items -> {
                    StockEveryDayInfo obj = new StockEveryDayInfo();
                    obj.setDate(items.get(0));
                    obj.setVolume(items.get(1));
                    obj.setOpen(items.get(3));
                    obj.setHight(items.get(4));
                    obj.setLow(items.get(5));
                    obj.setClose(items.get(6));
                    obj.setPriceDif(items.get(7));
                    return obj;
                })
                .collect(Collectors.toList());

    }

    private String combinTableName(String stockId) {
        return StockEveryDayInfo.TABLE_NAME_PREFIX + stockId;
    }

    /**
     * 自動更新歷史資料，到抓不到資料
     */
    public void updateHistory(String stockId) throws TwseDbSaveException {
        try {
            //查詢出最後一筆資料日期(原則上每次抓取資料都是一個月一個月的)
            LocalDate lastDate = queryLastDate(stockId);
            //無限迴圈上網抓取資料，直到沒有資料
            do {
                crawl(stockId, lastDate, 1);
                lastDate = lastDate.minusMonths(1);
            } while (true);
        } catch (NotMatchDataException ex) {
            //更新結束
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private LocalDate queryLastDate(String stockId) throws SQLException {
        return sdToLdUS.change(saveDao.queryLastDate(combinTableName(stockId)));
    }

    private LocalDate queryLatestDate(String stockId) throws SQLException {
        return sdToLdUS.change(saveDao.queryLatestDate(combinTableName(stockId)));
    }

    /**
     * 方便測試用,建議使用updateToLatest(String stockId)
     */
    public void updateToLatest(String stockId, LocalDate nowDate) throws TwseDbSaveException {

        try {
            //取得資料庫內最新紀錄日期
            LocalDate dbLatestDate = queryLatestDate(stockId);
            //比對日期差異計算出要更新月份數
            long difMonths = ChronoUnit.MONTHS.between(YearMonth.from(dbLatestDate), YearMonth.from(nowDate)) + 1;//+1是因為dbLatestDate的月份也要抓取用來確保資料完整
            crawl(stockId, nowDate, (int) difMonths);
        } catch (NotMatchDataException ex) {
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 自動更新到最新資料
     */
    public void updateToLatest(String stockId) throws TwseDbSaveException {
        updateToLatest(stockId, LocalDate.now());
    }
}
