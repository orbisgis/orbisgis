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

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.legend.thematic.recode.AbstractRecodedLegend;

/**
 * A JComboBox containing the non-spatial fields of the given {@link DataSource}.
 */
public class NonSpatialFieldsComboBox extends FieldComboBox {

    private static final Logger LOGGER = Logger.getLogger(NonSpatialFieldsComboBox.class);

    public NonSpatialFieldsComboBox(DataSource ds,
                                    AbstractRecodedLegend legend) {
        super(ds, legend);
    }

    @Override
    protected boolean canAddField(int index) {
        try {
            return !TypeFactory.isSpatial(
                    ds.getMetadata().getFieldType(index).getTypeCode());
        } catch (DriverException ex) {
            LOGGER.error("Cannot at field at position " + index
                    + " to the NonSpatialFieldsComboBox because the metadata " +
                    "could not be recovered.");
            return false;
        }
    }
}
