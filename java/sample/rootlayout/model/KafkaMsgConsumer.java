package sample.rootlayout.model;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
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

	public  SimpleStringProperty getAllConfig(){
//		return new SimpleStringProperty(this.getGroupId() +" "+
//					this.getIpPort() + " "  + this.getTopic()+":"+stateMsg);
		return  stateMsg;
	}
	private SimpleStringProperty stateMsg ;
	@Getter
	private SimpleStringProperty groupId = new SimpleStringProperty();
	private volatile boolean exitConsumer = false;
	private  boolean finished = false;
	// ip:port
	@Getter
	private SimpleStringProperty ipPort = new SimpleStringProperty();
	// topic
	private SimpleStringProperty topic = new SimpleStringProperty();
	public boolean isFinished() {
		return finished;
	}

	public void setExitConsumer(boolean exitConsumer) {
		this.exitConsumer = exitConsumer;
	}

	public String getGroupId() {
		return groupId.get();
	}



	public String getIpPort() {
		return ipPort.get();
	}



	public String getTopic() {
		return topic.get();
	}

	public SimpleStringProperty topicProperty() {
		return topic;
	}
	public KafkaMsgConsumer(String ipPort, String topic,String groupID) {
		this.ipPort.setValue(ipPort);
		this.topic.setValue(topic);
		this.groupId.setValue(groupID);
		System.out.println(this);
		stateMsg = new SimpleStringProperty(this.ipPort.get() + this.topic.get()+
				this.groupId.get() +":暂时没有无消息");
		loadProperties();
	}


	public  Properties  loadProperties(){
		props.put("bootstrap.servers", this.ipPort.get());
		props.put("group.id", this.groupId.get());
		props.put("enable.auto.commit", "false");
		props.put("auto.commit.interval.ms", "1000");
		props.put("auto.offset.reset", "earliest");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		//props.put("value.deserializer", "sample.serial.VideoEventDataKryoDeSerializer");
		return props;
	}

	public void consumer(){
		System.out.println("consumer subscribe "+this.getTopic());
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList(this.topic.get()));
		final int minBatchSize = 10;
		int a  = 0;
		boolean isReceived = false;
		//创建显示界面 main
		int commit = 0;
		KafkaMsg kafkamsg = new KafkaMsg();
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(1000);
			if(records.isEmpty() ){
				if(a == 20 ){
					if(isReceived == true) {
						consumer.commitSync();
						commit = 0;
						if(exitConsumer == true){
							finished = true;
							break;
						}
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
					//转交数据给 Video Show Page进行显示  （1）进行解析  (2)对数据进行保存，如果数据为满则等待阻塞 不会进行下一步（3）转交显示
					kafkamsg.resolve(data,true);//
				}
				commit = commit + records.count();
				if (commit >= minBatchSize) {//每批数据提交一次
					consumer.commitSync();
					commit = 0;
					if(exitConsumer == true){
						finished = true;
						break;
					}
				}
			}
		}
		consumer.close();


	}
	public void consumerKryo(){
		System.out.println("consumer subscribe "+this.getTopic());
		KafkaConsumer<String, VideoEventData> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList(this.topic.get()));
		final int minBatchSize = 10;
		int a  = 0;
		boolean isReceived = false;
		//创建显示界面 main
		int commit = 0;
		KafkaMsg kafkamsg = new KafkaMsg();
		while (true) {
			ConsumerRecords<String, VideoEventData> records = consumer.poll(1000);
			if(records.isEmpty() ){
				if(a == 20 ){
					if(isReceived == true) {
						consumer.commitSync();
						commit = 0;
						if(exitConsumer == true){
							finished = true;
							break;
						}
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
				for (ConsumerRecord<String, VideoEventData> record : records) {

					//转交数据给 Video Show Page进行显示  （1）进行解析  (2)对数据进行保存，如果数据为满则等待阻塞 不会进行下一步（3）转交显示
					kafkamsg.resolvedata(record.key(),record.value());//
				}
				commit = commit + records.count();
				if (commit >= minBatchSize) {//每批数据提交一次
					consumer.commitSync();
					commit = 0;
					if(exitConsumer == true){
						finished = true;
						break;
					}
				}
			}
		}
		consumer.close();


	}


	public void consumerPartiton(int partition,int offset){
		System.out.println("Debug consumer subscribe "+this.getTopic());
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

		TopicPartition p = new TopicPartition(this.getTopic(),partition);
		consumer.assign(Arrays.asList(p));
		consumer.seek(p,10);

		final int minBatchSize = 10;
		int a  = 0;
		boolean isReceived = false;
		//创建显示界面 main
		int commit = 0;
		while (!finished) {
			ConsumerRecords<String, String> records = consumer.poll(1000);
			if(!records.isEmpty() ){
				for (ConsumerRecord<String, String> record : records) {
					System.out.println(record.offset());
					Tuple2<String, String> data = new Tuple2<String, String>(record.key(), record.value());
					//转交数据给 Video Show Page进行显示  （1）进行解析  (2)对数据进行保存，如果数据为满则等待阻塞 不会进行下一步（3）转交显
					DebugBufferQueue.saveData(VideoEventData.fromJson(data._2));
				}
				commit = commit + records.count();
				if (commit >= minBatchSize) {//每批数据提交一次
					consumer.commitSync();
					commit = 0;

				}
				if(exitConsumer == true|| Thread.currentThread().isInterrupted()){
					consumer.commitSync();
					finished = true;
					break;
				}
			}


		}
		consumer.close();

	}

	public static void main(String[] args) {
		//KafkaMsgConsumer msg = new KafkaMsgConsumer("192.168.0.110:9092", "video-stream-large","app3");
		KafkaMsgConsumer msg = new KafkaMsgConsumer("115.157.201.215:9092", "kryo-collector","app7");
		Properties properties = msg.loadProperties();
		properties.put("value.deserializer","sample.serial.VideoEventDataKryoDeSerializer");
		msg.consumer();
		//msg.consumerPartiton(1,0);
	}

	@Override
	public String toString() {
		return "KafkaMsgConsumer{" +
				"groupId=" + groupId +
				", ipPort=" + ipPort +
				", topic=" + topic +
				'}';
	}
}
