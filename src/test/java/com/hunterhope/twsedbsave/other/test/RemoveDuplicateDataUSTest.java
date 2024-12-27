/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.hunterhope.twsedbsave.other.test;

import com.hunterhope.twsedbsave.dao.SaveDao;
import com.hunterhope.twsedbsave.dao.impl.SaveDaoImpl;
import com.hunterhope.twsedbsave.entity.StockEveryDayInfo;
import com.hunterhope.twsedbsave.other.RemoveDuplicateDataUS;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;

/**
 *
 * @author user
 */
public class RemoveDuplicateDataUSTest {

    private final SaveDao saveDao = Mockito.mock(SaveDaoImpl.class);

    public RemoveDuplicateDataUSTest() {
    }

    /**
     * 測試移除重複日期的邏輯正確 無重複日期
     */
    @Test
    public void testAction_not_remove() throws Exception {
        System.out.print("測試移除重複日期的邏輯正確 無重複日期:");
        //準備物件
        String tableName = "";
        StockEveryDayInfo day1 = new StockEveryDayInfo();
        day1.setDate("113/12/02");
        StockEveryDayInfo day2 = new StockEveryDayInfo();
        day2.setDate("113/12/07");
        List<StockEveryDayInfo> data = new ArrayList<>();
        data.add(day1);
        data.add(day2);
        RemoveDuplicateDataUS instance = new RemoveDuplicateDataUS();
        //模擬依賴
        Mockito.when(saveDao.queryDates(Mockito.any(), Mockito.any())).thenReturn(List.of("113/12/05"));
        //跑起來
        instance.action(tableName, saveDao, data);
        //驗證
        List<StockEveryDayInfo> exp = List.of(day1, day2);
        assertEquals(exp, data);
        System.out.println("成功");
    }
    
    /**
     * 測試移除重複日期的邏輯正確 有重複日期
     */
    @Test
    public void testAction_has_remove() throws Exception {
        System.out.print("測試移除重複日期的邏輯正確 有重複日期:");
        //準備物件
        String tableName = "";
        StockEveryDayInfo day1 = new StockEveryDayInfo();
        day1.setDate("113/12/02");
        StockEveryDayInfo day2 = new StockEveryDayInfo();
        day2.setDate("113/12/07");
        List<StockEveryDayInfo> data = new ArrayList<>();
        data.add(day1);
        data.add(day2);
        RemoveDuplicateDataUS instance = new RemoveDuplicateDataUS();
        //模擬依賴
        Mockito.when(saveDao.queryDates(Mockito.any(), Mockito.any())).thenReturn(List.of("113/12/02"));
        //跑起來
        instance.action(tableName, saveDao, data);
        //驗證
        List<StockEveryDayInfo> exp = List.of(day2);
        assertEquals(exp, data);
        System.out.println("成功");
    }

}
