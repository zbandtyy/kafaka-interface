package sample.rootlayout.view;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import sample.MainApp;
import sample.rootlayout.model.KafkaAdmin;
import sample.rootlayout.model.KafkaMsg;
import sample.rootlayout.model.KafkaMsgConsumer;
;import java.util.Random;
import java.util.concurrent.ExecutionException;

public class RootLayoutController {
    public Label appNameLabel;
    public ComboBox topicCombox;
    public Label topicLable;
    public TextField groupidTextField;

    public void setMain(MainApp main) {
        this.main = main;
    }

    private MainApp main;
    public Label portLabel;
    @FXML
    private Label ipLabel;
    @FXML
    private TextField ipTextField;
    @FXML
    private TextField portTextField;

    boolean enableCheckClicked = true;//响应过程中只进行一次
    @FXML
    private Button checkButton;
    

    @FXML
    private void connectButtonPress(){
        if(enableCheckClicked == false) {
            String url = ipTextField.getText() + ":" + portTextField.getText();
            String topic = (String) topicCombox.getSelectionModel().getSelectedItem();
            String groupid = groupidTextField.getText();
            KafkaMsgConsumer kafka = new KafkaMsgConsumer(url, topic, groupid);
            main.showVideoPage(kafka);
        }else{
            topicLable.setText("请先获取选择正确的主题！！！！");
        }
    }
    public void initialize(){
        Random r = new Random();

        groupidTextField.textProperty().setValue("app" + r.nextInt(10000) + r.nextInt(1000000));
    }
    @FXML
    private void checkButtonAction(MouseEvent mouseEvent) {
        //1.检查IP地址

         final String url =  checkIPPort();
        //2.使用模型进行连接，获取主题
         if(url != null){
             enableCheckClicked = false;
            try {

                Task<ObservableList<String>> task = new Task<ObservableList<String>>() {
                    KafkaAdmin admin;
                    @Override
                    protected ObservableList<String> call()  {
                        admin = new KafkaAdmin(url);
                        ObservableList<String> topics= null;
                        try {
                            topics = admin.getTopic();
                        } catch (Exception e) {
                            return null;
                        }
                        return topics;

                    }
                    @Override protected void succeeded() {
                        // Now update the customer
                        Platform.runLater(new Runnable() {
                            @Override public void run() {
                                try {
                                    ObservableList<String> r = get();
                                    if(r != null){
                                        topicCombox.setItems(r);
                                        if(r.size() == 0){
                                            topicLable.setText("该IP暂无主题,请更改IP");
                                            checkButton.setText("retry");
                                        }else {

                                            topicCombox.getSelectionModel().select(0);
                                            checkButton.setText("OK");
                                            topicLable.setText("请选择topic进行连接");
                                            enableCheckClicked = false;
                                        }
                                    }else {
                                        checkButton.setText("retry");
                                        topicLable.setText("无法连接，请重新输入IP和端口");
                                        enableCheckClicked = true;
                                    }
                                } catch (Exception e) {
                                    checkButton.setText("retry");
                                    topicLable.setText("无法连接，请重新输入IP和端口");
                                    enableCheckClicked = true;
                                }
                            }
                        });
                        admin.closeAdmin();
                    }
                };
                final Thread thread = new Thread(task , "kafka-query-topic");
                thread.setDaemon(true);
                thread.start();
             //   main.showVideoPage();
            } catch (Exception e) {
                topicLable.setText("无法连接到指定的IP，请确认");
                enableCheckClicked = true;
            }

        }else {
            return ;
        }


    }
    @FXML
    private String checkIPPort() {
        if(enableCheckClicked){

            ipLabel.setText("");
            portLabel.setText("");
            String ip = ipTextField.getText();
            String port = portTextField.getText();
          //  checkButton.setStyle();

            if(Utils.checkIp(ip) && Utils.checkPort(port))  {
                checkButton.setText("wait");
                topicLable.setText("请等待....");
                enableCheckClicked = false;
                return  ip+":"+port;
            }else if(!Utils.checkIp(ip)){
                ipLabel.setText("IP地址不合法");
            }else if(!Utils.checkPort(port)){
                portLabel.setText("端口号不合法");
            }

        }
        return  null;
    }
}