/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *  
 *  Lead Erwan BOCHER, scientific researcher, 
 *
 *  Developer lead : Pierre-Yves FADET, computer engineer. 
 *  
 *  User support lead : Gwendall Petit, geomatic engineer. 
 * 
 * Previous computer developer : Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 * 
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 * 
 * Copyright (C) 2010 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
 * For more information, please consult: <http://orbisgis.cerma.archi.fr/>
 * <http://sourcesup.cru.fr/projects/orbisgis/>
 * 
 * or contact directly: 
 * erwan.bocher _at_ ec-nantes.fr 
 * Pierre-Yves.Fadet _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 **/

package org.orbisgis.core.ui.plugins.views.sqlConsole.ui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.components.jtextComponent.BracketMatcher;
import org.orbisgis.core.ui.components.jtextComponent.RedZigZagPainter;
import org.orbisgis.core.ui.components.jtextComponent.TextLineNumber;
import org.orbisgis.core.ui.components.text.UndoRedoInstaller;
import org.orbisgis.core.ui.editorViews.toc.TransferableLayer;
import org.orbisgis.core.ui.plugins.views.geocatalog.TransferableSource;
import org.orbisgis.core.ui.plugins.views.sqlConsole.actions.ActionsListener;
import org.orbisgis.core.ui.plugins.views.sqlConsole.actions.ConsoleListener;
import org.orbisgis.core.ui.plugins.views.sqlConsole.syntax.SQLDocument;

public class SQLScriptPanel extends JScrollPane implements DropTargetListener {

	private ActionsListener actionAndKeyListener;

	/** The document holding the text being edited. */
	private StyledDocument document;

	JTextPane jTextPane;

	private ConsoleListener listener;

	public SQLScriptPanel(final ActionsListener actionAndKeyListener,
			ConsoleListener listener) {
		this.actionAndKeyListener = actionAndKeyListener;
		this.listener = listener;
		setViewportView(getJTextPane());
		this.getVerticalScrollBar().setBlockIncrement(10);
		this.getVerticalScrollBar().setUnitIncrement(5);
	}

	public JTextPane getJTextPane() {
		if (jTextPane == null) {
			jTextPane = new JTextPane();
			jTextPane.setCaretPosition(0);
			document = new SQLDocument(jTextPane);
			jTextPane.setDocument(document);
			jTextPane.setDropTarget(new DropTarget(this, this));
			jTextPane.getDocument().addDocumentListener(actionAndKeyListener);
			UndoRedoInstaller.installUndoRedoSupport(jTextPane);

			TextLineNumber tln = new TextLineNumber(jTextPane);

			this.setRowHeaderView(tln);

			BracketMatcher bracketMatcher = new BracketMatcher();
			jTextPane.addCaretListener(bracketMatcher);

		}

		return jTextPane;

	}

	public void dragEnter(DropTargetDragEvent dtde) {
	}

	public void dragExit(DropTargetEvent dte) {
	}

	public void dragOver(DropTargetDragEvent dtde) {
	}

	public void drop(DropTargetDropEvent dtde) {
		final Transferable t = dtde.getTransferable();

		String query = listener.doDrop(t);
		if (query == null) {
			try {
				if ((t.isDataFlavorSupported(TransferableSource
						.getResourceFlavor()))
						|| (t.isDataFlavorSupported(TransferableLayer
								.getLayerFlavor()))) {
					dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					String s = (String) t
							.getTransferData(DataFlavor.stringFlavor);
					dtde.getDropTargetContext().dropComplete(true);
					query = s;
				} else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					String s = (String) t
							.getTransferData(DataFlavor.stringFlavor);
					dtde.getDropTargetContext().dropComplete(true);
					query = s;
				}
			} catch (IOException e) {
				dtde.rejectDrop();
			} catch (UnsupportedFlavorException e) {
				dtde.rejectDrop();
			}
		}

		if (query != null) {
			// Cursor position
			int position = jTextPane.viewToModel(dtde.getLocation());
			try {
				jTextPane.getDocument().insertString(position, query, null);
			} catch (BadLocationException e) {
				Services.getErrorManager().error("Cannot place the text there",
						e);
			}
		} else {
			dtde.rejectDrop();
		}

		actionAndKeyListener.setButtonsStatus();
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	public void setText(String text) {
		setText(text, true);
	}

	/**
	 * Replace the contents of the SQL entry area with the passed SQL script and
	 * specify whether to select it.
	 * 
	 * @param sqlScript
	 *            The script to be placed in the SQL entry area..
	 * @param select
	 *            If <TT>true</TT> then select the passed script in the sql
	 *            entry area.
	 */
	public void setText(String sqlScript, boolean select) {
		jTextPane.setText(sqlScript);
		if (select) {
			setSelectionEnd(getText().length());
			setSelectionStart(0);
		}
		jTextPane.setCaretPosition(0);
	}

	/**
	 * 
	 */
	public int getSelectionStart() {
		return jTextPane.getSelectionStart();
	}

	/**
	 * 
	 */
	public void setSelectionStart(int pos) {
		jTextPane.setSelectionStart(pos);
	}

	public int getSelectionEnd() {
		return jTextPane.getSelectionEnd();
	}

	/**
	 * 
	 */
	public void setSelectionEnd(int pos) {
		jTextPane.setSelectionEnd(pos);
	}

	/**
	 * Return the entire contents of the SQL entry area.
	 * 
	 * @return the entire contents of the SQL entry area.
	 */

	public String getText() {
		return jTextPane.getText();
	}

	/**
	 * Return the selected contents of the SQL entry area.
	 * 
	 * @return the selected contents of the SQL entry area.
	 */
	public String getSelectedText() {
		return jTextPane.getSelectedText();
	}

	public String getSQLToBeExecuted() {
		String sql = getSelectedText();
		if (sql == null || sql.trim().length() == 0) {
			sql = getText();
			int[] bounds = getBoundsOfSQLToBeExecuted();

			if (bounds[0] >= bounds[1]) {
				sql = "";
			} else {
				sql = sql.substring(bounds[0], bounds[1]).trim();
			}
		}
		return sql != null ? sql : "";
	}

	public int[] getBoundsOfSQLToBeExecuted() {
		int[] bounds = new int[2];
		bounds[0] = getSelectionStart();
		bounds[1] = getSelectionEnd();

		if (bounds[0] == bounds[1]) {
			bounds = getSqlBoundsBySeparatorRule(jTextPane.getCaretPosition());
		}

		return bounds;
	}

	private int[] getSqlBoundsBySeparatorRule(int iCaretPos) {
		int[] bounds = new int[2];

		String sql = getText();

		bounds[0] = lastIndexOfStateSep(sql, iCaretPos);
		bounds[1] = indexOfStateSep(sql, iCaretPos);

		return bounds;

	}

	private static int indexOfStateSep(String sql, int pos) {
		int ix = pos;

		int newLinteCount = 0;
		for (;;) {
			if (sql.length() == ix) {
				return sql.length();
			}

			if (false == Character.isWhitespace(sql.charAt(ix))) {
				newLinteCount = 0;
			}

			if ('\n' == sql.charAt(ix)) {
				++newLinteCount;
				if (2 == newLinteCount) {
					return ix - 1;
				}
			}

			++ix;
		}
	}

	private static int lastIndexOfStateSep(String sql, int pos) {
		int ix = pos;

		int newLinteCount = 0;
		for (;;) {

			if (ix == sql.length()) {
				if (ix == 0) {
					return ix;
				} else {
					ix--;
				}
			}

			if (false == Character.isWhitespace(sql.charAt(ix))) {
				newLinteCount = 0;
			}

			if ('\n' == sql.charAt(ix)) {
				++newLinteCount;
				if (2 == newLinteCount) {
					return ix + newLinteCount;
				}
			}

			if (0 == ix) {
				return 0 + newLinteCount;
			}

			--ix;
		}
	}

	public void insertString(String string) throws BadLocationException {
		document.insertString(document.getLength(), string, null);
	}

	public JTextComponent getTextComponent() {
		return jTextPane;
	}

	public void updateCodeError(int startError, int endError) {
		Highlighter highlighter = jTextPane.getHighlighter();
		DefaultHighlighter.DefaultHighlightPainter painter = new RedZigZagPainter();
		highlighter.removeAllHighlights();

		try {
			highlighter.addHighlight(startError, endError, painter);
		} catch (BadLocationException e) {
		}

	}

	public void replaceSelection(String text) {
		jTextPane.replaceSelection(text);
	}

}