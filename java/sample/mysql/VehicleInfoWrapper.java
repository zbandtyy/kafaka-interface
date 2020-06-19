package sample.mysql;

import javafx.beans.property.SimpleStringProperty;

/**
 * @author ：tyy
 * @date ：Created in 2020/3/31 1:53
 * @description：车辆信息封装
 * @modified By：
 * @version: $
 */
public class VehicleInfoWrapper {
    SimpleStringProperty plateStr = new SimpleStringProperty("没有查询到此消息");
    SimpleStringProperty VIN = new SimpleStringProperty("没有查询到此消息");

    SimpleStringProperty carBranch= new SimpleStringProperty("没有查询到此消息");
    SimpleStringProperty owner= new SimpleStringProperty("没有查询到此消息");
    SimpleStringProperty ownerID= new SimpleStringProperty("没有查询到此消息");
    VehicleInfo vi = null ;

    @Override
    public String toString() {
        return  "车牌号    :" + plateStr.getValue() +"\n" +
                "车架号    :" + VIN.getValue() +"\n" +
                "车子型号 :" + carBranch.getValue() +"\n" +
                "所有者    :" + owner.getValue() +"\n" +
                "所有者身份证:" + ownerID.getValue();
    }

    public  VehicleInfoWrapper( VehicleInfo vi){


        this.vi = vi;
        if(vi != null) {
            plateStr.setValue(vi.plateStr);
            VIN.setValue(vi.VIN);
            carBranch.setValue(vi.carBranch);
            owner.setValue(vi.owner);
            ownerID.setValue(vi.ownerID);
        }
    }

}
