/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.hunterhope.twsedbsave.service.test;

import com.hunterhope.jsonrequest.exception.ServerMaintainException;
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
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.naming.spi.DirStateFactory;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 *
 * @author user
 */
public class TwseDbSaveServiceTest {

    //準備假物件
    private final JsonRequestService jrs = Mockito.mock(JsonRequestService.class);
    private final SaveDao saveDao = Mockito.mock(SaveDaoImpl.class);
    private final WaitClock waitClock = Mockito.mock(WaitClock.class);
    private final OneMonthPrice hasData;
    private final OneMonthPrice noData;

    public TwseDbSaveServiceTest() {
        hasData = new OneMonthPrice();
        hasData.setStat("ok");
        hasData.setData(List.of(List.of("114/03/03",
                "73,279,419",
                "74,076,141,634",
                "1,000.00",
                "1,020.00",
                "1,000.00",
                "1,020.00",
                "-20.00",
                "208,673")));
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

    private void mock_db_latestDate(String latestData) throws Exception {
        Mockito.when(saveDao.queryLatestDate(Mockito.any())).thenReturn(latestData);
    }

    private void verifyDaoCreateTable(int times) throws Exception {
        Mockito.verify(saveDao, Mockito.times(times)).createTable(Mockito.any());
    }

    private void verifyHttpRequest(int times) throws Exception {
        Mockito.verify(jrs, Mockito.times(times)).getData(Mockito.any(), Mockito.any());
    }

    private void verifyDaoSave(int times) throws Exception {
        Mockito.verify(saveDao, Mockito.times(times)).save(Mockito.any(), Mockito.any());
    }

    private void verifyWaitClockAction(int i) {
        Mockito.verify(waitClock, Mockito.times(i)).waitForSecurity(Mockito.anyInt(), Mockito.anyInt());
    }

    /**
     * 測試可假捕獲TwseDbSaveException例外
     */
    @Test
    public void teseUpdateHistory_throw_TwseDbSaveException() throws Exception {
        System.out.print("測試可假捕獲TwseDbSaveException例外:");
        //準備物件
        String stockId = "2323";
        TwseDbSaveService tds = new TwseDbSaveService(jrs, saveDao, waitClock);
        //模擬依賴行為
        Mockito.when(jrs.getData(Mockito.any(), Mockito.eq(OneMonthPrice.class))).thenThrow(ServerMaintainException.class);
        try {
            //跑起來
            tds.updateHistory(stockId);
            fail("沒產生例外");
        } catch (TwseDbSaveException ex) {
            System.out.println("成功");
        }
    }

    /**
     * 測試上網爬資料2個月
     */
    @Test
    public void testCrawl_two_month_data_real_send_request_2times() throws Exception {
        System.out.print("測試上網爬2個月資料，確實發出2次請求:");
        //準備物件
        String stockId = "2323";
        int months = 2;
        TwseDbSaveService tds = new TwseDbSaveService(jrs, saveDao, waitClock);
        //模擬依賴行為
        mock_request_hasData();
        //跑起來
        tds.crawl(stockId, LocalDate.now(), months);
        //驗證
        verifyHttpRequest(2);
        verifyWaitClockAction(1);
        System.out.println("成功");
    }

    /**
     * 測試上網查詢到資料後會存入資料庫
     */
    @Test
    public void testCrawl_hasData() throws Exception {
        System.out.print("測試上網爬取2個月有資料，資料庫確實存取2次:");
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
        System.out.println("成功");
    }

    /**
     * 測試上網查詢得不到該筆資料
     */
    @Test
    public void testCrawl_noData() throws Exception {
        System.out.print("測試上網爬取資料，回應無資料:");
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
            System.out.println("失敗");
        } catch (NotMatchDataException ex) {
            //驗證
            verifyDaoSave(0);
            System.out.println("成功");
        }
    }

    /**
     * Test of updateHistory method, of class TwseDbService.
     */
    @Test
    public void testUpdateHistoryForOneYear() throws Exception {
        System.out.print("測試更新歷史資料，更新直到網路沒資料:");
        //準備物件
        String stockId = "2323";
        UrlAndQueryString noDataQueryString = new UrlAndQueryString("https://www.twse.com.tw/rwd/zh/afterTrading/STOCK_DAY");
        noDataQueryString.addParam("stockNo", stockId);
        noDataQueryString.addParam("response", "json");
        noDataQueryString.addParam("date", "202312");
        TwseDbSaveService instance = new TwseDbSaveService(jrs, saveDao, waitClock);
        //模擬依賴行為
        mock_request_hasData();
        mock_request_noData(noDataQueryString);
        Mockito.when(saveDao.queryLastDate(Mockito.any())).thenReturn("113/12/23");
        //跑起來
        instance.updateHistory(stockId);
        //驗證
        verifyDaoSave(12);
        verifyWaitClockAction(12);
        System.out.println("成功");
    }

    @Test
    public void testUpdateToLatest_1_month() throws Exception {
        System.out.print("測試上網更新最新資料1個月(或是說當月份)");
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
        verifyWaitClockAction(0);
        System.out.println("成功");
    }

    @Test
    public void testUpdateToLatest_13_month() throws Exception {
        System.out.print("測試上網更新最新資料13個月:");
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
        verifyWaitClockAction(12);
        System.out.println("成功");
    }

    @Test
    public void testUpdateToLatest_2_month_but_first_month_no_data() throws Exception {
        System.out.print("測試上網更新最新資料2個月,但第一個月分無資料:");
        //準備物件
        String stockId = "2323";
        TwseDbSaveService instance = new TwseDbSaveService(jrs, saveDao, waitClock);
        //模擬依賴行為
        UrlAndQueryString noDataQueryString = new UrlAndQueryString("https://www.twse.com.tw/rwd/zh/afterTrading/STOCK_DAY");
        noDataQueryString.addParam("stockNo", stockId);
        noDataQueryString.addParam("response", "json");
        noDataQueryString.addParam("date", "202401");
        mock_request_hasData();
        mock_request_noData(noDataQueryString);
        mock_db_latestDate("112/12/23");
        //跑起來
        instance.updateToLatest(stockId, LocalDate.of(2024, 1, 30));
        //驗證
        verifyDaoSave(1);
        verifyWaitClockAction(1);
        System.out.println("成功");
    }

    @Test
    public void testCrawlLatestNoSave_2330_one_month_has() throws Exception {
        System.out.print("測試上網抓取最進2個月是否有資料,第一個月有資料:");
        //準備物件
        String stockId = "2330";
        TwseDbSaveService instance = new TwseDbSaveService(jrs, saveDao, waitClock);
        //模擬依賴行為
        mock_request_hasData();
        //跑起來
        Map<String, String> result = instance.crawlLatestNoSave(stockId);
        //驗證
        verifyDaoSave(0);
        verifyWaitClockAction(0);
        Assertions.assertFalse(result.isEmpty(), "第一個月應該要有資料");
        System.out.println("成功");
    }

    @Test
    public void testCrawlLatestNoSave_2330_second_month_has() throws Exception {
        System.out.print("測試上網抓取最進2個月是否有資料,第二個月才有資料:");
        //準備物件
        String stockId = "2330";
        TwseDbSaveService instance = new TwseDbSaveService(jrs, saveDao, waitClock);
        //模擬依賴行為
        mock_request_hasData();
        UrlAndQueryString noDataQueryString = new UrlAndQueryString("https://www.twse.com.tw/rwd/zh/afterTrading/STOCK_DAY");
        noDataQueryString.addParam("stockNo", stockId);
        noDataQueryString.addParam("response", "json");
        noDataQueryString.addParam("date", YearMonth.now().format(DateTimeFormatter.ofPattern("yyyyMM")));
        mock_request_noData(noDataQueryString);
        //跑起來
        Map<String, String> result = instance.crawlLatestNoSave(stockId);
        //驗證
        verifyDaoSave(0);
        verifyWaitClockAction(1);
        Assertions.assertFalse(result.isEmpty(), "應該要有資料");
        System.out.println("成功");
    }

    @Test
    public void testCrawlLatestNoSave_2330_no_data() throws Exception {
        System.out.print("測試上網抓取最進2個月是否有資料,都沒資料:");
        //準備物件
        String stockId = "2330";
        TwseDbSaveService instance = new TwseDbSaveService(jrs, saveDao, waitClock);
        //模擬依賴行為
        mock_request_noData(null);
        //跑起來
        try {
            instance.crawlLatestNoSave(stockId);
            Assertions.fail("沒有拋出例外");
        } catch (NotMatchDataException ex) {
            //驗證
            verifyDaoSave(0);
            verifyWaitClockAction(1);
            System.out.println("成功");
        }
    }
}
