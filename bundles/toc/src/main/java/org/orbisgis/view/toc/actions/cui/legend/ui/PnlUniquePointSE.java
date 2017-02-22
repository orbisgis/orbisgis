/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.view.toc.actions.cui.legend.ui;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.legend.thematic.constant.UniqueSymbolPoint;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.legend.panels.AreaPanel;
import org.orbisgis.view.toc.actions.cui.legend.panels.PointPanel;
import org.orbisgis.view.toc.actions.cui.legend.panels.PreviewPanel;
import org.orbisgis.view.toc.actions.cui.legend.panels.LinePanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.JPanel;

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
            this(lc, legend, true, true, true);
        }

        /**
         * Builds a panel based on the given legend, hiding the UOM and
         * optionally displaying the Enable Area checkbox.
         *
         * @param legend        Legend
         * @param borderEnabled Is the border enabled?
         */
        public PnlUniquePointSE(UniqueSymbolPoint legend,
                                boolean borderEnabled) {
            this(null, legend, false, false, borderEnabled);
        }

        /**
         * Builds a panel based on the given legend, optionally displaying
         * the UOM and the Enable Area checkbox.
         *
         * @param lc            LegendContext
         * @param legend        Legend
         * @param showCheckbox  Draw the Enable checkbox?
         * @param displayUOM    Display the unit of measure?
         * @param borderEnabled Is the border enabled?
         */
        private PnlUniquePointSE(LegendContext lc,
                                 UniqueSymbolPoint legend,
                                 boolean showCheckbox,
                                 boolean displayUOM,
                                 boolean borderEnabled) {
            super(showCheckbox, displayUOM, borderEnabled);
            this.uniquePoint = legend;
            if (lc != null) {
                this.geometryType = lc.getGeometryType();
            }
            initPreview();
            buildUI();
        }

        @Override
        public UniqueSymbolPoint getLegend() {
                return uniquePoint;
        }

        @Override
        public void buildUI() {
                JPanel glob = new JPanel(new MigLayout("wrap 2"));

                LinePanel linePanel = new LinePanel(uniquePoint,
                        getPreview(),
                        I18N.tr(BORDER_SETTINGS),
                        showCheckbox,
                        displayUOM);
                glob.add(linePanel);

                glob.add(new PointPanel(uniquePoint,
                        getPreview(),
                        I18N.tr(MARK_SETTINGS),
                        displayUOM,
                        geometryType));

                glob.add(new AreaPanel(uniquePoint,
                        getPreview(),
                        I18N.tr(FILL_SETTINGS),
                        showCheckbox));

                glob.add(new PreviewPanel(getPreview()), "growx");

                this.add(glob);
        }

        // ************************* UIPanel ***************************
        @Override
        public String getTitle() {
            return UniqueSymbolPoint.NAME;
        }
}
