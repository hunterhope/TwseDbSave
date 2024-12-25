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
import com.hunterhope.twsedbsave.service.exception.TwseDbSaveException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 *
 * @author user
 */
public class TwseDbServiceTest {

    public TwseDbServiceTest() {
    }

    /**
     * 測試上網爬資料2個月
     */
    @Test
    public void testCrawl_two_month_data_real_send_request_2times() throws Exception {
        System.out.println("testCrawl_two_month_data_real_send_request_2times");
        //準備假物件
        JsonRequestService jrs = Mockito.mock(JsonRequestService.class);
        SaveDao saveDao = Mockito.mock(SaveDaoImpl.class);
        WaitClock waitClock = Mockito.mock(WaitClock.class);
        OneMonthPrice omp = new OneMonthPrice();
        omp.setStat("ok");
        omp.setData(List.of());
        Mockito.when(jrs.getData(Mockito.any(), Mockito.eq(OneMonthPrice.class))).thenReturn(omp);
        //準備物件
        String stockId = "2323";
        int months = 2;
        TwseDbSaveService tds = new TwseDbSaveService(jrs, saveDao,waitClock);
        //跑起來
        tds.crawl(stockId, LocalDate.now(), months);
        //驗證
        Mockito.verify(saveDao, Mockito.times(1)).createTable(Mockito.any());
        Mockito.verify(jrs, Mockito.times(2)).getData(Mockito.any(), Mockito.any());
    }

    /**
     * 測試上網查詢到資料後會存入資料庫
     */
    @Test
    public void testCrawl_hasData() throws Exception {
        System.out.println("testCrawl_saveDao_active");
        //準備假物件
        JsonRequestService jrs = Mockito.mock(JsonRequestService.class);
        SaveDao saveDao = Mockito.mock(SaveDaoImpl.class);
        WaitClock waitClock = Mockito.mock(WaitClock.class);
        OneMonthPrice omp = new OneMonthPrice();
        omp.setStat("ok");
        omp.setData(List.of());
        Mockito.when(jrs.getData(Mockito.any(), Mockito.eq(OneMonthPrice.class))).thenReturn(omp);
        //準備物件
        String stockId = "2323";
        int months = 2;
        TwseDbSaveService tds = new TwseDbSaveService(jrs, saveDao,waitClock);
        //跑起來
        tds.crawl(stockId, LocalDate.now(), months);
        //驗證
        Mockito.verify(saveDao, Mockito.times(1)).createTable(Mockito.any());
        Mockito.verify(saveDao, Mockito.times(2)).save(Mockito.any(), Mockito.any());

    }

    /**
     * 測試上網查詢到資料後會存入資料庫
     */
    @Test
    public void testCrawl_noData() throws Exception {
        System.out.println("testCrawl_saveDao_no_active");
        //準備假物件
        JsonRequestService jrs = Mockito.mock(JsonRequestService.class);
        SaveDao saveDao = Mockito.mock(SaveDaoImpl.class);
        WaitClock waitClock = Mockito.mock(WaitClock.class);
        OneMonthPrice omp = new OneMonthPrice();
        omp.setStat("沒有符合的資料");
        Mockito.when(jrs.getData(Mockito.any(), Mockito.eq(OneMonthPrice.class))).thenReturn(omp);
        //準備物件
        String stockId = "2323";
        int months = 2;
        TwseDbSaveService tds = new TwseDbSaveService(jrs, saveDao,waitClock);
        try {
            //跑起來
            tds.crawl(stockId, LocalDate.now(), months);
        } catch (NotMatchDataException ex) {
        }
        //驗證
        Mockito.verify(saveDao, Mockito.times(1)).createTable(Mockito.any());
        Mockito.verify(saveDao, Mockito.times(0)).save(Mockito.any(), Mockito.any());
    }

    /**
     * Test of updateHistory method, of class TwseDbService.
     */
    @Test
    public void testUpdateHistoryForOneYear() throws Exception{
        System.out.println("testUpdateHistoryForOneYear");
        String stockId = "2323";
        //準備假物件
        JsonRequestService jrs = Mockito.mock(JsonRequestService.class);
        SaveDao saveDao = Mockito.mock(SaveDaoImpl.class);
        WaitClock waitClock = Mockito.mock(WaitClock.class);
        Mockito.when(saveDao.queryLastDate(Mockito.any())).thenReturn("113/12/23");
        
        UrlAndQueryString qsOK = new UrlAndQueryString("https://www.twse.com.tw/rwd/zh/afterTrading/STOCK_DAY");
        qsOK.addParam("stockNo", stockId);
        qsOK.addParam("response", "json");
        qsOK.addParam("date", "20241223");
        
        UrlAndQueryString qsFail = new UrlAndQueryString("https://www.twse.com.tw/rwd/zh/afterTrading/STOCK_DAY");
        qsFail.addParam("stockNo", stockId);
        qsFail.addParam("response", "json");
        qsFail.addParam("date", "20231223");
        
        OneMonthPrice ompOK = new OneMonthPrice();
        ompOK.setStat("ok");
        ompOK.setData(List.of());
        
        OneMonthPrice ompError = new OneMonthPrice();
        ompError.setStat("無資料");
        ompError.setData(List.of());
        
        Mockito.when(jrs.getData(qsOK, OneMonthPrice.class)).thenReturn(ompOK);
        Mockito.when(jrs.getData(Mockito.any(), Mockito.eq(OneMonthPrice.class))).thenReturn(ompOK);
        Mockito.when(jrs.getData(qsFail, OneMonthPrice.class)).thenReturn(ompError);
        //準備物件
        TwseDbSaveService instance = new TwseDbSaveService(jrs,saveDao,waitClock);
        //跑起來
        instance.updateHistory(stockId);
        //驗證
        Mockito.verify(saveDao, Mockito.times(12)).save(Mockito.any(), Mockito.any());
        
    }
    
    @Test
    public void testUpdateToLatest() throws TwseDbSaveException{
        System.out.println("testUpdateToLatest");
        
        //準備假物件
        JsonRequestService jrs = Mockito.mock(JsonRequestService.class);
        SaveDao saveDao = Mockito.mock(SaveDaoImpl.class);
        WaitClock waitClock = Mockito.mock(WaitClock.class);
        Mockito.when(saveDao.queryLatestDate(Mockito.any())).thenReturn("113/11/23");
        
        //準備物件
        String stockId = "2323";
        TwseDbSaveService instance = new TwseDbSaveService(jrs,saveDao,waitClock);
        //跑起來
        instance.updateToLatest(stockId);
        //驗證
        Assertions.fail();
        
    }
   
}
