/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.coremap.renderer.se.parameter.real;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Maxence Laurent
 */
public class RealFunctionTest {

    @Test
    public void testDiv() throws Exception {
            RealFunction op1 = new RealFunction("div");
            op1.addOperand(new RealLiteral(100));
            op1.addOperand(new RealLiteral(10));
            assertEquals(op1.getValue(null, -1), 10.0, 0.0000001);
    }

    @Test
    public void testSqrt() throws Exception {
            RealFunction op1 = new RealFunction("sqrt");
            op1.addOperand(new RealLiteral(25.0));

            assertEquals(5.0, op1.getValue(null, -1), 0.0000001);
            RealFunction op2 = new RealFunction("sqrt");
            op2.addOperand(new RealLiteral(-25.0));

            assertEquals(Double.NaN, op2.getValue(null, -1), 0.0000001);

    }

    @Test
    public void testLog() throws Exception {
            RealFunction op1 = new RealFunction("log");
            op1.addOperand(new RealLiteral(100));
            assertEquals(2.0, op1.getValue(null, -1), 0.0000001);

            RealFunction op2 = new RealFunction("log");
            op2.addOperand(new RealLiteral(-100));
            assertEquals(Double.NaN, op2.getValue(null, -1), 0.0000001);
    }

    @Test
    public void testGetOperator() throws Exception {
            RealFunction op1 = new RealFunction("log");
            assertTrue(op1.getOperator() == RealFunction.Operators.LOG);
            assertTrue(op1.getOperands().isEmpty());
    }

    @Test
    public void testGetOperatorBis() throws Exception {
            RealFunction op1 = new RealFunction(RealFunction.Operators.ADD);
            assertTrue(op1.getOperator() == RealFunction.Operators.ADD);
            assertTrue(op1.getOperands().isEmpty());
    }

    @Test
    public void testgetChildren() throws Exception {
            RealFunction op1 = new RealFunction("div");
            op1.addOperand(new RealLiteral(100));
            op1.addOperand(new RealLiteral(10));
            assertEquals(op1.getChildren().size(),2);
    }
}
