package org.orbisgis.core.ui.editors.map.tools;

import java.awt.Color;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Observable;

import javax.swing.AbstractButton;

import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.map.tool.DrawingException;
import org.orbisgis.core.ui.editors.map.tool.FinishedAutomatonException;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.editors.map.tool.TransitionException;
import org.orbisgis.core.ui.editors.map.tools.generated.Compass;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.plugins.views.output.OutputManager;
import org.orbisgis.utils.I18N;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;

public class CompassTool extends Compass {

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(
			"###.###");
	private Coordinate c1;
	private Coordinate c2;
	private Coordinate c3;
	private GeometryFactory gf = new GeometryFactory();
	private AbstractButton button;

	private void showAngle() {
		OutputManager om = Services.getService(OutputManager.class);
		om.println("Angle : " + getFormatedAngle(c1, c2, c3), Color.blue);
	}

	private double getAngle(Coordinate c1, Coordinate c2, Coordinate c3) {
		double angle1 = getAngleWithXAxis(c2, c1);
		double angle2 = getAngleWithXAxis(c2, c3);
		if (angle2 < angle1) {
			angle2 += 2 * Math.PI;
		}
		double segmentAngle = angle2 - angle1;
		return segmentAngle;
	}

	private String getFormatedAngle(Coordinate c1, Coordinate c2, Coordinate c3) {
		return DECIMAL_FORMAT.format(Angle.toDegrees(getAngle(c1, c2, c3)))
				+ "º";
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

	@Override
	public void drawIn_Cancel(Graphics g, MapContext mc, ToolManager tm)
			throws DrawingException {
	}

	@Override
	public void drawIn_OnePoint(Graphics g, MapContext mc, ToolManager tm)
			throws DrawingException {
		tm.addGeomToDraw(gf.createLineString(new Coordinate[] { c1,
				getCurrentCoordinateInDraw(tm) }));
	}

	private Coordinate getCurrentCoordinateInDraw(ToolManager tm) {
		return new Coordinate(tm.getLastRealMousePosition().getX(), tm
				.getLastRealMousePosition().getY());
	}

	@Override
	public void drawIn_Standby(Graphics g, MapContext mc, ToolManager tm)
			throws DrawingException {
	}

	@Override
	public void drawIn_ThreePoints(Graphics g, MapContext mc, ToolManager tm)
			throws DrawingException {
		Coordinate currentCoord = getCurrentCoordinateInDraw(tm);
		tm.addGeomToDraw(gf.createLineString(new Coordinate[] { c2, c3,
				currentCoord }));
		tm.addTextToDraw(getFormatedAngle(c2, c3, currentCoord) + "");
		drawSemiCircle(tm, c2, c3, currentCoord);
	}

	@Override
	public void drawIn_TwoPoints(Graphics g, MapContext mc, ToolManager tm)
			throws DrawingException {
		Coordinate currentCoord = getCurrentCoordinateInDraw(tm);
		tm.addGeomToDraw(gf.createLineString(new Coordinate[] { c1, c2,
				currentCoord }));
		tm.addTextToDraw(getFormatedAngle(c1, c2, currentCoord) + "");
		drawSemiCircle(tm, c1, c2, currentCoord);
	}

	private void drawSemiCircle(ToolManager tm, Coordinate c1, Coordinate c2,
			Coordinate c3) {
		double radius = c2.distance(c1) * 0.3;
		ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
		double angle1 = getAngleWithXAxis(c2, c1);
		double angle2 = getAngleWithXAxis(c2, c3);
		if (angle2 < angle1) {
			angle2 += Math.PI * 2;
		}
		for (double i = angle1; i < angle2; i = i + 0.1) {
			double x = c2.x + Math.cos(i) * radius;
			double y = c2.y + Math.sin(i) * radius;
			coords.add(new Coordinate(x, y));
		}
		double x = c2.x + Math.cos(angle2) * radius;
		double y = c2.y + Math.sin(angle2) * radius;
		coords.add(new Coordinate(x, y));
		if (coords.size() > 1) {
			LineString semiCircle = gf.createLineString(coords
					.toArray(new Coordinate[0]));
			tm.addGeomToDraw(semiCircle);
		}
	}

	@Override
	public void transitionTo_Cancel(MapContext mc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
	}

	@Override
	public void transitionTo_OnePoint(MapContext mc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		c1 = new Coordinate(tm.getValues()[0], tm.getValues()[1]);
	}

	@Override
	public void transitionTo_Standby(MapContext mc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
	}

	@Override
	public void transitionTo_ThreePoints(MapContext mc, ToolManager tm)
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

	@Override
	public void transitionTo_TwoPoints(MapContext mc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		c2 = new Coordinate(tm.getValues()[0], tm.getValues()[1]);
	}

	@Override
	public AbstractButton getButton() {
		return button;
	}

	@Override
	public void setButton(AbstractButton button) {
		this.button = button;

	}

	@Override
	public void update(Observable arg0, Object arg1) {
		PlugInContext.checkTool(this);
	}

	public String getName() {
		return I18N.getText("orbisgis.core.ui.editors.map.tool.mesure.angle");
	}

}
