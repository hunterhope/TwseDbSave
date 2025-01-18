/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.dao.impl.test;

import com.hunterhope.twsedbsave.dao.impl.QueryDaoImpl;
import static com.hunterhope.twsedbsave.dao.impl.test.other.UtilityForTest.SQLITE_DB_ROOT_PATH;
import static com.hunterhope.twsedbsave.dao.impl.test.other.UtilityForTest.createJdbcTemplate;
import java.nio.file.Files;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author user
 */
public class QueryDaoImplTest {
    @Test
    public void test_select_all_then_table_not_exist()throws Exception{
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
}
