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
 *    
 *    Metadata part from gearscape
 */

package org.gdms.sql.strategies;

import java.util.ArrayList;

import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.orbisgis.progress.ProgressMonitor;

public class UnionOperator extends AbstractOperator implements Operator {

	public UnionOperator(Operator op1, Operator op2) {
		this.addChild(op1);
		this.addChild(op2);
	}

	public ObjectDriver getResultContents(ProgressMonitor pm)
			throws ExecutionException {
		try {
			return new UnionDriver(getOperator(0).getResult(pm), getOperator(1)
					.getResult(pm), getResultMetadata());
		} catch (DriverException e) {
			throw new ExecutionException("Cannot obtain "
					+ "the metadata of the union", e);
		}
	}

	public Metadata getResultMetadata() throws DriverException {
		try {
			return getUnionMetadata();
		} catch (SemanticException e) {
			// Preprocessor should have catched this error
			throw new RuntimeException("bug", e);
		}
	}

	private DefaultMetadata getUnionMetadata() throws DriverException,
			SemanticException {
		DefaultMetadata dm = new DefaultMetadata();
		Metadata m1 = getOperator(0).getResultMetadata();
		Metadata m2 = getOperator(1).getResultMetadata();
		if (m1.getFieldCount() != m2.getFieldCount()) {
			throw new SemanticException("Cannot evaluate "
					+ "union on sources with different field count");
		}
		for (int i = 0; i < m1.getFieldCount(); i++) {
			Type t1 = m1.getFieldType(i);
			Type t2 = m2.getFieldType(i);
			int type1Code = t1.getTypeCode();
			int type2Code = t2.getTypeCode();
			int type = TypeFactory.getBroaderType(type1Code, type2Code);
			if (type == -1) {
				throw new SemanticException("Cannot evaluate union: {0}"
						+ "th field type does not match: {1}" + " and {2}"
						+ ". Left type: {3}" + ". Right type:{4}");
			}
			ArrayList<Constraint> constraints = new ArrayList<Constraint>();
			for (int j = 0; j < t1.getConstraints().length; j++) {
				Constraint c1 = t1.getConstraints()[j];
				Constraint c2 = t2.getConstraint(c1.getConstraintCode());
				if (c2 == null) {
					continue;
				} else if (c1.getConstraintValue().equals(
						c2.getConstraintValue())) {
					constraints.add(c1);
				}
			}
			dm.addField(m1.getFieldName(i), TypeFactory.createType(type,
					constraints.toArray(new Constraint[constraints.size()])));
		}
		return dm;
	}

	/**
	 * Checks that the metadata of both sources is identical
	 * 
	 * @see org.gdms.sql.strategies.AbstractOperator#validateExpressionTypes()
	 */
	@Override
	public void validateExpressionTypes() throws SemanticException,
			DriverException {
		getUnionMetadata();
		super.validateExpressionTypes();
	}

}
