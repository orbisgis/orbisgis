/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.toc.actions.cui.legends.ui;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.constant.UniqueSymbolArea;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.legends.panels.AreaPanel;
import org.orbisgis.view.toc.actions.cui.legends.panels.LinePanel;
import org.orbisgis.view.toc.actions.cui.legends.panels.PreviewPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;

/**
 * "Unique Symbol - Area" UI.
 *
 * This panel is used to configure a unique symbol configuration for an area symbolizer.
 * @author Alexis Guéganno
 */
public class PnlUniqueAreaSE extends PnlUniqueSymbolSE {

        private static final I18n I18N = I18nFactory.getI18n(PnlUniqueAreaSE.class);

        private UniqueSymbolArea uniqueArea;
        private boolean displayUOM;
        protected boolean isAreaOptional;

        /**
         * Builds a panel based on a new legend.
         */
        public PnlUniqueAreaSE() {
            this(new UniqueSymbolArea());
        }

        /**
         * Builds a panel based on the given legend, displaying the UOM and the
         * Enable Area checkbox.
         *
         * @param legend Legend
         */
        public PnlUniqueAreaSE(UniqueSymbolArea legend) {
            this(legend, true, true);
        }

        /**
         * Builds a panel based on the given legend, hiding the UOM and optionally
         * displaying the Enable Area checkbox.
         *
         * @param legend Legend
         */
        public PnlUniqueAreaSE(UniqueSymbolArea legend,
                               boolean isAreaOptional){
            this(legend, false, isAreaOptional);
        }

        /**
         * Builds a panel based on the given legend, optionally displaying the
         * UOM and the Enable Area checkbox.
         *
         * @param legend Legend
         */
        private PnlUniqueAreaSE(UniqueSymbolArea legend,
                                boolean uom,
                                boolean isAreaOptional){
            this.uniqueArea = legend;
            this.displayUOM = uom;
            this.isAreaOptional = isAreaOptional;
            initPreview();
            initializeLegendFields();
        }

        @Override
        public UniqueSymbolArea getLegend() {
                return uniqueArea;
        }

        @Override
        public void setLegend(Legend legend) {
                throw new UnsupportedOperationException("No longer setting " +
                        "legends this way for unique areas.");
        }

    @Override
        public boolean acceptsGeometryType(int geometryType) {
                return geometryType == SimpleGeometryType.POLYGON||
                        geometryType == SimpleGeometryType.ALL;
        }

        @Override
        public Legend copyLegend() {
                UniqueSymbolArea ret = new UniqueSymbolArea();
                ret.getFillLegend().setColor(uniqueArea.getFillLegend().getColor());
                return ret;
        }

        @Override
        public void initializeLegendFields() {
                this.removeAll();
                JPanel glob = new JPanel(new MigLayout());

                glob.add(new LinePanel(uniqueArea,
                        getPreview(),
                        I18N.tr(BORDER_SETTINGS),
                        true,
                        displayUOM),
                        "cell 0 0, span 1 2, aligny top");

                glob.add(new AreaPanel(uniqueArea,
                        getPreview(),
                        I18N.tr(FILL_SETTINGS),
                        isAreaOptional),
                        "cell 1 0, growx");

                glob.add(new PreviewPanel(getPreview()),
                        "cell 1 1, growx");
                this.add(glob);
        }

        // ************************* UIPanel ***************************
        @Override
        public String getTitle() {
            return UniqueSymbolArea.NAME;
        }
}
