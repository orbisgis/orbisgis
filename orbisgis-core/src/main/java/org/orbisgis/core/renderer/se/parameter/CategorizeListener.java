/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.parameter;

/**
 * Listens for modifications in a Categorize.
 * @author maxence
 */
public interface CategorizeListener {

        /**
         * fired when the class i has been revoved
         * 
         * @param i 
         */
        void classRemoved(int i);

        /** 
         * the iest class is a new one
         * 
         * @param i 
         */
        void classAdded(int i);

        /**
         *  the iest has been moven at jest position
         * @param i
         * @param j 
         */
        void classMoved(int i, int j);

        void thresholdResorted();
}
