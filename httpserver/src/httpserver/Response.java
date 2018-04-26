/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

import com.sun.org.apache.xpath.internal.Arg;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 *
 * @author Giovani
 */
public class Response {    
    
    public Response(int statusCode, String url) {
        this.statusCode = statusCode;
        this.content = content;
        this.headerFields = new HashMap<String, String>();
        this.url = url;
        this.gridServer = gridServer;
    }

    public int getStatusCode() {
        return statusCode;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
    
    public Map<String, String> getHeaderFields() {
        return headerFields;
    }

    public void setHeaderFields(Map<String, String> headerFields) {
        this.headerFields = headerFields;
    }
    
    public static void constructResponseHeader(int responseCode, String url, Socket client, GridServer grid, String request) throws IOException {
        Response.gridServer = grid;
        Response response = new Response(responseCode, url);        
        File fiile = null;
        String codedyn="",newHtml="";
        OutputStream fout = new DataOutputStream(client.getOutputStream());
        int filecheck = 0;
        if (response.getStatusCode() == 200) {
            // /mult.cgi?m=4&n=9
            if(response.getUrl().contains(".cgi")){
                String param="";
                if(response.getUrl().contains("?")){
                    param = response.getUrl();
                    int i = param.indexOf("?");
                    param = param.substring(i);
                    param = param.split("=")[1];
                    response.setUrl(response.getUrl().substring(0,i));
                }
                String fileprocessed="";
                newHtml="";
                //WORKER Dynamic Resource CGI
                fiile = new File("src/files/"+response.getUrl());
                if(fiile.exists()){
                    if(fiile.canExecute()){
                        fileprocessed = processFile(fiile,param);
                    }
                    response.setContent(fileprocessed.getBytes());                
                    filecheck=1;
                    
                }else{
                    //CONSULTAR SERVER FRIENDS
                    Boolean result = helpServersFriendly(request);
                }
            }else if(response.getUrl().contains(".dyn")){ //Worker dyn
                fiile = new File("src/files/"+response.getUrl());
                if(fiile.exists()){
                    String result[] = Dyn.readDyn(fiile);
                    String partDyn = result[0];
                    codedyn = result[1];
                    String replace = Dyn.processDyn(partDyn);
                    codedyn = codedyn.replace(" ","");
                    partDyn = partDyn.replace(" ","");
                    codedyn = codedyn.replace(partDyn, replace);
                    File html = generateHtml(codedyn, response.getUrl());
                    response.setContent(Files.readAllBytes(html.toPath()));                
                    filecheck=1;
                    response.getHeaderFields().put("httpversion", "HTTP/1.1 200 OK\r\n");//HTTP/1.1 200 OK                
                    String contenttype = "Content-Type: " + Files.probeContentType(html.toPath()); 
                    response.getHeaderFields().put("content-type", contenttype);//  Content-Type: text/html                
                    String contentlength = "Content-Length: " +String.valueOf(html.length());
                    response.getHeaderFields().put("content-length", contentlength);//  Content-Length: 3495
                }else{
                    //CONSULTAR SERVER FRIENDS
                    Boolean result = helpServersFriendly(request);
                }
            }else if(response.getUrl().equals("")||response.getUrl().equals("/")){
                //SE TENTAR ACESSAR O RAIZ VAI CRIAR UM INDEX.HTML E ACESSÁ-LO
                String codeHtml = genericHtmlDirectory(response.getUrl());
                File html = generateHtml(codeHtml,response.getUrl());
                response.setContent(Files.readAllBytes(html.toPath()));                
                filecheck=1;
                response.getHeaderFields().put("httpversion", "HTTP/1.1 200 OK\r\n");//HTTP/1.1 200 OK                
                String contenttype = "Content-Type: " + Files.probeContentType(html.toPath()); 
                response.getHeaderFields().put("content-type", contenttype);//  Content-Type: text/html                
                String contentlength = "Content-Length: " +String.valueOf(html.length());
                response.getHeaderFields().put("content-length", contentlength);//  Content-Length: 3495
            }else if(!response.getUrl().equals("") && !response.getUrl().contains(".")){
                // Worker Dir
                String codeHtml = genericHtmlDirectory(response.getUrl());
                File html = generateHtml(codeHtml,response.getUrl());
                if(html.exists()){
                    response.setContent(Files.readAllBytes(html.toPath()));                
                    filecheck=1;
                    //HTTP/1.1 200 OK
                    response.getHeaderFields().put("httpversion", "HTTP/1.1 200 OK\r\n");
                    //Content-Type: text/html 
                    String contenttype = "Content-Type: " + Files.probeContentType(html.toPath());
                    response.getHeaderFields().put("content-type", contenttype);
                    // Content-Length: 3495
                    String contentlength = "Content-Length: " +String.valueOf(html.length());
                    response.getHeaderFields().put("content-length", contentlength);
                }else{
                    //CONSULTAR SERVER FRIENDS
                    Boolean result = helpServersFriendly(request);
                }

            }else if(response.getUrl().contains(".")){
                //Outros tipos de arquivos
                fiile = new File("src/files/"+response.getUrl());
                if(fiile.exists()){
                    response.setContent(Files.readAllBytes(fiile.toPath()));
                    filecheck=1;

                    response.getHeaderFields().put("httpversion", "HTTP/1.1 200 OK\r\n");//HTTP/1.1 200 OK                
                    String contenttype = "Content-Type: " + Files.probeContentType(fiile.toPath()); 
                    response.getHeaderFields().put("content-type", contenttype);//  Content-Type: text/html                
                    String contentlength = "Content-Length: " +String.valueOf(fiile.length());
                    response.getHeaderFields().put("content-length", contentlength);//  Content-Length: 3495
                }else{
                    //CONSULTAR SERVER FRIENDS
                    System.out.println("Erro");
                    Boolean result = helpServersFriendly(request);
                }
            }else{
                //CONSULTAR SERVER FRIENDS
                response.getHeaderFields().put("httpversion", "HTTP/1.1 404 Not Found\r\n");
                response.getHeaderFields().put("final", "\r\n");
            }

        }   
        if(!response.getUrl().contains(".cgi")){
            String resp = mountResponseHeader(response);        
            fout.write(resp.getBytes());
            fout.flush();
        }        
        if(filecheck==1){            
            fout.write(response.getContent());
            fout.flush();
        }
        fout.close();
    }
    
    public static String listDir(File directory){
        String retorno = "";
        File file = directory;
        File[] arquivos = file.listFiles();
        File file2;
        if(arquivos!=null){
            for (File fileTmp : arquivos) {
                retorno += "<tr>"+"\n";
                String temp = fileTmp.getPath().substring(10);
                retorno+="<td><a href=\"http://localhost:2080/"+fileTmp.getName()+"\">"+fileTmp.getName()+"</a></td>\n";
                file2 = new File("src/files/"+fileTmp.getName());
                retorno+="<td>"+String.valueOf(file2.length())+"</td>\n";
                retorno += "<tr>"+"\n";
                System.out.println(fileTmp.getName());
            }
        }
        return retorno; 
    }
    private static String genericHtmlDirectory(String url) throws FileNotFoundException, IOException {
        String newHtml = "";
        File directory;
        if(url.equals("")||url.equals("/")){
            directory = new File("src/files/");
        }else{
            directory = new File("src/files/"+url);
        }
        BufferedReader br = new BufferedReader(new FileReader("/home/bertuzzo/NetBeansProjects/httpserver/src/generatedFiles/indexPrev.html"));                
        while(br.ready()){
           String linha = br.readLine();
           newHtml += linha;
           if(linha.contains("</th><th>Size</th></tr>")){
               String addtohtml = listDir2(directory);
               newHtml += addtohtml;
           }                   
        }
        br.close();
        return newHtml;
    }
    private static File generateHtml(String codeHtml, String url) throws IOException {
        File html;
        if(url.equals("")||url.equals("/")){
            html = new File("src/generatedFiles/index.html");
        }else if(url.contains(".dyn")){
            int a = url.lastIndexOf("/");
            String newarq = url.substring(a);
            newarq = url.replace(".dyn", "");
            html = new File("src/generatedFiles"+newarq+".html");
        }else{
            int a = url.lastIndexOf("/");
            String newarq = url.substring(a);
            a = url.lastIndexOf(".");
            if(a>=0)
                newarq = url.substring(0,a);
            html = new File("src/generatedFiles"+newarq+".html");
        }
        html.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(html));
        writer.write(codeHtml);
        writer.flush();
        writer.close();
        return html;
    }
    private static String mountResponseHeader(Response response) {
        String ret = "";
        ret = response.getHeaderFields().get("httpversion");
        for (String key : response.getHeaderFields().keySet()) {
            if(key!="httpversion")
                ret += response.getHeaderFields().get(key)+"\n";
        }
        ret += "\n";
        return ret;
    }
    private static String listDir2(File directory){
        String retorno = "";
        File[] arquivos = directory.listFiles();
        File file;
        if(arquivos!=null){
            for (File fileTmp : arquivos) {
                retorno += "<tr>"+"\n";
                String temp = fileTmp.getPath().substring(10);
                retorno+="<td><a href=\"http://localhost:2080/"+temp+"\">"+fileTmp.getName()+"</a></td>\n";
                file = new File(fileTmp.getPath());
                retorno+="<td>"+String.valueOf(file.length())+"</td>\n";
                retorno += "</tr>"+"\n";
            }
        }
        return retorno; 
    }    
    
    private static String processFile(File file, String param) throws IOException {
        String name = file.getName()/*.split("/")[1]*/;
        String process = "./src/files/"+name+" "+param;
        Process p = Runtime.getRuntime().exec(process);
        InputStream input = p.getInputStream();
        int n;
        String result="";
        while ((n = input.read()) != -1) {  
            //aqui é feita a conversão para char e concatenado à string de saída
            result += (char)n;  
        }  
        return result;
    }
    
    private static Boolean helpServersFriendly(String request) throws IOException {
        String requestHeader = request;
        requestHeader = requestHeader.trim();
        requestHeader = requestHeader + "\nFromServer: True\r\n\r\n";
        String urlrequest = requestHeader.split("\n")[0].split(" ")[1];
        ServerFriend serverSelect = randomServers();
        if(serverSelect!=null){
            DatagramSocket socket = new DatagramSocket(serverSelect.getPortHttp());
            byte[] buf = requestHeader.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            packet = new DatagramPacket(buf, buf.length, serverSelect.getIp(), serverSelect.getPortHttp());
            socket.send(packet);
            socket.close();
            System.out.println("chegou aqui");
            return true;
        }
        return false;
    }
    
    private static ServerFriend randomServers() {
        //Random rand = new Random();
        //int i = rand.nextInt(gridServer.getListServers().size());
        //if(i<=0)
        //    return null;
        //ServerFriend sf = gridServer.getListServers().get(i);
        int j = gridServer.getListServers().size();
        if(j<=0)
            return null;
        ServerFriend sf = gridServer.getListServers().get(j-1);
        return sf;
    }
    
    public int statusCode;
    public byte[] content;   
    public Map<String,String> headerFields;
    public String url;
    public static GridServer gridServer;
}
