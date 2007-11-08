package org.orbisgis.geoview.tools;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import junit.framework.TestCase;

import org.gdms.data.values.BooleanValue;
import org.orbisgis.tools.CannotChangeGeometryException;
import org.orbisgis.tools.Handler;
import org.orbisgis.tools.Primitive;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;


public class PrimitiveTest extends TestCase {

    private Geometry point;
    private Geometry multipoint;
    private Geometry line;
    private Geometry multiline;
    private Geometry polygon;
    private Geometry multipolygon;
//    private com.hardcode.gdbms.engine.spatial.GeometryFactory gf =
//        com.hardcode.gdbms.engine.spatial.GeometryFactory.instance;
//
//    public void testCorrectPolygonHandlers() throws Exception {
//        ClosedGeneralPath gp = new ClosedGeneralPath();
//        gp.moveTo(0, 0);
//        gp.lineTo(10, 10);
//        gp.lineTo(10, 0);
//        gp.lineTo(0, 0);
//        gp.closePath();
//        Geometry p1 = gf.createPolygon(gp);
//        gp = new ClosedGeneralPath();
//        gp.moveTo(50, 50);
//        gp.lineTo(100, 100);
//        gp.lineTo(100, 50);
//        gp.closePath();
//        Geometry p2 = gf.createPolygon(gp);
//        gp = new ClosedGeneralPath();
//        gp.moveTo(50, 50);
//        gp.lineTo(210, 210);
//        gp.lineTo(210, 20);
//        gp.lineTo(50, 50);
//        Geometry p3 = gf.createPolygon(gp);
//
//        Primitive p = new Primitive(gf.createMultiPolygon(new Geometry[]{p1, p2, p3}), -1);
//        Handler[] handlers = p.getHandlers();
//        assertTrue(handlers.length == 9);
//        assertTrue(handlers[0].getPoint().equals(new Point2D.Double(0, 0)));
//        assertTrue(handlers[1].getPoint().equals(new Point2D.Double(10, 10)));
//        assertTrue(handlers[2].getPoint().equals(new Point2D.Double(10, 0)));
//        assertTrue(handlers[3].getPoint().equals(new Point2D.Double(50, 50)));
//        assertTrue(handlers[4].getPoint().equals(new Point2D.Double(100, 100)));
//        assertTrue(handlers[5].getPoint().equals(new Point2D.Double(100, 50)));
//        assertTrue(handlers[6].getPoint().equals(new Point2D.Double(50, 50)));
//        assertTrue(handlers[7].getPoint().equals(new Point2D.Double(210, 210)));
//        assertTrue(handlers[8].getPoint().equals(new Point2D.Double(210, 20)));
//    }
//
//    public void testRemovePointHandler() {
//        Primitive p = new Primitive(point, -1);
//        Handler[] handlers = p.getHandlers();
//        assertTrue(handlers.length == 1);
//        try {
//            handlers[0].remove();
//            assertTrue(false);
//        } catch (CannotChangeGeometryException e) {
//            assertTrue(true);
//        }
//    }
//
//    public void testRemoveMultiPointHandler() throws Exception {
//        Primitive p = new Primitive(multipoint, -1);
//        Handler[] handlers = p.getHandlers();
//        assertTrue(handlers.length == 2);
//        boolean all = false;
//        try {
//            Geometry g = handlers[0].remove();
//            MultipointGeneralPath newgp = new MultipointGeneralPath();
//            newgp.moveTo(1, 1);
//            assertTrue(((BooleanValue) g.equals(gf.createMultiPoint(newgp))).getValue());
//            assertTrue(true);
//            all = true;
//            new Primitive(g, -1).getHandlers()[0].remove();
//            assertTrue(false);
//        } catch (CannotChangeGeometryException e) {
//            assertTrue(all);
//        }
//    }
//
//    public void testRemoveLineHandler() throws Exception {
//        Primitive p = new Primitive(line, -1);
//        Handler[] handlers = p.getHandlers();
//        assertTrue(handlers.length == 3);
//        boolean all = false;
//        try {
//            Geometry g = handlers[0].remove();
//            GeneralPath newgp = new GeneralPath();
//            newgp.moveTo(1, 1);
//            newgp.lineTo(2, 2);
//            assertTrue(((BooleanValue) g.equals(gf.createLineString(newgp))).getValue());
//            assertTrue(true);
//            all = true;
//            new Primitive(g, -1).getHandlers()[0].remove();
//            assertTrue(false);
//        } catch (CannotChangeGeometryException e) {
//            assertTrue(all);
//        }
//    }
//
//    public void testRemovePolygonHandler() throws Exception {
//        Primitive p = new Primitive(polygon, -1);
//        Handler[] handlers = p.getHandlers();
//        assertTrue(handlers.length == 4);
//        boolean all = false;
//        try {
//            Geometry g = handlers[0].remove();
//            ClosedGeneralPath newgp = new ClosedGeneralPath();
//            newgp.moveTo(1, 1);
//            newgp.lineTo(2, 2);
//            newgp.lineTo(3, 0);
//            newgp.closePath();
//            assertTrue(((BooleanValue) g.equals(gf.createPolygon(newgp))).getValue());
//
//            g = handlers[2].remove();
//            newgp = new ClosedGeneralPath();
//            newgp.moveTo(0, 0);
//            newgp.lineTo(1, 1);
//            newgp.lineTo(3, 0);
//            newgp.closePath();
//            assertTrue(((BooleanValue) g.equals(gf.createPolygon(newgp))).getValue());
//
//            assertTrue(true);
//            all = true;
//            new Primitive(g, -1).getHandlers()[0].remove();
//            assertTrue(false);
//        } catch (CannotChangeGeometryException e) {
//            assertTrue(true);
//            assertTrue(all);
//            try {
//                Geometry g = handlers[0].remove();
//                g = handlers[3].remove();
//                assertTrue(false);
//            } catch (CannotChangeGeometryException e1) {
//                assertTrue(true);
//            }
//        }
//    }
//
//    public void testRemoveMultiLineHandler() throws Exception {
//        Primitive p = new Primitive(multiline, -1);
//        Handler[] handlers = p.getHandlers();
//        assertTrue(handlers.length == 5);
//        boolean all = false;
//        try {
//            Geometry g;
//            GeneralPath newgp;
//            try{
//                g = handlers[0].remove();
//                assertTrue(false);
//            } catch (CannotChangeGeometryException e) {
//                assertTrue(true);
//            }
//
//            try{
//                g = handlers[1].remove();
//                assertTrue(false);
//            } catch (CannotChangeGeometryException e) {
//                assertTrue(true);
//            }
//
//            g = handlers[2].remove();
//            newgp = new GeneralPath();
//            newgp.setSingleGeometry(false);
//            newgp.moveTo(0, 0);
//            newgp.lineTo(1, 1);
//            newgp.moveTo(3, 0);
//            newgp.lineTo(4, 0);
//            assertTrue(((BooleanValue) g.equals(gf.createMultiLineString(GeneralPath.getParts(newgp.getPathIterator(null))))).getValue());
//
//            g = handlers[3].remove();
//            newgp = new GeneralPath();
//            newgp.setSingleGeometry(false);
//            newgp.moveTo(0, 0);
//            newgp.lineTo(1, 1);
//            newgp.moveTo(2, 2);
//            newgp.lineTo(4, 0);
//            assertTrue(((BooleanValue) g.equals(gf.createMultiLineString(GeneralPath.getParts(newgp.getPathIterator(null))))).getValue());
//
//            g = handlers[4].remove();
//            newgp = new GeneralPath();
//            newgp.setSingleGeometry(false);
//            newgp.moveTo(0, 0);
//            newgp.lineTo(1, 1);
//            newgp.moveTo(2, 2);
//            newgp.lineTo(3, 0);
//            assertTrue(((BooleanValue) g.equals(gf.createMultiLineString(GeneralPath.getParts(newgp.getPathIterator(null))))).getValue());
//
//
//            assertTrue(true);
//            all = true;
//            new Primitive(g, -1).getHandlers()[0].remove();
//            assertTrue(false);
//        } catch (CannotChangeGeometryException e) {
//            assertTrue(all);
//            assertTrue(true);
//        }
//
//    }
//
//    public void testRemoveMultiPolygonHandler() throws Exception {
//        Primitive p = new Primitive(multipolygon, -1);
//        Handler[] handlers = p.getHandlers();
//        assertTrue(handlers.length == 7);
//        boolean all = false;
//        try {
//            Geometry g, pol1, pol2;
//            ClosedGeneralPath newgp;
//            try{
//                g = handlers[0].remove();
//                assertTrue(false);
//            } catch (CannotChangeGeometryException e) {
//                assertTrue(true);
//            }
//
//            try{
//                g = handlers[1].remove();
//                assertTrue(false);
//            } catch (CannotChangeGeometryException e) {
//                assertTrue(true);
//            }
//
//            try{
//                g = handlers[2].remove();
//                assertTrue(false);
//            } catch (CannotChangeGeometryException e) {
//                assertTrue(true);
//            }
//
//            g = handlers[3].remove();
//            newgp = new ClosedGeneralPath();
//            newgp.moveTo(0, 0);
//            newgp.lineTo(1, 1);
//            newgp.lineTo(2, 0);
//            newgp.closePath();
//            pol1 = gf.createPolygon(newgp);
//            newgp = new ClosedGeneralPath();
//            newgp.moveTo(3, 3);
//            newgp.lineTo(3, 0);
//            newgp.lineTo(2, 0);
//            newgp.lineTo(3, 3);
//            pol2 = gf.createPolygon(newgp);
//            assertTrue(((BooleanValue) g.equals(gf.createMultiPolygon(
//                    new Geometry[]{pol1, pol2}))).getValue());
//
//            g = handlers[4].remove();
//            newgp = new ClosedGeneralPath();
//            newgp.setSingleGeometry(false);
//            newgp.moveTo(0, 0);
//            newgp.lineTo(1, 1);
//            newgp.lineTo(2, 0);
//            newgp.closePath();
//            pol1 = gf.createPolygon(newgp);
//            newgp = new ClosedGeneralPath();
//            newgp.moveTo(2, 2);
//            newgp.lineTo(3, 0);
//            newgp.lineTo(2, 0);
//            newgp.lineTo(2, 2);
//            pol2 = gf.createPolygon(newgp);
//            assertTrue(((BooleanValue) g.equals(gf.createMultiPolygon(
//                    new Geometry[]{pol1, pol2}))).getValue());
//
//            g = handlers[5].remove();
//            newgp = new ClosedGeneralPath();
//            newgp.setSingleGeometry(false);
//            newgp.moveTo(0, 0);
//            newgp.lineTo(1, 1);
//            newgp.lineTo(2, 0);
//            newgp.closePath();
//            pol1 = gf.createPolygon(newgp);
//            newgp = new ClosedGeneralPath();
//            newgp.moveTo(2, 2);
//            newgp.lineTo(3, 3);
//            newgp.lineTo(2, 0);
//            newgp.lineTo(2, 2);
//            pol2 = gf.createPolygon(newgp);
//            assertTrue(((BooleanValue) g.equals(gf.createMultiPolygon(
//                    new Geometry[]{pol1, pol2}))).getValue());
//
//            g = handlers[6].remove();
//            newgp = new ClosedGeneralPath();
//            newgp.setSingleGeometry(false);
//            newgp.moveTo(0, 0);
//            newgp.lineTo(1, 1);
//            newgp.lineTo(2, 0);
//            newgp.closePath();
//            pol1 = gf.createPolygon(newgp);
//            newgp = new ClosedGeneralPath();
//            newgp.moveTo(2, 2);
//            newgp.lineTo(3, 3);
//            newgp.lineTo(3, 0);
//            newgp.lineTo(2, 2);
//            pol2 = gf.createPolygon(newgp);
//            assertTrue(((BooleanValue) g.equals(gf.createMultiPolygon(
//                    new Geometry[]{pol1, pol2}))).getValue());
//
//
//            assertTrue(true);
//            all = true;
//            new Primitive(g, -1).getHandlers()[0].remove();
//            assertTrue(false);
//        } catch (CannotChangeGeometryException e) {
//            assertTrue(all);
//            assertTrue(true);
//        }
//
//    }
//
//    public void testPointSnapPoints() throws Exception {
//        Primitive p = new Primitive(point,
//                -1);
//        SnapPoint[] points = p.getSnapPoints();
//        assertTrue(points.length == 1);
//        assertTrue(points[0].getLocation().equals(new Point2D.Double(0, 0)));
//    }
//
//    public void testMultipointSnapPoints() throws Exception {
//        Primitive p = new Primitive(multipoint,
//                -1);
//        SnapPoint[] points = p.getSnapPoints();
//        assertTrue(points.length == 2);
//        assertTrue(points[0].getLocation().equals(new Point2D.Double(0, 0)));
//        assertTrue(points[1].getLocation().equals(new Point2D.Double(1, 1)));
//    }
//
//    public void testLineSnapPoints() throws Exception {
//        Primitive p = new Primitive(line,
//                -1);
//        SnapPoint[] points = p.getSnapPoints();
//        assertTrue(points.length == 5);
//        assertTrue(points[0].getLocation().equals(new Point2D.Double(0, 0)));
//        assertTrue(points[1].getLocation().equals(new Point2D.Double(0.5, 0.5)));
//        assertTrue(points[2].getLocation().equals(new Point2D.Double(1, 1)));
//        assertTrue(points[3].getLocation().equals(new Point2D.Double(1.5, 1.5)));
//        assertTrue(points[4].getLocation().equals(new Point2D.Double(2, 2)));
//    }
//
//    public void testMultilineSnapPoints() throws Exception {
//        Primitive p = new Primitive(multiline,
//                -1);
//        SnapPoint[] points = p.getSnapPoints();
//        assertTrue(points.length == 8);
//        assertTrue(points[0].getLocation().equals(new Point2D.Double(0, 0)));
//        assertTrue(points[1].getLocation().equals(new Point2D.Double(0.5, 0.5)));
//        assertTrue(points[2].getLocation().equals(new Point2D.Double(1, 1)));
//        assertTrue(points[3].getLocation().equals(new Point2D.Double(2, 2)));
//        assertTrue(points[4].getLocation().equals(new Point2D.Double(2.5, 1)));
//        assertTrue(points[5].getLocation().equals(new Point2D.Double(3, 0)));
//        assertTrue(points[6].getLocation().equals(new Point2D.Double(3.5, 0)));
//        assertTrue(points[7].getLocation().equals(new Point2D.Double(4, 0)));
//    }
//
//    public void testPolygonSnapPoints() throws Exception {
//        Primitive p = new Primitive(polygon,
//                -1);
//        SnapPoint[] points = p.getSnapPoints();
//        assertTrue(points.length == 8);
//        assertTrue(points[0].getLocation().equals(new Point2D.Double(0, 0)));
//        assertTrue(points[1].getLocation().equals(new Point2D.Double(0.5, 0.5)));
//        assertTrue(points[2].getLocation().equals(new Point2D.Double(1, 1)));
//        assertTrue(points[3].getLocation().equals(new Point2D.Double(1.5, 1.5)));
//        assertTrue(points[4].getLocation().equals(new Point2D.Double(2, 2)));
//        assertTrue(points[5].getLocation().equals(new Point2D.Double(2.5, 1)));
//        assertTrue(points[6].getLocation().equals(new Point2D.Double(3, 0)));
//        assertTrue(points[7].getLocation().equals(new Point2D.Double(1.5, 0)));
//    }
//
//    public void testMultiPolygonSnapPoints() throws Exception {
//        Primitive p = new Primitive(multipolygon,
//                -1);
//        SnapPoint[] points = p.getSnapPoints();
//        assertTrue(points.length == 14);
//        assertTrue(points[0].getLocation().equals(new Point2D.Double(0, 0)));
//        assertTrue(points[1].getLocation().equals(new Point2D.Double(0.5, 0.5)));
//        assertTrue(points[2].getLocation().equals(new Point2D.Double(1, 1)));
//        assertTrue(points[3].getLocation().equals(new Point2D.Double(1.5, 0.5)));
//        assertTrue(points[4].getLocation().equals(new Point2D.Double(2, 0)));
//        assertTrue(points[5].getLocation().equals(new Point2D.Double(1, 0)));
//        assertTrue(points[6].getLocation().equals(new Point2D.Double(2, 2)));
//        assertTrue(points[7].getLocation().equals(new Point2D.Double(2.5, 2.5)));
//        assertTrue(points[8].getLocation().equals(new Point2D.Double(3, 3)));
//        assertTrue(points[9].getLocation().equals(new Point2D.Double(3, 1.5)));
//        assertTrue(points[10].getLocation().equals(new Point2D.Double(3, 0)));
//        assertTrue(points[11].getLocation().equals(new Point2D.Double(2.5, 0)));
//        assertTrue(points[12].getLocation().equals(new Point2D.Double(2, 0)));
//        assertTrue(points[13].getLocation().equals(new Point2D.Double(2, 1)));
//    }
//
//    public void testPuteoSnapPoints() throws Exception {
//        GeneralPath gp = new GeneralPath();
//        gp.setSingleGeometry(false);
//        gp.moveTo(0, 0);
//        gp.moveTo(1, 1);
//        gp.lineTo(1, 0);
//        gp.moveTo(2, 2);
//        gp.moveTo(3, 3);
//        gp.lineTo(4, 0);
//        gp.lineTo(3, 0);
//        gp.lineTo(3, 3);
//        GeneralPath[] linestrings = GeneralPath.getParts(gp.getPathIterator(null));
//        Primitive p = new Primitive(gf.createMultiLineString(linestrings),
//                -1);
//        SnapPoint[] points = p.getSnapPoints();
//        assertTrue(points.length == 10);
//        assertTrue(points[0].getLocation().equals(new Point2D.Double(1, 1)));
//        assertTrue(points[1].getLocation().equals(new Point2D.Double(1, 0.5)));
//        assertTrue(points[2].getLocation().equals(new Point2D.Double(1, 0)));
//        assertTrue(points[3].getLocation().equals(new Point2D.Double(3, 3)));
//        assertTrue(points[4].getLocation().equals(new Point2D.Double(3.5, 1.5)));
//        assertTrue(points[5].getLocation().equals(new Point2D.Double(4, 0)));
//        assertTrue(points[6].getLocation().equals(new Point2D.Double(3.5, 0)));
//        assertTrue(points[7].getLocation().equals(new Point2D.Double(3, 0)));
//        assertTrue(points[8].getLocation().equals(new Point2D.Double(3, 1.5)));
//        assertTrue(points[9].getLocation().equals(new Point2D.Double(3, 3)));
//    }
//
//    public void testMovePointHandler() throws Exception {
//        Primitive p = new Primitive(point, -1);
//        Handler[] handlers = p.getHandlers();
//        assertTrue(handlers.length == 1);
//        Geometry g = handlers[0].moveTo(10, 10);
//        assertTrue(((BooleanValue) g.equals(gf.createPoint(10, 10))).getValue());
//    }
//
//    public void testMoveMultiPointHandler() throws Exception {
//        Primitive p = new Primitive(multipoint, -1);
//        Handler[] handlers = p.getHandlers();
//        assertTrue(handlers.length == 2);
//        Geometry g = handlers[0].moveTo(8, 3);
//        assertTrue(((BooleanValue) g.equals(gf.createMultiPoint(
//                new double[]{8, 1}, new double[]{3, 1}))).getValue());
//    }
//
//    public void testMoveLineHandler() throws Exception {
//        Primitive p = new Primitive(line, -1);
//        Handler[] handlers = p.getHandlers();
//        assertTrue(handlers.length == 3);
//        Geometry g = handlers[0].moveTo(10, 10);
//        GeneralPath newgp = new GeneralPath();
//        newgp.moveTo(10, 10);
//        newgp.lineTo(1, 1);
//        newgp.lineTo(2, 2);
//        assertTrue(((BooleanValue) g.equals(gf.createLineString(newgp))).getValue());
//    }
//
//    public void testMovePolygonHandler() throws Exception {
//        Primitive p = new Primitive(polygon, -1);
//        Handler[] handlers = p.getHandlers();
//        assertTrue(handlers.length == 4);
//
//        Geometry g = handlers[0].moveTo(-10, -10);
//        ClosedGeneralPath newgp = new ClosedGeneralPath();
//        newgp.moveTo(-10, -10);
//        newgp.lineTo(1, 1);
//        newgp.lineTo(2, 2);
//        newgp.lineTo(3, 0);
//        newgp.closePath();
//        assertTrue(((BooleanValue) g.equals(gf.createPolygon(newgp))).getValue());
//    }
//
//    public void testMoveMultiLineHandler() throws Exception {
//        Primitive p = new Primitive(multiline, -1);
//        Handler[] handlers = p.getHandlers();
//        assertTrue(handlers.length == 5);
//
//        Geometry g = handlers[4].moveTo(-10, -10);
//        GeneralPath newgp = new GeneralPath();
//        newgp.setSingleGeometry(false);
//        newgp.moveTo(0, 0);
//        newgp.lineTo(1, 1);
//        newgp.moveTo(2, 2);
//        newgp.lineTo(3, 0);
//        newgp.lineTo(-10, -10);
//        Geometry ml = gf.createMultiLineString(GeneralPath.getParts(newgp.getPathIterator(null)));
//        assertTrue(((BooleanValue) g.equals(ml)).getValue());
//    }
//
//    public void testMoveMultiPolygonHandler() throws Exception {
//        Primitive p = new Primitive(multipolygon, -1);
//        Handler[] handlers = p.getHandlers();
//        assertTrue(handlers.length == 7);
//
//        Geometry g = handlers[0].moveTo(-10, 0);
//        ClosedGeneralPath newgp = new ClosedGeneralPath();
//        newgp.moveTo(-10, 0);
//        newgp.lineTo(1, 1);
//        newgp.lineTo(2, 0);
//        newgp.closePath();
//        Geometry pol1 = gf.createPolygon(newgp);
//        newgp = new ClosedGeneralPath();
//        newgp.moveTo(2, 2);
//        newgp.lineTo(3, 3);
//        newgp.lineTo(3, 0);
//        newgp.lineTo(2, 0);
//        newgp.closePath();
//        Geometry pol2 = gf.createPolygon(newgp);
//        assertTrue(((BooleanValue) g.equals(gf.createMultiPolygon(
//                new Geometry[]{pol1, pol2}))).getValue());
//
//        g = handlers[3].moveTo(2, 3);
//        newgp = new ClosedGeneralPath();
//        newgp.moveTo(0, 0);
//        newgp.lineTo(1, 1);
//        newgp.lineTo(2, 0);
//        newgp.closePath();
//        pol1 = gf.createPolygon(newgp);
//        newgp = new ClosedGeneralPath();
//        newgp.moveTo(2, 3);
//        newgp.lineTo(3, 3);
//        newgp.lineTo(3, 0);
//        newgp.lineTo(2, 0);
//        newgp.closePath();
//        pol2 = gf.createPolygon(newgp);
//        assertTrue(((BooleanValue) g.equals(gf.createMultiPolygon(new Geometry[]{
//                pol1, pol2}))).getValue());
//    }
//
//    public void testFailedPolygonVertexMovements() throws Exception {
//        Primitive p = new Primitive(polygon, -1);
//        Handler[] handlers = p.getHandlers();
//        assertTrue(handlers.length == 4);
//
//        try{
//            handlers[3].moveTo(3, 3);
//            assertTrue(false);
//        } catch (CannotChangeGeometryException e) {
//            assertTrue(true);
//        }
//
//        try{
//            handlers[3].moveTo(1, 1);
//            assertTrue(false);
//        } catch (CannotChangeGeometryException e) {
//            assertTrue(true);
//        }
//
//        ClosedGeneralPath gp = new ClosedGeneralPath();
//        gp.moveTo(0, 0);
//        gp.lineTo(0, 1);
//        gp.lineTo(1, 1);
//        gp.lineTo(1, 0);
//        gp.closePath();
//
//        p = new Primitive(gf.createPolygon(gp), -1);
//        handlers = p.getHandlers();
//        assertTrue(handlers.length == 4);
//
//        try{
//            handlers[3].moveTo(-1, 0);
//            assertTrue(false);
//        } catch (CannotChangeGeometryException e) {
//            assertTrue(true);
//        }
//    }
//
//    public void testRemovePolygonVertexGettingInvalidPolygon() throws Exception {
//        ClosedGeneralPath gp = new ClosedGeneralPath();
//        gp.moveTo(0, 0);
//        gp.lineTo(0, 2);
//        gp.lineTo(3, 2);
//        gp.lineTo(3, 1);
//        gp.lineTo(1, 1);
//        gp.closePath();
//
//        Primitive p = new Primitive(gf.createPolygon(gp), -1);
//        Handler[] handlers = p.getHandlers();
//        assertTrue(handlers.length == 5);
//
//        try{
//            handlers[1].remove();
//            assertTrue(false);
//        } catch (CannotChangeGeometryException e) {
//            assertTrue(true);
//        }
//    }
//
//    public void testMoveAndRemoveHole() throws Exception {
//        ClosedGeneralPath gp = new ClosedGeneralPath();
//        gp.moveTo(0, 0);
//        gp.lineTo(0, 10);
//        gp.lineTo(10, 10);
//        gp.lineTo(10, 0);
//        gp.closePath();
//
//        ClosedGeneralPath gphole = new ClosedGeneralPath();
//        gphole.moveTo(2, 2);
//        gphole.lineTo(4, 2);
//        gphole.lineTo(4, 4);
//        gphole.lineTo(2, 4);
//        gphole.closePath();
//
//        Primitive p = new Primitive(gf.createPolygon(gp,
//                new ClosedGeneralPath[]{gphole}), -1);
//        Handler[] handlers = p.getHandlers();
//        assertTrue(handlers.length == 8);
//
//        Geometry g = handlers[4].moveTo(3, 3);
//        gp = new ClosedGeneralPath();
//        gp.moveTo(0, 0);
//        gp.lineTo(0, 10);
//        gp.lineTo(10, 10);
//        gp.lineTo(10, 0);
//        gp.closePath();
//        gphole = new ClosedGeneralPath();
//        gphole.moveTo(3, 3);
//        gphole.lineTo(4, 2);
//        gphole.lineTo(4, 4);
//        gphole.lineTo(2, 4);
//        gphole.closePath();
//        assertTrue(((BooleanValue) g.equals(gf.createPolygon(gp,
//                new ClosedGeneralPath[]{gphole}))).getValue());
//
//        g = handlers[4].remove();
//        gp = new ClosedGeneralPath();
//        gp.moveTo(0, 0);
//        gp.lineTo(0, 10);
//        gp.lineTo(10, 10);
//        gp.lineTo(10, 0);
//        gp.closePath();
//        gphole = new ClosedGeneralPath();
//        gphole.moveTo(4, 2);
//        gphole.lineTo(4, 4);
//        gphole.lineTo(2, 4);
//        gphole.closePath();
//        assertTrue(((BooleanValue) g.equals(gf.createPolygon(gp,
//                new ClosedGeneralPath[]{gphole}))).getValue());
//
//        p = new Primitive(gf.createPolygon(gp,
//                new ClosedGeneralPath[]{gphole}), -1);
//        handlers = p.getHandlers();
//        assertTrue(handlers.length == 7);
//
//        try{
//            handlers[4].remove();
//            assertTrue(false);
//        } catch (CannotChangeGeometryException e) {
//            assertTrue(true);
//        }
//    }
//
//    public void testJTS() throws Exception {
//        //0 0, 0 2, 3 2, 3 1, 1 1)
//        GeometryFactory gf = new GeometryFactory();
//
//        Coordinate[] coords = new Coordinate[6];
//        coords[0] = new Coordinate(0, 0);
//        coords[1] = new Coordinate(1, 1);
//        coords[2] = new Coordinate(3, 2);
//        coords[3] = new Coordinate(3, 1);
//        coords[4] = new Coordinate(1, 1);
//        coords[5] = new Coordinate(0, 0);
//        Polygon lr = gf.createPolygon(gf.createLinearRing(coords), new LinearRing[0]);
//        boolean validFirst = lr.isValid();
//
//        coords = new Coordinate[6];
//        coords[0] = new Coordinate(0, 0);
//        coords[1] = new Coordinate(2, 0);
//        coords[2] = new Coordinate(3, 2);
//        coords[3] = new Coordinate(3, 1);
//        coords[4] = new Coordinate(2, 0);
//        coords[5] = new Coordinate(0, 0);
//        lr = gf.createPolygon(gf.createLinearRing(coords), new LinearRing[0]);
//        boolean validSecond = lr.isValid();
//
//        assertTrue(validFirst == validSecond);
//    }
//
//    public void testLineAdition() throws Exception {
//        Primitive p = new Primitive(line, -1);
//        Geometry g = p.insertVertex(new Point2D.Double(0.5, 0.5), 1);
//        GeneralPath gp = new GeneralPath();
//        gp.moveTo(0, 0);
//        gp.lineTo(0.5, 0.5);
//        gp.lineTo(1, 1);
//        gp.lineTo(2, 2);
//        assertTrue(((BooleanValue)g.equals(gf.createLineString(gp))).getValue());
//        assertTrue(p.insertVertex(new Point2D.Double(2.5, 2), 0.4) == null);
//        assertTrue(p.insertVertex(new Point2D.Double(-1, 0), 0.9) == null);
//    }
//
//    public void testPolygonAdition() throws Exception {
//        Primitive p = new Primitive(polygon, -1);
//        Geometry g = p.insertVertex(new Point2D.Double(0.5, 0.5), 1);
//        ClosedGeneralPath gp = new ClosedGeneralPath();
//        gp.moveTo(0, 0);
//        gp.lineTo(0.5, 0.5);
//        gp.lineTo(1, 1);
//        gp.lineTo(2, 2);
//        gp.lineTo(3, 0);
//        gp.closePath();
//        assertTrue(((BooleanValue)g.equals(gf.createPolygon(gp))).getValue());
//        g = p.insertVertex(new Point2D.Double(1.5, 0.2), 0.4);
//        gp = new ClosedGeneralPath();
//        gp.moveTo(0, 0);
//        gp.lineTo(1, 1);
//        gp.lineTo(2, 2);
//        gp.lineTo(3, 0);
//        gp.lineTo(1.5, 0.2);
//        gp.closePath();
//        assertTrue(((BooleanValue)g.equals(gf.createPolygon(gp))).getValue());
//
//        assertTrue(p.insertVertex(new Point2D.Double(-1, 0), 0.9) == null);
//    }
//
//    public void testMultiLineAdition() throws Exception {
//        Primitive p = new Primitive(multiline, -1);
//        Geometry g = p.insertVertex(new Point2D.Double(0.5, 0.5), 1);
//        GeneralPath gp = new GeneralPath();
//        gp.setSingleGeometry(false);
//        gp.moveTo(0, 0);
//        gp.lineTo(0.5, 0.5);
//        gp.lineTo(1, 1);
//        gp.moveTo(2, 2);
//        gp.lineTo(3, 0);
//        gp.lineTo(4, 0);
//        Geometry ml = gf.createMultiLineString(GeneralPath.getParts(gp.getPathIterator(null)));
//        assertTrue(((BooleanValue)g.equals(ml)).getValue());
//
//        assertTrue(p.insertVertex(new Point2D.Double(1.5, 1.5), 0.4) == null);
//
//        g = p.insertVertex(new Point2D.Double(3.5, 0), 0.1);
//        gp = new GeneralPath();
//        gp.setSingleGeometry(false);
//        gp.moveTo(0, 0);
//        gp.lineTo(1, 1);
//        gp.moveTo(2, 2);
//        gp.lineTo(3, 0);
//        gp.lineTo(3.5, 0);
//        gp.lineTo(4, 0);
//        ml = gf.createMultiLineString(GeneralPath.getParts(gp.getPathIterator(null)));
//        assertTrue(((BooleanValue)g.equals(ml)).getValue());
//    }
//
//    public void testMultiPolygonAdition() throws Exception {
//        Primitive p = new Primitive(multipolygon, -1);
//        Geometry g = p.insertVertex(new Point2D.Double(0.5, 0.5), 1);
//        assertTrue(g.getGeometryType() == SpatialDataSource.MULTIPOLYGON);
//        ClosedGeneralPath gp = new ClosedGeneralPath();
//        gp.moveTo(0, 0);
//        gp.lineTo(0.5, 0.5);
//        gp.lineTo(1, 1);
//        gp.lineTo(2, 0);
//        gp.closePath();
//        Geometry pol1 = gf.createPolygon(gp);
//        gp = new ClosedGeneralPath();
//        gp.moveTo(2, 2);
//        gp.lineTo(3, 3);
//        gp.lineTo(3, 0);
//        gp.lineTo(2, 0);
//        gp.closePath();
//        Geometry pol2 = gf.createPolygon(gp);
//        assertTrue(((BooleanValue)g.equals(gf.createMultiPolygon(
//                new Geometry[]{pol1, pol2}))).getValue());
//        g = p.insertVertex(new Point2D.Double(2, 1.1), 0.3);
//        gp = new ClosedGeneralPath();
//        gp.moveTo(0, 0);
//        gp.lineTo(1, 1);
//        gp.lineTo(2, 0);
//        gp.closePath();
//        pol1 = gf.createPolygon(gp);
//        gp = new ClosedGeneralPath();
//        gp.moveTo(2, 2);
//        gp.lineTo(3, 3);
//        gp.lineTo(3, 0);
//        gp.lineTo(2, 0);
//        gp.lineTo(2, 1.1);
//        gp.closePath();
//        pol2 = gf.createPolygon(gp);
//        assertTrue(((BooleanValue)g.equals(
//                gf.createMultiPolygon(new Geometry[]{pol1, pol2}))).getValue());
//        assertTrue(p.insertVertex(new Point2D.Double(1.5, 1.5), 0.4) == null);
//    }
//
//    @Override
//    protected void setUp() throws Exception {
//        ClosedGeneralPath cgp = new ClosedGeneralPath();
//        cgp.moveTo(0, 0);
//        cgp.lineTo(1, 1);
//        cgp.lineTo(2, 0);
//        cgp.closePath();
//        Geometry p1 = gf.createPolygon(cgp);
//        cgp = new ClosedGeneralPath();
//        cgp.moveTo(2, 2);
//        cgp.lineTo(3, 3);
//        cgp.lineTo(3, 0);
//        cgp.lineTo(2, 0);
//        cgp.lineTo(2, 2);
//        Geometry p2 = gf.createPolygon(cgp);
//        multipolygon = gf.createMultiPolygon(new Geometry[] {p1, p2});
//
//        GeneralPath gp = new GeneralPath();
//        gp.setSingleGeometry(false);
//        gp.moveTo(0, 0);
//        gp.lineTo(1, 1);
//        gp.moveTo(2, 2);
//        gp.lineTo(3, 0);
//        gp.lineTo(4, 0);
//        multiline = gf.createMultiLineString(GeneralPath.getParts(gp.getPathIterator(null)));
//
//        cgp = new ClosedGeneralPath();
//        cgp.moveTo(0, 0);
//        cgp.lineTo(1, 1);
//        cgp.lineTo(2, 2);
//        cgp.lineTo(3, 0);
//        cgp.closePath();
//        polygon = gf.createPolygon(cgp);
//
//        gp = new GeneralPath();
//        gp.moveTo(0, 0);
//        gp.lineTo(1, 1);
//        gp.lineTo(2, 2);
//        line = gf.createLineString(gp);
//
//        gp = new MultipointGeneralPath();
//        gp.moveTo(0, 0);
//        gp.lineTo(1, 1);
//        multipoint = gf.createMultiPoint((MultipointGeneralPath)gp);
//
//        point = gf.createPoint(0, 0);
//        super.setUp();
//    }
}
