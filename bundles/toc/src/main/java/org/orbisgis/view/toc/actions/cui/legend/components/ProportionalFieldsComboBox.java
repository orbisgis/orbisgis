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
package org.orbisgis.view.toc.actions.cui.legend.components;

import org.orbisgis.coremap.renderer.classification.ClassificationUtils;
import org.orbisgis.legend.IInterpolationLegend;
import org.orbisgis.legend.LookupFieldName;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Root class for proportional point/line field combo boxes.
 *
 * @author Adam Gouge
 */
public abstract class ProportionalFieldsComboBox extends NumericalFieldsComboBox {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProportionalFieldsComboBox.class);

    protected CanvasSE preview;

    /**
     * Constructor
     *
     * @param ds      DataSource
     * @param legend  Legend
     * @param preview Preview
     */
    protected ProportionalFieldsComboBox(DataSource ds,
                                         String table,
                                         IInterpolationLegend legend,
                                         CanvasSE preview) {
        super(ds,table, legend);
        this.preview = preview;
    }

    /**
     * Gets the legend.
     *
     * @return The legend
     */
    protected IInterpolationLegend getLegend() {
        return (IInterpolationLegend) legend;
    }

    @Override
    protected void updateField(String name) {
        // Set the lookup field name.
        ((LookupFieldName) legend).setLookupFieldName(name);
        try(Connection connection = dataSource.getConnection()) {
            // Set the first and second data
            double[] minAndMax = ClassificationUtils
                    .getMinAndMax( connection, tableIdentifier, name);
            setFirstAndSecondValues(minAndMax);
            // Set the sample data source for the preview.
            Map<String, Object> sample = new HashMap<>();
            sample.put(name, minAndMax[1]);
            preview.setSampleDatasource(sample);
            // Update the preview.
            preview.setDisplayed(true);
            preview.imageChanged();
        } catch (Exception e) {
            LOGGER.warn("Cannot get min and max values.");
        }
    }

    /**
     * Set the first and second values of the legend.
     *
     * @param minAndMax A two-item double array containing the min and max
     *                  values, in this order.
     */
    protected abstract void setFirstAndSecondValues(double[] minAndMax);
}
