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
package org.gdms.sql.strategies;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.edition.OriginalDirection;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.EvaluationException;
import org.gdms.sql.instruction.Expression;
import org.gdms.sql.instruction.InstructionContext;

public class DynamicLoop {
	private static Logger logger = Logger.getLogger(DynamicLoop.class.getName());

	private DataSource[] fromTables;

	private FilteredProductDataSourceDecorator result;

	private int[] loopIndexes;

	private int nestingLevel;

	private Expression whereExpression;

	private InstructionContext ic;

	public DynamicLoop(DataSource[] forTables, Expression whereExpression,
			InstructionContext ic) {
		super();
		this.fromTables = forTables;
		this.whereExpression = whereExpression;
		this.ic = ic;

		sortTables();
	}

	private void sortTables() {
		// ArrayList<DataSource> ordered = new ArrayList<DataSource>();
		// ArrayList<IndexHint> pendingHints = new ArrayList<IndexHint>();
		// for (int i = 0; i < array.length; i++) {
		//
		// }
	}

	public AbstractSecondaryDataSource processNestedLoop()
			throws DriverException, EvaluationException {
		result = new FilteredProductDataSourceDecorator(fromTables);

		loopIndexes = new int[fromTables.length];
		for (int i = 0; i < loopIndexes.length; i++) {
			// we want to crash if some uninitialized value is called
			loopIndexes[i] = -1;
		}
		ic.setNestedForIndexes(loopIndexes);

		nestingLevel = 0;

		for (int i = 0; i < fromTables.length; i++) {
			fromTables[i].open();
		}

		nextNestedLoop(fromTables[0]);

		for (int i = 0; i < fromTables.length; i++) {
			fromTables[i].cancel();
		}

		return result;
	}

	private void nextNestedLoop(DataSource source) throws DriverException,
			EvaluationException {
		// Gets an iterator of the DataSource taking into account the indexes
		Iterator<PhysicalDirection> it = null;
		if (whereExpression != null) {
			it = whereExpression.filter(source);
		}

		if (it == null) {
			it = new FullIterator(source);
		}
		logger.info("Nesting level: " + nestingLevel);
		logger.info("Iterator type: " + it.getClass().getName());

		while (it.hasNext()) {
			PhysicalDirection dir = it.next();
			loopIndexes[nestingLevel] = ((OriginalDirection) dir).getRowIndex();

			if (loopIndexes.length - 1 == nestingLevel) {
				result.addRow(loopIndexes);
			} else {
				nestingLevel++;
				nextNestedLoop(fromTables[nestingLevel]);
				nestingLevel--;
			}
		}
	}

}
