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
import org.springframework.jdbc.core.JdbcTemplate;

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
            instance.selectAllDayInfo(tableName,DataClassTemp.class);
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
    public void test_StockEveryDayInfo_has_data_convert_to_any_data_class() throws Exception {
        String dbName = "test_StockEveryDayInfo_convert_to_any_data_class.db";
        try {
            System.out.print("query時,有資料,可轉換成任意dataclass:");
            //準備物件
            String tableName = "stock_2323";
            JdbcTemplate jt = createDefData(dbName, tableName);
            //帶測物件
            QueryDaoImpl instance = new QueryDaoImpl(jt);
            //跑起來
            List<DataClassTemp> result = instance.selectAllDayInfo(tableName, DataClassTemp.class);
            //驗正
            Assertions.assertFalse(result.isEmpty(), "傳換成dataclass失敗");
            System.out.println("成功");
            System.out.println(result);
        } finally {
            //刪除資料庫
            Files.deleteIfExists(SQLITE_DB_ROOT_PATH.resolve(dbName));
        }

    }

    private JdbcTemplate createDefData(String dbName, String tableName) throws Exception {
        JdbcTemplate jt = createJdbcTemplate(dbName);
        //預設DB資料
        List<StockEveryDayInfo> data = createAnyYearTestDataForOneMonth();
        SaveDaoImpl saveDaoImpl = new SaveDaoImpl(jt);
        //建立表格
        saveDaoImpl.createTable(tableName);
        //建立預設資料
        saveDaoImpl.save(tableName, data);
        return jt;
    }
}
