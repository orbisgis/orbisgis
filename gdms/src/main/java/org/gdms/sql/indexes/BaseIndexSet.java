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
/*
 * Created on 23-oct-2004
 */
package org.gdms.sql.indexes;

import java.io.IOException;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public interface BaseIndexSet {
	/**
	 * Cierra el fichero de �ndices
	 * 
	 * @throws IOException
	 *             Si se produce un fallo al cerrar
	 */
	public void close() throws IOException;

	/**
	 * Devuelve el �ndice nth-�simo si se invoc� previamente a indexSetComplete
	 * y lanza una excepci�n en caso contrario
	 * 
	 * @param nth
	 *            �ndice del �ndice que se quiere obtener
	 * 
	 * @return indice nth-�simo
	 * 
	 * @throws IOException
	 *             Si se produce un fallo al recuperar el �ndice
	 */
	public long getIndex(long nth) throws IOException;

	/**
	 * Devuelve el n�mero de �ndices si se invoc� previamente a indexSetComplete
	 * y lanza una excepci�n en caso contrario
	 * 
	 * @return Si se produce un fallo al obtener el n�mero de �ndices
	 */
	public long getIndexCount();
}
