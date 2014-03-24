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
package org.orbisgis.core.renderer.classification;

import java.util.Arrays;
import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 * Some methods used for classification...
 * @author Maxence Laurent
 * @author Alexis Guéganno
 */
public class ClassificationUtils {

        private ClassificationUtils(){};

        /**
         * Retrieves the double values in {@code sds} from {@code value} in
         * ascending order.
         * @param sds
         * @param value
         * @return
         * @throws DriverException
         * @throws ParameterException
         */
	public static double[] getSortedValues(DataSource sds, RealParameter value)
			throws DriverException, ParameterException {

		double[] values = new double[(int) sds.getRowCount()];
		for (long i = 0; i < values.length; i++) {
			values[(int)i] = value.getValue(sds, i);
		}
		Arrays.sort(values);
		return values;
	}

        /**
         * Gets the minimum and maximum values of {@code sds} from {@code value}.
         * @param sds
         * @param value
         * @return
         * A double array of length two that contains[min, max].
         * @throws DriverException
         * @throws ParameterException
         */
        public static double[] getMinAndMax(DataSource sds, RealParameter value)
                throws DriverException, ParameterException{
                double[] minAndMax = new double[]{Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
                long rows =  sds.getRowCount();
                for(long i = 0; i<rows; i++){
                        double tmp = value.getValue(sds, i);
                        minAndMax[0] = Math.min(tmp,minAndMax[0]);
                        minAndMax[1] = Math.max(tmp,minAndMax[1]);
                }
                return minAndMax;
        }
}
