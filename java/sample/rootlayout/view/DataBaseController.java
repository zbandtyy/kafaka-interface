package sample.rootlayout.view;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import sample.MainApp;
import sample.mysql.*;
import sample.rootlayout.model.DataBase;
import sample.rootlayout.model.TableViewData;
import sample.rootlayout.utils.TimeFilter;
import sample.rootlayout.utils.TimeStampConverter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;

/**
 * @author ：tyy
 * @date ：Created in 2020/3/28 21:59
 * @description：显示数据库数据的界面
 * @modified By：
 * @version: $
 */
public class DataBaseController {
    public AnchorPane firstAnchorPane;
    public TextField qinfoTextField;
    public VBox resultVBox;
    public DatePicker fromDate;
    public DatePicker toDate;
    public TextArea licenseTextField;
    public TableView<TableViewData> trackTableView;
    public TableColumn<TableViewData,String> vidColumn = new TableColumn<>();
    public TableColumn<TableViewData, String> seqTime = new TableColumn<>();
    public TableColumn<TableViewData,String> vidinfo = new TableColumn<>();
    boolean enableQuery = true;
    public static  DataBaseController getInstance() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("rootlayout/view/DataBase.fxml"));
        AnchorPane page = null;
        page = (AnchorPane) loader.load();
        DataBaseController dbc = loader.getController();
        return dbc;

    }
    public void initialize() {
        licenseTextField.setWrapText(true);
        trackTableView.getColumns().addAll(vidColumn,seqTime,vidinfo);
        vidinfo.setText("摄像头信息");
        seqTime.setText("时间戳");
        vidColumn.setText("摄像头ID");
        vidinfo.setCellValueFactory(cellData -> cellData.getValue().vidInfo);
        seqTime.setCellValueFactory(cellData -> cellData.getValue().time);
        vidColumn.setCellValueFactory(cellData -> cellData.getValue().vid);
        trackTableView.setItems(TableViewData.trackList);//绑定显示的轨迹数据内容
        fromDate.setValue(LocalDate.now());
        toDate.setValue(LocalDate.now());
       //保证日期fromaDate 日期 比 toData日期小 ,并且不能超过今天的日期
        final Callback<DatePicker, DateCell> dayCellFactory =
                new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);

                                if (item.isBefore(
                                                fromDate.getValue()  ) || item.isAfter(LocalDate.now())
                                ) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                            }
                        };
                    }
                };
        toDate.setDayCellFactory(dayCellFactory);


    }
    public void queryAction(MouseEvent mouseEvent) {
        String text = qinfoTextField.getText();

        if(mouseEvent.getClickCount() == 1 && !text.isEmpty() ){
            //1.创建一个线程对后台数据进行查询
            if (enableQuery == true) {
                enableQuery = false;

                LocalDate f = fromDate.getValue();
                LocalDate t = toDate.getValue();
                System.out.println(TimeStampConverter.fromLocalDate(f));
               //1.获取选中的框
                Task<Integer> task = new Task<Integer>() {
                    @Override
                    protected Integer call()  {
                        try {
                            //获取填取得文本
                            TrackSql  sql = new TrackSql(DataBase.dbGlobal.getDbaddress(), DataBase.dbGlobal  .getUsername(), DataBase.dbGlobal.getPasswd());
                            //查询车牌信息即车牌的归属人进行显示
                            VehicleInfoWrapper res = new VehicleInfoWrapper(sql.getVehicleInfoByPlateStr(text));
                            TimeFilter filter = new TimeFilter(f,t);
                            TableViewData resTrack = new TableViewData(new TrackTableWrapper(sql.getTrackByPlateStr(text)),filter);//查询车牌的轨迹信息
                            //对查询到的轨迹信息根据日期进行过滤。
                            //1.获取空的摄像头ID集合，即没有事先保存的摄像头集合
                            Set<String> set = resTrack.getEmptyCameraSet();
                            //2.查询对应的摄像头信息，批量查询
                            Set<CameraInfo> info = sql.getCameraInfoByCID(set);
                            //3.对摄像头数据进行更新
                            resTrack.insertCameraData(info);
                            //更新界车牌信息显示
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    licenseTextField.setText(res.toString());
                                }
                            });
                        } catch (Exception e) {

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    CustomDialog.showException(e, "查询数据库错误异常,请修改配置->数据库连接配置选项");
                                }
                            });
                        }
                        finally {
                            enableQuery = true;
                        }
                        return 0;
                    }
                    @Override
                    protected void succeeded() {
                        enableQuery = true;

                    }
                };
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
            }
            else {
                CustomDialog.showWarnings("请等待数据获取执行完成");
            }
        }else{
            CustomDialog.showWarnings("必须输入查询车牌的内容");
        }
    }
}
