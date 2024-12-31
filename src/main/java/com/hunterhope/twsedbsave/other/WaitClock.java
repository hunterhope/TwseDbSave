/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hunterhope.twsedbsave.other;

import java.util.Random;

/**
 *
 * @author user
 */
public class WaitClock {

    private Random r = new Random();

    public void waitForSecurity(int min, int max) {
        try {
            long waitTime = r.nextLong(min, max) * 1000;
            System.out.print("執行等待時間:"+waitTime);
            Thread.sleep(waitTime);
            System.out.println(" 等待時間結束");
        } catch (InterruptedException ex) {
        }
    }
}
