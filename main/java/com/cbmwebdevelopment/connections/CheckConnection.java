/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.connections;

import java.io.IOException;
import java.net.InetAddress;

/**
 *
 * @author cmeehan
 */
public class CheckConnection {
    public boolean checkConnection(){
        boolean connection;
        try{
            System.out.println("Getting Address");
            InetAddress addr = InetAddress.getByName("www.google.com");
            for(byte ad : addr.getAddress()){
                System.out.println(String.valueOf(ad));
            }
            connection = true;
        }catch(IOException ex){
            System.err.println(ex.getMessage());
            connection = false;
        }
        return connection;
    }
}
