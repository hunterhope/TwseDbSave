/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.service;

import com.hunterhope.twsedbsave.dao.QueryDao;
import com.hunterhope.twsedbsave.entity.StockEveryDayInfo;
import com.hunterhope.twsedbsave.service.exception.TwseDbQueryException;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author user
 */
public class TwseDbQueryService {
    private QueryDao queryDao;

    public TwseDbQueryService() {
    }

    public TwseDbQueryService(QueryDao queryDao) {
        this.queryDao = queryDao;
    }
    
    public List<StockEveryDayInfo> selectAllDayInfo(String stockId) throws TwseDbQueryException{
        try{
            return queryDao.selectAllDayInfo(StockEveryDayInfo.combinTableName(stockId));
        }catch(SQLException ex){
            throw new TwseDbQueryException("表格不存在");
        }
    }
    
}
