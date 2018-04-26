/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bertuzzo
 */
public class GridServer {

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        GridServer.port = port;
    }

    public static DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public static void setDatagramSocket(DatagramSocket datagramSocket) {
        GridServer.datagramSocket = datagramSocket;
    }

    public static List<ServerFriend> getListServers() {
        return listServers;
    }

    public static void setListServers(List<ServerFriend> listServers) {
        GridServer.listServers = listServers;
    }
    
    public static int port;
    public static DatagramSocket datagramSocket;
    public static List<ServerFriend> listServers;

    public GridServer(int port) throws SocketException {
        this.datagramSocket = new DatagramSocket(port);
        this.port = port;
    }
    
    public void initServer() throws UnknownHostException, IOException{
        listServers = new ArrayList<>();
        sendMessage("SD"+port+" "+"2080\n", InetAddress.getByName("255.255.255.255"),5554);
        listen();        
    }
    
    public void listen() {
        new Thread() {
            public void run() {
                while (true) {
                    try {
                        byte[] buf = new byte[1000];
                        DatagramPacket packet = new DatagramPacket(buf,buf.length);
                        datagramSocket.receive(packet);
                        String message = new String(buf);
                        System.out.println("Recieved: " + message);
                        InetAddress ipPkg = packet.getAddress();
                        int porta = packet.getPort();
                        worker(message,ipPkg,porta);
                        //TRATAR MSG                        
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
            
        }.start();
    }
    
    public void worker(String message,InetAddress ipPkg,int port) throws IOException {
        if(message.contains("SD")){
            int portGrid = Integer.parseInt(message.split(" ")[0].substring(2));
            String portagrid = message.split(" ")[1].trim();
            int portHttp = Integer.parseInt(message.split(" ")[1].trim());
            ServerFriend sf = new ServerFriend(portGrid, portHttp, ipPkg);
            if(!listServers.contains(sf))
                listServers.add(sf);
            sendMessage("AD"+"2080\n", ipPkg,portGrid);
        }else if(message.contains("AD")){
            int portHttp = Integer.parseInt(message.substring(2, 6));
            ServerFriend sf = new ServerFriend(port, portHttp, ipPkg);
            if(!listServers.contains(sf))
                listServers.add(sf);
        }else{
            //tratar
        }
    }
    
    
    public void sendMessage(String message, InetAddress address, int port) throws IOException {
        //datagramSocket = new DatagramSocket();
        if(address.equals(InetAddress.getByName("255.255.255.255")))
            datagramSocket.setBroadcast(true); 
        byte[] buffer = message.getBytes(); 
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
        datagramSocket.send(packet);
        //datagramSocket.close();
    }
}
