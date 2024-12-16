/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.hunterhope.twsedbsave.service.test;

import com.hunterhope.twsedbsave.service.TwseDbService;
import com.hunterhope.twsedbsave.service.exception.TwseDbSaveException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
    public void testCrawl() throws TwseDbSaveException {
        System.out.println("crawl");
        String stockId = "2323";
        int months = 1;       
        TwseDbService instance = new TwseDbService();
        instance.crawl(stockId, months);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
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
