/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.service;

import com.hunterhope.jsonrequest.JsonRequestService;
import com.hunterhope.jsonrequest.UrlAndQueryString;
import com.hunterhope.jsonrequest.exception.DataClassFieldNameErrorException;
import com.hunterhope.jsonrequest.exception.NoInternetException;
import com.hunterhope.jsonrequest.exception.ServerMaintainException;
import com.hunterhope.twsedbsave.dao.SaveDao;
import com.hunterhope.twsedbsave.entity.StockEveryDayInfo;
import com.hunterhope.twsedbsave.service.data.OneMonthPrice;
import com.hunterhope.twsedbsave.service.exception.TwseDbSaveException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author user
 */
public class TwseDbService {

    private final JsonRequestService jrs;
    private final SaveDao saveDao;
    private final String TWSE_STOCK_PRICE_BASE_URL = "https://www.twse.com.tw/rwd/zh/afterTrading/STOCK_DAY";//?date=20240331&stockNo=2323&response=json

    public TwseDbService() {
        this.jrs = new JsonRequestService();
        this.saveDao = null;
    }

    public TwseDbService(JsonRequestService jrs,SaveDao saveDao) {
        this.jrs = jrs;
        this.saveDao = saveDao;
    }

    public void crawl(String stockId, int months) throws TwseDbSaveException {
        //建立UrlAndQueryString
        UrlAndQueryString qs = new UrlAndQueryString(TWSE_STOCK_PRICE_BASE_URL);
        qs.addParam("stockNo", stockId);
        qs.addParam("response", "json");
        for (int i = 0; i < months; i++) {
            qs.addParam("date", LocalDate.now().minusMonths(i).format(DateTimeFormatter.BASIC_ISO_DATE));
            try {
                //上網爬資料
                Optional<OneMonthPrice> opt = jrs.getData(qs, OneMonthPrice.class);
                if (opt.isPresent()) {
                    //轉換成資料庫表格形式
                    List<StockEveryDayInfo> data=convert(opt.get());
                    //建立表格
                    saveDao.createTable("");
                    //存入資料庫
                    saveDao.save("", data);
                }

            } catch (NoInternetException | ServerMaintainException | DataClassFieldNameErrorException ex) {
                throw new TwseDbSaveException(ex);
            }
        }

    }

    private List<StockEveryDayInfo> convert(OneMonthPrice omp) {
        return omp.getData().stream()
                .map(items -> {
                    StockEveryDayInfo obj = new StockEveryDayInfo();
                    obj.setDate(items.get(0));
                    obj.setVolume(items.get(1));
                    obj.setOpen(items.get(3));
                    obj.setHight(items.get(4));
                    obj.setLow(items.get(5));
                    obj.setClose(items.get(6));
                    obj.setPriceDif(items.get(7));
                    return obj;
                })
                .collect(Collectors.toList());
        
    }

    public void updateHistory(String stockId) {
    }
}
