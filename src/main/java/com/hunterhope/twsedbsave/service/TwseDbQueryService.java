/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.service;

import com.hunterhope.twsedbsave.dao.QueryDao;
import com.hunterhope.twsedbsave.entity.StockEveryDayInfo;
import com.hunterhope.twsedbsave.service.exception.TwseDbQueryException;
import java.util.List;

/**
 *
 * @author user
 */
public class TwseDbQueryService {
    private QueryDao queryDao;
    
    public List<StockEveryDayInfo> selectAllDayInfo(String string) throws TwseDbQueryException{
        throw new TwseDbQueryException("表格不存在");
    }
    
}
