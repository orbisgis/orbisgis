package org.orbisgis.core.renderer.symbol;

import java.awt.Color;
import java.awt.Graphics2D;

public class GraphicsUtils {
	/**
	 * Draws an arrow on the given Graphics2D context
	 * 
	 * @param g
	 *            The Graphics2D context to draw on
	 * @param x
	 *            The x location of the "tail" of the arrow
	 * @param y
	 *            The y location of the "tail" of the arrow
	 * @param xx
	 *            The x location of the "head" of the arrow
	 * @param yy
	 *            The y location of the "head" of the arrow
	 * @param arrowWidth
	 * @param arrowLength
	 */
	public static void drawArrow(Graphics2D g, int x, int y, int xx, int yy,
			int arrowWidth, int arrowLength, Color fillColor, Color outlineColor) {
		float theta = 0.423f;
		int[] xPoints = new int[3];
		int[] yPoints = new int[3];
		float[] vecLine = new float[2];
		float[] vecLeft = new float[2];
		float fLength;
		float th;
		float ta;
		float baseX, baseY;

		xPoints[0] = xx;
		yPoints[0] = yy;

		// build the line vector
		vecLine[0] = (float) xPoints[0] - x;
		vecLine[1] = (float) yPoints[0] - y;

		// build the arrow base vector - normal to the line
		vecLeft[0] = -vecLine[1];
		vecLeft[1] = vecLine[0];

		// setup length parameters
		fLength = (float) Math.sqrt(vecLine[0] * vecLine[0] + vecLine[1]
				* vecLine[1]);
		th = arrowWidth / (2.0f * fLength);
		ta = arrowLength / (2.0f * ((float) Math.tan(theta) / 2.0f) * fLength);

		// find the base of the arrow
		baseX = ((float) xPoints[0] - ta * vecLine[0]);
		baseY = ((float) yPoints[0] - ta * vecLine[1]);

		// build the points on the sides of the arrow
		xPoints[1] = (int) (baseX + th * vecLeft[0]);
		yPoints[1] = (int) (baseY + th * vecLeft[1]);
		xPoints[2] = (int) (baseX - th * vecLeft[0]);
		yPoints[2] = (int) (baseY - th * vecLeft[1]);

		if (fillColor != null) {
			g.setColor(fillColor);
			g.fillPolygon(xPoints, yPoints, 3);
		}

		if (outlineColor != null) {
			g.setColor(outlineColor);
			g.drawLine(x, y, (int) baseX, (int) baseY);
			g.drawPolygon(xPoints, yPoints, 3);
		}
	}
}
