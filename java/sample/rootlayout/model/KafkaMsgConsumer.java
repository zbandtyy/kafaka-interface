package sample.rootlayout.model;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.log4j.Logger;
import org.checkerframework.checker.units.qual.A;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import sample.MainApp;
import scala.Tuple2;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
public class KafkaMsgConsumer {
	Logger logger= Logger.getLogger(KafkaMsgConsumer.class);
	Properties props = new Properties();
	public KafkaMsgConsumer(String ipPort, String topic,String groupID) {
		this.ipPort.setValue(ipPort);
		this.topic.setValue(topic);
		this.groupId.setValue(groupID);

		stateMsg = new SimpleStringProperty(this.ipPort.get() + this.topic.get()+
				 this.groupId.get() +":暂时没有无消息");
	}
	public  SimpleStringProperty getAllConfig(){
//		return new SimpleStringProperty(this.getGroupId() +" "+
//					this.getIpPort() + " "  + this.getTopic()+":"+stateMsg);
		return  stateMsg;
	}
	private SimpleStringProperty stateMsg ;
	private SimpleStringProperty groupId = new SimpleStringProperty();

	public String getGroupId() {
		return groupId.get();
	}

	public SimpleStringProperty groupIdProperty() {
		return groupId;
	}

	public String getIpPort() {
		return ipPort.get();
	}

	public SimpleStringProperty ipPortProperty() {
		return ipPort;
	}

	public String getTopic() {
		return topic.get();
	}

	public SimpleStringProperty topicProperty() {
		return topic;
	}

	// ip:port
	private SimpleStringProperty ipPort = new SimpleStringProperty();
	// topic
	private SimpleStringProperty topic = new SimpleStringProperty();

	public  void  loadProperties(){
		props.put("bootstrap.servers", this.ipPort.get());
		props.put("group.id", this.groupId.get());
		props.put("enable.auto.commit", "false");
		props.put("auto.commit.interval.ms", "1000");
		props.put("auto.offset.reset", "earliest");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	}

	public void consumer(){
		System.out.println("consumer subscribe "+this.getTopic());
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

		consumer.subscribe(Arrays.asList(this.topic.get()));
		//TopicPartition partition0 = new TopicPartition(topic, 2);
		//consumer.assign(Arrays.asList(partition0));
        final int minBatchSize = 50;
        int a  = 0;
        boolean isReceived = false;
		//创建显示界面 main
		int commit = 0;
		KafkaMsg kafkamsg = new KafkaMsg();
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(1000);
            if(records.isEmpty() ){
				if(a == 40 ){
					if(isReceived == true) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								stateMsg.setValue(ipPort.get() + topic.get()+
										groupId.get() +": 暂时没有消息");
							}
						});
					}
					a =  0;
					isReceived = false;
				}
				a++;
			}else {
				if (isReceived == false) {
					Platform.runLater(new Runnable() {
										  @Override
										  public void run() {
											  stateMsg.setValue(ipPort.get() + topic.get()+
													 groupId.get() + ": 接收处理中");
										  }
									  });

					isReceived = true;
				}
				for (ConsumerRecord<String, String> record : records) {
					Tuple2<String, String> data = new Tuple2<String, String>(record.key(), record.value());
					//转交数据给 Video Show Page进行显示  （1）进行解析  （2）转交显示
					kafkamsg.resolve(data);//进行解析，解析成VideoEventData数据
					commit++;
					if (commit >= minBatchSize) {
						consumer.commitSync();
					}
				}
			}


		}

	}

	public static void main(String[] args) {
		KafkaMsgConsumer msg = new KafkaMsgConsumer("192.168.0.110:9092", "video-stream-large","app3");
		msg.loadProperties();
		msg.consumer();
	}


}
