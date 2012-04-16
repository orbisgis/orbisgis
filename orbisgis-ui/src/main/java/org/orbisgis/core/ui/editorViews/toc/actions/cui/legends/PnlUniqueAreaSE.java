/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui.legends;

import java.awt.Color;
import java.awt.Component;
import java.net.URL;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.ConstraintSymbolFilter;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendContext;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.SymbolFilter;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.constant.UniqueSymbolArea;

/**
 *
 * @author alexis
 */
public class PnlUniqueAreaSE extends PnlUniqueSymbolSE {

        /**
         * Here we can put all the Legend instances we want... but they have to
         * be unique symbol (ie constant) Legends.
         */
        private UniqueSymbolArea uniqueArea;
        private LegendContext legendContext ;
        

        @Override
        public Component getComponent() {
                return this;
        }

        @Override
        public Legend getLegend() {
                return uniqueArea;
        }

        @Override
        public void setLegend(Legend legend) {
                if(legend instanceof UniqueSymbolArea){
                        uniqueArea = (UniqueSymbolArea) legend;
                        this.initializeLegendFields();
                } else {
                        throw new IllegalArgumentException("The given Legend is not"
                                + "a UniqueSymbolPoint");
                }
        }

	/**
	 * Initialize the panel. This method is called just after the panel
	 * creation.</p>
         * <p>WARNING : the panel will be empty after calling this method. Indeed,
         * there won't be any {@code Legend} instance associated to it. Use the
         * {@code setLegend} method to achieve this goal.
	 *
	 * @param lc
	 *            LegendContext is useful to get some information about the
	 *            layer in edition.
	 */
        @Override
        public void initialize(LegendContext lc) {
                if(uniqueArea == null){
                        setLegend(new UniqueSymbolArea());
                }
                legendContext = lc;
        }

        @Override
        public boolean acceptsGeometryType(int geometryType) {
                return geometryType == GeometryProperties.POLYGON;
        }

        @Override
        public ILegendPanel newInstance() {
                return new PnlUniqueAreaSE();
        }

        @Override
        public String validateInput() {
                return null;
        }

        @Override
        public URL getIconURL() {
		return UIFactory.getDefaultIcon();
        }

        @Override
        public String getTitle() {
                return "Unique symbol for lines.";
        }

        @Override
        public String initialize() {
                return null;
        }

        @Override
        public String postProcess() {
                return null;
        }

        @Override
        public String getInfoText() {
                return "Configure a line to be displayed as a unique symbol.";
        }

	private SymbolFilter getSymbolFilter() {
		return new ConstraintSymbolFilter(new Type[]{TypeFactory.createType(Type.GEOMETRY)});
	}


        @Override
        public Legend copyLegend() {
                UniqueSymbolArea ret = new UniqueSymbolArea();
                ret.setFillColor(uniqueArea.getFillColor());
                return ret;
        }
}
