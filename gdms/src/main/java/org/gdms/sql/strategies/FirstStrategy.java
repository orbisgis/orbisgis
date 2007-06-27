package org.gdms.sql.strategies;

import java.io.IOException;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.instruction.CustomAdapter;
import org.gdms.sql.instruction.EvaluationException;
import org.gdms.sql.instruction.Expression;
import org.gdms.sql.instruction.InstructionContext;
import org.gdms.sql.instruction.SelectAdapter;
import org.gdms.sql.instruction.SemanticException;
import org.gdms.sql.instruction.UnionAdapter;

import com.hardcode.driverManager.DriverLoadException;

/**
 * Strategy de pruebas, en la que los metodos tienen la caracteristica de que
 * son los mas faciles de implementar en el momento en que fueron necesarios
 *
 * @author Fernando Gonzalez Cortes
 */
public class FirstStrategy extends Strategy {
	public static boolean indexes;

	/**
	 * @see org.gdms.sql.strategies.Strategy#select(org.gdbms.parser.ASTSQLSelectCols)
	 */
	public DataSource select(SelectAdapter instr) throws ExecutionException {
		try {

			AbstractSecondaryDataSource ret = null;

			DataSource[] fromTables = instr.getTables();

			Expression whereExpression = instr.getWhereExpression();

			instr.getInstructionContext().setFromTables(fromTables);

			if (false) {

				DynamicLoop loop = new DynamicLoop(fromTables, whereExpression,
						instr.getInstructionContext());
				ret = loop.processNestedLoop();
			} else {
				ret = new PDataSourceDecorator(fromTables);
			}

			instr.getInstructionContext().scalarProductDone();

			instr.getInstructionContext().setDs(ret);

			Expression[] fields = instr.getFieldsExpression();

			if (fields != null) {
				if (fields[0].isAggregated()) {
					ret = executeAggregatedSelect(
							instr.getInstructionContext(), fields,
							whereExpression, ret);

					ret.setSQL(instr.getInstructionContext().getSql());

					return ret;
				}

				ret.open();

				AbstractSecondaryDataSource res = new ProjectionDataSourceDecorator(
						ret, fields, instr.getFieldsAlias());
				ret.cancel();

				ret = res;
			}

			if (whereExpression != null) {
				ret.open();
				FilteredDataSourceDecorator dataSource = new FilteredDataSourceDecorator(
						ret, whereExpression);
				dataSource.filtrar(instr.getInstructionContext());
				ret.cancel();
				ret = dataSource;
			}

			if (instr.isDistinct()) {
				ret.open();

				DistinctDataSourceDecorator dataSource = new DistinctDataSourceDecorator(
						ret, instr.getFieldsExpression());
				dataSource.filter(instr.getInstructionContext());
				ret.cancel();

				ret = dataSource;
			}

			int orderFieldCount = instr.getOrderCriterionCount();
			if (orderFieldCount > 0) {
				ret.open();
				String[] fieldNames = new String[orderFieldCount];
				int[] types = new int[orderFieldCount];
				for (int i = 0; i < types.length; i++) {
					fieldNames[i] = instr.getFieldName(i);
					types[i] = instr.getOrder(i);
				}
				OrderedDataSourceDecorator dataSource = new OrderedDataSourceDecorator(
						ret, fieldNames, types);
				dataSource.order();
				ret.cancel();

				ret = dataSource;
			}

			ret.setSQL(instr.getInstructionContext().getSql());

			return ret;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (EvaluationException e) {
			throw new ExecutionException(e);
		} catch (IOException e) {
			throw new ExecutionException(e);
		} catch (SemanticException e) {
			throw new ExecutionException(e);
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (NoSuchTableException e) {
			throw new ExecutionException(e);
		} catch (DataSourceCreationException e) {
			throw new ExecutionException(e);
		}
	}

	/**
	 * @param expression
	 * @param fields
	 * @throws DriverException
	 * @throws EvaluationException
	 * @throws SemanticException
	 * @throws IOException
	 *
	 */
	private AbstractSecondaryDataSource executeAggregatedSelect(
			InstructionContext ic, Expression[] fields,
			Expression whereExpression, DataSource ds) throws DriverException,
			IOException, SemanticException, EvaluationException {
		Value[] aggregateds = new Value[fields.length];
		if (whereExpression != null) {
			ds.open();

			FilteredDataSourceDecorator dataSource = new FilteredDataSourceDecorator(
					ds, whereExpression);
			aggregateds = dataSource.aggregatedFilter(ic, fields);
			ds.cancel();

		} else {
			ds.open();
			for (int i = 0; i < fields.length; i++) {
				int[] index = new int[1];
				ic.setNestedForIndexes(index);
				for (index[0] = 0; index[0] < ds.getRowCount(); index[0]++) {
					aggregateds[i] = fields[i].evaluate();
				}
			}
			ds.cancel();
		}

		return new AggregateDataSourceDecorator(aggregateds);
	}

	/**
	 * @see org.gdms.sql.strategies.Strategy#union(String,
	 *      org.gdms.sql.instruction.UnionInstruction)
	 */
	public DataSource union(UnionAdapter instr) throws ExecutionException {
		try {
			return new UnionDataSourceDecorator(instr.getFirstTable(), instr
					.getSecondTable());
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (NoSuchTableException e) {
			throw new ExecutionException(e);
		} catch (DataSourceCreationException e) {
			throw new ExecutionException(e);
		} catch (ExecutionException e) {
			throw new ExecutionException(e);
		}
	}

	/**
	 * @param dsf
	 * @throws DriverException
	 * @throws NoSuchTableException
	 * @throws DriverLoadException
	 * @see org.gdms.sql.strategies.Strategy#custom(String,
	 *      org.gdms.sql.instruction.CustomAdapter)
	 */
	public DataSource custom(CustomAdapter instr, DataSourceFactory dsf) throws ExecutionException {
		CustomQuery query = QueryManager.getQuery(instr.getQueryName());

		if (query == null) {
			throw new RuntimeException("No such custom query");
		}

		try {
			return query.evaluate(dsf, instr.getTables(DataSourceFactory.STATUS_CHECK), instr.getValues());
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (NoSuchTableException e) {
			throw new ExecutionException(e);
		} catch (DataSourceCreationException e) {
			throw new ExecutionException(e);
		} catch (EvaluationException e) {
			throw new ExecutionException(e);
		} catch (SemanticException e) {
			throw new ExecutionException(e);
		}
	}

}
