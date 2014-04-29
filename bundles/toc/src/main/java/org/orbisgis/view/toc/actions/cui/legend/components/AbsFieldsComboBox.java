/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.toc.actions.cui.legend.components;

import org.apache.log4j.Logger;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.LookupFieldName;
import org.orbisgis.sif.components.WideComboBox;

import javax.sql.DataSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Root class for combo boxes containing field names.
 *
 * @author Adam Gouge
 */
public abstract class AbsFieldsComboBox extends AbsComboBox {

    private static final Logger LOGGER = Logger.getLogger(AbsFieldsComboBox.class);

    protected String tableIdentifier;
    protected DataSource dataSource;

    /**
     * Constructor
     *
     * @param tableIdentifier     DataSource
     * @param legend Legend
     */
    public AbsFieldsComboBox(DataSource dataSource, String tableIdentifier, final LookupFieldName legend) {
        super((Legend) legend);
        this.dataSource = dataSource;
        if (tableIdentifier == null) {
            throw new IllegalStateException("A FieldsComboBox requires " +
                    "a non-null DataSource.");
        }
        this.tableIdentifier = tableIdentifier;
    }

    private LookupFieldName getLegend() {
        return (LookupFieldName) legend;
    }

    /**
     * Initializes the combo box.
     */
    protected void init() {
        addFields();
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateField((String) ((WideComboBox) e.getSource())
                        .getSelectedItem());
            }
        });
        String field = getLegend().getLookupFieldName();
        if (field != null && !field.isEmpty()) {
            setSelectedItem(field);
        }
        updateField((String) getSelectedItem());
    }

    /**
     * Add the fields.
     */
    private void addFields() {
        TableLocation tableLocation = TableLocation.parse(tableIdentifier);
        try(Connection connection = dataSource.getConnection();
            ResultSet rs = connection.getMetaData().getColumns(tableLocation.getCatalog(), tableLocation.getSchema(), tableLocation.getTable(), null)) {
            while(rs.next()) {
                if (canAddField(rs.getInt("ORDINAL_POSITION"), rs.getInt("DATA_TYPE"), rs.getString("TYPE_NAME"))) {
                    addItem(rs.getString("COLUMN_NAME"));
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
    }

    /**
     * Determine which kind of fields to add.
     * @param index Field index [1-n]
     * @param fieldTypeCode Code from {@link java.sql.Types}
     * @param fieldTypeName Type name
     * @return True if the field will be added.
     */
    protected abstract boolean canAddField(int index,int fieldTypeCode, String fieldTypeName);

    /**
     * Used when the field against which the analysis is made changes.
     *
     * @param name The new field.
     */
    protected void updateField(String name) {
        getLegend().setLookupFieldName(name);
    }
}
