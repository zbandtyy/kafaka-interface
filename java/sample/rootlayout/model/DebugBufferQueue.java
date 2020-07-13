package sample.rootlayout.model;

import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.*;

/**
 * @author ：tyy
 * @date ：Created in 2020/7/8 22:59
 * @description：用于保存调试模式下的数据
 * @modified By：
 * @version: $
 */
public class DebugBufferQueue {
    private final static BlockingQueue<VideoEventData> queue = new LinkedBlockingQueue<VideoEventData>(10) ;

    public static void  saveData(VideoEventData data){

        try {
            queue.put(data);
        } catch (InterruptedException e) {
            System.out.println("====================");

            return;
        }
    }

    public static ArrayList<VideoEventData> getBatchData(){//当没有数据时，有阻塞
        ArrayList<VideoEventData> list = new ArrayList<>(10);
        long start = System.currentTimeMillis();
        long duration = 20000;
        while (duration > 0 && list.size() < 10) {
            try {
               VideoEventData d = queue.poll(duration,TimeUnit.MILLISECONDS);
               if(d == null){
                   System.out.println("没有数据");
               }
               else {
                   list.add(d);
               }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            duration = duration - (System.currentTimeMillis() - start);
        }
        System.out.println("接收数据大小为" + list.size());
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
        return list;
    }
    public  static  void clear(){
        queue.clear();
    }



}
