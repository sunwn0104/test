package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class SocketRunnable implements Runnable{

	/**
	 * 服务端接收到的客户端
	 */
	private Socket socket;

	/**
	 * 向平台发送数据的客户端
	 */
	private Socket cliet;
	
	
	/**
	 * 编码
	 */
	private String encoding;

	/***
	 * 接收消息标志
	 */
	boolean isIn = false;

	/**
	 * 发送消息标志
	 */
	boolean isOut = false;

	/**
	 * HL7格式消息
	 */
	StringBuffer outMes;
	
	/**
	 * 响应级别,0为无响应,1为直接返回接收到的消息,2为返回平台响应的消息
	 */
	private int reLevl;
	
	public SocketRunnable(Socket socket,String encoding, int reLevl){
		this.socket = socket;
		this.encoding = encoding;
		this.reLevl = reLevl;
	}
	
	
	
	@Override
	public void run() {
		try {
			InetAddress address = socket.getInetAddress();
			String clientIp = address.getHostAddress();
			// 获取客户端的端口号
			int clienrPort = socket.getPort();
			System.out.println(clientIp + ":" + clienrPort + " 客户端连接了");
			InputStream in = socket.getInputStream();
			/*
			 * 将字节输入流包装为字符输出流，这样 可以指定编码集来读取每一个字符
			 */
			InputStreamReader isr = new InputStreamReader(in, encoding);
			/*
			 * 将字符流转换为缓冲字符输入流 这样就可以以行为单位读取字符串了
			 */
			BufferedReader br = new BufferedReader(isr);

			String message = null;
			// 读取客户端发送过来的一行字符串
			/*
			 * 读取客户端发送过来的信息这里 windows与linux存在一定的差异: linux:当客户端与服务端断开连接后
			 * 我们通过输入流会读取到null 但这是合乎逻辑的，因为缓冲流的 readLine()方法若返回null就
			 * 表示无法通过该留再读取到信息。 参考之前服务文本文件的判断。
			 * 
			 * windows:当客户端与服务端断开连接后 readLine()方法会抛出异常。
			 */
			// 获取向平台发送消息的客户端
			cliet = SocketClient.getSocketClient(clientIp,clienrPort);
			while ((message = br.readLine()) != null) {
				// HL7消息以0x0b为开头,当收到消息的头为0x0b时开始拼接消息
				if (message.length() > 0 && 0x0b == message.charAt(0)) {
					isIn = true;
					outMes = new StringBuffer();
				}
				if (isIn) {
					outMes.append(message + "\r");
				}
				// HL7消息以0x1c为结尾,当收到消息尾为0x1c,结束拼接消息,并发送消息
				if (message.length() > 0 && message.charAt(message.length() - 1) == 0x1c) {
					isIn = false;
					isOut = true;
				}
				if (isOut) {
					isOut = false;
					//判断连接平台的客户端是否断开,如果断开了,需要重新获取连接平台的客户端
					if(SocketClient.isClosed(cliet)){
						//关闭原先的客户端连接
						SocketClient.closeSocket(cliet,clientIp,clienrPort);
						//重新获取客户端连接
						cliet = SocketClient.getSocketClient(clientIp,clienrPort);
					}
					SocketClient.sendSocket(cliet, outMes.toString(),clientIp,clienrPort);
					//reLevl不为0 socket服务提供响应
					if(reLevl!=0){
						String reMes = null;
						//直接响应收到的消息
						if(reLevl==1){
							reMes = outMes.toString();
						}
						//响应平台返回的消息
						if(reLevl==2){
							reMes = SocketClient.getResponseMes(cliet);
						}
						responseSocket(reMes);
					}
					
				}
			}
			SocketClient.closeSocket(cliet,clientIp,clienrPort);
			System.out.println(clientIp + ":" + clienrPort + " 客户端断开连接");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private void responseSocket(String message) {
		try {
			PrintWriter socketOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),encoding));
			socketOut.print(message);
			socketOut.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
