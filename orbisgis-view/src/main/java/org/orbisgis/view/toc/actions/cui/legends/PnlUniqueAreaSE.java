/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.view.toc.actions.cui.legends;

import java.awt.*;
import java.net.URL;
import javax.swing.JPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legends.GeometryProperties;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.constant.UniqueSymbolArea;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;

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
                if(uniqueArea == null){
                        setLegend(new UniqueSymbolArea());
                }
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

        @Override
        public Legend copyLegend() {
                UniqueSymbolArea ret = new UniqueSymbolArea();
                ret.setFillColor(uniqueArea.getFillColor());
                return ret;
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
                JPanel p1 = getLegendBlock(uniqueArea.getParametersLine(), "Line configuration");
                glob.add(p1, gbc);
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 1;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 0, 5, 0);
                JPanel p2 = getLegendBlock(uniqueArea.getParametersArea(), "Fill configuration");
                glob.add(p2, gbc);
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 2;
                glob.add(getPreview(), gbc);
                this.add(glob);
        }
}
