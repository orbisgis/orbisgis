package org.orbisgis.views.geocognition.sync.editor.text;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JComponent;

public class MiddleComponent extends JComponent {

	// Values added to the polygon 'y' coordinates to fit well in my own
	// computer
	private static final int MAGIC_OFFSET_TOP = 1;
	private static final int MAGIC_OFFSET_BOTTOM = 1;

	private CompareTextPane leftArea, rightArea;

	private int leftOffset, rightOffset;

	private int titleOffset;

	private int selectedPolygon;

	/**
	 * Creates a new middle component
	 * 
	 * @param left
	 *            the left text area
	 * @param right
	 *            the right text area
	 */
	MiddleComponent(CompareTextPane left, CompareTextPane right) {
		selectedPolygon = -1;
		leftOffset = 0;
		rightOffset = 0;
		leftArea = left;
		rightArea = right;
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(AbstractCompareTextEditor.highlightColor);

		ArrayList<Rectangle> leftRectangles = leftArea.getRectangles();
		ArrayList<Rectangle> rightRectangles = rightArea.getRectangles();

		if (leftRectangles != null && rightRectangles != null) {
			for (int i = 0; i < leftRectangles.size(); i++) {

				Rectangle leftRect = leftRectangles.get(i);
				Rectangle rightRect = rightRectangles.get(i);

				// y coordinates
				int leftTop = leftRect.y + MAGIC_OFFSET_TOP - leftOffset;
				int rightTop = rightRect.y + MAGIC_OFFSET_TOP - rightOffset;
				int leftBottom = leftRect.y + leftRect.height
						+ MAGIC_OFFSET_BOTTOM - leftOffset;
				int rightBottom = rightRect.y + rightRect.height
						+ MAGIC_OFFSET_BOTTOM - rightOffset;
				int[] yCoordinates = { leftTop, rightTop, rightBottom,
						leftBottom };

				// x coordinates
				int[] xCoordinates = { 0, getWidth(), getWidth(), 0 };

				checkCoordinates(yCoordinates);

				if (i == selectedPolygon) {
					g.setColor(AbstractCompareTextEditor.selectColor);
					g.fillPolygon(xCoordinates, yCoordinates, 4);
					g.setColor(AbstractCompareTextEditor.highlightColor);
				} else {
					g.fillPolygon(xCoordinates, yCoordinates, 4);
				}
			}
		}
	}

	private void checkCoordinates(int[] coords) {
		Rectangle visibleLeft = leftArea.getVisibleArea();

		int minimum = titleOffset + MAGIC_OFFSET_TOP;
		int maximum = minimum + visibleLeft.height;

		for (int i = 0; i < coords.length; i++) {
			coords[i] = Math.max(coords[i], minimum);
			coords[i] = Math.min(coords[i], maximum);
		}
	}

	/**
	 * Sets the offset for the left text area
	 * 
	 * @param offset
	 *            the offset to set
	 */
	void setLeftOffset(int offset) {
		leftOffset = offset;
	}

	/**
	 * Sets the offset for the right text area
	 * 
	 * @param offset
	 *            the offset to set
	 */
	void setRightOffset(int offset) {
		rightOffset = offset;
	}

	/**
	 * Sets the offset for the left text area
	 * 
	 * @param offset
	 *            the offset to set
	 */
	void setTitleOffset(int offset) {
		titleOffset = offset;
	}

	/**
	 * Sets the polygon with the given index as the selected one
	 * 
	 * @param i
	 *            the index of the polygon to select
	 */
	void setSelected(int i) {
		selectedPolygon = i;
	}

	/**
	 * Gets the index of the selected polygon
	 * 
	 * @return the index of the selected polygon
	 */
	int getSelected() {
		return selectedPolygon;
	}
}
