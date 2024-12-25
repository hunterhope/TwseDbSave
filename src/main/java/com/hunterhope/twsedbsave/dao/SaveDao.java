/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.hunterhope.twsedbsave.dao;

import com.hunterhope.twsedbsave.entity.StockEveryDayInfo;
import java.util.List;

/**
 * Dao設計中的存取介面<br>
 * 因為將一支股票用一個別表格保存日成交資訊，所以每次都需要指定表格名稱<br>
 * @author hunterhope
 */
public interface SaveDao {
    /**
     * 將取得的一整個月資訊存入資料庫<br>
     * 更新歷史資訊使用此方法會比較有效率<br>
     * @param tableName 表格名稱
     * @param data 每一列的資料
     * @return 是否成功異動
     */
    int[] save(String tableName,List<StockEveryDayInfo> data);
  
    /**
     * 資料庫一開始沒有任何資料表，要等到使用者查詢才會建立<br>
     * 建立資料表
     * @param tableName 資料表名稱
     */
    void createTable(String tableName);
    
    /**
     * 取得資料庫內最後一筆交易紀錄日期
     */
    String queryLastDate(String tableName);
    
    /**
     * 取得資料庫內最新一筆交易紀錄日期
     */
    String queryLatestDate(String tableName);
}
