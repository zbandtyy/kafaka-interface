package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import sample.rootlayout.model.KafkaAdmin;
import sample.rootlayout.model.KafkaMsgConsumer;
import sample.rootlayout.model.VideoShowPageData;
import sample.rootlayout.view.RootLayoutController;
import sample.rootlayout.view.VideoAndLabelController;
import sample.rootlayout.view.VideoShowPageController;

import java.io.IOException;

import static java.lang.Math.random;

public class MainApp extends Application {
    private static Stage primaryStage;
    private AnchorPane rootLayout;

    public  static  void resizeWidth(int width){
        primaryStage.setWidth(width);
    }
    public void showRootLayout(){
        try {
            // 加载FXNL的RootLayout
            FXMLLoader loader = new FXMLLoader();
            System.out.println(MainApp.class.getResource("rootlayout/view/RootLayout.fxml").getPath());
            loader.setLocation(MainApp.class.getResource("rootlayout/view/RootLayout.fxml"));
            rootLayout = (AnchorPane) loader.load();
            // 显示包含rootLayout的scene.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            //给控制权给ROOTLAYOUT 以便显视频界面
            RootLayoutController controller = loader.getController();
            controller.setMain(this);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //显示读取并且显示数据的页面，并且创建后台线程读取消息
    public void showVideoPage(KafkaMsgConsumer kafkaMsgConsumer){
        //1.创建后台线程处理kafka数据
        Thread rcvDataThread = null;
        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call()  {
                kafkaMsgConsumer.loadProperties();
                kafkaMsgConsumer.consumer();
                return  0;
            }
        };
        rcvDataThread = new Thread(task);
        rcvDataThread.setDaemon(true);
        rcvDataThread.start();

        //2.显示主界面,并且对主界面进行设置
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("rootlayout/view/MaintainInterface.fxml"));

        AnchorPane page = null;
        try {

            page = (AnchorPane) loader.load();
            Scene scene = new Scene(page);
            primaryStage.titleProperty().bind(kafkaMsgConsumer.getAllConfig());

            Thread finalRcvDataThread = rcvDataThread;
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    if(finalRcvDataThread != null){
                        finalRcvDataThread.interrupt();
                    }
                    Platform.runLater(()->   showRootLayout());

                }
            });
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    //显示登入kafka的界面
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Kafka-tyy-test");
        System.out.println(MainApp.class.getResource("rootlayout").getPath());
        this.primaryStage.getIcons().add(new Image("kafka-logo.png"));
        showRootLayout();
        //showVideoPage(new KafkaMsgConsumer("192.168.0.110:9092","video-stream-large","app2"));
       //primaryStage.show();
    }




    public static void main(String[] args) {
        launch(args);
    }
}
