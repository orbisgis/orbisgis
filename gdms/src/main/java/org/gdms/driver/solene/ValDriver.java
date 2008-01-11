/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
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
import org.gdms.source.SourceManager;

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
		for (int boundIdx = 0; boundIdx < nbContours; boundIdx++) {
			final double tmp = in.nextDouble();
			min = (tmp < min) ? tmp : min;
			max = (tmp > max) ? tmp : max;
			rows.add(new Value[] {
					ValueFactory.createValue(faceIdx + "_" + boundIdx),
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
		result[0] = new DefaultTypeDefinition("STRING", Type.STRING,
				new ConstraintNames[] { ConstraintNames.UNIQUE,
						ConstraintNames.NOT_NULL });
		result[1] = new DefaultTypeDefinition("DOUBLE", Type.DOUBLE,
				new ConstraintNames[] { ConstraintNames.NOT_NULL });
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

	public int getType() {
		return SourceManager.VAL;
	}

}