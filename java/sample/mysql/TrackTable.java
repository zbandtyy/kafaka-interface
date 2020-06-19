package sample.mysql;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public  class TrackTable{
    @Override
    public String toString() {
        return "TrackTable{" +
                "plateStr='" + plateStr + '\'' +
                ", cameraSeq='" + cameraSeq + '\'' +
                '}';
    }
   public void append(TrackTable tnew){
        this.cameraSeq += ","+ tnew.getCameraSeq();
   }
    public String getPlateStr() {
        return plateStr;
    }

    public void setPlateStr(String plateStr) {
        this.plateStr = plateStr;
    }

    public String getCameraSeq() {
        return cameraSeq;
    }

    public void setCameraSeq(String cameraSeq) {
        this.cameraSeq = cameraSeq;
    }

    private  String plateStr = null;
    private  String cameraSeq = null;//最长为64KB，受限于TEXT的大小

    public TrackTable(String plateStr, String cameraSeq) {
        this.plateStr = plateStr;
        this.cameraSeq = cameraSeq;
    }

    public TrackTable(String plateStr, String[] cameraSeq) {
        this.plateStr = plateStr;
        for(String cmera:cameraSeq){
            this.cameraSeq += cmera + ",";
        }
    }

    public static Map<String, String> resolveTrace(TrackTable tt){
        if(tt == null){
            return null;
        }
        Map<String,String> cmeraSeq = new TreeMap<>();
        String[] traces = tt.cameraSeq.split(",");
        for(String trace: traces){
            if(trace.isEmpty())
                continue;
            String str[] = trace.split(":",2);
            String cameraID = str[0];
            String time = str[1];
            cmeraSeq.put(time, cameraID);
        }
        return cmeraSeq;
    }


}
