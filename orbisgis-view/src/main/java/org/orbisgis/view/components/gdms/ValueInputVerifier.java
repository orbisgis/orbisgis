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

package org.orbisgis.view.components.gdms;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.text.ParseException;

/**
 * Check for a valid GDMS value.
 * @author Nicolas Fortin
 */
public class ValueInputVerifier extends InputVerifier {
        private DataSource ds;
        private int fieldIndex;
        private static final Logger LOGGER = Logger.getLogger(ValueInputVerifier.class);
        private static final I18n I18N = I18nFactory.getI18n(ValueInputVerifier.class);

        /**
         * @param ds DataSource
         * @param fieldIndex Field index
         */
        public ValueInputVerifier(DataSource ds, int fieldIndex) {
                this.ds = ds;
                this.fieldIndex = fieldIndex;
        }

        @Override
        public boolean shouldYieldFocus(JComponent jComponent) {
                return false;
        }

        /**
         * @param value String representation of the field
         * @return The corresponding value or null value if constraints are not respected.
         * @throws ParseException Unable to parse provided string
         * @throws DriverException The driver throw this exception
         */
        public Value getValue(Object value) throws ParseException, DriverException {
                Type fieldType = ds.getMetadata().getFieldType(fieldIndex);
                Value inputValue;
                if(value!=null && (fieldType.getTypeCode() == Type.STRING || !value.toString().isEmpty())) {
                        inputValue = ValueFactory.createValueByType(value.toString(), fieldType.getTypeCode());
                } else {
                        inputValue = ValueFactory.createNullValue();
                }
                String error = ds.check(fieldIndex, inputValue);
                if (error == null || error.isEmpty()) {
                        return inputValue;
                } else {
                        throw new ParseException(error,0);
                }
        }

        @Override
        public boolean verify(JComponent jComponent) {
                if(jComponent instanceof JTextComponent) {
                        String value = ((JTextField) jComponent).getText();
                        try {
                                getValue(value);
                                return true;
                        } catch (ParseException ex) {
                                LOGGER.error(I18N.tr("The entered value cannot be parsed",ex));
                                return false;
                        }  catch (Exception ex) {
                                LOGGER.error(ex.getLocalizedMessage(),ex);
                                return false;
                        }
                } else {
                        throw new IllegalArgumentException("This verifier is only valid on Text field component");
                }
        }
}
