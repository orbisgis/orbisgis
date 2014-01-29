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
package org.orbisgis.view.toc.actions.cui.legends.components;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.classification.ClassificationUtils;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.legend.IInterpolationLegend;
import org.orbisgis.legend.LookupFieldName;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;

import java.util.HashMap;
import java.util.Map;

/**
 * Root class for proportional point/line field combo boxes.
 *
 * @author Adam Gouge
 */
public abstract class ProportionalFieldsComboBox extends NumericalFieldsComboBox {

    private static final Logger LOGGER =
            Logger.getLogger(ProportionalFieldsComboBox.class);

    protected CanvasSE preview;

    /**
     * Constructor
     *
     * @param ds      DataSource
     * @param legend  Legend
     * @param preview Preview
     */
    protected ProportionalFieldsComboBox(DataSource ds,
                                         IInterpolationLegend legend,
                                         CanvasSE preview) {
        super(ds, legend);
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
        try {
            // Set the first and second data
            double[] minAndMax = ClassificationUtils
                    .getMinAndMax(ds, new RealAttribute(name));
            setFirstAndSecondValues(minAndMax);
            // Set the sample data source for the preview.
            Map<String, Object> sample = new HashMap<String, Object>();
            sample.put(name, minAndMax[1]);
            // Todo: Remove this. It is slow.
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
