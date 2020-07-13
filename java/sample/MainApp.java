package sample;

import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.rootlayout.model.KafkaMsgConsumer;
import sample.rootlayout.view.MaintainInterfaceController;
import sample.rootlayout.view.RootLayoutController;

import java.io.IOException;

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
            MaintainInterfaceController mic = loader.getController();
            mic.heightProperty().addListener((observableValue,oldvalue,newvalue)->{primaryStage.setHeight(newvalue.intValue());});
           mic.setConsumer(kafkaMsgConsumer);
            Scene scene = new Scene(page);
            primaryStage.titleProperty().bind(kafkaMsgConsumer.getAllConfig());
            //设置返回登录界面重新连接的示图
            //Thread finalRcvDataThread = rcvDataThread;
//            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//                @Override
//                public void handle(WindowEvent event) {
//                    if(finalRcvDataThread != null){
//                        finalRcvDataThread.interrupt();
//                    }
//                    Platform.runLater(()->   showRootLayout());
//
//                }
//            });
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
      //  showVideoPage(new KafkaMsgConsumer("115.157.201.215:9092","video-fix","app"));
       primaryStage.show();
    }




    public static void main(String[] args) {
        launch(args);
    }
}
