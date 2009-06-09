package org.orbisgis.core.ui.components.text;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;


public class UndoRedoInstaller {
	private static TextKeyListener keyListener = new TextKeyListener();
	private static TextMouseListener mouseListener = new TextMouseListener();

	/**
	 * Installs <code>undo/redo</code> support as well as
	 * <code>cut, copy, paste</code> and <code>select all</code> functionality
	 * (popup menu and key bindings) on the given text component
	 *
	 * @param txt
	 *            component to install on
	 * @return the undo manager created for the component
	 */
	public static void installUndoRedoSupport(final JTextComponent txt) {
		if (!(txt.getDocument() instanceof UndoableDocument)) {
			throw new IllegalArgumentException(
					"The text component document must be "
							+ UndoableDocument.class);
		}

		// Remove listeners
		txt.removeKeyListener(keyListener);
		txt.removeMouseListener(mouseListener);

		// Add new listeners
		txt.addKeyListener(keyListener);
		txt.addMouseListener(mouseListener);
	}

	/**
	 * Key listener for the text component
	 *
	 * @author victorzinho
	 *
	 */
	private static class TextKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			JTextComponent txt = (JTextComponent) e.getComponent();
			UndoableDocument doc = (UndoableDocument) txt.getDocument();

			if (e.getModifiers() == KeyEvent.CTRL_MASK) {
				if (e.getKeyCode() == KeyEvent.VK_Z) {
					doc.undo();
					e.consume();
				} else if (e.getKeyCode() == KeyEvent.VK_X) {
					txt.cut();
					e.consume();
				} else if (e.getKeyCode() == KeyEvent.VK_C) {
					txt.copy();
					e.consume();
				} else if (e.getKeyCode() == KeyEvent.VK_V) {
					txt.paste();
					e.consume();
				} else if (e.getKeyCode() == KeyEvent.VK_A) {
					txt.setSelectionStart(0);
					txt.setSelectionEnd(txt.getText().length());
					e.consume();
				}
			} else if (e.getModifiers() == (KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK)) {
				if (e.getKeyCode() == KeyEvent.VK_Z) {
					doc.redo();
					e.consume();
				}
			}
		}
	}

	/**
	 * Mouse listener for the text component
	 *
	 * @author victorzinho
	 *
	 */
	private static class TextMouseListener extends MouseAdapter {
		private JPopupMenu popup;
		private JMenuItem undo, redo, cut, copy, paste, select;

		/**
		 * Creates a new TextMouseListener
		 *
		 * @param um
		 *            the undo manager to use for the undo / redo actions
		 * @param l
		 *            the listeners to call when a undo / redo action is
		 *            performed
		 */
		private TextMouseListener() {
			popup = new JPopupMenu();
			undo = addItemToPopup("Undo", KeyEvent.VK_Z, KeyEvent.CTRL_MASK);
			redo = addItemToPopup("Redo", KeyEvent.VK_Z, KeyEvent.CTRL_MASK
					| KeyEvent.SHIFT_MASK);
			popup.addSeparator();
			cut = addItemToPopup("Cut", KeyEvent.VK_X, KeyEvent.CTRL_MASK);
			copy = addItemToPopup("Copy", KeyEvent.VK_C, KeyEvent.CTRL_MASK);
			paste = addItemToPopup("Paste", KeyEvent.VK_V, KeyEvent.CTRL_MASK);
			popup.addSeparator();
			select = addItemToPopup("Select All", KeyEvent.VK_A,
					KeyEvent.CTRL_MASK);

			undo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JTextComponent txt = (JTextComponent) popup.getInvoker();
					UndoableDocument doc = (UndoableDocument) txt.getDocument();
					doc.undo();
				}
			});

			redo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JTextComponent txt = (JTextComponent) popup.getInvoker();
					UndoableDocument doc = (UndoableDocument) txt.getDocument();
					doc.redo();
				}
			});

			cut.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					((JTextComponent) popup.getInvoker()).cut();
				}
			});

			copy.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					((JTextComponent) popup.getInvoker()).copy();
				}
			});

			paste.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					((JTextComponent) popup.getInvoker()).paste();
				}
			});

			select.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JTextComponent txt = (JTextComponent) popup.getInvoker();
					txt.setSelectionStart(0);
					txt.setSelectionEnd(txt.getText().length());
				}
			});
		}

		/**
		 * Creates a new JMenuItem with the specified text and key stroke and
		 * adds it to the popup menu
		 *
		 * @param text
		 *            the text of the created item
		 * @param key
		 *            the key of the key stroke
		 * @param modifiers
		 *            the modifiers of the key stroke
		 * @return the element added to the popup menu
		 */
		private JMenuItem addItemToPopup(String text, int key, int modifiers) {
			final JMenuItem item = new JMenuItem(text);
			item.setAccelerator(KeyStroke.getKeyStroke(key, modifiers));
			popup.add(item);

			return item;
		}

		/**
		 * Pops up the menu if necessary
		 *
		 * @param e
		 *            the mouse event firing the popup
		 */
		private void popup(MouseEvent e) {
			JTextComponent txt = (JTextComponent) e.getSource();
			UndoableDocument doc = (UndoableDocument) txt.getDocument();
			if (!e.isPopupTrigger()) {
				return;
			}

			boolean selected = (txt.getSelectedText() != null);
			boolean clipboard = txt.getToolkit().getSystemClipboard()
					.getContents(null) != null;

			undo.setEnabled(doc.canUndo());
			redo.setEnabled(doc.canRedo());
			cut.setEnabled(selected);
			copy.setEnabled(selected);
			paste.setEnabled(clipboard);

			popup.show(e.getComponent(), e.getX(), e.getY());
		}

		@Override
		public void mousePressed(MouseEvent e) {
			JTextComponent txt = (JTextComponent) e.getSource();
			txt.requestFocusInWindow();
			popup(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			JTextComponent txt = (JTextComponent) e.getSource();
			txt.requestFocusInWindow();
			popup(e);
		}
	}
}
