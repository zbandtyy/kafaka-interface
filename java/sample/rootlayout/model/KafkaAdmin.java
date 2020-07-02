package sample.rootlayout.model;

import javafx.collections.*;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author ：tyy
 * @date ：Created in 2020/3/16 19:11
 * @description：获取kafka集群的信息配置等信息
 * @modified By：
 * @version: $
 */
public class KafkaAdmin {
    private static final Logger logger = Logger.getLogger(KafkaMsg.class);

    private static AdminClient admin;
    private static ObservableList<String> topicList =  FXCollections.observableArrayList();
    private static ArrayList<TopicPartitionInfo> partitions = null;
    private String broker;
    public KafkaAdmin(String brokerUrl) {
        this.broker = brokerUrl;
        //创建一个实例
        Properties properties = new Properties();
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,brokerUrl);
        properties.put(CommonClientConfigs.REQUEST_TIMEOUT_MS_CONFIG,"1000");
        admin = AdminClient.create(properties);

    }
    private  void   listAllTopic() throws ExecutionException, InterruptedException {


              Set<String> topics = admin.listTopics().names().get();
             for(String topic :topics){
                 topicList.add(topic);
             }


    }
    public ObservableList<String> getTopic() throws ExecutionException, InterruptedException {
        listAllTopic();
        return topicList;
    }

    //只支持一个主题的查询
    private void getPartitions(String topics)  {

            DescribeTopicsResult topicInfo = admin.describeTopics(Arrays.asList(topics));
            TopicDescription s = null;
            try {
                s = topicInfo.values().get(topics).get();
            } catch (InterruptedException e) {
                logger.warn(e.getMessage());
            } catch (ExecutionException e) {
                logger.warn(e.getMessage());
            }
            partitions = new ArrayList<TopicPartitionInfo>(s.partitions());

    }
    public int getPartitionSize(String topic){
        if(partitions == null){
            getPartitions(topic);
        }
        return partitions.size();
    }
    public void showTopicConfigDescribe(String topic) throws ExecutionException, InterruptedException {
        ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, topic);
        DescribeConfigsResult result = admin.describeConfigs(Collections.singleton(resource));
        Config config = result.all().get().get(resource);
        for(ConfigEntry e: config.entries()){
            System.out.println("    "+e.name()+ " " + e.value()+ " " + e.isDefault());
        }

    }

    public void showBrokerConfigDescribe()  {
        try {
            ConfigResource resource = new ConfigResource(ConfigResource.Type.BROKER, "0");//结点ID
            DescribeConfigsResult result = admin.describeConfigs(Collections.singleton(resource));
            Config config = result.all().get().get(resource);
            for (ConfigEntry e : config.entries()) {
                System.out.println("    " + e.name() + " = " + e.value());
            }
        }catch (Exception e){
            logger.warn(e.getMessage());
        }

    }
    public void closeAdmin(){
        admin.close();
    }
    public static void main(String[] args) throws ExecutionException, InterruptedException {
       // KafkaAdmin admin = new KafkaAdmin("192.168.0.110:9092");
      // System.out.println( admin.getPartitionSize("test"));
      // admin.listAllTopic();

       // admin.showBrokerConfigDescribe();
    }


}
