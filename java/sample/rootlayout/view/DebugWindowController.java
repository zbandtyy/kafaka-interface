package sample.rootlayout.view;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.opencv.video.Video;
import sample.rootlayout.model.*;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author ：tyy
 * @date ：Created in 2020/7/5 7:02
 * @description：显示底层调试界面的
 * @modified By：内存不够的多模式存储的问题
 * @version: $
 */
public class DebugWindowController{

    public Label infoLable;
    public TextField kconsumerLine;
    public Button nxtbtn;
    public Button ptrbtn;
    public Button stpbtn;
    public Button ctnbtn;
    public Button okbtn;
    private SimpleStringProperty info = new SimpleStringProperty();
    private KafkaMsgConsumer mainRcvData = null;//正常执行线程的数据
    Thread rcvDataThread = null;
    KafkaMsgConsumer kafkaMsgConsumer = null;//调试接收线程的数据
    public void setMainRcvData(KafkaMsgConsumer mainRcvData) {
        this.mainRcvData = mainRcvData;
    }
    public Thread getBatchDataThread = null;
    public Task<ArrayList<VideoEventData>> getBatchDataTask =  null;
    private KafkaMsg km = new KafkaMsg();

    void disableBtn(){
        nxtbtn.setDisable(true);
        ptrbtn.setDisable(true);
        stpbtn.setDisable(true);
        okbtn.setDisable(true);
        ctnbtn.setDisable(true);
    }
    void enableBtn(){
        nxtbtn.setDisable(false);
        ptrbtn.setDisable(false);
        stpbtn.setDisable(false);
        okbtn.setDisable(false);
        ctnbtn.setDisable(false);
    }
    private void startThreadToGetBatchData(){
        index = 0;
       getBatchDataTask =  new Task<ArrayList<VideoEventData>>() {
            @Override
            protected ArrayList<VideoEventData> call() {
                ArrayList<VideoEventData> result = null;
                Platform.runLater(() -> {
                    disableBtn();
                });
                result = DebugBufferQueue.getBatchData();
                km.resolvedatas("DEBUG", result);
                return result;
            }
            @Override protected void succeeded() {
                try {
                    history = this.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    enableBtn();
                });
            }

        };
        getBatchDataThread = new Thread(getBatchDataTask,"debug-task");
        getBatchDataThread.setDaemon(true);
        getBatchDataThread.start();

    }
    public void initialize(){
        infoLable.textProperty().bind(info);
    }
    //创建消费者线程,接收20组数据缓存 | 一批数据
    public void confirmBtn(MouseEvent mouseEvent) throws ExecutionException, InterruptedException {
        if(mouseEvent.getClickCount() == 1) {
            String text = kconsumerLine.getText();
            String[] infos = text.split(":");
            if(infos.length < 5) {
                info.setValue("填写格式必须为IP:port:groupid:partition id:offset");
                return;
            }
            System.out.println(text);
            String ip = new String(infos[0] +":"+ infos[1]).trim();
            String topic = infos[2].trim();
            String groupid = infos[3].trim();
            int partition = Integer.parseInt(infos[4].trim());
            int offset = Integer.parseInt(infos[5].trim());

//            if(mainRcvData != null && !mainRcvData.isFinished()){
//                info.setValue("稍等2s后再重试");
//                return;
//            }

            kafkaMsgConsumer = new KafkaMsgConsumer(ip, topic, groupid);


            Task<Integer> task = new Task<Integer>() {
                @Override
                protected Integer call()  {
                    kafkaMsgConsumer.loadProperties();
                    kafkaMsgConsumer.consumerPartiton(partition, offset);
                    return  0;
                }
            };
            rcvDataThread = new Thread(task);
            rcvDataThread.setDaemon(true);
            rcvDataThread.start();
            startThreadToGetBatchData();
            info.setValue("中断原始线程并且开始新的调试线程");
        }
    }
    //继续接受下一10帧,进行分析
    public void continueBtn(MouseEvent mouseEvent) throws ExecutionException, InterruptedException {
        if(getBatchDataThread.isAlive()){

            info.setValue("正在获取数据，请稍后重试按钮");
            return;
        } else {
            startThreadToGetBatchData();
        }
        if(history == null){
            info.setValue("请等待数据接受");
            return;
        }
        System.out.println("这批数据的大小为" + history.size());
    }
    //结束改过程，调试下一分区
    public void stopBtn(MouseEvent mouseEvent) {
        kafkaMsgConsumer.setExitConsumer(true);
        rcvDataThread.interrupt();
        info.setValue("已经结束线程");
    }
    //根据缓存数据进行上一针
    public void nextBtn(MouseEvent mouseEvent) {
        if(history == null){
            info.setValue("没有接收到数据");
            return;
        }
        VideoEventData d = getNext();
        if(history == null){
            return;
        }
        if(d == null){
            info.setValue("没有下一张了,数据数量为"+history.size());
        }else {
            km.resolvedata("DEBUG",d);
            info.setValue("当前位值为该批为："+index);
        }

    }
    //根据缓存数据下一帧
    public void previoudBtn(MouseEvent mouseEvent) {
        if(history == null){
            index = 0;
            info.setValue("没有接收到数据");
            return;
        }
        VideoEventData d = getPrevious();
        if(d == null){
            info.setValue("没有上一张了");
        }else {
            km.resolvedata("DEBUG",d);
            info.setValue("当前位值为该批为："+index);
        }


    }
    private   ArrayList<VideoEventData> history = null;//用于保存收到的数据
    private  static int index = 0;
    public    VideoEventData getNext(){
        index ++;
        if(index < 0 || index >= history.size()){
            index = 0;
            return null;
        }
        VideoEventData d = history.get(index);

        return  d;

    }
    public    VideoEventData getPrevious(){
        index --;
        if(index < 0 || index >= history.size()){
            return null;
        }

        VideoEventData d = history.get(index );
        return d;
    }

}
