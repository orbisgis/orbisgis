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

import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.filter.FilterFactoryImpl;
import org.geotools.filter.text.ecql.ECQL;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;

/**
 *
 * @author Erwan Bocher, CNRS, 2020
 */
public class SQLToExpressionTest {

    @Test
    public void selectExpressionTobeSupported()  {
        SQLToExpression sqlToExpression = new SQLToExpression();
        String expression = "CASE\n" +
                        "    WHEN type > 300 THEN 1\n" +
                        "    WHEN type = 30 THEN 2\n" +
                        "    ELSE 3\n" +
                        "END";
        try {
            sqlToExpression.parse(expression);
        }catch (RuntimeException ex){
            System.out.println("--------\nExpression : \n"+ expression + "\n must be supported in the future");
        }
    }

    @Test
    public void convertToExpression()  {
        SQLToExpression sqlToExpression = new SQLToExpression();
        String expression = "AREA(the_geom)";
        assertEquals("Area([the_geom])", sqlToExpression.parse(expression).toString());
        expression = "ST_AREA(the_geom)";
        assertEquals("Area([the_geom])", sqlToExpression.parse(expression).toString());
        expression = "1 + 2";
        assertEquals("(1+2)", sqlToExpression.parse(expression).toString());
        expression = "1 + 2 * 3";
        assertEquals("(1+(2*3))", sqlToExpression.parse(expression).toString());
        expression = "(1 + 2) * 3";
        assertEquals("((1+2)*3)", sqlToExpression.parse(expression).toString());
        expression = "AREA(the_geom)+(1 - GEOMLENGTH(AREA))/12";
        assertEquals("(Area([the_geom])+((1-geomLength([AREA]))/12))", sqlToExpression.parse(expression).toString());
        expression = "the_geom = 'orbisgis'";
        assertNull( sqlToExpression.parse(expression));
        expression = "AREA(the_geom), the_geom";
        assertNull( sqlToExpression.parse(expression));
        expression = "AREA(the_geom) as area";
        assertNull( sqlToExpression.parse(expression));
        expression = "CASE WHEN AREA(the_geom) > 0 THEN 'OK' ELSE 'NO' END";
        assertEquals("if_then_else([greaterThan([Area([the_geom])], [0])], [OK], [NO])", sqlToExpression.parse(expression).toString());
    }

    @Test
    public void evaluateExpression() throws Exception {
        FilterFactoryImpl ff = new FilterFactoryImpl();
        SimpleFeatureType type = DataUtilities.createType("testSchema", "name:String,gid:Integer,*geom:Geometry");
        WKTReader reader = new WKTReader();
        Geometry geom1 = reader.read("LINESTRING(0 0 0, 10 10 10)");
        Feature f = SimpleFeatureBuilder.build(type, new Object[] {"testFeature1", 627, geom1}, null);
        Function geom_area = ff.function("geomlength", ff.property("geom"));
        Function if_ = ff.function("greaterThan", geom_area, ff.literal(0));
        Function if_then_else = ff.function("if_then_else", if_, ff.literal("ok"), ff.literal("no"));
        assertEquals("ok", if_then_else.evaluate(f));
        String sqlExpression = "CASE WHEN geomlength(geom) > 0 THEN 'ok' ELSE 'no' END";
        Expression expression = SQLToExpression.transform(sqlExpression);
        assertEquals("ok", expression.evaluate(f));
        sqlExpression = "CASE WHEN name='testFeature1' THEN 'ok' ELSE 'no' END";
        expression = SQLToExpression.transform(sqlExpression);
        assertEquals("ok", expression.evaluate(f));
        sqlExpression = "CASE WHEN gid!=627 THEN 'ok' ELSE 'no' END";
        expression = SQLToExpression.transform(sqlExpression);
        assertEquals("no", expression.evaluate(f));
        sqlExpression = "CASE WHEN gid=627 THEN 'ok' ELSE 'no' END";
        expression = SQLToExpression.transform(sqlExpression);
        assertEquals("ok", expression.evaluate(f));
    }

}
