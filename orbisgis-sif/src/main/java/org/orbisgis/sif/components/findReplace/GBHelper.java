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
package org.orbisgis.sif.components.findReplace;

import java.awt.GridBagConstraints;

public class GBHelper extends GridBagConstraints {
        private static final long serialVersionUID = 1L;

        /**
         * Creates helper at top left, component always fills cells
         */
        public GBHelper() {
                gridx = 0;
                gridy = 0;
                fill = GridBagConstraints.BOTH;  // Component fills area
        }

        /**
         * Moves the helper's cursor to the right one column.
         * @return this
         */
        public GBHelper nextCol() {
                gridx++;
                return this;
        }

        /**
         * Moves the helper's cursor to first col in next row.
         * @return this
         */
        public GBHelper nextRow() {
                gridx = 0;
                gridy++;
                return this;
        }

        /**
         * Expandable Width.  Returns new helper allowing horizontal expansion.
        A new helper is created so the expansion values don't
        pollute the origin helper.
        * @return this
        */
        public GBHelper expandW() {
                GBHelper duplicate = (GBHelper) this.clone();
                duplicate.weightx = 1.0;
                return duplicate;
        }

        /**
         * Expandable Height. Returns new helper allowing vertical expansion.
         * @return this
         */
        public GBHelper expandH() {
                GBHelper duplicate = (GBHelper) this.clone();
                duplicate.weighty = 1.0;
                return duplicate;
        }

        /**
         * Sets the width of the area in terms of number of columns.
         * @param colsWide Column count
         * @return this
         */
        public GBHelper width(int colsWide) {
                GBHelper duplicate = (GBHelper) this.clone();
                duplicate.gridwidth = colsWide;
                return duplicate;
        }

        /**
         * Width is set to all remaining columns of the grid.
         * @return this
         */
        public GBHelper width() {
                GBHelper duplicate = (GBHelper) this.clone();
                duplicate.gridwidth = REMAINDER;
                return duplicate;
        }

        /**
         * Sets the height of the area in terms of rows.
         * @param rowsHigh Row count
         * @return this 
         */
        public GBHelper height(int rowsHigh) {
                GBHelper duplicate = (GBHelper) this.clone();
                duplicate.gridheight = rowsHigh;
                return duplicate;
        }

        /**
         * Height is set to all remaining rows.
         * @return this
         */
        public GBHelper height() {
                GBHelper duplicate = (GBHelper) this.clone();
                duplicate.gridheight = REMAINDER;
                return duplicate;
        }

        /**
         * Alignment is set by parameter.
         * @param alignment Anchor alignment
         * @return this 
         */
        public GBHelper align(int alignment) {
                GBHelper duplicate = (GBHelper) this.clone();
                duplicate.fill = NONE;
                duplicate.anchor = alignment;
                return duplicate;
        }
}
