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
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
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
package org.gdms.sql.indexes;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Implementaci�n del conjunto de �ndices que guarda en memoria hasta un l�mite
 * y a partir de ese l�mite pasa todos los �ndices a disco
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class IndexSetImpl implements VariableIndexSet {
	private VariableIndexSet set;

	/**
	 * Creates a new IndexSetImpl object.
	 */
	public IndexSetImpl() {
		set = new VariableMemoryIndexSet();
	}

	/**
	 * A�ade un �ndice al conjunto
	 *
	 * @param index
	 *            �ndice a a�adir
	 *
	 * @throws IOException
	 *             Si se produce un error al escribir en el disco
	 * @throws RuntimeException
	 */
	public synchronized void addIndex(long index) throws IOException {
		set.addIndex(index);
	}

	/**
	 * Devuelve el �ndice nth-�simo si se invoc� previamente a indexSetComplete
	 * y lanza una excepci�n en caso contrario
	 *
	 * @param nth
	 *            �ndice de �ndice que se quiere obtener
	 *
	 * @return indice nth-�simo
	 *
	 * @throws IOException
	 *             Si se produce un error accediendo a disco
	 * @throws RuntimeException
	 */
	public long getIndex(long nth) throws IOException {
		return set.getIndex(nth);
	}

	/**
	 * Devuelve el n�mero de �ndices si se invoc� previamente a indexSetComplete
	 * y lanza una excepci�n en caso contrario
	 *
	 * @return n�mero de �ndices
	 *
	 * @throws RuntimeException
	 */
	public long getIndexCount() {
		return set.getIndexCount();
	}

	/**
	 * @see org.gdms.sql.indexes.VariableIndexSet#open(java.io.File)
	 */
	public void open() throws IOException {
		set.open();
	}

	/**
	 * @see org.gdms.sql.indexes.VariableIndexSet#close()
	 */
	public void close() throws IOException {
		set.close();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws IOException
	 *
	 * @see org.gdms.sql.indexes.VariableIndexSet#getIndexes()
	 */
	public ArrayList<Long> getIndexes() throws IOException {
		return set.getIndexes();
	}
}
