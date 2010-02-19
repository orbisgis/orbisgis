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

import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.orbisgis.progress.IProgressMonitor;

public class DropIndexOperator extends AbstractExpressionOperator implements
		Operator {

	private String tableName;
	private String fieldName;
	private DataSourceFactory dsf;

	public DropIndexOperator(DataSourceFactory dsf, String tableName,
			String fieldName) {
		this.tableName = tableName;
		this.fieldName = fieldName;
		this.dsf = dsf;
	}

	public ObjectDriver getResultContents(IProgressMonitor pm)
			throws ExecutionException {
		try {
			IndexManager im = dsf.getIndexManager();
			im.deleteIndex(tableName, fieldName);
			return null;
		} catch (NoSuchTableException e) {
			throw new ExecutionException("Cannot remove index:" + tableName, e);
		} catch (IndexException e) {
			throw new ExecutionException("Cannot remove index:" + tableName, e);
		}
	}

	public Metadata getResultMetadata() throws DriverException {
		return null;
	}

	@Override
	protected Expression[] getExpressions() throws DriverException,
			SemanticException {
		return new Expression[] { new Field(fieldName) };
	}

}
