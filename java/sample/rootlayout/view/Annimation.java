package sample.rootlayout.view;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

import static java.lang.Math.random;

/**
 * @author ：tyy
 * @date ：Created in 2020/3/21 21:07
 * @description：给大的视频窗口添加的动画效果
 * @modified By：
 * @version: $
 */
public class Annimation {
   private  Group root= new Group();
    Group circles;
    Rectangle colors;
    int radius = 100;//半径大小
    int circleCount = 18;//圆数量
    int Height = 800;
    int Width =800;
    void  creatCircle(){

         circles = new Group();
        for (int i = 0; i < circleCount; i++) {
            Circle circle = new Circle(radius, Color.web("black", 0.1));
            circle.setStrokeType(StrokeType.OUTSIDE);
            circle.setStroke(Color.web("black", 0.2));
            circle.setStrokeWidth(4);
            circles.getChildren().add(circle);
        }

    }
    void createRectColor(){
        colors = new Rectangle(Width, Height,
                new LinearGradient(0f, 1f, 1f, 0f, true, CycleMethod.NO_CYCLE, new Stop[]{
                        new Stop(0, Color.web("#f8bd55")),
                        new Stop(0.14, Color.web("#c0fe56")),
                        new Stop(0.28, Color.web("#5dfbc1")),
                        new Stop(0.43, Color.web("#64c2f8")),
                        new Stop(0.57, Color.web("#be4af7")),
                        new Stop(0.71, Color.web("#ed5fc2")),
                        new Stop(0.85, Color.web("#ef504c")),
                        new Stop(1, Color.web("#f2660f")),}));
        colors.widthProperty().setValue(Width);
        colors.heightProperty().setValue(Height);
    }
    void creatBlendModeGroup(){
        Group blendModeGroup =
                new Group(new Group(new Rectangle(800, 800,
                        Color.WHITE), circles), colors);
        colors.setBlendMode(BlendMode.OVERLAY);
        root.getChildren().add(blendModeGroup);
        circles.setEffect(new BoxBlur(10, 10, 3));

    }
    public  Annimation(){

        creatCircle();
        createRectColor();;
        creatBlendModeGroup();
        Timeline timeline = new Timeline();
        timeline.setCycleCount(2);
        timeline.setAutoReverse(true);
        for (Node circle : circles.getChildren()) {
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, // set start position at 0
                            new KeyValue(circle.translateXProperty(), random() * 800),
                            new KeyValue(circle.translateYProperty(), random() * 800)),
                    new KeyFrame(new Duration(8000), // set end position at 40s
                            new KeyValue(circle.translateXProperty(), random() * 800),
                            new KeyValue(circle.translateYProperty(), random() * 800)));
        }
        timeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                creatCircle();
                createRectColor();;
                creatBlendModeGroup();
                circleCount = (circleCount-4 > 0) ? (circleCount - 4): 18;
                radius  = (radius + 10 < 140)? radius+10 : 100;
                for (Node circle : circles.getChildren()) {
                    timeline.getKeyFrames().addAll(
                            new KeyFrame(Duration.ZERO, // set start position at 0
                                    new KeyValue(circle.translateXProperty(), random() * 800),
                                    new KeyValue(circle.translateYProperty(), random() * 800)),
                            new KeyFrame(new Duration((count*30)+8000), // set end position at 40s
                                    new KeyValue(circle.translateXProperty(), random() * 800),
                                    new KeyValue(circle.translateYProperty(), random() * 800)));
                }
                count++;
                timeline.play();
            }
        });
        timeline.play();
    }

    public  static  Group getAnnimation(){
        Annimation a = new Annimation();
        return  a.root;
    }
    int count = 0;//技术结束的次数

}
