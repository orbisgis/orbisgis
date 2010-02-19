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
package org.gdms.driver.solene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.DimensionConstraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.NotNullConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.UniqueConstraint;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.source.SourceManager;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.utils.FileUtils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

// call register('../../datas2tests/cir/faceTrouee.cir','moncir');
// call register('/tmp/r.cir','r');
// create table r as select * from moncir;

public class CirDriver implements FileReadWriteDriver {
	public static final String DRIVER_NAME = "Solene Cir driver";

	private String EXTENSION = "cir";

	private Scanner in;

	private List<Value[]> rows;

	private Envelope envelope;

	private PrintWriter out;

	// final static String COORD3D_WRITTING_FORMAT = "\t%g\t%g\t%g\r\n";
	final static String COORD3D_WRITTING_FORMAT = "\t%10.5f\t%10.5f\t%10.5f\r\n";

	public void close() throws DriverException {
		in.close();
	}

	public void open(File file) throws DriverException {
		try {
			rows = new ArrayList<Value[]>();

			in = new Scanner(file);
			in.useLocale(Locale.US); // essential to read float values

			final int nbFacesCir = in.nextInt();
			in.next(); // useless "supNumFaces"
			for (int i = 0; i < 10; i++) {
				in.next(); // 5 rows of 2 useless values
			}

			final GeometryFactory geometryFactory = new GeometryFactory();
			for (int i = 0; i < nbFacesCir; i++) {
				_readFace(geometryFactory);
			}
		} catch (FileNotFoundException e) {
			throw new DriverException(e);
		}
	}

	private final void _readFace(final GeometryFactory geometryFactory)
			throws DriverException {
		final String faceIdx = in.next();
		if (!faceIdx.startsWith("f")) {
			throw new DriverException("Bad CIR file format (f) !");
		}
		final int nbContours = in.nextInt();
		final Coordinate normal = _readCoordinate();
		for (int boundIdx = 0; boundIdx < nbContours; boundIdx++) {
			_readBound(geometryFactory, faceIdx, boundIdx, normal);
		}
	}

	private final void _readBound(final GeometryFactory geometryFactory,
			final String faceIdx, final int boundIdx, final Coordinate normal)
			throws DriverException {
		final String tmpNbHoles = in.next();
		if (!tmpNbHoles.startsWith("c")) {
			throw new DriverException("Bad CIR file format (c) !");
		}
		final int nbHoles = Integer.parseInt(tmpNbHoles.substring(1));

		final LinearRing shell = _readLinearRing(geometryFactory);
		final LinearRing[] holes = _readHoles(geometryFactory, nbHoles);

		Geometry geom = geometryFactory.createPolygon(shell, holes);
		if (Geometry3DUtilities.scalarProduct(normal, Geometry3DUtilities
				.computeNormal((Polygon) geom)) < 0) {
			geom = Geometry3DUtilities.reverse((Polygon) geom);
		}

		if (null == envelope) {
			envelope = geom.getEnvelopeInternal();
		} else {
			envelope.expandToInclude(geom.getEnvelopeInternal());
		}

		rows.add(new Value[] {
				ValueFactory.createValue(faceIdx + "_" + boundIdx),
				ValueFactory.createValue(geom) });
	}

	private final LinearRing _readLinearRing(
			final GeometryFactory geometryFactory) {
		Coordinate[] points = null;
		final int nbPoints = in.nextInt();
		if (1 < nbPoints) {
			points = new Coordinate[nbPoints];
			for (int i = 0; i < nbPoints; i++) {
				points[i] = _readCoordinate();
			}
		}
		return geometryFactory.createLinearRing(points);
	}

	private final LinearRing[] _readHoles(
			final GeometryFactory geometryFactory, final int nbHoles)
			throws DriverException {
		LinearRing[] holes = null;
		if (0 < nbHoles) {
			holes = new LinearRing[nbHoles];
			for (int i = 0; i < nbHoles; i++) {
				if (!in.next().equals("t")) {
					throw new DriverException("Bad CIR file format (t) !");
				}
				holes[i] = _readLinearRing(geometryFactory);
			}
		}
		return holes;
	}

	private final Coordinate _readCoordinate() {
		return new Coordinate(in.nextDouble(), in.nextDouble(), in.nextDouble());
	}

	public Metadata getMetadata() throws DriverException {
		final DefaultMetadata metadata = new DefaultMetadata();
		try {
			metadata.addField("id", Type.STRING, new Constraint[] {
					new UniqueConstraint(), new NotNullConstraint() });
			metadata.addField("the_geom", Type.GEOMETRY, new Constraint[] {
					new GeometryConstraint(GeometryConstraint.POLYGON),
					new DimensionConstraint(3) });
		} catch (InvalidTypeException e) {
			throw new RuntimeException("Bug in the driver", e);
		}
		return metadata;
	}

	public TypeDefinition[] getTypesDefinitions() {
		final TypeDefinition[] result = new TypeDefinition[2];
		result[0] = new DefaultTypeDefinition("STRING", Type.STRING, new int[] {
				Constraint.UNIQUE, Constraint.NOT_NULL });
		result[1] = new DefaultTypeDefinition("GEOMETRY", Type.GEOMETRY,
				new int[] { Constraint.GEOMETRY_DIMENSION,
						Constraint.GEOMETRY_TYPE });
		return result;
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	public String getDriverId() {
		return DRIVER_NAME;
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		final Value[] fields = rows.get((int) rowIndex);
		if ((fieldId < 0) || (fieldId > 1)) {
			return ValueFactory.createNullValue();
		} else {
			return fields[fieldId];
		}
	}

	public long getRowCount() throws DriverException {
		return rows.size();
	}

	public Number[] getScope(int dimension) throws DriverException {
		if (dimension == X) {
			return new Number[] { envelope.getMinX(), envelope.getMaxX() };
		} else if (dimension == Y) {
			return new Number[] { envelope.getMinY(), envelope.getMaxY() };
		} else {
			return null;
		}
	}

	public void copy(File in, File out) throws IOException {
		FileUtils.copy(in, out);
	}

	public void createSource(String path, Metadata metadata,
			DataSourceFactory dataSourceFactory) throws DriverException {
		try {
			int spatialFieldIndex = -1;
			for (int fieldId = 0; fieldId < metadata.getFieldCount(); fieldId++) {
				final Constraint c = metadata.getFieldType(fieldId)
						.getConstraint(Constraint.GEOMETRY_TYPE);
				if (null != c) {
					spatialFieldIndex = fieldId;
					break;
				}
			}
			checkGeometryConstraint(metadata, spatialFieldIndex);

			final File file = new File(path);
			file.getParentFile().mkdirs();
			file.createNewFile();
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	public void writeFile(final File file, final DataSource dataSource,
			IProgressMonitor pm) throws DriverException {
		final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
				dataSource);
		final int spatialFieldIndex = sds.getSpatialFieldIndex();
		checkGeometryConstraint(sds.getMetadata(), spatialFieldIndex);
		try {
			out = new PrintWriter(new FileOutputStream(file));

			// write header part...
			out.printf("%d %d\r\n", sds.getRowCount(), sds.getRowCount());
			for (int i = 0; i < 5; i++) {
				out.printf("\t\t%d %d\r\n", 99999, 99999);
			}

			// write body part...
			for (long rowIndex = 0; rowIndex < dataSource.getRowCount(); rowIndex++) {
				if (rowIndex / 100 == rowIndex / 100.0) {
					if (pm.isCancelled()) {
						break;
					} else {
						pm.progressTo((int) (100 * rowIndex / dataSource
								.getRowCount()));
					}
				}
				final Geometry g = sds.getGeometry(rowIndex);
				if (g instanceof Polygon) {
					writeAPolygon((Polygon) g, rowIndex);
				} else if (g instanceof MultiPolygon) {
					writeAMultiPolygon((MultiPolygon) g, rowIndex);
				} else {
					throw new DriverException("Geometric field (row "
							+ rowIndex + ") is not a (multi-)polygon !");
				}
			}
			out.close();
		} catch (FileNotFoundException e) {
			throw new DriverException(e);
		}
	}

	private final void checkGeometryConstraint(final Metadata metadata,
			final int spatialFieldIndex) throws DriverException {
		Type fieldType = metadata.getFieldType(spatialFieldIndex);
		final GeometryConstraint c = (GeometryConstraint) fieldType
				.getConstraint(Constraint.GEOMETRY_TYPE);
		DimensionConstraint dc = (DimensionConstraint) fieldType
				.getConstraint(Constraint.GEOMETRY_DIMENSION);
		final int geometryType = c.getGeometryType();
		if ((GeometryConstraint.POLYGON != geometryType)
				&& (GeometryConstraint.MULTI_POLYGON != geometryType)) {
			throw new DriverException(
					"Geometric field must be a (multi-)polygon !");
		}
		if ((dc != null) && (dc.getDimension() == 2)) {
			throw new DriverException("Only 3d can be stored in this format !");
		}
	}

	private final void writeAMultiPolygon(final MultiPolygon multiPolygon,
			final long rowIndex) {
		final int nbOfCtrs = multiPolygon.getNumGeometries();
		out.printf("f%d %d\r\n", rowIndex + 1, nbOfCtrs);
		// the normal of the multi-polygon is set to the normal of its 1st
		// component (ie polygon)...
		writeANode(Geometry3DUtilities.computeNormal((Polygon) multiPolygon
				.getGeometryN(0)));
		for (int i = 0; i < nbOfCtrs; i++) {
			writeAContour((Polygon) multiPolygon.getGeometryN(i));
		}
	}

	private final void writeAPolygon(final Polygon polygon, final long rowIndex) {
		out.printf("f%d 1\r\n", rowIndex + 1);
		writeANode(Geometry3DUtilities.computeNormal(polygon));
		writeAContour(polygon);
	}

	private final void writeAContour(final Polygon polygon) {
		final LineString shell = polygon.getExteriorRing();
		final int nbOfHoles = polygon.getNumInteriorRing();
		out.printf("c%d\r\n", nbOfHoles);
		writeALinearRing(shell);
		for (int i = 0; i < nbOfHoles; i++) {
			out.printf("t\r\n");
			writeALinearRing(polygon.getInteriorRingN(i));
		}
	}

	private final void writeALinearRing(final LineString shell) {
		final Coordinate[] nodes = shell.getCoordinates();
		out.printf("%d\r\n", nodes.length);
		for (Coordinate node : nodes) {
			writeANode(node);
		}
	}

	private final void writeANode(final Coordinate node) {
		if (Double.isNaN(node.z)) {
			out.printf(COORD3D_WRITTING_FORMAT, node.x, node.y, 0d);
		} else {
			out.printf(COORD3D_WRITTING_FORMAT, node.x, node.y, node.z);
		}
	}

	public boolean isCommitable() {
		return true;
	}

	public int getType() {
		return SourceManager.FILE | SourceManager.VECTORIAL;
	}

	public String validateMetadata(Metadata metadata) throws DriverException {
		return null;
	}

	@Override
	public String[] getFileExtensions() {
		return new String[] { EXTENSION };
	}

	@Override
	public String getTypeDescription() {
		return "Solene file";
	}

	@Override
	public String getTypeName() {
		return "CIR";
	}

}