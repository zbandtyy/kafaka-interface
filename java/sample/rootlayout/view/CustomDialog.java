package sample.rootlayout.view;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author ：tyy
 * @date ：Created in 2020/3/30 20:31
 * @description：各种对话框
 * @modified By：
 * @version: $
 */
public class CustomDialog {
    public static void  showException(Exception e,String context){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("异常");
        alert.setHeaderText(" 异常，请将该异常反馈给qq:707196697");
        alert.setContentText(context);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();
        Label label = new Label("该异常的跟踪路径为：");
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    public  static  void  showWarnings(String text){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("警告");
        alert.setHeaderText(text);
        alert.showAndWait();
    }
}
