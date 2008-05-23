package org.gdms.drivers;

import ij.ImagePlus;

import java.io.File;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import junit.framework.TestCase;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreation;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.DigestUtilities;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.types.AutoIncrementConstraint;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.DimensionConstraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.LengthConstraint;
import org.gdms.data.types.MaxConstraint;
import org.gdms.data.types.MinConstraint;
import org.gdms.data.types.NotNullConstraint;
import org.gdms.data.types.PatternConstraint;
import org.gdms.data.types.PrecisionConstraint;
import org.gdms.data.types.PrimaryKeyConstraint;
import org.gdms.data.types.RasterTypeConstraint;
import org.gdms.data.types.ReadOnlyConstraint;
import org.gdms.data.types.ScaleConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.types.UniqueConstraint;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

public class GDMSDriverTest extends TestCase {

	private DataSourceFactory dsf;

	@Override
	protected void setUp() throws Exception {
		dsf = new DataSourceFactory("src/test/resources/backup/sources",
				"src/test/resources/backup");
	}

	public void testSaveASGDMS() throws Exception {
		File gdmsFile = new File("src/test/resources/backup/saveAsGDMS.gdms");
		dsf.getSourceManager().register("gdms", gdmsFile);
		DataSource ds = dsf.getDataSource(new File(SourceTest.externalData
				+ "shp/mediumshape2D/landcover2000.shp"));
		ds.open();
		dsf.saveContents("gdms", ds);
		ds.cancel();

		ds = dsf.getDataSource(gdmsFile);
		ds.open();
		ds.cancel();
	}

	public void testAllTypes() throws Exception {
		DefaultMetadata metadata = new DefaultMetadata();
		metadata.addField("binary", TypeFactory.createType(Type.BINARY));
		metadata.addField("boolean", TypeFactory.createType(Type.BOOLEAN));
		metadata.addField("byte", TypeFactory.createType(Type.BYTE));
		metadata
				.addField("collection", TypeFactory.createType(Type.COLLECTION));
		metadata.addField("date", TypeFactory.createType(Type.DATE));
		metadata.addField("double", TypeFactory.createType(Type.DOUBLE));
		metadata.addField("float", TypeFactory.createType(Type.FLOAT));
		metadata.addField("geometry", TypeFactory.createType(Type.GEOMETRY));
		metadata.addField("int", TypeFactory.createType(Type.INT));
		metadata.addField("long", TypeFactory.createType(Type.LONG));
		metadata.addField("raster", TypeFactory.createType(Type.RASTER));
		metadata.addField("short", TypeFactory.createType(Type.SHORT));
		metadata.addField("string", TypeFactory.createType(Type.STRING));
		metadata.addField("time", TypeFactory.createType(Type.TIME));
		metadata.addField("timestamp", TypeFactory.createType(Type.TIMESTAMP));

		File file = new File("src/test/resources/backup/allgdms.gdms");
		DataSourceCreation dsc = new FileSourceCreation(file, metadata);
		file.delete();
		dsf.createDataSource(dsc);

		DataSource ds = dsf.getDataSource(file);
		ds.open();
		ds.insertEmptyRow();
		ds.setBinary(0, 0, new byte[] { 3, 3 });
		ds.setBoolean(0, 1, true);
		ds.setByte(0, 2, (byte) 5);
		ds.setFieldValue(0, 3, ValueFactory
				.createValue(new Value[] { ValueFactory.createValue(true) }));
		ds.setDate(0, 4, new Date());
		ds.setDouble(0, 5, 4d);
		ds.setFloat(0, 6, 5.2f);
		ds.setFieldValue(0, 7, ValueFactory.createValue(new GeometryFactory()
				.createPoint(new Coordinate(3, 3))));
		ds.setInt(0, 8, 4);
		ds.setLong(0, 9, 5L);
		GeoRaster gr = GeoRasterFactory
				.createGeoRaster("src/test/resources/sample.png");
		Value grValue = ValueFactory.createValue(gr);
		ds.setFieldValue(0, 10, grValue);
		ds.setShort(0, 11, (short) 34);
		ds.setString(0, 12, "sd");
		ds.setTime(0, 13, new Time(12424L));
		ds.setTimestamp(0, 14, new Timestamp(2525234L));
		String digest = DigestUtilities.getBase64Digest(ds);
		ds.commit();

		ds = dsf.getDataSource(file);
		DataSource ds2 = dsf.getDataSource(file);
		ds.open();
		ds2.open();
		assertTrue(digest.equals(DigestUtilities.getBase64Digest(ds2)));
		ds2.cancel();
		ds.cancel();
	}

	public void testAllConstraints() throws Exception {
		DefaultMetadata metadata = new DefaultMetadata();
		Type[] types = new Type[] {
				TypeFactory.createType(Type.BINARY, new PrimaryKeyConstraint()),
				TypeFactory.createType(Type.BOOLEAN, new UniqueConstraint()),
				TypeFactory.createType(Type.BYTE, new MinConstraint(0)),
				TypeFactory
						.createType(Type.COLLECTION, new NotNullConstraint()),
				TypeFactory.createType(Type.DATE, new ReadOnlyConstraint()),
				TypeFactory.createType(Type.DOUBLE,
						new AutoIncrementConstraint()),
				TypeFactory.createType(Type.FLOAT),
				TypeFactory.createType(Type.GEOMETRY,
						new DimensionConstraint(3), new GeometryConstraint(
								GeometryConstraint.LINESTRING)),
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.LONG),
				TypeFactory.createType(Type.RASTER, new RasterTypeConstraint(
						ImagePlus.COLOR_256)),
				TypeFactory.createType(Type.SHORT, new MaxConstraint(4),
						new PrecisionConstraint(0), new ScaleConstraint(2)),
				TypeFactory.createType(Type.STRING, new LengthConstraint(4),
						new PatternConstraint("%")),
				TypeFactory.createType(Type.TIME),
				TypeFactory.createType(Type.TIMESTAMP) };
		for (int i = 0; i < types.length; i++) {
			metadata.addField("field" + i, types[i]);
		}

		File file = new File("src/test/resources/backup/allgdms.gdms");
		DataSourceCreation dsc = new FileSourceCreation(file, metadata);
		file.delete();
		dsf.createDataSource(dsc);

		DataSource ds = dsf.getDataSource(file);
		ds.open();
		for (int i = 0; i < ds.getMetadata().getFieldCount(); i++) {
			checkType(ds.getFieldType(i), types[i]);
			assertTrue(ds.getFieldName(i).equals("field" + i));
		}

		ds.cancel();
	}

	private void checkType(Type fieldType, Type type) {
		assertTrue(fieldType.getTypeCode() == type.getTypeCode());
		Constraint[] cons = fieldType.getConstraints();
		Constraint[] cons2 = type.getConstraints();
		assertTrue(cons.length == cons2.length);
		for (int i = 0; i < cons2.length; i++) {
			assertTrue(cons[i].getConstraintValue().equals(
					cons2[i].getConstraintValue()));
		}
	}

	public void testRemoveRasterField() throws Exception {
		File file = new File("src/test/resources/sample.png");
		DataSource ds = dsf.getDataSource(file);
		ds.open();
		String digest = DigestUtilities.getBase64Digest(ds);
		ds.removeField(0);
		ds.undo();
		assertTrue(digest.equals(DigestUtilities.getBase64Digest(ds)));
		ds.cancel();
	}
}
