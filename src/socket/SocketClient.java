package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;

public class SocketClient {


	private static String ip1;
	
	private static int port1;
	
	private static String ip2;
	
	private static int port2;
	
	private static int maxCount;
	
	private static String encoding;
	
	static{
		Properties prop = PropertiesUtil.loadProperties();
		ip1 = prop.getProperty("client1.ip");
		port1= Integer.parseInt(prop.getProperty("client1.port"));
		ip2 = prop.getProperty("client2.ip");
		port2 = Integer.parseInt(prop.getProperty("client2.port"));
		maxCount = Integer.parseInt(prop.getProperty("maxCount"));
		encoding = prop.getProperty("encoding");
	}
	
	/**
	 * 创建与平台的连接
	 * @param clientIp
	 * @param clienrPort
	 * @return
	 */
	public static Socket getSocketClient(String clientIp, int clienrPort){
		//Socket，用于连接服务端的ServerSocket
		Socket socket = null;
		 
		//isClent1为true返回Clent1,isClent1为false返回Clent2
		boolean isClent1 = true;
		
		for (int i = 0; i < maxCount; i++) {
			
			try {
				if(isClent1){
					socket = new Socket(ip1, port1);
				}else{
					socket = new Socket(ip2, port2);
				}
				
			} catch (IOException e) {
				isClent1 = !isClent1;
			}finally{
				if(socket!=null){
					System.out.println(clientIp + ":" + clienrPort+" 成功连接平台");
					break;
				}
			}
			
		}
		return socket;
	}
	/**
	 * 向平台发送消息
	 * @param socket
	 * @param message
	 * @param clientIp
	 * @param clienrPort
	 */
	public static void sendSocket(Socket socket,String message, String clientIp, int clienrPort){
		try {
			if(socket!=null){
				PrintWriter socketOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),encoding));
				socketOut.print(message);
				socketOut.flush();
				System.out.println(clientIp + ":" + clienrPort+" 已向平台发送消息");
			}else{
				System.out.println(clientIp + ":" + clienrPort+" 无法连接平台,向平台发送消息失败");
			}
		} catch (IOException e) {
			System.out.println(clientIp + ":" + clienrPort+" 向平台发送消息失败:"+e.getMessage());
		}
	}
	/**
	 * 关闭与平台的连接
	 * @param socket
	 * @param clienrPort 
	 * @param clientIp 
	 */
	public static void closeSocket(Socket socket, String clientIp, int clienrPort){
			try {
				if(socket!=null){
					socket.close();
					System.out.println(clientIp + ":" + clienrPort+" 断开与平台的连接");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
				
		
	}
	/***
	 * 判断socket连接是否断开,返回true为断开,返回false没断开
	 * @param socket
	 * @return
	 */
	public static boolean isClosed(Socket socket){
		boolean isClosed = false;
		if(socket!=null){
			try {
				socket.sendUrgentData(0xff);
				Thread.sleep(10);
				socket.sendUrgentData(0xff);
			} catch (Exception e) {
				isClosed = true;
			}
		}else{
			isClosed = true;
		}
		return isClosed;
		
	}
	public static String getResponseMes(Socket cliet) {
		StringBuffer message = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(cliet.getInputStream(),encoding));
			Thread.sleep(100);
			while(br.ready()){
				message.append( br.readLine() + "\r");
			}
			br.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return message.toString();
		
	} 

	
}
