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
package org.orbisgis.sif.multiInputPanel;

import java.awt.Component;
import javax.swing.JCheckBox;

/**
 * {@code InputType} particularly designed for booleans : if the inner {@code
 * JCheckbox} is checked, the boolean will be true, false otherwise. Note that
 * as {@code InputType} returns only {@code String} values through its {@code
 * {@code getValue} method, the boolean will be returned as a String...
 * @author Erwan Bocher
 * @author Alexis Guéganno
 */
public class CheckBoxChoice implements InputType {

        private JCheckBox jCheckBox;

        public CheckBoxChoice(boolean b) {
                jCheckBox = new JCheckBox();
                jCheckBox.setSelected(b);
        }

        @Override
        public Component getComponent() {
                return jCheckBox;
        }       

        /**
         * The {@code String} representation of the associated {@code boolean}.
         * @return
         */
        @Override
        public String getValue() {
                return Boolean.toString(jCheckBox.isSelected());
        }

        /**
         * Set the value stored in this {@code CheckBoxChoice}. If {@code value}
         * is equal to the string {@code true}, ignoring case, the associated
         * checkbox will be checked. If value is null or equal to anything else,
         * the cehckbox will be unchecked.
         * @param value
         */
        @Override
        public void setValue(String value) {
                jCheckBox.setSelected(Boolean.parseBoolean(value));
        }
}
