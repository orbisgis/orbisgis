package org.orbisgis.views.geocognition.sync.editor.text;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import org.orbisgis.Services;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.geocognition.GeocognitionElementListener;
import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.images.IconLoader;
import org.orbisgis.ui.text.UndoableDocument;
import org.orbisgis.views.geocognition.sync.GeocognitionElementDecorator;
import org.orbisgis.views.geocognition.sync.editor.ICompareEditor;
import org.orbisgis.views.geocognition.sync.editor.text.diff.Difference;
import org.orbisgis.views.geocognition.sync.editor.text.diff.Mapping;
import org.orbisgis.views.geocognition.sync.editor.text.diff.Range;

public abstract class AbstractCompareTextEditor extends JPanel implements
		ICompareEditor {
	private static final Icon ALL_TO_LEFT = IconLoader.getIcon("all_left.png");
	private static final Icon ALL_TO_RIGHT = IconLoader
			.getIcon("all_right.png");
	private static final Icon SELECTED_TO_LEFT = IconLoader
			.getIcon("current_left.png");
	private static final Icon SELECTED_TO_RIGHT = IconLoader
			.getIcon("current_right.png");
	private static final Icon SAVE_LEFT = IconLoader.getIcon("save_left.png");
	private static final Icon SAVE_RIGHT = IconLoader.getIcon("save_right.png");

	// String constants for the resource modification dialog
	private static final String LEFT_OUTSIDE_MODIFY_TEXT = "The left resource has changed outside "
			+ "the editor. Do you want to load the changes?";
	private static final String LEFT_OUTSIDE_MODIFY_TITLE = "Left resource changed";
	private static final String RIGHT_OUTSIDE_MODIFY_TEXT = "The right resource has changed outside "
			+ "the editor. Do you want to load the changes?";
	private static final String RIGHT_OUTSIDE_MODIFY_TITLE = "Right resource changed";

	// String constants for the empty node
	private static final String NO_DOCUMENT_TITLE = "(no document)";

	// Color constants for highlighting
	public static final Color highlightColor = new Color(0, 0, 0, 60);
	public static final Color selectColor = new Color(0, 0, 0, 30);

	// Preferred width of the middle component
	private static final int MIDDLE_COMPONENT_WIDTH = 40;

	// Constants used for the setModel method to preserve
	// the right or the left caret position of the panes
	public static final int PRESERVE_RIGHT_POSITION = 1;
	public static final int PRESERVE_LEFT_POSITION = 2;
	public static final int NO_PRESERVE_POSITION = 0;

	// Interface
	private CompareTextPane rightPane, leftPane;
	private JScrollBar scrollbar;
	private MiddleComponent middle;
	private JButton allToLeft, allToRight, selectedToLeft, selectedToRight,
			saveRight, saveLeft;

	// References to the original nodes in the local and remote trees
	protected GeocognitionElementDecorator originalLeft, originalRight;

	// Copy of the original nodes. All the modifications of the content
	// of the files update this nodes and the original nodes are never modified
	// except when saving
	private String rightText, leftText, leftSavedText, rightSavedText,
			rightTitle, leftTitle;

	private Mapping mapping;
	private ArrayList<Point> originalMap, map;

	// Flags that indicates whether the scrollbar, the caret position or
	// the text of the panes has been modified by code or not
	private boolean caretChangeByCode;
	private boolean scrollBarChangeByCode;
	private boolean documentChangeByCode;

	// Element listeners
	private RightElementListener rightElementListener;
	private LeftElementListener leftElementListener;

	/**
	 * Creates a new CompareEditor
	 * 
	 * @param p
	 *            the CompareSplitPane where this editor is contained
	 */
	public AbstractCompareTextEditor() {
		caretChangeByCode = false;
		scrollBarChangeByCode = false;
		documentChangeByCode = false;

		leftElementListener = new LeftElementListener();
		rightElementListener = new RightElementListener();

		leftPane = new CompareTextPane(this);
		rightPane = new CompareTextPane(this);

		scrollbar = new JScrollBar(JScrollBar.VERTICAL, 0, 10, 0, 10);
		middle = new MiddleComponent(leftPane, rightPane);
		middle.setPreferredSize(new Dimension(MIDDLE_COMPONENT_WIDTH, 10));

		allToLeft = new JButton(ALL_TO_LEFT);
		allToLeft.setToolTipText("Copy all changes to the local element");
		allToRight = new JButton(ALL_TO_RIGHT);
		allToRight.setToolTipText("Copy all changes to the remote element");
		selectedToLeft = new JButton(SELECTED_TO_LEFT);
		selectedToLeft
				.setToolTipText("Copy selected change to the local element");
		selectedToRight = new JButton(SELECTED_TO_RIGHT);
		selectedToRight
				.setToolTipText("Copy selected change to the remote element");
		saveLeft = new JButton(SAVE_LEFT);
		saveLeft.setToolTipText("Save local element");
		saveRight = new JButton(SAVE_RIGHT);
		saveRight.setToolTipText("Save remote element");

		addListeners();
		addComponents();
	}

	/**
	 * Adds all the listeners to the controls
	 */
	private void addListeners() {
		// scroll bar listener
		scrollbar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (!scrollBarChangeByCode) {
					Point mapping = map.get(Math.min(e.getValue(),
							map.size() - 1));
					scrollPanes(mapping.x, mapping.y, -1);
				}
			}

		});

		// left pane listeners
		leftPane.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				scrollLeftPane(e.getWheelRotation());
			}
		});

		leftPane.addCaretListener(new CaretSelectionListener());

		leftPane.getTextArea().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) {
				Point mapping = map.get(scrollbar.getValue());
				scrollPanes(mapping.x, mapping.y, -1);

				Rectangle left = leftPane.getVisibleArea();
				Rectangle right = rightPane.getVisibleArea();
				right.x = left.x;
				rightPane.getTextArea().scrollRectToVisible(right);
			}
		});

		final UndoableDocument leftDoc = (UndoableDocument) leftPane
				.getTextArea().getDocument();
		leftDoc.addDocumentListener(new DocumentListener() {
			private void fire(DocumentEvent e) {
				try {
					String text = e.getDocument().getText(0,
							e.getDocument().getLength());
					leftText = text;
				} catch (BadLocationException exc) {
					Services.getErrorManager().error("bug!", exc);
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				fire(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				fire(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				fire(e);
			}

		});

		leftDoc.setDocumentFilter(new DocumentFilter() {

			@Override
			public void replace(FilterBypass fb, int offset, int length,
					String text, AttributeSet attrs)
					throws BadLocationException {
				leftDoc.groupUndoEdits(true);
				if (!documentChangeByCode) {
					if (leftPane.isEnabled()) {
						documentChangeByCode = true;
						super.replace(fb, offset, length, text, attrs);
						updateLeft(fb.getDocument().getText(0,
								fb.getDocument().getLength()));
						documentChangeByCode = false;
					}
				} else {
					super.replace(fb, offset, length, text, attrs);
				}
				leftDoc.groupUndoEdits(false);
			}

			@Override
			public void remove(FilterBypass fb, int offset, int length)
					throws BadLocationException {
				if (!documentChangeByCode) {
					if (leftPane.isEnabled()) {
						documentChangeByCode = true;
						super.remove(fb, offset, length);
						updateLeft(fb.getDocument().getText(0,
								fb.getDocument().getLength()));
						documentChangeByCode = false;
					}
				} else {
					super.remove(fb, offset, length);
				}
			}

		});

		// right pane listeners
		rightPane.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				scrollRightPane(e.getWheelRotation());
			}
		});

		rightPane.addCaretListener(new CaretSelectionListener());

		rightPane.getTextArea().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) {
				Point mapping = map.get(scrollbar.getValue());
				scrollPanes(mapping.x, mapping.y, -1);

				Rectangle left = leftPane.getVisibleArea();
				Rectangle right = rightPane.getVisibleArea();
				left.x = right.x;
				leftPane.getTextArea().scrollRectToVisible(left);
			}
		});

		final UndoableDocument rightDoc = (UndoableDocument) rightPane
				.getTextArea().getDocument();
		rightDoc.setDocumentFilter(new DocumentFilter() {
			@Override
			public void replace(FilterBypass fb, int offset, int length,
					String text, AttributeSet attrs)
					throws BadLocationException {
				rightDoc.groupUndoEdits(true);
				if (!documentChangeByCode) {
					if (rightPane.isEnabled()) {
						documentChangeByCode = true;
						super.replace(fb, offset, length, text, attrs);
						updateRight(fb.getDocument().getText(0,
								fb.getDocument().getLength()));
						documentChangeByCode = false;
					}
				} else {
					super.replace(fb, offset, length, text, attrs);
				}
				rightDoc.groupUndoEdits(false);
			}

			@Override
			public void remove(FilterBypass fb, int offset, int length)
					throws BadLocationException {
				if (!documentChangeByCode) {
					if (rightPane.isEnabled()) {
						documentChangeByCode = true;
						super.remove(fb, offset, length);
						updateRight(fb.getDocument().getText(0,
								fb.getDocument().getLength()));
						documentChangeByCode = false;
					}
				} else {
					super.remove(fb, offset, length);
				}
			}

		});

		rightDoc.addDocumentListener(new DocumentListener() {
			private void fire(DocumentEvent e) {
				try {
					String text = e.getDocument().getText(0,
							e.getDocument().getLength());
					rightText = text;
				} catch (BadLocationException exc) {
					Services.getErrorManager().error("bug!", exc);
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				fire(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				fire(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				fire(e);
			}

		});

		// editor listener
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				scrollLeftPane(0);
				refresh();

				int i;
				for (i = 0; i < map.size(); i++) {
					Point mapping = map.get(i);
					if (mapping.x == leftPane.getFirstLine()
							&& mapping.y == rightPane.getFirstLine())
						break;
				}

				scrollBarChangeByCode = true;
				scrollbar.setValue(i);
				scrollBarChangeByCode = false;
			}
		});

		// toolbar buttons listeners
		allToLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				allToLeft();
			}
		});

		allToRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				allToRight();
			}
		});

		selectedToRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedToRight();
			}
		});

		selectedToLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedToLeft();
			}
		});

		saveLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveLeft();
			}
		});

		saveRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveRight();
			}
		});
	}

	/**
	 * Adds all the components to the editor
	 */
	private void addComponents() {
		this.setLayout(new BorderLayout());
		JPanel center = new JPanel();
		GridBagLayout gl = new GridBagLayout();
		center.setLayout(gl);

		// left pane
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.45;
		center.add(leftPane, c);

		// middle component
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.1;
		center.add(middle, c);

		// right pane
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 0.45;
		c.weighty = 1.0;
		center.add(rightPane, c);

		// toolbar
		JPanel north = new JPanel();
		north.add(saveLeft);
		north.add(allToRight);
		north.add(allToLeft);
		north.add(selectedToRight);
		north.add(selectedToLeft);
		north.add(saveRight);

		// Put all together
		this.add(center, BorderLayout.CENTER);
		this.add(scrollbar, BorderLayout.EAST);
		this.add(north, BorderLayout.NORTH);
	}

	/**
	 * Gets the value of the scrollbar
	 * 
	 * @return the value of the scrollbar
	 */
	int getScrollBarValue() {
		return scrollbar.getValue();
	}

	/**
	 * Sets the value of the scrollbar
	 * 
	 * @param v
	 *            the value to set
	 */
	void setScrollBarValue(int v) {
		scrollbar.setValue(v);
	}

	/**
	 * Updates the compare editor preserving both panes
	 */
	void update() {
		update(PRESERVE_LEFT_POSITION | PRESERVE_RIGHT_POSITION);
	}

	/**
	 * Updates the editor. This method is called by the <code>updateLeft</code>
	 * or <code>updateRight</code> methods after the contents of any pane has
	 * been modified
	 * 
	 * @param preserve
	 *            mask to preserve or not the position of the editor
	 */
	private void update(int preserveMask) {
		// Create mapping
		mapping = new Mapping(splitLines(leftText), splitLines(rightText));
		originalMap = mapping.getMap();
		refresh();

		caretChangeByCode = true;

		// Update documents
		int preserveLine = -1;
		documentChangeByCode = true;
		if ((preserveMask & PRESERVE_LEFT_POSITION) != 0) {
			preserveLine = leftPane.getFirstLine();
			leftPane.setModel(leftTitle, mapping.getOriginalHighlight());
		} else {
			leftPane.setModel(leftText, leftTitle, mapping
					.getOriginalHighlight());
		}

		if ((preserveMask & PRESERVE_RIGHT_POSITION) != 0) {
			preserveLine = rightPane.getFirstLine();
			rightPane.setModel(rightTitle, mapping.getNewHighlight());
		} else {
			rightPane
					.setModel(rightText, rightTitle, mapping.getNewHighlight());

		}
		documentChangeByCode = false;

		// Enable or disable components
		if (mapping.getDifferences().size() != 0 && leftPane.isEnabled()
				&& !rightTitle.equals(NO_DOCUMENT_TITLE)) {
			allToLeft.setEnabled(true);
		} else {
			allToLeft.setEnabled(false);
		}

		if (mapping.getDifferences().size() != 0 && rightPane.isEnabled()
				&& !leftTitle.equals(NO_DOCUMENT_TITLE)) {
			allToRight.setEnabled(true);
		} else {
			allToRight.setEnabled(false);
		}

		// Update caret
		caretChangeByCode = false;
		if (preserveMask == PRESERVE_RIGHT_POSITION) {
			rightPane.fireCaretEvent();
			scrollRightPane(preserveLine - rightPane.getFirstLine());
		} else if (preserveMask == PRESERVE_LEFT_POSITION) {
			leftPane.fireCaretEvent();
			scrollLeftPane(preserveLine - leftPane.getFirstLine());
		} else if (preserveMask == NO_PRESERVE_POSITION) {
			scrollPanes(0, 0, -1);
			leftPane.getTextArea().setCaretPosition(0);
			rightPane.getTextArea().setCaretPosition(0);
			leftPane.fireCaretEvent();
		} else {
			if (leftPane.isFocusOwner()) {
				leftPane.fireCaretEvent();
			} else {
				rightPane.fireCaretEvent();
			}
		}

		// Update middle
		updateMiddleComponent();

		boolean dirtyLeft = !leftText.equals(leftSavedText);
		boolean dirtyRight = !rightText.equals(rightSavedText);

		leftPane.setDirty(dirtyLeft);
		rightPane.setDirty(dirtyRight);

		saveLeft.setEnabled(dirtyLeft);
		saveRight.setEnabled(dirtyRight);

		repaint();
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public GeocognitionElement getLeftElement() {
		return originalLeft;
	}

	@Override
	public GeocognitionElement getRightElement() {
		return originalRight;
	}

	@Override
	public boolean isLeftDirty() {
		return leftPane.isDirty();
	}

	@Override
	public boolean isRightDirty() {
		return rightPane.isDirty();
	}

	@Override
	public void close() {
		if (originalLeft != null) {
			originalLeft.removeElementListener(leftElementListener);
		}

		if (originalRight != null) {
			originalRight.removeElementListener(rightElementListener);
		}
	}

	/**
	 * Gets the content from the left geocognition element
	 * 
	 * @return the content of the element
	 * @throws GeocognitionException
	 *             if the element cannot be readed
	 */
	protected abstract String getLeftContent() throws GeocognitionException;

	/**
	 * Gets the content from the right geocognition element
	 * 
	 * @return the content of the element
	 * @throws GeocognitionException
	 *             if the element cannot be readed
	 */
	protected abstract String getRightContent() throws GeocognitionException;

	/**
	 * Sets the content of the left geoconition element
	 * 
	 * @param content
	 *            the content to set
	 * @throws GeocognitionException
	 *             if the content cannot be written
	 */
	protected abstract void setLeftContent(String content)
			throws GeocognitionException;

	/**
	 * Sets the content of the left geoconition element
	 * 
	 * @param content
	 *            the content to set
	 * @throws GeocognitionException
	 *             if the content cannot be written
	 */
	protected abstract void setRightContent(String content)
			throws GeocognitionException;

	/**
	 * Sets the model of the editor
	 * 
	 * @param path
	 *            the path to the nodes to compare
	 * @param manager
	 *            the synchronization manager with the elements to edit
	 */
	public void setModel(GeocognitionElement left, GeocognitionElement right) {
		// Left node
		if (left == null) {
			originalLeft = null;
			leftText = "";
			leftTitle = NO_DOCUMENT_TITLE;
		} else {
			try {
				originalLeft = (left instanceof GeocognitionElementDecorator) ? (GeocognitionElementDecorator) left
						: new GeocognitionElementDecorator(left, null);
				leftText = getLeftContent();
				leftTitle = left.getId();
				originalLeft.addElementListener(leftElementListener);
			} catch (GeocognitionException e) {
				Services.getErrorManager().error(
						"An error has ocurred while reading the file", e);
			}
		}

		// Right node
		if (right == null) {
			originalRight = null;
			rightText = "";
			rightTitle = NO_DOCUMENT_TITLE;
		} else {
			try {
				originalRight = (right instanceof GeocognitionElementDecorator) ? (GeocognitionElementDecorator) right
						: new GeocognitionElementDecorator(right, null);
				rightText = getRightContent();
				rightTitle = right.getId();
				originalRight.addElementListener(rightElementListener);
			} catch (GeocognitionException e) {
				Services.getErrorManager().error(
						"An error has ocurred while reading the file", e);
			}
		}

		leftSavedText = leftText;
		rightSavedText = rightText;

		update(NO_PRESERVE_POSITION);

		leftPane.resetUndoManager();
		rightPane.resetUndoManager();

		// Enable both panes by default
		setEnabledLeft(true);
		setEnabledRight(true);
	}

	@Override
	public void setEnabledRight(boolean b) {
		boolean enableRight = b && originalRight != null;
		rightPane.setEnabled(enableRight);
		rightPane.getTextArea().setFocusable(enableRight);
		if (enableRight && !leftTitle.equals(NO_DOCUMENT_TITLE)) {
			allToRight.setEnabled(true);
			if (middle.getSelected() != -1) {
				selectedToRight.setEnabled(true);
			}

		} else {
			allToRight.setEnabled(false);
			selectedToRight.setEnabled(false);
		}
	}

	@Override
	public void setEnabledLeft(boolean b) {
		boolean enableLeft = b && originalLeft != null;
		leftPane.setEnabled(enableLeft);
		leftPane.getTextArea().setFocusable(enableLeft);
		if (enableLeft && !rightTitle.equals(NO_DOCUMENT_TITLE)) {
			allToLeft.setEnabled(true);
			if (middle.getSelected() != -1) {
				selectedToLeft.setEnabled(true);
			}
		} else {
			allToLeft.setEnabled(false);
			selectedToLeft.setEnabled(false);
		}
	}

	/**
	 * Removes the redundant mappings and updates the extent and maximum value
	 * of the scroll bar
	 */
	private void refresh() {
		// Remove redundant mappings
		int length = splitLines(leftText).length;
		int visibleRows = leftPane.getVisibleRows();
		int maxA = length - visibleRows;
		int maxB = splitLines(rightText).length - rightPane.getVisibleRows();

		map = new ArrayList<Point>();
		map.add(originalMap.get(0));
		for (int i = 1; i < originalMap.size(); i++) {
			Point previous = originalMap.get(i - 1);
			Point actual = originalMap.get(i);
			boolean isRedundant = (actual.x == previous.x && actual.y > maxB)
					|| (actual.y == previous.y && actual.x > maxA)
					|| (actual.x > maxA && actual.y > maxB);
			if (!isRedundant) {
				map.add(actual);
			}
		}

		// Update scrollbar
		scrollBarChangeByCode = true;
		if (map.size() == 1) {
			scrollbar.getModel().setMaximum(1);
			scrollbar.getModel().setExtent(1);
		} else {
			scrollbar.getModel().setMaximum(
					leftPane.getVisibleRows() + map.size() - 1);
			scrollbar.getModel().setExtent(leftPane.getVisibleRows());
		}
		scrollBarChangeByCode = false;
	}

	/**
	 * Scrolls the pane of the original file the specified amount of lines
	 * (scroll down if positive, scroll up if negative)
	 * 
	 * @param lines
	 *            the amount of lines to scroll
	 */
	private void scrollLeftPane(int lines) {
		// Get new line numbers. The line number for the new pane
		// is the lower mapping value with the original line value
		int originalLine = leftPane.getFirstLine() + lines;
		int newLine = -1;
		int i;
		for (i = 0; i < map.size(); i++) {
			Point mapping = map.get(i);
			if (mapping.x == originalLine) {
				newLine = mapping.y;
				break;
			}
		}

		if (newLine != -1) {
			scrollPanes(originalLine, newLine, i);
		}
	}

	/**
	 * Scrolls the pane of the new file the specified amount of lines (scroll
	 * down if positive, scroll up if negative)
	 * 
	 * @param lines
	 *            the amount of lines to scroll
	 */
	private void scrollRightPane(int lines) {
		// Get new line numbers. The line number for the original line
		// is the lower mapping value with the new line value
		int newLine = rightPane.getFirstLine() + lines;
		int originalLine = -1;
		int i;
		for (i = 0; i < map.size(); i++) {
			Point mapping = map.get(i);
			if (mapping.y == newLine) {
				originalLine = mapping.x;
				break;
			}
		}

		if (originalLine != -1) {
			scrollPanes(originalLine, newLine, i);
		}
	}

	/**
	 * Scroll both panes to the specified lines and updates the scrollbar to the
	 * specified value
	 * 
	 * @param leftLine
	 *            the first line to show on the left pane
	 * @param rightLine
	 *            the first line to show on the right pane
	 * @param scrollBarValue
	 *            the value to set in the scrollbar or -1 if the scrollbar has
	 *            not to be changed
	 */
	private void scrollPanes(int leftLine, int rightLine, int scrollBarValue) {
		leftPane.scrollTo(leftLine);
		rightPane.scrollTo(rightLine);

		updateMiddleComponent();

		// Update scrollbar if needed
		if (scrollBarValue >= 0) {
			scrollBarChangeByCode = true;
			scrollbar.setValue(scrollBarValue);
			scrollBarChangeByCode = false;
		}
	}

	/**
	 * Update the offsets of the middle component and repaint
	 */
	private void updateMiddleComponent() {
		middle.setLeftOffset(leftPane.getOffset());
		middle.setRightOffset(rightPane.getOffset());
		middle.setTitleOffset(rightPane.getTitleOffset());
		middle.repaint();
	}

	@Override
	public void saveLeft() {
		try {
			setLeftContent(leftText);
			leftSavedText = leftText;
			update();
		} catch (GeocognitionException e) {
			Services.getErrorManager().error(
					"An error has ocurred while saving the file", e);
		}
	}

	@Override
	public void saveRight() {
		try {
			setRightContent(rightText);
			rightSavedText = rightText;
			update();
		} catch (GeocognitionException e) {
			Services.getErrorManager().error(
					"An error has ocurred while saving the file", e);
		}
	}

	/**
	 * Merges the selected difference into the left part of the editor
	 */
	private void selectedToLeft() {
		int selected = middle.getSelected();
		if (selected != -1) {
			Difference d = mapping.getDifferences().get(selected);
			leftText = replace(leftText, rightText, d.getDeletion(), d
					.getAddition());
			update(PRESERVE_RIGHT_POSITION);
			requestFocus();
		}
	}

	/**
	 * Merges the selected difference into the right part of the editor
	 */
	private void selectedToRight() {
		int selected = middle.getSelected();
		if (selected != -1) {
			Difference d = mapping.getDifferences().get(selected);
			rightText = replace(rightText, leftText, d.getAddition(), d
					.getDeletion());
			update(PRESERVE_LEFT_POSITION);
			requestFocus();
		}
	}

	/**
	 * Puts the right content of the editor into the left part
	 */
	private void allToLeft() {
		leftText = rightText;
		update(PRESERVE_RIGHT_POSITION);
		requestFocus();
	}

	/**
	 * Puts the left content of the editor into the right part
	 */
	private void allToRight() {
		rightText = leftText;
		update(PRESERVE_LEFT_POSITION);
		requestFocus();
	}

	/**
	 * Update the content of the left part of the editor and refresh
	 * 
	 * @param text
	 *            the new text
	 */
	private void updateLeft(String text) throws BadLocationException {
		leftText = text;
		update(PRESERVE_LEFT_POSITION | PRESERVE_RIGHT_POSITION);
	}

	/**
	 * Update the content of the right part of the editor and refresh
	 * 
	 * @param text
	 *            the new text
	 */
	private void updateRight(String text) {
		rightText = text;
		update(PRESERVE_LEFT_POSITION | PRESERVE_RIGHT_POSITION);
	}

	/**
	 * Replaces the given range in the original text for the given range in the
	 * replacing text
	 * 
	 * @param original
	 *            the original text
	 * @param replace
	 *            the replacing text
	 * @param originalRange
	 *            the range in the original text (both indexes of the range are
	 *            inclusive)
	 * @param replaceRange
	 *            the range in the replacing text (both indexes of the range are
	 *            inclusive)
	 * @return
	 */
	private String replace(String original, String replace,
			Range originalRange, Range replaceRange) {
		String str = "";
		String[] o = splitLines(original);
		String[] r = splitLines(replace);
		for (int i = 0; i < originalRange.getStart(); i++) {
			str += o[i];
		}

		// i <= in the condition because the indexes are both inclusive
		for (int i = replaceRange.getStart(); i <= replaceRange.getEnd(); i++) {
			str += r[i];
		}

		int continueIndex;
		if (originalRange.getEnd() == -1) {
			continueIndex = originalRange.getStart();
		} else {
			continueIndex = originalRange.getEnd() + 1;
		}

		for (int i = continueIndex; i < o.length; i++) {
			str += o[i];
		}
		return str;
	}

	/**
	 * Split the given string into an array using the '\n' regular expression to
	 * split. The difference between this method and String.split is that this
	 * function preserves the regular expression used to split
	 * 
	 * @param str
	 *            the string to split
	 * @return the split string
	 */
	private String[] splitLines(String str) {
		if (str.equalsIgnoreCase("")) {
			return new String[0];
		} else {
			ArrayList<String> aux = new ArrayList<String>();
			int startIndex = 0;
			int endIndex = str.indexOf("\n");
			while (endIndex != -1) {
				aux.add(str.substring(startIndex, endIndex));
				startIndex = endIndex;
				endIndex = str.indexOf("\n", startIndex + 1);
			}

			if (startIndex <= str.length()) {
				aux.add(str.substring(startIndex, str.length()));
			}
			String[] array = new String[aux.size()];

			return aux.toArray(array);
		}
	}

	/**
	 * Listener of the caret events. Updates the selected difference and the
	 * panes and scroll bar if the new position of the caret is not visible
	 * 
	 * @author Victorzinho
	 * 
	 */
	private class CaretSelectionListener implements CaretListener {
		@Override
		public void caretUpdate(CaretEvent e) {
			if (caretChangeByCode) {
				return;
			}

			// Get the pane
			Container comp = (Container) e.getSource();
			while (comp != null && !(comp instanceof CompareTextPane)) {
				comp = comp.getParent();
			}
			CompareTextPane pane = (CompareTextPane) comp;

			// Update
			try {
				int line = pane.getTextArea().getLineOfOffset(e.getDot());
				updateSelection(pane, line);
				updatePane(pane, line);
			} catch (BadLocationException exc) {
				Services.getErrorManager().error("bug!", exc);
			}

		}

		/**
		 * Updates both panes if the new caret position is out of view
		 * 
		 * @param pane
		 *            the pane with the caret change
		 * @param line
		 *            the line of the caret
		 * @throws BadLocationException
		 */
		private void updatePane(CompareTextPane pane, int line)
				throws BadLocationException {
			int lastVisibleLine = pane.getFirstLine() + pane.getVisibleRows()
					- 1;
			int linesToScroll = 0;

			if (lastVisibleLine < line) {
				linesToScroll = line - lastVisibleLine;
			} else if (line < pane.getFirstLine()) {
				linesToScroll = line - pane.getFirstLine();
			}

			if (pane == rightPane) {
				scrollRightPane(linesToScroll);
			} else if (pane == leftPane) {
				scrollLeftPane(linesToScroll);
			}
		}

		/**
		 * Updates the selected difference if the caret position is inside of
		 * one of them
		 * 
		 * @param pane
		 *            the pane with the caret change
		 * @param line
		 *            the line of the caret
		 */
		private void updateSelection(CompareTextPane pane, int line)
				throws BadLocationException {
			// Get selected difference
			int selected = -1;
			for (int i = 0; i < pane.getHighlights().size(); i++) {
				Range range = pane.getHighlights().get(i);
				if (range.getStart() <= line && range.getEnd() >= line) {
					selected = i;
					break;
				}
			}

			if (!caretChangeByCode) {
				if (selected != -1) {
					// Update opposite caret
					CompareTextPane opposite;
					caretChangeByCode = true;
					if (pane == rightPane) {
						opposite = leftPane;
					} else {
						opposite = rightPane;
					}

					int maxRows = opposite.getTextArea().getLineCount();
					Range r = opposite.getHighlights().get(selected);
					int newLine = Math.min(maxRows - 1, r.getStart());
					int newCaretPos = opposite.getTextArea()
							.getLineStartOffset(newLine);
					opposite.getTextArea().setCaretPosition(newCaretPos);
					updatePane(pane, newLine);
					caretChangeByCode = false;
				}
			}

			// Update buttons
			if (selected != -1) {
				if (leftPane.isEnabled()
						&& !rightTitle.equalsIgnoreCase(NO_DOCUMENT_TITLE)) {
					selectedToLeft.setEnabled(true);
				}
				if (rightPane.isEnabled()
						&& !leftTitle.equalsIgnoreCase(NO_DOCUMENT_TITLE)) {
					selectedToRight.setEnabled(true);
				}
			} else {
				selectedToLeft.setEnabled(false);
				selectedToRight.setEnabled(false);
			}

			// Update and repaint interface
			leftPane.getTextArea().setSelected(selected);
			rightPane.getTextArea().setSelected(selected);
			middle.setSelected(selected);
			repaint();
		}
	}

	/**
	 * Listener for the edited nodes
	 * 
	 * @author Victorzinho
	 */
	private class RightElementListener implements GeocognitionElementListener {
		private void fireChange() {
			try {
				String original = getRightContent();
				if (!rightText.equals(original)) {
					int option = JOptionPane.showConfirmDialog(null,
							RIGHT_OUTSIDE_MODIFY_TEXT,
							RIGHT_OUTSIDE_MODIFY_TITLE,
							JOptionPane.YES_NO_OPTION);
					if (option == JOptionPane.OK_OPTION) {
						rightText = original;
						update(PRESERVE_LEFT_POSITION);
					}
				}
			} catch (GeocognitionException e) {
				Services.getErrorManager().error(
						"An error has ocurred while reading the file", e);
			}
		}

		@Override
		public void contentChanged(GeocognitionElement element) {
			fireChange();
		}

		@Override
		public void idChanged(GeocognitionElement element) {
			fireChange();
		}

		@Override
		public void saved(GeocognitionElement element) {
		}
	}

	private class LeftElementListener implements GeocognitionElementListener {
		private void fireChange() {
			try {
				String original = getLeftContent();
				if (!leftText.equals(original)) {
					int option = JOptionPane.showConfirmDialog(null,
							LEFT_OUTSIDE_MODIFY_TEXT,
							LEFT_OUTSIDE_MODIFY_TITLE,
							JOptionPane.YES_NO_OPTION);
					if (option == JOptionPane.OK_OPTION) {
						leftText = original;
						update(PRESERVE_RIGHT_POSITION);
					}
				}
			} catch (GeocognitionException e) {
				Services.getErrorManager().error(
						"An error has ocurred while reading the file", e);
			}
		}

		@Override
		public void contentChanged(GeocognitionElement element) {
			fireChange();
		}

		@Override
		public void idChanged(GeocognitionElement element) {
			fireChange();
		}

		@Override
		public void saved(GeocognitionElement element) {
		}
	}
}
