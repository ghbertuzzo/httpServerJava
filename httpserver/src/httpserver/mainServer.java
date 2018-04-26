package httpserver;

import java.io.IOException;
import java.net.SocketException;

class mainServer {

	public static void main(String[] args) throws InterruptedException, SocketException, IOException {
		//int portNumber = Integer.parseInt(args[0]);
		int httpPortNumber = 2080;                
		try {
                    MultiThreaded socketServer = new MultiThreaded(httpPortNumber);
                    socketServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
