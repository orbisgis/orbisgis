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
package org.gdms.sql.strategies;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.sql.instruction.CreateAdapter;
import org.gdms.sql.instruction.CustomAdapter;
import org.gdms.sql.instruction.SelectAdapter;
import org.gdms.sql.instruction.UnionAdapter;

/**
 * Interfaz que define las operaciones que se pueden realizar con los
 * DataSource. Las distintas implementaciones de esta interfaz ser�n las
 * encargadas del uso de los indices, del algoritmo usado para cada operaci�n,
 * ...
 */
public abstract class Strategy {
	/**
	 * Realiza una select a partir de la instrucci�n que se pasa como par�metro
	 *
	 * @param instr
	 *            Objeto con la informaci�n sobre las tablas que entran en juego
	 *            en la instrucci�n, campos, expresiones condicionales, ...
	 *
	 * @return DataSource con el resultado de la instruccion
	 *
	 * @throws ExecutionException
	 *             The query failed
	 */
	public DataSource select(SelectAdapter instr) throws ExecutionException {
		throw new RuntimeException(
				"This strategy does not support select execution");
	}

	/**
	 * Realiza una union a partir de la instrucci�n que se pasa como par�metro
	 *
	 * @param instr
	 *            Objeto con la informaci�n sobre las tablas que entran en juego
	 *            en la instrucci�n
	 *
	 * @return DataSource con el resultado de la instruccion
	 *
	 * @throws ExecutionException
	 *             The query failed
	 */
	public DataSource union(UnionAdapter instr) throws ExecutionException {
		throw new RuntimeException(
				"This strategy does not support union execution");
	}

	/**
	 * Makes a custom query
	 *
	 * @param instr
	 *            The instruction specifying the custom query
	 *
	 * @return The result DataSource
	 *
	 * @throws ExecutionException
	 *             The query failed
	 */
	public DataSource custom(CustomAdapter instr, DataSourceFactory dsf)
			throws ExecutionException {
		throw new RuntimeException(
				"This strategy does not support custom queries execution");
	}

	public void create(CreateAdapter instr) throws ExecutionException {
		throw new RuntimeException(
				"This strategy does not support create execution");
	}
}
