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
/**
 * Purpose: Keeps track of current position in GridBagLayout.
Supports a few GridBag features: position, width, height, expansion.
All methods return GBHelper object for call chaining.
Author : Fred Swartz - January 30, 2007 - Placed in public domain.
 *
 */
package org.orbisgis.core.ui.components.findReplace;

import java.awt.*;

public class GBHelper extends GridBagConstraints {

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
         */
        public GBHelper nextCol() {
                gridx++;
                return this;
        }

        /**
         * Moves the helper's cursor to first col in next row.
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
         */
        public GBHelper expandW() {
                GBHelper duplicate = (GBHelper) this.clone();
                duplicate.weightx = 1.0;
                return duplicate;
        }

        /**
         * Expandable Height. Returns new helper allowing vertical expansion.
         */
        public GBHelper expandH() {
                GBHelper duplicate = (GBHelper) this.clone();
                duplicate.weighty = 1.0;
                return duplicate;
        }

        /**
         * Sets the width of the area in terms of number of columns.
         */
        public GBHelper width(int colsWide) {
                GBHelper duplicate = (GBHelper) this.clone();
                duplicate.gridwidth = colsWide;
                return duplicate;
        }

        /**
         * Width is set to all remaining columns of the grid.
         */
        public GBHelper width() {
                GBHelper duplicate = (GBHelper) this.clone();
                duplicate.gridwidth = REMAINDER;
                return duplicate;
        }

        /**
         * Sets the height of the area in terms of rows.
         */
        public GBHelper height(int rowsHigh) {
                GBHelper duplicate = (GBHelper) this.clone();
                duplicate.gridheight = rowsHigh;
                return duplicate;
        }

        /**
         * Height is set to all remaining rows.
         */
        public GBHelper height() {
                GBHelper duplicate = (GBHelper) this.clone();
                duplicate.gridheight = REMAINDER;
                return duplicate;
        }

        /**
         * Alignment is set by parameter.
         */
        public GBHelper align(int alignment) {
                GBHelper duplicate = (GBHelper) this.clone();
                duplicate.fill = NONE;
                duplicate.anchor = alignment;
                return duplicate;
        }
}
