/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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
 *
 * or contact directly:
 * info@orbisgis.org
 **/

package org.gdms.data;

import java.io.File;
import org.gdms.SourceTest;

public class FilterDataSourceDecoratorTest extends SourceTest {

    public void testFilterDecorator() throws Exception {
        dsf.getSourceManager().register("landcover2000",
				new File(internalData + "landcover2000.shp"));
        DataSource original = dsf.getDataSource("landcover2000");
        FilterDataSourceDecorator decorator = new FilterDataSourceDecorator(original);
        decorator.setFilter("type = 'cereals'");

        original.open();
        decorator.open();

        assertTrue(original.getFieldCount() == decorator.getFieldCount());

        for (int i = 0; i < original.getMetadata().getFieldCount(); i++) {
            assertTrue(original.getFieldName(i).equals(decorator.getFieldName(i)));
        }

        int cols = original.getFieldCount();

        for (int i = 0; i < decorator.getRowCount() && i < 10000; i++) {
            long o = decorator.getOriginalIndex(i);
            assertTrue(decorator.getFieldValue(i, decorator.getFieldIndexByName("type")).toString().equals("cereals"));
            for (int j = 0; j < cols; j++) {
                assertTrue(decorator.getFieldValue(i, j).doEquals(original.getFieldValue(o, j)));
            }
        }

        decorator.close();
        original.close();
    }

}
