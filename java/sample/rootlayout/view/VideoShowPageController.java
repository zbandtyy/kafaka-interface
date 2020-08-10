package sample.rootlayout.view;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Group;

import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import sample.MainApp;
import sample.rootlayout.model.VideoShowPageData;
import scala.Tuple2;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author ：tyy
 * @date ：Created in 2020/3/15 0:35
 * @description：显示效果界面
 * @modified By：
 * @version: $
 */
public class VideoShowPageController {

    public AnchorPane bigVideoAnchor;
    public SplitPane splitPane;
    public TextArea VideoInfoTxtArea;
    public Label bigVideoInfo;

    public AnchorPane getBigVideoAnchor() {
        return bigVideoAnchor;
    }

    public AnchorPane anchorPane;
    @FXML
    private ListView<Tuple2<SimpleStringProperty, VideoAndLabelController>> videolistView;
    @FXML
    public ImageView imageView;

    public void initialize() {
        videolistView.setCellFactory(new Callback<ListView<Tuple2<SimpleStringProperty, VideoAndLabelController>>, ListCell<Tuple2<SimpleStringProperty, VideoAndLabelController>>>() {
            @Override
            public ListCell<Tuple2<SimpleStringProperty, VideoAndLabelController>> call(ListView<Tuple2<SimpleStringProperty, VideoAndLabelController>> param) {
                // TODO Auto-generated method stub
                ListCell<Tuple2<SimpleStringProperty, VideoAndLabelController>> listcell = new ListCell<Tuple2<SimpleStringProperty, VideoAndLabelController>>() {
                    @Override
                    //只定义编辑单元格一定要重写的方法
                    // This method is called whenever the item in the cell changes, for example
                    // when the user scrolls the ListView or 底层数据模型改变
                    // (and the cell is reused to represent some different item in the ListView)
                    protected void updateItem(Tuple2<SimpleStringProperty, VideoAndLabelController> item, boolean empty) {
                        // TODO Auto-generated method stub
                        super.updateItem(item, empty);
                        if (empty == false) {
                            item._2.setLabelText(item._1);
                            this.setGraphic(item._2.mainInterface);

                        }
                    }
                };
                return listcell;
            }

        });

        //设置小视频显示的总界面
        videolistView.setItems(VideoShowPageData.getglobalList());
        Group root  = Annimation.getAnnimation();
        bigVideoAnchor.getChildren().add(0,root);

        //设置大视频监听的队列
        VideoShowPageData.bigVideo.addListener(new ListChangeListener<Image>() {
            @Override
            public void onChanged(Change<? extends Image> c) {
                boolean s = c.next();
                if(c.wasAdded()){
                    List<? extends Image> imgs = c.getAddedSubList();
                    for(Image img:imgs){
                        imageView.setFitHeight(bigVideoAnchor.getHeight());//设置大图像的边界高
                        imageView.setFitWidth(bigVideoAnchor.getWidth());
                        imageView.setPreserveRatio(true);//图像保持的比率
                        imageView.setImage(img);

                    }
                   VideoShowPageData.bigVideo.clear();
                }
            }
        });

        VideoShowPageData.bigVideoInfo.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                bigVideoInfo.setText(newValue);
            }
        });
        //设置大视频区域内的文字

        VideoShowPageData.ImageorOtherInfo.addListener(new ListChangeListener<String>() {

            @Override
            public void onChanged(Change<? extends String> c) {

                int size = VideoShowPageData.ImageorOtherInfo.size();
                if(size > 30){
                    VideoShowPageData.ImageorOtherInfo.remove(0,15);
                    Platform.runLater(()->{
                        VideoInfoTxtArea.setText("");
                        List<? extends String> strs = VideoShowPageData.ImageorOtherInfo;
                        for (String str : strs) {
                            VideoInfoTxtArea.appendText(str);
                        }
                    });

                }else {
                    boolean s = c.next();
                    if (c.wasAdded()) {
                        List<? extends String> strs = c.getAddedSubList();
                        for (String str : strs) {
                            VideoInfoTxtArea.appendText(str);
                        }
                    }
                }

            }

        });

        //设置显示选中的图形
        VideoShowPageData.selection.addListener((observable, oldValue, newValue) -> {
            int i = 0;
           ObservableList<Tuple2<SimpleStringProperty, VideoAndLabelController>> list = VideoShowPageData.getglobalList();
           System.out.println(newValue);
           for(Tuple2<SimpleStringProperty,VideoAndLabelController> t :list){
               if(t._1.get() == newValue){
                    break;
               }

               i =  i+ 1;
           }
           final  int index = i;
           System.out.println(index);
           videolistView.getSelectionModel().select(index);

        });
    }

    public static VideoShowPageController getInstance() throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("rootlayout/view/VideoShowPage.fxml"));
        AnchorPane page = null;
        page = (AnchorPane) loader.load();
        VideoShowPageController vspc = loader.getController();
        return vspc;
    }


}