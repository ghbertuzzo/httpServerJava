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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.swing.JOptionPane;

public class Worker implements Runnable {

	private Socket client;
        private String resourcePath;
        private Map<String,String> map;
        public GridServer gridServer;
        public Response resp;
        //httpMethod (ENUM), resourcePath (STRING), requestHeaderMap (MAP<STRING, STRING))

	public Worker(Socket client,GridServer grid) {
            this.client = client;
            this.gridServer = grid;
	}

	@Override
	public void run() {            
            try {
                System.out.println("Thread started");
                readResponse();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
	}
	private void readResponse() throws IOException, InterruptedException {
            try {
                BufferedReader request = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String requestHeader = "";
                String temp = ".";
                while (!temp.equals("")) {
                    temp = request.readLine();
                    System.out.println(temp);
                    requestHeader += temp + "\n" ;
                }
                String url = requestHeader.split("\n")[0].split(" ")[1];
                if (requestHeader.split("\n")[0].contains(Metodos.GET.name())) {
                    resp.constructResponseHeader(200,url,client,gridServer,requestHeader);                    
                } else {
                    resp.constructResponseHeader(404,url,client,gridServer,requestHeader);                    
                }
                request.close();
                
                client.close();
                return;
            } catch (Exception e) {
            }
	}
}
