package sample.rootlayout.model;

import javafx.beans.property.SimpleStringProperty;

/**
 * @author ：tyy
 * @date ：Created in 2020/3/29 0:50
 * @description：查询数据库的配置
 * @modified By：
 * @version: $
 */
public class DataBase {

    //1.登录地址
    SimpleStringProperty dbaddress= new SimpleStringProperty("jdbc:mysql://192.168.0.103:3306/track?user=root&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8");
    // 2.用户名
    SimpleStringProperty username = new SimpleStringProperty("root");
    //3.密码
    SimpleStringProperty passwd = new SimpleStringProperty("123456");
    public void setDbaddress(String dbaddress) {
        this.dbaddress.set(dbaddress);
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public void setPasswd(String passwd) {
        this.passwd.set(passwd);
    }

    public String getDbaddress() {
        return dbaddress.get();
    }

    public SimpleStringProperty dbaddressProperty() {
        return dbaddress;
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public String getPasswd() {
        return passwd.get();
    }

    public SimpleStringProperty passwdProperty() {
        return passwd;
    }
    //1.提供一个全局的唯一的即可
    private DataBase(){

    }
    public  static DataBase dbGlobal = new DataBase();

}
