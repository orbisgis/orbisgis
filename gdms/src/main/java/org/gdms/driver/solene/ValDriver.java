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
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.NotNullConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.UniqueConstraint;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;

public class ValDriver implements FileDriver {
	public static final String DRIVER_NAME = "Solene Val driver";

	private String EXTENSION = ".val";

	private Scanner in;

	private List<Value[]> rows;

	private double min = Double.MAX_VALUE;

	private double max = Double.MIN_VALUE;

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
			in.nextDouble(); // useless "read min"
			in.nextDouble(); // useless "read max"
			for (int i = 0; i < nbFacesCir; i++) {
				_readFace();
			}
		} catch (FileNotFoundException e) {
			throw new DriverException(e);
		}
	}

	private final void _readFace() throws DriverException {
		final String faceIdx = in.next();
		if (!faceIdx.startsWith("f")) {
			throw new DriverException("Bad VAL file format (f) !");
		}
		final int nbContours = in.nextInt();
		for (int i = 0; i < nbContours; i++) {
			final double tmp = in.nextDouble();
			min = (tmp < min) ? tmp : min;
			max = (tmp > max) ? tmp : max;
			rows.add(new Value[] { ValueFactory.createValue(faceIdx + "_" + i),
					ValueFactory.createValue(tmp) });
		}
	}

	public Metadata getMetadata() throws DriverException {
		final DefaultMetadata metadata = new DefaultMetadata();
		try {
			metadata.addField("id", Type.STRING, new Constraint[] {
					new UniqueConstraint(), new NotNullConstraint() });
			metadata.addField("noName", Type.DOUBLE,
					new Constraint[] { new NotNullConstraint() });
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
			result[1] = new DefaultTypeDefinition("DOUBLE", Type.DOUBLE,
					new ConstraintNames[] { ConstraintNames.NOT_NULL });
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
		return null;
	}
}