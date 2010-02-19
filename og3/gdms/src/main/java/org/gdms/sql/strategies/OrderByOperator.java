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
package org.gdms.sql.strategies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.gdms.sql.evaluator.LessThan;
import org.orbisgis.progress.IProgressMonitor;

public class OrderByOperator extends AbstractExpressionOperator implements
		Operator, SelectionTransporter {

	private ArrayList<Field> fields = new ArrayList<Field>();
	private ArrayList<Boolean> orders = new ArrayList<Boolean>();

	@Override
	protected Expression[] getExpressions() throws DriverException,
			SemanticException {
		return fields.toArray(new Expression[0]);
	}

	public ObjectDriver getResultContents(IProgressMonitor pm)
			throws ExecutionException {
		try {
			ObjectDriver source = getOperator(0).getResult(pm);

			int rowCount = (int) source.getRowCount();
			Value[][] columnCache = new Value[rowCount][fields.size()];
			int[] fieldIndexes = new int[fields.size()];
			for (int i = 0; i < fieldIndexes.length; i++) {
				fieldIndexes[i] = getFieldIndexByName(source, fields.get(i)
						.getFieldName());
			}
			pm.startTask("Caching values");
			for (int i = 0; i < rowCount; i++) {
				if (i / 1000 == i / 1000.0) {
					if (pm.isCancelled()) {
						return null;
					} else {
						pm.progressTo(100 * i / rowCount);
					}
				}
				for (int field = 0; field < fields.size(); field++) {
					columnCache[i][field] = source.getFieldValue(i,
							fieldIndexes[field]);
				}
			}
			pm.endTask();

			TreeSet<Integer> set = new TreeSet<Integer>(new SortComparator(
					columnCache, orders));

			pm.startTask("Sorting values");
			for (int i = 0; i < source.getRowCount(); i++) {
				if (i / 1000 == i / 1000.0) {
					if (pm.isCancelled()) {
						return null;
					} else {
						pm.progressTo(100 * i / rowCount);
					}
				}
				set.add(new Integer(i));
			}
			pm.endTask();

			ArrayList<Integer> indexes = new ArrayList<Integer>();
			Iterator<Integer> it = set.iterator();
			while (it.hasNext()) {
				Integer integer = (Integer) it.next();
				indexes.add(integer);
			}
			ObjectDriver ret = new RowMappedDriver(source, indexes);
			return ret;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

	public Metadata getResultMetadata() throws DriverException {
		return getOperator(0).getResultMetadata();
	}

	public void addCriterium(Field field, boolean asc) {
		fields.add(field);
		orders.add(asc);
	}

	/**
	 * Checks that the field types used in the order operation are 'orderable'
	 *
	 * @see org.gdms.sql.strategies.AbstractExpressionOperator#validateExpressionTypes()
	 */
	@Override
	public void validateExpressionTypes() throws SemanticException,
			DriverException {
		for (Field field : fields) {
			LessThan lt = new LessThan(field, field);
			lt.validateExpressionTypes();
		}
	}

	@Override
	public void validateFieldReferences() throws SemanticException,
			DriverException {
		for (int i = 0; i < getOperatorCount(); i++) {
			getOperator(i).validateFieldReferences();
		}
		Field[] fieldReferences = getFieldReferences();
		for (Field field : fieldReferences) {
			int fieldIndex = getOperator(0).passFieldUp(field);
			if (fieldIndex == -1) {
				throw new SemanticException("Field not found: "
						+ field.toString());
			} else {
				field.setFieldIndex(fieldIndex);
			}
		}
	}

	public void transportSelection(SelectionOp op) {
		throw new UnsupportedOperationException("Nested "
				+ "instructions not yet allowed");
	}

}
