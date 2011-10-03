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
