/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.hunterhope.twsedbsave.service.test;

import com.hunterhope.jsonrequest.service.JsonRequestService;
import com.hunterhope.jsonrequest.service.UrlAndQueryString;
import com.hunterhope.twsedbsave.dao.SaveDao;
import com.hunterhope.twsedbsave.dao.impl.SaveDaoImpl;
import com.hunterhope.twsedbsave.service.TwseDbSaveService;
import com.hunterhope.twsedbsave.other.WaitClock;
import com.hunterhope.twsedbsave.service.data.OneMonthPrice;
import com.hunterhope.twsedbsave.service.exception.NotMatchDataException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 *
 * @author user
 */
public class TwseDbServiceTest {

    //準備假物件
    private JsonRequestService jrs = Mockito.mock(JsonRequestService.class);
    private SaveDao saveDao = Mockito.mock(SaveDaoImpl.class);
    private WaitClock waitClock = Mockito.mock(WaitClock.class);
    private OneMonthPrice hasData;
    private OneMonthPrice noData;

    public TwseDbServiceTest() {
        hasData = new OneMonthPrice();
        hasData.setStat("ok");
        hasData.setData(List.of());
        noData = new OneMonthPrice();
        noData.setStat("沒有資料");
    }

    private void mock_request_hasData() throws Exception {
        Mockito.when(jrs.getData(Mockito.any(), Mockito.eq(OneMonthPrice.class))).thenReturn(hasData);
    }

    private void mock_request_noData(UrlAndQueryString noDataQueryString) throws Exception {
        if (noDataQueryString == null) {
            Mockito.when(jrs.getData(Mockito.any(), Mockito.eq(OneMonthPrice.class))).thenReturn(noData);
        } else {
            Mockito.when(jrs.getData(noDataQueryString, OneMonthPrice.class)).thenReturn(noData);
        }
    }

    private void mock_db_latestDate(String latestData) {
        Mockito.when(saveDao.queryLatestDate(Mockito.any())).thenReturn(latestData);
    }

    private void verifyDaoCreateTable(int times) {
        Mockito.verify(saveDao, Mockito.times(times)).createTable(Mockito.any());
    }

    private void verifyHttpRequest(int times) throws Exception {
        Mockito.verify(jrs, Mockito.times(times)).getData(Mockito.any(), Mockito.any());
    }

    private void verifyDaoSave(int times) {
        Mockito.verify(saveDao, Mockito.times(times)).save(Mockito.any(), Mockito.any());
    }

    /**
     * 測試上網爬資料2個月
     */
    @Test
    public void testCrawl_two_month_data_real_send_request_2times() throws Exception {
        System.out.println("testCrawl_two_month_data_real_send_request_2times");
        //準備物件
        String stockId = "2323";
        int months = 2;
        TwseDbSaveService tds = new TwseDbSaveService(jrs, saveDao, waitClock);
        //模擬依賴行為
        mock_request_hasData();
        //跑起來
        tds.crawl(stockId, LocalDate.now(), months);
        //驗證
        verifyDaoCreateTable(1);
        verifyHttpRequest(2);

    }

    /**
     * 測試上網查詢到資料後會存入資料庫
     */
    @Test
    public void testCrawl_hasData() throws Exception {
        System.out.println("testCrawl_saveDao_active");
        //準備物件
        String stockId = "2323";
        int months = 2;
        TwseDbSaveService tds = new TwseDbSaveService(jrs, saveDao, waitClock);
        //模擬依賴行為
        mock_request_hasData();
        //跑起來
        tds.crawl(stockId, LocalDate.now(), months);
        //驗證
        verifyDaoSave(2);

    }

    /**
     * 測試上網查詢得不到該筆資料
     */
    @Test
    public void testCrawl_noData() throws Exception {
        System.out.println("testCrawl_saveDao_no_active");
        //準備物件
        String stockId = "2323";
        int months = 2;
        TwseDbSaveService tds = new TwseDbSaveService(jrs, saveDao, waitClock);
        //模擬依賴行為
        mock_request_noData(null);
        try {
            //跑起來
            tds.crawl(stockId, LocalDate.now(), months);
            verifyDaoSave(2);
        } catch (NotMatchDataException ex) {
            //驗證
            verifyDaoCreateTable(1);
            verifyDaoSave(0);
        }
    }

    /**
     * Test of updateHistory method, of class TwseDbService.
     */
    @Test
    public void testUpdateHistoryForOneYear() throws Exception {
        System.out.println("testUpdateHistoryForOneYear");
        //準備物件
        String stockId = "2323";
        UrlAndQueryString noDataQueryString = new UrlAndQueryString("https://www.twse.com.tw/rwd/zh/afterTrading/STOCK_DAY");
        noDataQueryString.addParam("stockNo", stockId);
        noDataQueryString.addParam("response", "json");
        noDataQueryString.addParam("date", "20231223");
        TwseDbSaveService instance = new TwseDbSaveService(jrs, saveDao, waitClock);
        //模擬依賴行為
        mock_request_hasData();
        mock_request_noData(noDataQueryString);
        Mockito.when(saveDao.queryLastDate(Mockito.any())).thenReturn("113/12/23");
        //跑起來
        instance.updateHistory(stockId);
        //驗證
        verifyDaoSave(12);
    }

    @Test
    public void testUpdateToLatest_1_month() throws Exception {
        System.out.println("testUpdateToLatest_1_month");
        //準備物件
        String stockId = "2323";
        TwseDbSaveService instance = new TwseDbSaveService(jrs, saveDao, waitClock);
        //模擬依賴行為
        mock_request_hasData();
        mock_db_latestDate("112/12/23");
        //跑起來
        instance.updateToLatest(stockId, LocalDate.of(2023, 12, 30));
        //驗證
        verifyDaoSave(1);
    }

    @Test
    public void testUpdateToLatest_13_month() throws Exception {
        System.out.println("testUpdateToLatest_12_month");
        //準備物件
        String stockId = "2323";
        TwseDbSaveService instance = new TwseDbSaveService(jrs, saveDao, waitClock);
        //模擬依賴行為
        mock_request_hasData();
        mock_db_latestDate("112/12/23");
        //跑起來
        instance.updateToLatest(stockId, LocalDate.of(2024, 12, 30));
        //驗證
        verifyDaoSave(13);
    }

}
