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
/*
 * Created on 16-oct-2004
 */
package org.gdms.sql.indexes;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Interfaz a implementar por los �ndices sobre las tablas. Esta interfaz se
 * utiliza al filtrar una tabla, en la que se a�aden indices a la tabla
 * secuencialmente. Una vez se invoca el m�todo indexSetComplete ya no se pueden
 * meter m�s �ndices
 *
 * @author Fernando Gonz�lez Cort�s
 */
public interface VariableIndexSet extends BaseIndexSet {

	/**
	 * A�ade un �ndice al conjunto de �ndices
	 *
	 * @param value
	 *            �ndice de la fila a la que apunta el �ndice que se quiere
	 *            a�adir
	 *
	 * @throws IOException
	 *             Si se produce un fallo al escribir el �ndice
	 */
	public void addIndex(long value) throws IOException;

	/**
	 * Abre el almacenamiento del �ndice para la escritura de los �ndices. En
	 * caso de un almacenamiento permanente se usar� un fichero temporal
	 *
	 * @throws IOException
	 *             Si se produce un fallo al abrir
	 */
	public void open() throws IOException;

	/**
	 * Obtiene los �ndices del conjunto de �ndices en un array
	 *
	 * @return long[]
	 *
	 * @throws IOException
	 */
	public ArrayList<Long> getIndexes() throws IOException;
}
