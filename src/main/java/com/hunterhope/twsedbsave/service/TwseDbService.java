/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.service;

import com.hunterhope.jsonrequest.JsonRequestService;
import com.hunterhope.jsonrequest.UrlAndQueryString;
import com.hunterhope.jsonrequest.exception.DataClassFieldNameErrorException;
import com.hunterhope.jsonrequest.exception.NoInternetException;
import com.hunterhope.jsonrequest.exception.ResponseEmptyException;
import com.hunterhope.jsonrequest.exception.ServerMaintainException;
import com.hunterhope.twsedbsave.dao.SaveDao;
import com.hunterhope.twsedbsave.entity.StockEveryDayInfo;
import com.hunterhope.twsedbsave.service.data.OneMonthPrice;
import com.hunterhope.twsedbsave.service.exception.NotMatchDataException;
import com.hunterhope.twsedbsave.service.exception.TwseDbSaveException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 *
 * @author user
 */
public class TwseDbService {

    private final JsonRequestService jrs;
    private final SaveDao saveDao;
    private final String TWSE_STOCK_PRICE_BASE_URL = "https://www.twse.com.tw/rwd/zh/afterTrading/STOCK_DAY";//?date=20240331&stockNo=2323&response=json

    public TwseDbService() {
        this.jrs = new JsonRequestService();
        this.saveDao = null;
    }

    public TwseDbService(JsonRequestService jrs, SaveDao saveDao) {
        this.jrs = jrs;
        this.saveDao = saveDao;
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
        String tableName = StockEveryDayInfo.TABLE_NAME_PREFIX + stockId;
        //建立表格
        saveDao.createTable(tableName);
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
                    List<StockEveryDayInfo> data = convert(omp);
                    //存入資料庫
                    saveDao.save(tableName, data);
                } else {
                    throw new NotMatchDataException(omp.getStat());
                }
            } catch (NoInternetException | ServerMaintainException | DataClassFieldNameErrorException | ResponseEmptyException ex) {
                throw new TwseDbSaveException(ex);
            }
            //每次上網爬資料間隔5~10秒
            if (months > 1) {//只抓取1個月則不用等
                waitForSecurity();
            }
        }
    }

    private void waitForSecurity() {
        Random r = new Random();
        try {
            Thread.sleep(r.nextLong(5, 11) * 1000);
        } catch (InterruptedException ex) {
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

    public void updateHistory(String stockId) {
    }
}
