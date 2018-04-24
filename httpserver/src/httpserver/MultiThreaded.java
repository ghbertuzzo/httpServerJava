/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

/**
 *
 * @author Giovani
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple socket server
 * 
 */
public class MultiThreaded {

	public ServerSocket serverSocket;
	private int port;

	public MultiThreaded(int port) {
            this.port = port;
	}

	public void start() throws IOException, InterruptedException {
            this.serverSocket = new ServerSocket(port);
            System.out.println("Starting the socket server at port:" + port);
            Socket client = null;
            while (true) {
                System.out.println("Esperando conexões...");
                client = serverSocket.accept();
                System.out.println("O cliente: "+ client.getInetAddress().getCanonicalHostName()+" conectou");
                Thread thread = new Thread(new Worker(client));
                thread.start();
            }
	}

}
