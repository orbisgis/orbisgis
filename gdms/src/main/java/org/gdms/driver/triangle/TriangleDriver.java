package org.gdms.driver.triangle;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.source.SourceManager;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;

public class TriangleDriver implements FileReadWriteDriver {
	public final static String DRIVER_NAME = "Triangle (2D mesh generator) driver";
	private final static String[] EXTENSIONS = new String[] { ".node", ".poly",
			".ele", ".edge", ".v.node", ".v.edge" }; // + ".area", ".neigh"
	private static GeometryFactory geometryFactory = new GeometryFactory();
	private List<Value[]> rows;
	private Envelope envelope;

	public void copy(File in, File out) throws IOException {
		// TODO Auto-generated method stub

	}

	public void createSource(String path, Metadata metadata,
			DataSourceFactory dataSourceFactory) throws DriverException {
		// TODO Auto-generated method stub

	}

	public void writeFile(File file, DataSource dataSource)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void close() throws DriverException {
		// TODO Auto-generated method stub

	}

	public String completeFileName(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean fileAccepted(File f) {
		final String tmp = f.getAbsolutePath().toLowerCase();
		return (tmp.endsWith(".node") && !tmp.endsWith(".v.node"))
				|| (tmp.endsWith(".poly") && !tmp.endsWith(".v.poly"));
	}

	private String removeFilenameExtension(File file) {
		final String tmp = file.getAbsolutePath();
		return tmp.substring(0, tmp.length() - 5);
	}

	public void open(File file) throws DriverException {
		final String radical = removeFilenameExtension(file);
		for (String extension : EXTENSIONS) {
			File f = new File(radical.concat(extension));
			if (f.exists()) {

			}

		}
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

	public int getType() {
		return SourceManager.TRIANGLE;
	}

	public TypeDefinition[] getTypesDefinitions() throws DriverException {
		final TypeDefinition[] result = new TypeDefinition[2];
		result[0] = new DefaultTypeDefinition("STRING", Type.STRING,
				new ConstraintNames[] { ConstraintNames.UNIQUE,
						ConstraintNames.NOT_NULL });
		result[1] = new DefaultTypeDefinition("GEOMETRY", Type.GEOMETRY,
				new ConstraintNames[] { ConstraintNames.GEOMETRY });
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

	public boolean isCommitable() {
		return true;
	}
}