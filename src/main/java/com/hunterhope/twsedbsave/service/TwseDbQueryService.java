/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.service;

import com.hunterhope.twsedbsave.dao.QueryDao;
import com.hunterhope.twsedbsave.dao.impl.QueryDaoImpl;
import com.hunterhope.twsedbsave.entity.StockEveryDayInfo;
import com.hunterhope.twsedbsave.other.DBManager;
import com.hunterhope.twsedbsave.service.exception.TwseDbQueryException;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author user
 */
public class TwseDbQueryService {
    private final QueryDao queryDao;

    public TwseDbQueryService() {
        queryDao=new QueryDaoImpl(DBManager.getJdbcTemplate());
    }

    public TwseDbQueryService(QueryDao queryDao) {
        this.queryDao = queryDao;
    }
    
    public <T> List<T> selectAllDayInfo(String stockId,Class<T> dataClass) throws TwseDbQueryException{
        try{
            return queryDao.selectAllDayInfo(StockEveryDayInfo.combinTableName(stockId),dataClass);
        }catch(SQLException ex){
            throw new TwseDbQueryException("表格不存在");
        }catch(Exception e){
            throw new TwseDbQueryException(e);
        }
    }
    
}
