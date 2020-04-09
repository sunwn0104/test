package socket;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer{

	/**
	 * 编码
	 */
	private String encoding;
	
	/**
	 * 服务端接收到的客户端
	 */
	private Socket socket;
	
	/**
	 * 接收数据的服务端
	 */
	private ServerSocket server;

	
	/**
	 * 线程池
	 */
	private ExecutorService executorService;

	/**
	 * 最大线程数
	 */
	private int maxTheadNum;
	
	/**
	 * TCP对外提供端口
	 */
	private int serverPort;
	
	/**
	 * 响应级别,0为无响应,1为直接返回接收到的消息,2为返回平台响应的消息
	 */
	private int reLevl;

	
	public SocketServer() {
		try {
			System.out.println("初始化服务端");
			Properties prop = PropertiesUtil.loadProperties(); 
			serverPort = Integer.parseInt(prop.getProperty("serverPort"));
			maxTheadNum = Integer.parseInt(prop.getProperty("maxTheadNum"));
			encoding = prop.getProperty("encoding");
			reLevl = Integer.parseInt(prop.getProperty("reLevl"));
			server = new ServerSocket(serverPort);
			System.out.println("服务端初始化完毕");
		} catch (IOException e) {
			System.out.println("服务端初始化失败:" + e.getMessage());
		}
	}


	/**
	 * 服务端开始工作的方法
	 * @param socketServer 
	 */
	public void start() {
		try {
			executorService = Executors.newFixedThreadPool(maxTheadNum);
			while( true ){
				socket = server.accept();
				SocketRunnable socketRunnable = new SocketRunnable(socket,encoding,reLevl);
				executorService.execute(socketRunnable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
