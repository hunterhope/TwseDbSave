/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.dao.impl.test;

import com.hunterhope.twsedbsave.dao.impl.QueryDaoImpl;
import com.hunterhope.twsedbsave.dao.impl.SaveDaoImpl;
import static com.hunterhope.twsedbsave.dao.impl.test.other.UtilityForTest.SQLITE_DB_ROOT_PATH;
import static com.hunterhope.twsedbsave.dao.impl.test.other.UtilityForTest.createAnyYearTestDataForOneMonth;
import static com.hunterhope.twsedbsave.dao.impl.test.other.UtilityForTest.createJdbcTemplate;
import com.hunterhope.twsedbsave.entity.StockEveryDayInfo;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author user
 */
public class QueryDaoImplTest {

    @Test
    public void test_select_all_then_table_not_exist() throws Exception {
        String dbName = "test_select_all_then_table_not_exist.db";
        try {
            System.out.print("query時，無該表格會拋出例外:");
            //準備物件
            String tableName = "stock_2323";

            QueryDaoImpl instance = new QueryDaoImpl(createJdbcTemplate(dbName));
            //跑起來
            instance.selectAllDayInfo(tableName);
            //驗證，可以正確執行完畢
            fail("沒有丟出例外");
        } catch (SQLException ex) {
            //驗證
            System.out.println("成功");;
        } finally {
            //刪除資料庫
            Files.deleteIfExists(SQLITE_DB_ROOT_PATH.resolve(dbName));
        }
    }

    @Test
    public void test_select_all_has_data() throws Exception {
        String dbName = "test_select_all_has_data.db";
        try {
            System.out.print("query時，有資料:");
            //準備物件
            String tableName = "stock_2323";
            //預設DB資料
            List<StockEveryDayInfo> data = createAnyYearTestDataForOneMonth();
            SaveDaoImpl saveDaoImpl = new SaveDaoImpl(createJdbcTemplate(dbName));
            //建立表格
            saveDaoImpl.createTable(tableName);
            //建立預設資料
            saveDaoImpl.save(tableName, data);
            //帶測物件
            QueryDaoImpl instance = new QueryDaoImpl(createJdbcTemplate(dbName));
            //跑起來
            List<StockEveryDayInfo> result = instance.selectAllDayInfo(tableName);
            //驗證
            Assertions.assertFalse(result.isEmpty(),"資料庫應該要有資料");
            System.out.println("成功");
        } finally {
            //刪除資料庫
            Files.deleteIfExists(SQLITE_DB_ROOT_PATH.resolve(dbName));
        }
    }
}
