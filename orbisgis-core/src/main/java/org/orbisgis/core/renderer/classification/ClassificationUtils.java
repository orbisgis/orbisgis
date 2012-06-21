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
package org.orbisgis.core.renderer.classification;

import java.util.Arrays;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legends.GeometryProperties;

public class ClassificationUtils {

	public static double[] getSortedValues(DataSource ds, String fieldName)
			throws DriverException {

		double[] values = new double[(int) ds.getRowCount()];
		if (GeometryProperties.isFieldName(fieldName)) {
			for (int i = 0; i < values.length; i++) {
				values[i] = GeometryProperties.getPropertyValue(fieldName,
						ds.getGeometry(i))
						.getAsDouble();
			}
		} else {
			int fieldIndex = ds.getFieldIndexByName(fieldName);
			for (int i = 0; i < values.length; i++) {
				values[i] = ds.getFieldValue(i, fieldIndex).getAsDouble();
			}
		}
		Arrays.sort(values);
		return values;

	}

}
