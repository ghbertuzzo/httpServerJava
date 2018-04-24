/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author bertuzzo
 */
public class Dyn {

    static String[] readDyn(File fiile) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(fiile));
        String partDyn="";
        String codedyn="";
        int flag = 0;
        while(br.ready()){
            String linha = br.readLine();
            codedyn += linha;
            if(flag==1){
                partDyn += linha;
                if(linha.contains("?>")){                            
                    flag=0;
                }
            }
           if(linha.contains("<?php")){
               flag=1;
               partDyn += linha;
           }                   
        }
        br.close();
        String[] ret = new String[2];
        ret[0]=partDyn;
        ret[1]=codedyn;
        return ret;
    }

    public Dyn(String functionName, String functionParam) {
        this.functionName = functionName;
        this.functionParam = functionParam;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionParam() {
        return functionParam;
    }

    public void setFunctionParam(String functionParam) {
        this.functionParam = functionParam;
    }

    public String getFunctionReturn() {
        return functionReturn;
    }

    public void setFunctionReturn(String functionReturn) {
        this.functionReturn = functionReturn;
    }
    
    private static String processGetParam(String functionParam) {
        return "null";
    }

    private static String processGetHeaderField(String functionParam) {
        return "null";
    }
    
    public static String processDyn(String partDyn) {
        String ret="";
        partDyn = partDyn.trim();
        partDyn = partDyn.replace("<?php", "");
        partDyn = partDyn.replace("?>", "");
        partDyn = partDyn.trim();
        String[] part = partDyn.split(";");
        ArrayList<Dyn> listDyn = new ArrayList<Dyn>();
        int size = part.length;
        for(int i=0;i<size;i++){
            int indexinicial = part[i].indexOf("(");
            int indexfinal = part[i].lastIndexOf(")");
            String fname = part[i].substring(0, indexinicial);
            String fparam = part[i].substring(indexinicial+1, indexfinal);
            Dyn dyn = new Dyn(fname, fparam);
            listDyn.add(dyn);
        }
        ret = getResultDyn(listDyn);
        
        return ret;
    }
    private static String getResultDyn(ArrayList<Dyn> listDyn) {
        String ret="";
        for(Dyn dyn: listDyn){
            dyn.setFunctionReturn(processFuncDyn(dyn.getFunctionName(),dyn.getFunctionParam()));
            ret += "<p><h2>"+dyn.getFunctionReturn()+"</h2></p>\n";
        }
        return ret;
    }
    private static String processFuncDyn(String functionName, String functionParam) {
        String ret="";
        
        if(functionName.equals("date")){
            ret = processDate(functionParam);
        }else if(functionName.equals("getParam")){
            ret = processGetParam(functionParam);
        }else if(functionName.equals("getHeaderField")){
            ret = processGetHeaderField(functionParam);
        }else{
            
        }
        
        return ret;
    }    
    private static String processDate(String functionParam) {
        if(functionParam.equals("\"dd/MM/yyyy\"")){     
            SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
            Date dataAtual = new Date(System.currentTimeMillis());
            String data = sd.format(dataAtual);        
            return "Data de hoje: "+data;
        }else{
            return "erroData";
        }
    }
    String functionName;
    String functionParam;
    String functionReturn;
    
}
