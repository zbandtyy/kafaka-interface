package sample.rootlayout.model;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.mysql.CameraInfo;
import sample.mysql.TrackTableWrapper;
import sample.rootlayout.utils.Filter;
import sample.rootlayout.utils.TimeStampConverter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author ：tyy
 * @date ：Created in 2020/3/31 17:43
 * @description：显示查询的数据信息
 * @modified By：
 * @version: $
 */

public class TableViewData {


    Filter filter = null;
    public SimpleStringProperty vid = new SimpleStringProperty("");
    public  SimpleStringProperty time = new SimpleStringProperty("");
    public SimpleStringProperty vidInfo = new SimpleStringProperty("无");
    public static ObservableList<TableViewData> trackList = FXCollections.observableArrayList();//所有的查询数据库的轨迹记录集合


    //2.获取没有找到的camera的集合
    private  static   Set<String> emptyCameraSet = new HashSet<>();
    public TableViewData(String vid, String time) {
        this.vid.setValue(vid);
        this.time.setValue(time);
    }
    //存储摄像头标识，和摄像头信息
    public  static  Map<String, CameraInfo> vidMap = new HashMap<>();
    //解析得到的车牌数据存储 trackList中
    private  void getTrackMapConstruct(TrackTableWrapper tbw){
        emptyCameraSet.clear();
        if(tbw == null){
            return;
        }
        trackList.clear();//清空内部的内容
        // 传入的twb包含查询到车辆轨迹的所有内容
        Map<String, String> rdata = tbw.resolve();
        if(rdata == null){
            return;
        }

        for(String time:rdata.keySet()) {
            if(filter == null ||  filter.isValid(TimeStampConverter.fromString(time)) == true) {
                //过滤掉没在选择范围中的内容
                TableViewData tvd = new TableViewData(rdata.get(time), time);
                if (vidMap.get(rdata.get(time)) == null) {
                    emptyCameraSet.add(rdata.get(time));
                } else {
                    tvd.setVidInfo(vidMap.get(rdata.get(time)).getMessage());
                }
                trackList.add(tvd);
            }

        }
    }
    //添加过滤器的构造函数
    public TableViewData(TrackTableWrapper tbw ,Filter f ) {
        this.filter = f;
        getTrackMapConstruct(tbw);


    }
    //查询所有的vid集合
    public TableViewData(TrackTableWrapper tbw  ){
        getTrackMapConstruct(tbw);
    }
    public static Set<String> getEmptyCameraSet() {
        return emptyCameraSet;
    }

    public  void insertCameraData(Set<CameraInfo> cis){
        if(cis == null){
            return;
        }

        for( CameraInfo info :cis){
            vidMap.put(info.getCamerName(),info);
        }
        for(TableViewData track:trackList){
            if(track.vid.getValue().equals("无"))
                System.out.println(vidMap.get(track.vid.getValue()));
                track.setVidInfo(vidMap.get(track.vid.getValue()).getMessage());
        }
    }

    public void setVidInfo(String vidInfo) {
        this.vidInfo.set(vidInfo);
    }

    @Override
    public String toString() {
        return "TableViewData{" +
                "vid=" + vid +
                ", time=" + time +
                ", vidInfo=" + vidInfo +
                '}';
    }


}
