package sample.rootlayout.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import scala.sys.Prop;

import java.beans.ConstructorProperties;

/**
 * @author ：tyy
 * @date ：Created in 2020/8/3 20:22
 * @description：
 * @modified By：
 * @version: $
 */
@Getter
@Setter
@AllArgsConstructor
public class ParameterModel {

    StringProperty paraName = new SimpleStringProperty();
    StringProperty paraValue = new SimpleStringProperty();

    public ParameterModel(String name, String value) {
       paraName.setValue(name);
       paraValue.setValue(value);
    }

    public void setParaName(String paraName) {
        this.paraName.set(paraName);
    }

    public void setParaValue(String paraValue) {
        this.paraValue.set(paraValue);
    }
}
