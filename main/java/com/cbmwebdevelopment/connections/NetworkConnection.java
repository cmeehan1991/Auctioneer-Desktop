/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.connections;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author cmeehan
 */
public class NetworkConnection extends TimerTask {
    private static TimerTask timerTask;
    
    public static void main(String...args){
        timerTask = new NetworkConnection();
        
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 2500);
    }
    
    /**
     * Cancel the timer task
     */
    public void cancelTask(){
       timerTask.cancel();
    }
    
    /**
     * Run the task to check for a network connection
     */
    @Override 
    public void run(){
        CheckConnection checkConnection = new CheckConnection();
        System.out.println("Running");
        System.out.println(checkConnection.checkConnection());
    }
    
}
