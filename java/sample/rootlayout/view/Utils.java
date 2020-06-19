package sample.rootlayout.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {


	public static boolean checkPort(String port) {
		boolean isLegal = false;
		try{
			Integer b= Integer.valueOf(port).intValue();
			if(b >= 1000 && b <= 65536){
				return true;
			}
		}catch (NumberFormatException e){
			return false;
		}
		return  false;

	}
	// checkip
	public static  boolean checkIp(String ip) {
		// 定义正则表达式
		String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
				+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
		if (ip.matches(regex)) {
			return true;
		} else {
			return false;
		}
	}


	public boolean checkMsg(String kafkaMessage) {
		if ("".equals(kafkaMessage)) {
			return false;
		}
		if (!checkJson(kafkaMessage)) {
			return false;
		}
		return true;
	}

	private boolean checkJson(String kafkaMessage) {
		try {
			//nJSONObject jsonStr = JSONObject.parseObject(kafkaMessage);
			return true;
		} catch (Exception e) {
			return false;
		}
	}


	public boolean isFileExit(File file) {
		if (file.exists()) {
			return true;
		}
		return false;
	}

	public String formatJson(String content) {
		StringBuffer sb = new StringBuffer();
		int index = 0;
		int count = 0;
		while (index < content.length()) {
			char ch = content.charAt(index);
			if (ch == '{' || ch == '[') {
				sb.append(ch);
				sb.append('\n');
				count++;
				for (int i = 0; i < count; i++) {
					sb.append('\t');
				}
			} else if (ch == '}' || ch == ']') {
				sb.append('\n');
				count--;
				for (int i = 0; i < count; i++) {
					sb.append('\t');
				}
				sb.append(ch);
			} else if (ch == ',') {
				sb.append(ch);
				sb.append('\n');
				for (int i = 0; i < count; i++) {
					sb.append('\t');
				}
			} else {
				sb.append(ch);
			}
			index++;
		}
		return sb.toString();
	}
	// json变一行
	public static String compactJson(String content) {
		String regEx = "[\t\n]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(content);
		return m.replaceAll("").trim();
	}

	public static void main(String[] args) {
		System.out.println(checkPort("11187"));//65536
	}

}
