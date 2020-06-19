package sample.mysql;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：tyy
 * @date ：Created in 2020/3/31 1:33
 * @description：封装获取的数据库内容属性
 * @modified By：
 * @version: $
 */
public class TrackTableWrapper {

    public String getPlateStr() {
        return plateStr.get();
    }

    public SimpleStringProperty plateStrProperty() {
        return plateStr;
    }

    public String getCameraSeq() {
        return cameraSeq.get();
    }

    public SimpleStringProperty cameraSeqProperty() {
        return cameraSeq;
    }

    private static SimpleStringProperty plateStr = new SimpleStringProperty();
    private  static SimpleStringProperty cameraSeq = new SimpleStringProperty();//最长为64KB，受限于TEXT的大小

    public TrackTable getTb() {
        return tb;
    }

    private TrackTable tb;
    public TrackTableWrapper(TrackTable res){
        plateStr.setValue("");
        cameraSeq.setValue("");
        tb = res;
        if(res != null) {
            plateStr.setValue(res.getPlateStr());
            cameraSeq.setValue(res.getCameraSeq());
        }
    }
    public Map<String,String> resolve(){//获取拿到的车牌轨迹集合
        return TrackTable.resolveTrace(tb);
    }


}
