package sample.rootlayout.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javafx.scene.paint.Paint;
import sample.MainApp;
import sample.rootlayout.model.VideoShowPageData;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static org.opencv.imgcodecs.Imgcodecs.imencode;

/**
 * @author ：tyy
 * @date ：Created in 2020/3/19 0:42
 * @description：
 * @modified By：
 * @version: $ 以他为基准，调出舒心视频的累
 */
public class VideoAndLabelController  {

    private VideoShowPageData outerInterface;
 //   private VideoAndLabelController(){}
    public ObservableList<Image> getImgList() {
        return imgList;
    }
    private ObservableList<Image>  imgList = FXCollections.observableArrayList();
    @FXML
    public AnchorPane mainInterface;//追界面
    @FXML
    public AnchorPane videoAnchor;//视频页面
    //获得一个界面,
    public  static VideoAndLabelController getInstance(){

        try {
            // 加载FXNL的RootLayout
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("rootlayout/view/VideoAndLabel.fxml"));
             loader.load();
            // 显示包含rootLayout的scene.
            return loader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
    @FXML
    private ImageView imageView;

    public ImageView getImageView() {
        return imageView;
    }

    public Button getKeyLabelButton() {
        return keyLabelButton;
    }
    static  int index = 0;//想用来标识窗口的个数  但是是随机增减的
    @FXML
    private Button keyLabelButton;
    public void setLabelText(SimpleStringProperty text) {
        keyLabelButton.textProperty().bindBidirectional(text);
    }
    public void initialize(){

        VideoAndLabelController mycontroller = this;
        imgList.addListener(new ListChangeListener<Image>() {
            @Override
            public void onChanged(Change<? extends Image> c) {
                boolean s = c.next();
                if(c.wasAdded()){
                    List<? extends Image> imgs = c.getAddedSubList();
                    for(Image img:imgs){
                        //当前选中的为空 或者 选中的就是所需的
                        if(mycontroller.keyLabelButton.getText() == VideoShowPageData.selection.getValue() ||
                                (VideoShowPageData.selection.get().isEmpty() &&
                                        VideoShowPageData.getglobalList().get(0)._2 == mycontroller)) {
                            VideoShowPageData.bigVideo.add(img);
                        }
                        imageView.setFitHeight(videoAnchor.getHeight());
                        imageView.setFitWidth(videoAnchor.getWidth());
                        imageView.setPreserveRatio(true);
                        imageView.setImage( img);
                    }
                     imgList.clear();
                }
            }
        });
        index ++;
            //设置标签的字体颜色
        String[] color = {"green","aqua","brown","lightsalmon","mediumblue"};
        keyLabelButton.setTextFill(Paint.valueOf(color[index % (color.length)]));

    }
    public void showBigVideo(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 2){
            VideoShowPageData.selection.set(keyLabelButton.getText());
        }
    }
}
