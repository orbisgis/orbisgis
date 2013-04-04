/*
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

package org.orbisgis.view.table;

import org.apache.log4j.Logger;
import org.orbisgis.view.components.gdms.ValueInputVerifier;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Component;

/**
 * This generic cell editor read the input validator of the field while stop editing.
 * @author Nicolas Fortin
 */
public class ValidatorCellEditor extends DefaultCellEditor {
        private static final Logger LOGGER = Logger.getLogger(ValidatorCellEditor.class);
        private ValueInputVerifier valueInputVerifier;

        /**
         * @param valueInputVerifier Value checker and converter from string
         */
        public ValidatorCellEditor(ValueInputVerifier valueInputVerifier) {
                super(new JTextField());
                this.valueInputVerifier = valueInputVerifier;
        }

        @Override
        public Object getCellEditorValue() {
                try {
                        return valueInputVerifier.getValue(super.getCellEditorValue());
                } catch (Exception ex) {
                        LOGGER.error(ex.getLocalizedMessage(),ex);
                        return super.getCellEditorValue();
                }
        }

        @Override
        public boolean stopCellEditing() {
                Component component = getComponent();
                if(component instanceof JComponent) {
                        JComponent jComponent = ((JComponent) component);
                        if(!valueInputVerifier.verify(jComponent)) {
                                jComponent.setBorder(new LineBorder(Color.red));
                                return false;
                        }
                }
                return super.stopCellEditing();
        }
}
