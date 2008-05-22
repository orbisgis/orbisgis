package org.gdms.data.values;

import ij.ImagePlus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.gdms.data.types.Type;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.orbisgis.utils.ByteUtils;

public class RasterValue extends AbstractValue {

	private GeoRaster geoRaster;

	RasterValue(GeoRaster geoRaster) {
		this.geoRaster = geoRaster;
	}

	public String getStringValue(ValueWriter writer) {
		return "Raster";
	}

	public int getType() {
		return Type.RASTER;
	}

	public int doHashCode() {
		return geoRaster.hashCode();
	}

	@Override
	public boolean doEquals(Object obj) {
		if (obj instanceof RasterValue) {
			return geoRaster.equals(((RasterValue) obj).geoRaster);
		} else {
			return false;
		}
	}

	public byte[] getBytes() {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			geoRaster.open();
			// Write metadata
			RasterMetadata metadata = geoRaster.getMetadata();
			dos.writeDouble(metadata.getXulcorner());
			dos.writeDouble(metadata.getYulcorner());
			dos.writeInt(metadata.getNRows());
			dos.writeInt(metadata.getNCols());
			dos.writeFloat(metadata.getPixelSize_X());
			dos.writeFloat(metadata.getPixelSize_Y());
			dos.writeDouble(metadata.getRotation_X());
			dos.writeDouble(metadata.getRotation_Y());
			dos.writeFloat(metadata.getNoDataValue());
			// Write cached values
			dos.writeDouble(geoRaster.getMin());
			dos.writeDouble(geoRaster.getMax());
			// Write data
			int imageType = geoRaster.getType();
			dos.writeInt(imageType);
			switch (imageType) {
			case ImagePlus.COLOR_256:
			case ImagePlus.GRAY8:
				byte[] bytePixels = geoRaster.getBytePixels();
				dos.write(bytePixels);
				break;
			case ImagePlus.GRAY16:
				short[] shortPixels = geoRaster.getShortPixels();
				dos.write(ByteUtils.shortsToBytes(shortPixels));
				break;
			case ImagePlus.GRAY32:
				float[] floatPixels = geoRaster.getFloatPixels();
				dos.write(ByteUtils.floatsToBytes(floatPixels));
				break;
			case ImagePlus.COLOR_RGB:
				int[] intPixels = geoRaster.getIntPixels();
				dos.write(ByteUtils.intsToBytes(intPixels));
				break;
			}

			return bos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block. What happens if the raster
			// doesn't exist
			throw new RuntimeException(e);
		}
	}

	@Override
	public GeoRaster getAsRaster() throws IncompatibleTypesException {
		return geoRaster;
	}

	public static Value readBytes(byte[] buffer) {
		ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
		DataInputStream dis = new DataInputStream(bis);
		try {
			// Read metadata
			double upperLeftX = dis.readDouble();
			double upperLeftY = dis.readDouble();
			int nRows = dis.readInt();
			int nCols = dis.readInt();
			float pixelWidth = dis.readFloat();
			float pixelHeight = dis.readFloat();
			double rotationX = dis.readDouble();
			double rotationY = dis.readDouble();
			float noDataValue = dis.readFloat();
			RasterMetadata metadata = new RasterMetadata(upperLeftX,
					upperLeftY, pixelWidth, pixelHeight, nCols, nRows,
					rotationX, rotationY, noDataValue);
			// Read cached values
			double min = dis.readDouble();
			double max = dis.readDouble();
			// Read image
			int imageType = dis.readInt();
			GeoRaster gr;
			switch (imageType) {
			case ImagePlus.COLOR_256:
			case ImagePlus.GRAY8:
				byte[] bytePixels = new byte[nRows * nCols];
				dis.read(bytePixels);
				gr = GeoRasterFactory.createGeoRaster(bytePixels, metadata,
						imageType, min, max);
				return new RasterValue(gr);
			case ImagePlus.GRAY16:
				byte[] shortPixels = new byte[2 * nRows * nCols];
				dis.read(shortPixels);
				gr = GeoRasterFactory.createGeoRaster(ByteUtils
						.bytesToShorts(shortPixels), metadata, imageType, min,
						max);
				return new RasterValue(gr);
			case ImagePlus.GRAY32:
				byte[] floatPixels = new byte[4 * nRows * nCols];
				dis.read(floatPixels);
				gr = GeoRasterFactory.createGeoRaster(ByteUtils
						.bytesToFloats(floatPixels), metadata, imageType, min,
						max);
				return new RasterValue(gr);
			case ImagePlus.COLOR_RGB:
				byte[] intPixels = new byte[4 * nRows * nCols];
				dis.read(intPixels);
				gr = GeoRasterFactory.createGeoRaster(ByteUtils
						.bytesToInts(intPixels), metadata, imageType, min, max);
				return new RasterValue(gr);
			default:
				throw new RuntimeException("Unrecognized image type: "
						+ imageType);
			}

		} catch (IOException e) {
			throw new RuntimeException("Bug! We access no file!!", e);
		}
	}
}
