package org.orbisgis.views.geocognition.sync.editor.text;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;

import org.orbisgis.views.geocognition.sync.editor.text.diff.Range;

public class CompareTextArea extends JTextArea {

	private ArrayList<Range> highlightRanges;

	private int selectedRectangle;

	/**
	 * Creates a new CompareTextArea and highlights the given line numbers
	 */
	CompareTextArea() {
		selectedRectangle = -1;
	}

	/**
	 * Sets the model of the text area
	 * 
	 * @param h
	 *            the ranges of the lines to highlight
	 */
	void setModel(ArrayList<Range> h) {
		highlightRanges = h;
	}

	/**
	 * Gets the highlight rectangles
	 * 
	 * @return the highlight rectangles
	 */
	ArrayList<Rectangle> getRectangles() {
		ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();
		for (int i = 0; i < highlightRanges.size(); i++) {
			int start = highlightRanges.get(i).getStart();
			int end = highlightRanges.get(i).getEnd() + 1;
			if (end == 0) {
				Rectangle rectangle = new Rectangle(0, start * getRowHeight(),
						getWidth(), 1);
				rectangles.add(rectangle);
			} else {
				Rectangle rectangle = new Rectangle(0, start * getRowHeight(),
						getWidth(), (end - start) * getRowHeight());
				rectangles.add(rectangle);
			}
		}

		return rectangles;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(AbstractCompareTextEditor.highlightColor);

		ArrayList<Rectangle> rectangles = getRectangles();
		if (rectangles != null) {
			for (int i = 0; i < rectangles.size(); i++) {
				Rectangle r = rectangles.get(i);
				if (i == selectedRectangle) {
					g.setColor(AbstractCompareTextEditor.selectColor);
					g.fillRect(r.x, r.y, r.width, r.height);
					g.setColor(AbstractCompareTextEditor.highlightColor);
				} else {
					g.fillRect(r.x, r.y, r.width, r.height);
				}
			}
		}
	}

	@Override
	public int getRowHeight() {
		return super.getRowHeight();
	}

	/**
	 * Sets the specified polygon as selected
	 * 
	 * @param i
	 *            the index of the polygon
	 */
	void setSelected(int i) {
		selectedRectangle = i;
	}

	/**
	 * Gets the index of the selected polygon
	 * 
	 * @return the index of the selected polygon
	 */
	int getSelected() {
		return selectedRectangle;
	}

	@Override
	public void fireCaretUpdate(CaretEvent e) {
		super.fireCaretUpdate(e);
	}
}
