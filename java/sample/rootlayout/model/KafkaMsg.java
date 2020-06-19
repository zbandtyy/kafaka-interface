package sample.rootlayout.model;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import sample.rootlayout.view.VideoAndLabelController;
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
                        Mat frame = new Mat(ed.getRows(), ed.getCols(), ed.getType());
                        frame.put(0, 0, Base64.getDecoder().decode(ed.getData()));
                        MatOfByte mob = new MatOfByte();
                        imencode(".jpg", frame, mob);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                VideoShowPageData.setImage(data._1, mob.toArray());
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
    public  void resolve(Tuple2<String,String>income){
        //解析并且添加到了resultData中
            Tuple2<String, VideoEventData> outData = new Tuple2<String, VideoEventData>(income._1,VideoEventData.fromJson(income._2));
            resultData.add(outData);


    }

}
