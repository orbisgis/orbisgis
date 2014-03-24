/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.renderer.se.parameter;

import org.junit.Test;
import org.orbisgis.core.AbstractTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author alexis
 */
public class UtilTest extends AbstractTest {

    @Test
    public void testDashArrayValidation1() throws Exception {
        assertTrue(ParameterUtil.validateDashArray(""));
    }

    @Test
    public void testDashArrayValidation2() throws Exception {
        assertTrue(ParameterUtil.validateDashArray("1"));
    }

    @Test
    public void testDashArrayValidation3() throws Exception {
        assertTrue(ParameterUtil.validateDashArray("1 3 5 4 6 91.5222"));
    }

    @Test
    public void testDashArrayValidation4() throws Exception {
        assertFalse(ParameterUtil.validateDashArray("bonjour"));
    }

    @Test
    public void testDashArrayValidation5() throws Exception {
        assertFalse(ParameterUtil.validateDashArray("52 2 3 6 bonjour"));
    }

    @Test
    public void testDashArrayValidation6() throws Exception {
        assertFalse(ParameterUtil.validateDashArray("-1"));
    }
}
