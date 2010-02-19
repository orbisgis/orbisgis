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
package org.gdms.data;

import java.io.File;
import java.util.ArrayList;

import org.gdms.data.file.FileDataSourceAdapter;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.gdms.GdmsDriver;
import org.gdms.source.SourceManager;
import org.gdms.source.directory.DefinitionType;
import org.gdms.source.directory.SqlDefinitionType;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.DiskBufferDriver;
import org.gdms.sql.strategies.Instruction;
import org.gdms.sql.strategies.SQLProcessor;
import org.gdms.sql.strategies.SemanticException;
import org.orbisgis.progress.IProgressMonitor;

public class SQLSourceDefinition extends AbstractDataSourceDefinition implements
		DataSourceDefinition {

	private Instruction instruction;
	private String tempSQL;

	public SQLSourceDefinition(Instruction instruction) {
		this.instruction = instruction;
	}

	private SQLSourceDefinition(String sql) {
		this.tempSQL = sql;
	}

	public File execute(IProgressMonitor pm) throws DriverException,
			ExecutionException, SemanticException {
		getDataSourceFactory().fireInstructionExecuted(instruction.getSQL());
		ObjectDriver source = instruction.execute(pm);
		if (pm.isCancelled()) {
			return null;
		} else {
			if (source == null) {
				throw new IllegalArgumentException(
						"The query produces no result: " + instruction.getSQL());
			} else {
				File file = null;
				DataSourceFactory dsf = getDataSourceFactory();
				if (source instanceof DiskBufferDriver) {
					file = ((DiskBufferDriver) source).getFile();
				} else {
					file = new File(dsf.getTempFile("gdms"));
					DataSourceDefinition dsd = new FileSourceDefinition(file);
					String name = dsf.getSourceManager().nameAndRegister(dsd);
					dsf.saveContents(name, dsf.getDataSource(source,
							DataSourceFactory.NORMAL));
				}

				return file;
			}
		}
	}

	public DataSource createDataSource(String tableName, IProgressMonitor pm)
			throws DataSourceCreationException {
		try {
			File file = execute(pm);
			if (pm.isCancelled()) {
				return null;
			} else {
				return new FileDataSourceAdapter(getSource(tableName), file,
						new GdmsDriver(), false);
			}
		} catch (ExecutionException e) {
			throw new DataSourceCreationException(
					"Cannot instantiate the source", e);
		} catch (SemanticException e) {
			throw new DataSourceCreationException(
					"Cannot instantiate the source", e);
		} catch (DriverException e) {
			throw new DataSourceCreationException(
					"Cannot instantiate the source", e);
		}
	}

	public void createDataSource(DataSource contents, IProgressMonitor pm)
			throws DriverException {
		throw new DriverException("Read only source");
	}

	public DefinitionType getDefinition() {
		SqlDefinitionType ret = new SqlDefinitionType();
		ret.setSql(instruction.getSQL());

		return ret;
	}

	public static DataSourceDefinition createFromXML(DataSourceFactory dsf,
			SqlDefinitionType definitionType) throws DriverException {
		return new SQLSourceDefinition(definitionType.getSql());
	}

	@Override
	protected ReadOnlyDriver getDriverInstance() {
		return null;
	}

	@Override
	public ArrayList<String> getSourceDependencies() throws DriverException {
		try {
			ArrayList<String> ret = new ArrayList<String>();
			String[] sources = instruction.getReferencedSources();
			for (String source : sources) {
				ret.add(source);
			}

			return ret;
		} catch (ParseException e) {
			// Should never happen since we have already parsed it
			throw new RuntimeException("bug!", e);
		} catch (SemanticException e) {
			// Should never happen since we have already parsed it
			throw new RuntimeException("bug!", e);
		}
	}

	public ReadOnlyDriver getDriver() {
		return null;
	}

	public String getSQL() {
		return instruction.getSQL();
	}

	public int getType() {
		int type = SourceManager.SQL;
		try {
			Metadata metadata = instruction.getResultMetadata();
			for (int i = 0; i < metadata.getFieldCount(); i++) {
				int typeCode = metadata.getFieldType(i).getTypeCode();
				if (typeCode == Type.GEOMETRY) {
					type = type | SourceManager.VECTORIAL;
				} else if (typeCode == Type.RASTER) {
					type = type | SourceManager.RASTER;
				}
			}
		} catch (DriverException e) {
		}
		return type;
	}

	@Override
	public String getTypeName() {
		return "SQL";
	}

	public void initialize() throws DriverException {
		SQLProcessor ag = new SQLProcessor(getDataSourceFactory());
		try {
			instruction = ag.prepareInstruction(tempSQL);
		} catch (ParseException e) {
			throw new DriverException("Cannot " + "initialize source: "
					+ tempSQL, e);
		} catch (SemanticException e) {
			throw new DriverException("Cannot " + "initialize source: "
					+ tempSQL, e);
		}
	}

	@Override
	public boolean equals(DataSourceDefinition obj) {
		if (obj instanceof SQLSourceDefinition) {
			SQLSourceDefinition dsd = (SQLSourceDefinition) obj;
			return instruction.getSQL().equals(dsd.instruction.getSQL());
		} else {
			return false;
		}
	}
}
