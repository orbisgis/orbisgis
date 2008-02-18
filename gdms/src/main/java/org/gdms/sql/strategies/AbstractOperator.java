package org.gdms.sql.strategies;

import java.util.ArrayList;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.orbisgis.IProgressMonitor;

public abstract class AbstractOperator implements Operator {

	protected ArrayList<Operator> children = new ArrayList<Operator>();
	private ObjectDriver result;
	private DataSourceFactory dsf;
	private boolean validated = false;

	public void setDataSourceFactory(DataSourceFactory dsf) {
		this.dsf = dsf;
		for (int i = 0; i < getOperatorCount(); i++) {
			getOperator(i).setDataSourceFactory(dsf);
		}
	}

	protected DataSourceFactory getDataSourceFactory() {
		return dsf;
	}

	public void addChild(Operator operator) {
		children.add(operator);
	}

	public String toString() {
		String ret = this.getClass().getSimpleName() + "(";
		for (int i = 0; i < children.size(); i++) {
			ret = ret + children.get(i);
		}
		return ret + ")";
	}

	public void addChilds(Operator[] childOperators) {
		for (Operator operator : childOperators) {
			addChild(operator);
		}
	}

	public Operator getOperator(int i) {
		return children.get(i);
	}

	public int getOperatorCount() {
		return children.size();
	}

	protected interface OperatorFilter {
		boolean accept(Operator op);
	}

	protected Operator[] getOperators(Operator op, OperatorFilter operatorFilter) {
		ArrayList<Operator> ret = new ArrayList<Operator>();

		if (operatorFilter.accept(op)) {
			ret.add(op);
		}

		for (int i = 0; i < op.getOperatorCount(); i++) {
			Operator childOperator = op.getOperator(i);
			Operator[] ops = getOperators(childOperator, operatorFilter);
			for (Operator operator : ops) {
				ret.add(operator);
			}
		}

		return ret.toArray(new Operator[0]);
	}

	public void validateFieldReferences() throws SemanticException,
			DriverException {
		for (int i = 0; i < getOperatorCount(); i++) {
			getOperator(i).validateFieldReferences();
		}
	}

	public void validateTableReferences() throws NoSuchTableException,
			SemanticException, DriverException {
		for (int i = 0; i < getOperatorCount(); i++) {
			getOperator(i).validateTableReferences();
		}
	}

	public void validateExpressionTypes() throws SemanticException,
			DriverException {
		for (int i = 0; i < getOperatorCount(); i++) {
			getOperator(i).validateExpressionTypes();
		}
	}

	public void validateFunctionReferences() throws DriverException,
			SemanticException {
		for (int i = 0; i < getOperatorCount(); i++) {
			getOperator(i).validateFunctionReferences();
		}
	}

	public String[] getReferencedTables() {
		ArrayList<String> ret = new ArrayList<String>();
		for (Operator child : children) {
			String[] tables = child.getReferencedTables();
			for (String table : tables) {
				ret.add(table);
			}
		}

		return ret.toArray(new String[0]);
	}

	public void prepareValidation() throws SemanticException, DriverException {
		for (int i = 0; i < getOperatorCount(); i++) {
			getOperator(i).prepareValidation();
		}
	}

	public ObjectDriver getResult(IProgressMonitor pm) throws ExecutionException {
		if (result == null) {
			result = getResultContents(pm);
		}
		return result;
	}

	protected abstract ObjectDriver getResultContents(IProgressMonitor pm)
			throws ExecutionException;

	protected Integer getFieldIndexByName(ObjectDriver source, String fieldName)
			throws DriverException, ExecutionException {
		for (int i = 0; i < source.getMetadata().getFieldCount(); i++) {
			if (fieldName.equals(source.getMetadata().getFieldName(i))) {
				return i;
			}
		}

		throw new ExecutionException("Field not found; " + fieldName);
	}

	public void operationFinished() throws ExecutionException {
	}

	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}
}
