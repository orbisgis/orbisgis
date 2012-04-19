/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.thematic;

import org.orbisgis.legend.Legend;

/**
 * Gathers method that are common to all the {@code Legend} realizations.
 *
 * @author alexis
 */
public abstract class SymbolizerLegend implements Legend {

        @Override
        public String getName() {
                return getSymbolizer().getName();
        }

        @Override
        public void setName(String name) {
                getSymbolizer().setName(name);
        }

        @Override
        public Double getMinScale() {
                return getSymbolizer().getRule().getMinScaleDenom();
        }

        @Override
        public void setMinScale(Double scale) {
                getSymbolizer().getRule().setMinScaleDenom(scale);
        }

        @Override
        public Double getMaxScale() {
                return getSymbolizer().getRule().getMaxScaleDenom();
        }

        @Override
        public void setMaxScale(Double scale) {
                getSymbolizer().getRule().setMaxScaleDenom(scale);
        }

	@Override
	public String getLegendTypeId(){
		return "org.orbisgis.legend.thematic.SymbolizerLegend";
	}

}
