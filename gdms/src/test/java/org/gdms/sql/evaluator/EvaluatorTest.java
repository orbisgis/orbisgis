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
package org.gdms.sql.evaluator;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.memory.ObjectMemoryDriver;

public class EvaluatorTest extends TestCase {

	private DataSourceFactory dsf;
	private DataSource ds1;
	private DataSource dsMemory;

	@Override
	protected void setUp() throws Exception {
		dsf = new DataSourceFactory();
		dsf.setTempDir("src/test/resources/backup");
		ds1 = dsf.getDataSource(new File("src/test/resources/evaluator.csv"));
		ds1.open();

		ObjectMemoryDriver omd = new ObjectMemoryDriver(
				new String[] { "field" }, new Type[] { TypeFactory
						.createType(Type.DOUBLE) });
		omd.addValues(new Value[] { ValueFactory.createValue(4.7) });
		dsMemory = dsf.getDataSource(omd);
		dsMemory.open();
	}

	@Override
	protected void tearDown() throws Exception {
		ds1.close();
		dsMemory.close();
	}

	public void testFilter() throws Exception {
		Field p = new Field("name");
		Literal l = new Literal(ValueFactory.createValue("b"));
		GreaterThanOrEqual gte = new GreaterThanOrEqual(p, l);
		DataSource filtered = Evaluator.filter(ds1, gte);
		filtered.open();
		System.out.println(filtered.getAsString());
		for (int i = 0; i < filtered.getRowCount(); i++) {
			assertTrue(filtered.getString(i, "name").compareTo("b") >= 0);
		}
		filtered.close();
	}

	public void testFilterDoubles() throws Exception {
		Field p = new Field("field");
		Literal l = new Literal(ValueFactory.createValue("2"));
		GreaterThanOrEqual gte = new GreaterThanOrEqual(p, l);
		DataSource filtered = Evaluator.filter(dsMemory, gte);
		filtered.open();
		assertTrue(filtered.getRowCount() == 1);
		filtered.close();

	}

	public void testFilterString() throws Exception {
		Field p = new Field("name");
		Literal l = new Literal(ValueFactory.createValue("1"));
		GreaterThanOrEqual gte = new GreaterThanOrEqual(p, l);
		DataSource filtered = Evaluator.filter(ds1, gte);
		filtered.open();
		System.out.println(filtered.getAsString());
		for (int i = 0; i < filtered.getRowCount(); i++) {
			assertTrue(filtered.getString(i, "name").compareTo("1") >= 0);
		}
		filtered.close();
	}

	public void testReplaceOr() throws Exception {
		Literal l1 = new Literal(ValueFactory.createValue(true));
		Literal l2 = new Literal(ValueFactory.createValue(false));
		Literal l3 = new Literal(ValueFactory.createValue(false));

		Not exp2 = new Not(l1);
		Or exp0 = new Or(l1, new And(l3, l2));
		Expression exp = new And(new And(exp0, l3), new And(exp2, l2));

		// Expression transformedExpression = exp.changeOrForNotAnd();
		// assertTrue(exp.evaluate().getAsBoolean() == transformedExpression
		// .evaluate().getAsBoolean());
		// assertTrue(noOr(transformedExpression));

		Expression[] ands = exp.splitAnds();
		assertTrue(ands.length == 4);
		assertTrue(equalStructure(ands[0], exp0));
		assertTrue(equalStructure(ands[1], l3));
		assertTrue(equalStructure(ands[2], exp2));
		assertTrue(equalStructure(ands[3], l2));
	}

	private boolean equalStructure(Expression exp0, Expression exp1) {
		if (exp0.getClass().equals(exp1.getClass())) {
			if (exp0.getChildCount() != exp1.getChildCount()) {
				return false;
			} else {
				for (int i = 0; i < exp0.getChildCount(); i++) {
					if (!equalStructure(exp0.getChild(i), exp1.getChild(i))) {
						return false;
					}
				}
				return true;
			}
		} else {
			return false;
		}
	}

}
