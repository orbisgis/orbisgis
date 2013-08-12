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
import org.orbisgis.legend.thematic.constant.UniqueSymbolPoint;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.legends.panels.AreaPanel;
import org.orbisgis.view.toc.actions.cui.legends.panels.LinePanel;
import org.orbisgis.view.toc.actions.cui.legends.panels.PointPanel;
import org.orbisgis.view.toc.actions.cui.legends.panels.PreviewPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;

/**
 * "Unique Symbol - Point" UI.
 *
 * @author Alexis Guéganno
 * @author Adam Gouge
 */
public final class PnlUniquePointSE extends PnlUniqueSymbolSE {

        private static final I18n I18N = I18nFactory.getI18n(PnlUniquePointSE.class);

        private UniqueSymbolPoint uniquePoint;
        private int geometryType = SimpleGeometryType.ALL;

        private boolean uom;
        private boolean enableArea;

        /**
         * Builds a panel based on a new legend.
         *
         * @param lc     LegendContext
         */
        public PnlUniquePointSE(LegendContext lc) {
            this(lc, new UniqueSymbolPoint());
        }

        /**
         * Builds a panel based on the given legend, displaying the UOM and
         * the Enable Area checkbox.
         *
         * @param lc     LegendContext
         * @param legend Legend
         */
        public PnlUniquePointSE(LegendContext lc, UniqueSymbolPoint legend) {
            this(lc, legend, true, true);
        }

        /**
         * Builds a panel based on the given legend, hiding the UOM and
         * optionally displaying the Enable Area checkbox.
         *
         * @param legend        Legend
         * @param enableArea    True if the Enable Area checkbox should be
         *                      displayed
         */
        public PnlUniquePointSE(UniqueSymbolPoint legend,
                                boolean enableArea) {
            this(null, legend, false, enableArea);
        }

        /**
         * Builds a panel based on the given legend, optionally displaying
         * the UOM and the Enable Area checkbox.
         *
         * @param lc            LegendContext
         * @param legend        Legend
         * @param uom           True if the UOM should be displayed
         * @param enableArea    True if the Enable Area checkbox should be
         *                      displayed
         */
        private PnlUniquePointSE(LegendContext lc,
                                UniqueSymbolPoint legend,
                                boolean uom,
                                boolean enableArea) {
            this.uniquePoint = legend;
            this.uom = uom;
            this.enableArea = enableArea;
            if (lc != null) {
                this.geometryType = lc.getGeometryType();
            }
            initPreview();
            initializeLegendFields();
        }

        @Override
        public UniqueSymbolPoint getLegend() {
                return uniquePoint;
        }

        @Override
        public void setLegend(Legend legend) {
                throw new UnsupportedOperationException("No longer setting " +
                        "legends this way for unique points.");
        }

        @Override
        public void initializeLegendFields() {
                this.removeAll();
                JPanel glob = new JPanel(new MigLayout("wrap 2"));

                glob.add(new LinePanel(uniquePoint,
                        getPreview(),
                        I18N.tr(BORDER_SETTINGS),
                        true,
                        uom));

                glob.add(new PointPanel(uniquePoint,
                        getPreview(),
                        I18N.tr(MARK_SETTINGS),
                        uom,
                        geometryType));

                glob.add(new AreaPanel(uniquePoint,
                        getPreview(),
                        I18N.tr(FILL_SETTINGS),
                        enableArea));

                glob.add(new PreviewPanel(getPreview()), "growx");

                this.add(glob);
        }

        // ************************* UIPanel ***************************
        @Override
        public String getTitle() {
            return UniqueSymbolPoint.NAME;
        }
}
