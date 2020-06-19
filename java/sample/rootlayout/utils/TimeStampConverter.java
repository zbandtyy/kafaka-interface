package sample.rootlayout.utils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * @author ：tyy
 * @date ：Created in 2020/4/19 22:25
 * @description：将LocalDate以及String转换为TimeStamp
 * @modified By：
 * @version: $
 */
public class TimeStampConverter {
    public  static Timestamp fromString(String time){

         return Timestamp.valueOf(time);
    }
    public static Timestamp fromLocalDate(LocalDate localDate){

        long timestamp = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return new Timestamp(timestamp);

    }

    public static void main(String[] args) {
       //TimeStampConverter.fromLocalDate();
    }
}
