/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.coremap.renderer.se.parameter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.orbisgis.coremap.renderer.se.parameter.real.*;
import org.orbisgis.coremap.renderer.se.parameter.string.StringAttribute;

/**
 *
 * @author Alexis Guéganno
 */


public class UsedAnalysisTest {

    @Test
    public void testInitialization(){
        UsedAnalysis ua = new UsedAnalysis();
        assertFalse(ua.isCategorizeUsed());
        assertFalse(ua.isRecodeUsed());
        assertFalse(ua.isInterpolateUsed());
    }

    @Test
    public void testCategorize(){
        UsedAnalysis ua = new UsedAnalysis();
        Categorize2Real c2c = new Categorize2Real(new RealLiteral(), new RealLiteral(), new RealAttribute("youhou"));
        Categorize2Real c2cb = new Categorize2Real(new RealLiteral(), new RealLiteral(), new RealAttribute("youhou"));
        ua.include(c2c);
        assertTrue(ua.isCategorizeUsed());
        ua.include(c2cb);
        assertTrue(ua.isCategorizeUsed());
        assertFalse(ua.isRecodeUsed());
        assertFalse(ua.isInterpolateUsed());
        assertTrue(ua.getAnalysis().size()==2);
    }

    @Test
    public void testRecode(){
        UsedAnalysis ua = new UsedAnalysis();
        Recode2Real c2c = new Recode2Real(new RealLiteral(), new StringAttribute("youhou"));
        Recode2Real c2cb = new Recode2Real(new RealLiteral(), new StringAttribute("youhou"));
        ua.include(c2c);
        assertTrue(ua.isRecodeUsed());
        ua.include(c2cb);
        assertTrue(ua.isRecodeUsed());
        assertFalse(ua.isCategorizeUsed());
        assertFalse(ua.isInterpolateUsed());
        assertTrue(ua.getAnalysis().size()==2);
    }

    @Test
    public void testInterpolate(){
        UsedAnalysis ua = new UsedAnalysis();
        Interpolate2Real c2c = new Interpolate2Real(new RealLiteral());
        Interpolate2Real c2cb = new Interpolate2Real(new RealLiteral());
        ua.include(c2c);
        assertTrue(ua.isInterpolateUsed());
        ua.include(c2cb);
        assertTrue(ua.isInterpolateUsed());
        assertFalse(ua.isCategorizeUsed());
        assertFalse(ua.isRecodeUsed());
        assertTrue(ua.getAnalysis().size()==2);
    }
}
