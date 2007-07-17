package org.gdms.driver.solene;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

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
import org.gdms.driver.FileDriver;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

// call register('../../datas2tests/cir/faceTrouee.cir','moncir');

public class CirDriver implements FileDriver {
	public static final String DRIVER_NAME = "Solene Cir driver";

	private String EXTENSION = ".cir";

	private Scanner in;

	private List<Value[]> rows;

	private Envelope envelope;

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
			in.useLocale(Locale.US);

			final int nbFacesCir = in.nextInt();
			in.next(); // useless "supNumFaces"
			for (int i = 0; i < 10; i++) {
				// 5 rows of 2 useless values
				in.next();
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
		_readCoordinate(); // useless "normal"
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
}