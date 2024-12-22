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
            Thread.sleep(r.nextLong(min, max) * 1000);
        } catch (InterruptedException ex) {
        }
    }
}
