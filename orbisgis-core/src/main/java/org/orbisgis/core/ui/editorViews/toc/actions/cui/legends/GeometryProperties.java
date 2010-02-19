package org.orbisgis.core.ui.editorViews.toc.actions.cui.legends;

import java.util.HashSet;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ILegendPanel;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class GeometryProperties {

	private static final String GEOMETRY_Z = "Geometry.Z";
	private static final String GEOMETRY_START_Z = "Geometry.StartZ";
	private static final String GEOMETRY_END_Z = "Geometry.EndZ";
	private static final String GEOMETRY_LENGHT = "Geometry.Lenght";
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
		case ILegendPanel.POINT:

			names.add(GEOMETRY_NUMPOINTS);
			names.add(GEOMETRY_Z);
			break;

		case ILegendPanel.LINE:

			names.add(GEOMETRY_NUMPOINTS);
			names.add(GEOMETRY_START_Z);
			names.add(GEOMETRY_END_Z);
			names.add(GEOMETRY_LENGHT);
			names.add(ENVELOPE_AREA);
			names.add(GEOMETRY_SLOPE);
			break;
		case ILegendPanel.POLYGON:

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
					.getCoordinates().length - 1].z);
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
