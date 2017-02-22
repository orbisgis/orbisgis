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
import org.orbisgis.legend.thematic.constant.UniqueSymbolLine;
import org.orbisgis.view.toc.actions.cui.legend.panels.PreviewPanel;
import org.orbisgis.view.toc.actions.cui.legend.panels.LinePanel;
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
