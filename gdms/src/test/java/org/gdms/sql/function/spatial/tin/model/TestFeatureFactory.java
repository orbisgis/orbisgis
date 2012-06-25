/*
 * TANATO  is a library dedicated to the modelling of water pathways based on 
 * triangulate irregular network. TANATO takes into account anthropogenic and 
 * natural artifacts to evaluate their impacts on the watershed response. 
 * It ables to compute watershed, main slope directions and water flow pathways.
 * 
 * This library has been originally created  by Erwan Bocher during his thesis 
 * “Impacts des activités humaines sur le parcours des écoulements de surface dans 
 * un bassin versant bocager : essai de modélisation spatiale. Application au 
 * Bassin versant du Jaudy-Guindy-Bizien (France)”. It has been funded by the 
 * Bassin versant du Jaudy-Guindy-Bizien and Syndicat d’Eau du Trégor.
 * 
 * The new version is developed at French IRSTV institut as part of the 
 * AvuPur project, funded by the French Agence Nationale de la Recherche 
 * (ANR) under contract ANR-07-VULN-01.
 * 
 * TANATO is distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Jean-Yves MARTIN
 * Copyright (C) 2011 Erwan BOCHER, , Alexis GUEGANNO, Jean-Yves MARTIN
 * 
 * TANATO is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * TANATO is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * TANATO. If not, see <http://www.gnu.org/licenses/>.
 * 
 * For more information, please consult: <http://trac.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.gdms.sql.function.spatial.tin.model;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import org.gdms.sql.FunctionTest;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.geometries.DEdge;
import org.jdelaunay.delaunay.geometries.DPoint;
import org.jdelaunay.delaunay.geometries.DTriangle;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test the methods taht are in TINFeatureFactory
 * @author alexis
 */
public class TestFeatureFactory extends FunctionTest {
        
        @Test
        public void testTriangleCreation() throws DelaunayError{
                GeometryFactory gf = new GeometryFactory();
                Geometry geom = gf.createPolygon(gf.createLinearRing(new Coordinate[]{
                        new Coordinate(0,0,1),
                        new Coordinate(1,1,3),
                        new Coordinate(5,3,1),
                        new Coordinate(0,0,1)
                }), new LinearRing[]{});
                DTriangle dt = TINFeatureFactory.createDTriangle(geom);
                assertTrue(dt.equals(new DTriangle(
                        new DEdge(0,0,1,1,1,3), 
                        new DEdge(1,1,3,5,3,1), 
                        new DEdge(5,3,1,0,0,1))));
        }
        
        @Test
        public void testTriangleException() throws DelaunayError {
                GeometryFactory gf = new GeometryFactory();
                Geometry geom = gf.createPolygon(gf.createLinearRing(new Coordinate[]{
                        new Coordinate(0,0,1),
                        new Coordinate(1,1,3),
                        new Coordinate(5,3,1),
                        new Coordinate(5,8,1),
                        new Coordinate(0,0,1)
                }), new LinearRing[]{});
                try {
                        DTriangle dt = TINFeatureFactory.createDTriangle(geom);
                        assertTrue(false);
                } catch (IllegalArgumentException e) {
                        assertTrue(true);
                }
                geom = gf.createLineString(new Coordinate[]{
                        new Coordinate(0,0,0),
                        new Coordinate(5,5,2)
                });
                try {
                        DTriangle dt = TINFeatureFactory.createDTriangle(geom);
                        assertTrue(false);
                } catch (IllegalArgumentException e) {
                        assertTrue(true);
                }
        }
        
        @Test
        public void testDEdgeCreation() throws DelaunayError {
                GeometryFactory gf = new GeometryFactory();
                Geometry geom = gf.createLineString(new Coordinate[]{
                        new Coordinate(0,0,0),
                        new Coordinate(5,5,2)
                });
                DEdge ed = TINFeatureFactory.createDEdge(geom);
                assertTrue(ed.equals(new DEdge(0,0,0,5,5,2)));
        }
        
        @Test
        public void testDEdgeException() throws DelaunayError {
                GeometryFactory gf = new GeometryFactory();
                Geometry geom = gf.createPolygon(gf.createLinearRing(new Coordinate[]{
                        new Coordinate(0,0,1),
                        new Coordinate(1,1,3),
                        new Coordinate(5,3,1),
                        new Coordinate(5,8,1),
                        new Coordinate(0,0,1)
                }), new LinearRing[]{});
                try {
                        DEdge ed = TINFeatureFactory.createDEdge(geom);
                        assertTrue(false);
                } catch (Exception e) {
                        assertTrue(true);
                }
                geom = gf.createPoint(new Coordinate(0,0,85));
                try {
                        DEdge ed = TINFeatureFactory.createDEdge(geom);
                        assertTrue(false);
                } catch (Exception e) {
                        assertTrue(true);
                }
        }
        
        @Test
        public void testDPointCreation() throws DelaunayError {
                GeometryFactory gf = new GeometryFactory();
                Geometry geom = gf.createPoint(new Coordinate(0,0,85));
                DPoint ed = TINFeatureFactory.createDPoint(geom);
                assertTrue(ed.equals(new DPoint(0,0,85)));
                ed = TINFeatureFactory.createDPoint(new Coordinate(5,5,85));
                assertTrue(ed.equals(new DPoint(5,5,85)));
        }
        
        @Test
        public void testDPointException() throws DelaunayError {
                GeometryFactory gf = new GeometryFactory();
                Geometry geom = gf.createPolygon(gf.createLinearRing(new Coordinate[]{
                        new Coordinate(0,0,1),
                        new Coordinate(1,1,3),
                        new Coordinate(5,3,1),
                        new Coordinate(5,8,1),
                        new Coordinate(0,0,1)
                }), new LinearRing[]{});
                try {
                        DPoint ed = TINFeatureFactory.createDPoint(geom);
                        assertTrue(false);
                } catch (Exception e) {
                        assertTrue(true);
                }
        }
}
