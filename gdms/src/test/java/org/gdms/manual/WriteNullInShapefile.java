package org.gdms.manual;

import java.net.URL;

import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.AttributeType;
import org.geotools.feature.AttributeTypeFactory;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypeBuilder;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.WKTReader;

public class WriteNullInShapefile {
	public static void main(String[] args) throws Exception {
		// create new shapefile data store
		ShapefileDataStore newShapefileDataStore = new ShapefileDataStore(
				new URL("file:///tmp/newSHP.shp"));

		// create the schema using from the original shapefile
		AttributeType[] atts = new AttributeType[2];
		atts[0] = AttributeTypeFactory.newAttributeType("the_geom",
				LineString.class);
		atts[1] = AttributeTypeFactory.newAttributeType("sometext",
				String.class);
		FeatureType ft = FeatureTypeBuilder.newFeatureType(atts, "type");
		newShapefileDataStore.createSchema(ft);

		WKTReader wktReader = new WKTReader();
		LineString geometry = (LineString) wktReader
				.read("LINESTRING (0 0, 10 10)");
		String value = "cool";

		FeatureWriter writer = newShapefileDataStore
				.getFeatureWriter(Transaction.AUTO_COMMIT);
		Feature newF = writer.next();
		newF.setAttribute("the_geom", geometry);
		newF.setAttribute("sometext", value);
		newF = writer.next();
		newF.setAttribute("the_geom", null);
		newF.setAttribute("sometext", value);

		writer.write();
		writer.close();

		newShapefileDataStore = new ShapefileDataStore(new URL(
				"file:///tmp/newSHP.shp"));
		FeatureCollection fc = newShapefileDataStore.getFeatureSource()
				.getFeatures();
		FeatureIterator fi = fc.features();
		while (fi.hasNext()) {
			Feature f = fi.next();
			Object[] values = f.getAttributes(null);
			for (Object object : values) {
				System.out.println(object);
			}

		}
	}
}
