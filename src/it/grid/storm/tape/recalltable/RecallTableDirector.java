/**
 * 
 */
package it.grid.storm.tape.recalltable;

import java.io.IOException;

/**
 * @author zappi
 *
 */
public class RecallTableDirector {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            RecallTableService.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
