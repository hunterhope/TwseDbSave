/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.hunterhope.twsedbsave.service.test;

import com.hunterhope.jsonrequest.JsonRequestService;
import com.hunterhope.twsedbsave.service.TwseDbService;
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
     * Test of crawl method, of class TwseDbService.
     */
    @Test
    public void testCrawl_two_month_data_real_send_request_2times() throws Exception {
        System.out.println("crawl two month data");        
        //準備假物件
        JsonRequestService jrs=Mockito.mock(JsonRequestService.class);
        //準備物件
        String stockId = "2323";
        int months = 2; 
        TwseDbService tds = new TwseDbService(jrs);
        //跑起來
        tds.crawl(stockId, months);
        //驗證
        Mockito.verify(jrs, Mockito.times(2)).getData(Mockito.any(), Mockito.any());
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
