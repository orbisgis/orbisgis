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
