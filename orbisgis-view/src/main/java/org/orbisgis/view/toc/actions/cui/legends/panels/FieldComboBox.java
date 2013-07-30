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
    private MappedLegend legend;

    public FieldComboBox(DataSource ds, final MappedLegend legend) {
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
            String field = legend.getLookupFieldName();
            if (field != null && !field.isEmpty()) {
                setSelectedItem(field);
            }
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateField(legend, (String) ((WideComboBox) e.getSource())
                            .getSelectedItem());
                }
            });
            updateField(legend, (String) getSelectedItem());
        } else {
            LOGGER.error("Cannot construct the field combo box because the " +
                    "DataSource is null.");
        }
    }

    /**
     * Add the fields.
     */
    protected abstract void addFields();

    /**
     * Used when the field against which the analysis is made changes.
     *
     * @param name The new field.
     */
    private void updateField(MappedLegend legend, String name) {
        legend.setLookupFieldName(name);
    }
}
