/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.view.toc.actions.cui.legends;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.URL;
import javax.swing.JPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legends.GeometryProperties;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.constant.UniqueSymbolLine;
import org.orbisgis.sif.SIFMessage;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;

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
                        initPreview();
                        initializeLegendFields();
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
        public SIFMessage validateInput() {
                return new SIFMessage();
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
        public SIFMessage initialize() {
                return new SIFMessage();
        }

        @Override
        public SIFMessage postProcess() {
                return new SIFMessage();
        }

        @Override
        public String getInfoText() {
                return "Configure a line to be displayed as a unique symbol.";
        }

        @Override
        public Legend copyLegend() {
                UniqueSymbolLine usl = new UniqueSymbolLine();
                usl.setDashArray(uniqueLine.getDashArray());
                usl.setLineWidth(uniqueLine.getLineWidth());
                usl.setLineColor(uniqueLine.getLineColor());
                return usl;
        }

        private void initializeLegendFields(){
                this.removeAll();
                JPanel glob = new JPanel();
                GridBagLayout grid = new GridBagLayout();
                glob.setLayout(grid);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                JPanel p1 = getLegendBlock(uniqueLine.getParametersLine(), "Line configuration");
                glob.add(p1, gbc);
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 1;
                glob.add(getPreview(), gbc);
                this.add(glob);
        }

        
}
