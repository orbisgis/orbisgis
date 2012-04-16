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
import org.orbisgis.legend.thematic.constant.UniqueSymbolLine;

/**
 *
 * @author alexis
 */
public class PnlUniqueLineSE extends PnlUniqueSymbolSE {

        /**
         * Here we can put all the Legend instances we want... but they have to
         * be unique symbol (ie constant) Legends.
         */
        private UniqueSymbolLine uniqueLine;
        private LegendContext legendContext ;
        

        @Override
        public Component getComponent() {
                return this;
        }

        @Override
        public Legend getLegend() {
                return uniqueLine;
        }

        @Override
        public void setLegend(Legend legend) {
                if(legend instanceof UniqueSymbolLine){
                        uniqueLine = (UniqueSymbolLine) legend;
                        this.initializeLegendFields();
                } else {
                        throw new IllegalArgumentException("The given Legend is not"
                                + "a UniqueSymbolLine");
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
                if(uniqueLine == null){
                        setLegend(new UniqueSymbolLine());
                }
                legendContext = lc;
        }

        @Override
        public boolean acceptsGeometryType(int geometryType) {
                return geometryType == GeometryProperties.LINE;
        }

        @Override
        public ILegendPanel newInstance() {
                return new PnlUniqueLineSE();
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
                UniqueSymbolLine usl = new UniqueSymbolLine();
                usl.setDashArray(uniqueLine.getDashArray());
                usl.setLineWidth(uniqueLine.getLineWidth());
                usl.setLineColor(uniqueLine.getLineColor());
                return usl;
        }

        
}
