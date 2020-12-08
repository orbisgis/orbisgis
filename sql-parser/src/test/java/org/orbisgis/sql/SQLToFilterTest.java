/*
 * Bundle sql-parser is part of the OrbisGIS platform
 *
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
 * sql-parser is distributed under LGPL 3 license.
 *
 * Copyright (C) 2020 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * sql-parser is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * sql-parser is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * sql-parser. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.sql;

import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.junit.jupiter.api.Test;
import org.opengis.filter.Filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 *
 * @author Erwan Bocher, CNRS, 2020
 */
public class SQLToFilterTest {

    @Test
    public void selectFilterTobeSupported()  {
        SQLToFilter sqlToFilter = new SQLToFilter();
        String expression = "lastEarthQuake BEFORE 2006-11-30T01:30:00Z";
        try {
            sqlToFilter.parse(expression);
        }catch (RuntimeException ex){
            System.out.println("--------\nExpression : \n"+ expression + "\n must be supported in the future");
        }
    }

    @Test
    public void convertToFilter()  {
        SQLToFilter sqlToFilter = new SQLToFilter();
        String expression = "the_geom = 10";
        assertEquals("[ the_geom = 10 ]", sqlToFilter.parse(expression).toString());
        expression = "the_geom = 'orbisgis'";
        assertEquals("[ the_geom = orbisgis ]", sqlToFilter.parse(expression).toString());
        expression = "AREA(the_geom) = 'orbisgis'";
        assertEquals("[ Area([the_geom]) = orbisgis ]", sqlToFilter.parse(expression).toString());
        expression = "AREA(geomLength(the_geom)) = 'orbisgis'";
        assertEquals("[ Area([geomLength([the_geom])]) = orbisgis ]", sqlToFilter.parse(expression).toString());
        expression = "AREA(the_geom) = geomLength(the_geom)";
        assertEquals("[ Area([the_geom]) = geomLength([the_geom]) ]", sqlToFilter.parse(expression).toString());
        expression = "AREA(geomLength(the_geom)) = geomLength(the_geom)";
        assertEquals("[ Area([geomLength([the_geom])]) = geomLength([the_geom]) ]", sqlToFilter.parse(expression).toString());
        expression = "the_geom = 10 and top = 'first'";
        assertEquals("[[ the_geom = 10 ] AND [ top = first ]]", sqlToFilter.parse(expression).toString());
        expression = "AREA(geomLength(the_geom)) = 12 or geomLength(the_geom)<20 and type = 'super'";
        assertEquals("[[ Area([geomLength([the_geom])]) = 12 ] OR [[ geomLength([the_geom]) < 20 ] AND [ type = super ]]]", sqlToFilter.parse(expression).toString());
        expression = "(AREA(geomLength(the_geom)) = 12 or geomLength(the_geom)<20) and type = 'super'";
        assertEquals("[[[ Area([geomLength([the_geom])]) = 12 ] OR [ geomLength([the_geom]) < 20 ]] AND [ type = super ]]", sqlToFilter.parse(expression).toString());
        expression = "type is not null";
        assertEquals("[ NOT [ type IS NULL ] ]", sqlToFilter.parse(expression).toString());
        expression = "type is null";
        assertEquals("[ type IS NULL ]", sqlToFilter.parse(expression).toString());
    }

    @Test
    public void compareFilterTest() throws CQLException {
        Filter fromsql = SQLToFilter.transform("gid=1237");
        Filter filter = ECQL.toFilter("gid=1237");
        assertEquals(filter, fromsql);
        fromsql = SQLToFilter.transform("st_area(the_geom)>100");
        filter = ECQL.toFilter("area(the_geom)>100");
        assertEquals(filter, fromsql);
    }
}
