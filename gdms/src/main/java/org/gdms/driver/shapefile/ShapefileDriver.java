package org.gdms.driver.shapefile;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.WarningListener;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.DriverUtilities;
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.driver.dbf.DBFDriver;
import org.gdms.spatial.SpatialDataSourceDecorator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class ShapefileDriver implements FileReadWriteDriver {

	public static final String DRIVER_NAME = "Shapefile driver";

	private static GeometryFactory gf = new GeometryFactory();

	private File fileShp;

	private Envelope envelope;

	private ShapeType type;

	private DBFDriver dbfDriver;

	private ShapefileReader reader;

	private IndexFile shxFile;

	private DataSourceFactory dataSourceFactory;

	public void close() throws DriverException {
		try {
			reader.close();
			shxFile.close();
			dbfDriver.close();
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	public String completeFileName(String fileName) {
		if (!fileName.toLowerCase().endsWith(".shp")) {
			return fileName + ".shp";
		} else {
			return fileName;
		}
	}

	public boolean fileAccepted(File f) {
		return f.getName().toUpperCase().endsWith(".SHP");
	}

	public void open(File f) throws DriverException {
		try {
			FileInputStream shpFis = new FileInputStream(f);
			WarningListener warningListener = dataSourceFactory
					.getWarningListener();
			reader = new ShapefileReader(shpFis.getChannel(), warningListener);
			FileInputStream shxFis = new FileInputStream(getFile(f, ".shx"));
			shxFile = new IndexFile(shxFis.getChannel(), warningListener);
			fileShp = f;

			ShapefileHeader header = reader.getHeader();
			envelope = new Envelope(
					new Coordinate(header.minX(), header.minY()),
					new Coordinate(header.maxX(), header.maxY()));

			type = header.getShapeType();

			String strFichDbf = getFile(fileShp, ".dbf");

			dbfDriver = new DBFDriver();
			dbfDriver.setDataSourceFactory(dataSourceFactory);
			dbfDriver.open(new File(strFichDbf));
		} catch (IOException e) {
			throw new DriverException(e);
		} catch (ShapefileException e) {
			throw new DriverException(e);
		}
	}

	private String getFile(File baseFile, final String extension)
			throws IOException {
		String base = baseFile.getAbsolutePath();
		base = base.substring(0, base.length() - 4);
		final File prefix = new File(base);
		File[] dbfs = prefix.getParentFile().listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				String ext = pathname.getName();
				if (ext.length() > 3) {
					ext = ext.substring(ext.length() - 4);
					return (pathname.getName().startsWith(prefix.getName()))
							&& ext.toLowerCase().equals(extension);
				} else {
					return false;
				}
			}

		});

		if (dbfs.length > 0) {
			return dbfs[0].getAbsolutePath();
		} else {
			throw new IOException("Cannot find " + extension + " file");
		}
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		try {
			if (fieldId == 0) {
				int offset = shxFile.getOffset((int) rowIndex);
				Geometry shape = (Geometry) reader.geomAt(offset);
				// TODO why must I replace null with NullValue ?
				// You don't have to replace it. it's replaced by
				// RightValueDecorator
				return (null == shape) ? null : ValueFactory.createValue(shape);
				// return (null == shape) ? new NullValue() :
				// ValueFactory.createValue(shape);
			} else {
				return dbfDriver.getFieldValue(rowIndex, fieldId - 1);
			}
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	public Metadata getMetadata() throws DriverException {
		DefaultMetadata metadata = new DefaultMetadata(dbfDriver.getMetadata());
		try {
			Constraint c = null;
			// In case of a geometric type, the GeometryConstraint is mandatory
			if (type.id == ShapeType.POINT.id) {
				c = new GeometryConstraint(GeometryConstraint.POINT_2D);
			} else if (type.id == ShapeType.ARC.id) {
				c = new GeometryConstraint(GeometryConstraint.LINESTRING_2D);
			} else if (type.id == ShapeType.POLYGON.id) {
				c = new GeometryConstraint(GeometryConstraint.POLYGON_2D);
			} else if (type.id == ShapeType.MULTIPOINT.id) {
				c = new GeometryConstraint(GeometryConstraint.MULTI_POINT_2D);
			} else if (type.id == ShapeType.POINTZ.id) {
				c = new GeometryConstraint(GeometryConstraint.POINT_3D);
			} else if (type.id == ShapeType.ARCZ.id) {
				c = new GeometryConstraint(GeometryConstraint.LINESTRING_3D);
			} else if (type.id == ShapeType.POLYGONZ.id) {
				c = new GeometryConstraint(GeometryConstraint.POLYGON_3D);
			} else if (type.id == ShapeType.MULTIPOINTZ.id) {
				c = new GeometryConstraint(GeometryConstraint.MULTI_POINT_3D);
			} else {
				throw new DriverException("Unknown geometric type !");
			}

			metadata.addField(0, "the_geom", Type.GEOMETRY,
					new Constraint[] { c });
		} catch (InvalidTypeException e) {
			throw new RuntimeException("Bug in the driver", e);
		}
		return metadata;
	}

	public long getRowCount() throws DriverException {
		return shxFile.getRecordCount();
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
		this.dataSourceFactory = dsf;
	}

	public String getName() {
		return DRIVER_NAME;
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

	public TypeDefinition[] getTypesDefinitions() throws DriverException {
		List<TypeDefinition> result = new LinkedList<TypeDefinition>(Arrays
				.asList(new DBFDriver().getTypesDefinitions()));
		try {
			result.add(new DefaultTypeDefinition("GEOMETRY", Type.GEOMETRY,
					new ConstraintNames[] { ConstraintNames.GEOMETRY }));
		} catch (InvalidTypeException e) {
			throw new DriverException("Invalid type");
		}
		return (TypeDefinition[]) result.toArray(new TypeDefinition[result
				.size()]);
	}

	public void copy(File in, File out) throws IOException {
		File inDBF = new File(getFile(in, ".dbf"));
		File inSHX = new File(getFile(in, ".shx"));
		File outDBF = null;
		try {
			outDBF = new File(getFile(out, ".dbf"));
		} catch (IOException e) {
			String name = in.getAbsolutePath();
			name = name.substring(0, name.length() - 4);
			outDBF = new File(name + ".dbf");
		}
		File outSHX = null;
		try {
			outSHX = new File(getFile(out, ".shx"));
		} catch (IOException e) {
			String name = in.getAbsolutePath();
			name = name.substring(0, name.length() - 4);
			outDBF = new File(name + ".shx");
		}
		DriverUtilities.copy(inDBF, outDBF);
		DriverUtilities.copy(inSHX, outSHX);
		DriverUtilities.copy(in, out);
	}

	private static Geometry convertGeometry(Geometry geom, ShapeType type)
			throws DriverException {

		Geometry retVal = null;

		if ((geom == null) || geom.isEmpty()) {
			if ((geom instanceof Point) || (geom instanceof MultiPoint)) {
				retVal = new GeometryFactory().createMultiPoint((Point[]) null);
			} else if ((geom instanceof LineString)
					|| (geom instanceof MultiLineString)) {
				retVal = new GeometryFactory()
						.createMultiLineString((LineString[]) null);
			} else if ((geom instanceof Polygon)
					|| (geom instanceof MultiPolygon)) {
				retVal = new GeometryFactory()
						.createMultiPolygon((Polygon[]) null);
			} else {
				retVal = new GeometryFactory().createMultiPoint((Point[]) null);
			}
		} else {
			if (type == ShapeType.NULL) {
				return geom;
			}

			if ((type == ShapeType.POINT) || (type == ShapeType.POINTZ)) {
				if ((geom instanceof Point)) {
					retVal = geom;
				} else if (geom instanceof MultiPoint) {
					MultiPoint mp = (MultiPoint) geom;
					if (mp.getNumGeometries() == 1) {
						retVal = mp.getGeometryN(0);
					}
				}
			} else if ((type == ShapeType.MULTIPOINT)
					|| (type == ShapeType.MULTIPOINTZ)) {
				if ((geom instanceof Point)) {
					retVal = gf.createMultiPoint(new Point[] { (Point) geom });
				} else if (geom instanceof MultiPoint) {
					retVal = geom;
				}
			} else if ((type == ShapeType.POLYGON)
					|| (type == ShapeType.POLYGONZ)) {
				if (geom instanceof Polygon) {
					Polygon p = JTSUtilities
							.makeGoodShapePolygon((Polygon) geom);
					retVal = gf.createMultiPolygon(new Polygon[] { p });
				} else if (geom instanceof MultiPolygon) {
					retVal = JTSUtilities
							.makeGoodShapeMultiPolygon((MultiPolygon) geom);
				}
			} else if ((type == ShapeType.ARC) || (type == ShapeType.ARCZ)) {
				if ((geom instanceof LineString)) {
					retVal = gf
							.createMultiLineString(new LineString[] { (LineString) geom });
				} else if (geom instanceof MultiLineString) {
					retVal = geom;
				}
			}
		}
		if (retVal == null) {
			throw new DriverException(
					"Cannot mix geometry types in a shapefile. "
							+ "ShapeType: " + type.name + " -> Geometry: "
							+ geom.toText());
		}

		return retVal;
	}

	public void createSource(String path, Metadata metadata,
			DataSourceFactory dataSourceFactory) throws DriverException {
		// write dbf
		String dbfFile = replaceExtension(new File(path), ".dbf")
				.getAbsolutePath();
		DBFDriver dbfDriver = new DBFDriver();
		dbfDriver.setDataSourceFactory(dataSourceFactory);
		dbfDriver.createSource(dbfFile, new DBFMetadata(metadata),
				dataSourceFactory);

		// write shapefile and shx
		try {
			FileOutputStream shpFis = new FileOutputStream(new File(path));
			final FileOutputStream shxFis = new FileOutputStream(
					replaceExtension(new File(path), ".shx"));

			ShapefileWriter writer = new ShapefileWriter(shpFis.getChannel(),
					shxFis.getChannel());
			writer.writeHeaders(new Envelope(0, 0, 0, 0),
					getShapeType(metadata), 0, 100);
			writer.close();
		} catch (FileNotFoundException e) {
			throw new DriverException(e);
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	private ShapeType getShapeType(Metadata metadata) throws DriverException {
		for (int i = 0; i < metadata.getFieldCount(); i++) {
			if (metadata.getFieldType(i).getTypeCode() == Type.GEOMETRY) {
				Constraint gc = metadata.getFieldType(i).getConstraint(
						ConstraintNames.GEOMETRY);
				if (gc == null) {
					return null;
				} else {
					int gt = ((GeometryConstraint) gc).getGeometryType();
					switch (gt) {
					case GeometryConstraint.POINT_2D:
						return ShapeType.POINT;
					case GeometryConstraint.POINT_3D:
						return ShapeType.POINTZ;
					case GeometryConstraint.MULTI_POINT_2D:
						return ShapeType.MULTIPOINT;
					case GeometryConstraint.MULTI_POINT_3D:
						return ShapeType.MULTIPOINTZ;
					case GeometryConstraint.LINESTRING_2D:
					case GeometryConstraint.MULTI_LINESTRING_2D:
						return ShapeType.ARC;
					case GeometryConstraint.LINESTRING_3D:
					case GeometryConstraint.MULTI_LINESTRING_3D:
						return ShapeType.ARCZ;
					case GeometryConstraint.POLYGON_2D:
					case GeometryConstraint.MULTI_POLYGON_2D:
						return ShapeType.POLYGON;
					case GeometryConstraint.POLYGON_3D:
					case GeometryConstraint.MULTI_POLYGON_3D:
						return ShapeType.POLYGONZ;
					}

					return null;
				}
			}
		}

		throw new IllegalArgumentException("The data "
				+ "source doesn't contain any spatial field");
	}

	public void writeFile(final File file, final DataSource dataSource)
			throws DriverException {
		WarningListener warningListener = dataSourceFactory
				.getWarningListener();
		// write dbf
		DBFDriver dbfDriver = new DBFDriver();
		dbfDriver.setDataSourceFactory(dataSourceFactory);
		dbfDriver.writeFile(replaceExtension(file, ".dbf"),
				new DBFRowProvider(dataSource), warningListener);

		// write shapefile and shx
		try {
			SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
					dataSource);
			FileOutputStream shpFis = new FileOutputStream(file);
			final FileOutputStream shxFis = new FileOutputStream(
					replaceExtension(file, ".shx"));

			ShapefileWriter writer = new ShapefileWriter(shpFis.getChannel(),
					shxFis.getChannel());
			Envelope fullExtent = sds.getFullExtent();
			ShapeType shapeType = getShapeType(dataSource.getMetadata());
			if (shapeType == null) {
				warningListener.throwWarning("No geometry type in the "
						+ "metadata. Will take the type of the first geometry");
				shapeType = getFirstShapeType(sds);
				if (shapeType == null) {
					throw new IllegalArgumentException("A "
							+ "geometry type have to be specified");
				}
			}
			int fileLength = computeSize(sds, shapeType);
			writer.writeHeaders(fullExtent, shapeType, (int) sds.getRowCount(),
					fileLength);
			for (int i = 0; i < sds.getRowCount(); i++) {
				Geometry geometry = sds.getGeometry(i);
				if (geometry != null) {
					writer.writeGeometry(convertGeometry(geometry, shapeType));
				} else {
					writer.writeGeometry(null);
				}
			}
			writer.close();
		} catch (FileNotFoundException e) {
			throw new DriverException(e);
		} catch (IOException e) {
			throw new DriverException(e);
		} catch (ShapefileException e) {
			throw new DriverException(e);
		}
	}

	private File replaceExtension(File file, String suffix) {
		String prefix = file.getAbsolutePath();
		prefix = prefix.substring(0, prefix.lastIndexOf('.'));
		return new File(prefix + suffix);
	}

	private ShapeType getFirstShapeType(SpatialDataSourceDecorator sds)
			throws DriverException {
		for (int i = 0; i < sds.getRowCount(); i++) {
			Geometry geom = sds.getGeometry(i);
			if (geom != null) {
				return getShapeType(geom);
			}
		}

		return null;
	}

	private ShapeType getShapeType(Geometry geom) {
		if (geom instanceof Point) {
			if (geom.getCoordinate().z == Double.NaN) {
				return ShapeType.POINT;
			} else {
				return ShapeType.POINTZ;
			}
		} else if ((geom instanceof LineString)
				|| (geom instanceof MultiLineString)) {
			if (geom.getCoordinate().z == Double.NaN) {
				return ShapeType.ARC;
			} else {
				return ShapeType.ARCZ;
			}
		} else if ((geom instanceof Polygon) || (geom instanceof MultiPolygon)) {
			if (geom.getCoordinate().z == Double.NaN) {
				return ShapeType.POLYGON;
			} else {
				return ShapeType.POLYGONZ;
			}
		} else if (geom instanceof MultiPoint) {
			if (geom.getCoordinate().z == Double.NaN) {
				return ShapeType.MULTIPOINT;
			} else {
				return ShapeType.MULTIPOINTZ;
			}
		} else {
			throw new IllegalArgumentException("Unrecognized geometry type : "
					+ geom.getClass());
		}
	}

	private int computeSize(SpatialDataSourceDecorator dataSource,
			ShapeType type) throws DriverException, ShapefileException {
		int fileLength = 100;
		for (int i = (int) (dataSource.getRowCount() - 1); i >= 0; i--) {
			Geometry geometry = dataSource.getGeometry(i);
			if (geometry != null) {
				// shape length + record (2 ints)
				int size = type.getShapeHandler().getLength(
						convertGeometry(geometry, type)) + 8;
				fileLength += size;
			} else {
				// null byte + record (2 ints)
				fileLength += 4 + 8;
			}
		}

		return fileLength;
	}

	public boolean isCommitable() {
		return true;
	}
}