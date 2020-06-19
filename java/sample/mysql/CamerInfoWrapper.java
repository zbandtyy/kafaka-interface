package sample.mysql;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author ：tyy
 * @date ：Created in 2020/3/31 1:56
 * @description：
 * @modified By：
 * @version: $
 */
public class CamerInfoWrapper {
    public String getCamerName() {
        return camerName.get();
    }

    public SimpleStringProperty camerNameProperty() {
        return camerName;
    }

    public String getRoad() {
        return road.get();
    }

    public SimpleStringProperty roadProperty() {
        return road;
    }

    public String getDirection() {
        return direction.get();
    }

    public SimpleStringProperty directionProperty() {
        return direction;
    }

    public String getLane() {
        return lane.get();
    }

    public SimpleStringProperty laneProperty() {
        return lane;
    }

    SimpleStringProperty camerName = new SimpleStringProperty();
    SimpleStringProperty road  = new SimpleStringProperty();
    SimpleStringProperty direction  = new SimpleStringProperty();
    SimpleStringProperty lane  = new SimpleStringProperty();

    public static ObservableList<CamerInfoWrapper> getList() {
        return info;
    }

    public  static ObservableList<CamerInfoWrapper> info = FXCollections.observableArrayList();
    public  CamerInfoWrapper(CameraInfo ci){
        camerName.setValue(ci.camerName);
        road.setValue(ci.road);
        direction.setValue(ci.direction);
        lane.setValue(ci.lane);
        info.add(this);
        System.out.println(info.size());
    }


}
