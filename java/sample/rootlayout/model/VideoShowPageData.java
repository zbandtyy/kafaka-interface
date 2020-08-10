package sample.rootlayout.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import sample.rootlayout.view.VideoAndLabelController;
import scala.Tuple2;

import java.io.ByteArrayInputStream;

/**
 * @author ：tyy
 * @date ：Created in 2020/3/19 20:52
 * @description：视频显示界面的数据结构，包含一个对小视频列表的更新，以及一个大视频的管理的视频控件
 *  显示视频公用的界面
 * @modified By：
 * @version: $
 */

public class VideoShowPageData {
    //全局只有一个页面，所以只需要一个，
    public    static  SimpleStringProperty selection = new SimpleStringProperty("");
    private   static ObservableList<Tuple2<SimpleStringProperty, VideoAndLabelController>>  smallVideoDataList =
            FXCollections.observableArrayList();
    //存储大图片视频的队列
    public    static ObservableList<Image> bigVideo  = FXCollections.observableArrayList();
    public  static  SimpleStringProperty bigVideoInfo = new SimpleStringProperty();
    public    static ObservableList<String> ImageorOtherInfo  = FXCollections.observableArrayList();//标签信息 全部显示

    public static  ObservableList<Tuple2<SimpleStringProperty, VideoAndLabelController>> getglobalList(){
        return smallVideoDataList;
    }
    public static  void addList(String text){
        smallVideoDataList.add(new Tuple2<SimpleStringProperty, VideoAndLabelController>(
                new SimpleStringProperty(text),VideoAndLabelController.getInstance()));
    }
    //查找与text相同的controller
    private static VideoAndLabelController findController(String text){
        if(text == null || text.isEmpty() || smallVideoDataList.size() == 0){
            return null;
        }
        for(Tuple2<SimpleStringProperty,VideoAndLabelController> data:smallVideoDataList){
            if(data._1.get().equals(text)){
                return data._2;
            }
        }
        return  null;
    }
    //无论如何都给你一个控制器，创建或者查找一个与text具有相同标签的 控制器（界面）
    private static  VideoAndLabelController getController(String text){
        if(text.isEmpty() || text == null){
            return  null;
        }
        VideoAndLabelController valc = findController(text);
        if( valc == null){
            final VideoAndLabelController label = VideoAndLabelController.getInstance();
            smallVideoDataList.add(new Tuple2<SimpleStringProperty, VideoAndLabelController>(new SimpleStringProperty(text),label));
            return label;
        }else {
            return  valc;
        }
    }
    //对list中的图像进行设置
    public  static  boolean  setImageandInfo(VideoEventData data,byte[] img){
        String text = data.getCameraId();
        VideoAndLabelController valc =  getController(text);
        //仅在text为空时失败
        if(valc == null){
            return false;
        }
        valc.getImgList().add(new Image(new ByteArrayInputStream(img)));
        valc.setInfo(data.toString());
        return  true;
    }




}
