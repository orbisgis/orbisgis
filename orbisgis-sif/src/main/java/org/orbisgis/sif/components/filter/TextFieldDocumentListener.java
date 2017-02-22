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
package org.orbisgis.sif.components.filter;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * This listener update an ActiveLayer at each modification of a JTextField.
 * This listener is used by DataSourceFilterFactories that use a JTextField
 */
public class TextFieldDocumentListener implements DocumentListener {
    private JTextField textField;
    private DefaultActiveFilter activeFilter;
    /**
     * Constructor
     * @param textField TextField to read
     * @param activeFilter filter properties to update
     */
    public TextFieldDocumentListener(JTextField textField,DefaultActiveFilter activeFilter) {
        this.textField = textField;
        this.activeFilter = activeFilter;
    }
    /**
     * User insert characters
     * @param de the document event
     */
    @Override
    public void insertUpdate(DocumentEvent de) {
        SwingUtilities.invokeLater(new updateFilter());
    }
    /**
     * User remove characters
     * @param de the document event
     */
    @Override
    public void removeUpdate(DocumentEvent de) {
        SwingUtilities.invokeLater(new updateFilter());
    }
    /**
     * Gives notification that an attribute or set of attributes changed.
     * @param de the document event
     */
    @Override
    public void changedUpdate(DocumentEvent de) {
    }
    
    /**
     * DocumentListener events is not on the swing thread
     */
    private class updateFilter implements Runnable {

                @Override
                public void run() {
                        activeFilter.setCurrentFilterValue(textField.getText());
                }
            
    }
}
