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
package org.orbisgis.coremap.renderer.se.common;
import org.junit.Test;
import org.orbisgis.coremap.renderer.se.parameter.string.Recode2String;
import org.orbisgis.coremap.renderer.se.parameter.string.StringAttribute;
import org.orbisgis.coremap.renderer.se.parameter.string.StringLiteral;

import javax.media.jai.PlanarImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Alexis Guéganno
 */
public class VariableOnlineResourceTest {

    @Test
    public void testCacheEmptying() throws Exception {
        URL resource = VariableOnlineResourceTest.class.getResource("3.gif");
        StringLiteral lit = new StringLiteral(resource.toString());
        VariableOnlineResource vor = new VariableOnlineResource(lit);
        PlanarImage first = vor.getPlanarJAI(null);
        PlanarImage second = vor.getPlanarJAI(null);
        assertTrue(first == second);
        lit.setValue(VariableOnlineResourceTest.class.getResource("21.gif").toString());
        lit.setValue(VariableOnlineResourceTest.class.getResource("3.gif").toString());
        PlanarImage third = vor.getPlanarJAI(null);
        assertFalse(third == second);
    }

    @Test
    public void testCacheRecode() throws Exception {
        Map<String,Object> input = new HashMap<String,Object>();
        input.put("field", "s");
        URL resourceF = VariableOnlineResourceTest.class.getResource("3.gif");
        URL resourceD = VariableOnlineResourceTest.class.getResource("21.gif");
        StringLiteral fb = new StringLiteral(resourceF.toString());
        Recode2String rs = new Recode2String(fb,new StringAttribute("field"));
        rs.addMapItem("s",new StringLiteral(resourceD.toString()));
        VariableOnlineResource vor = new VariableOnlineResource(rs);
        PlanarImage first = vor.getPlanarJAI(input);
        PlanarImage second = vor.getPlanarJAI(input);
        assertTrue(first == second);
        rs.addMapItem("s",new StringLiteral(resourceF.toString()));
        rs.addMapItem("s",new StringLiteral(resourceD.toString()));
        PlanarImage third = vor.getPlanarJAI(input);
        assertFalse(third == second);
        second = vor.getPlanarJAI(input);
        fb.setValue(resourceD.toString());
        third = vor.getPlanarJAI(input);
        assertFalse(second == third);
    }
}
