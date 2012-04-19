/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.fill;

import java.awt.Color;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.legend.structure.categorize.Categorize2ColorLegend;

/**
 * A {@code Legend} that represents a {@code SolidFill} where the color is defined
 * accorgind to a {@code Categorize} operation.
 * @author alexis
 */
public class CategorizedSolidFillLegend extends SolidFillLegend {

        /**
         * Build a new {@code CategorizedSolidFillLegend} using the {@code 
         * SolidFill} and {@code Categorize2ColorLegend} given in parameter.
         * @param fill
         * @param colorLegend
         */
        public CategorizedSolidFillLegend(SolidFill fill, Categorize2ColorLegend colorLegend) {
                super(fill, colorLegend);
        }

        /**
         * Get the colour returned if the input data can't be associated to one
         * of the given interval.
         * @return
         */
        public Color getFallBackColor() {
            return ((Categorize2ColorLegend) getColorLegend()).getFallBackColor();
        }

        /**
         * Set the colour returned if the input data can't be associated to one
         * of the given interval.
         * @param col
         */
        public void setFallBackColor(Color col) {
            ((Categorize2ColorLegend) getColorLegend()).setFallBackColor(col);
        }

        /**
         * Get the {@code Color} that is returned for input values that are inferior
         * to the first threshold.
         * @param i
         * The index of the class we want to retrieve the {@code Color} from.
         * @return
         */
        public Color getColor(int i) {
            return ((Categorize2ColorLegend) getColorLegend()).getColor(i);
        }

        /**
         * Set the {@code Color} that is returned for input values that are inferior
         * to the first threshold.
         * @param i
         * The index of the class we want to set the {@code Color}.
         * @param col
         */
        public void setColor(int i, Color col) {
            ((Categorize2ColorLegend) getColorLegend()).setColor(i, col);
        }

        /**
         * Get the number of classes defined in the inner {@code Categorize}.
         * @return
         */
        public int getNumClass() {
            return ((Categorize2ColorLegend) getColorLegend()).getNumClass();
        }

        /**
         * Get the value of the ith threshold.
         * @param i
         * @return
         */
        public double getThreshold(int i) {
            return ((Categorize2ColorLegend) getColorLegend()).getThreshold(i);
        }

        /**
         * Get the value of the ith threshold.
         * @param i
         * @param d
         */
        public void setThreshold(int i, double d) {
            ((Categorize2ColorLegend) getColorLegend()).setThreshold(i, d);
        }

        /**
         * Add a class to the inner {@code Categorize}.
         * @param threshold
         * @param col
         */
        public void addClass(double threshold, Color col){
            ((Categorize2ColorLegend) getColorLegend()).addClass(threshold, col);
        }

        /**
         * Remove the ith class of this {@code Categorize}.
         * @param i
         */
        public void removeClass(int i) {
            ((Categorize2ColorLegend) getColorLegend()).removeClass(i);
        }

}
