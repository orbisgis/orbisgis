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

import java.io.File;
import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.parser.ParseException;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.progress.NullProgressMonitor;

/**
 * Class that embeds an optimized instruction
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public class Instruction {
	private String sql;
	private Operator op;
	private DataSourceFactory dsf;
	private boolean doOpenClose;

	Instruction(DataSourceFactory dsf, Operator op, String sql,
			boolean doOpenClose) {
		this.op = op;
		this.doOpenClose = doOpenClose;
		this.sql = sql;
		this.dsf = dsf;
	}

	/**
	 * Executes the instruction and returns a source with the result of the
	 * query
	 *
	 * @param pm
	 *
	 * @return
	 * @throws ExecutionException
	 * @throws DriverException
	 * @throws SemanticException
	 */
	public ObjectDriver execute(IProgressMonitor pm) throws ExecutionException,
			SemanticException, DriverException {
		if (pm == null) {
			pm = new NullProgressMonitor();
		}

		if (doOpenClose) {
			op.initialize();
		}
		ObjectDriver ret = op.getResult(pm);
		if (doOpenClose) {
			op.operationFinished();
		}
		return ret;
	}

	/**
	 * Executes the instruction, registers the result and returns a DataSource
	 * to explore the result. The resulting DataSource cannot be commited
	 *
	 * @param pm
	 *
	 * @return The result of this instruction or null if the execution was
	 *         cancelled
	 *
	 * @throws ExecutionException
	 * @throws DataSourceCreationException
	 * @throws DriverException
	 * @throws SemanticException
	 */
	public DataSource getDataSource(IProgressMonitor pm)
			throws ExecutionException, DataSourceCreationException,
			SemanticException, DriverException {
		if (pm == null) {
			pm = new NullProgressMonitor();
		}
		ObjectDriver ret = execute(pm);
		if (pm.isCancelled()) {
			return null;
		} else {
			File file = dsf.getResultFile();
			DataSourceDefinition dsd = new FileSourceDefinition(file);
			String name = dsf.getSourceManager().nameAndRegister(dsd);
			dsf.saveContents(name, dsf.getDataSource(ret));

			try {
				return dsf.getDataSource(name);
			} catch (DriverLoadException e) {
				throw new RuntimeException("bug!", e);
			} catch (NoSuchTableException e) {
				throw new RuntimeException("bug!", e);
			}
		}
	}

	public String getSQL() {
		return sql;
	}

	public String[] getReferencedSources() throws ParseException,
			SemanticException, DriverException {
		return getReferencedTables(op);
	}

	private String[] getReferencedTables(Operator op) {
		ArrayList<String> ret = new ArrayList<String>();
		String[] tables = op.getReferencedTables();
		for (String table : tables) {
			ret.add(table);
		}
		for (int i = 0; i < op.getOperatorCount(); i++) {
			tables = getReferencedTables(op.getOperator(i));
			for (String table : tables) {
				ret.add(table);
			}
		}

		return ret.toArray(new String[0]);
	}

	public Metadata getResultMetadata() throws DriverException {
		return op.getResultMetadata();
	}

	public Operator getOperator() {
		return op;
	}

}
