/*
 * UrbSAT is a set of spatial functionalities to build morphological
 * and aerodynamic urban indicators. It has been developed on
 * top of GDMS and OrbisGIS. UrbSAT is distributed under GPL 3
 * license. It is produced by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of UrbSAT.
 *
 * UrbSAT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UrbSAT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UrbSAT. If not, see <http://www.gnu.org/licenses/>.
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
package org.gdms.sql.function.spatial;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.spatial.extract.ToMultiSegments;
import org.gdms.sql.strategies.IncompatibleTypesException;



public class ExtractTest extends FunctionTest {
	
	

	
	public final void testToMultiSegments() throws Exception {
		
		//Test null input
		ToMultiSegments function = new ToMultiSegments();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(g2));
		assertTrue(res.getType() == Type.GEOMETRY);		
		System.out.println(res.getAsGeometry());
		assertTrue(res.getAsGeometry().getNumGeometries()==3);

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(4), ValueFactory.createValue(4), ValueFactory
					.createValue(4));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(3), ValueFactory.createValue(3));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(""), ValueFactory.createValue(""));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
		try {
			res = evaluate(function, ValueFactory.createValue(""), ValueFactory
					.createValue(3), ValueFactory.createValue(""));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
		
	}
}