package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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

    public void showVideoPage(KafkaMsgConsumer kafkaMsgConsumer){
        //1.显示主界面
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("rootlayout/view/MaintainInterface.fxml"));
        AnchorPane page = null;
        try {

            page = (AnchorPane) loader.load();
            Scene scene = new Scene(page);
            primaryStage.titleProperty().bind(kafkaMsgConsumer.getAllConfig());
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //2.创建后台线程处理kafka数据
        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call()  {
                kafkaMsgConsumer.loadProperties();
                kafkaMsgConsumer.consumer();
                return  0;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();



    }

    @Override
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
