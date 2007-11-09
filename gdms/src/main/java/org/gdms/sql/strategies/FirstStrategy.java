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

import java.io.IOException;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.instruction.Adapter;
import org.gdms.sql.instruction.CreateAdapter;
import org.gdms.sql.instruction.CustomAdapter;
import org.gdms.sql.instruction.EvaluationException;
import org.gdms.sql.instruction.Expression;
import org.gdms.sql.instruction.InstructionContext;
import org.gdms.sql.instruction.SelectAdapter;
import org.gdms.sql.instruction.SemanticException;
import org.gdms.sql.instruction.UnionAdapter;

/**
 * Strategy de pruebas, en la que los metodos tienen la caracteristica de que
 * son los mas faciles de implementar en el momento en que fueron necesarios
 *
 * @author Fernando Gonzalez Cortes
 */
public class FirstStrategy extends Strategy {
	private static Logger logger = Logger.getLogger(FirstStrategy.class.getName());

	public static boolean indexes = true;

	/**
	 * @see org.gdms.sql.strategies.Strategy#select(org.gdms.sql.parser.ASTSQLSelectCols)
	 */
	public DataSource select(String name, SelectAdapter instr) throws ExecutionException {
		try {
			logger.info("executing select");

			AbstractSecondaryDataSource ret = null;
			DataSource[] fromTables = instr.getTables();
			Expression whereExpression = instr.getWhereExpression();
			instr.getInstructionContext().setFromTables(fromTables);

			logger.info("Using indexes: " + indexes);
			if (indexes) {
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
				logger.info("filtering fields...");
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
				logger.info("filtering rows...");
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

			ret.setName(name);

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

		return new AggregateDataSourceDecorator(ds, aggregateds);
	}

	/**
	 * @see org.gdms.sql.strategies.Strategy#union(String,
	 *      org.gdms.sql.instruction.UnionInstruction)
	 */
	public DataSource union(String name, UnionAdapter instr) throws ExecutionException {
		try {
			UnionDataSourceDecorator ret = new UnionDataSourceDecorator(instr.getFirstTable(), instr
					.getSecondTable());
			ret.setName(name);
			return ret;
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
	public DataSource custom(String name, CustomAdapter instr, DataSourceFactory dsf)
			throws ExecutionException {
		CustomQuery query = QueryManager.getQuery(instr.getQueryName());

		if (query == null) {
			throw new RuntimeException("No such custom query");
		}

		try {
			return query.evaluate(dsf, instr
					.getTables(DataSourceFactory.STATUS_CHECK), instr
					.getValues());
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

	@Override
	public void create(CreateAdapter instr) throws ExecutionException {
		Adapter[] childs = instr.getChilds();
		if (childs[0] instanceof SelectAdapter) {
			DataSourceFactory dsf = instr.getInstructionContext().getDSFactory();
			Adapter adapter = childs[0];
			DataSource ds = dsf
					.getDataSource((SelectAdapter) adapter,
							DataSourceFactory.NORMAL);
			try {
				dsf.saveContents(instr.getTableName(), ds);
			} catch (DriverException e) {
				throw new ExecutionException(e);
			}
		} else if (childs[0] instanceof CustomAdapter) {
			DataSourceFactory dsf = instr.getInstructionContext().getDSFactory();
			DataSource ds = dsf
					.getDataSource((CustomAdapter) childs[0],
							DataSourceFactory.NORMAL);
			if (ds == null) {
				throw new ExecutionException("The call doesn't return a DataSource");
			}
			try {
				dsf.saveContents(instr.getTableName(), ds);
			} catch (DriverException e) {
				throw new ExecutionException(e);
			}
		}
	}

}
