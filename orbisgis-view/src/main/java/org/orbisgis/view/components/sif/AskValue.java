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
package org.orbisgis.view.components.sif;

import java.awt.Component;
import java.net.URL;
import java.util.Arrays;
import javax.swing.JTextField;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;

/**
 * This class is used to ask for a value of any kind through a graphical interface.
 * Whatever the input, it will always be validated with
 * {@link AskValue#validateInput() }. The built UI is basically made with a
 * single text field.
 * @author Erwan Bocher
 * @author Alexis Guéganno
 */
public class AskValue implements UIPanel {

        private JTextField txtField;
        private String title;
        private String[] error;
        private String initialValue;

        /**
         * Build a new {@code AskValue}.
         * @param title
         * The title of the window.
         * @param sql
         * An SQL statement - can be empty or null.
         * @param error
         * The error returned if something wrong happens.
         */
        public AskValue(String title, String sql, String error) {
                this(title, sql, error, "");
        }

        /**
         * Build a new {@code AskValue} with an initial value set in the text
         * field.
         * @param title
         * @param sql
         * @param error
         * @param initialValue
         */
        public AskValue(String title, String sql, String error,
                String initialValue) {
                this.title = title;
                this.error = (error == null) ? null : new String[]{error};
                this.initialValue = initialValue;
        }

        @Override
        public Component getComponent() {
                txtField = new JTextField(initialValue);
                return txtField;
        }

        @Override
        public String getTitle() {
                return title;
        }

        @Override
        public String validateInput() {
                return null;
        }

        /**
         * Get the error messages that can be returned by this {@code AskValue}.
         * @return
         */
        public String[] getErrorMessages() {
                return Arrays.copyOf(error, error.length);
        }

        /**
         * Get the value contained in the text field as a String embedded in a
         * String array.
         * @return
         */
        public String[] getValues() {
                return new String[]{txtField.getText()};
        }

        /**
         * Gets the value contained in the text field.
         * @return
         */
        public String getValue() {
                return getValues()[0];
        }

        /**
         *
         * @return
         * {@code null}.
         */
        public String getId() {
                return null;
        }

        /**
         * Sets the value of the text field externally
         * @param fieldName
         * Not used in AskValue, can be null.
         * @param fieldValue
         * The value to be set.
         */
        public void setValue(String fieldName, String fieldValue) {
                txtField.setText(fieldValue);
        }

        @Override
        public URL getIconURL() {
               return UIFactory.getDefaultIcon();
        }
}
