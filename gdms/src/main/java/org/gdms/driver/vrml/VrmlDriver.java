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
package org.gdms.driver.vrml;

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
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;
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
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

/**
 * @author Thomas LEDUC
 * 
 */
public class VrmlDriver implements FileReadWriteDriver {
	private Scanner in;
	private PrintWriter out;
	private List<Value[]> rows;
	private Envelope envelope;
	private final static String EOL = "\r\n";
	private final static String VRML_LINE_FMT = "Shape {" + EOL
			+ "\tappearance Appearance {" + EOL + "\t\tmaterial Material {"
			+ EOL + "\t\t\tdiffuseColor 1 1 0" + EOL + "\t\t}" + EOL + "\t}"
			+ EOL + "\tgeometry IndexedLineSet {" + EOL
			+ "\t\tcoord Coordinate {" + EOL + "\t\t\tpoint [" + EOL + "%s"
			+ "\t\t\t]" + EOL + "\t\t}" + EOL + "\t\tcoordIndex [" + EOL + "%s"
			+ EOL + "\t\t]" + EOL + "\t}" + EOL + "}" + EOL + EOL;
	private final static String VRML_FACE_FMT = "Shape {" + EOL
			+ "\tappearance Appearance {" + EOL + "\t\tmaterial Material {"
			+ EOL + "\t\t\tdiffuseColor 1 1 0" + EOL + "\t\t}" + EOL + "\t}"
			+ EOL + "\tgeometry IndexedFaceSet {" + EOL
			+ "\t\tcoord Coordinate {" + EOL + "\t\t\tpoint [" + EOL + "%s"
			+ "\t\t\t]" + EOL + "\t\t}" + EOL + "\t\tcoordIndex [" + EOL
			+ "\t\t\t%s" + EOL + "\t\t]" + EOL + "\t}" + EOL + "}" + EOL + EOL;

	public void close() throws DriverException {
		in.close();
	}

	public void open(File file) throws DriverException {
		try {
			rows = new ArrayList<Value[]>();
			in = new Scanner(file);
			in.useLocale(Locale.US); // essential to read float values
			// TODO needs to be written
		} catch (FileNotFoundException e) {
			throw new DriverException(e);
		}
	}

	public Metadata getMetadata() throws DriverException {
		return new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.GEOMETRY) }, new String[] { "gid",
				"the_geom" });
	}

	public TypeDefinition[] getTypesDefinitions() {
		final TypeDefinition[] result = new TypeDefinition[2];
		result[0] = new DefaultTypeDefinition("STRING", Type.STRING);
		result[1] = new DefaultTypeDefinition("GEOMETRY", Type.GEOMETRY);
		return result;
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	public String getDriverId() {
		return "VRML driver";
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
		try {
			out = new PrintWriter(new FileOutputStream(file));

			// write header part...
			out.printf("#VRML V2.0 utf8%s", EOL);

			// write body part...
			final long rowCount = dataSource.getRowCount();

			for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				if (rowIndex / 100 == rowIndex / 100.0) {
					if (pm.isCancelled()) {
						break;
					} else {
						pm.progressTo((int) (100 * rowIndex / dataSource
								.getRowCount()));
					}
				}

				final Geometry g = sds.getGeometry(rowIndex);
				write(g);
			}
			out.close();
		} catch (FileNotFoundException e) {
			throw new DriverException(e);
		}
	}

	private void write(final Geometry geometry) {
		if (geometry instanceof GeometryCollection) {
			final GeometryCollection gc = (GeometryCollection) geometry;
			final int nbOfGeometries = geometry.getNumGeometries();
			for (int i = 0; i < nbOfGeometries; i++) {
				write(gc.getGeometryN(i));
			}
		} else if (geometry instanceof LineString) {
			write((LineString) geometry);
		} else if (geometry instanceof Polygon) {
			write((Polygon) geometry);
		} else {
			throw new RuntimeException("Needs to be written");
		}
	}

	private void write(final LineString lineString) {
		final StringBuffer sb_coords = new StringBuffer();
		final StringBuffer sb_idx = new StringBuffer();
		final Coordinate[] coordinates = lineString.getCoordinates();
		for (int i = 0; i < coordinates.length; i++) {
			sb_coords.append("\t\t\t\t").append(coordinates[i].x).append(" ")
					.append(coordinates[i].y).append(" ").append(
							coordinates[i].z).append(",").append(EOL);
			sb_idx.append(i).append(", ");
		}
		out.printf(VRML_LINE_FMT, sb_coords.toString(), sb_idx.toString());
	}

	private void write(final Polygon polygon) {
		final StringBuffer sb_coords = new StringBuffer();
		final StringBuffer sb_idx = new StringBuffer();
		final Coordinate[] coordinates = polygon.getExteriorRing()
				.getCoordinates();
		for (int i = 0; i < coordinates.length; i++) {
			sb_coords.append("\t\t\t\t").append(coordinates[i].x).append(" ")
					.append(coordinates[i].y).append(" ").append(
							coordinates[i].z).append(",").append(EOL);
			sb_idx.append(i).append(", ");
		}
		out.printf(VRML_FACE_FMT, sb_coords.toString(), sb_idx.toString());
	}

	public boolean isCommitable() {
		return true;
	}

	public int getType() {
		return SourceManager.FILE | SourceManager.VECTORIAL;
	}

	public String validateMetadata(Metadata metadata) throws DriverException {
		throw new RuntimeException("Needs to be written");
	}

	@Override
	public String[] getFileExtensions() {
		return new String[] { "wrl" };
	}

	@Override
	public String getTypeDescription() {
		return "VRML file";
	}

	@Override
	public String getTypeName() {
		return "VRML";
	}
}