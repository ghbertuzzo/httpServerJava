/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

import java.net.InetAddress;

/**
 *
 * @author bertuzzo
 */
public class ServerFriend {

    public ServerFriend(int portGrid, int portHttp, InetAddress ip) {
        this.portGrid = portGrid;
        this.portHttp = portHttp;
        this.ip = ip;
    }

    public int getPortGrid() {
        return portGrid;
    }

    public void setPortGrid(int portGrid) {
        this.portGrid = portGrid;
    }

    public int getPortHttp() {
        return portHttp;
    }

    public void setPortHttp(int portHttp) {
        this.portHttp = portHttp;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }
    
    int portGrid;
    int portHttp;
    InetAddress ip;
    
}
