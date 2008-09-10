package org.orbisgis.views.geocognition.sync.editor.text;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.orbisgis.ui.text.UndoRedoInstaller;
import org.orbisgis.ui.text.UndoRedoListener;
import org.orbisgis.ui.text.UndoableDocument;
import org.orbisgis.views.geocognition.sync.editor.text.diff.Range;

public class CompareTextPane extends JPanel {
	// Constants
	private static final String TITLE_MARGIN = " ";
	private static final int TITLE_HEIGHT = 22;
	private static final int PANE_MARGIN = 4;
	private static final int AREA_WIDTH = 300;
	private static final int VISIBLE_ROWS = 20;

	// Interface
	private CompareTextArea textArea;
	private JScrollPane scrollPane;
	private JLabel titleLabel;

	// The total number of lines in the textArea
	private int maxLines;

	// The current line shown as first in the JScrollPane
	private int line;

	private ArrayList<Range> highlights;

	// Flag to determine if the pane text has been modified
	private boolean dirty;

	private AbstractCompareTextEditor editor;

	/**
	 * Creates a new CompareTextPane
	 */
	CompareTextPane(AbstractCompareTextEditor editor) {
		this.editor = editor;
		line = 0;
		textArea = new CompareTextArea();
		textArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
		textArea.setMargin(new Insets(0, 10, 0, 10));
		textArea.addPropertyChangeListener("document",
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						UndoRedoInstaller.installUndoRedoSupport(textArea);
					}
				});

		UndoableDocument doc = new UndoableDocument();
		doc.addUndoRedoListener(new CompareUndoListener());
		textArea.setDocument(doc);

		scrollPane = new JScrollPane();
		scrollPane.setViewportView(textArea);
		scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		scrollPane.setPreferredSize(new Dimension(AREA_WIDTH, VISIBLE_ROWS
				* textArea.getRowHeight() + PANE_MARGIN));
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		setLayout(new BorderLayout());

		titleLabel = new JLabel();
		titleLabel.setPreferredSize(new Dimension(0, TITLE_HEIGHT));
		add(titleLabel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
	}

	void setModel(String text, String title, ArrayList<Range> h) {
		textArea.setText(text);
		setModel(title, h);
	}

	void setModel(String title, ArrayList<Range> h) {
		maxLines = textArea.getText().split("\n").length;
		titleLabel.setText(TITLE_MARGIN + title);
		highlights = h;
		textArea.setModel(h);
	}

	@Override
	public boolean isFocusOwner() {
		return textArea.isFocusOwner();
	}

	/**
	 * Updates the CompareTextPane to show the specified line as the first shown
	 * line. If the given line is greater than the max number of lines, this
	 * method does nothing
	 * 
	 * @param lineNumber
	 *            the number of line to show
	 */
	void scrollTo(int lineNumber) {
		if (lineNumber >= 0 && lineNumber < maxLines) {
			// the line number can't be greater than maxLines - visibleRows
			// (if maxLines > visibleRows) or 0 (otherwise)
			int upBound = Math.max(maxLines - getVisibleRows(), 0);
			line = Math.min(lineNumber, upBound);
		}

		Rectangle visible = getVisibleArea();
		visible.y = line * textArea.getRowHeight();
		textArea.scrollRectToVisible(visible);
	}

	/**
	 * Adds a caret listener to the contained CompareTextArea
	 * 
	 * @param listener
	 *            the listener to add
	 */
	void addCaretListener(CaretListener listener) {
		textArea.addCaretListener(listener);
	}

	@Override
	public synchronized void addMouseWheelListener(MouseWheelListener l) {
		if (textArea == null) {
			super.addMouseWheelListener(l);
		} else {
			textArea.addMouseWheelListener(l);
		}
	}

	/**
	 * Returns the current line shown as first int the JScrollPane
	 * 
	 * @return the line
	 */
	int getFirstLine() {
		return line;
	}

	/**
	 * Gets the number of visible rows of the CompareTextArea
	 * 
	 * @return the number of visible rows
	 */
	int getVisibleRows() {
		int height = getHeight();
		if (height == 0) {
			height = getPreferredSize().height;
		}

		if (scrollPane.getHorizontalScrollBar().isVisible()) {
			return height / textArea.getRowHeight() - 2;
		} else {
			return height / textArea.getRowHeight() - 1;
		}
	}

	/**
	 * Gets the highlight rectangles of the pane
	 * 
	 * @return the highlight rectangles
	 */
	ArrayList<Rectangle> getRectangles() {
		return textArea.getRectangles();
	}

	/**
	 * Gets the highlight ranges of the pane
	 * 
	 * @return the highlight ranges
	 */
	ArrayList<Range> getHighlights() {
		return highlights;
	}

	/**
	 * Gets the CompareTextArea contained in this pane
	 * 
	 * @return the CompareTextArea
	 */
	CompareTextArea getTextArea() {
		return textArea;
	}

	/**
	 * Gets the number of pixels occupied by the title label
	 * 
	 * @return the number of pixels occupied by the title label
	 */
	int getTitleOffset() {
		return scrollPane.getLocation().y;
	}

	/**
	 * Gets the number of pixels of the textArea not shown above this panel
	 * 
	 * @return the number of pixels of the textArea not shown above this panel
	 */
	int getOffset() {
		return getVisibleArea().y - getTitleOffset();
	}

	/**
	 * Sets this pane as modified or not
	 * 
	 * @param flag
	 */
	void setDirty(boolean flag) {
		dirty = flag;

		String title = titleLabel.getText();
		if (dirty && !title.endsWith("*")) {
			title += "*";
		} else if (title.endsWith("*")) {
			title = title.substring(0, title.length() - 1);
		}

		titleLabel.setText(title);
	}

	/**
	 * Determines if the content of the pane has been modified
	 * 
	 * @return true if the content has been modified, false otherwise
	 */
	boolean isDirty() {
		return dirty;
	}

	/**
	 * Fires a new caret event for the text area
	 */
	void fireCaretEvent() {
		textArea.fireCaretUpdate(new CaretEvent(textArea) {
			@Override
			public int getMark() {
				return textArea.getCaret().getMark();
			}

			@Override
			public int getDot() {
				return textArea.getCaret().getDot();
			}

		});
	}

	Rectangle getVisibleArea() {
		return textArea.getVisibleRect();
	}

	/**
	 * Listener for the undo manager
	 * 
	 * @author victorzinho
	 * 
	 */
	private class CompareUndoListener implements UndoRedoListener {
		private int scrollValue;

		private void postUndoRedo() {
			editor.update();
			editor.setScrollBarValue(scrollValue);
			repaint();
		}

		@Override
		public void redoPerformed() {
			postUndoRedo();
		}

		@Override
		public void undoPerformed() {
			postUndoRedo();
		}

		@Override
		public void preRedo() {
			scrollValue = editor.getScrollBarValue();
		}

		@Override
		public void preUndo() {
			scrollValue = editor.getScrollBarValue();
		}
	}

	void resetUndoManager() {
		((UndoableDocument) textArea.getDocument()).resetUndoEdits();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		editor.repaint();
	}
}
