package org.gdms.sql.strategies;

import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.edition.OriginalDirection;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.EvaluationException;
import org.gdms.sql.instruction.Expression;
import org.gdms.sql.instruction.IndexHint;
import org.gdms.sql.instruction.InstructionContext;

public class DynamicLoop {

	private DataSource[] fromTables;

	private IndexHint[] hints;

	private FilteredProductDataSourceDecorator result;

	private int[] loopIndexes;

	private int nestingLevel;

	private Expression whereExpression;

	private InstructionContext ic;

	public DynamicLoop(DataSource[] forTables, Expression whereExpression,
			IndexHint[] hints, InstructionContext ic) {
		super();
		this.fromTables = forTables;
		this.whereExpression = whereExpression;
		this.hints = hints;
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
		Iterator<PhysicalDirection> it = whereExpression.filter(source);

		if (it == null) {
			it = new FullIterator(source);
		}

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
