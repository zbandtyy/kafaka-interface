package sample.rootlayout.model;

import com.google.gson.JsonSyntaxException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import scala.Tuple2;

import java.util.*;

import static org.opencv.imgcodecs.Imgcodecs.imencode;

/**
 * @author ：tyy
 * @date ：Created in 2020/3/16 15:08
 * @description：收的具体数据类型,进行解析 ，解析成key value
 * @modified By：
 * @version: $
 */
public class KafkaMsg {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);//加载opencv
    }
    //3-20  所有的振消息共用的队列
    private  ObservableList<Tuple2<String,VideoEventData>> resultData = FXCollections.observableArrayList();//接收到得数据
    public    KafkaMsg() {
        //数据显示过程
        resultData.addListener(new ListChangeListener<Tuple2<String, VideoEventData>>() {
            @Override
            public void onChanged(Change<? extends Tuple2<String, VideoEventData>> c) {
                boolean r = c.next();
                if(c.wasAdded()){
                    //1.进行显示 ，找到对应的界面的链表进行添加
                    List<? extends Tuple2<String, VideoEventData>> newedit = c.getAddedSubList();
                    for (Tuple2<String, VideoEventData> data:newedit){
                            //转交给控制器进行显示
                        VideoEventData ed = data._2;
                        if(ed == null){
                            VideoShowPageData.ImageorOtherInfo.add("not get data\n");
                        }
                        else if(ed.getImagebytes() == null){
                            VideoShowPageData.ImageorOtherInfo.add(ed + "get message but not have image\n");

                            continue;
                        }

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                //当data._1为空时，会失败，否则会将图片加入对应的小视频框图中
                                VideoShowPageData.setImageandInfo(ed, ed.getImagebytes());
                            }
                        });
                    }
                    resultData.clear();
                }
            }
        });
    }
    //private ObservableList<>  resolvedData //解析之后的数据存储的位置
    //解析接收到得所有数据 将rawData 变换为 resolveData  //将resolved Data 进行显示
    public  void resolve(Tuple2<String,String>income,boolean debug){
        //解析并且添加到了resultData中
        if(income._1 == null){//key值为null
            VideoShowPageData.ImageorOtherInfo.add(new Date() + ": (null" +"," + income._2 + ")\n");
            return;
        }
        VideoEventData ed = null;
        try {
             ed =  VideoEventData.fromJson(income._2);

        }catch (JsonSyntaxException e){
            VideoShowPageData.ImageorOtherInfo.add(new Date() + ": (null" +"," + income._2 + ")\n");
        }

        if(ed.getCameraId() == null && ed.getData() == null && ed.getTimestamp() == null){
            VideoShowPageData.ImageorOtherInfo.add(new Date() + ": The data is not satisfy VideoEvent data form!(" +income._1 + ","+ income._2 +")\n");
            return;
        }
        Tuple2<String, VideoEventData> outData = new Tuple2<String, VideoEventData>(income._1,ed);
        resultData.add(outData);


    }
    public    void resolvedatas(String key ,List<VideoEventData> datas){
        for (VideoEventData data : datas) {
            Tuple2<String, VideoEventData> outData = new Tuple2<String, VideoEventData>(key,data);
            resultData.add(outData);
        }

    }
    public    void resolvedata(String key ,VideoEventData data){

            Tuple2<String, VideoEventData> outData = new Tuple2<String, VideoEventData>(key,data);
            resultData.add(outData);



    }
}
