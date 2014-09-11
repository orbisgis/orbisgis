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
package org.orbisgis.mapeditor.map.tools;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.mapeditor.map.icons.MapEditorIcons;
import org.orbisgis.mapeditor.map.tool.DrawingException;
import org.orbisgis.mapeditor.map.tool.FinishedAutomatonException;
import org.orbisgis.mapeditor.map.tool.NoSuchTransitionException;
import org.orbisgis.mapeditor.map.tool.ToolManager;
import org.orbisgis.mapeditor.map.tool.TransitionException;
import org.orbisgis.mapeditor.map.tools.generated.AbstractAutomaton;

/**
 * A tool to show the drawn angle in degree.
 */
public class CompassTool extends AbstractAutomaton {
    private static Logger GUILOGGER = Logger.getLogger("gui." + CompassTool.class);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(
            "###.###");
    private static final double DRAW_RADIUS_PERCENT_SIZE = 0.3;     // circle radius P0 P1 distance
    private static final double DRAW_CIRCLE_SUBDIVISION_ANGLE = 10; // Lower=smoother circle
    private Coordinate c1;
    private Coordinate c2;
    private Coordinate c3;
    private GeometryFactory gf = new GeometryFactory();

    private void showAngle() {
        GUILOGGER.info(i18n.tr("Angle : {0}", getFormatedAngle(c1, c2, c3)));
    }

    private double getAngle(Coordinate c1, Coordinate c2, Coordinate c3) {
        double angle1 = getAngleWithXAxis(c2, c1);
        double angle2 = getAngleWithXAxis(c2, c3);
        if (angle2 < angle1) {
            angle2 += 2 * Math.PI;
        }
        return angle2 - angle1;
    }

    private String getFormatedAngle(Coordinate c1, Coordinate c2, Coordinate c3) {
        return DECIMAL_FORMAT.format(Angle.toDegrees(getAngle(c1, c2, c3)))
                + "º";
    }

    @Override
    public void transition(Code code) throws NoSuchTransitionException,
            TransitionException, FinishedAutomatonException {
        if (Code.ESC == code) {
            status = Status.CANCEL;
            if (isFinished(status)) {
                throw new FinishedAutomatonException();
            }
        }
        Status preStatus;
        switch (status) {
            case STANDBY:
                if (Code.PRESS == code) {
                    preStatus = status;
                    try {
                        status = Status.ONE_POINT;
                        transitionTo_OnePoint(tm);
                        if (isFinished(status)) {
                            throw new FinishedAutomatonException();
                        }
                    } catch (TransitionException e) {
                        status = preStatus;
                        throw e;
                    }
                }
                break;
            case ONE_POINT:
                if (Code.PRESS == code) {
                    preStatus = status;
                    try {
                        status = Status.TWO_POINTS;
                        transitionTo_TwoPoints(tm);
                        if (isFinished(status)) {
                            throw new FinishedAutomatonException();
                        }
                    } catch (TransitionException e) {
                        status = preStatus;
                        throw e;
                    }
                }
                break;
            case TWO_POINTS:
                if (Code.PRESS == code) {
                    preStatus = status;
                    try {
                        status = Status.THREE_POINTS;
                        transitionTo_ThreePoints(tm);
                        if (isFinished(status)) {
                            throw new FinishedAutomatonException();
                        }
                    } catch (TransitionException e) {
                        status = preStatus;
                        throw e;
                    }
                }
                break;
            case THREE_POINTS:
                if (Code.PRESS == code) {
                    preStatus = status;
                    try {
                        status = Status.THREE_POINTS;
                        transitionTo_ThreePoints(tm);
                        if (isFinished(status)) {
                            throw new FinishedAutomatonException();
                        }
                    } catch (TransitionException e) {
                        status = preStatus;
                        throw e;
                    }
                }
            default:
                throw new NoSuchTransitionException(code.toString());
        }


    }

    private double getAngleWithXAxis(Coordinate origin, Coordinate destination) {
        LineSegment s1 = new LineSegment(origin, destination);
        double angle = s1.angle();
        if (angle < 0) {
            angle = 2 * Math.PI + angle;
        }
        return angle;
    }

    @Override
    public boolean isEnabled(MapContext vc, ToolManager tm) {
        return ToolUtilities.layerCountGreaterThan(vc, 0);
    }

    @Override
    public boolean isVisible(MapContext vc, ToolManager tm) {
        return true;
    }

    private void drawIn_OnePoint(ToolManager tm)
            throws DrawingException {
        tm.addGeomToDraw(gf.createLineString(new Coordinate[]{c1,
                getCurrentCoordinateInDraw(tm)}));
    }

    private Coordinate getCurrentCoordinateInDraw(ToolManager tm) {
        return new Coordinate(tm.getLastRealMousePosition().getX(), tm
                .getLastRealMousePosition().getY());
    }

    private void drawIn_ThreePoints(ToolManager tm)
            throws DrawingException {
        Coordinate currentCoord = getCurrentCoordinateInDraw(tm);
        tm.addGeomToDraw(gf.createLineString(new Coordinate[]{c2, c3,
                currentCoord}));
        tm.addTextToDraw(getFormatedAngle(c2, c3, currentCoord) + "");
        drawSemiCircle(tm, c2, c3, currentCoord);
    }

    private void drawIn_TwoPoints(ToolManager tm)
            throws DrawingException {
        Coordinate currentCoord = getCurrentCoordinateInDraw(tm);
        tm.addGeomToDraw(gf.createLineString(new Coordinate[]{c1, c2,
                currentCoord}));
        tm.addTextToDraw(getFormatedAngle(c1, c2, currentCoord) + "");
        drawSemiCircle(tm, c1, c2, currentCoord);
    }

    private void drawSemiCircle(ToolManager tm, Coordinate c1, Coordinate c2,
                                Coordinate c3) {
        double radius = c2.distance(c1) * DRAW_RADIUS_PERCENT_SIZE;
        List<Coordinate> coordinates = new LinkedList<Coordinate>();
        double angle1 = getAngleWithXAxis(c2, c1);
        double angle2 = getAngleWithXAxis(c2, c3);
        if (angle2 < angle1) {
            angle2 += Math.PI * 2;
        }
        for (double i = angle1; i < angle2; i = i + DRAW_CIRCLE_SUBDIVISION_ANGLE) {
            double x = c2.x + Math.cos(i) * radius;
            double y = c2.y + Math.sin(i) * radius;
            coordinates.add(new Coordinate(x, y));
        }
        double x = c2.x + Math.cos(angle2) * radius;
        double y = c2.y + Math.sin(angle2) * radius;
        coordinates.add(new Coordinate(x, y));
        if (coordinates.size() > 1) {
            LineString semiCircle = gf.createLineString(coordinates
                    .toArray(new Coordinate[coordinates.size()]));
            tm.addGeomToDraw(semiCircle);
        }
    }

    private void transitionTo_OnePoint(ToolManager tm)
            throws FinishedAutomatonException, TransitionException {
        c1 = new Coordinate(tm.getValues()[0], tm.getValues()[1]);
    }

    @Override
    protected void transitionTo_Standby(MapContext mc, ToolManager tm)
            throws FinishedAutomatonException, TransitionException {
    }

    private void transitionTo_ThreePoints(ToolManager tm)
            throws FinishedAutomatonException, TransitionException {
        Coordinate newCoord = new Coordinate(tm.getValues()[0],
                tm.getValues()[1]);
        if (c3 == null) {
            c3 = newCoord;
        } else {
            c1 = c2;
            c2 = c3;
            c3 = newCoord;
        }
        showAngle();
    }

    private void transitionTo_TwoPoints(ToolManager tm)
            throws FinishedAutomatonException, TransitionException {
        c2 = new Coordinate(tm.getValues()[0], tm.getValues()[1]);
    }

    @Override
    public ImageIcon getImageIcon() {
        return MapEditorIcons.getIcon("angle");
    }

    @Override
    public String getName() {
        return i18n.tr("Mesure angle");
    }

    @Override
    public String getTooltip() {
        return i18n.tr("This tool mesure the angle");
    }

    public void update(Observable o, Object o1) {
    }

    @Override
    public String[] getTransitionLabels() {
        return new String[]{i18n.tr("Cancel")};
    }

    @Override
    public Code[] getTransitionCodes() {
        return new Code[]{Code.ESC};
    }


    public boolean isFinished(Status status) {
        switch (status) {
            case STANDBY:
            case ONE_POINT:
            case TWO_POINTS:
            case THREE_POINTS:
                return false;
            case CANCEL:
                return true;
            default:
                throw new RuntimeException("Invalid status: " + status);
        }
    }

    @Override
    public void draw(Graphics g) throws DrawingException {
        switch (status) {
            case ONE_POINT:
                drawIn_OnePoint(tm);
                break;
            case TWO_POINTS:
                drawIn_TwoPoints(tm);
                break;
            case THREE_POINTS:
                drawIn_ThreePoints(tm);
                break;
        }
    }

    public String getMessage() {
        switch (status) {
            case STANDBY:
            case ONE_POINT:
            case TWO_POINTS:
            case THREE_POINTS:
            case CANCEL:
                return "";
            default:
                throw new RuntimeException();
        }
    }

    @Override
    public void toolFinished(MapContext mc, ToolManager tm)
            throws NoSuchTransitionException, TransitionException,
            FinishedAutomatonException {

    }
}
