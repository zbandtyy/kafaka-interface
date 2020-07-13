package sample.rootlayout.view;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import sample.MainApp;
import sample.rootlayout.model.DataBase;
import sample.rootlayout.model.KafkaMsgConsumer;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Random;

/**
 * @author ：tyy
 * @date ：Created in 2020/3/21 23:26
 * @description：主要的界面管理
 * @modified By：
 * @version: $
 */
public class MaintainInterfaceController {


    public BorderPane mainBorderPane;
    public AnchorPane firstAnchorPane;
    public MenuItem debugItem;
    public HBox bottomAnchor;
    private SimpleIntegerProperty height = new SimpleIntegerProperty(800);



    private KafkaMsgConsumer consumer;//接受消息的线程

    public void setConsumer(KafkaMsgConsumer consumer) {
        this.consumer = consumer;
    }

    public SimpleIntegerProperty heightProperty() {
        return height;
    }

    public void initialize() throws IOException {


       mainBorderPane.centerProperty().setValue(VideoShowPageController.getInstance().anchorPane);
        DataBaseController baseController =  DataBaseController.getInstance();
        mainBorderPane.rightProperty().setValue(baseController.firstAnchorPane);
        mainBorderPane.setPadding(new Insets(0,0,0,0));


    }


    public void configureDataBase(ActionEvent actionEvent) {
        //1.自定义对话框
        Dialog<DataBase> dialog = new Dialog<>();
        dialog.setTitle("数据库配置信息");
        dialog.setHeaderText("请填写以下信息");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField connectInfo  = new TextField(DataBase.dbGlobal.getDbaddress());
       HBox base1 =  new HBox(0,new Label("连接地址"),connectInfo);
       TextField user = new TextField(DataBase.dbGlobal.getUsername());
       HBox base2 =  new HBox(5,new Label("用 户 名"),user);
       TextField passwd = new TextField(DataBase.dbGlobal.getPasswd());
       HBox base3 =  new HBox(5,new Label("密     码"),passwd);
       dialogPane.setContent(new VBox(8, base1, base2,base3));

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                DataBase.dbGlobal.setDbaddress(connectInfo.getText());
                DataBase.dbGlobal.setPasswd(passwd.getText());
                DataBase.dbGlobal.setUsername(user.getText());
                return DataBase.dbGlobal;
            }
            return  DataBase.dbGlobal;
        });
        Optional<DataBase> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((DataBase results) -> {
            System.out.println(
                    results.getDbaddress() + " " + results.getUsername() + " " + results.getPasswd() );
        });
    }


    public void dIshowDebug(ActionEvent actionEvent) throws IOException {

        consumer.setExitConsumer(true);
        heightProperty().setValue(910);
        bottomAnchor.setVisible(true);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("rootlayout/view/DebugWindow.fxml"));

        HBox rootLayout = (HBox) loader.load();
        DebugWindowController  dwc = loader.getController();
        dwc.setMainRcvData(consumer);
        mainBorderPane.bottomProperty().setValue(rootLayout);


    }
}


