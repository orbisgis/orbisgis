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
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.NotNullConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.UniqueConstraint;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.DriverUtilities;
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.spatial.GeometryValue;
import org.gdms.spatial.SpatialDataSourceDecorator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

// call register('../../datas2tests/cir/faceTrouee.cir','moncir');

public class CirDriver implements FileReadWriteDriver {
	public static final String DRIVER_NAME = "Solene Cir driver";

	private String EXTENSION = ".cir";

	private Scanner in;

	private List<Value[]> rows;

	private Envelope envelope;

	private PrintWriter out;

	// final static String COORD3D_WRITTING_FORMAT = "\t%g\t%g\t%g\r\n";
	final static String COORD3D_WRITTING_FORMAT = "\t%10.5f\t%10.5f\t%10.5f\r\n";

	public void close() throws DriverException {
		in.close();
	}

	public String completeFileName(String fileName) {
		if (!fileName.toLowerCase().endsWith(EXTENSION)) {
			return fileName + EXTENSION;
		} else {
			return fileName;
		}
	}

	public boolean fileAccepted(File f) {
		return f.getAbsolutePath().toLowerCase().endsWith(EXTENSION);
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
			for (int i = 0; i < nbFacesCir; i++) {
				_readFace();
			}
		} catch (FileNotFoundException e) {
			throw new DriverException(e);
		}
	}

	private void _readFace() throws DriverException {
		final String faceIdx = in.next();
		if (!faceIdx.startsWith("f")) {
			throw new DriverException("Bad CIR file format (f) !");
		}
		final int nbContours = in.nextInt();
		_readCoordinate(); // useless "normal" value
		for (int i = 0; i < nbContours; i++) {
			_readBound(faceIdx);
		}
	}

	private void _readBound(final String faceIdx) throws DriverException {
		final String boundIdx = in.next();
		if (!boundIdx.startsWith("c")) {
			throw new DriverException("Bad CIR file format (c) !");
		}
		final int nbHoles = Integer.parseInt(boundIdx.substring(1));

		final LinearRing shell = _readLinearRing();
		final LinearRing[] holes = _readHoles(nbHoles);

		final Geometry geom = new GeometryFactory().createPolygon(shell, holes);
		// System.err.println(geom.toText());

		// TODO : is getEnvelopeInternal() the right method ?
		if (null == envelope) {
			envelope = geom.getEnvelopeInternal();
		} else {
			envelope.expandToInclude(geom.getEnvelopeInternal());
		}

		rows.add(new Value[] {
				ValueFactory.createValue(faceIdx + "_" + boundIdx),
				ValueFactory.createValue(geom) });
	}

	private LinearRing _readLinearRing() {
		Coordinate[] points = null;
		final int nbPoints = in.nextInt();
		if (1 < nbPoints) {
			points = new Coordinate[nbPoints];
			for (int i = 0; i < nbPoints; i++) {
				points[i] = _readCoordinate();
			}
		}
		// System.err.println(new GeometryFactory().createLinearRing(points)
		// .toText());
		return new GeometryFactory().createLinearRing(points);
	}

	private LinearRing[] _readHoles(final int nbHoles) throws DriverException {
		LinearRing[] holes = null;
		if (0 < nbHoles) {
			holes = new LinearRing[nbHoles];
			for (int i = 0; i < nbHoles; i++) {
				if (!in.next().equals("t")) {
					throw new DriverException("Bad CIR file format (t) !");
				}
				holes[i] = _readLinearRing();
			}
		}
		return holes;
	}

	private Coordinate _readCoordinate() {
		return new Coordinate(in.nextDouble(), in.nextDouble(), in.nextDouble());
	}

	public Metadata getMetadata() throws DriverException {
		final DefaultMetadata metadata = new DefaultMetadata();
		try {
			metadata.addField("id", Type.STRING, new Constraint[] {
					new UniqueConstraint(), new NotNullConstraint() });
			metadata.addField("the_geom", Type.GEOMETRY,
					new Constraint[] { new GeometryConstraint(
							GeometryConstraint.POLYGON_3D) });
		} catch (InvalidTypeException e) {
			throw new RuntimeException("Bug in the driver", e);
		}
		return metadata;
	}

	public TypeDefinition[] getTypesDefinitions() throws DriverException {
		final TypeDefinition[] result = new TypeDefinition[2];
		try {
			result[0] = new DefaultTypeDefinition("STRING", Type.STRING,
					new ConstraintNames[] { ConstraintNames.UNIQUE,
							ConstraintNames.NOT_NULL });
			result[1] = new DefaultTypeDefinition("GEOMETRY", Type.GEOMETRY,
					new ConstraintNames[] { ConstraintNames.GEOMETRY });
		} catch (InvalidTypeException e) {
			throw new DriverException("Invalid type");
		}
		return result;
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	public String getName() {
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
		DriverUtilities.copy(in, out);
	}

	public void createSource(String path, Metadata metadata,
			DataSourceFactory dataSourceFactory) throws DriverException {
		try {
			int spatialFieldIndex = -1;
			for (int fieldId = 0; fieldId < metadata.getFieldCount(); fieldId++) {
				final Constraint c = metadata.getFieldType(fieldId)
						.getConstraint(ConstraintNames.GEOMETRY);
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

	public void writeFile(File file, DataSource dataSource)
			throws DriverException {
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
				final GeometryValue g = (GeometryValue) sds.getFieldValue(
						rowIndex, spatialFieldIndex);
				if (g.getGeom() instanceof Polygon) {
					writeAPolygon((Polygon) g.getGeom(), rowIndex);
				} else if (g.getGeom() instanceof MultiPolygon) {
					writeAMultiPolygon((MultiPolygon) g.getGeom(), rowIndex);
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

	private void checkGeometryConstraint(final Metadata metadata,
			final int spatialFieldIndex) throws DriverException {
		final GeometryConstraint c = (GeometryConstraint) metadata
				.getFieldType(spatialFieldIndex).getConstraint(
						ConstraintNames.GEOMETRY);
		final int geometryType = c.getGeometryType();
		if ((GeometryConstraint.POLYGON_2D != geometryType)
				&& (GeometryConstraint.POLYGON_3D != geometryType)
				&& (GeometryConstraint.MULTI_POLYGON_2D != geometryType)
				&& (GeometryConstraint.MULTI_POLYGON_3D != geometryType)) {
			throw new DriverException(
					"Geometric field must be a (multi-)polygon !");
		}
	}

	private void writeAMultiPolygon(final MultiPolygon multiPolygon,
			final long rowIndex) {
		final int nbOfCtrs = multiPolygon.getNumGeometries();
		out.printf("f%ld %d\r\n", rowIndex, nbOfCtrs);
		// TODO
		out.printf(COORD3D_WRITTING_FORMAT, 99999, 99999, 99999);
		for (int i = 0; i < nbOfCtrs; i++) {
			writeAContour((Polygon) multiPolygon.getGeometryN(i));
		}
	}

	private void writeAPolygon(final Polygon p, final long rowIndex) {
		out.printf("f%ld 1\r\n", rowIndex);
		// TODO
		out.printf(COORD3D_WRITTING_FORMAT, 99999, 99999, 99999);
		writeAContour(p);
	}

	private void writeAContour(final Polygon p) {
		final LineString shell = p.getExteriorRing();
		final int nbOfHoles = p.getNumInteriorRing();
		out.printf("c%d\r\n", nbOfHoles);
		writeALinearRing(shell);
		for (int i = 0; i < nbOfHoles; i++) {
			out.printf("t\r\n");
			writeALinearRing(p.getInteriorRingN(i));
		}
	}

	private void writeALinearRing(final LineString shell) {
		final Coordinate[] nodes = shell.getCoordinates();
		out.printf("%d\r\n", nodes.length);
		for (Coordinate node : nodes) {
			if (Double.isNaN(node.z)) {
				out.printf(COORD3D_WRITTING_FORMAT, node.x, node.y, 0d);
			} else {
				out.printf(COORD3D_WRITTING_FORMAT, node.x, node.y, node.z);
			}
		}
	}

	public boolean isCommitable() {
		return true;
	}
}