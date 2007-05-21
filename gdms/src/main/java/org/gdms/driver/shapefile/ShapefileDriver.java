package org.gdms.driver.shapefile;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.edition.Field;
import org.gdms.data.metadata.DefaultDriverMetadata;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.dbf.DBFDriver;
import org.gdms.spatial.FID;
import org.gdms.spatial.PTTypes;
import org.gdms.spatial.SpatialDataSource;
import org.geotools.data.PrjFileReader;
import org.geotools.data.coverage.grid.AbstractGridFormat;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.algorithm.RobustCGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;

public class ShapefileDriver implements FileDriver {

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
			finShx = new FileInputStream(getShxFile(f));

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

			envelope = new Envelope(myHeader.myXmin, myHeader.myYmin,
					myHeader.myXmax, myHeader.myYmax);

			type = myHeader.myShapeType;

			String strFichDbf = getDBFFile();

			dbfDriver = new DBFDriver();

			dbfDriver.open(new File(strFichDbf));
			numReg = (int) dbfDriver.getRowCount();
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	private String getDBFFile() throws DriverException {
		String base = fileShp.getAbsolutePath();
		base = base.substring(0, base.length() - 4);
		final File prefix = new File(base);
		File[] dbfs = prefix.getParentFile().listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				String ext = pathname.getName();
				ext = ext.substring(ext.length() - 4);
				return (pathname.getName().startsWith(prefix.getName()))
						&& ext.toLowerCase().equals(".dbf");
			}

		});

		if (dbfs.length > 0) {
			return dbfs[0].getAbsolutePath();
		} else {
			throw new DriverException("Cannot find dbf file");
		}
	}

	private File getShxFile(File f) {
		String str = f.getAbsolutePath();

		return new File(str.substring(0, str.length() - 3) + "shx");
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

	public DriverMetadata getDriverMetadata() throws DriverException {
		DefaultDriverMetadata ret = new DefaultDriverMetadata();
		ret.addField("the_geom", PTTypes.STR_GEOMETRY);
		ret.addAll(dbfDriver.getDriverMetadata());
		return ret;
	}

	public int getType(String driverType) {
		if (PTTypes.STR_GEOMETRY.equals(driverType)) {
			return PTTypes.GEOMETRY;
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

	public Number[] getScope(int dimension, String fieldName)
			throws DriverException {
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
		case SHP.MULTIPOINT2D:
		case SHP.MULTIPOINT3D:
			return SpatialDataSource.MULTIPOINT;
		case SHP.POINT2D:
		case SHP.POINT3D:
			return SpatialDataSource.POINT;
		case SHP.POLYGON2D:
		case SHP.POLYGON3D:
			return SpatialDataSource.MULTIPOLYGON;
		case SHP.POLYLINE2D:
		case SHP.POLYLINE3D:
			return SpatialDataSource.MULTILINESTRING;
		}

		throw new DriverException("Unrecognized Geometry Type: " + type);
	}

	public void createSource(String path, DriverMetadata dsm)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void writeFile(File file, SpatialDataSource dataSource)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void copy(File in, File out) throws IOException {
		// TODO Auto-generated method stub

	}

	public String check(Field field, Value value) throws DriverException {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getAvailableTypes() throws DriverException {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getParameters(String driverType) throws DriverException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isReadOnly(int i) throws DriverException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isValidParameter(String driverType, String paramName,
			String paramValue) {
		// TODO Auto-generated method stub
		return false;
	}

	public void writeFile(File file, DataSource dataSource)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.gdms.driver.ReadOnlyDriver#getFid(long)
	 */
	public FID getFid(long row) {
		return null;
	}

	/**
	 * @see org.gdms.driver.ReadOnlyDriver#hasFid()
	 * 
	 * In a shapefile there is no FID field.
	 */
	public boolean hasFid() {
		return false;
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
				throw new DriverException();
			} catch (FactoryException e) {
				throw new DriverException();
			}
		}
		return crs;
	}
}