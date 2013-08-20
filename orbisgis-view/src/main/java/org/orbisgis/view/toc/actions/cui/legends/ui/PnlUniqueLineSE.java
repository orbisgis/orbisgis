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
import org.orbisgis.legend.thematic.constant.UniqueSymbolLine;
import org.orbisgis.view.toc.actions.cui.legends.panels.LinePanel;
import org.orbisgis.view.toc.actions.cui.legends.panels.PreviewPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;

/**
 * "Unique Symbol - Line" UI.
 *
 * @author Alexis Guéganno
 * @author Adam Gouge
 */
public final class PnlUniqueLineSE extends PnlUniqueSymbolSE {

        private static final I18n I18N = I18nFactory.getI18n(PnlUniqueLineSE.class);

        public static final String LINE_SETTINGS = I18n.marktr("Line settings");

        private UniqueSymbolLine uniqueLine;

        /**
         * Builds a panel based on a new legend.
         */
        public PnlUniqueLineSE() {
            this(new UniqueSymbolLine());
        }

        /**
         * Builds a panel based on the given legend, displaying the UOM.
         *
         * @param legend Legend
         */
        public PnlUniqueLineSE(UniqueSymbolLine legend) {
            this(legend, true);
        }

        /**
         * Builds a panel based on the given legend, optionally displaying the
         * UOM.
         *
         * @param legend        Legend
         * @param displayUOM    True if the UOM should be displayed
         */
        public PnlUniqueLineSE(UniqueSymbolLine legend, boolean displayUOM) {
            super(false, displayUOM, true);
            this.uniqueLine = legend;
            initPreview();
            buildUI();
        }

        @Override
        public UniqueSymbolLine getLegend() {
                return uniqueLine;
        }

        @Override
        public void buildUI() {
                JPanel glob = new JPanel(new MigLayout());
                glob.add(new LinePanel(uniqueLine,
                        getPreview(),
                        I18N.tr(LINE_SETTINGS),
                        showCheckbox,
                        displayUOM));
                glob.add(new PreviewPanel(getPreview()));
                this.add(glob);
        }

        // ************************* UIPanel ***************************
        @Override
        public String getTitle() {
            return UniqueSymbolLine.NAME;
        }
}
