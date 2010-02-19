/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.drivers;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ShortProcessor;

import java.io.File;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import junit.framework.TestCase;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreation;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.DigestUtilities;
import org.gdms.data.SpatialDataSourceDecorator;
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
import org.gdms.driver.DriverException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;

public class GDMSDriverTest extends TestCase {

	private DataSourceFactory dsf;

	@Override
	protected void setUp() throws Exception {
		dsf = new DataSourceFactory("src/test/resources/backup/sources",
				"src/test/resources/backup");
	}

	public void testSaveASGDMS() throws Exception {
		File source = new File(SourceTest.internalData + "landcover2000.shp");
		saveAs(source);
	}

	private void saveAs(File source) throws DataSourceCreationException,
			DriverException {
		File gdmsFile = new File("src/test/resources/backup/saveAsGDMS.gdms");
		dsf.getSourceManager().register("gdms", gdmsFile);
		DataSource ds = dsf.getDataSource(source);
		ds.open();
		dsf.saveContents("gdms", ds);
		ds.close();

		ds = dsf.getDataSource(gdmsFile);
		ds.open();
		ds.getAsString();
		ds.close();
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

		Value[] nullValues = new Value[15];
		for (int i = 0; i < nullValues.length; i++) {
			nullValues[i] = ValueFactory.createNullValue();
		}
		ds.insertFilledRow(nullValues);
		String digest = DigestUtilities.getBase64Digest(ds);
		ds.commit();
		ds.close();

		ds = dsf.getDataSource(file);
		DataSource ds2 = dsf.getDataSource(file);
		ds.open();
		ds2.open();
		assertTrue(digest.equals(DigestUtilities.getBase64Digest(ds2)));
		ds2.close();
		ds.close();
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

		ds.close();
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
		ds.close();
	}

	public void testKeepFullExtent() throws Exception {
		File vectFile = new File(
				"src/test/resources/backup/fullExtentVectGDMS.gdms");
		vectFile.delete();
		testFullExtent(vectFile, new File(SourceTest.internalData
				+ "landcover2000.shp"));
		File rasterFile = new File(
				"src/test/resources/backup/fullExtentRasterGDMS.gdms");
		rasterFile.delete();
		testFullExtent(rasterFile, new File(SourceTest.internalData
				+ "sample.png"));
	}

	private void testFullExtent(File gdmsFile, File original)
			throws DataSourceCreationException, DriverException {
		String name = dsf.getSourceManager().nameAndRegister(gdmsFile);
		DataSource ds = dsf.getDataSource(original);
		ds.open();
		Envelope fe = new SpatialDataSourceDecorator(ds).getFullExtent();
		dsf.saveContents(name, ds);
		ds.close();

		ds = dsf.getDataSource(gdmsFile);
		ds.open();
		assertTrue(fe
				.equals(new SpatialDataSourceDecorator(ds).getFullExtent()));
		ds.close();
	}

	public void testDifferentValueTypeAndFieldType() throws Exception {
		File source = new File(SourceTest.internalData + "points.shp");
		saveAs(source);
	}

	public void testKeepNoDataValue() throws Exception {
		DataSource ds = dsf.getDataSource(new File("../../datatestjunit/"
				+ "gdms/tif440606.gdms"));
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();
		GeoRaster gr = sds.getRaster(0);
		gr.setNodataValue(345);
		assertTrue(gr.getNoDataValue() == 345);
		gr = sds.getRaster(0);
		assertTrue(gr.getNoDataValue() == 345);
		sds.close();
	}

	public void testRasterMetadataPixelArrayInconsistencyFloat()
			throws Exception {
		RasterMetadata rm = new RasterMetadata(0, 0, 10, 10, 2, 2);
		FloatProcessor fp = new FloatProcessor(3, 3);
		float[] pixels = (float[]) fp.getPixels();
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = i;
		}
		GeoRaster gr = GeoRasterFactory.createGeoRaster(fp, rm);

		float[] result = (float[]) testRasterMetadataPixelArrayInconsistency(gr);
		assertTrue(result[2] == 3);
		assertTrue(result.length == 4);
	}

	public void testRasterMetadataPixelArrayInconsistencyInt() throws Exception {
		RasterMetadata rm = new RasterMetadata(0, 0, 10, 10, 2, 2);
		ColorProcessor fp = new ColorProcessor(3, 3);
		int[] pixels = (int[]) fp.getPixels();
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = i;
		}
		GeoRaster gr = GeoRasterFactory.createGeoRaster(fp, rm);

		int[] result = (int[]) testRasterMetadataPixelArrayInconsistency(gr);
		assertTrue(result[2] == 3);
		assertTrue(result.length == 4);
	}

	public void testRasterMetadataPixelArrayInconsistencyByte()
			throws Exception {
		RasterMetadata rm = new RasterMetadata(0, 0, 10, 10, 2, 2);
		ByteProcessor fp = new ByteProcessor(3, 3);
		byte[] pixels = (byte[]) fp.getPixels();
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = (byte) i;
		}
		GeoRaster gr = GeoRasterFactory.createGeoRaster(fp, rm);

		byte[] result = (byte[]) testRasterMetadataPixelArrayInconsistency(gr);
		assertTrue(result[2] == 3);
		assertTrue(result.length == 4);
	}

	public void testRasterMetadataPixelArrayInconsistencyShort()
			throws Exception {
		RasterMetadata rm = new RasterMetadata(0, 0, 10, 10, 2, 2);
		ShortProcessor fp = new ShortProcessor(3, 3);
		short[] pixels = (short[]) fp.getPixels();
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = (short) i;
		}
		GeoRaster gr = GeoRasterFactory.createGeoRaster(fp, rm);

		short[] result = (short[]) testRasterMetadataPixelArrayInconsistency(gr);
		assertTrue(result[2] == 3);
		assertTrue(result.length == 4);
	}

	private Object testRasterMetadataPixelArrayInconsistency(GeoRaster gr)
			throws Exception {
		// Create the source
		File out = new File(dsf.getTempFile(".gdms"));
		DefaultMetadata dm = new DefaultMetadata(new Type[] { TypeFactory
				.createType(Type.RASTER) }, new String[] { "raster" });
		FileSourceCreation fsc = new FileSourceCreation(out, dm);
		dsf.createDataSource(fsc);

		// Register the source
		DataSource ds = dsf.getDataSource(out);
		ds.open();
		ds.insertFilledRow(new Value[] { ValueFactory.createValue(gr) });
		ds.commit();

		GeoRaster readRaster = ds.getFieldValue(0, 0).getAsRaster();
		Object pixels = readRaster.getImagePlus().getProcessor().getPixels();
		assertTrue(readRaster.getMetadata().getNCols() == 2);
		ds.close();
		return pixels;
	}

	public void testCompatibleWith2_0() throws Exception {
		DataSource ds = dsf.getDataSource(new File(
				"src/test/resources/version2.gdms"));
		ds.open();
		ds.getAsString();
		ds.close();
	}
}
