package socket;

public class Run {

	public static void main(String[] args) {
		 SocketServer server = new SocketServer();
		 if(server!=null){
			 //本地
			 server.start();
		 }
	}
}
