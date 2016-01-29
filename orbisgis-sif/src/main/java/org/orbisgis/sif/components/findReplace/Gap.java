/**
 * OrbisGIS is a GIS application dedicated to scientific spatial analysis.
 * This cross-platform GIS is developed at the Lab-STICC laboratory by the DECIDE 
 * team located in University of South Brittany, Vannes.
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
 * Copyright (C) 2015-2016 CNRS (UMR CNRS 6285)
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
/*
Purpose: Gaps for use in GridBagLayout (or any other).
Library alternatives are available in the Box class.
Author : Fred Swartz - January 30, 2007 - Placed in public domain.
 */
package org.orbisgis.sif.components.findReplace;

import java.awt.Dimension;
import javax.swing.JComponent;
/**
 * Invisible space component.
 */
public class Gap extends JComponent {
        private static final long serialVersionUID = 1L;

        /**
         * Creates filler with minimum size, but expandable infinitely.
         */
        public Gap() {
                Dimension min = new Dimension(0, 0);
                Dimension max = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
                setMinimumSize(min);
                setPreferredSize(min);
                setMaximumSize(max);
        }

        /**
         * Creates rigid filler. 
         * @param size Width and height of the component
         */
        public Gap(int size) {
                Dimension dim = new Dimension(size, size);
                setMinimumSize(dim);
                setPreferredSize(dim);
                setMaximumSize(dim);
        }
}
