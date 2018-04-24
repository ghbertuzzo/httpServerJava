package httpserver;

import java.io.IOException;

class mainServer {

	public static void main(String[] args) throws InterruptedException {
		//int portNumber = Integer.parseInt(args[0]);
		int portNumber = 2080;
		try {
                    MultiThreaded socketServer = new MultiThreaded(portNumber);
                    socketServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
