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

/**
 * DOCUMENT ME!
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public interface Index {
	/**
	 * Invocado cuando se va a comenzar una operaci�n de escritura con la
	 * estructura de datos
	 * 
	 * @throws IndexException
	 *             DOCUMENT ME!
	 */
	public void start() throws IndexException;

	/**
	 * Invocado cuando se termina la operaci�n de escritura con el �ndice
	 * 
	 * @throws IndexException
	 *             DOCUMENT ME!
	 */
	public void stop() throws IndexException;

	/**
	 * A�ade la posici�n de un valor al �ndice. Posiblemente ya haya una o
	 * varias posiciones para dicho valor tomando como funci�n de identidad el
	 * m�todo equals de Value. En dicho caso se deber�n mantener todas estas
	 * 
	 * @param v
	 *            Valor
	 * @param position
	 *            posici�n del Valor dentro del DataSource
	 * 
	 * @throws IndexException
	 */
	public void add(Object v, int position) throws IndexException;

	/**
	 * Obtiene un iterador para iterar sobre las posiciones sobre las que puede
	 * haber valores iguales al que se pasa como par�metro. No todas las
	 * posiciones se deben corresponder necesariamente con registros que
	 * contengan el valor buscado pero todas las posiciones de los registros que
	 * contengan value estar�n en las posiciones que se retornen.
	 * 
	 * @param v
	 *            Value
	 * 
	 * @return Objeto para iterar por las posiciones
	 * 
	 * @throws IndexException
	 */
	public PositionIterator getPositions(Object v) throws IndexException;
}
