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
package org.gdms.sql.indexes.hashMap;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class DiskPositionIterator implements PositionIterator {
	private FileChannel channel;

	private ByteBuffer buffer;

	private boolean next;

	/**
	 * Crea un nuevo DiskPositionIterator.
	 * 
	 * @param channel
	 *            DOCUMENT ME!
	 * @param positionCount
	 *            DOCUMENT ME!
	 * @param value
	 *            DOCUMENT ME!
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public DiskPositionIterator(FileChannel channel, int positionCount,
			Object value) throws IOException {
		this.buffer = ByteBuffer.allocate(DiskIndex.RECORD_SIZE);
		this.channel = channel;

		int pos = Math.abs(value.hashCode());
		pos = (pos % positionCount);
		channel.position(DiskIndex.byteNumber(pos));
		buffer.clear();
		channel.read(buffer);
		buffer.flip();

		if (buffer.getInt() == -1) {
			next = false;
		} else {
			next = true;
		}
	}

	/**
	 * @see org.gdms.sql.indexes.hashMap.PositionIterator#hasNext()
	 */
	public boolean hasNext() {
		return next;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws IOException
	 * @throws RuntimeException
	 *             DOCUMENT ME!
	 * 
	 * @see org.gdms.sql.indexes.hashMap.PositionIterator#next()
	 */
	public int next() throws IOException {
		if (!next) {
			throw new RuntimeException("No more results");
		}

		int returnValue = buffer.getInt(0);
		int nextPosition = buffer.getInt(4);

		if (nextPosition == -1) {
			next = false;
			buffer = null;
		} else {
			channel.position(DiskIndex.byteNumber(nextPosition));
			buffer.clear();
			channel.read(buffer);
			buffer.flip();
		}

		return returnValue;
	}
}
