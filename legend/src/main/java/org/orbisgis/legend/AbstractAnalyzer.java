/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend;

/**
 * A simple base for {@code Analyzer} instances. It gathers instances of {@code
 * LegendStructure} in a {@code List}.
 * @author alexis
 */
public abstract class AbstractAnalyzer implements Analyzer {

        private LegendStructure legend;

        @Override
        public LegendStructure getLegend(){
                return legend;
        }

        /**
         * Set the {@code LegendStructure} associated to this {@code Analyzer}.
         * @param leg
         */
        public void setLegend(LegendStructure leg){
                legend = leg;
        }

}
