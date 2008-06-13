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
/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2002-2006, Geotools Project Managment Committee (PMC)
 *    (C) 2002, Centre for Computational Geography
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.gdms.driver.shapefile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.gdms.data.WarningListener;
import org.gdms.driver.WriteBufferManager;

/**
 *
 * @author jamesm
 * @author Ian Schneider
 * @source $URL:
 *         http://svn.geotools.org/geotools/tags/2.3.1/plugin/shapefile/src/org/geotools/data/shapefile/shp/ShapefileHeader.java $
 */
public class ShapefileHeader {

	public static final int MAGIC = 9994;

	public static final int VERSION = 1000;

	private int fileCode = -1;

	private int fileLength = -1;

	private int version = -1;

	private ShapeType shapeType = ShapeType.UNDEFINED;

	private double minX;

	private double maxX;

	private double minY;

	private double maxY;

	private void checkMagic(WarningListener warningListener) {
		if (fileCode != MAGIC) {
			warningListener.throwWarning("Wrong magic number, expected "
					+ MAGIC + ", got " + fileCode);
		}
	}

	private void checkVersion(WarningListener warningListener) {
		if (version != VERSION) {
			warningListener.throwWarning("Wrong version, expected " + MAGIC
					+ ", got " + version);
		}
	}

	public void read(ByteBuffer file, WarningListener warningListener)
			throws java.io.IOException {
		file.order(ByteOrder.BIG_ENDIAN);
		fileCode = file.getInt();

		checkMagic(warningListener);

		// skip 5 ints...
		file.position(file.position() + 20);

		fileLength = file.getInt();

		file.order(ByteOrder.LITTLE_ENDIAN);
		version = file.getInt();
		checkVersion(warningListener);
		shapeType = ShapeType.forID(file.getInt());

		minX = file.getDouble();
		minY = file.getDouble();
		maxX = file.getDouble();
		maxY = file.getDouble();

		// skip remaining unused bytes
		file.order(ByteOrder.BIG_ENDIAN);// well they may not be unused
		// forever...
		file.position(file.position() + 32);

	}

	public void write(WriteBufferManager shapeBuffer, ShapeType type,
			int numGeoms, int length, double minX, double minY, double maxX,
			double maxY) throws IOException {
		shapeBuffer.order(ByteOrder.BIG_ENDIAN);

		shapeBuffer.putInt(MAGIC);

		for (int i = 0; i < 5; i++) {
			shapeBuffer.putInt(0); // Skip unused part of header
		}

		shapeBuffer.putInt(length);

		shapeBuffer.order(ByteOrder.LITTLE_ENDIAN);

		shapeBuffer.putInt(VERSION);
		shapeBuffer.putInt(type.id);

		// write the bounding box
		shapeBuffer.putDouble(minX);
		shapeBuffer.putDouble(minY);
		shapeBuffer.putDouble(maxX);
		shapeBuffer.putDouble(maxY);

		// skip remaining unused bytes
		shapeBuffer.order(ByteOrder.BIG_ENDIAN);
		for (int i = 0; i < 8; i++) {
			shapeBuffer.putInt(0); // Skip unused part of header
		}
	}

	public ShapeType getShapeType() {
		return shapeType;
	}

	public int getVersion() {
		return version;
	}

	public int getFileLength() {
		return fileLength;
	}

	public double minX() {
		return minX;
	}

	public double minY() {
		return minY;
	}

	public double maxX() {
		return maxX;
	}

	public double maxY() {
		return maxY;
	}

	public String toString() {
		String res = new String("ShapeFileHeader[ size " + fileLength
				+ " version " + version + " shapeType " + shapeType
				+ " bounds " + minX + "," + minY + "," + maxX + "," + maxY
				+ " ]");
		return res;
	}
}
