package sample.mysql;

public class    CameraInfo{
    @Override
    public String toString() {
        return "CameraInfo{" +
                "camerName='" + camerName + '\'' +
                ", road='" + road + '\'' +
                ", direction='" + direction + '\'' +
                ", lane='" + lane + '\'' +
                '}';
    }

    String camerName;
    String road;
    String direction;
    String lane;
    public CameraInfo(String camerName, String road, String direction, String lane) {
        this.camerName = camerName;
        this.road = road;
        this.direction = direction;
        this.lane = lane;
    }

    public String getCamerName() {
        return camerName;
    }

    public String getRoad() {
        return road;
    }

    public String getDirection() {
        return direction;
    }

    public String getLane() {
        return lane;
    }
    public String getMessage(){
        return this.road + "," + this.direction;
    }

}
