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
package org.orbisgis.core.ui.editorViews.toc.actions.cui.legends;

import java.util.HashSet;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class GeometryProperties {

        public static final int POINT = 1;
	public static final int LINE = 2;
	public static final int POLYGON = 4;
        public static final int ALL = POINT | LINE | POLYGON;

	private static final String GEOMETRY_Z = "Geometry.Z";
	private static final String GEOMETRY_START_Z = "Geometry.StartZ";
	private static final String GEOMETRY_END_Z = "Geometry.EndZ";
	private static final String GEOMETRY_LENGHT = "Geometry.Length";
	private static final String GEOMETRY_MEAN_Z = "Geometry.MeanZ";
	private static final String GEOMETRY_AREA = "Geometry.Area";
	private static final String ENVELOPE_AREA = "Envelope.Area";
	private static final String GEOMETRY_NUMPOINTS = "Geometry.NumPoints";
	private static final String GEOMETRY_SLOPE = "Geometry.Slope";

	static String[] propertyNames = new String[] { GEOMETRY_NUMPOINTS,
			GEOMETRY_Z, GEOMETRY_START_Z, GEOMETRY_END_Z, GEOMETRY_LENGHT,
			GEOMETRY_MEAN_Z, GEOMETRY_AREA, ENVELOPE_AREA, GEOMETRY_SLOPE };

	public static HashSet<String> getPropertiesName(int geometryType) {

		HashSet<String> names = new HashSet<String>();
		switch (geometryType) {
		case POINT:

			names.add(GEOMETRY_NUMPOINTS);
			names.add(GEOMETRY_Z);
			break;

		case LINE:

			names.add(GEOMETRY_NUMPOINTS);
			names.add(GEOMETRY_START_Z);
			names.add(GEOMETRY_END_Z);
			names.add(GEOMETRY_LENGHT);
			names.add(ENVELOPE_AREA);
			names.add(GEOMETRY_SLOPE);
			break;
		case POLYGON:

			names.add(GEOMETRY_NUMPOINTS);
			names.add(GEOMETRY_MEAN_Z);
			names.add(GEOMETRY_LENGHT);
			names.add(GEOMETRY_AREA);
			names.add(ENVELOPE_AREA);
			break;

		default:
			break;
		}
		return names;

	}

	public static Value getPropertyValue(String fieldName, Geometry geom) {

		Value v = null;
		if (fieldName.equals(GEOMETRY_Z)) {
			v = ValueFactory.createValue(geom.getCoordinates()[0].z);
		} else if (fieldName.equals(GEOMETRY_START_Z)) {
			v = ValueFactory.createValue(geom.getCoordinates()[0].z);
		} else if (fieldName.equals(GEOMETRY_END_Z)) {
			v = ValueFactory.createValue(geom.getCoordinates()[geom
					.getCoordinates().length].z);
		} else if (fieldName.equals(GEOMETRY_MEAN_Z)) {
			Coordinate[] coords = geom.getCoordinates();
			int count = coords.length;
			double sum = 0;
			for (int j = 0; j < coords.length; j++) {
				double z = coords[j].z;
				sum = sum + z;
			}
			double mean = (sum / count);
			v = ValueFactory.createValue(mean);
		} else if (fieldName.equals(GEOMETRY_AREA)) {
			v = ValueFactory.createValue(geom.getArea());
		} else if (fieldName.equals(GEOMETRY_LENGHT)) {
			v = ValueFactory.createValue(geom.getLength());
		} else if (fieldName.equals(GEOMETRY_NUMPOINTS)) {
			v = ValueFactory.createValue(geom.getNumPoints());
		} else if (fieldName.equals(GEOMETRY_SLOPE)) {
			Coordinate coordStart = geom.getCoordinates()[0];
			Coordinate coordEnd = geom.getCoordinates()[geom.getCoordinates().length - 1];
			// l'ordre des coordonnees correspond a l'orientation de l'arc
			double dz = coordEnd.z - coordStart.z;
			// Calcul de la distance horizontale
			double d = coordStart.distance(coordEnd);
			double slope = d == 0.0 ? 0.0 : dz / d;
			v = ValueFactory.createValue(slope);

		}

		else if (fieldName.equals(ENVELOPE_AREA)) {

			v = ValueFactory.createValue(geom.getEnvelopeInternal().getArea());
		}

		return v;
	}

	public static boolean isFieldName(String fieldName) {

		boolean flag = false;
		for (int i = 0; i < propertyNames.length; i++) {
			if (fieldName.equals(propertyNames[i])) {
				flag = true;
			}
		}
		return flag;
	}

}
