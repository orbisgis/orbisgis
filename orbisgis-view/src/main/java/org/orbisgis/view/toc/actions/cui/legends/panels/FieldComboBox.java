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
package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.gdms.data.DataSource;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.legend.LookupFieldName;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.sif.components.WideComboBox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 25/07/13
 * Time: 15:58
 * To change this template use File | Settings | File Templates.
 */
public abstract class FieldComboBox extends WideComboBox {

    private static final Logger LOGGER = Logger.getLogger("gui." + FieldComboBox.class);

    protected DataSource ds;
    protected LookupFieldName legend;

    public FieldComboBox(DataSource ds, final LookupFieldName legend) {
        super();
        this.ds = ds;
        this.legend = legend;
        init();
    }

    /**
     * Creates and fill the combobox that will be used to compute the
     * analysis.
     *
     * @return A ComboBox linked to the underlying MappedLegend that
     *         configures the analysis field.
     */
    private void init() {
        if (ds != null) {
            addFields();
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateField((String) ((WideComboBox) e.getSource())
                            .getSelectedItem());
                }
            });
            String field = legend.getLookupFieldName();
            if (field != null && !field.isEmpty()) {
                setSelectedItem(field);
            }
        } else {
            LOGGER.error("Cannot construct the field combo box because the " +
                    "DataSource is null.");
        }
    }

    /**
     * Add the fields.
     */
    private void addFields() {
        try {
            Metadata md = ds.getMetadata();
            int fc = md.getFieldCount();
            for (int i = 0; i < fc; i++) {
                if (canAddField(i)) {
                    addItem(md.getFieldName(i));
                }
            }
        } catch (DriverException ex) {
            LOGGER.error(ex);
        }
    }

    /**
     * Determine which kind of fields to add.
     */
    protected abstract boolean canAddField(int index);

    /**
     * Used when the field against which the analysis is made changes.
     *
     * @param name The new field.
     */
    protected void updateField(String name) {
        legend.setLookupFieldName(name);
    }
}
