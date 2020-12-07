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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Erwan Bocher, CNRS, 2020
 */
public class SQLParserTest {

    @Test
    public void forTests()  {
        SQLParser sqlParser = new SQLParser();
        String expression = "Math.Abs(12)";
        sqlParser.toExpression(expression);
    }

    @Test
    public void convertToExpression()  {
        SQLParser sqlParser = new SQLParser();
        String expression = "AREA(the_geom)";
        assertEquals("Area([the_geom])", sqlParser.toExpression(expression).toString());
        expression = "1 + 2";
        assertEquals("(1+2)", sqlParser.toExpression(expression).toString());
        expression = "1 + 2 * 3";
        assertEquals("(1+(2*3))", sqlParser.toExpression(expression).toString());
        expression = "(1 + 2) * 3";
        assertEquals("((1+2)*3)", sqlParser.toExpression(expression).toString());
        expression = "AREA(the_geom)+(1 - GEOMLENGTH(AREA))/12";
        assertEquals("(Area([the_geom])+((1-geomLength([AREA]))/12))", sqlParser.toExpression(expression).toString());
        expression = "the_geom = 'orbisgis'";
        assertNull( sqlParser.toExpression(expression));
        expression = "AREA(the_geom), the_geom";
        assertNull( sqlParser.toExpression(expression));
        expression = "AREA(the_geom) as area";
        assertNull( sqlParser.toExpression(expression));
        expression = "CASE WHEN AREA(the_geom) > 0 THEN 'OK' ELSE 'NO' END";
        assertNull( sqlParser.toExpression(expression));
    }


    @Test
    public void convertToFilter()  {
        SQLParser sqlParser = new SQLParser();
        String expression = "the_geom = 10";
        assertEquals("equalTo([the_geom], [10])", sqlParser.toFilter(expression).toString());
        expression = "the_geom = 'orbisgis'";
        assertEquals("equalTo([the_geom], [orbisgis])", sqlParser.toFilter(expression).toString());
        expression = "'the_geom' = 'orbisgis'";
        assertEquals("equalTo([the_geom], [orbisgis])", sqlParser.toFilter(expression).toString());
        expression = "AREA(the_geom) = 'orbisgis'";
        assertEquals("equalTo([Area([the_geom])], [orbisgis])", sqlParser.toFilter(expression).toString());
        expression = "AREA(geomLength(the_geom)) = 'orbisgis'";
        assertEquals("equalTo([Area([geomLength([the_geom])])], [orbisgis])", sqlParser.toFilter(expression).toString());
        expression = "AREA(the_geom) = geomLength(the_geom)";
        assertEquals("equalTo([Area([the_geom])], [geomLength([the_geom])])", sqlParser.toFilter(expression).toString());
        expression = "AREA(geomLength(the_geom)) = geomLength(the_geom)";
        assertEquals("equalTo([Area([geomLength([the_geom])])], [geomLength([the_geom])])", sqlParser.toFilter(expression).toString());
        expression = "the_geom = 10 and top = 'first'";
        assertEquals("And([equalTo([the_geom], [10])], [equalTo([top], [first])])", sqlParser.toFilter(expression).toString());
        expression = "AREA(geomLength(the_geom)) = 12 or geomLength(the_geom)<20 and type = 'super'";
        assertEquals("Or([equalTo([Area([geomLength([the_geom])])], [12])], [And([20], [equalTo([type], [super])])])", sqlParser.toFilter(expression).toString());
        expression = "(AREA(geomLength(the_geom)) = 12 or geomLength(the_geom)<20) and type = 'super'";
        assertEquals("And([Or([equalTo([Area([geomLength([the_geom])])], [12])], [20])], [equalTo([type], [super])])", sqlParser.toFilter(expression).toString());
    }

    @Test
    public void convertWithSFFunctions() {


    }

    }
