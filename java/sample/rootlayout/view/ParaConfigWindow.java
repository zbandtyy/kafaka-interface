package sample.rootlayout.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import sample.MainApp;
import sample.rootlayout.model.ParameterModel;

import java.io.IOException;
import java.lang.reflect.Parameter;

/**
 * @author ：tyy
 * @date ：Created in 2020/8/3 20:12
 * @description：
 * @modified By：
 * @version: $
 */
public class ParaConfigWindow {
    @FXML
    public TableColumn<ParameterModel,String>  keyColumn;
    @FXML
    public TableColumn<ParameterModel,String> valueColumn;
    public TableView<ParameterModel> kvsetTable;
    @Getter
    public AnchorPane anchorPane;
    ObservableList<ParameterModel> valueData = null;
    public void setTableData(ObservableList<ParameterModel> valueData){
        this.valueData = valueData;
        kvsetTable.setItems(valueData);
    }
    public void initialize() {
        kvsetTable.setEditable(true);
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());//设置可以被编辑
        keyColumn.setCellValueFactory(cellData -> cellData.getValue().getParaName());
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().getParaValue());
    }
    public static ParaConfigWindow getInstance( ObservableList<ParameterModel> valueData){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("rootlayout/view/ParaConfigWindow.fxml"));
        //设置表格可编辑状态

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 显示包含rootLayout的scene.
        ParaConfigWindow controller = loader.getController();
        return controller;
    }


    public void commitValue(TableColumn.CellEditEvent<ParameterModel, String> parameterModelStringCellEditEvent) {
        String newValue = parameterModelStringCellEditEvent.getNewValue();

        TablePosition<ParameterModel, String> tablePosition = parameterModelStringCellEditEvent.getTablePosition();
        valueData.get(tablePosition.getRow()).setParaValue(newValue);
    }
}
