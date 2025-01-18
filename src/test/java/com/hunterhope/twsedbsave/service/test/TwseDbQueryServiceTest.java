/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.service.test;

import com.hunterhope.twsedbsave.entity.StockEveryDayInfo;
import com.hunterhope.twsedbsave.service.TwseDbQueryService;
import com.hunterhope.twsedbsave.service.exception.TwseDbQueryException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author user
 */
public class TwseDbQueryServiceTest {

    @Test
    public void test_select_stock_all_info_no_table_exist() {
        System.out.print("測試查詢時表格不存在,拋出例外:");
        //測試物件
        TwseDbQueryService tdqs = new TwseDbQueryService();
        try {
            //跑起來
            List<StockEveryDayInfo> result = tdqs.selectAllDayInfo("2323");
            //驗證
            fail("沒有例外丟出");
        } catch (TwseDbQueryException ex) {
            System.out.println("成功");
        }
    }
}
