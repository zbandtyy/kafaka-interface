package sample.rootlayout.utils;

import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * @author ：tyy
 * @date ：Created in 2020/4/19 22:24
 * @description：处理时间过滤
 * @modified By：
 * @version: $
 */
public class TimeFilter implements Filter {
    Timestamp from;//起始日期
    Timestamp to;//结束日期
    public  TimeFilter(){
        from = null;
        to  = null;
    }
    public TimeFilter(Timestamp from,Timestamp to) {
        this.from = from;
        this.to = to;
    }
    public TimeFilter(LocalDate from,LocalDate to) {
        this.from = TimeStampConverter.fromLocalDate(from);
        this.to = TimeStampConverter.fromLocalDate(to);

    }

    public  boolean isValid(Timestamp ts){//是否在对应的范围内部

        if(from== null && to == null){
            return  true;
        }else{
            //比from先，比to 还要晚，那么肯定不在合理范围之类
            if(ts.before(from) || ts.after(to) ){
                return  false;
            }else{
                return  true;
            }

        }

    }

    @Override
    public boolean isValid(Object obj) {
        if(obj instanceof Timestamp){
            return isValid((Timestamp) obj);
        }

        return false;
    }
}