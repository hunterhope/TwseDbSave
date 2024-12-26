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
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
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
    private final JsonRequestService jrs = Mockito.mock(JsonRequestService.class);
    private final SaveDao saveDao = Mockito.mock(SaveDaoImpl.class);
    private final WaitClock waitClock = Mockito.mock(WaitClock.class);
    private final OneMonthPrice hasData;
    private final OneMonthPrice noData;

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

    private void mock_db_latestDate(String latestData) throws Exception{
        Mockito.when(saveDao.queryLatestDate(Mockito.any())).thenReturn(latestData);
    }

    private void verifyDaoCreateTable(int times) throws Exception{
        Mockito.verify(saveDao, Mockito.times(times)).createTable(Mockito.any());
    }

    private void verifyHttpRequest(int times) throws Exception {
        Mockito.verify(jrs, Mockito.times(times)).getData(Mockito.any(), Mockito.any());
    }

    private void verifyDaoSave(int times) throws Exception{
        Mockito.verify(saveDao, Mockito.times(times)).save(Mockito.any(), Mockito.any());
    }

    /**
     * 測試當資料庫存入相同資料產生主鍵重複的情況,會執行排除重複資料動作在存入資料庫
     */
    @Test
    public void testCrawl_has_duplicate_data()throws Exception{
        System.out.print("測試資料庫發生資料重複例外,有執行排除後再存入動作:");
        //準備物件
        String stockId = "2323";
        int months = 1;
        TwseDbSaveService tds = new TwseDbSaveService(jrs, saveDao, waitClock);
        //模擬依賴行為
        mock_request_hasData();
        Mockito.when(saveDao.save(Mockito.any(), Mockito.any())).thenThrow(SQLIntegrityConstraintViolationException.class).thenReturn(new int[]{});
        //跑起來
        tds.crawl(stockId, LocalDate.now(), months);
        //驗證
        verifyDaoSave(2);
        System.out.println("成功");
    }
    /**
     * 測試第一次抓取到網路資料,要存入資料庫卻沒有表格的狀態
     */
    @Test
    public void testCrawl_not_exist_table()throws Exception{
        System.out.print("測試第一次抓取到網路資料,要存入資料庫卻沒有表格的狀態:");
        //準備物件
        String stockId = "2323";
        int months = 2;
        TwseDbSaveService tds = new TwseDbSaveService(jrs, saveDao, waitClock);
        //模擬依賴行為
        mock_request_hasData();
        Mockito.when(saveDao.save(Mockito.any(), Mockito.any())).thenThrow(SQLSyntaxErrorException.class).thenReturn(new int[]{});
        //跑起來
        tds.crawl(stockId, LocalDate.now(), months);
        //驗證
        verifyDaoCreateTable(1);
        verifyDaoSave(3);//因為是抓取2個月資料,所以會發生3次存取
        System.out.println("成功");
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
        System.out.println("成功");
    }

}
