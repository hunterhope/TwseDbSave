/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.service;

import com.hunterhope.twsedbsave.other.WaitClock;
import com.hunterhope.jsonrequest.service.JsonRequestService;
import com.hunterhope.jsonrequest.service.UrlAndQueryString;
import com.hunterhope.jsonrequest.exception.DataClassFieldNameErrorException;
import com.hunterhope.jsonrequest.exception.NoInternetException;
import com.hunterhope.jsonrequest.exception.ResponseEmptyException;
import com.hunterhope.jsonrequest.exception.ServerMaintainException;
import com.hunterhope.twsedbsave.dao.SaveDao;
import com.hunterhope.twsedbsave.dao.impl.SaveDaoImpl;
import com.hunterhope.twsedbsave.entity.StockEveryDayInfo;
import static com.hunterhope.twsedbsave.entity.StockEveryDayInfo.combinTableName;
import com.hunterhope.twsedbsave.other.DBManager;
import com.hunterhope.twsedbsave.other.StringDateToLocalDateUS;
import com.hunterhope.twsedbsave.service.data.OneMonthPrice;
import com.hunterhope.twsedbsave.service.exception.NotMatchDataException;
import com.hunterhope.twsedbsave.service.exception.TwseDbSaveException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 此服務類任務是抓取網路TWSE網站上市股票交易資訊，並存入資料庫內<br>
 * 每次對網路發出請求會有5~10秒的延遲。<br>
 *
 * @author hunterhope
 */
public class TwseDbSaveService {

    public interface StepListener{
        void onStep(StepInfo stepInfo);
    }
    
    private final List<StepListener> listeners=new ArrayList<>();
    
    private final JsonRequestService jrs;
    private final SaveDao saveDao;
    private final String TWSE_STOCK_PRICE_BASE_URL = "https://www.twse.com.tw/rwd/zh/afterTrading/STOCK_DAY";//?date=20240331&stockNo=2323&response=json
    private final WaitClock waitClock;
    private final StringDateToLocalDateUS sdToLdUS;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMM");

    public TwseDbSaveService() {
        this.jrs = new JsonRequestService();
        this.saveDao = new SaveDaoImpl(DBManager.getJdbcTemplate());
        this.waitClock = new WaitClock();
        this.sdToLdUS = new StringDateToLocalDateUS();
    }

    public TwseDbSaveService(JsonRequestService jrs, SaveDao saveDao, WaitClock waitClock1) {
        this.jrs = jrs;
        this.saveDao = saveDao;
        this.waitClock = waitClock1;
        this.sdToLdUS = new StringDateToLocalDateUS();
    }

    /**
     * 加入監聽器.由於內部會有5~10秒延遲,可收到目前更新到哪裡.<br>
     * 當你不使用時記得要移除.
     */
    public void addListener(StepListener listener){
        synchronized(listeners){
            listeners.add(listener);
        }
    }
    /**
     * 移除監聽器.當你不使用時記得要移除,不然有可能造成記憶體洩漏(該回收沒有被回收).
     */
    public void removeListener(StepListener listener){
        synchronized(listeners){
            listeners.remove(listener);
        }
    }
    /**
     * 由於在多執行續下,所以任何通知要靠此方法才安全
     */
    private void notifyStep(StepInfo stepInfo){
        synchronized(listeners){
            for(StepListener listener:listeners){
                listener.onStep(stepInfo);
            }
        }
    }
    /**
     * 上網爬指定股票指定開始日期，爬指定幾個月份。<br>
     * 此方法會確保資料表存在，
     *
     * @param stockId 股票代碼
     * @param stateDate 開始日期
     * @param months 開始日期後爬幾個月(包含開始日期月份)
     * @throws TwseDbSaveException 包裝底層例外用
     * @throws NotMatchDataException
     * 查詢回來如果沒有符合的資料，有可能是此股票代號錯誤，或沒有該股票本月的交易紀錄，需要使用者自己處理
     */
    public void crawl(String stockId, LocalDate stateDate, int months) throws TwseDbSaveException, NotMatchDataException {
        String tableName = combinTableName(stockId);
        List<StockEveryDayInfo> data;
        UrlAndQueryString qs = createUrlAndQueryString(stockId);
        int lastTimeLoop = months - 1;
        String tempDate;
        boolean hasSaveDb=false;
        for (int i = 0; i < months; i++) {
            hasSaveDb=false;
            tempDate=stateDate.minusMonths(i).format(dateFormat);
            qs.addParam("date", tempDate);
            try {
                //上網爬資料
                OneMonthPrice omp = jrs.getData(qs, OneMonthPrice.class);
                //轉換成資料庫表格形式
                data = omp.convertToStockEveryDayInfo();
                //存入資料庫
                saveDao.save(tableName, data);
                hasSaveDb=true;
            } catch (NoInternetException | ServerMaintainException | DataClassFieldNameErrorException | ResponseEmptyException ex) {
                throw new TwseDbSaveException(ex);//讓使用者只須要認識這個例外就好
            } catch (NotMatchDataException ex) {
                if (i == lastTimeLoop) {//抓取次數等於最後一次時，才可以判定為沒有資料
                    throw ex;
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            //每次上網爬資料間隔5~10秒
            if (i != lastTimeLoop) {//只抓取1個月則不用等，最後一次不用等
                if(hasSaveDb){
                    notifyStep(new StepInfo(stockId,tempDate+"月份資料已存入資料庫,"+"查詢進入安全等待時間5~10秒"));
                }else{
                    notifyStep(new StepInfo(stockId,tempDate+"月份無資料,"+"查詢進入安全等待時間5~10秒"));
                }
                waitClock.waitForSecurity(5, 11);
            }
        }
    }

    public final UrlAndQueryString createUrlAndQueryString(String stockId) {
        //建立UrlAndQueryString
        UrlAndQueryString qs = new UrlAndQueryString(TWSE_STOCK_PRICE_BASE_URL);
        qs.addParam("stockNo", stockId);
        qs.addParam("response", "json");
        return qs;
    }

    /**
     * 自動更新歷史資料，到抓不到資料<br>
     * 使用此方法要自己確認資料表已經存在<br>
     */
    public void updateHistory(String stockId) throws TwseDbSaveException {
        updateStructure(() -> {
            //查詢出最後一筆資料日期(原則上每次抓取資料都是一個月一個月的)
            LocalDate lastDate = queryLastDate(stockId);
            //無限迴圈上網抓取資料，直到沒有資料
            do {
                crawl(stockId, lastDate, 1);
                notifyStep(new StepInfo(stockId,lastDate.format(dateFormat)+"月份資料已存入資料庫,查詢進入安全等待時間5~10秒"));
                lastDate = lastDate.minusMonths(1);
                waitClock.waitForSecurity(5, 11);
            } while (true);
        });
    }

    private LocalDate queryLastDate(String stockId) throws SQLException {
        return sdToLdUS.change(saveDao.queryLastDate(combinTableName(stockId)));
    }

    private LocalDate queryLatestDate(String stockId) throws SQLException {
        return sdToLdUS.change(saveDao.queryLatestDate(combinTableName(stockId)));
    }

    /**
     * 方便測試用,建議使用updateToLatest(String stockId) 使用此方法要自己確認資料表已經存在<br>
     */
    public void updateToLatest(String stockId, LocalDate nowDate) throws TwseDbSaveException {
        updateStructure(() -> {
            //取得資料庫內最新紀錄日期
            LocalDate dbLatestDate = queryLatestDate(stockId);
            //比對日期差異計算出要更新月份數
            long difMonths = ChronoUnit.MONTHS.between(YearMonth.from(dbLatestDate), YearMonth.from(nowDate)) + 1;//+1是因為dbLatestDate的月份也要抓取用來確保資料完整
            crawl(stockId, nowDate, (int) difMonths);
        });

    }

    private void updateStructure(UpdateAction action) throws TwseDbSaveException {
        try {
            action.run();
        } catch (NotMatchDataException ex) {
            //crawl丟出這例外,可以判定為更新結束
        } catch (SQLException ex) {//若產生SQL例外,應判定為城市邏輯錯誤,所以丟出RuntimeException
            throw new RuntimeException(ex);
        }
    }

    /**
     * 自動更新到最新資料<br>
     * 使用此方法要自己確認資料表已經存在<br>
     */
    public void updateToLatest(String stockId) throws TwseDbSaveException {
        updateToLatest(stockId, LocalDate.now());
    }

    private interface UpdateAction {

        void run() throws NotMatchDataException, SQLException, TwseDbSaveException;
    }
    
}
