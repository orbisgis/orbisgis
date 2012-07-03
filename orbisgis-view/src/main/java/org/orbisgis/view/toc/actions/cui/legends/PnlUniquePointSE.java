/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.view.toc.actions.cui.legends;

import java.awt.*;
import java.net.URL;
import javax.swing.JPanel;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legends.GeometryProperties;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.constant.UniqueSymbolPoint;

/**
 *
 * @author alexis
 */
public class PnlUniquePointSE extends PnlUniqueSymbolSE {

        /**
         * Here we can put all the Legend instances we want... but they have to
         * be unique symbol (ie constant) Legends.
         */
        private UniqueSymbolPoint uniquePoint;
        

        @Override
        public Component getComponent() {
                return this;
        }

        @Override
        public Legend getLegend() {
                return uniquePoint;
        }

        @Override
        public void setLegend(Legend legend) {
                if(legend instanceof UniqueSymbolPoint){
                        uniquePoint = (UniqueSymbolPoint) legend;
                        initPreview();
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
                if(uniquePoint == null){
                        setLegend(new UniqueSymbolPoint());
                }
        }

        @Override
        public boolean acceptsGeometryType(int geometryType) {
                return geometryType == GeometryProperties.POINT;
        }

        @Override
        public ILegendPanel newInstance() {
                return new PnlUniquePointSE();
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

        @Override
        public Legend copyLegend() {
                return new UniqueSymbolPoint();
        }

        private void initializeLegendFields(){
                this.removeAll();
                JPanel glob = new JPanel();
                GridBagLayout grid = new GridBagLayout();
                glob.setLayout(grid);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                JPanel p1 = getLegendBlock(uniquePoint.getParametersLine(), "Line configuration");
                glob.add(p1, gbc);
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 1;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 0, 5, 0);
                JPanel p2 = getLegendBlock(uniquePoint.getParametersArea(), "Fill configuration");
                glob.add(p2, gbc);
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 2;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 0, 5, 0);
                JPanel p3 = getLegendBlock(uniquePoint.getParametersPoint(), "Mark configuration");
                glob.add(p3, gbc);
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 3;
                glob.add(getPreview(), gbc);
                this.add(glob);
        }
        
}
