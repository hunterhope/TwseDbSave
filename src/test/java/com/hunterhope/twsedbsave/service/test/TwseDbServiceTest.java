/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.hunterhope.twsedbsave.service.test;

import com.hunterhope.jsonrequest.JsonRequestService;
import com.hunterhope.twsedbsave.dao.SaveDao;
import com.hunterhope.twsedbsave.dao.impl.SaveDaoImpl;
import com.hunterhope.twsedbsave.service.TwseDbService;
import com.hunterhope.twsedbsave.service.data.OneMonthPrice;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
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
        //準備物件
        String stockId = "2323";
        int months = 2;
        TwseDbService tds = new TwseDbService(jrs, null);
        //跑起來
        tds.crawl(stockId,LocalDate.now(), months);
        //驗證
        Mockito.verify(jrs, Mockito.times(2)).getData(Mockito.any(), Mockito.any());
    }

    /**
     * 測試上網查詢到資料後會存入資料庫
     */
    @Test
    public void testCrawl_saveDao_active() throws Exception{
        System.out.println("testCrawl_saveDao_active");
        //準備假物件
        JsonRequestService jrs = Mockito.mock(JsonRequestService.class);
        SaveDao saveDao = Mockito.mock(SaveDaoImpl.class);
        OneMonthPrice omp = new OneMonthPrice();
        omp.setData(List.of());
        Optional<OneMonthPrice> opt = Optional.of(omp);
        Mockito.when(jrs.getData(Mockito.any(),Mockito.eq(OneMonthPrice.class))).thenReturn(opt);
        //準備物件
        String stockId = "2323";
        int months = 2;
        TwseDbService tds = new TwseDbService(jrs, saveDao);
        //跑起來
        tds.crawl(stockId,LocalDate.now(), months);
        //驗證
        Mockito.verify(saveDao, Mockito.times(1)).createTable(Mockito.any());
        Mockito.verify(saveDao, Mockito.times(1)).save(Mockito.any(), Mockito.any());

    }

    /**
     * Test of updateHistory method, of class TwseDbService.
     */
    @Test
    public void testUpdateHistory() {
        System.out.println("updateHistory");
        String stockId = "";
        TwseDbService instance = new TwseDbService();
        instance.updateHistory(stockId);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
