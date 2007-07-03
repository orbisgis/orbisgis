package org.gdms.driver.shapefile;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.DefaultType;
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
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.geotoolsAdapter.FeatureCollectionAdapter;
import org.gdms.geotoolsAdapter.FeatureTypeAdapter;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.geotools.data.DataSourceException;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.FeatureWriter;
import org.geotools.data.PrjFileReader;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.feature.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.driverManager.DriverLoadException;
import com.vividsolutions.jts.algorithm.RobustCGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;

public class ShapefileDriver implements FileReadWriteDriver {

	private File fileShp;

	private FileInputStream fin;

	private FileChannel channel;

	private BigByteBuffer2 bb;

	private FileInputStream finShx;

	private FileChannel channelShx;

	private BigByteBuffer2 bbShx;

	private GeometryFactory gf;

	private Envelope envelope;

	private int type;

	private int numReg;

	private DBFDriver dbfDriver;

	public ShapefileDriver() {
		gf = new GeometryFactory();
	}

	public void close() throws DriverException {
		try {
			channel.close();
			channelShx.close();
			fin.close();
			finShx.close();
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
			fileShp = f;
			fin = new FileInputStream(f);
			// Open the file and then get a channel from the stream
			channel = fin.getChannel();
			bb = new BigByteBuffer2(channel, FileChannel.MapMode.READ_ONLY);
			finShx = new FileInputStream(getFile(fileShp, ".shx"));

			// Open the file and then get a channel from the stream
			channelShx = finShx.getChannel();
			bbShx = new BigByteBuffer2(channelShx,
					FileChannel.MapMode.READ_ONLY);
			bbShx.order(ByteOrder.BIG_ENDIAN);

			// create a new header.
			ShapeFileHeader myHeader = new ShapeFileHeader();

			bb.position(0);

			// read the header
			myHeader.readHeader(bb);

			envelope = new Envelope(new Coordinate(myHeader.myXmin,
					myHeader.myYmin), new Coordinate(myHeader.myXmax,
					myHeader.myYmax));

			type = myHeader.myShapeType;

			String strFichDbf = getFile(fileShp, ".dbf");

			dbfDriver = new DBFDriver();

			dbfDriver.open(new File(strFichDbf));
			numReg = (int) dbfDriver.getRowCount();
		} catch (IOException e) {
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
			throw new IOException("Cannot find dbf file");
		}
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		try {
			if (fieldId == 0) {
				return ValueFactory
						.createValue((Geometry) getShape((int) rowIndex));
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
			switch (type) {
			case ShapeFileHeader.SHAPE_POINT:
				c = new GeometryConstraint(GeometryConstraint.POINT_2D);
				break;
			case ShapeFileHeader.SHAPE_POLYLINE:
				c = new GeometryConstraint(GeometryConstraint.LINESTRING_2D);
				break;
			case ShapeFileHeader.SHAPE_POLYGON:
				c = new GeometryConstraint(GeometryConstraint.POLYGON_2D);
				break;
			case ShapeFileHeader.SHAPE_MULTIPOINT:
				c = new GeometryConstraint(GeometryConstraint.MULTI_POINT_2D);
				break;
			case ShapeFileHeader.SHAPE_POINTZ:
				c = new GeometryConstraint(GeometryConstraint.POINT_3D);
				break;
			case ShapeFileHeader.SHAPE_POLYLINEZ:
				c = new GeometryConstraint(GeometryConstraint.LINESTRING_3D);
				break;
			case ShapeFileHeader.SHAPE_POLYGONZ:
				c = new GeometryConstraint(GeometryConstraint.POLYGON_3D);
				break;
			case ShapeFileHeader.SHAPE_MULTIPOINTZ:
				c = new GeometryConstraint(GeometryConstraint.MULTI_POINT_3D);
				break;
			default:
				throw new DriverException("Unknown geometric type !");
			}

			metadata.addField(0, "the_geom", Type.GEOMETRY,
					new Constraint[] { c });
		} catch (InvalidTypeException e) {
			throw new RuntimeException("Bug in the driver", e);
		}
		return metadata;
	}

	public int getType(String driverType) {
		if (DefaultType.typesDescription.get(Type.GEOMETRY).equals(driverType)) {
			return Type.GEOMETRY;
		} else {
			return dbfDriver.getType(driverType);
		}
	}

	private long getPositionForRecord(int numRec) {
		/*
		 * shx file has a 100 bytes header. Records are 8 bytes length, there is
		 * one for each entity. first 4 bytes are the offset next 4 bytes are
		 * length
		 */

		int posIndex = 100 + (numRec * 8);
		long pos = 8 + 2 * bbShx.getInt(posIndex);

		return pos;
	}

	/**
	 * Reads the Point from the shape file.
	 *
	 * @param in
	 *            ByteBuffer.
	 *
	 * @return Point2D.
	 */
	private synchronized Coordinate readPoint(BigByteBuffer2 in) {
		// bytes 1 to 4 are the type and have already been read.
		// bytes 4 to 12 are the X coordinate
		in.order(ByteOrder.LITTLE_ENDIAN);
		return new Coordinate(in.getDouble(), in.getDouble());
	}

	private Geometry getShape(int index) throws IOException {
		Coordinate p = new Coordinate();
		int numParts;
		int numPoints;
		int i;
		int shapeType;

		bb.position(getPositionForRecord(index));
		bb.order(ByteOrder.LITTLE_ENDIAN);

		// /bb.position(bb.position()+4);
		shapeType = bb.getInt();
		// el shape tal con tema tal y n�mro tal es null
		if (shapeType == SHP.NULL) {
			return null;
		}

		// retrieve that shape.
		switch (type) {
		case (SHP.POINT2D):
			p = readPoint(bb);

			return gf.createPoint(p);

		case (SHP.POLYLINE3D):
		case (SHP.POLYLINE2D): {
			bb.position(bb.position() + 32);
			numParts = bb.getInt();
			numPoints = bb.getInt();

			int[] partsIndex = new int[numParts + 1];

			for (i = 0; i < numParts; i++) {
				partsIndex[i] = bb.getInt();
			}
			partsIndex[numParts] = numPoints;

			List<Coordinate> allCoordinates = new ArrayList<Coordinate>();
			List<LineString> lineStrings = new ArrayList<LineString>();
			for (int partIndex = 0; partIndex < numParts; partIndex++) {
				List<Coordinate> current = new ArrayList<Coordinate>();
				for (int j = partsIndex[partIndex]; j < partsIndex[partIndex + 1]; j++) {
					p = readPoint(bb);
					Coordinate newC = new Coordinate(p.x, p.y);
					current.add(newC);
					allCoordinates.add(newC);
				}
				lineStrings.add(gf.createLineString(current
						.toArray(new Coordinate[0])));
			}

			if (type == SHP.POLYLINE3D) {
				/*
				 * Read Z range
				 */
				bb.getDouble();
				bb.getDouble();

				/*
				 * Asing the z values
				 */
				for (i = 0; i < allCoordinates.size(); i++) {
					allCoordinates.get(i).z = bb.getDouble();
				}
			}

			return gf.createMultiLineString(lineStrings
					.toArray(new LineString[0]));
		}
		case (SHP.POLYGON3D):
		case (SHP.POLYGON2D): {
			bb.position(bb.position() + 32);
			numParts = bb.getInt();
			numPoints = bb.getInt();

			int[] partsIndex = new int[numParts + 1];

			for (i = 0; i < numParts; i++) {
				partsIndex[i] = bb.getInt();
			}
			partsIndex[numParts] = numPoints - 1;

			List<Coordinate> allCoordinates = new ArrayList<Coordinate>();
			List<LinearRing> innerRings = new ArrayList<LinearRing>();
			LinearRing outerRing = null;
			for (int partIndex = 0; partIndex < numParts; partIndex++) {
				List<Coordinate> current = new ArrayList<Coordinate>();
				for (int j = partsIndex[partIndex]; j < partsIndex[partIndex + 1]; j++) {
					p = readPoint(bb);
					Coordinate newC = new Coordinate(p.x, p.y);
					current.add(newC);
					allCoordinates.add(newC);
				}
				if (!current.get(0).equals(current.get(current.size() - 1))) {
					Coordinate first = current.get(0);
					Coordinate closingCoord = new Coordinate(first.x, first.y);
					current.add(closingCoord);
					allCoordinates.add(closingCoord);
				}
				LinearRing newRing = gf.createLinearRing(current
						.toArray(new Coordinate[0]));
				if (RobustCGAlgorithms
						.isCCW(current.toArray(new Coordinate[0]))) {
					innerRings.add(newRing);
				} else {
					outerRing = newRing;
				}
			}

			if (type == SHP.POLYGON3D) {
				/*
				 * Read Z range
				 */
				bb.getDouble();
				bb.getDouble();

				/*
				 * Asing the z values
				 */
				for (i = 0; i < allCoordinates.size(); i++) {
					allCoordinates.get(i).z = bb.getDouble();
				}
			}

			/*
			 * Check bad shapefiles
			 */
			if (outerRing == null) {
				if (innerRings.size() > 0) {
					outerRing = innerRings.get(0);
					innerRings.remove(0);
				}
			}

			return gf.createPolygon(outerRing, innerRings
					.toArray(new LinearRing[0]));
		}
		case (SHP.POINT3D):

			double x = bb.getDouble();
			double y = bb.getDouble();
			double z = bb.getDouble();

			return gf.createPoint(new Coordinate(x, y, z));

		case (SHP.MULTIPOINT2D):
		case (SHP.MULTIPOINT3D):
			bb.position(bb.position() + 32);
			numPoints = bb.getInt();

			Coordinate[] coords = new Coordinate[numPoints];

			for (i = 0; i < numPoints; i++) {
				coords[i] = new Coordinate(bb.getDouble(), bb.getDouble());
			}
			if (shapeType == SHP.MULTIPOINT3D) {
				for (int j = 0; j < coords.length; j++) {
					coords[i].z = bb.getDouble();
				}
			}

			return gf.createMultiPoint(coords);

		}

		return null;
	}

	public long getRowCount() throws DriverException {
		return numReg;
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	public String getName() {
		return "Shapefile driver";
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

	public int getGeometryType() throws DriverException {
		switch (type) {
		case SHP.POINT2D:
			return GeometryConstraint.POINT_2D;
		case SHP.POINT3D:
			return GeometryConstraint.POINT_3D;
		case SHP.MULTIPOINT2D:
			return GeometryConstraint.MULTI_POINT_2D;
		case SHP.MULTIPOINT3D:
			return GeometryConstraint.MULTI_POINT_3D;

		case SHP.POLYLINE2D:
			return GeometryConstraint.LINESTRING_2D;
			// return GeometryConstraint.MULTI_LINESTRING_2D;
		case SHP.POLYLINE3D:
			return GeometryConstraint.LINESTRING_3D;
			// return GeometryConstraint.MULTI_LINESTRING_3D;

		case SHP.POLYGON2D:
			return GeometryConstraint.POLYGON_2D;
			// return GeometryConstraint.MULTI_POLYGON_2D;
		case SHP.POLYGON3D:
			return GeometryConstraint.POLYGON_3D;
			// return GeometryConstraint.MULTI_POLYGON_3D;
		}

		throw new DriverException("Unrecognized Geometry Type: " + type);
	}

	private File getPrjFile() {
		String fileNamePrefix = fileShp.getAbsolutePath();
		fileNamePrefix = fileNamePrefix.substring(0,
				fileNamePrefix.length() - 4);
		File prjFile = null;

		if (new File(fileNamePrefix + ".prj").exists()) {
			prjFile = new File(fileNamePrefix + ".prj");
		} else if (new File(fileNamePrefix + ".PRJ").exists()) {
			prjFile = new File(fileNamePrefix + ".PRJ");
		}
		return prjFile;
	}

	public CoordinateReferenceSystem getCRS(final String fieldName)
			throws DriverException {
		// fieldname is not taken into account here, because in a SHP file,
		// there is only one spatial field
		CoordinateReferenceSystem crs = null;
		final File prjFile = getPrjFile();

		if (null != prjFile) {
			try {
				PrjFileReader prjFileReader = new PrjFileReader(
						new FileInputStream(prjFile).getChannel());
				crs = prjFileReader.getCoodinateSystem();
			} catch (IOException e) {
				throw new DriverException(e);
			} catch (FactoryException e) {
				throw new DriverException(e);
			}
		}
		return crs;
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

	private static void mutual(final String path, Metadata metadata,
			int spatialFieldIndex, SpatialDataSourceDecorator sds,
			DataSourceFactory dataSourceFactory) throws DriverException {
		final File file = new File(path);

		final FeatureType featureType = new FeatureTypeAdapter(metadata,
				spatialFieldIndex);
		try {
			final ShapefileDataStore shapefileDataStore = new ShapefileDataStore(
					file.toURI().toURL());
			shapefileDataStore.createSchema(featureType);
			final FeatureSource featureSource = shapefileDataStore
					.getFeatureSource(featureType.getTypeName());
			final FeatureStore featureStore = (FeatureStore) featureSource;
			final Transaction transaction = featureStore.getTransaction();

			try {
				if (null != sds) {
					addFeatures(sds, shapefileDataStore);
				} else {
					final ObjectMemoryDriver driver = new ObjectMemoryDriver(
							metadata);
					final DataSource resultDs = dataSourceFactory
							.getDataSource(driver);
					sds = new SpatialDataSourceDecorator(resultDs);
					sds.open();
					addFeatures(sds, shapefileDataStore);
					sds.cancel();
				}
			} catch (DriverException e) {
				// TODO
				throw new Error();
			} catch (DriverLoadException e) {
				// TODO
				throw new Error();
			} catch (DataSourceCreationException e) {
				// TODO
				throw new Error();
			}
			transaction.commit();
			transaction.close();
		} catch (MalformedURLException e) {
			throw new DriverException(e);
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	private static void addFeatures(SpatialDataSourceDecorator sds,
			ShapefileDataStore shapefileDataStore) throws IOException,
			DriverException {
		FeatureCollection collection = new FeatureCollectionAdapter(sds);
		String typeName = collection.getSchema().getTypeName();
		Feature feature = null;
		SimpleFeature newFeature;
		FeatureWriter writer = shapefileDataStore.getFeatureWriterAppend(
				typeName, Transaction.AUTO_COMMIT);

		Iterator iterator = collection.iterator();
		int index = 0;
		try {

			while (iterator.hasNext()) {
				feature = (Feature) iterator.next();
				newFeature = (SimpleFeature) writer.next();
				try {
					newFeature.setAttributes(feature.getAttributes(null));
				} catch (IllegalAttributeException writeProblem) {
					throw new DataSourceException("Could not create "
							+ typeName + " out of provided feature: "
							+ feature.getID(), writeProblem);
				}

				writer.write();
				index++;
			}
		} catch (ClassCastException e) {
			throw new DriverException("Incompatible types at row " + index, e);
		} finally {
			collection.close(iterator);
			writer.close();
		}
	}

	public void createSource(String path, Metadata metadata,
			DataSourceFactory dataSourceFactory) throws DriverException {
		final File file = new File(path);
		file.getParentFile().mkdirs();

		int spatialFieldIndex = -1;
		for (int fieldId = 0; fieldId < metadata.getFieldCount(); fieldId++) {
			final Constraint c = metadata.getFieldType(fieldId).getConstraint(
					ConstraintNames.GEOMETRY);
			if (null != c) {
				spatialFieldIndex = fieldId;
				break;
			}
		}
		mutual(path, metadata, spatialFieldIndex, null, dataSourceFactory);
	}

	public void writeFile(final File file, final DataSource dataSource)
			throws DriverException {
		final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
				dataSource);
		mutual(file.getAbsolutePath(), dataSource.getMetadata(), sds
				.getSpatialFieldIndex(), sds, null);
	}

	public boolean isCommitable() {
		return true;
	}
}