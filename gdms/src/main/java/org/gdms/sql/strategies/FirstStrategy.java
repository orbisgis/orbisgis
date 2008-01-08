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
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.source.SourceManager;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.instruction.Adapter;
import org.gdms.sql.instruction.CreateAdapter;
import org.gdms.sql.instruction.EvaluationException;
import org.gdms.sql.instruction.Expression;
import org.gdms.sql.instruction.IncompatibleTypesException;
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
	private static Logger logger = Logger.getLogger(FirstStrategy.class
			.getName());

	public static boolean indexes = true;

	/**
	 * @see org.gdms.sql.strategies.Strategy#select(org.gdms.sql.parser.ASTSQLSelectCols)
	 */
	public DataSource select(String name, SelectAdapter instr)
			throws ExecutionException {
		try {
			logger.info("executing select");

			CustomQuery customQuery = instr.getCustomQuery();
			if (customQuery != null) {
				return executeCustomQuery(name, instr, customQuery);
			} else if (instr.getGroupByFieldNames().length > 0) {
				return executeGroupBy(instr, instr.getGroupByFieldNames());
			} else {

				AbstractSecondaryDataSource ret = null;
				DataSource[] fromTables = instr.getTables();
				Expression whereExpression = instr.getWhereExpression();
				instr.getInstructionContext().setFromTables(fromTables);

				logger.info("Using indexes: " + indexes);
				if (indexes) {
					DynamicLoop loop = new DynamicLoop(fromTables,
							whereExpression, instr.getInstructionContext());
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
						ret = executeAggregatedSelect(instr
								.getInstructionContext(), fields,
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

			}
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

	private DataSource executeGroupBy(SelectAdapter instr,
			String[] groupByFields) throws DriverLoadException,
			NoSuchTableException, DataSourceCreationException, DriverException,
			SyntaxException, ExecutionException, IncompatibleTypesException {

		// sort
		String sorted = "select * " + getFrom(instr.getTables()) + " order by "
				+ getCommaSeparated(groupByFields);
		DataSourceFactory dsf = instr.getInstructionContext().getDSFactory();
		DataSource sortedDS = dsf.executeSQL(sorted);

		// Split
		sortedDS.open();
		ArrayList<String> splited = new ArrayList<String>();
		int[] groupByFieldIndexes = new int[groupByFields.length];
		for (int i = 0; i < groupByFieldIndexes.length; i++) {
			groupByFieldIndexes[i] = sortedDS
					.getFieldIndexByName(groupByFields[i]);
		}
		ValueCollection groupByValues = getValues(0, groupByFieldIndexes,
				sortedDS);
		ObjectMemoryDriver omd = new ObjectMemoryDriver(sortedDS.getMetadata());
		SourceManager sourceManager = dsf.getSourceManager();
		for (int i = 0; i < sortedDS.getRowCount(); i++) {
			Value[] row = sortedDS.getRow(i);
			ValueCollection rowValues = getValues(i, groupByFieldIndexes,
					sortedDS);
			if (rowValues.equals(groupByValues).getAsBoolean()) {
				omd.addValues(row);
			} else {
				splited.add(sourceManager.nameAndRegister(omd));
				omd = new ObjectMemoryDriver(sortedDS.getMetadata());
				omd.addValues(row);
				groupByValues = rowValues;
			}
		}
		splited.add(sourceManager.nameAndRegister(omd));

		sortedDS.cancel();

		// Create aggregate. No need to create identity aggregated function?
		String sql = instr.getWithoutOrderByAndThisFrom("from "
				+ splited.get(0));
		DataSource ds = dsf.executeSQL(sql);
		ds.open();
		omd = new ObjectMemoryDriver(ds.getMetadata());
		ds.cancel();
		for (String group : splited) {
			sql = instr.getWithoutOrderByAndThisFrom("from " + group);
			ds = dsf.executeSQL(sql);
			ds.open();
			for (int i = 0; i < ds.getRowCount(); i++) {
				omd.addValues(ds.getRow(i));
			}
			ds.cancel();
		}

		return dsf.getDataSource(omd);
	}

	private String getCommaSeparated(String[] items) {
		String ret = "";
		String separator = "";
		for (String item : items) {
			ret += separator + item;
			separator = ", ";
		}

		return ret;
	}

	private ValueCollection getValues(int row, int[] fieldIds, DataSource ds)
			throws DriverException {
		Value[] ret = new Value[fieldIds.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = ds.getFieldValue(row, fieldIds[i]);
		}

		return ValueFactory.createValue(ret);
	}

	private DataSource executeCustomQuery(String name, SelectAdapter instr,
			CustomQuery query) throws DriverLoadException, ExecutionException,
			NoSuchTableException, DataSourceCreationException,
			EvaluationException {
		DataSourceFactory dsf = instr.getInstructionContext().getDSFactory();

		CustomQuery customQuery = instr.getCustomQuery();

		DataSource[] tables = instr.getTables();
		if (instr.getWhereExpression() != null) {
			String tableList = getFrom(tables);
			String sql = "select *  " + tableList + " " + instr.getWhereSQL();
			tables = new DataSource[] { dsf.executeSQL(sql) };
		}

		return customQuery.evaluate(dsf, tables, instr.getCustomQueryArgs());
	}

	private String getFrom(DataSource[] tables) {
		String tableList = "from ";
		String separator = "";
		for (DataSource dataSource : tables) {
			tableList += separator + dataSource.getName();
			separator = ", ";
		}
		return tableList;
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
	public DataSource union(String name, UnionAdapter instr)
			throws ExecutionException {
		try {
			UnionDataSourceDecorator ret = new UnionDataSourceDecorator(instr
					.getFirstTable(), instr.getSecondTable());
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

	@Override
	public void create(CreateAdapter instr) throws ExecutionException {
		Adapter[] childs = instr.getChilds();
		DataSourceFactory dsf = instr.getInstructionContext().getDSFactory();
		Adapter adapter = childs[0];
		DataSource ds = dsf.getDataSource((SelectAdapter) adapter,
				DataSourceFactory.NORMAL);
		try {
			dsf.saveContents(instr.getTableName(), ds);
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

}
